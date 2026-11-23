import {
  ArgumentsHost,
  Catch,
  ExceptionFilter,
  HttpException,
  HttpStatus,
  Logger,
} from '@nestjs/common';
import type { Response } from 'express';
import { RequestWithUtilisateur } from '../interfaces/request-with-utilisateur';

@Catch()
export class AllExceptionsFilter implements ExceptionFilter {
  private readonly logger = new Logger(AllExceptionsFilter.name);

  catch(exception: unknown, host: ArgumentsHost): void {
    const ctx = host.switchToHttp();
    const response = ctx.getResponse<Response>();
    const request = ctx.getRequest<RequestWithUtilisateur>();

    let status = HttpStatus.INTERNAL_SERVER_ERROR;
    let message: string | string[] = 'Erreur interne du serveur';
    let error = 'Internal Server Error';

    if (exception instanceof HttpException) {
      status = exception.getStatus();
      const body = exception.getResponse();
      error = exception.name.replace(/Exception$/, '') || error;

      if (typeof body === 'string') {
        message = body;
      } else if (typeof body === 'object' && body !== null) {
        const payload = body as { message?: string | string[]; error?: string };
        message = payload.message ?? message;
        error = payload.error ?? error;
      }
    } else if (this.isDuplicateKey(exception)) {
      status = HttpStatus.CONFLICT;
      message = 'Une ressource en conflit existe déjà';
      error = 'Conflict';
    } else if (exception instanceof Error && exception.name === 'ValidationError') {
      status = HttpStatus.BAD_REQUEST;
      message = exception.message;
      error = 'Bad Request';
    } else if (exception instanceof Error && exception.name === 'CastError') {
      status = HttpStatus.BAD_REQUEST;
      message = 'Identifiant invalide';
      error = 'Bad Request';
    } else {
      this.logger.error(
        exception instanceof Error ? exception.stack : String(exception),
      );
    }

    response.status(status).json({
      statusCode: status,
      error,
      message,
      path: request.url,
      timestamp: new Date().toISOString(),
    });
  }

  private isDuplicateKey(exception: unknown): boolean {
    return (
      typeof exception === 'object' &&
      exception !== null &&
      'code' in exception &&
      (exception as { code?: number }).code === 11000
    );
  }
}
