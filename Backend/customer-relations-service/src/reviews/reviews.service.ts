import {
  BadRequestException,
  ConflictException,
  ForbiddenException,
  Injectable,
  NotFoundException,
} from '@nestjs/common';
import { InjectModel } from '@nestjs/mongoose';
import { Model } from 'mongoose';
import { UtilisateurReferenceDto } from '../common/dto/references/utilisateur-reference.dto';
import { UserRole } from '../common/enums/user-role.enum';
import { isDuplicateKeyError } from '../common/utils/is-duplicate-key';
import { CreateReviewDto } from './dto/create-review.dto';
import { UpdateReviewDto } from './dto/update-review.dto';
import { Review, ReviewDocument } from './schemas/review.schema';

export interface MoyenneNotes {
  agriculteurId: string;
  moyenne: number;
  nombreAvis: number;
}

@Injectable()
export class ReviewsService {
  constructor(
    @InjectModel(Review.name) private readonly reviewModel: Model<Review>,
  ) {}

  async creerAvis(
    dto: CreateReviewDto,
    utilisateur: UtilisateurReferenceDto,
  ): Promise<ReviewDocument> {
    this.ensureClient(utilisateur);

    if (dto.clientId !== utilisateur.utilisateurId) {
      throw new ForbiddenException(
        'Un client ne peut créer un avis que pour son propre compte',
      );
    }

    const now = new Date();

    try {
      const review = new this.reviewModel({
        clientId: dto.clientId,
        agriculteurId: dto.agriculteurId,
        commandeId: dto.commandeId,
        note: dto.note,
        commentaire: dto.commentaire,
        dateCreation: now,
        dateModification: now,
      });

      return await review.save();
    } catch (error) {
      if (isDuplicateKeyError(error)) {
        throw new ConflictException('Un avis existe déjà pour cette commande');
      }

      throw error;
    }
  }

  async modifierAvis(
    id: string,
    dto: UpdateReviewDto,
    utilisateur: UtilisateurReferenceDto,
  ): Promise<ReviewDocument> {
    this.ensureClient(utilisateur);

    if (dto.note === undefined && dto.commentaire === undefined) {
      throw new BadRequestException(
        'Indiquez une note ou un commentaire à modifier',
      );
    }

    const review = await this.obtenirAvisParId(id);
    this.ensureOwner(review, utilisateur);

    if (dto.note !== undefined) {
      review.note = dto.note;
    }

    if (dto.commentaire !== undefined) {
      review.commentaire = dto.commentaire;
    }

    review.dateModification = new Date();
    return review.save();
  }

  async supprimerAvis(
    id: string,
    utilisateur: UtilisateurReferenceDto,
  ): Promise<void> {
    this.ensureClient(utilisateur);
    const review = await this.obtenirAvisParId(id);
    this.ensureOwner(review, utilisateur);
    await review.deleteOne();
  }

  async obtenirAvisParId(id: string): Promise<ReviewDocument> {
    const review = await this.reviewModel.findById(id).exec();

    if (!review) {
      throw new NotFoundException('Avis introuvable');
    }

    return review;
  }

  async obtenirAvisParAgriculteur(agriculteurId: string): Promise<ReviewDocument[]> {
    return this.reviewModel
      .find({ agriculteurId })
      .sort({ dateCreation: -1 })
      .exec();
  }

  async calculerMoyenneNotes(agriculteurId: string): Promise<MoyenneNotes> {
    const [result] = await this.reviewModel
      .aggregate<{ moyenne: number; nombreAvis: number }>([
        { $match: { agriculteurId } },
        {
          $group: {
            _id: '$agriculteurId',
            moyenne: { $avg: '$note' },
            nombreAvis: { $sum: 1 },
          },
        },
      ])
      .exec();

    if (!result) {
      return { agriculteurId, moyenne: 0, nombreAvis: 0 };
    }

    return {
      agriculteurId,
      moyenne: Math.round(result.moyenne * 100) / 100,
      nombreAvis: result.nombreAvis,
    };
  }

  private ensureClient(utilisateur: UtilisateurReferenceDto): void {
    if (utilisateur.role !== UserRole.CLIENT) {
      throw new ForbiddenException('Seul un client peut gérer ses avis');
    }
  }

  private ensureOwner(
    review: ReviewDocument,
    utilisateur: UtilisateurReferenceDto,
  ): void {
    if (review.clientId !== utilisateur.utilisateurId) {
      throw new ForbiddenException(
        'Vous ne pouvez gérer que vos propres avis',
      );
    }
  }
}
