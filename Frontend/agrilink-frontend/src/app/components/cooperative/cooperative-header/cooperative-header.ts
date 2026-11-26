import { Component, input, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { CooperativeService } from '../../../services/cooperative.service';

@Component({
  selector: 'app-cooperative-header',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './cooperative-header.html',
  styleUrl: './cooperative-header.css'
})
export class CooperativeHeader {
  pageTitle = input<string>('ESPACE COOPÉRATIVE');
  currentPage = input<string>("Vue d'Ensemble");

  coopService = inject(CooperativeService);
  coop = this.coopService.cooperativeProfile;
  allCooperatives = this.coopService.allCooperatives;
  stats = this.coopService.kpiStats;

  changeCooperative(id: any): void {
    this.coopService.setCooperative(Number(id));
  }
}
