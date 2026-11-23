import { ApiProperty } from '@nestjs/swagger';
import { IsEmail, IsNotEmpty, IsString, MaxLength } from 'class-validator';
import { IsExternalId } from '../../decorators/is-external-id.decorator';

/**
 * Représentation locale d'un client appartenant à MS1.
 * Cette classe n'est pas un schéma Mongoose et n'est pas persistée.
 */
export class ClientReferenceDto {
  @ApiProperty({ example: 'client-001' })
  @IsExternalId()
  clientId: string;

  @ApiProperty({ example: 'Benali' })
  @IsString()
  @IsNotEmpty()
  @MaxLength(100)
  nom: string;

  @ApiProperty({ example: 'Amine' })
  @IsString()
  @IsNotEmpty()
  @MaxLength(100)
  prenom: string;

  @ApiProperty({ example: 'amine.benali@example.com' })
  @IsEmail({}, { message: 'Adresse e-mail invalide' })
  @MaxLength(180)
  email: string;
}
