import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { CooperativeHeader } from '../cooperative-header/cooperative-header';
import { CooperativeService } from '../../../services/cooperative.service';

@Component({
  selector: 'app-cooperative-overview',
  standalone: true,
  imports: [CommonModule, RouterModule, CooperativeHeader],
  templateUrl: './cooperative-overview.html',
  styleUrl: './cooperative-overview.css'
})
export class CooperativeOverview {
  coopService = inject(CooperativeService);

  stats = this.coopService.kpiStats;
  annonces = this.coopService.activeAnnonces;
  besoins = this.coopService.besoins;
  propositions = this.coopService.activePropositions;

  getPercentage(acceptee: number, objectif: number): number {
    if (!objectif || objectif <= 0) return 0;
    const pct = Math.round((acceptee / objectif) * 100);
    return Math.min(pct, 100);
  }
}
