import { getModelToken } from '@nestjs/mongoose';
import { Test, TestingModule } from '@nestjs/testing';
import { UtilisateurReferenceDto } from '../common/dto/references/utilisateur-reference.dto';
import { UserRole } from '../common/enums/user-role.enum';
import { ComplaintsService } from './complaints.service';
import { ComplaintStatus } from './enums/complaint-status.enum';
import { Complaint } from './schemas/complaint.schema';

const client: UtilisateurReferenceDto = {
  utilisateurId: 'client-001',
  role: UserRole.CLIENT,
};

const agriculteur: UtilisateurReferenceDto = {
  utilisateurId: 'agri-001',
  role: UserRole.AGRICULTEUR,
};

const admin: UtilisateurReferenceDto = {
  utilisateurId: 'admin-001',
  role: UserRole.ADMIN,
};

describe('ComplaintsService', () => {
  let service: ComplaintsService;
  let complaintModel: jest.Mock & { findById: jest.Mock; find: jest.Mock };

  beforeEach(async () => {
    complaintModel = Object.assign(
      jest.fn().mockImplementation((data: Record<string, unknown>) => {
        const document = {
          ...data,
          save: jest.fn(),
        };
        document.save.mockResolvedValue(document);
        return document;
      }),
      {
        findById: jest.fn(),
        find: jest.fn(),
      },
    );

    const module: TestingModule = await Test.createTestingModule({
      providers: [
        ComplaintsService,
        { provide: getModelToken(Complaint.name), useValue: complaintModel },
      ],
    }).compile();

    service = module.get(ComplaintsService);
  });

  it('crée une réclamation au statut OPEN', async () => {
    const created = await service.creerReclamation(
      {
        clientId: 'client-001',
        commandeId: 'cmd-001',
        sujet: 'Colis incomplet',
        description: 'Il manque une caisse de tomates dans la livraison.',
      },
      client,
    );

    expect(created.statut).toBe(ComplaintStatus.OPEN);
    expect(created.clientId).toBe('client-001');
  });

  it('refuse une transition de statut invalide', async () => {
    const complaint = {
      statut: ComplaintStatus.OPEN,
      save: jest.fn(),
    };
    complaintModel.findById.mockReturnValue({
      exec: jest.fn().mockResolvedValue(complaint),
    });

    await expect(
      service.modifierStatut(
        '665f1f77bcf86cd799439011',
        { statut: ComplaintStatus.RESOLVED },
        agriculteur,
      ),
    ).rejects.toThrow('Transition de statut interdite');
    expect(complaint.save).not.toHaveBeenCalled();
  });

  it('laisse un agriculteur passer la réclamation en cours', async () => {
    const complaint = {
      statut: ComplaintStatus.OPEN,
      dateModification: new Date('2026-01-01'),
      save: jest.fn(),
    };
    complaint.save.mockResolvedValue(complaint);
    complaintModel.findById.mockReturnValue({
      exec: jest.fn().mockResolvedValue(complaint),
    });

    const updated = await service.modifierStatut(
      '665f1f77bcf86cd799439011',
      { statut: ComplaintStatus.IN_PROGRESS },
      agriculteur,
    );

    expect(updated.statut).toBe(ComplaintStatus.IN_PROGRESS);
  });

  it('interdit à un agriculteur de clôturer une réclamation', async () => {
    await expect(
      service.cloturerReclamation('665f1f77bcf86cd799439011', agriculteur),
    ).rejects.toThrow(
      'Seul un administrateur ou le service client peut clôturer une réclamation',
    );
  });

  it('permet à un administrateur de clôturer une réclamation ouverte', async () => {
    const complaint = {
      statut: ComplaintStatus.IN_PROGRESS,
      save: jest.fn(),
    };
    complaint.save.mockResolvedValue(complaint);
    complaintModel.findById.mockReturnValue({
      exec: jest.fn().mockResolvedValue(complaint),
    });

    const closed = await service.cloturerReclamation(
      '665f1f77bcf86cd799439011',
      admin,
    );

    expect(closed.statut).toBe(ComplaintStatus.CLOSED);
  });
});
