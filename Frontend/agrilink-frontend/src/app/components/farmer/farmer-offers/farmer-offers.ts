import { Component, computed, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ProductService } from '../../../services/product.service';
import { Product, FarmerOffer } from '../../../models/product.model';
import { FarmerHeader } from '../farmer-header/farmer-header';

@Component({
  selector: 'app-farmer-offers',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, FarmerHeader],
  templateUrl: './farmer-offers.html',
  styleUrl: './farmer-offers.css'
})
export class FarmerOffers {
  private productService = inject(ProductService);

  farmer = computed(() => this.productService.getCurrentFarmer());
  allProducts = computed(() => this.productService.getProducts());
  farmerOffers = computed(() => this.productService.getOffersByFarmer(this.farmer().id));

  // Filters
  searchTerm = signal<string>('');
  statusFilter = signal<string>('Tous');

  // Toast
  toastMessage = signal<string>('');

  // Modal State
  showModal = signal<boolean>(false);
  modalMode = signal<'create' | 'edit'>('create');
  editingOfferId = signal<string | null>(null);
  editingProductId = signal<string | null>(null);

  // Choice: Existing product vs Create new product
  productMode = signal<'existing' | 'new'>('existing');

  // Existing product selector
  selectedProductId = signal<string>('');

  // New product fields
  newProdName = signal<string>('');
  newProdCategory = signal<'Fruits' | 'Legumes' | 'Herbes' | 'Epicerie'>('Fruits');
  newProdUnit = signal<string>('Kg');
  newProdImage = signal<string>('https://images.unsplash.com/photo-1540420773420-3366772f4999?w=600&q=80');
  newProdIsBio = signal<boolean>(true);
  newProdIsNonTraite = signal<boolean>(true);
  newProdDesc = signal<string>('');

  // Offer fields
  offerPrice = signal<number>(2.5);
  offerOriginalPrice = signal<number | null>(null);
  offerStock = signal<number>(100);
  offerMinQty = signal<number>(1);
  offerHarvestDate = signal<string>('Récolté ce matin à l\'aube');
  offerDeliveryTime = signal<string>('Livraison en 24h');
  offerCertification = signal<string>('100% Bio Certifié Ecocert');
  offerFarmerNote = signal<string>('');
  offerStatus = signal<'Actif' | 'En rupture' | 'Brouillon'>('Actif');

  // Quick image presets
  imagePresets = [
    { label: 'Oranges / Agrumes', url: 'https://images.unsplash.com/photo-1547514701-42782101795e?w=600&q=80' },
    { label: 'Avocats', url: 'https://images.unsplash.com/photo-1523049673857-eb18f1d7b578?w=600&q=80' },
    { label: 'Tomates fermières', url: 'https://images.unsplash.com/photo-1592924357228-91a4daadcfea?w=600&q=80' },
    { label: 'Fraises fraîches', url: 'https://images.unsplash.com/photo-1543528176-61b239494933?w=600&q=80' },
    { label: 'Huile d\'Olive', url: 'https://images.unsplash.com/photo-1474979266404-7eaacbcd87c5?w=600&q=80' },
    { label: 'Grenades', url: 'https://images.unsplash.com/photo-1615485290382-441e4d049cb5?w=600&q=80' }
  ];

  filteredOffers = computed(() => {
    const term = this.searchTerm().trim().toLowerCase();
    const stat = this.statusFilter();
    let list = this.farmerOffers();

    if (term) {
      list = list.filter(item =>
        item.product.name.toLowerCase().includes(term) ||
        item.offer.certification.toLowerCase().includes(term)
      );
    }

    if (stat !== 'Tous') {
      list = list.filter(item => (item.offer.status || 'Actif') === stat);
    }

    return list;
  });

  openCreateModal() {
    this.modalMode.set('create');
    this.editingOfferId.set(null);
    this.editingProductId.set(null);
    this.productMode.set('existing');
    const prods = this.allProducts();
    this.selectedProductId.set(prods.length > 0 ? prods[0].id : '');
    this.newProdName.set('');
    this.newProdDesc.set('');
    this.offerPrice.set(2.5);
    this.offerOriginalPrice.set(null);
    this.offerStock.set(150);
    this.offerMinQty.set(1);
    this.offerHarvestDate.set('Récolté ce matin à l\'aube');
    this.offerDeliveryTime.set('Livraison en 24h');
    this.offerCertification.set('100% Bio Certifié Ecocert');
    this.offerFarmerNote.set(`Fruits mûris au soleil de ${this.farmer().location}, cueillis à la main sans intermédiaire.`);
    this.offerStatus.set('Actif');
    this.showModal.set(true);
  }

