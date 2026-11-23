import {
  Body,
  Controller,
  Delete,
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
  ApiNoContentResponse,
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
import { ParseExternalIdPipe } from '../common/pipes/parse-external-id.pipe';
import { ParseObjectIdPipe } from '../common/pipes/parse-object-id.pipe';
import { CreateReviewDto } from './dto/create-review.dto';
import { UpdateReviewDto } from './dto/update-review.dto';
import { ReviewsService } from './reviews.service';

@ApiTags('reviews')
@Controller('reviews')
export class ReviewsController {
  constructor(private readonly reviewsService: ReviewsService) {}

  @Post()
  @HttpCode(HttpStatus.CREATED)
  @UseGuards(UtilisateurGuard, RolesGuard)
  @Roles(UserRole.CLIENT)
  @ApiSecurity('utilisateur-id')
  @ApiSecurity('utilisateur-role')
  @ApiOperation({ summary: 'Noter un agriculteur après une commande' })
  @ApiCreatedResponse({ description: 'Avis créé' })
  creerAvis(
    @Body() dto: CreateReviewDto,
    @CurrentUser() utilisateur: UtilisateurReferenceDto,
  ) {
    return this.reviewsService.creerAvis(dto, utilisateur);
  }

  @Get('agriculteur/:id/average')
  @ApiOperation({ summary: "Consulter la moyenne des notes d'un agriculteur" })
  @ApiOkResponse({
    schema: {
      type: 'object',
      properties: {
        agriculteurId: { type: 'string', example: 'agri-001' },
        moyenne: { type: 'number', example: 4.5 },
        nombreAvis: { type: 'integer', example: 2 },
      },
    },
  })
  calculerMoyenneNotes(@Param('id', ParseExternalIdPipe) id: string) {
    return this.reviewsService.calculerMoyenneNotes(id);
  }

  @Get('agriculteur/:id')
  @ApiOperation({ summary: "Consulter les avis d'un agriculteur" })
  @ApiOkResponse({ description: "Liste des avis de l'agriculteur" })
  obtenirAvisParAgriculteur(@Param('id', ParseExternalIdPipe) id: string) {
    return this.reviewsService.obtenirAvisParAgriculteur(id);
  }

  @Get(':id')
  @ApiOperation({ summary: 'Consulter un avis' })
  @ApiOkResponse({ description: "Détail de l'avis" })
  obtenirAvisParId(@Param('id', ParseObjectIdPipe) id: string) {
    return this.reviewsService.obtenirAvisParId(id);
  }

  @Patch(':id')
  @UseGuards(UtilisateurGuard, RolesGuard)
  @Roles(UserRole.CLIENT)
  @ApiSecurity('utilisateur-id')
  @ApiSecurity('utilisateur-role')
  @ApiOperation({ summary: 'Modifier son propre avis' })
  @ApiOkResponse({ description: 'Avis modifié' })
  modifierAvis(
    @Param('id', ParseObjectIdPipe) id: string,
    @Body() dto: UpdateReviewDto,
    @CurrentUser() utilisateur: UtilisateurReferenceDto,
  ) {
    return this.reviewsService.modifierAvis(id, dto, utilisateur);
  }

  @Delete(':id')
  @HttpCode(HttpStatus.NO_CONTENT)
  @UseGuards(UtilisateurGuard, RolesGuard)
  @Roles(UserRole.CLIENT)
  @ApiSecurity('utilisateur-id')
  @ApiSecurity('utilisateur-role')
  @ApiOperation({ summary: 'Supprimer son propre avis' })
  @ApiNoContentResponse({ description: 'Avis supprimé' })
  supprimerAvis(
    @Param('id', ParseObjectIdPipe) id: string,
    @CurrentUser() utilisateur: UtilisateurReferenceDto,
  ) {
    return this.reviewsService.supprimerAvis(id, utilisateur);
  }
}
