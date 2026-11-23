import {
  BadRequestException,
  ForbiddenException,
  Injectable,
  NotFoundException,
} from '@nestjs/common';
import { InjectModel } from '@nestjs/mongoose';
import { Model } from 'mongoose';
import { UtilisateurReferenceDto } from '../common/dto/references/utilisateur-reference.dto';
import { UserRole } from '../common/enums/user-role.enum';
import { CreateComplaintDto } from './dto/create-complaint.dto';
import { UpdateComplaintStatusDto } from './dto/update-complaint-status.dto';
import { ComplaintStatus } from './enums/complaint-status.enum';
import { Complaint, ComplaintDocument } from './schemas/complaint.schema';

const STAFF_ROLES: UserRole[] = [
  UserRole.ADMIN,
  UserRole.SERVICE_CLIENT,
  UserRole.AGRICULTEUR,
];

const CLOSE_ROLES: UserRole[] = [UserRole.ADMIN, UserRole.SERVICE_CLIENT];

@Injectable()
export class ComplaintsService {
  private readonly transitions: Record<ComplaintStatus, ComplaintStatus[]> = {
    [ComplaintStatus.OPEN]: [ComplaintStatus.IN_PROGRESS],
    [ComplaintStatus.IN_PROGRESS]: [
      ComplaintStatus.RESOLVED,
      ComplaintStatus.OPEN,
    ],
    [ComplaintStatus.RESOLVED]: [ComplaintStatus.IN_PROGRESS],
    [ComplaintStatus.CLOSED]: [],
  };

  constructor(
    @InjectModel(Complaint.name)
    private readonly complaintModel: Model<Complaint>,
  ) {}

  async creerReclamation(
    dto: CreateComplaintDto,
    utilisateur: UtilisateurReferenceDto,
  ): Promise<ComplaintDocument> {
    if (utilisateur.role !== UserRole.CLIENT) {
      throw new ForbiddenException('Seul un client peut créer une réclamation');
    }

    if (dto.clientId !== utilisateur.utilisateurId) {
      throw new ForbiddenException(
        'Un client ne peut créer une réclamation que pour son propre compte',
      );
    }

    const now = new Date();
    const complaint = new this.complaintModel({
      clientId: dto.clientId,
      commandeId: dto.commandeId,
      sujet: dto.sujet,
      description: dto.description,
      statut: ComplaintStatus.OPEN,
      dateCreation: now,
      dateModification: now,
    });

    return complaint.save();
  }

  async obtenirMesReclamations(
    utilisateur: UtilisateurReferenceDto,
  ): Promise<ComplaintDocument[]> {
    if (utilisateur.role !== UserRole.CLIENT) {
      throw new ForbiddenException(
        'Seul un client peut consulter ses réclamations',
      );
    }

    return this.complaintModel
      .find({ clientId: utilisateur.utilisateurId })
      .sort({ dateCreation: -1 })
      .exec();
  }

  async obtenirToutesLesReclamations(
    utilisateur: UtilisateurReferenceDto,
  ): Promise<ComplaintDocument[]> {
    this.ensureRole(
      utilisateur,
      STAFF_ROLES,
      'Seuls le service client, un administrateur ou un agriculteur peuvent consulter toutes les réclamations',
    );

    return this.complaintModel.find().sort({ dateCreation: -1 }).exec();
  }

  async obtenirReclamationParId(
    id: string,
    utilisateur: UtilisateurReferenceDto,
  ): Promise<ComplaintDocument> {
    const complaint = await this.complaintModel.findById(id).exec();

    if (!complaint) {
      throw new NotFoundException('Réclamation introuvable');
    }

    if (
      utilisateur.role === UserRole.CLIENT &&
      complaint.clientId !== utilisateur.utilisateurId
    ) {
      throw new ForbiddenException(
        'Vous ne pouvez consulter que vos propres réclamations',
      );
    }

    if (
      utilisateur.role !== UserRole.CLIENT &&
      !STAFF_ROLES.includes(utilisateur.role)
    ) {
      throw new ForbiddenException('Rôle non autorisé pour cette opération');
    }

    return complaint;
  }

  async modifierStatut(
    id: string,
    dto: UpdateComplaintStatusDto,
    utilisateur: UtilisateurReferenceDto,
  ): Promise<ComplaintDocument> {
    if (dto.statut === ComplaintStatus.CLOSED) {
      return this.cloturerReclamation(id, utilisateur);
    }

    this.ensureRole(
      utilisateur,
      STAFF_ROLES,
      'Seuls le service client, un administrateur ou un agriculteur peuvent modifier le statut',
    );

    const complaint = await this.findOrFail(id);
    this.ensureMutable(complaint);

    const allowed = this.transitions[complaint.statut];
    if (!allowed.includes(dto.statut)) {
      throw new BadRequestException(
        `Transition de statut interdite : ${complaint.statut} vers ${dto.statut}`,
      );
    }

    complaint.statut = dto.statut;
    complaint.dateModification = new Date();
    return complaint.save();
  }

  async cloturerReclamation(
    id: string,
    utilisateur: UtilisateurReferenceDto,
  ): Promise<ComplaintDocument> {
    this.ensureRole(
      utilisateur,
      CLOSE_ROLES,
      'Seul un administrateur ou le service client peut clôturer une réclamation',
    );

    const complaint = await this.findOrFail(id);

    if (complaint.statut === ComplaintStatus.CLOSED) {
      throw new BadRequestException('La réclamation est déjà clôturée');
    }

    complaint.statut = ComplaintStatus.CLOSED;
    complaint.dateModification = new Date();
    return complaint.save();
  }

  private async findOrFail(id: string): Promise<ComplaintDocument> {
    const complaint = await this.complaintModel.findById(id).exec();

    if (!complaint) {
      throw new NotFoundException('Réclamation introuvable');
    }

    return complaint;
  }

  private ensureMutable(complaint: ComplaintDocument): void {
    if (complaint.statut === ComplaintStatus.CLOSED) {
      throw new BadRequestException(
        'Une réclamation clôturée ne peut plus être modifiée',
      );
    }
  }

  private ensureRole(
    utilisateur: UtilisateurReferenceDto,
    roles: UserRole[],
    message: string,
  ): void {
    if (!roles.includes(utilisateur.role)) {
      throw new ForbiddenException(message);
    }
  }
}