  openEditModal(item: { product: Product; offer: FarmerOffer }) {
    this.modalMode.set('edit');
    this.editingOfferId.set(item.offer.id);
    this.editingProductId.set(item.product.id);
    this.productMode.set('existing');
    this.selectedProductId.set(item.product.id);
    this.offerPrice.set(item.offer.price);
    this.offerOriginalPrice.set(item.offer.originalPrice || null);
    this.offerStock.set(item.offer.availableStock);
    this.offerMinQty.set(item.offer.minOrderQuantity || 1);
    this.offerHarvestDate.set(item.offer.harvestDate);
    this.offerDeliveryTime.set(item.offer.deliveryTime);
    this.offerCertification.set(item.offer.certification);
    this.offerFarmerNote.set(item.offer.farmerNote);
    this.offerStatus.set(item.offer.status || 'Actif');
    this.showModal.set(true);
  }

  saveOffer() {
    if (this.modalMode() === 'edit') {
      const pId = this.editingProductId();
      const oId = this.editingOfferId();
      if (pId && oId) {
        this.productService.updateOffer(pId, oId, {
          price: this.offerPrice(),
          originalPrice: this.offerOriginalPrice() || undefined,
          availableStock: this.offerStock(),
          minOrderQuantity: this.offerMinQty(),
          harvestDate: this.offerHarvestDate(),
          deliveryTime: this.offerDeliveryTime(),
          certification: this.offerCertification(),
          farmerNote: this.offerFarmerNote(),
          status: this.offerStatus()
        });
        this.showToast('✅ Offre mise à jour avec succès !');
        this.showModal.set(false);
      }
      return;
    }

    // CREATE MODE:
    if (this.productMode() === 'new') {
      if (!this.newProdName().trim()) {
        alert('Veuillez renseigner le nom du nouveau produit.');
        return;
      }

      this.productService.createProductWithOffer(
        {
          name: this.newProdName().trim(),
          category: this.newProdCategory(),
          unit: this.newProdUnit(),
          image: this.newProdImage(),
          isBio: this.newProdIsBio(),
          isNonTraite: this.newProdIsNonTraite(),
          shortDescription: this.newProdDesc() || `Produit frais cultivé à ${this.farmer().location}.`,
          origin: this.farmer().location
        },
        {
          price: this.offerPrice(),
          originalPrice: this.offerOriginalPrice() || undefined,
          availableStock: this.offerStock(),
          minOrderQuantity: this.offerMinQty(),
          harvestDate: this.offerHarvestDate(),
          certification: this.offerCertification(),
          deliveryTime: this.offerDeliveryTime(),
          farmerNote: this.offerFarmerNote()
        }
      );

      this.showToast(`🌾 Nouveau produit "${this.newProdName()}" créé et offre publiée sur la Marketplace !`);
    } else {
      const pId = this.selectedProductId();
      if (!pId) {
        alert('Veuillez sélectionner un produit existant.');
        return;
      }

      const farmer = this.farmer();
      const newOffer: FarmerOffer = {
        id: 'off-' + Date.now(),
        farmerId: farmer.id,
        farmerName: farmer.name,
        farmName: farmer.farmName,
        farmerAvatar: farmer.avatar,
        location: farmer.location,
        region: farmer.region,
        distanceKm: 20,
        rating: farmer.rating,
        reviewsCount: farmer.reviewsCount,
        price: this.offerPrice(),
        originalPrice: this.offerOriginalPrice() || undefined,
        unit: 'Kg',
        minOrderQuantity: this.offerMinQty(),
        availableStock: this.offerStock(),
        harvestDate: this.offerHarvestDate(),
        certification: this.offerCertification(),
        deliveryTime: this.offerDeliveryTime(),
        farmerNote: this.offerFarmerNote(),
        isBestPrice: true,
        status: this.offerStatus()
      };

      this.productService.addOfferToProduct(pId, newOffer);
      this.showToast('✅ Nouvelle offre ajoutée avec succès au produit !');
    }

    this.showModal.set(false);
  }

  deleteOffer(productId: string, offerId: string) {
    if (confirm('Voulez-vous retirer cette offre de la Marketplace ?')) {
      this.productService.deleteOffer(productId, offerId);
      this.showToast('Offre retirée de la Marketplace.');
    }
  }

  private showToast(msg: string) {
    this.toastMessage.set(msg);
    setTimeout(() => this.toastMessage.set(''), 4500);
  }
}
