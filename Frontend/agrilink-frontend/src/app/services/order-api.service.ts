import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { ORDER_API_CONFIG } from '../config/order-api.config';
import { DevProduct, OrderRequest, OrderResponse } from '../models/order.model';
import { CustomerContextService } from './customer-context.service';
import { orderHttpErrorMessage } from './ms3-http';
export function orderErrorMessage(error:unknown):string {
  if(!(error instanceof HttpErrorResponse))return 'Une erreur inattendue est survenue.';
  const detail=typeof error.error?.message==='string'?error.error.message:'';
  return orderHttpErrorMessage(error.status,detail);
}
@Injectable({ providedIn: 'root' })
export class OrderApiService {
  private readonly http=inject(HttpClient);
  private readonly config=inject(ORDER_API_CONFIG);
  private readonly customer=inject(CustomerContextService);
  private get base():string{return this.config.baseUrl.replace(/\/$/,'');}
  private fail(error:HttpErrorResponse){return throwError(()=>new Error(orderErrorMessage(error)));}
  getDevelopmentProducts(){
    if(!this.customer.developmentMode)return throwError(()=>new Error('Catalogue de développement désactivé.'));
    return this.http.get<DevProduct[]>(`${this.base}/api/dev/products`).pipe(catchError(e=>this.fail(e)));
  }
  createOrder(request:OrderRequest){
    return this.http.post<OrderResponse>(`${this.base}/api/orders`,request,{headers:this.customer.orderHeaders()}).pipe(catchError(e=>this.fail(e)));
  }
  getOrder(id:number){
    return this.http.get<OrderResponse>(`${this.base}/api/orders/${encodeURIComponent(id)}`,{headers:this.customer.orderHeaders()}).pipe(catchError(e=>this.fail(e)));
  }
  getCustomerOrders(id=this.customer.customerId){
    return this.http.get<OrderResponse[]>(`${this.base}/api/orders/customer/${encodeURIComponent(id)}`,{headers:this.customer.orderHeaders()}).pipe(catchError(e=>this.fail(e)));
  }
  confirmOrder(id:number){
    return this.http.patch<OrderResponse>(`${this.base}/api/orders/${encodeURIComponent(id)}/confirm`,{},{headers:this.customer.orderHeaders()}).pipe(catchError(e=>this.fail(e)));
  }
  cancelOrder(id:number){
    return this.http.patch<OrderResponse>(`${this.base}/api/orders/${encodeURIComponent(id)}/cancel`,{},{headers:this.customer.orderHeaders()}).pipe(catchError(e=>this.fail(e)));
  }
}
