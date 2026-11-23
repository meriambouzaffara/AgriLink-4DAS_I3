import { ApiPropertyOptional } from '@nestjs/swagger';
import { Transform } from 'class-transformer';
import { IsInt, IsOptional, IsString, Max, MaxLength, Min } from 'class-validator';

export class UpdateReviewDto {
  @ApiPropertyOptional({ example: 4, minimum: 1, maximum: 5 })
  @IsOptional()
  @IsInt({ message: 'La note doit être un entier' })
  @Min(1, { message: 'La note minimale est 1' })
  @Max(5, { message: 'La note maximale est 5' })
  note?: number;

  @ApiPropertyOptional({ example: 'Mise à jour de mon commentaire.', maxLength: 1000 })
  @IsOptional()
  @Transform(({ value }) => (typeof value === 'string' ? value.trim() : value))
  @IsString({ message: 'Le commentaire doit être une chaîne de caractères' })
  @MaxLength(1000, { message: 'Le commentaire ne peut pas dépasser 1000 caractères' })
  commentaire?: string;
}
