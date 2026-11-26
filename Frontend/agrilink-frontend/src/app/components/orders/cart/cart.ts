import { CommonModule } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { switchMap } from 'rxjs';
import { CartItem } from '../../../models/cart.model';
import { DevProduct, OrderResponse } from '../../../models/order.model';
import { CartService } from '../../../services/cart.service';
import { formatMillis, toMillis, toOrderRequest, validProduct } from '../../../services/cart-state';
import { CustomerContextService } from '../../../services/customer-context.service';
import { OrderApiService } from '../../../services/order-api.service';
@Component({selector:'app-cart',standalone:true,imports:[CommonModule,RouterLink],templateUrl:'./cart.html',styleUrl:'./cart.css'})
export class Cart {
  // Presentation only; product data and identifiers still come from the MS3 development API.
  private readonly productSymbols: Record<number, string> = {
    101: '🍅', 102: '🥔', 103: '🥕', 104: '🧅', 105: '🍎',
  };
  readonly cart=inject(CartService);
  private readonly api=inject(OrderApiService);
  readonly customer=inject(CustomerContextService);
  readonly products=signal<DevProduct[]>([]);
  readonly loading=signal(false);
  readonly submitting=signal(false);
  readonly catalogError=signal('');
  readonly actionError=signal('');
  readonly actionMessage=signal('');
  readonly createdOrder=signal<OrderResponse|null>(null);
  constructor(){this.refreshProducts();}
  money(value:number):string{return formatMillis(toMillis(value));}
  lineTotal(item:CartItem):string{return formatMillis(this.cart.subtotalMillis(item));}
  total():string{return formatMillis(this.cart.estimatedTotalMillis());}
  productSymbol(id:number):string{return this.productSymbols[id] ?? '🌿';}
  canSubmit():boolean{
    try{toOrderRequest(this.cart.items());return true;}
    catch{return false;}
  }
  refreshProducts():void{
    this.loading.set(true);this.catalogError.set('');
    this.api.getDevelopmentProducts().subscribe({
      next:products=>{const valid=products.filter(validProduct);this.products.set(valid);this.cart.reconcileCatalog(valid);this.loading.set(false);},
      error:error=>{this.catalogError.set(error.message);this.loading.set(false);}
    });
  }
  addProduct(product:DevProduct,raw:string):void{
    this.actionError.set('');this.actionMessage.set('');
    try{this.cart.add(product,Number(raw));this.actionMessage.set(`${product.name} ajouté au panier.`);}
    catch(error){this.actionError.set((error as Error).message);}
  }
  setQuantity(item:CartItem,raw:string,input:HTMLInputElement):void{
    this.actionError.set('');
    try{this.cart.setQuantity(item.productId,Number(raw));}
    catch(error){this.actionError.set((error as Error).message);input.value=String(item.quantity);}
  }
  increase(item:CartItem):void{this.actionError.set('');try{this.cart.increase(item.productId);}catch(error){this.actionError.set((error as Error).message);}}
  decrease(item:CartItem):void{this.actionError.set('');try{this.cart.decrease(item.productId);}catch(error){this.actionError.set((error as Error).message);}}
  submit():void{
    if(this.submitting())return;
    this.actionError.set('');this.actionMessage.set('');this.createdOrder.set(null);
    if(!this.cart.lineCount()){this.actionError.set('Votre panier est vide.');return;}
    this.submitting.set(true);
    this.api.getDevelopmentProducts().pipe(switchMap(products=>{
      const valid=products.filter(validProduct);this.products.set(valid);this.cart.reconcileCatalog(valid);
      return this.api.createOrder(toOrderRequest(this.cart.items()));
    })).subscribe({
      next:order=>{this.createdOrder.set(order);this.cart.clear();this.submitting.set(false);this.refreshProducts();},
      error:error=>{this.actionError.set(error.message);this.submitting.set(false);}
    });
  }
}
