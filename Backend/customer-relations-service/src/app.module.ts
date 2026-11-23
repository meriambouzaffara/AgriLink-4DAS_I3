import { Module } from '@nestjs/common';
import { ConfigModule, ConfigService } from '@nestjs/config';
import { MongooseModule } from '@nestjs/mongoose';
import { ComplaintMessagesModule } from './complaint-messages/complaint-messages.module';
import { ComplaintsModule } from './complaints/complaints.module';
import { ReviewsModule } from './reviews/reviews.module';

@Module({
  imports: [
    ConfigModule.forRoot({ isGlobal: true }),
    MongooseModule.forRootAsync({
      inject: [ConfigService],
      useFactory: (configService: ConfigService) => ({
        uri: configService.getOrThrow<string>('MONGODB_URI'),
      }),
    }),
    ReviewsModule,
    ComplaintsModule,
    ComplaintMessagesModule,
  ],
})
export class AppModule {}
