export type StatutBesoin = 'SOUMIS' | 'EN_COLLECTE' | 'SATISFAIT' | 'ANNULE';

export type StatutAnnonce = 'OUVERTE' | 'OBJECTIF_ATTEINT' | 'CLOTUREE' | 'ANNULEE';

export type StatutProposition = 
  | 'EN_ATTENTE' 
  | 'ACCEPTEE' 
  | 'ACCEPTEE_PARTIELLEMENT' 
  | 'REFUSEE' 
  | 'RECUE' 
  | 'RECUE_PARTIELLEMENT';

export interface BesoinClient {
  id?: number;
  clientId: number;
  clientNom?: string;
  clientEntreprise?: string;
  productId: number;
  productNom?: string;
  quantiteDemandee: number;
  dateSouhaitee: string;
  description?: string;
  statut: StatutBesoin;
  dateCreation?: string;
}

export interface AnnonceCollecte {
  id?: number;
  besoinId: number;
  besoinInfo?: {
    produitNom: string;
    clientNom: string;
    quantiteInitiale: number;
  };
  responsableId: number;
  responsableNom?: string;
  cooperativeNom?: string;
  quantiteObjectif: number;
  quantiteAcceptee: number;
  datePublication?: string;
  dateLimite: string;
  description?: string;
  statut: StatutAnnonce;
  propositionsCount?: number;
}

export interface PropositionRecolte {
  id?: number;
  annonceId: number;
  annonceTitre?: string;
  agriculteurId: number;
  agriculteurNom?: string;
  agriculteurFerme?: string;
  recolteId: number;
  recolteNom?: string;
  quantiteProposee: number;
  quantiteAcceptee?: number;
  quantiteRecue?: number;
  dateProposition?: string;
  dateReception?: string;
  statut: StatutProposition;
  commentaire?: string;
}

export interface CooperativeProfile {
  id: number;
  nom: string;
  region: string;
  responsable: string;
  telephone: string;
  depotAdresse: string;
  capaciteStockageKg: number;
}
