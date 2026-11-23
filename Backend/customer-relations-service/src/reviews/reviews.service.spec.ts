import { getModelToken } from '@nestjs/mongoose';
import { Test, TestingModule } from '@nestjs/testing';
import { UserRole } from '../common/enums/user-role.enum';
import { UtilisateurReferenceDto } from '../common/dto/references/utilisateur-reference.dto';
import { CreateReviewDto } from './dto/create-review.dto';
import { ReviewsService } from './reviews.service';
import { Review } from './schemas/review.schema';

const client: UtilisateurReferenceDto = {
  utilisateurId: 'client-001',
  role: UserRole.CLIENT,
};

describe('ReviewsService', () => {
  let service: ReviewsService;
  let reviewModel: jest.Mock & {
    findById: jest.Mock;
    find: jest.Mock;
    aggregate: jest.Mock;
  };

  beforeEach(async () => {
    reviewModel = Object.assign(
      jest.fn().mockImplementation((data: Record<string, unknown>) => {
        const document = {
          ...data,
          save: jest.fn().mockResolvedValue(data),
          deleteOne: jest.fn().mockResolvedValue({ deletedCount: 1 }),
        };
        document.save.mockResolvedValue(document);
        return document;
      }),
      {
        findById: jest.fn(),
        find: jest.fn(),
        aggregate: jest.fn(),
      },
    );

    const module: TestingModule = await Test.createTestingModule({
      providers: [
        ReviewsService,
        { provide: getModelToken(Review.name), useValue: reviewModel },
      ],
    }).compile();

    service = module.get(ReviewsService);
  });

  it('crée un avis pour le client connecté', async () => {
    const dto: CreateReviewDto = {
      clientId: 'client-001',
      agriculteurId: 'agri-001',
      commandeId: 'cmd-001',
      note: 5,
      commentaire: 'Très bien',
    };

    const created = await service.creerAvis(dto, client);

    expect(created).toMatchObject(dto);
    expect(reviewModel).toHaveBeenCalledWith(
      expect.objectContaining({ note: 5, commandeId: 'cmd-001' }),
    );
  });

  it('refuse un avis créé pour un autre client', async () => {
    await expect(
      service.creerAvis(
        {
          clientId: 'client-999',
          agriculteurId: 'agri-001',
          commandeId: 'cmd-001',
          note: 4,
        },
        client,
      ),
    ).rejects.toThrow('Un client ne peut créer un avis que pour son propre compte');
  });

  it('signale un conflit si la commande a déjà un avis', async () => {
    reviewModel.mockImplementation(() => ({
      save: jest.fn().mockRejectedValue(Object.assign(new Error('dup'), { code: 11000 })),
    }));

    await expect(
      service.creerAvis(
        {
          clientId: 'client-001',
          agriculteurId: 'agri-001',
          commandeId: 'cmd-001',
          note: 3,
        },
        client,
      ),
    ).rejects.toThrow('Un avis existe déjà pour cette commande');
  });

  it('interdit la modification de l’avis d’un autre client', async () => {
    reviewModel.findById.mockReturnValue({
      exec: jest.fn().mockResolvedValue({
        clientId: 'client-999',
        note: 2,
        save: jest.fn(),
      }),
    });

    await expect(
      service.modifierAvis('665f1f77bcf86cd799439011', { note: 5 }, client),
    ).rejects.toThrow('Vous ne pouvez gérer que vos propres avis');
  });

  it('calcule une moyenne arrondie et zéro en l’absence d’avis', async () => {
    reviewModel.aggregate.mockReturnValueOnce({
      exec: jest.fn().mockResolvedValue([{ moyenne: 4.666, nombreAvis: 3 }]),
    });

    await expect(service.calculerMoyenneNotes('agri-001')).resolves.toEqual({
      agriculteurId: 'agri-001',
      moyenne: 4.67,
      nombreAvis: 3,
    });

    reviewModel.aggregate.mockReturnValueOnce({
      exec: jest.fn().mockResolvedValue([]),
    });

    await expect(service.calculerMoyenneNotes('agri-002')).resolves.toEqual({
      agriculteurId: 'agri-002',
      moyenne: 0,
      nombreAvis: 0,
    });
  });
});
