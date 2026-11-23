import { Routes } from '@angular/router';
import { Home } from './components/home/home';
import { Marketplace } from './components/marketplace/marketplace';
import { ProductDetails } from './components/product-details/product-details';

export const routes: Routes = [
  { path: '', component: Home },
  { path: 'marketplace', component: Marketplace },
  { path: 'shop', redirectTo: 'marketplace', pathMatch: 'full' },
  { path: 'product/:id', component: ProductDetails },
  { path: 'product', redirectTo: 'marketplace', pathMatch: 'full' },
  { path: '**', redirectTo: '' }
];
