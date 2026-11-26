import { Component, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { OrderResponse, OrderStatus } from '../../models/order.model';
import { CustomerContextService } from '../../services/customer-context.service';
import { OrderApiService } from '../../services/order-api.service';
import { ORDER_STATUS_LABELS, orderDate, orderMoney } from './order-view';

@Component({
  selector: 'app-order-history',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './order-history.html',
  styleUrl: './order-history.css',
})
export class OrderHistory {
  private readonly api = inject(OrderApiService);
  readonly customer = inject(CustomerContextService);
  readonly orders = signal<OrderResponse[]>([]);
  readonly loading = signal(true);
  readonly error = signal('');
  readonly filter = signal<OrderStatus | 'ALL'>('ALL');
  readonly statuses = Object.entries(ORDER_STATUS_LABELS) as [OrderStatus, string][];
  readonly statusLabels = ORDER_STATUS_LABELS;
  readonly date = orderDate;
  readonly money = orderMoney;
  readonly visibleOrders = computed(() => this.orders().filter(order =>
    this.filter() === 'ALL' || order.statut === this.filter()));
  readonly pendingCount = computed(() => this.orders().filter(order => order.statut === 'PENDING').length);
  readonly deliveredCount = computed(() => this.orders().filter(order => order.statut === 'DELIVERED').length);

  constructor() { this.refresh(); }

  refresh(): void {
    if (this.loading() && this.orders().length) return;
    this.loading.set(true);
    this.error.set('');
    if (!this.customer.developmentMode) {
      this.error.set('Identité client indisponible. Connectez-vous pour consulter vos commandes.');
      this.loading.set(false);
      return;
    }
    this.api.getCustomerOrders().subscribe({
      next: orders => {
        this.orders.set([...orders].sort((a, b) =>
          b.dateCommande.localeCompare(a.dateCommande) || b.id - a.id));
        this.loading.set(false);
      },
      error: error => { this.error.set(error.message); this.loading.set(false); },
    });
  }

  changeFilter(value: string): void {
    if (value === 'ALL' || value in ORDER_STATUS_LABELS) this.filter.set(value as OrderStatus | 'ALL');
  }
}
