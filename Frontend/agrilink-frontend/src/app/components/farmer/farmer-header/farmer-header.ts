import { Component, computed, inject, input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ProductService } from '../../../services/product.service';
import { FarmerProfile } from '../../../models/product.model';

@Component({
  selector: 'app-farmer-header',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './farmer-header.html',
  styleUrl: './farmer-header.css'
})
export class FarmerHeader {
  private productService = inject(ProductService);

  pageTitle = input<string>('Espace Producteur');
  currentPage = input<string>('Tableau de Bord');

  farmer = computed<FarmerProfile>(() => this.productService.getCurrentFarmer());
  allFarmers = computed<FarmerProfile[]>(() => this.productService.getFarmers());

  offersCount = computed(() => this.productService.getOffersByFarmer(this.farmer().id).length);
  harvestsCount = computed(() => this.productService.getHarvestsByFarmer(this.farmer().id).length);

  changeFarmer(farmerId: string) {
    this.productService.setCurrentFarmer(farmerId);
  }
}
