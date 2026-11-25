import { Component, computed, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ProductService } from '../../../services/product.service';
import { FarmerHeader } from '../farmer-header/farmer-header';

@Component({
  selector: 'app-farmer-overview',
  standalone: true,
  imports: [CommonModule, RouterModule, FarmerHeader],
  templateUrl: './farmer-overview.html',
  styleUrl: './farmer-overview.css'
})
export class FarmerOverview {
  private productService = inject(ProductService);

  farmer = computed(() => this.productService.getCurrentFarmer());
  farmerOffers = computed(() => this.productService.getOffersByFarmer(this.farmer().id));
  farmerHarvests = computed(() => this.productService.getHarvestsByFarmer(this.farmer().id));

  totalStockKg = computed(() => {
    return this.farmerOffers().reduce((acc, curr) => acc + (curr.offer.availableStock || 0), 0);
  });

  totalHarvestedQty = computed(() => {
    return this.farmerHarvests()
      .filter(h => h.status === 'Récoltée' || h.status === 'Commercialisée')
      .reduce((acc, curr) => acc + (curr.quantity || 0), 0);
  });
}
