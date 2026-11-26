export type OrderStatus = 'PENDING' | 'CONFIRMED' | 'IN_PREPARATION' | 'SHIPPED' | 'DELIVERED' | 'CANCELLED';

export interface OrderItemResponse {
  id: number;
  produitId: number;
  quantite: number;
  prixUnitaire: number;
  sousTotal: number;
}

export interface OrderResponse {
  id: number;
  clientId: number;
  dateCommande: string;
  montantTotal: number;
  statut: OrderStatus;
  lignes: OrderItemResponse[];
}

export interface OrderRequest {
  lignes: { produitId: number; quantite: number }[];
}

export interface DevProduct {
  id: number;
  name: string;
  unitPrice: number;
  availableQuantity: number;
}
