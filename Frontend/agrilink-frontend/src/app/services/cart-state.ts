import type { CartItem } from '../models/cart.model';
import type { DevProduct, OrderRequest } from '../models/order.model';
const LIMIT=99_999_999_999;
export function toMillis(value:number):number {
  if(!Number.isFinite(value)||value<0) throw new Error('Nombre invalide.');
  const raw=value.toString();
  if(!/^\d+(?:\.\d{1,3})?$/.test(raw)) throw new Error('Trois décimales maximum.');
  const [whole,fraction='']=raw.split('.');
  const n=Number(whole)*1000+Number(fraction.padEnd(3,'0'));
  if(!Number.isSafeInteger(n)||n>LIMIT) throw new Error('Valeur trop élevée.');
  return n;
}
export function formatMillis(value:number):string { return (value/1000).toFixed(3); }
export function subtotalMillis(item:CartItem):number {
  const n=toMillis(item.unitPrice)*toMillis(item.quantity);
  if(!Number.isSafeInteger(n)) throw new Error('Montant trop élevé.');
  return Math.floor((n+500)/1000); // HALF_UP
}
export function totalMillis(items:CartItem[]):number {
  return items.reduce((sum,item)=>{const n=sum+subtotalMillis(item);if(!Number.isSafeInteger(n))throw new Error('Montant trop élevé.');return n;},0);
}
export function validProduct(p:DevProduct):boolean {
  try{return Number.isSafeInteger(p.id)&&p.id>0&&typeof p.name==='string'&&!!p.name.trim()&&toMillis(p.unitPrice)>0&&toMillis(p.availableQuantity)>=0;}catch{return false;}
}
export function validCartItem(i:CartItem):boolean {
  try{return Number.isSafeInteger(i.productId)&&i.productId>0&&typeof i.name==='string'&&!!i.name.trim()&&toMillis(i.unitPrice)>0&&toMillis(i.availableQuantity)>=0&&toMillis(i.quantity)>0;}catch{return false;}
}
export function readCart(raw:string|null):CartItem[] {
  if(!raw)return [];
  try{const items:unknown=JSON.parse(raw);if(!Array.isArray(items)||items.length>100)return [];
    const ids=new Set<number>();
    for(const item of items){if(!item||typeof item!=='object'||!validCartItem(item as CartItem))return [];
      const id=(item as CartItem).productId;if(ids.has(id))return [];ids.add(id);}
    return items as CartItem[];
  }catch{return [];}
}
export function setCartQuantity(items:CartItem[],id:number,quantity:number):CartItem[] {
  const found=items.find(i=>i.productId===id);if(!found)throw new Error('Produit absent du panier.');
  if(toMillis(quantity)<=0)throw new Error('La quantité doit être positive.');
  if(toMillis(quantity)>toMillis(found.availableQuantity))throw new Error('Stock disponible insuffisant.');
  return items.map(i=>i.productId===id?{...i,quantity}:i);
}
export function addCartProduct(items:CartItem[],product:DevProduct,quantity:number):CartItem[] {
  if(!validProduct(product))throw new Error('Produit indisponible.');
  if(toMillis(quantity)<=0)throw new Error('La quantité doit être positive.');
  const existing=items.find(i=>i.productId===product.id);
  const next=(existing?toMillis(existing.quantity):0)+toMillis(quantity);
  if(next>toMillis(product.availableQuantity))throw new Error('Stock disponible insuffisant.');
  const updated:CartItem={productId:product.id,name:product.name,unitPrice:product.unitPrice,availableQuantity:product.availableQuantity,quantity:next/1000};
  return existing?items.map(i=>i.productId===product.id?updated:i):[...items,updated];
}
export function toOrderRequest(items:CartItem[]):OrderRequest {
  if(!items.length)throw new Error('Le panier est vide.');
  if(items.some(i=>!validCartItem(i)||toMillis(i.quantity)>toMillis(i.availableQuantity)))throw new Error('Produit indisponible ou stock insuffisant.');
  return {lignes:items.map(i=>({produitId:i.productId,quantite:i.quantity}))};
}
