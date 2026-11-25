import { Component, computed, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ProductService } from '../../../services/product.service';
import { Harvest } from '../../../models/product.model';
import { FarmerHeader } from '../farmer-header/farmer-header';

@Component({
  selector: 'app-farmer-harvests',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, FarmerHeader],
  templateUrl: './farmer-harvests.html',
  styleUrl: './farmer-harvests.css'
})
export class FarmerHarvests {
  private productService = inject(ProductService);
  private router = inject(Router);

  farmer = computed(() => this.productService.getCurrentFarmer());
  allProducts = computed(() => this.productService.getProducts());
  farmerHarvests = computed(() => this.productService.getHarvestsByFarmer(this.farmer().id));

  statusFilter = signal<string>('Tous');
  toastMessage = signal<string>('');

  // Modal State
  showModal = signal<boolean>(false);
  modalMode = signal<'create' | 'edit'>('create');
  editingHarvestId = signal<string | null>(null);

  // Choice: Existing product vs Create new product
  productMode = signal<'existing' | 'new'>('existing');
  selectedProductId = signal<string>('');

  // New product fields
  newProdName = signal<string>('');
  newProdCategory = signal<'Fruits' | 'Legumes' | 'Herbes' | 'Epicerie'>('Fruits');
  newProdUnit = signal<string>('Kg');
  newProdImage = signal<string>('https://images.unsplash.com/photo-1592924357228-91a4daadcfea?w=600&q=80');
  newProdIsBio = signal<boolean>(true);
  newProdIsNonTraite = signal<boolean>(true);
  newProdDesc = signal<string>('');

  // Harvest fields
  harvestParcel = signal<string>('Verger Sud - Parcelle des Agrumes');
  harvestDate = signal<string>(new Date().toISOString().split('T')[0]);
  harvestQty = signal<number>(250);
  harvestGrade = signal<string>('Extra - Premier Choix');
  harvestMethod = signal<string>('Cueillette manuelle à pleine maturité');
  harvestStatus = signal<'Planifiée' | 'En cours' | 'Récoltée' | 'Commercialisée'>('Récoltée');
  harvestNotes = signal<string>('');
  harvestSyncWithStock = signal<boolean>(true);

  filteredHarvests = computed(() => {
    const status = this.statusFilter();
    const list = this.farmerHarvests();
    if (status === 'Tous') return list;
    return list.filter(h => h.status === status);
  });

  openCreateModal() {
    this.modalMode.set('create');
    this.editingHarvestId.set(null);
    this.productMode.set('existing');
    const prods = this.allProducts();
    this.selectedProductId.set(prods.length > 0 ? prods[0].id : '');
    this.newProdName.set('');
    this.newProdDesc.set('');
    this.harvestParcel.set('Verger Sud - Parcelle des Agrumes');
    this.harvestDate.set(new Date().toISOString().split('T')[0]);
    this.harvestQty.set(300);
    this.harvestGrade.set('Extra - Premier Choix');
    this.harvestMethod.set('Cueillette manuelle sélective');
    this.harvestStatus.set('Récoltée');
    this.harvestNotes.set('Excellente floraison et maturité optimale.');
    this.harvestSyncWithStock.set(true);
    this.showModal.set(true);
  }

  openEditModal(harvest: Harvest) {
    this.modalMode.set('edit');
    this.editingHarvestId.set(harvest.id);
    this.productMode.set('existing');
    this.selectedProductId.set(harvest.productId);
    this.harvestParcel.set(harvest.parcelName);
    this.harvestDate.set(harvest.harvestDate);
    this.harvestQty.set(harvest.quantity);
    this.harvestGrade.set(harvest.qualityGrade);
    this.harvestMethod.set(harvest.method);
    this.harvestStatus.set(harvest.status);
    this.harvestNotes.set(harvest.notes || '');
    this.harvestSyncWithStock.set(harvest.syncedWithStock || false);
    this.showModal.set(true);
  }

