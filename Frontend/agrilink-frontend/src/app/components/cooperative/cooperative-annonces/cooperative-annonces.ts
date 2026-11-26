import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CooperativeHeader } from '../cooperative-header/cooperative-header';
import { CooperativeService } from '../../../services/cooperative.service';
import { StatutAnnonce } from '../../../models/cooperative.model';

@Component({
  selector: 'app-cooperative-annonces',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, CooperativeHeader],
  templateUrl: './cooperative-annonces.html',
  styleUrl: './cooperative-annonces.css'
})
export class CooperativeAnnonces {
  coopService = inject(CooperativeService);
  annonces = this.coopService.activeAnnonces;
  besoins = this.coopService.besoins;

  filterStatut = signal<string>('TOUTES');
  showCreateModal = signal<boolean>(false);

  // Formulaire nouvelle annonce
  newAnnonce = {
    besoinId: 1,
    quantiteObjectif: 10000,
    dateLimite: '2026-12-30',
    description: 'Campagne de collecte groupée auprès des producteurs locaux certifiés.'
  };

  filteredAnnonces() {
    const filter = this.filterStatut();
    if (filter === 'TOUTES') return this.annonces();
    return this.annonces().filter(a => a.statut === filter);
  }

  getPercentage(acceptee: number, objectif: number): number {
    if (!objectif || objectif <= 0) return 0;
    return Math.min(Math.round((acceptee / objectif) * 100), 100);
  }

  openCreateModal() {
    // Si des besoins non pris en charge existent, présélectionner le premier
    const nonTraites = this.besoins().filter(b => b.statut === 'SOUMIS');
    if (nonTraites.length > 0) {
      this.newAnnonce.besoinId = nonTraites[0].id!;
      this.newAnnonce.quantiteObjectif = nonTraites[0].quantiteDemandee;
      this.newAnnonce.dateLimite = nonTraites[0].dateSouhaitee;
    }
    this.showCreateModal.set(true);
  }

  closeCreateModal() {
    this.showCreateModal.set(false);
  }

  submitNewAnnonce() {
    const selectedBesoin = this.besoins().find(b => b.id === Number(this.newAnnonce.besoinId));
    this.coopService.createAnnonce({
      besoinId: Number(this.newAnnonce.besoinId),
      quantiteObjectif: Number(this.newAnnonce.quantiteObjectif),
      dateLimite: this.newAnnonce.dateLimite,
      description: this.newAnnonce.description,
      besoinInfo: selectedBesoin ? {
        produitNom: selectedBesoin.productNom || 'Produit Agricole',
        clientNom: selectedBesoin.clientNom || 'Client B2B',
        quantiteInitiale: selectedBesoin.quantiteDemandee
      } : undefined
    });
    this.closeCreateModal();
  }

  cloturer(id: number) {
    if (confirm('Êtes-vous sûr de vouloir clôturer cette campagne de collecte ?')) {
      this.coopService.cloturerAnnonce(id);
    }
  }
}
