import { applyDecorators } from '@nestjs/common';
import { IsNotEmpty, IsString, Matches, MaxLength } from 'class-validator';

export const EXTERNAL_ID_PATTERN = /^[a-zA-Z0-9_-]+$/;

export function IsExternalId(): PropertyDecorator {
  return applyDecorators(
    IsString({ message: "L'identifiant doit être une chaîne de caractères" }),
    IsNotEmpty({ message: "L'identifiant est obligatoire" }),
    MaxLength(64, {
      message: "L'identifiant ne peut pas dépasser 64 caractères",
    }),
    Matches(EXTERNAL_ID_PATTERN, {
      message:
        "L'identifiant ne peut contenir que des lettres, des chiffres, '_' et '-'",
    }),
  );
}
