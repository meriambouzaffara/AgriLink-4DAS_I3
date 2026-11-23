import { ApiProperty } from '@nestjs/swagger';
import { IsNotEmpty, IsString, MaxLength } from 'class-validator';
import { IsExternalId } from '../../decorators/is-external-id.decorator';

/**
 * Représentation locale d'un agriculteur appartenant à MS1.
 * Cette classe n'est pas un schéma Mongoose et n'est pas persistée.
 */
export class AgriculteurReferenceDto {
  @ApiProperty({ example: 'agri-001' })
  @IsExternalId()
  agriculteurId: string;

  @ApiProperty({ example: 'El Mansouri' })
  @IsString()
  @IsNotEmpty()
  @MaxLength(100)
  nom: string;

  @ApiProperty({ example: 'Sara' })
  @IsString()
  @IsNotEmpty()
  @MaxLength(100)
  prenom: string;
}
