import { ApiProperty } from '@nestjs/swagger';
import { Transform } from 'class-transformer';
import { IsNotEmpty, IsString, MaxLength, MinLength } from 'class-validator';
import { IsExternalId } from '../../common/decorators/is-external-id.decorator';

export class CreateComplaintMessageDto {
  @ApiProperty({ example: 'client-001' })
  @IsExternalId()
  auteurId: string;

  @ApiProperty({
    example: 'Le colis est arrivé incomplet, pouvez-vous vérifier ?',
    minLength: 1,
    maxLength: 2000,
  })
  @Transform(({ value }) => (typeof value === 'string' ? value.trim() : value))
  @IsString()
  @IsNotEmpty()
  @MinLength(1, { message: 'Le message ne peut pas être vide' })
  @MaxLength(2000, { message: 'Le message ne peut pas dépasser 2000 caractères' })
  contenu: string;
}
