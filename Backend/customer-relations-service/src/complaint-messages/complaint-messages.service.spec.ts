import { getModelToken } from '@nestjs/mongoose';
import { Test, TestingModule } from '@nestjs/testing';
import { ComplaintsService } from '../complaints/complaints.service';
import { ComplaintStatus } from '../complaints/enums/complaint-status.enum';
import { UtilisateurReferenceDto } from '../common/dto/references/utilisateur-reference.dto';
import { UserRole } from '../common/enums/user-role.enum';
import { ComplaintMessagesService } from './complaint-messages.service';
import { ComplaintMessage } from './schemas/complaint-message.schema';

const client: UtilisateurReferenceDto = {
  utilisateurId: 'client-001',
  role: UserRole.CLIENT,
};

describe('ComplaintMessagesService', () => {
  let service: ComplaintMessagesService;
  let messageModel: jest.Mock & { find: jest.Mock };
  let complaintsService: { obtenirReclamationParId: jest.Mock };

  beforeEach(async () => {
    messageModel = Object.assign(
      jest.fn().mockImplementation((data: Record<string, unknown>) => {
        const document = { ...data, save: jest.fn() };
        document.save.mockResolvedValue(document);
        return document;
      }),
      { find: jest.fn() },
    );
    complaintsService = { obtenirReclamationParId: jest.fn() };

    const module: TestingModule = await Test.createTestingModule({
      providers: [
        ComplaintMessagesService,
        {
          provide: getModelToken(ComplaintMessage.name),
          useValue: messageModel,
        },
        { provide: ComplaintsService, useValue: complaintsService },
      ],
    }).compile();

    service = module.get(ComplaintMessagesService);
  });

  it('refuse un message sur une réclamation clôturée', async () => {
    complaintsService.obtenirReclamationParId.mockResolvedValue({
      statut: ComplaintStatus.CLOSED,
      clientId: 'client-001',
    });

    await expect(
      service.ajouterMessage(
        '665f1f77bcf86cd799439011',
        { auteurId: 'client-001', contenu: 'Un suivi ?' },
        client,
      ),
    ).rejects.toThrow("Impossible d'ajouter un message à une réclamation clôturée");
  });

  it('refuse un auteur différent de l’utilisateur connecté', async () => {
    await expect(
      service.ajouterMessage(
        '665f1f77bcf86cd799439011',
        { auteurId: 'admin-001', contenu: 'Réponse' },
        client,
      ),
    ).rejects.toThrow("L'auteur du message doit correspondre à l'utilisateur connecté");
    expect(complaintsService.obtenirReclamationParId).not.toHaveBeenCalled();
  });

  it('enregistre un message lorsque la réclamation est accessible', async () => {
    complaintsService.obtenirReclamationParId.mockResolvedValue({
      statut: ComplaintStatus.OPEN,
      clientId: 'client-001',
    });

    const message = await service.ajouterMessage(
      '665f1f77bcf86cd799439011',
      { auteurId: 'client-001', contenu: 'Le colis est incomplet.' },
      client,
    );

    expect(message).toMatchObject({
      complaintId: '665f1f77bcf86cd799439011',
      auteurId: 'client-001',
      contenu: 'Le colis est incomplet.',
    });
  });
});
