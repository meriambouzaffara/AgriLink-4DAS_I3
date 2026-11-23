import { ApiProperty, ApiPropertyOptional } from '@nestjs/swagger';
import { Transform } from 'class-transformer';
import { IsInt, IsOptional, IsString, Max, MaxLength, Min } from 'class-validator';
import { IsExternalId } from '../../common/decorators/is-external-id.decorator';

export class CreateReviewDto {
  @ApiProperty({ example: 'client-001' })
  @IsExternalId()
  clientId: string;

  @ApiProperty({ example: 'agri-001' })
  @IsExternalId()
  agriculteurId: string;

  @ApiProperty({ example: 'cmd-001' })
  @IsExternalId()
  commandeId: string;

  @ApiProperty({ example: 5, minimum: 1, maximum: 5 })
  @IsInt({ message: 'La note doit être un entier' })
  @Min(1, { message: 'La note minimale est 1' })
  @Max(5, { message: 'La note maximale est 5' })
  note: number;

  @ApiPropertyOptional({ example: 'Livraison soignée et produits frais.', maxLength: 1000 })
  @IsOptional()
  @Transform(({ value }) => (typeof value === 'string' ? value.trim() : value))
  @IsString({ message: 'Le commentaire doit être une chaîne de caractères' })
  @MaxLength(1000, { message: 'Le commentaire ne peut pas dépasser 1000 caractères' })
  commentaire?: string;
}
