import { createParamDecorator, ExecutionContext, UnauthorizedException } from '@nestjs/common';
import { UtilisateurReferenceDto } from '../dto/references/utilisateur-reference.dto';
import { RequestWithUtilisateur } from '../interfaces/request-with-utilisateur';

export const CurrentUser = createParamDecorator(
  (_data: unknown, context: ExecutionContext): UtilisateurReferenceDto => {
    const request = context.switchToHttp().getRequest<RequestWithUtilisateur>();

    if (!request.utilisateur) {
      throw new UnauthorizedException('Utilisateur non authentifié');
    }

    return request.utilisateur;
  },
);
