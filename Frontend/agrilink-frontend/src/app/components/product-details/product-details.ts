import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ProductService } from '../../services/product.service';
import { Product, FarmerOffer } from '../../models/product.model';

@Component({
  selector: 'app-product-details',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './product-details.html',
  styleUrl: './product-details.css'
})
export class ProductDetails implements OnInit {
  private route = inject(ActivatedRoute);
  private productService = inject(ProductService);

  product = signal<Product | null>(null);
  selectedImage = signal<string>('');
  selectedOffer = signal<FarmerOffer | null>(null);
  quantity = signal<number>(1);
  activeTab = signal<'description' | 'farmers' | 'additional' | 'reviews'>('farmers');
  addedToCartMessage = signal<string>('');
  isFavorite = signal<boolean>(false);

  relatedProducts = computed(() => {
    const current = this.product();
    return current ? this.productService.getRelatedProducts(current.id, 4) : [];
  });

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      const id = params.get('id') || 'orange-maltaise-succulente';
      const prod = this.productService.getProductById(id) || this.productService.getProductById('orange-maltaise-succulente');
      if (prod) {
        this.product.set(prod);
        this.selectedImage.set(prod.image);
        // Default select first offer (often the best price or primary farmer)
        if (prod.offers && prod.offers.length > 0) {
          const best = prod.offers.find(o => o.isBestPrice) || prod.offers[0];
          this.selectedOffer.set(best);
        }
      }
      window.scrollTo({ top: 0, behavior: 'smooth' });
    });
  }

  setImage(img: string) {
    this.selectedImage.set(img);
  }

  selectFarmerOffer(offer: FarmerOffer) {
    this.selectedOffer.set(offer);
    // Smooth visual feedback
    this.showMessage(`Offre sélectionnée : ${offer.farmerName} (${offer.farmName}) - ${offer.price.toFixed(3)} DT/${offer.unit}`);
  }

  incrementQty() {
    this.quantity.update(q => q + 1);
  }

  decrementQty() {
    this.quantity.update(q => (q > 1 ? q - 1 : 1));
  }

  setTab(tab: 'description' | 'farmers' | 'additional' | 'reviews') {
    this.activeTab.set(tab);
  }

  toggleFavorite() {
    this.isFavorite.update(v => !v);
  }

  addToCart() {
    const offer = this.selectedOffer();
    const prod = this.product();
    if (offer && prod) {
      this.showMessage('Cette offre marketplace ne peut pas encore être commandée via MS3. Le panier de développement est accessible depuis la navigation.');
    }
  }

  private showMessage(msg: string) {
    this.addedToCartMessage.set(msg);
    setTimeout(() => {
      this.addedToCartMessage.set('');
    }, 4000);
  }
}
