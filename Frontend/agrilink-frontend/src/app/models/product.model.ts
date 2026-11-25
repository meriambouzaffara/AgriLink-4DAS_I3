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
  status?: 'Actif' | 'En rupture' | 'Brouillon';
}

export interface Harvest {
  id: string;
  farmerId: string;
  productId: string;
  productName: string;
  productImage: string;
  productCategory: string;
  parcelName: string; // Ex: Parcelle Est, Verger Vallée, Serre 2
  harvestDate: string;
  quantity: number;
  unit: string;
  qualityGrade: string; // Ex: Extra, Premier Choix, Calibre 1
  method: string; // Ex: Cueillette manuelle, À pleine maturité
  status: 'Planifiée' | 'En cours' | 'Récoltée' | 'Commercialisée';
  notes?: string;
  syncedWithStock?: boolean;
}

export interface FarmerProfile {
  id: string;
  name: string;
  farmName: string;
  avatar: string;
  location: string;
  region: string;
  phone: string;
  rating: number;
  reviewsCount: number;
  bio: string;
  memberSince: string;
  certifications: string[];
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
