import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CooperativeHeader } from '../cooperative-header/cooperative-header';
import { CooperativeService } from '../../../services/cooperative.service';
import { StatutBesoin } from '../../../models/cooperative.model';

@Component({
  selector: 'app-cooperative-besoins',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, CooperativeHeader],
  templateUrl: './cooperative-besoins.html',
  styleUrl: './cooperative-besoins.css'
})
export class CooperativeBesoins {
  coopService = inject(CooperativeService);
  besoins = this.coopService.besoins;

  filterStatut = signal<string>('TOUS');
  showCreateModal = signal<boolean>(false);

  newBesoin = {
    clientId: 104,
    clientNom: 'Centrale d\'Achat des Épiceries Fines',
    productId: 201,
    productNom: 'Huile d\'Olive Extra Vierge de Terroir',
    quantiteDemandee: 5000,
    dateSouhaitee: '2026-12-28',
    description: 'Bouteilles et bidons alimentaires pour réseau de distribution national.'
  };

  filteredBesoins() {
    const f = this.filterStatut();
    if (f === 'TOUS') return this.besoins();
    return this.besoins().filter(b => b.statut === f);
  }

  openCreateModal() {
    this.showCreateModal.set(true);
  }

  closeCreateModal() {
    this.showCreateModal.set(false);
  }

  submitNewBesoin() {
    this.coopService.createBesoin({
      clientId: Number(this.newBesoin.clientId),
      productId: Number(this.newBesoin.productId),
      productNom: this.newBesoin.productNom,
      quantiteDemandee: Number(this.newBesoin.quantiteDemandee),
      dateSouhaitee: this.newBesoin.dateSouhaitee,
      description: this.newBesoin.description
    });
    this.closeCreateModal();
  }

  lancerCollecte(besoin: any) {
    this.coopService.createAnnonce({
      besoinId: besoin.id,
      quantiteObjectif: besoin.quantiteDemandee,
      dateLimite: besoin.dateSouhaitee,
      description: `Collecte groupée pour ${besoin.productNom}`,
      besoinInfo: {
        produitNom: besoin.productNom,
        clientNom: besoin.clientNom,
        quantiteInitiale: besoin.quantiteDemandee
      }
    });
    alert(`Campagne de collecte créée avec succès pour ${besoin.productNom} !`);
  }

  annuler(id: number) {
    if (confirm('Voulez-vous vraiment annuler ce besoin client ?')) {
      this.coopService.annulerBesoin(id);
    }
  }
}
