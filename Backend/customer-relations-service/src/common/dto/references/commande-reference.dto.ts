import { ApiProperty } from '@nestjs/swagger';
import { IsDateString, IsNotEmpty, IsString, MaxLength } from 'class-validator';
import { IsExternalId } from '../../decorators/is-external-id.decorator';

/**
 * Représentation locale d'une commande appartenant à MS3.
 * Cette classe n'est pas un schéma Mongoose et n'est pas persistée.
 */
export class CommandeReferenceDto {
  @ApiProperty({ example: 'cmd-001' })
  @IsExternalId()
  commandeId: string;

  @ApiProperty({ example: 'client-001' })
  @IsExternalId()
  clientId: string;

  @ApiProperty({ example: 'agri-001' })
  @IsExternalId()
  agriculteurId: string;

  @ApiProperty({ example: 'LIVREE', description: 'Statut tel que fourni par MS3' })
  @IsString()
  @IsNotEmpty()
  @MaxLength(50)
  statut: string;

  @ApiProperty({ example: '2026-11-22T10:00:00.000Z' })
  @IsDateString(
    {},
    { message: 'La date de commande doit être une date ISO 8601' },
  )
  dateCommande: string;
}
