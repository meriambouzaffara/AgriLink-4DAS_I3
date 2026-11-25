import { Component, computed, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ProductService } from '../../../services/product.service';
import { Product, FarmerOffer, Harvest } from '../../../models/product.model';
import { FarmerHeader } from '../farmer-header/farmer-header';

@Component({
  selector: 'app-farmer-create-product',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, FarmerHeader],
  templateUrl: './farmer-create-product.html',
  styleUrl: './farmer-create-product.css'
})
export class FarmerCreateProduct {
  private productService = inject(ProductService);
  private router = inject(Router);

  farmer = computed(() => this.productService.getCurrentFarmer());

  toastMessage = signal<string>('');

  // Product Fields
  prodName = signal<string>('');
  prodCategory = signal<'Fruits' | 'Legumes' | 'Herbes' | 'Epicerie'>('Fruits');
  prodVariety = signal<string>('');
  prodUnit = signal<string>('Kg');
  prodImage = signal<string>('https://images.unsplash.com/photo-1547514701-42782101795e?w=600&q=80');
  prodIsBio = signal<boolean>(true);
  prodIsNonTraite = signal<boolean>(true);
  prodOrigin = signal<string>('');
  prodConservation = signal<string>('Conserver dans un endroit frais et aéré');
  prodDesc = signal<string>('');

  // Assignment Toggles
  attachOffer = signal<boolean>(true);
  attachHarvest = signal<boolean>(true);

  // Offer Fields
  offerPrice = signal<number>(2.8);
  offerOriginalPrice = signal<number | null>(null);
  offerStock = signal<number>(150);
  offerMinQty = signal<number>(1);
  offerHarvestDate = signal<string>('Récolté ce matin à l\'aube');
  offerDeliveryTime = signal<string>('Livraison en 24h');
  offerCertification = signal<string>('100% Bio Certifié Ecocert');
  offerFarmerNote = signal<string>('');

  // Harvest Fields
  harvestParcel = signal<string>('Verger Sud - Parcelle des Terres Fertiles');
  harvestDate = signal<string>(new Date().toISOString().split('T')[0]);
  harvestQty = signal<number>(300);
  harvestGrade = signal<string>('Extra - Premier Choix');
  harvestMethod = signal<string>('Cueillette manuelle sélective');
  harvestStatus = signal<'Planifiée' | 'En cours' | 'Récoltée' | 'Commercialisée'>('Récoltée');
  harvestNotes = signal<string>('');

  // Image Presets
  imagePresets = [
    { label: 'Oranges / Agrumes', url: 'https://images.unsplash.com/photo-1547514701-42782101795e?w=600&q=80' },
    { label: 'Avocats', url: 'https://images.unsplash.com/photo-1523049673857-eb18f1d7b578?w=600&q=80' },
    { label: 'Tomates fermières', url: 'https://images.unsplash.com/photo-1592924357228-91a4daadcfea?w=600&q=80' },
    { label: 'Fraises fraîches', url: 'https://images.unsplash.com/photo-1543528176-61b239494933?w=600&q=80' },
    { label: 'Huile d\'Olive', url: 'https://images.unsplash.com/photo-1474979266404-7eaacbcd87c5?w=600&q=80' },
    { label: 'Grenades', url: 'https://images.unsplash.com/photo-1615485290382-441e4d049cb5?w=600&q=80' },
    { label: 'Dattes du Sud', url: 'https://images.unsplash.com/photo-1546548970-71785318a17b?w=600&q=80' },
    { label: 'Miel de Thym', url: 'https://images.unsplash.com/photo-1587049352846-4a222e784d38?w=600&q=80' }
  ];

  constructor() {
    this.prodOrigin.set(this.farmer().location);
    this.offerFarmerNote.set(`Produit cultivé avec fierté à ${this.farmer().location} par ${this.farmer().farmName}. Fraîcheur garantie.`);
  }

