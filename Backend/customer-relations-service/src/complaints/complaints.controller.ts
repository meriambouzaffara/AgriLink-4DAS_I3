import {
  Body,
  Controller,
  Get,
  HttpCode,
  HttpStatus,
  Param,
  Patch,
  Post,
  UseGuards,
} from '@nestjs/common';
import {
  ApiCreatedResponse,
  ApiOkResponse,
  ApiOperation,
  ApiSecurity,
  ApiTags,
} from '@nestjs/swagger';
import { CurrentUser } from '../common/decorators/current-user.decorator';
import { Roles } from '../common/decorators/roles.decorator';
import { UtilisateurReferenceDto } from '../common/dto/references/utilisateur-reference.dto';
import { UserRole } from '../common/enums/user-role.enum';
import { RolesGuard } from '../common/guards/roles.guard';
import { UtilisateurGuard } from '../common/guards/utilisateur.guard';
import { ParseObjectIdPipe } from '../common/pipes/parse-object-id.pipe';
import { ComplaintsService } from './complaints.service';
import { CreateComplaintDto } from './dto/create-complaint.dto';
import { UpdateComplaintStatusDto } from './dto/update-complaint-status.dto';
import { ComplaintStatus } from './enums/complaint-status.enum';

@ApiTags('complaints')
@ApiSecurity('utilisateur-id')
@ApiSecurity('utilisateur-role')
@UseGuards(UtilisateurGuard, RolesGuard)
@Controller('complaints')
export class ComplaintsController {
  constructor(private readonly complaintsService: ComplaintsService) {}

  @Post()
  @HttpCode(HttpStatus.CREATED)
  @Roles(UserRole.CLIENT)
  @ApiOperation({ summary: 'Créer une réclamation associée à une commande' })
  @ApiCreatedResponse({ description: 'Réclamation créée avec le statut OPEN' })
  creerReclamation(
    @Body() dto: CreateComplaintDto,
    @CurrentUser() utilisateur: UtilisateurReferenceDto,
  ) {
    return this.complaintsService.creerReclamation(dto, utilisateur);
  }

  @Get('mine')
  @Roles(UserRole.CLIENT)
  @ApiOperation({ summary: 'Consulter ses réclamations' })
  @ApiOkResponse({ description: 'Réclamations du client connecté' })
  obtenirMesReclamations(@CurrentUser() utilisateur: UtilisateurReferenceDto) {
    return this.complaintsService.obtenirMesReclamations(utilisateur);
  }

  @Get()
  @Roles(UserRole.ADMIN, UserRole.SERVICE_CLIENT, UserRole.AGRICULTEUR)
  @ApiOperation({ summary: 'Consulter toutes les réclamations' })
  @ApiOkResponse({ description: 'Liste des réclamations' })
  obtenirToutesLesReclamations(
    @CurrentUser() utilisateur: UtilisateurReferenceDto,
  ) {
    return this.complaintsService.obtenirToutesLesReclamations(utilisateur);
  }

  @Get(':id')
  @Roles(
    UserRole.CLIENT,
    UserRole.ADMIN,
    UserRole.SERVICE_CLIENT,
    UserRole.AGRICULTEUR,
  )
  @ApiOperation({ summary: "Consulter le détail d'une réclamation" })
  @ApiOkResponse({ description: 'Détail de la réclamation' })
  obtenirReclamationParId(
    @Param('id', ParseObjectIdPipe) id: string,
    @CurrentUser() utilisateur: UtilisateurReferenceDto,
  ) {
    return this.complaintsService.obtenirReclamationParId(id, utilisateur);
  }

  @Patch(':id/status')
  @Roles(UserRole.ADMIN, UserRole.SERVICE_CLIENT, UserRole.AGRICULTEUR)
  @ApiOperation({
    summary: 'Modifier le statut ou clôturer une réclamation',
    description:
      'Le statut CLOSED est réservé à ADMIN et SERVICE_CLIENT. Les autres transitions sont ouvertes à ADMIN, SERVICE_CLIENT et AGRICULTEUR.',
  })
  @ApiOkResponse({ description: 'Statut mis à jour' })
  modifierStatut(
    @Param('id', ParseObjectIdPipe) id: string,
    @Body() dto: UpdateComplaintStatusDto,
    @CurrentUser() utilisateur: UtilisateurReferenceDto,
  ) {
    if (dto.statut === ComplaintStatus.CLOSED) {
      return this.complaintsService.cloturerReclamation(id, utilisateur);
    }

    return this.complaintsService.modifierStatut(id, dto, utilisateur);
  }
}
