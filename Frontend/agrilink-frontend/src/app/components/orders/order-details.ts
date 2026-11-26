import { Component, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { OrderResponse } from '../../models/order.model';
import { CustomerContextService } from '../../services/customer-context.service';
import { OrderApiService } from '../../services/order-api.service';
import { DELIVERY_STAGES, ORDER_STATUS_LABELS, canCancelOrder, canConfirmOrder, orderDate, orderMoney, orderStageIndex } from './order-view';

@Component({
  selector: 'app-order-details',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './order-details.html',
  styleUrl: './order-details.css',
})
export class OrderDetails {
  private readonly route = inject(ActivatedRoute);
  private readonly api = inject(OrderApiService);
  readonly customer = inject(CustomerContextService);
  readonly order = signal<OrderResponse | null>(null);
  readonly productNames = signal<Record<number, string>>({});
  readonly loading = signal(true);
  readonly pending = signal(false);
  readonly error = signal('');
  readonly actionError = signal('');
  readonly stages = DELIVERY_STAGES;
  readonly labels = ORDER_STATUS_LABELS;
  readonly date = orderDate;
  readonly money = orderMoney;
  readonly canConfirm = canConfirmOrder;
  readonly canCancel = canCancelOrder;

  constructor() {
    this.route.paramMap.pipe(takeUntilDestroyed()).subscribe(params => {
      const raw = params.get('id');
      const id = raw && /^\d+$/.test(raw) ? Number(raw) : NaN;
      if (!Number.isSafeInteger(id) || id <= 0) {
        this.order.set(null);
        this.error.set('Numéro de commande invalide.');
        this.loading.set(false);
      } else {
        this.order.set(null);
        this.load(id);
      }
    });
    if (this.customer.developmentMode) {
      this.api.getDevelopmentProducts().subscribe({
        next: products => this.productNames.set(Object.fromEntries(products.map(p => [p.id, p.name]))),
        error: () => this.productNames.set({}),
      });
    }
  }

  productName(id: number): string { return this.productNames()[id] ?? `Produit n° ${id}`; }
  stageIndex(order: OrderResponse): number { return orderStageIndex(order.statut); }

  refresh(): void {
    const id = this.order()?.id;
    if (id && !this.pending()) this.load(id);
  }

  private load(id: number): void {
    this.loading.set(true);
    this.error.set('');
    this.actionError.set('');
    if (!this.customer.developmentMode) {
      this.error.set('Identité client indisponible. Connectez-vous pour consulter vos commandes.');
      this.loading.set(false);
      return;
    }
    this.api.getOrder(id).subscribe({
      next: order => { this.order.set(order); this.loading.set(false); },
      error: error => {
        this.error.set(/^Order \d+ not found/.test(error.message) ? 'Commande introuvable.' : error.message);
        this.loading.set(false);
      },
    });
  }

  confirm(): void {
    const current = this.order();
    if (!current || !canConfirmOrder(current.statut) || this.pending()) return;
    this.pending.set(true);
    this.actionError.set('');
    this.api.confirmOrder(current.id).subscribe({
      next: order => { this.order.set(order); this.pending.set(false); },
      error: error => { this.actionError.set(error.message); this.pending.set(false); },
    });
  }

  cancel(): void {
    const current = this.order();
    if (!current || !canCancelOrder(current.statut) || this.pending()) return;
    if (!window.confirm(`Annuler la commande n° ${current.id} ?`)) return;
    this.pending.set(true);
    this.actionError.set('');
    this.api.cancelOrder(current.id).subscribe({
      next: order => { this.order.set(order); this.pending.set(false); },
      error: error => { this.actionError.set(error.message); this.pending.set(false); },
    });
  }
}
