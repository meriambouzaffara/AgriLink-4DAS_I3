import { ApiProperty } from '@nestjs/swagger';
import { IsEnum } from 'class-validator';
import { ComplaintStatus } from '../enums/complaint-status.enum';

export class UpdateComplaintStatusDto {
  @ApiProperty({ enum: ComplaintStatus, example: ComplaintStatus.IN_PROGRESS })
  @IsEnum(ComplaintStatus, { message: 'Statut de réclamation invalide' })
  statut: ComplaintStatus;
}
