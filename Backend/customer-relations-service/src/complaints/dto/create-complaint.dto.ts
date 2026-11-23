import { ApiProperty } from '@nestjs/swagger';
import { Transform } from 'class-transformer';
import { IsNotEmpty, IsString, MaxLength, MinLength } from 'class-validator';
import { IsExternalId } from '../../common/decorators/is-external-id.decorator';

export class CreateComplaintDto {
  @ApiProperty({ example: 'client-001' })
  @IsExternalId()
  clientId: string;

  @ApiProperty({ example: 'cmd-001' })
  @IsExternalId()
  commandeId: string;

  @ApiProperty({ example: 'Produit manquant', minLength: 3, maxLength: 150 })
  @Transform(({ value }) => (typeof value === 'string' ? value.trim() : value))
  @IsString()
  @IsNotEmpty()
  @MinLength(3, { message: 'Le sujet doit contenir au moins 3 caractères' })
  @MaxLength(150, { message: 'Le sujet ne peut pas dépasser 150 caractères' })
  sujet: string;

  @ApiProperty({
    example: 'La commande livrée ne contenait pas les tomates prévues.',
    minLength: 10,
    maxLength: 4000,
  })
  @Transform(({ value }) => (typeof value === 'string' ? value.trim() : value))
  @IsString()
  @IsNotEmpty()
  @MinLength(10, { message: 'La description doit contenir au moins 10 caractères' })
  @MaxLength(4000, { message: 'La description ne peut pas dépasser 4000 caractères' })
  description: string;
}
