import { ApiProperty } from '@nestjs/swagger';
import { IsEnum } from 'class-validator';
import { IsExternalId } from '../../decorators/is-external-id.decorator';
import { UserRole } from '../../enums/user-role.enum';

/**
 * Représentation de l'utilisateur connecté et de son rôle.
 * Prépare l'intégration Keycloak sans l'implémenter.
 * Cette classe n'est pas un schéma Mongoose et n'est pas persistée.
 */
export class UtilisateurReferenceDto {
  @ApiProperty({ example: 'client-001' })
  @IsExternalId()
  utilisateurId: string;

  @ApiProperty({ enum: UserRole, example: UserRole.CLIENT })
  @IsEnum(UserRole, { message: 'Rôle utilisateur invalide' })
  role: UserRole;
}
