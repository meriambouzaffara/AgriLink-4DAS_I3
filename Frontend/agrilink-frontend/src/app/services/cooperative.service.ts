import { Injectable, signal, computed, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { 
  BesoinClient, 
  AnnonceCollecte, 
  PropositionRecolte, 
  CooperativeProfile,
  StatutBesoin,
  StatutAnnonce,
  StatutProposition
} from '../models/cooperative.model';
import { catchError, of, tap } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class CooperativeService {
  private http = inject(HttpClient);
  private readonly baseUrl = 'http://localhost:8085/api';

  // Liste de toutes les coopératives disponibles sur le réseau
  allCooperatives = signal<CooperativeProfile[]>([
    {
      id: 501,
      nom: 'Coopérative Centrale du Cap Bon',
      region: 'Nabeul & Cap Bon',
      responsable: 'Si Mahmoud Ben Salem',
      telephone: '+216 72 250 800',
      depotAdresse: 'Dépôt Fédéral Route de Grombalia Km 4, Nabeul',
      capaciteStockageKg: 500000
    },
    {
      id: 502,
      nom: 'Coopérative Agricole de la Mejerda',
      region: 'Béja & Vallée de la Mejerda',
      responsable: 'Ing. Belgacem Trabelsi',
      telephone: '+216 78 450 120',
      depotAdresse: 'Silos & Dépôt Régional de Béja Nord',
      capaciteStockageKg: 850000
    },
    {
      id: 503,
      nom: 'Union Oléicole de Zaghouan',
      region: 'Zaghouan & Sahel Nord',
      responsable: 'Mme Salma Khemiri',
      telephone: '+216 72 410 330',
      depotAdresse: 'Centre de Collecte & Cuverie Fédérale de Zaghouan',
      capaciteStockageKg: 350000
    }
  ]);

  // Coopérative actuellement active
  currentCoopId = signal<number>(501);

  cooperativeProfile = computed(() => {
    return this.allCooperatives().find(c => c.id === this.currentCoopId()) || this.allCooperatives()[0];
  });

  setCooperative(id: number): void {
    this.currentCoopId.set(Number(id));
  }

  // Signal des besoins clients
  besoins = signal<BesoinClient[]>([
    {
      id: 1,
      clientId: 101,
      clientNom: 'Société Carthage Agro-Distribution',
      clientEntreprise: 'Grossiste Agroalimentaire Tunis',
      productId: 202,
      productNom: 'Pommes de Terre Spunta (Calibre 1)',
      quantiteDemandee: 15000,
      dateSouhaitee: '2026-12-31',
      description: 'Besoin urgent de 15 tonnes pour approvisionnement réseau de distribution centrale.',
      statut: 'EN_COLLECTE',
      dateCreation: '2026-11-20'
    },
    {
      id: 2,
      clientId: 102,
      clientNom: 'Restaurant Le Belvédère & Traiteur',
      clientEntreprise: 'Groupe Hôtelier Tunis',
      productId: 205,
      productNom: 'Oranges Maltaises de Tunisie Extra',
      quantiteDemandee: 3500,
      dateSouhaitee: '2026-12-15',
      description: 'Agrumes de table de première fraîcheur certifiés terroir pour réceptions festives.',
      statut: 'SOUMIS',
      dateCreation: '2026-11-23'
    },
    {
      id: 3,
      clientId: 103,
      clientNom: 'Conserverie Moderne du Nord',
      clientEntreprise: 'Industrie Agro Mateur',
      productId: 210,
      productNom: 'Tomates Rondes Plein Champ',
      quantiteDemandee: 25000,
      dateSouhaitee: '2026-12-25',
      description: 'Volume pour transformation en concentré pur terroirs.',
      statut: 'SATISFAIT',
      dateCreation: '2026-11-10'
    }
  ]);

  // Signal des annonces de collecte
  annonces = signal<AnnonceCollecte[]>([
    // === Coopérative Centrale du Cap Bon (501) ===
    {
      id: 1,
      besoinId: 1,
      besoinInfo: {
        produitNom: 'Pommes de Terre Spunta (Calibre 1)',
        clientNom: 'Société Carthage Agro-Distribution',
        quantiteInitiale: 15000
      },
      responsableId: 501,
      responsableNom: 'Si Mahmoud Ben Salem',
      cooperativeNom: 'Coopérative Centrale du Cap Bon',
      quantiteObjectif: 15000,
      quantiteAcceptee: 10500,
      datePublication: '2026-11-21',
      dateLimite: '2026-12-20',
      description: 'Campagne de collecte groupée pour approvisionner le marché central. Prix d\'achat coopératif garanti au dépôt.',
      statut: 'OUVERTE',
      propositionsCount: 3
    },
    {
      id: 2,
      besoinId: 3,
      besoinInfo: {
        produitNom: 'Tomates Rondes Plein Champ',
        clientNom: 'Conserverie Moderne du Nord',
        quantiteInitiale: 25000
      },
      responsableId: 501,
      responsableNom: 'Si Mahmoud Ben Salem',
      cooperativeNom: 'Coopérative Centrale du Cap Bon',
      quantiteObjectif: 25000,
      quantiteAcceptee: 25000,
      datePublication: '2026-11-12',
      dateLimite: '2026-11-24',
      description: 'Collecte industrielle terminée. Toutes les réceptions ont été effectuées avec succès.',
      statut: 'OBJECTIF_ATTEINT',
      propositionsCount: 2
    },

    // === Coopérative Agricole de la Mejerda (502) ===
    {
      id: 3,
      besoinId: 4,
      besoinInfo: {
        produitNom: 'Blé Dur Supérieur de la Mejerda (Variété Khiar)',
        clientNom: 'Minoterie Centrale du Nord',
        quantiteInitiale: 45000
      },
      responsableId: 502,
      responsableNom: 'Ing. Belgacem Trabelsi',
      cooperativeNom: 'Coopérative Agricole de la Mejerda',
      quantiteObjectif: 45000,
      quantiteAcceptee: 32000,
      datePublication: '2026-11-18',
      dateLimite: '2026-12-25',
      description: 'Collecte stratégique de blé dur de semence certifiée pour les silos fédéraux de Béja.',
      statut: 'OUVERTE',
      propositionsCount: 3
    },
    {
      id: 4,
      besoinId: 5,
      besoinInfo: {
        produitNom: 'Féveroles & Légumineuses Sèches',
        clientNom: 'Groupe Agro-Alimentaire Medjerda',
        quantiteInitiale: 18000
      },
      responsableId: 502,
      responsableNom: 'Ing. Belgacem Trabelsi',
      cooperativeNom: 'Coopérative Agricole de la Mejerda',
      quantiteObjectif: 18000,
      quantiteAcceptee: 18000,
      datePublication: '2026-11-10',
      dateLimite: '2026-11-23',
      description: 'Collecte de légumineuses de saison complétée à 100%.',
      statut: 'OBJECTIF_ATTEINT',
      propositionsCount: 2
    },

    // === Union Oléicole de Zaghouan (503) ===
    {
      id: 5,
      besoinId: 6,
      besoinInfo: {
        produitNom: 'Olives Chétoui pour Huile Extra Vierge AOP',
        clientNom: 'Société d\'Export Huiles Précieuses',
        quantiteInitiale: 28000
      },
      responsableId: 503,
      responsableNom: 'Mme Salma Khemiri',
      cooperativeNom: 'Union Oléicole de Zaghouan',
      quantiteObjectif: 28000,
      quantiteAcceptee: 16000,
      datePublication: '2026-11-20',
      dateLimite: '2026-12-31',
      description: 'Campagne oléicole de première pression à froid. Cueillette manuelle requise.',
      statut: 'OUVERTE',
      propositionsCount: 3
    },
    {
      id: 6,
      besoinId: 7,
      besoinInfo: {
        produitNom: 'Amandes Décortiquées Terroir Zriba',
        clientNom: 'Pâtisserie Traditionnelle Fine Tunis',
        quantiteInitiale: 8000
      },
      responsableId: 503,
      responsableNom: 'Mme Salma Khemiri',
      cooperativeNom: 'Union Oléicole de Zaghouan',
      quantiteObjectif: 8000,
      quantiteAcceptee: 4500,
      datePublication: '2026-11-15',
      dateLimite: '2026-12-15',
      description: 'Collecte d\'amandes douces locales séchées au soleil.',
      statut: 'OUVERTE',
      propositionsCount: 1
    }
  ]);

  // Signal des propositions de récolte
  propositions = signal<PropositionRecolte[]>([
    // Propositions pour Coop 501 (Cap Bon - Annonces 1 & 2)
    {
      id: 1,
      annonceId: 1,
      annonceTitre: 'Collecte Pommes de Terre Spunta (15 000 Kg)',
      agriculteurId: 10,
      agriculteurNom: 'Haj Moncef Jouini',
      agriculteurFerme: 'Ferme Les Orangers Royaux',
      recolteId: 301,
      recolteNom: 'Parcelle Nord - Spunta Récolte Novembre',
      quantiteProposee: 6000,
      quantiteAcceptee: 6000,
      quantiteRecue: 6000,
      dateProposition: '2026-11-22T10:30:00',
      dateReception: '2026-11-24T14:15:00',
      statut: 'RECUE',
      commentaire: 'Qualité excellente vérifiée au pont-bascule du dépôt.'
    },
    {
      id: 2,
      annonceId: 1,
      annonceTitre: 'Collecte Pommes de Terre Spunta (15 000 Kg)',
      agriculteurId: 1,
      agriculteurNom: 'Mabrouk El Falah',
      agriculteurFerme: 'Domaine Vert du Cap Bon',
      recolteId: 302,
      recolteNom: 'Parcelle Vallée - Pommes de Terre Saison',
      quantiteProposee: 4500,
      quantiteAcceptee: 4500,
      quantiteRecue: 4500,
      dateProposition: '2026-11-23T09:00:00',
      dateReception: '2026-11-25T11:00:00',
      statut: 'RECUE',
      commentaire: 'Livraison complète reçue et déchargée.'
    },
    {
      id: 3,
      annonceId: 1,
      annonceTitre: 'Collecte Pommes de Terre Spunta (15 000 Kg)',
      agriculteurId: 12,
      agriculteurNom: 'Tahar Ben Amor',
      agriculteurFerme: 'Ferme Maraîchère Soliman',
      recolteId: 303,
      recolteNom: 'Lot Spunta Soliman',
      quantiteProposee: 4500,
      quantiteAcceptee: 0,
      quantiteRecue: 0,
      dateProposition: '2026-11-25T16:45:00',
      statut: 'EN_ATTENTE',
      commentaire: 'Récolte fraîche prête pour chargement dépôt.'
    },

    // Propositions pour Coop 502 (Mejerda - Annonces 3 & 4)
    {
      id: 4,
      annonceId: 3,
      annonceTitre: 'Collecte Blé Dur Supérieur de la Mejerda (45 000 Kg)',
      agriculteurId: 21,
      agriculteurNom: 'Si Amor Riahi',
      agriculteurFerme: 'Exploitation Céréalière Oued Zarga',
      recolteId: 401,
      recolteNom: 'Grandes Parcelles Blé Dur Khiar',
      quantiteProposee: 20000,
      quantiteAcceptee: 20000,
      quantiteRecue: 20000,
      dateProposition: '2026-11-20T08:00:00',
      dateReception: '2026-11-23T15:30:00',
      statut: 'RECUE',
      commentaire: 'Taux d\'humidité 11.5% conforme aux exigences des silos.'
    },
    {
      id: 5,
      annonceId: 3,
      annonceTitre: 'Collecte Blé Dur Supérieur de la Mejerda (45 000 Kg)',
      agriculteurId: 22,
      agriculteurNom: 'Habib Cherif',
      agriculteurFerme: 'Domaine Agricole Testour',
      recolteId: 402,
      recolteNom: 'Parcelle Testour Ouest',
      quantiteProposee: 12000,
      quantiteAcceptee: 12000,
      quantiteRecue: 0,
      dateProposition: '2026-11-21T11:00:00',
      statut: 'ACCEPTEE',
      commentaire: 'En attente de rotation des camions pour acheminement.'
    },
    {
      id: 6,
      annonceId: 3,
      annonceTitre: 'Collecte Blé Dur Supérieur de la Mejerda (45 000 Kg)',
      agriculteurId: 23,
      agriculteurNom: 'Mouldi Dhaouadi',
      agriculteurFerme: 'Ferme de Teboursouk',
      recolteId: 403,
      recolteNom: 'Hauts Plateaux Teboursouk',
      quantiteProposee: 15000,
      quantiteAcceptee: 0,
      quantiteRecue: 0,
      dateProposition: '2026-11-25T14:20:00',
      statut: 'EN_ATTENTE',
      commentaire: 'Grains denses de qualité semence.'
    },

    // Propositions pour Coop 503 (Zaghouan - Annonces 5 & 6)
    {
      id: 7,
      annonceId: 5,
      annonceTitre: 'Collecte Olives Chétoui (28 000 Kg)',
      agriculteurId: 15,
      agriculteurNom: 'Mondher Zannad',
      agriculteurFerme: 'Huilerie Artisanale Zannad',
      recolteId: 501,
      recolteNom: 'Oliveraies Zaghouan Nord',
      quantiteProposee: 10000,
      quantiteAcceptee: 10000,
      quantiteRecue: 10000,
      dateProposition: '2026-11-22T07:30:00',
      dateReception: '2026-11-24T18:00:00',
      statut: 'RECUE',
      commentaire: 'Olives cueillies à maturité optimale.'
    },
    {
      id: 8,
      annonceId: 5,
      annonceTitre: 'Collecte Olives Chétoui (28 000 Kg)',
      agriculteurId: 16,
      agriculteurNom: 'Bechir Ben Amor',
      agriculteurFerme: 'Vergers du Jbel Zaghouan',
      recolteId: 502,
      recolteNom: 'Coteaux Zaghouan',
      quantiteProposee: 6000,
      quantiteAcceptee: 6000,
      quantiteRecue: 0,
      dateProposition: '2026-11-23T10:15:00',
      statut: 'ACCEPTEE',
      commentaire: 'Livraison prévue demain matin.'
    },
    {
      id: 9,
      annonceId: 5,
      annonceTitre: 'Collecte Olives Chétoui (28 000 Kg)',
      agriculteurId: 17,
      agriculteurNom: 'Kamel Souissi',
      agriculteurFerme: 'Domaine Sidi Medien',
      recolteId: 503,
      recolteNom: 'Parcelle Sidi Medien',
      quantiteProposee: 7000,
      quantiteAcceptee: 0,
      quantiteRecue: 0,
      dateProposition: '2026-11-25T17:00:00',
      statut: 'EN_ATTENTE',
      commentaire: 'Olives saines prêtes pour pressage immédiat.'
    }
  ]);

  // Annonces filtrées pour la coopérative active
  activeAnnonces = computed(() => {
    return this.annonces().filter(a => a.responsableId === this.currentCoopId());
  });

  // Propositions filtrées pour les annonces de la coopérative active
  activePropositions = computed(() => {
    const myAnnonceIds = new Set(this.activeAnnonces().map(a => a.id));
    return this.propositions().filter(p => myAnnonceIds.has(p.annonceId));
  });

  // KPIs calculés dynamiquement pour la coopérative active
  kpiStats = computed(() => {
    const coopAnnonces = this.activeAnnonces();
    const coopProps = this.activePropositions();
    const allBesoins = this.besoins();

    const annoncesOuvertes = coopAnnonces.filter(a => a.statut === 'OUVERTE').length;
    const totalObjectifKg = coopAnnonces.reduce((sum, a) => sum + Number(a.quantiteObjectif), 0);
    const totalCollecteKg = coopAnnonces.reduce((sum, a) => sum + Number(a.quantiteAcceptee), 0);
    const tauxCouverture = totalObjectifKg > 0 ? Math.round((totalCollecteKg / totalObjectifKg) * 100) : 0;
    
    // Besoins liés aux annonces de cette coopérative
    const myBesoinIds = new Set(coopAnnonces.map(a => a.besoinId));
    const coopBesoins = allBesoins.filter(b => myBesoinIds.has(b.id));
    const besoinsSatisfaits = coopBesoins.filter(b => b.statut === 'SATISFAIT').length;
    const propositionsEnAttente = coopProps.filter(p => p.statut === 'EN_ATTENTE').length;

    return {
      annoncesOuvertes,
      totalObjectifKg,
      totalCollecteKg,
      tauxCouverture,
      besoinsSatisfaits,
      totalBesoins: coopBesoins.length > 0 ? coopBesoins.length : allBesoins.length,
      propositionsEnAttente
    };
  });

  constructor() {
    this.refreshFromBackend();
  }

  /**
   * Tente de récupérer les données réelles du backend Spring Boot MS5
   */
  refreshFromBackend(): void {
    this.http.get<any[]>(`${this.baseUrl}/besoins`).pipe(
      catchError(() => of(null)),
      tap(backendBesoins => {
        if (backendBesoins && Array.isArray(backendBesoins) && backendBesoins.length > 0) {
          this.besoins.set(backendBesoins.map(b => ({
            id: b.id,
            clientId: b.clientId,
            clientNom: `Client Réf. ${b.clientId}`,
            productId: b.productId,
            productNom: `Produit Réf. ${b.productId}`,
            quantiteDemandee: b.quantiteDemandee,
            dateSouhaitee: b.dateSouhaitee,
            description: b.description,
            statut: b.statut,
            dateCreation: b.dateCreation
          })));
        }
      })
    ).subscribe();

    this.http.get<any[]>(`${this.baseUrl}/annonces-collecte`).pipe(
      catchError(() => of(null)),
      tap(backendAnnonces => {
        if (backendAnnonces && Array.isArray(backendAnnonces) && backendAnnonces.length > 0) {
          this.annonces.set(backendAnnonces.map(a => ({
            id: a.id,
            besoinId: a.besoinId,
            responsableId: a.responsableId,
            quantiteObjectif: a.quantiteObjectif,
            quantiteAcceptee: a.quantiteAcceptee || 0,
            datePublication: a.datePublication,
            dateLimite: a.dateLimite,
            description: a.description,
            statut: a.statut,
            propositionsCount: a.propositions ? a.propositions.length : 0
          })));
        }
      })
    ).subscribe();
  }

  // ==========================================
  // ACTIONS BESOINS CLIENTS
  // ==========================================
  createBesoin(data: {
    clientId: number;
    productId: number;
    productNom?: string;
    quantiteDemandee: number;
    dateSouhaitee: string;
    description?: string;
  }): void {
    const payload = {
      clientId: data.clientId,
      productId: data.productId,
      quantiteDemandee: data.quantiteDemandee,
      dateSouhaitee: data.dateSouhaitee,
      description: data.description
    };

    this.http.post<any>(`${this.baseUrl}/besoins`, payload).pipe(
      catchError(() => {
        // En cas d'indisponibilité backend, mise à jour du state local
        return of({
          id: Date.now(),
          ...payload,
          statut: 'SOUMIS' as StatutBesoin
        });
      }),
      tap(saved => {
        const newBesoin: BesoinClient = {
          id: saved.id,
          clientId: saved.clientId,
          clientNom: `Client Partenaire ${saved.clientId}`,
          clientEntreprise: 'Société Achat B2B',
          productId: saved.productId,
          productNom: data.productNom || `Produit Réf. ${saved.productId}`,
          quantiteDemandee: saved.quantiteDemandee,
          dateSouhaitee: saved.dateSouhaitee,
          description: saved.description,
          statut: saved.statut || 'SOUMIS',
          dateCreation: new Date().toISOString().split('T')[0]
        };
        this.besoins.update(list => [newBesoin, ...list]);
      })
    ).subscribe();
  }

  annulerBesoin(id: number): void {
    this.http.patch(`${this.baseUrl}/besoins/${id}/annuler`, {}).pipe(
      catchError(() => of(null)),
      tap(() => {
        this.besoins.update(list => 
          list.map(b => b.id === id ? { ...b, statut: 'ANNULE' as StatutBesoin } : b)
        );
      })
    ).subscribe();
  }

  // ==========================================
  // ACTIONS ANNONCES DE COLLECTE
  // ==========================================
  createAnnonce(data: {
    besoinId: number;
    responsableId?: number;
    quantiteObjectif: number;
    dateLimite: string;
    description: string;
    besoinInfo?: { produitNom: string; clientNom: string; quantiteInitiale: number };
  }): void {
    const payload = {
      besoinId: data.besoinId,
      responsableId: data.responsableId || this.cooperativeProfile().id,
      quantiteObjectif: data.quantiteObjectif,
      dateLimite: data.dateLimite,
      description: data.description
    };

    this.http.post<any>(`${this.baseUrl}/annonces-collecte`, payload).pipe(
      catchError(() => {
        return of({
          id: Date.now(),
          ...payload,
          quantiteAcceptee: 0,
          statut: 'OUVERTE' as StatutAnnonce,
          datePublication: new Date().toISOString()
        });
      }),
      tap(saved => {
        const newAnnonce: AnnonceCollecte = {
          id: saved.id,
          besoinId: saved.besoinId,
          besoinInfo: data.besoinInfo,
          responsableId: saved.responsableId,
          responsableNom: this.cooperativeProfile().responsable,
          cooperativeNom: this.cooperativeProfile().nom,
          quantiteObjectif: saved.quantiteObjectif,
          quantiteAcceptee: saved.quantiteAcceptee || 0,
          datePublication: saved.datePublication || new Date().toISOString().split('T')[0],
          dateLimite: saved.dateLimite,
          description: saved.description,
          statut: saved.statut || 'OUVERTE',
          propositionsCount: 0
        };
        this.annonces.update(list => [newAnnonce, ...list]);

        // Mettre à jour le statut du besoin en local
        this.besoins.update(list =>
          list.map(b => b.id === data.besoinId ? { ...b, statut: 'EN_COLLECTE' as StatutBesoin } : b)
        );
      })
    ).subscribe();
  }

  cloturerAnnonce(id: number): void {
    this.http.patch(`${this.baseUrl}/annonces-collecte/${id}/cloturer`, {}).pipe(
      catchError(() => of(null)),
      tap(() => {
        this.annonces.update(list =>
          list.map(a => a.id === id ? { ...a, statut: 'CLOTUREE' as StatutAnnonce } : a)
        );
      })
    ).subscribe();
  }

  // ==========================================
  // ACTIONS PROPOSITIONS DE RECOLTE
  // ==========================================
  submitProposition(data: {
    annonceId: number;
    agriculteurId: number;
    agriculteurNom?: string;
    recolteId: number;
    quantiteProposee: number;
    commentaire?: string;
  }): void {
    const payload = {
      annonceId: data.annonceId,
      agriculteurId: data.agriculteurId,
      recolteId: data.recolteId,
      quantiteProposee: data.quantiteProposee,
      commentaire: data.commentaire
    };

    this.http.post<any>(`${this.baseUrl}/annonces-collecte/${data.annonceId}/propositions`, payload).pipe(
      catchError(() => {
        return of({
          id: Date.now(),
          ...payload,
          statut: 'EN_ATTENTE' as StatutProposition,
          dateProposition: new Date().toISOString()
        });
      }),
      tap(saved => {
        const annonce = this.annonces().find(a => a.id === data.annonceId);
        const newProp: PropositionRecolte = {
          id: saved.id,
          annonceId: saved.annonceId,
          annonceTitre: annonce ? `Annonce ${annonce.id} (${annonce.quantiteObjectif} Kg)` : `Annonce ${saved.annonceId}`,
          agriculteurId: saved.agriculteurId,
          agriculteurNom: data.agriculteurNom || `Agriculteur ${saved.agriculteurId}`,
          recolteId: saved.recolteId,
          quantiteProposee: saved.quantiteProposee,
          statut: saved.statut || 'EN_ATTENTE',
          dateProposition: saved.dateProposition || new Date().toISOString(),
          commentaire: saved.commentaire
        };
        this.propositions.update(list => [newProp, ...list]);
      })
    ).subscribe();
  }

  traiterDecision(id: number, statut: 'ACCEPTEE' | 'REFUSEE', quantiteAcceptee?: number): void {
    const payload = { statut, quantiteAcceptee: quantiteAcceptee || 0 };

    this.http.patch<any>(`${this.baseUrl}/propositions/${id}/decision`, payload).pipe(
      catchError(() => of({ id, statut, quantiteAcceptee })),
      tap(() => {
        this.propositions.update(list =>
          list.map(p => {
            if (p.id === id) {
              return {
                ...p,
                statut: statut as StatutProposition,
                quantiteAcceptee: statut === 'ACCEPTEE' ? (quantiteAcceptee || p.quantiteProposee) : 0
              };
            }
            return p;
          })
        );
        this.recalculerAnnonceQuantite(id);
      })
    ).subscribe();
  }

  enregistrerReception(id: number, quantiteRecue: number): void {
    const payload = { quantiteRecue };

    this.http.patch<any>(`${this.baseUrl}/propositions/${id}/reception`, payload).pipe(
      catchError(() => of({ id, quantiteRecue })),
      tap(() => {
        this.propositions.update(list =>
          list.map(p => {
            if (p.id === id) {
              return {
                ...p,
                statut: 'RECUE' as StatutProposition,
                quantiteRecue,
                dateReception: new Date().toISOString()
              };
            }
            return p;
          })
        );
        this.verifierSatisfactionBesoin(id);
      })
    ).subscribe();
  }

  private recalculerAnnonceQuantite(propId: number): void {
    const prop = this.propositions().find(p => p.id === propId);
    if (!prop) return;

    const totalAcceptee = this.propositions()
      .filter(p => p.annonceId === prop.annonceId && (p.statut === 'ACCEPTEE' || p.statut === 'RECUE'))
      .reduce((sum, p) => sum + (p.quantiteAcceptee || 0), 0);

    this.annonces.update(list =>
      list.map(a => {
        if (a.id === prop.annonceId) {
          const reached = totalAcceptee >= a.quantiteObjectif;
          return {
            ...a,
            quantiteAcceptee: totalAcceptee,
            statut: reached ? 'OBJECTIF_ATTEINT' as StatutAnnonce : a.statut
          };
        }
        return a;
      })
    );
  }

  private verifierSatisfactionBesoin(propId: number): void {
    const prop = this.propositions().find(p => p.id === propId);
    if (!prop) return;

    const annonce = this.annonces().find(a => a.id === prop.annonceId);
    if (!annonce) return;

    // RÈGLE MÉTIER CRITIQUE : Somme des quantités RÉELLEMENT REÇUES >= quantité demandée
    const totalRecu = this.propositions()
      .filter(p => p.annonceId === prop.annonceId)
      .reduce((sum, p) => sum + (p.quantiteRecue || 0), 0);

    const besoin = this.besoins().find(b => b.id === annonce.besoinId);
    if (besoin && totalRecu >= besoin.quantiteDemandee) {
      this.besoins.update(list =>
        list.map(b => b.id === besoin.id ? { ...b, statut: 'SATISFAIT' as StatutBesoin } : b)
      );
    }
  }
}
