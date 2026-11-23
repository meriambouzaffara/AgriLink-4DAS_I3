import {
  CanActivate,
  ExecutionContext,
  Injectable,
  UnauthorizedException,
} from '@nestjs/common';
import {
  UTILISATEUR_ID_HEADER,
  UTILISATEUR_ROLE_HEADER,
} from '../constants/auth-headers';
import { EXTERNAL_ID_PATTERN } from '../decorators/is-external-id.decorator';
import { normalizeUserRole } from '../enums/user-role.enum';
import { RequestWithUtilisateur } from '../interfaces/request-with-utilisateur';

@Injectable()
export class UtilisateurGuard implements CanActivate {
  canActivate(context: ExecutionContext): boolean {
    const request = context.switchToHttp().getRequest<RequestWithUtilisateur>();
    const utilisateurId = request.header(UTILISATEUR_ID_HEADER)?.trim();
    const rawRole = request.header(UTILISATEUR_ROLE_HEADER)?.trim();

    if (!utilisateurId || !rawRole) {
      throw new UnauthorizedException(
        `Les en-têtes ${UTILISATEUR_ID_HEADER} et ${UTILISATEUR_ROLE_HEADER} sont requis`,
      );
    }

    if (!EXTERNAL_ID_PATTERN.test(utilisateurId) || utilisateurId.length > 64) {
      throw new UnauthorizedException('Identifiant utilisateur invalide');
    }

    const role = normalizeUserRole(rawRole);
    if (!role) {
      throw new UnauthorizedException('Rôle utilisateur invalide');
    }

    request.utilisateur = { utilisateurId, role };
    return true;
  }
}
