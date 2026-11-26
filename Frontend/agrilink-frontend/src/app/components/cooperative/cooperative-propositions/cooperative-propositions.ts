import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CooperativeHeader } from '../cooperative-header/cooperative-header';
import { CooperativeService } from '../../../services/cooperative.service';
import { StatutProposition } from '../../../models/cooperative.model';

@Component({
  selector: 'app-cooperative-propositions',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, CooperativeHeader],
  templateUrl: './cooperative-propositions.html',
  styleUrl: './cooperative-propositions.css'
})
export class CooperativePropositions {
  coopService = inject(CooperativeService);
  propositions = this.coopService.activePropositions;
  annonces = this.coopService.activeAnnonces;

  filterStatut = signal<string>('TOUTES');
  showProposalModal = signal<boolean>(false);
  showReceptionModal = signal<boolean>(false);
  selectedPropForReception: any = null;
  receptionQuantiteKg = signal<number>(0);

  // Formulaire nouvelle proposition agriculteur
  newProp = {
    annonceId: 1,
    agriculteurId: 10,
    agriculteurNom: 'Haj Moncef Jouini',
    recolteId: 301,
    quantiteProposee: 3000,
    commentaire: 'Lot de première qualité disponible immédiatement pour expédition au dépôt.'
  };

  filteredPropositions() {
    const f = this.filterStatut();
    if (f === 'TOUTES') return this.propositions();
    return this.propositions().filter(p => p.statut === f);
  }

  openProposalModal() {
    const ouvertes = this.annonces().filter(a => a.statut === 'OUVERTE');
    if (ouvertes.length > 0) {
      this.newProp.annonceId = ouvertes[0].id!;
    }
    this.showProposalModal.set(true);
  }

  closeProposalModal() {
    this.showProposalModal.set(false);
  }

  submitNewProposal() {
    this.coopService.submitProposition({
      annonceId: Number(this.newProp.annonceId),
      agriculteurId: Number(this.newProp.agriculteurId),
      agriculteurNom: this.newProp.agriculteurNom,
      recolteId: Number(this.newProp.recolteId),
      quantiteProposee: Number(this.newProp.quantiteProposee),
      commentaire: this.newProp.commentaire
    });
    this.closeProposalModal();
  }

  accepter(prop: any) {
    const qte = prompt(`Quantité à accepter pour ${prop.agriculteurNom} (Kg) :`, prop.quantiteProposee.toString());
    if (qte !== null && !isNaN(Number(qte)) && Number(qte) > 0) {
      this.coopService.traiterDecision(prop.id, 'ACCEPTEE', Number(qte));
    }
  }

  refuser(id: number) {
    if (confirm('Voulez-vous refuser cette proposition de récolte ?')) {
      this.coopService.traiterDecision(id, 'REFUSEE');
    }
  }

  openReceptionModal(prop: any) {
    this.selectedPropForReception = prop;
    this.receptionQuantiteKg.set(prop.quantiteAcceptee || prop.quantiteProposee);
    this.showReceptionModal.set(true);
  }

  closeReceptionModal() {
    this.showReceptionModal.set(false);
    this.selectedPropForReception = null;
  }

  enregistrerPesee() {
    if (!this.selectedPropForReception) return;
    const qte = Number(this.receptionQuantiteKg());
    this.coopService.enregistrerReception(this.selectedPropForReception.id, qte);
    this.closeReceptionModal();
    alert(`Pesée de ${qte.toLocaleString()} Kg enregistrée au dépôt ! Si le cumul atteint le besoin, le statut passera à SATISFAIT.`);
  }
}