  submitProduct() {
    if (!this.prodName().trim()) {
      alert('Veuillez renseigner le nom du nouveau produit.');
      return;
    }

    if (!this.attachOffer() && !this.attachHarvest()) {
      alert('Veuillez affecter au moins une offre de vente ou une récolte à ce nouveau produit.');
      return;
    }

    const farmer = this.farmer();
    const productId = 'prod-' + Date.now();

    const createdProduct: Product = {
      id: productId,
      name: this.prodName().trim(),
      frenchName: this.prodName().trim(),
      category: this.prodCategory(),
      isBio: this.prodIsBio(),
      isNonTraite: this.prodIsNonTraite(),
      unit: this.prodUnit().startsWith('(') ? this.prodUnit() : `(${this.prodUnit()})`,
      image: this.prodImage(),
      gallery: [this.prodImage()],
      sku: 'AGRI-' + Math.floor(1000 + Math.random() * 9000),
      tags: [this.prodCategory().toUpperCase(), 'TERROIR', 'NOUVEAU'],
      shortDescription: this.prodDesc() || `Produit frais et authentique issu du terroir de ${farmer.location}.`,
      fullDescription: this.prodDesc() || `Produit frais et authentique issu du terroir de ${farmer.location}.`,
      additionalInfo: {
        origin: this.prodOrigin() || farmer.location,
        conservation: this.prodConservation() || 'À conserver au frais',
        variety: this.prodVariety() || this.prodName(),
        packaging: 'Caisse fermière recyclable'
      },
      rating: 5.0,
      reviewsCount: 1,
      viewersCount: 3,
      soldLastHours: 0,
      offers: []
    };

    // 1. Attach offer if checked
    if (this.attachOffer()) {
      const offerId = 'off-' + Date.now();
      const newOffer: FarmerOffer = {
        id: offerId,
        farmerId: farmer.id,
        farmerName: farmer.name,
        farmName: farmer.farmName,
        farmerAvatar: farmer.avatar,
        location: farmer.location,
        region: farmer.region,
        distanceKm: 22,
        rating: farmer.rating,
        reviewsCount: farmer.reviewsCount,
        price: this.offerPrice(),
        originalPrice: this.offerOriginalPrice() || undefined,
        unit: this.prodUnit().replace(/[()]/g, '').trim(),
        minOrderQuantity: this.offerMinQty(),
        availableStock: this.offerStock(),
        harvestDate: this.offerHarvestDate(),
        certification: this.offerCertification(),
        deliveryTime: this.offerDeliveryTime(),
        farmerNote: this.offerFarmerNote(),
        isBestPrice: true,
        status: 'Actif'
      };
      createdProduct.offers.push(newOffer);
    }

    // Add product to catalogue
    this.productService.addProduct(createdProduct);

    // 2. Attach harvest if checked
    if (this.attachHarvest()) {
      const harvestId = 'harv-' + Date.now();
      const newHarvest: Harvest = {
        id: harvestId,
        farmerId: farmer.id,
        productId: createdProduct.id,
        productName: createdProduct.name,
        productImage: createdProduct.image,
        productCategory: createdProduct.category,
        parcelName: this.harvestParcel(),
        harvestDate: this.harvestDate(),
        quantity: this.harvestQty(),
        unit: createdProduct.unit.replace(/[()]/g, '').trim(),
        qualityGrade: this.harvestGrade(),
        method: this.harvestMethod(),
        status: this.harvestStatus(),
        notes: this.harvestNotes(),
        syncedWithStock: true
      };
      this.productService.addHarvest(newHarvest);
    }

    // Success notification & redirect
    this.toastMessage.set(`🎉 Produit "${createdProduct.name}" créé avec succès avec ses affectations !`);
    setTimeout(() => {
      if (this.attachOffer()) {
        this.router.navigate(['/espace-agriculteur/offres']);
      } else {
        this.router.navigate(['/espace-agriculteur/recoltes']);
      }
    }, 1200);
  }
}
