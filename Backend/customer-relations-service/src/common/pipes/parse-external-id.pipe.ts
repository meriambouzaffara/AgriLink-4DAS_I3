import { BadRequestException, Injectable, PipeTransform } from '@nestjs/common';
import { EXTERNAL_ID_PATTERN } from '../decorators/is-external-id.decorator';

@Injectable()
export class ParseExternalIdPipe implements PipeTransform<string, string> {
  transform(value: string): string {
    if (!EXTERNAL_ID_PATTERN.test(value) || value.length > 64) {
      throw new BadRequestException('Identifiant invalide');
    }

    return value;
  }
}
