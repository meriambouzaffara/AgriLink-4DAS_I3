import { Routes } from '@angular/router';
import { Home } from './components/home/home';
import { Marketplace } from './components/marketplace/marketplace';
import { ProductDetails } from './components/product-details/product-details';
import { FarmerOverview } from './components/farmer/farmer-overview/farmer-overview';
import { FarmerOffers } from './components/farmer/farmer-offers/farmer-offers';
import { FarmerHarvests } from './components/farmer/farmer-harvests/farmer-harvests';
import { FarmerCreateProduct } from './components/farmer/farmer-create-product/farmer-create-product';
import { CooperativeOverview } from './components/cooperative/cooperative-overview/cooperative-overview';
import { CooperativeAnnonces } from './components/cooperative/cooperative-annonces/cooperative-annonces';
import { CooperativeBesoins } from './components/cooperative/cooperative-besoins/cooperative-besoins';
import { CooperativePropositions } from './components/cooperative/cooperative-propositions/cooperative-propositions';

export const routes: Routes = [
  { path: '', component: Home },
  { path: 'marketplace', component: Marketplace },
  { path: 'shop', redirectTo: 'marketplace', pathMatch: 'full' },
  { path: 'product/:id', component: ProductDetails },
  { path: 'product', redirectTo: 'marketplace', pathMatch: 'full' },
  
  // Dedicated Farmer Pages (Split components)
  { path: 'espace-agriculteur', component: FarmerOverview },
  { path: 'espace-agriculteur/offres', component: FarmerOffers },
  { path: 'espace-agriculteur/recoltes', component: FarmerHarvests },
  { path: 'espace-agriculteur/nouveau-produit', component: FarmerCreateProduct },

  // MS5 - Espace Coopérative Pages
  { path: 'espace-cooperative', component: CooperativeOverview },
  { path: 'espace-cooperative/annonces', component: CooperativeAnnonces },
  { path: 'espace-cooperative/besoins', component: CooperativeBesoins },
  { path: 'espace-cooperative/propositions', component: CooperativePropositions },
  { path: 'espace-cooperative/nouveau-besoin', component: CooperativeBesoins },

  // Helpful Aliases & Redirects
  { path: 'cooperative', redirectTo: 'espace-cooperative', pathMatch: 'full' },
  { path: 'cooperative/collectes', redirectTo: 'espace-cooperative/annonces', pathMatch: 'full' },
  { path: 'cooperative/besoins', redirectTo: 'espace-cooperative/besoins', pathMatch: 'full' },
  { path: 'farmer-dashboard', redirectTo: 'espace-agriculteur', pathMatch: 'full' },
  { path: 'agriculteur', redirectTo: 'espace-agriculteur', pathMatch: 'full' },
  { path: 'agriculteur/offres', redirectTo: 'espace-agriculteur/offres', pathMatch: 'full' },
  { path: 'agriculteur/recoltes', redirectTo: 'espace-agriculteur/recoltes', pathMatch: 'full' },
  
  { path: '**', redirectTo: '' }
];
