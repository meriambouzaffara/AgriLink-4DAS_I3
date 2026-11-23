import { Prop, Schema, SchemaFactory } from '@nestjs/mongoose';
import { HydratedDocument } from 'mongoose';

export type ReviewDocument = HydratedDocument<Review>;

@Schema({ collection: 'reviews', versionKey: false })
export class Review {
  @Prop({ required: true, trim: true })
  clientId: string;

  @Prop({ required: true, trim: true })
  agriculteurId: string;

  @Prop({ required: true, unique: true, trim: true })
  commandeId: string;

  @Prop({
    required: true,
    min: 1,
    max: 5,
    validate: {
      validator: Number.isInteger,
      message: 'La note doit être un entier compris entre 1 et 5',
    },
  })
  note: number;

  @Prop({ required: false, trim: true, maxlength: 1000 })
  commentaire?: string;

  @Prop({ required: true })
  dateCreation: Date;

  @Prop({ required: true })
  dateModification: Date;
}

export const ReviewSchema = SchemaFactory.createForClass(Review);

ReviewSchema.index({ agriculteurId: 1, dateCreation: -1 });
