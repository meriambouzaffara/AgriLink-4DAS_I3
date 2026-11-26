import { computed, effect, inject, Injectable, signal } from '@angular/core';
import { CartItem } from '../models/cart.model';
import { DevProduct } from '../models/order.model';
import { CustomerContextService } from './customer-context.service';
import { addCartProduct, readCart, setCartQuantity, subtotalMillis, toMillis, totalMillis } from './cart-state';
@Injectable({ providedIn: 'root' })
export class CartService {
  private readonly customer=inject(CustomerContextService);
  private readonly key=this.customer.developmentMode?`agrilink.ms3.dev-cart.v1.customer-${this.customer.customerId}`:'agrilink.ms3.cart.v1.disabled';
  private readonly state=signal<CartItem[]>(this.restore());
  readonly items=this.state.asReadonly();
  readonly lineCount=computed(()=>this.state().length);
  readonly estimatedTotalMillis=computed(()=>totalMillis(this.state()));
  constructor(){effect(()=>{try{if(this.customer.developmentMode&&typeof localStorage!=='undefined')localStorage.setItem(this.key,JSON.stringify(this.state()));}catch{/* in-memory fallback */}});}
  private restore():CartItem[]{try{return this.customer.developmentMode&&typeof localStorage!=='undefined'?readCart(localStorage.getItem(this.key)):[];}catch{return [];}}
  subtotalMillis(item:CartItem):number{return subtotalMillis(item);}
  add(product:DevProduct,quantity:number):void{this.state.set(addCartProduct(this.state(),product,quantity));}
  remove(id:number):void{this.state.update(items=>items.filter(i=>i.productId!==id));}
  setQuantity(id:number,quantity:number):void{this.state.set(setCartQuantity(this.state(),id,quantity));}
  increase(id:number):void{const i=this.state().find(x=>x.productId===id);if(i)this.setQuantity(id,(toMillis(i.quantity)+1000)/1000);}
  decrease(id:number):void{const i=this.state().find(x=>x.productId===id);if(i&&toMillis(i.quantity)>1000)this.setQuantity(id,(toMillis(i.quantity)-1000)/1000);}
  clear():void{this.state.set([]);}
  reconcileCatalog(products:DevProduct[]):void{const byId=new Map(products.map(p=>[p.id,p]));this.state.update(items=>items.map(i=>{const p=byId.get(i.productId);return p?{...i,name:p.name,unitPrice:p.unitPrice,availableQuantity:p.availableQuantity}:{...i,availableQuantity:0};}));}
}