  saveHarvest() {
    if (this.modalMode() === 'edit') {
      const hId = this.editingHarvestId();
      if (hId) {
        this.productService.updateHarvest(hId, {
          parcelName: this.harvestParcel(),
          harvestDate: this.harvestDate(),
          quantity: this.harvestQty(),
          qualityGrade: this.harvestGrade(),
          method: this.harvestMethod(),
          status: this.harvestStatus(),
          notes: this.harvestNotes(),
          syncedWithStock: this.harvestSyncWithStock()
        });
        this.showToast('✅ Récolte mise à jour avec succès !');
        this.showModal.set(false);
      }
      return;
    }

    // CREATE MODE:
    if (this.productMode() === 'new') {
      if (!this.newProdName().trim()) {
        alert('Veuillez renseigner le nom du nouveau produit pour cette récolte.');
        return;
      }

      this.productService.createProductWithHarvest(
        {
          name: this.newProdName().trim(),
          category: this.newProdCategory(),
          unit: this.newProdUnit(),
          image: this.newProdImage(),
          isBio: this.newProdIsBio(),
          isNonTraite: this.newProdIsNonTraite(),
          shortDescription: this.newProdDesc() || `Produit cultivé sur nos parcelles à ${this.farmer().location}.`,
          origin: this.farmer().location
        },
        {
          parcelName: this.harvestParcel(),
          harvestDate: this.harvestDate(),
          quantity: this.harvestQty(),
          qualityGrade: this.harvestGrade(),
          method: this.harvestMethod(),
          status: this.harvestStatus(),
          notes: this.harvestNotes()
        }
      );

      this.showToast(`🌱 Nouveau produit "${this.newProdName()}" créé et affecté à la récolte !`);
    } else {
      const pId = this.selectedProductId();
      const prod = this.productService.getProductById(pId);
      if (!prod) {
        alert('Veuillez sélectionner un produit existant.');
        return;
      }

      const farmer = this.farmer();
      const newHarvest: Harvest = {
        id: 'harv-' + Date.now(),
        farmerId: farmer.id,
        productId: prod.id,
        productName: prod.name,
        productImage: prod.image,
        productCategory: prod.category,
        parcelName: this.harvestParcel(),
        harvestDate: this.harvestDate(),
        quantity: this.harvestQty(),
        unit: prod.unit.replace(/[()]/g, '').trim() || 'Kg',
        qualityGrade: this.harvestGrade(),
        method: this.harvestMethod(),
        status: this.harvestStatus(),
        notes: this.harvestNotes(),
        syncedWithStock: this.harvestSyncWithStock()
      };

      this.productService.addHarvest(newHarvest);

      if (this.harvestSyncWithStock() && (newHarvest.status === 'Récoltée' || newHarvest.status === 'Commercialisée')) {
        const existingOffer = prod.offers.find(o => o.farmerId === farmer.id);
        if (existingOffer) {
          this.productService.updateOffer(prod.id, existingOffer.id, {
            availableStock: existingOffer.availableStock + newHarvest.quantity,
            harvestDate: `Récolté le ${newHarvest.harvestDate}`
          });
        }
      }

      this.showToast(`🌾 Récolte enregistrée pour "${prod.name}" !`);
    }

    this.showModal.set(false);
  }

  deleteHarvest(harvestId: string) {
    if (confirm('Voulez-vous supprimer cette fiche de récolte ?')) {
      this.productService.deleteHarvest(harvestId);
      this.showToast('Récolte supprimée.');
    }
  }

  // Quick Action: Convert harvest to offer by navigating to offers with query params or service
  convertHarvestToOffer(harvest: Harvest) {
    this.router.navigate(['/espace-agriculteur/offres']);
  }

  private showToast(msg: string) {
    this.toastMessage.set(msg);
    setTimeout(() => this.toastMessage.set(''), 4500);
  }
}
