import { Module } from '@nestjs/common';
import { MongooseModule } from '@nestjs/mongoose';
import { ComplaintsModule } from '../complaints/complaints.module';
import { ComplaintMessagesController } from './complaint-messages.controller';
import { ComplaintMessagesService } from './complaint-messages.service';
import {
  ComplaintMessage,
  ComplaintMessageSchema,
} from './schemas/complaint-message.schema';

@Module({
  imports: [
    MongooseModule.forFeature([
      { name: ComplaintMessage.name, schema: ComplaintMessageSchema },
    ]),
    ComplaintsModule,
  ],
  controllers: [ComplaintMessagesController],
  providers: [ComplaintMessagesService],
})
export class ComplaintMessagesModule {}
