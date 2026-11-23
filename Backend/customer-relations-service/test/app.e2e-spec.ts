import { INestApplication, ValidationPipe } from '@nestjs/common';
import { Test, TestingModule } from '@nestjs/testing';
import request from 'supertest';
import { App } from 'supertest/types';
import { ComplaintMessagesController } from '../src/complaint-messages/complaint-messages.controller';
import { ComplaintMessagesService } from '../src/complaint-messages/complaint-messages.service';
import { ComplaintsController } from '../src/complaints/complaints.controller';
import { ComplaintsService } from '../src/complaints/complaints.service';
import { ComplaintStatus } from '../src/complaints/enums/complaint-status.enum';
import { AllExceptionsFilter } from '../src/common/filters/all-exceptions.filter';
import { ReviewsController } from '../src/reviews/reviews.controller';
import { ReviewsService } from '../src/reviews/reviews.service';

describe('Customer relations API (e2e)', () => {
  let app: INestApplication<App>;
  const reviewsService = {
    creerAvis: jest.fn().mockResolvedValue({ _id: '665f1f77bcf86cd799439011', note: 5 }),
    calculerMoyenneNotes: jest.fn().mockResolvedValue({
      agriculteurId: 'agri-001',
      moyenne: 0,
      nombreAvis: 0,
    }),
    obtenirAvisParAgriculteur: jest.fn().mockResolvedValue([]),
    obtenirAvisParId: jest.fn(),
    modifierAvis: jest.fn(),
    supprimerAvis: jest.fn().mockResolvedValue(undefined),
  };
  const complaintsService = {
    creerReclamation: jest.fn().mockResolvedValue({ statut: ComplaintStatus.OPEN }),
    obtenirMesReclamations: jest.fn().mockResolvedValue([]),
    obtenirToutesLesReclamations: jest.fn().mockResolvedValue([]),
    obtenirReclamationParId: jest.fn(),
    modifierStatut: jest.fn().mockResolvedValue({ statut: ComplaintStatus.IN_PROGRESS }),
    cloturerReclamation: jest.fn().mockResolvedValue({ statut: ComplaintStatus.CLOSED }),
  };
  const messagesService = {
    ajouterMessage: jest.fn().mockResolvedValue({ contenu: 'Bonjour' }),
    obtenirMessagesParReclamation: jest.fn().mockResolvedValue([]),
  };

  beforeEach(async () => {
    jest.clearAllMocks();
    const moduleFixture: TestingModule = await Test.createTestingModule({
      controllers: [
        ReviewsController,
        ComplaintsController,
        ComplaintMessagesController,
      ],
      providers: [
        { provide: ReviewsService, useValue: reviewsService },
        { provide: ComplaintsService, useValue: complaintsService },
        { provide: ComplaintMessagesService, useValue: messagesService },
      ],
    }).compile();

    app = moduleFixture.createNestApplication();
    app.setGlobalPrefix('api');
    app.useGlobalPipes(
      new ValidationPipe({
        whitelist: true,
        forbidNonWhitelisted: true,
        transform: true,
        transformOptions: { enableImplicitConversion: true },
      }),
    );
    app.useGlobalFilters(new AllExceptionsFilter());
    await app.init();
  });

  afterEach(async () => {
    await app.close();
  });

  it('POST /api/reviews refuse une requête sans identité', () => {
    return request(app.getHttpServer())
      .post('/api/reviews')
      .send({
        clientId: 'client-001',
        agriculteurId: 'agri-001',
        commandeId: 'cmd-001',
        note: 5,
      })
      .expect(401);
  });

  it('POST /api/reviews refuse une note hors limites', () => {
    return request(app.getHttpServer())
      .post('/api/reviews')
      .set('x-utilisateur-id', 'client-001')
      .set('x-utilisateur-role', 'CLIENT')
      .send({
        clientId: 'client-001',
        agriculteurId: 'agri-001',
        commandeId: 'cmd-001',
        note: 9,
      })
      .expect(400);
  });

  it('POST /api/reviews crée un avis pour un client', () => {
    return request(app.getHttpServer())
      .post('/api/reviews')
      .set('x-utilisateur-id', 'client-001')
      .set('x-utilisateur-role', 'CLIENT')
      .send({
        clientId: 'client-001',
        agriculteurId: 'agri-001',
        commandeId: 'cmd-001',
        note: 5,
        commentaire: 'Produits frais',
      })
      .expect(201)
      .expect(() => {
        expect(reviewsService.creerAvis).toHaveBeenCalled();
      });
  });

  it('GET /api/reviews/agriculteur/:id/average ne requiert pas d’identité', () => {
    return request(app.getHttpServer())
      .get('/api/reviews/agriculteur/agri-001/average')
      .expect(200)
      .expect({ agriculteurId: 'agri-001', moyenne: 0, nombreAvis: 0 });
  });

  it('GET /api/complaints/mine est réservé au client', async () => {
    await request(app.getHttpServer())
      .get('/api/complaints/mine')
      .set('x-utilisateur-id', 'admin-001')
      .set('x-utilisateur-role', 'ADMIN')
      .expect(403);

    await request(app.getHttpServer())
      .get('/api/complaints/mine')
      .set('x-utilisateur-id', 'client-001')
      .set('x-utilisateur-role', 'CLIENT')
      .expect(200);

    expect(complaintsService.obtenirMesReclamations).toHaveBeenCalled();
  });

  it('PATCH /api/complaints/:id/status clôture via le service dédié', () => {
    return request(app.getHttpServer())
      .patch('/api/complaints/665f1f77bcf86cd799439011/status')
      .set('x-utilisateur-id', 'admin-001')
      .set('x-utilisateur-role', 'ADMIN')
      .send({ statut: 'CLOSED' })
      .expect(200)
      .expect(() => {
        expect(complaintsService.cloturerReclamation).toHaveBeenCalled();
        expect(complaintsService.modifierStatut).not.toHaveBeenCalled();
      });
  });

  it('POST /api/complaints/:id/messages valide le contenu', () => {
    return request(app.getHttpServer())
      .post('/api/complaints/665f1f77bcf86cd799439011/messages')
      .set('x-utilisateur-id', 'client-001')
      .set('x-utilisateur-role', 'CLIENT')
      .send({ auteurId: 'client-001', contenu: '' })
      .expect(400);
  });
});
