export interface FarmerOffer {
  id: string;
  farmerId: string;
  farmerName: string;
  farmName: string;
  farmerAvatar: string;
  location: string;
  region: string;
  distanceKm: number;
  rating: number;
  reviewsCount: number;
  price: number; // En DT
  originalPrice?: number;
  unit: string;
  minOrderQuantity: number;
  availableStock: number;
  harvestDate: string;
  certification: string;
  deliveryTime: string;
  farmerNote: string;
  isBestPrice?: boolean;
  isEcoFriendly?: boolean;
  isLocalFavorite?: boolean;
  phone?: string;
}

export interface Product {
  id: string;
  name: string;
  frenchName: string;
  category: 'Fruits' | 'Legumes' | 'Herbes' | 'Epicerie';
  isBio: boolean;
  isNonTraite: boolean;
  unit: string; // Ex: "(1 Piece)", "(1 Kg)", "(500g)"
  image: string;
  gallery: string[];
  sku: string;
  tags: string[];
  shortDescription: string;
  fullDescription: string;
  additionalInfo: {
    origin: string;
    conservation: string;
    variety: string;
    packaging: string;
  };
  rating: number;
  reviewsCount: number;
  viewersCount: number;
  soldLastHours: number;
  offers: FarmerOffer[];
}
