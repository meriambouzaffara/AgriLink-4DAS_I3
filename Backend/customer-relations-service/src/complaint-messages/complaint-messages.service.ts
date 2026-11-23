import {
  BadRequestException,
  ForbiddenException,
  Injectable,
} from '@nestjs/common';
import { InjectModel } from '@nestjs/mongoose';
import { Model } from 'mongoose';
import { ComplaintsService } from '../complaints/complaints.service';
import { ComplaintStatus } from '../complaints/enums/complaint-status.enum';
import { UtilisateurReferenceDto } from '../common/dto/references/utilisateur-reference.dto';
import { CreateComplaintMessageDto } from './dto/create-complaint-message.dto';
import {
  ComplaintMessage,
  ComplaintMessageDocument,
} from './schemas/complaint-message.schema';

@Injectable()
export class ComplaintMessagesService {
  constructor(
    @InjectModel(ComplaintMessage.name)
    private readonly messageModel: Model<ComplaintMessage>,
    private readonly complaintsService: ComplaintsService,
  ) {}

  async ajouterMessage(
    complaintId: string,
    dto: CreateComplaintMessageDto,
    utilisateur: UtilisateurReferenceDto,
  ): Promise<ComplaintMessageDocument> {
    if (dto.auteurId !== utilisateur.utilisateurId) {
      throw new ForbiddenException(
        "L'auteur du message doit correspondre à l'utilisateur connecté",
      );
    }

    const complaint = await this.complaintsService.obtenirReclamationParId(
      complaintId,
      utilisateur,
    );

    if (complaint.statut === ComplaintStatus.CLOSED) {
      throw new BadRequestException(
        "Impossible d'ajouter un message à une réclamation clôturée",
      );
    }

    const message = new this.messageModel({
      complaintId,
      auteurId: dto.auteurId,
      contenu: dto.contenu,
      dateEnvoi: new Date(),
    });

    return message.save();
  }

  async obtenirMessagesParReclamation(
    complaintId: string,
    utilisateur: UtilisateurReferenceDto,
  ): Promise<ComplaintMessageDocument[]> {
    await this.complaintsService.obtenirReclamationParId(
      complaintId,
      utilisateur,
    );

    return this.messageModel
      .find({ complaintId })
      .sort({ dateEnvoi: 1 })
      .exec();
  }
}
