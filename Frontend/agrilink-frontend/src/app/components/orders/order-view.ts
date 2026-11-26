import type { OrderStatus } from '../../models/order.model';

export const ORDER_STATUS_LABELS: Record<OrderStatus, string> = {
  PENDING: 'En attente',
  CONFIRMED: 'Confirmée',
  IN_PREPARATION: 'En préparation',
  SHIPPED: 'Expédiée',
  DELIVERED: 'Livrée',
  CANCELLED: 'Annulée',
};

export const DELIVERY_STAGES = [
  'PENDING', 'CONFIRMED', 'IN_PREPARATION', 'SHIPPED', 'DELIVERED',
] as const;

export function canConfirmOrder(status: OrderStatus): boolean { return status === 'PENDING'; }
export function canCancelOrder(status: OrderStatus): boolean { return status === 'PENDING' || status === 'CONFIRMED'; }
export function orderStageIndex(status: OrderStatus): number {
  return status === 'CANCELLED' ? -1 : DELIVERY_STAGES.indexOf(status);
}

export function orderDate(value: string): string {
  const date = new Date(value);
  return Number.isNaN(date.getTime()) ? 'Date indisponible' :
    new Intl.DateTimeFormat('fr-TN', { dateStyle: 'medium', timeStyle: 'short' }).format(date);
}

export function orderMoney(value: number): string {
  return Number.isFinite(value) ? value.toFixed(3) : '—';
}
