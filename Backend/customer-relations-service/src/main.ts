import { ValidationPipe } from '@nestjs/common';
import { NestFactory } from '@nestjs/core';
import { DocumentBuilder, SwaggerModule } from '@nestjs/swagger';
import { AppModule } from './app.module';
import {
  UTILISATEUR_ID_HEADER,
  UTILISATEUR_ROLE_HEADER,
} from './common/constants/auth-headers';
import { AllExceptionsFilter } from './common/filters/all-exceptions.filter';

async function bootstrap() {
  const app = await NestFactory.create(AppModule);

  app.setGlobalPrefix('api');
  app.useGlobalPipes(
    new ValidationPipe({
      whitelist: true,
      forbidNonWhitelisted: true,
      transform: true,
      transformOptions: { enableImplicitConversion: true },
    }),
  );
  app.useGlobalFilters(new AllExceptionsFilter());
  app.enableShutdownHooks();

  const config = new DocumentBuilder()
    .setTitle('AgriLink MS7 - Customer Relations')
    .setDescription(
      'Gestion des avis, des réclamations et des messages. ' +
        `L'identité est fournie localement par les en-têtes ${UTILISATEUR_ID_HEADER} et ${UTILISATEUR_ROLE_HEADER} ` +
        '(CLIENT, ADMIN, SERVICE_CLIENT, AGRICULTEUR) en préparation de Keycloak. ' +
        'Aucune communication avec un autre microservice n\'est effectuée.',
    )
    .setVersion('1.0')
    .addApiKey(
      { type: 'apiKey', in: 'header', name: UTILISATEUR_ID_HEADER },
      'utilisateur-id',
    )
    .addApiKey(
      { type: 'apiKey', in: 'header', name: UTILISATEUR_ROLE_HEADER },
      'utilisateur-role',
    )
    .build();

  const document = SwaggerModule.createDocument(app, config);
  SwaggerModule.setup('docs', app, document, { useGlobalPrefix: true });

  const port = process.env.PORT ?? 3007;
  await app.listen(port);
}

void bootstrap();
