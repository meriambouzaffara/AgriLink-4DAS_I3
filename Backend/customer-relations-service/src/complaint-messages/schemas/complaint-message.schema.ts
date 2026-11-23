import { Prop, Schema, SchemaFactory } from '@nestjs/mongoose';
import { HydratedDocument } from 'mongoose';

export type ComplaintMessageDocument = HydratedDocument<ComplaintMessage>;

@Schema({ collection: 'complaintMessages', versionKey: false })
export class ComplaintMessage {
  @Prop({ required: true, trim: true })
  complaintId: string;

  @Prop({ required: true, trim: true })
  auteurId: string;

  @Prop({ required: true, trim: true, minlength: 1, maxlength: 2000 })
  contenu: string;

  @Prop({ required: true })
  dateEnvoi: Date;
}

export const ComplaintMessageSchema = SchemaFactory.createForClass(ComplaintMessage);

ComplaintMessageSchema.index({ complaintId: 1, dateEnvoi: 1 });
