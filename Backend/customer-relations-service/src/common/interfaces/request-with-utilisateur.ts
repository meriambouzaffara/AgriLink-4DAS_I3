import type { Request } from 'express';
import type { UtilisateurReferenceDto } from '../dto/references/utilisateur-reference.dto';

export interface RequestWithUtilisateur extends Request {
  utilisateur?: UtilisateurReferenceDto;
}
