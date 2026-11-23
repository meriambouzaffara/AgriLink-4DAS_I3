import { Prop, Schema, SchemaFactory } from '@nestjs/mongoose';
import { HydratedDocument } from 'mongoose';
import { ComplaintStatus } from '../enums/complaint-status.enum';

export type ComplaintDocument = HydratedDocument<Complaint>;

@Schema({ collection: 'complaints', versionKey: false })
export class Complaint {
  @Prop({ required: true, trim: true })
  clientId: string;

  @Prop({ required: true, trim: true })
  commandeId: string;

  @Prop({ required: true, trim: true, minlength: 3, maxlength: 150 })
  sujet: string;

  @Prop({ required: true, trim: true, minlength: 10, maxlength: 4000 })
  description: string;

  @Prop({
    required: true,
    enum: ComplaintStatus,
    default: ComplaintStatus.OPEN,
    type: String,
  })
  statut: ComplaintStatus;

  @Prop({ required: true })
  dateCreation: Date;

  @Prop({ required: true })
  dateModification: Date;
}

export const ComplaintSchema = SchemaFactory.createForClass(Complaint);

ComplaintSchema.index({ clientId: 1, dateCreation: -1 });
ComplaintSchema.index({ statut: 1, dateCreation: -1 });
