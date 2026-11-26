import { HttpHeaders } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { ORDER_API_CONFIG } from '../config/order-api.config';
import { developmentCustomerHeader } from './ms3-http';
@Injectable({ providedIn: 'root' })
export class CustomerContextService {
  private readonly config = inject(ORDER_API_CONFIG);
  get developmentMode(): boolean { return this.config.developmentMode; }
  get customerId(): number {
    return Number(developmentCustomerHeader(this.config.developmentMode, this.config.developmentCustomerId));
  }
  orderHeaders(): HttpHeaders { return new HttpHeaders({ 'X-Customer-Id': String(this.customerId) }); }
}
