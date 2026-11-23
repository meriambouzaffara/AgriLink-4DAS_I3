import {
  Body,
  Controller,
  Get,
  HttpCode,
  HttpStatus,
  Param,
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
import { ComplaintMessagesService } from './complaint-messages.service';
import { CreateComplaintMessageDto } from './dto/create-complaint-message.dto';

@ApiTags('complaint-messages')
@ApiSecurity('utilisateur-id')
@ApiSecurity('utilisateur-role')
@UseGuards(UtilisateurGuard, RolesGuard)
@Controller('complaints/:complaintId/messages')
export class ComplaintMessagesController {
  constructor(
    private readonly complaintMessagesService: ComplaintMessagesService,
  ) {}

  @Post()
  @HttpCode(HttpStatus.CREATED)
  @Roles(
    UserRole.CLIENT,
    UserRole.ADMIN,
    UserRole.SERVICE_CLIENT,
    UserRole.AGRICULTEUR,
  )
  @ApiOperation({ summary: 'Ajouter un message à une réclamation' })
  @ApiCreatedResponse({ description: 'Message ajouté' })
  ajouterMessage(
    @Param('complaintId', ParseObjectIdPipe) complaintId: string,
    @Body() dto: CreateComplaintMessageDto,
    @CurrentUser() utilisateur: UtilisateurReferenceDto,
  ) {
    return this.complaintMessagesService.ajouterMessage(
      complaintId,
      dto,
      utilisateur,
    );
  }

  @Get()
  @Roles(
    UserRole.CLIENT,
    UserRole.ADMIN,
    UserRole.SERVICE_CLIENT,
    UserRole.AGRICULTEUR,
  )
  @ApiOperation({ summary: "Consulter l'historique des échanges" })
  @ApiOkResponse({ description: 'Messages de la réclamation, du plus ancien au plus récent' })
  obtenirMessagesParReclamation(
    @Param('complaintId', ParseObjectIdPipe) complaintId: string,
    @CurrentUser() utilisateur: UtilisateurReferenceDto,
  ) {
    return this.complaintMessagesService.obtenirMessagesParReclamation(
      complaintId,
      utilisateur,
    );
  }
}
