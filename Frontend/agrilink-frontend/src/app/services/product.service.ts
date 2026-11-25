import { Injectable, signal } from '@angular/core';
import { Product, FarmerOffer, Harvest, FarmerProfile } from '../models/product.model';

@Injectable({
  providedIn: 'root'
})
export class ProductService {
  // Current active farmer profile (default: Haj Moncef Jouini)
  private currentFarmerId = signal<string>('far-10');

  private farmers = signal<FarmerProfile[]>([
    {
      id: 'far-10',
      name: 'Haj Moncef Jouini',
      farmName: 'Ferme Les Orangers Royaux',
      avatar: 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=120&q=80',
      location: 'Menzel Bouzelfa, Nabeul',
      region: 'Nabeul',
      phone: '+216 72 200 450',
      rating: 5.0,
      reviewsCount: 94,
      bio: 'Producteur passionné d\'agrumes traditionnels depuis 4 décennies au cœur du Cap Bon. Nous pratiquons une agriculture respectueuse de l\'eau et du sol tunisien.',
      memberSince: 'Mars 2023',
      certifications: ['Reine de Tunisie Terroir', 'Zéro Résidu Chimique', 'Cueillette Solaire']
    },
    {
      id: 'far-1',
      name: 'Mabrouk El Falah',
      farmName: 'Domaine Vert du Cap Bon',
      avatar: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=120&q=80',
      location: 'Soliman, Nabeul',
      region: 'Nabeul',
      phone: '+216 72 350 120',
      rating: 4.9,
      reviewsCount: 42,
      bio: 'Pionnier de la culture de l\'avocat et des fruits sains sans aucun pesticide post-récolte.',
      memberSince: 'Janvier 2024',
      certifications: ['Non Traité & Raisonné', 'Circuit Court Vérifié']
    },
    {
      id: 'far-15',
      name: 'Mondher Zannad',
      farmName: 'Huilerie Artisanale Zannad',
      avatar: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=120&q=80',
      location: 'Zaghouan',
      region: 'Zaghouan',
      phone: '+216 72 400 890',
      rating: 5.0,
      reviewsCount: 88,
      bio: 'Producteur d\'huile d\'olive extra vierge médaillée d\'or, pressée à froid au pied de la montagne de Zaghouan.',
      memberSince: 'Février 2023',
      certifications: ['Médaille d\'Or Terroir', 'Pressage à Froid']
    }
  ]);

  // Harvests signal
  private harvests = signal<Harvest[]>([
    {
      id: 'harv-1',
      farmerId: 'far-10',
      productId: 'orange-maltaise-succulente',
      productName: 'Oranges Maltaises de Nabeul',
      productImage: 'https://images.unsplash.com/photo-1547514701-42782101795e?w=600&q=80',
      productCategory: 'Fruits',
      parcelName: 'Verger Sud - Parcelle des Orangers Centenaires',
      harvestDate: '2026-11-24',
      quantity: 450,
      unit: 'Kg',
      qualityGrade: 'Extra - Calibre 1A (Demi-Sanguine)',
      method: 'Cueillette manuelle sélective à pleine maturité',
      status: 'Récoltée',
      notes: 'Taux de sucre exceptionnel cette saison, pulpe juteuse.',
      syncedWithStock: true
    },
    {
      id: 'harv-2',
      farmerId: 'far-10',
      productId: 'clementine-de-saison',
      productName: 'Clémentine Douce de Saison',
      productImage: 'https://images.unsplash.com/photo-1611080626919-7cf5a9dbab5b?w=600&q=80',
      productCategory: 'Fruits',
      parcelName: 'Parcelle Les Collines Douces',
      harvestDate: '2026-11-26',
      quantity: 300,
      unit: 'Kg',
      qualityGrade: 'Premier Choix sans pépins',
      method: 'Cueillette avec feuilles de garantie fraîcheur',
      status: 'En cours',
      notes: 'Cueillette en cours pour les commandes de la marketplace.',
      syncedWithStock: true
    },
    {
      id: 'harv-3',
      farmerId: 'far-10',
      productId: 'citron-bio',
      productName: 'Citron Bio Frais',
      productImage: 'https://images.unsplash.com/photo-1533038590840-1cde6e668a91?w=600&q=80',
      productCategory: 'Fruits',
      parcelName: 'Verger Ouest - Ruisseau de Beni Khaled',
      harvestDate: '2026-12-02',
      quantity: 200,
      unit: 'Kg',
      qualityGrade: 'Bio Certifié - Zeste comestible',
      method: 'Cueillette manuelle douce',
      status: 'Planifiée',
      notes: 'Arbres en fin de floraison automnale, récolte d\'hiver.',
      syncedWithStock: false
    }
  ]);
  private products = signal<Product[]>([
    {
      id: 'avocat-calibre-moyen',
      name: 'Avocat Calibre Moyen non traité',
      frenchName: 'Avocat Calibre Moyen non traité',
      category: 'Fruits',
      isBio: false,
      isNonTraite: true,
      unit: '(1 Piece)',
      image: 'https://images.unsplash.com/photo-1523049673857-eb18f1d7b578?w=600&q=80',
      gallery: [
        'https://images.unsplash.com/photo-1523049673857-eb18f1d7b578?w=600&q=80',
        'https://images.unsplash.com/photo-1601039641847-7857b994d704?w=600&q=80',
        'https://images.unsplash.com/photo-1590005354167-6da97870c757?w=600&q=80'
      ],
      sku: 'AVO-0281',
      tags: ['FRUITS', 'BIO', 'NATUREL', 'SUPERFOOD'],
      shortDescription: 'Avocats crémeux récoltés à la main à parfaite maturité, cultivés selon des méthodes traditionnelles sans aucun traitement chimique post-récolte.',
      fullDescription: `Nos avocats sont cultivés dans des vergers ensoleillés par des agriculteurs passionnés. Chair fondante, riche en acides gras insaturés et vitamines E. Chaque fruit est sélectionné à la main pour garantir une fraîcheur et une texture impeccables.
      Idéal pour vos toasts, salades composées ou guacamole maison. Cultivé sans pesticides de synthèse ni conservateurs chimiques.`,
      additionalInfo: {
        origin: 'Cap Bon, Tunisie',
        conservation: 'À température ambiante jusqu’à maturité, puis 3 jours au frais',
        variety: 'Hass & Fuerte',
        packaging: 'Carton recyclable aéré'
      },
      rating: 4.9,
      reviewsCount: 18,
      viewersCount: 14,
      soldLastHours: 6,
      offers: [
        {
          id: 'off-avocat-1',
          farmerId: 'far-1',
          farmerName: 'Mabrouk El Falah',
          farmName: 'Domaine Vert du Cap Bon',
          farmerAvatar: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=120&q=80',
          location: 'Soliman, Nabeul',
          region: 'Nabeul',
          distanceKm: 18,
          rating: 4.9,
          reviewsCount: 42,
          price: 2.800,
          originalPrice: 3.200,
          unit: 'Pièce',
          minOrderQuantity: 2,
          availableStock: 65,
          harvestDate: 'Récolté ce matin',
          certification: 'Non Traité & Raisonné',
          deliveryTime: 'Livraison demain avant 12h',
          farmerNote: 'Nos avocats mûrissent naturellement sur l’arbre au soleil du Cap Bon. Aucun produit chimique n’est utilisé.',
          isBestPrice: true,
          isLocalFavorite: true
        },
        {
          id: 'off-avocat-2',
          farmerId: 'far-2',
          farmerName: 'Youssef Chaabane',
          farmName: 'Ferme Biologique El Wifak',
          farmerAvatar: 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=120&q=80',
          location: 'Bizerte Nord',
          region: 'Bizerte',
          distanceKm: 34,
          rating: 4.8,
          reviewsCount: 29,
          price: 3.100,
          unit: 'Pièce',
          minOrderQuantity: 1,
          availableStock: 40,
          harvestDate: 'Récolté hier',
          certification: 'Certifié Bio Ecocert',
          deliveryTime: 'Livraison 24h - 48h',
          farmerNote: 'Agriculture 100% biologique certifiée. Variété Hass riche et onctueuse.',
          isEcoFriendly: true
        },
        {
          id: 'off-avocat-3',
          farmerId: 'far-3',
          farmerName: 'Fatma Ben Mahmoud',
          farmName: 'Les Jardins de Mornag',
          farmerAvatar: 'https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=120&q=80',
          location: 'Mornag, Ben Arous',
          region: 'Ben Arous',
          distanceKm: 12,
          rating: 5.0,
          reviewsCount: 56,
          price: 3.300,
          unit: 'Pièce',
          minOrderQuantity: 3,
          availableStock: 80,
          harvestDate: 'Récolte fraîche du jour',
          certification: 'Zéro Résidu Chimique',
          deliveryTime: 'Livraison Express le jour même',
          farmerNote: 'Ferme familiale depuis 3 générations, irrigation à l’eau de source de montagne.'
        }
      ]
    },
    {
      id: 'citron-bio',
      name: 'Citron Bio Frais',
      frenchName: 'Citron Bio',
      category: 'Fruits',
      isBio: true,
      isNonTraite: true,
      unit: '(1 Kg)',
      image: 'https://images.unsplash.com/photo-1533038590840-1cde6e668a91?w=600&q=80',
      gallery: [
        'https://images.unsplash.com/photo-1533038590840-1cde6e668a91?w=600&q=80',
        'https://images.unsplash.com/photo-1582281298055-e25b84a30b0b?w=600&q=80',
        'https://images.unsplash.com/photo-1590502593747-42a996133562?w=600&q=80'
      ],
      sku: 'CIT-0105',
      tags: ['AGRUMES', 'BIO', 'VITAMINE C', 'FRAIS'],
      shortDescription: 'Citrons 4 saisons juteux et très parfumés, non cirés, avec zeste comestible idéal pour vos assaisonnements et jus.',
      fullDescription: `Le citron de nos terroirs est réputé pour sa peau fine et sa richesse en jus aromatique. Sans conservateur ni cire chimique après récolte, vous pouvez consommer le zeste en toute sérénité.
      Riche en vitamine C naturelle et en antioxydants puissants.`,
      additionalInfo: {
        origin: 'Nabeul & Zaghouan, Tunisie',
        conservation: '15 jours au frais ou à l’air libre',
        variety: 'Eureka / Quatre Saisons',
        packaging: 'Filet en coton biologique'
      },
      rating: 4.8,
      reviewsCount: 31,
      viewersCount: 9,
      soldLastHours: 8,
      offers: [
        {
          id: 'off-citron-1',
          farmerId: 'far-4',
          farmerName: 'Kamel Belhadj',
          farmName: 'Vergers des Agrumes de Beni Khiar',
          farmerAvatar: 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=120&q=80',
          location: 'Beni Khiar, Nabeul',
          region: 'Nabeul',
          distanceKm: 22,
          rating: 4.9,
          reviewsCount: 38,
          price: 2.200,
          originalPrice: 2.600,
          unit: 'Kg',
          minOrderQuantity: 1,
          availableStock: 120,
          harvestDate: 'Cueilli ce matin',
          certification: 'Certifié Bio 100%',
          deliveryTime: 'Livraison demain',
          farmerNote: 'Arbres irrigués avec passion. Arôme puissant et zeste très parfumé.',
          isBestPrice: true
        },
        {
          id: 'off-citron-2',
          farmerId: 'far-5',
          farmerName: 'Hedi Mansour',
          farmName: 'Ferme Zaghouan Ecologique',
          farmerAvatar: 'https://images.unsplash.com/photo-1519085360753-af0119f7cbe7?w=120&q=80',
          location: 'El Fahs, Zaghouan',
          region: 'Zaghouan',
          distanceKm: 45,
          rating: 4.7,
          reviewsCount: 19,
          price: 2.400,
          unit: 'Kg',
          minOrderQuantity: 2,
          availableStock: 85,
          harvestDate: 'Hier après-midi',
          certification: 'Agriculture Naturelle',
          deliveryTime: 'Livraison 24h',
          farmerNote: 'Citrons cultivés en altitude avec eau de source naturelle.'
        }
      ]
    },
    {
      id: 'datte-non-traitee',
      name: 'Datte Deglet Nour Non-Traitée',
      frenchName: 'Datte Non-Traitée (500g)',
      category: 'Fruits',
      isBio: false,
      isNonTraite: true,
      unit: '(0.5 Kg)',
      image: 'https://images.unsplash.com/photo-1546548970-71785318a17b?w=600&q=80',
      gallery: [
        'https://images.unsplash.com/photo-1546548970-71785318a17b?w=600&q=80',
        'https://images.unsplash.com/photo-1574856344991-aaa31b6f4ce3?w=600&q=80'
      ],
      sku: 'DAT-0402',
      tags: ['DATTES', 'SUD', 'DEGLET NOUR', 'SUCRE NATUREL'],
      shortDescription: 'Véritable Deglet Nour branchée du Sud tunisien, miellée, translucide et séchée naturellement sur le palmier.',
      fullDescription: `Cueillie à maturité dans les oasis traditionnelles du Jérid, notre Deglet Nour offre une chair moelleuse aux arômes de caramel et de miel.
      Aucun ajout de sirop de glucose ni traitement chimique de conservation. Un trésor d’énergie naturelle pour sportifs et gourmets.`,
      additionalInfo: {
        origin: 'Tozeur & Kebili, Tunisie',
        conservation: 'Conserver dans un endroit sec et frais',
        variety: 'Deglet Nour Branchée',
        packaging: 'Boîte en carton kraft hermétique'
      },
      rating: 5.0,
      reviewsCount: 64,
      viewersCount: 22,
      soldLastHours: 15,
      offers: [
        {
          id: 'off-datte-1',
          farmerId: 'far-6',
          farmerName: 'Tahar Ben Amor',
          farmName: 'Palmeraie Authentique de Tozeur',
          farmerAvatar: 'https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?w=120&q=80',
          location: 'Tozeur Oasis',
          region: 'Tozeur',
          distanceKm: 290,
          rating: 5.0,
          reviewsCount: 78,
          price: 5.500,
          originalPrice: 6.200,
          unit: '500g',
          minOrderQuantity: 1,
          availableStock: 200,
          harvestDate: 'Nouvelle Récolte 2026',
          certification: 'Oasis Patrimoine & Naturel',
          deliveryTime: 'Livraison 48h suivie',
          farmerNote: 'Dattes branchées de première sélection directement du palmier.',
          isBestPrice: true,
          isLocalFavorite: true
        },
        {
          id: 'off-datte-2',
          farmerId: 'far-7',
          farmerName: 'Salem Ghouma',
          farmName: 'Domaine Dattes Bio Kebili',
          farmerAvatar: 'https://images.unsplash.com/photo-1492562080023-ab3db95bfbce?w=120&q=80',
          location: 'Kebili',
          region: 'Kebili',
          distanceKm: 310,
          rating: 4.9,
          reviewsCount: 35,
          price: 5.800,
          unit: '500g',
          minOrderQuantity: 1,
          availableStock: 150,
          harvestDate: 'Sélection Grand Cru',
          certification: 'Certifié Bio Export',
          deliveryTime: 'Livraison 48h',
          farmerNote: 'Sans conservateurs, qualité supérieure triée gousse par gousse.'
        }
      ]
    },
    {
      id: 'grenade-non-traitee',
      name: 'Grenade Gabèsienne Non-traitée',
      frenchName: 'Grenade Non-traitée',
      category: 'Fruits',
      isBio: false,
      isNonTraite: true,
      unit: '(1 Kg)',
      image: 'https://images.unsplash.com/photo-1615485290382-441e4d049cb5?w=600&q=80',
      gallery: [
        'https://images.unsplash.com/photo-1615485290382-441e4d049cb5?w=600&q=80',
        'https://images.unsplash.com/photo-1541344999736-83eca872f240?w=600&q=80'
      ],
      sku: 'GRE-0921',
      tags: ['GRENADE', 'ANTIOXYDANT', 'GABES', 'JUS'],
      shortDescription: 'Grenades douces aux grains rubis croquants et sucrés, issues des célèbres vergers de Testour et Gabès.',
      fullDescription: `Riches en polyphénols et antioxydants, nos grenades locales sont récoltées avec soin. Grains tendres, pépins doux et jus généreux au goût sucré équilibré.
      Parfaites à déguster fraîches, en jus pressé ou pour égayer vos desserts et salades.`,
      additionalInfo: {
        origin: 'Testour / Gabès, Tunisie',
        conservation: 'Jusqu’à 3 semaines au bac à légumes',
        variety: 'Gabsi & Chélfi',
        packaging: 'Caissette en bois naturel'
      },
      rating: 4.7,
      reviewsCount: 22,
      viewersCount: 11,
      soldLastHours: 5,
      offers: [
        {
          id: 'off-grenade-1',
          farmerId: 'far-8',
          farmerName: 'Amor Trabelsi',
          farmName: 'Verger Andalou de Testour',
          farmerAvatar: 'https://images.unsplash.com/photo-1522075469751-3a6694fb2f61?w=120&q=80',
          location: 'Testour, Béja',
          region: 'Béja',
          distanceKm: 75,
          rating: 4.8,
          reviewsCount: 27,
          price: 3.400,
          originalPrice: 4.000,
          unit: 'Kg',
          minOrderQuantity: 2,
          availableStock: 90,
          harvestDate: 'Cueilli hier',
          certification: 'Saveur du Terroir Testouri',
          deliveryTime: 'Livraison 24h',
          farmerNote: 'Arrosé par les eaux de la Medjerda, goût exceptionnellement doux.',
          isBestPrice: true
        },
        {
          id: 'off-grenade-2',
          farmerId: 'far-9',
          farmerName: 'Noureddine Zouari',
          farmName: 'Grenadiers de Gabès Oasis',
          farmerAvatar: 'https://images.unsplash.com/photo-1560250097-0b93528c311a?w=120&q=80',
          location: 'Chenini, Gabès',
          region: 'Gabès',
          distanceKm: 320,
          rating: 4.9,
          reviewsCount: 44,
          price: 3.800,
          unit: 'Kg',
          minOrderQuantity: 2,
          availableStock: 110,
          harvestDate: 'Récolte fraîche',
          certification: 'Indication Géographique Gabès',
          deliveryTime: 'Livraison 48h',
          farmerNote: 'La fameuse grenade de Gabès aux pépins fondants.'
        }
      ]
    },
    {
      id: 'orange-maltaise-succulente',
      name: 'Oranges Maltaises de Nabeul',
      frenchName: 'Bag Of Succulent Oranges',
      category: 'Fruits',
      isBio: true,
      isNonTraite: true,
      unit: '(1 Kg)',
      image: 'https://images.unsplash.com/photo-1547514701-42782101795e?w=600&q=80',
      gallery: [
        'https://images.unsplash.com/photo-1547514701-42782101795e?w=600&q=80',
        'https://images.unsplash.com/photo-1611080626919-7cf5a9dbab5b?w=600&q=80',
        'https://images.unsplash.com/photo-1582979512210-99b6a53386f9?w=600&q=80',
        'https://images.unsplash.com/photo-1587132137056-bfbf0166836e?w=600&q=80'
      ],
      sku: 'ORA-0282',
      tags: ['FOOD', 'NUTS/ORGANIC', 'AGRUMES', 'MALTAISE', 'JUS'],
      shortDescription: 'La reine des oranges : pulpe sanguine douce, jus abondant et équilibre parfait entre sucre et fraîche acidité.',
      fullDescription: `Célébrée mondialement pour ses qualités gustatives uniques, la Maltaise de Tunisie est cultivée sous le microclimat privilégié de la presqu'île du Cap Bon.
      Idéale aussi bien en orange de table qu'en jus frais du matin. Aucun traitement d’enrobage post-récolte.`,
      additionalInfo: {
        origin: 'Menzel Bouzelfa & Beni Khaled, Nabeul',
        conservation: '2 semaines dans un endroit tempéré et aéré',
        variety: 'Maltaise Demi-Sanguine',
        packaging: 'Panier kraft perforé réutilisable'
      },
      rating: 5.0,
      reviewsCount: 89,
      viewersCount: 29,
      soldLastHours: 19,
      offers: [
        {
          id: 'off-orange-1',
          farmerId: 'far-10',
          farmerName: 'Haj Moncef Jouini',
          farmName: 'Ferme Les Orangers Royaux',
          farmerAvatar: 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=120&q=80',
          location: 'Menzel Bouzelfa, Nabeul',
          region: 'Nabeul',
          distanceKm: 26,
          rating: 5.0,
          reviewsCount: 94,
          price: 2.500,
          originalPrice: 3.000,
          unit: 'Kg',
          minOrderQuantity: 2,
          availableStock: 250,
          harvestDate: 'Ce matin à 6h00',
          certification: 'Reine de Tunisie - Terroir Certifié',
          deliveryTime: 'Livraison demain matin',
          farmerNote: 'Oranges gorgées de soleil, cueillies à pleine maturité sans aucun intermédiaire.',
          isBestPrice: true,
          isLocalFavorite: true
        },
        {
          id: 'off-orange-2',
          farmerId: 'far-11',
          farmerName: 'Si Ridha El Ghali',
          farmName: 'Domaine Bio Citrus Beni Khaled',
          farmerAvatar: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=120&q=80',
          location: 'Beni Khaled, Nabeul',
          region: 'Nabeul',
          distanceKm: 24,
          rating: 4.9,
          reviewsCount: 52,
          price: 2.800,
          unit: 'Kg',
          minOrderQuantity: 1,
          availableStock: 180,
          harvestDate: 'Hier soir',
          certification: '100% Bio Certifié Ecocert',
          deliveryTime: 'Livraison en 24h',
          farmerNote: 'Culture biologique pure sans intrants synthétiques depuis plus de 15 ans.',
          isEcoFriendly: true
        },
        {
          id: 'off-orange-3',
          farmerId: 'far-12',
          farmerName: 'Chokri Gharbi',
          farmName: 'Coopérative Les Maraîchers du Cap',
          farmerAvatar: 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=120&q=80',
          location: 'Grombalia, Nabeul',
          region: 'Nabeul',
          distanceKm: 19,
          rating: 4.8,
          reviewsCount: 37,
          price: 2.700,
          unit: 'Kg',
          minOrderQuantity: 3,
          availableStock: 140,
          harvestDate: 'Ce matin',
          certification: 'Agriculture Raisonnée & Solidaire',
          deliveryTime: 'Livraison express demain',
          farmerNote: 'Partenariat direct avec 12 petits producteurs de la région de Grombalia.'
        }
      ]
    },
    {
      id: 'clementine-de-saison',
      name: 'Clémentine Douce de Saison',
      frenchName: 'Clémentine Bio',
      category: 'Fruits',
      isBio: true,
      isNonTraite: true,
      unit: '(1 Kg)',
      image: 'https://images.unsplash.com/photo-1611080626919-7cf5a9dbab5b?w=600&q=80',
      gallery: [
        'https://images.unsplash.com/photo-1611080626919-7cf5a9dbab5b?w=600&q=80',
        'https://images.unsplash.com/photo-1547514701-42782101795e?w=600&q=80'
      ],
      sku: 'CLE-0312',
      tags: ['CLEMENTINE', 'BIO', 'SANS PEPINS', 'GOURMAND'],
      shortDescription: 'Clémentines faciles à éplucher, sans pépins, très sucrées et adorées des enfants pour les pauses saines.',
      fullDescription: `Cueillies avec leur feuille verte gage de fraîcheur absolue, ces clémentines proviennent de vergers familiaux où chaque arbre reçoit un soin attentif.`,
      additionalInfo: {
        origin: 'Cap Bon, Tunisie',
        conservation: '10 à 15 jours à l’air libre',
        variety: 'Clémentine Commune',
        packaging: 'Poche kraft aérée'
      },
      rating: 4.9,
      reviewsCount: 45,
      viewersCount: 16,
      soldLastHours: 12,
      offers: [
        {
          id: 'off-clem-1',
          farmerId: 'far-10',
          farmerName: 'Haj Moncef Jouini',
          farmName: 'Ferme Les Orangers Royaux',
          farmerAvatar: 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=120&q=80',
          location: 'Menzel Bouzelfa, Nabeul',
          region: 'Nabeul',
          distanceKm: 26,
          rating: 5.0,
          reviewsCount: 94,
          price: 2.600,
          unit: 'Kg',
          minOrderQuantity: 1,
          availableStock: 160,
          harvestDate: 'Cueilli ce matin avec feuille',
          certification: 'Naturel & Sans Traitement',
          deliveryTime: 'Livraison demain',
          farmerNote: 'Douceur garantie, parfait pour les enfants !',
          isBestPrice: true
        }
      ]
    },
    {
      id: 'tomates-fermieresh-rondes',
      name: 'Tomates Fermières Rondes Plein Champ',
      frenchName: 'Tomates Bio Plein Champ',
      category: 'Legumes',
      isBio: true,
      isNonTraite: true,
      unit: '(1 Kg)',
      image: 'https://images.unsplash.com/photo-1592924357228-91a4daadcfea?w=600&q=80',
      gallery: [
        'https://images.unsplash.com/photo-1592924357228-91a4daadcfea?w=600&q=80',
        'https://images.unsplash.com/photo-1546470427-e26264be0b11?w=600&q=80'
      ],
      sku: 'TOM-0540',
      tags: ['LEGUMES', 'BIO', 'TOMATE', 'SALADE'],
      shortDescription: 'Tomates charnues mûries au soleil, parfum d’antan, idéales pour salades fraîches ou sauces maison.',
      fullDescription: `Cultivées en pleine terre sans serre chauffée, ces tomates fermières développent un parfum authentique et une saveur naturellement sucrée.`,
      additionalInfo: {
        origin: 'Tebourba & Manouba, Tunisie',
        conservation: 'Ne pas mettre au réfrigérateur pour préserver les arômes',
        variety: 'Tomate Ronde Ancienne',
        packaging: 'Barquette carton respirante'
      },
      rating: 4.8,
      reviewsCount: 37,
      viewersCount: 13,
      soldLastHours: 9,
      offers: [
        {
          id: 'off-tom-1',
          farmerId: 'far-13',
          farmerName: 'Belgacem Mejri',
          farmName: 'Ferme Maraîchère de la Medjerda',
          farmerAvatar: 'https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?w=120&q=80',
          location: 'Tebourba, Manouba',
          region: 'Manouba',
          distanceKm: 28,
          rating: 4.8,
          reviewsCount: 41,
          price: 1.800,
          originalPrice: 2.200,
          unit: 'Kg',
          minOrderQuantity: 2,
          availableStock: 220,
          harvestDate: 'Récolté à l’aube',
          certification: 'Plein Champ Zéro Pesticide',
          deliveryTime: 'Livraison demain matin',
          farmerNote: 'Tomates fermes et juteuses avec un vrai goût de tomate d’antan.',
          isBestPrice: true
        },
        {
          id: 'off-tom-2',
          farmerId: 'far-14',
          farmerName: 'Sofiene Ben Ali',
          farmName: 'Agri-Bio Mornag',
          farmerAvatar: 'https://images.unsplash.com/photo-1519085360753-af0119f7cbe7?w=120&q=80',
          location: 'Mornag',
          region: 'Ben Arous',
          distanceKm: 15,
          rating: 4.9,
          reviewsCount: 33,
          price: 2.100,
          unit: 'Kg',
          minOrderQuantity: 1,
          availableStock: 140,
          harvestDate: 'Hier',
          certification: 'Certifié Bio Ecocert',
          deliveryTime: 'Livraison 24h',
          farmerNote: 'Variété ancienne riche en lycopène et saveurs naturelles.'
        }
      ]
    },
    {
      id: 'huile-olive-extra-vierge',
      name: 'Huile d\'Olive Vierge Extra Première Pression',
      frenchName: 'Huile d\'Olive Bio 1L',
      category: 'Epicerie',
      isBio: true,
      isNonTraite: true,
      unit: '(1 L)',
      image: 'https://images.unsplash.com/photo-1474979266404-7eaacbcd87c5?w=600&q=80',
      gallery: [
        'https://images.unsplash.com/photo-1474979266404-7eaacbcd87c5?w=600&q=80',
        'https://images.unsplash.com/photo-1541256942802-7b299b48368f?w=600&q=80'
      ],
      sku: 'HUI-0801',
      tags: ['EPICERIE', 'HUILE D\'OLIVE', 'BIO', 'EXTRA VIERGE'],
      shortDescription: 'Huile d’olive pressée à froid dans les 12h suivant la récolte des olives Chemlali et Chetoui.',
      fullDescription: `Arômes intenses d’herbe fraîche et d’amande verte. Teneur en acidité inférieure à 0.3%, gage d'une qualité d'extraction irréprochable.`,
      additionalInfo: {
        origin: 'Sfax & Zaghouan, Tunisie',
        conservation: 'À l’abri de la lumière et de la chaleur',
        variety: 'Chetoui & Chemlali',
        packaging: 'Bouteille en verre teinté anti-UV'
      },
      rating: 5.0,
      reviewsCount: 112,
      viewersCount: 35,
      soldLastHours: 24,
      offers: [
        {
          id: 'off-huile-1',
          farmerId: 'far-15',
          farmerName: 'Mondher Zannad',
          farmName: 'Huilerie Artisanale Zannad',
          farmerAvatar: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=120&q=80',
          location: 'Zaghouan',
          region: 'Zaghouan',
          distanceKm: 55,
          rating: 5.0,
          reviewsCount: 88,
          price: 19.500,
          originalPrice: 22.000,
          unit: 'Bouteille 1L',
          minOrderQuantity: 1,
          availableStock: 90,
          harvestDate: 'Campagne Récolte 2026',
          certification: 'Médaille d’Or Qualité Terroir',
          deliveryTime: 'Livraison 24h sécurisée',
          farmerNote: 'Pressage à froid traditionnel mécanique sans aucun traitement chimique.',
          isBestPrice: true,
          isLocalFavorite: true
        },
        {
          id: 'off-huile-2',
          farmerId: 'far-16',
          farmerName: 'Adel Ellouze',
          farmName: 'Domaine Oléicole Chemlali',
          farmerAvatar: 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=120&q=80',
          location: 'Sfax Nord',
          region: 'Sfax',
          distanceKm: 240,
          rating: 4.9,
          reviewsCount: 67,
          price: 21.000,
          unit: 'Bouteille 1L',
          minOrderQuantity: 1,
          availableStock: 130,
          harvestDate: 'Pressée en Janvier',
          certification: '100% Bio Certifié Ecocert',
          deliveryTime: 'Livraison 48h',
          farmerNote: 'Olives cueillies manuellement au peigne sans abîmer les rameaux.'
        }
      ]
    },
    {
      id: 'miel-thym-sauvage',
      name: 'Miel Pur de Thym Sauvage de Montagne',
      frenchName: 'Miel de Thym Pur (500g)',
      category: 'Epicerie',
      isBio: true,
      isNonTraite: true,
      unit: '(500g)',
      image: 'https://images.unsplash.com/photo-1587049352846-4a222e784d38?w=600&q=80',
      gallery: [
        'https://images.unsplash.com/photo-1587049352846-4a222e784d38?w=600&q=80'
      ],
      sku: 'MIE-0199',
      tags: ['MIEL', 'APICULTURE', 'THYM', 'NATUREL'],
      shortDescription: 'Miel rare récolté sur les coteaux sauvages de Kroumirie, puissant, ambré et reconnu pour ses vertus apaisantes.',
      fullDescription: `Non chauffé, non pasteurisé, extrait à froid par nos apiculteurs partenaires pour conserver tous les enzymes et nutriments vivants de la ruche.`,
      additionalInfo: {
        origin: 'Aïn Draham, Kroumirie, Tunisie',
        conservation: 'Endroit sec à température ambiante',
        variety: 'Thym Sauvage (Thymus capitatus)',
        packaging: 'Pot en verre hermétique'
      },
      rating: 4.9,
      reviewsCount: 53,
      viewersCount: 18,
      soldLastHours: 11,
      offers: [
        {
          id: 'off-miel-1',
          farmerId: 'far-17',
          farmerName: 'Habib Cherif',
          farmName: 'Rucher Sauvage de Tabarka',
          farmerAvatar: 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=120&q=80',
          location: 'Tabarka / Aïn Draham',
          region: 'Jendouba',
          distanceKm: 160,
          rating: 4.9,
          reviewsCount: 62,
          price: 28.000,
          originalPrice: 32.000,
          unit: 'Pot 500g',
          minOrderQuantity: 1,
          availableStock: 50,
          harvestDate: 'Récolte d\'été artisanale',
          certification: 'Apiculture Biologique Respectueuse',
          deliveryTime: 'Livraison 24h - 48h',
          farmerNote: 'Nos abeilles butinent les fleurs sauvages de montagne préservées de toute pollution.',
          isBestPrice: true
        }
      ]
    }
  ]);

  getProducts(): Product[] {
    return this.products();
  }

  getProductById(id: string): Product | undefined {
    return this.products().find(p => p.id === id);
  }

  getProductsByCategory(category: string): Product[] {
    if (!category || category === 'Tous' || category === 'Tous les produits') {
      return this.products();
    }
    return this.products().filter(p => p.category.toLowerCase() === category.toLowerCase());
  }

  getRelatedProducts(currentId: string, limit = 4): Product[] {
    return this.products().filter(p => p.id !== currentId).slice(0, limit);
  }

  // --- Farmer Profiles ---
  getCurrentFarmer(): FarmerProfile {
    const found = this.farmers().find(f => f.id === this.currentFarmerId());
    return found || this.farmers()[0];
  }

  getFarmers(): FarmerProfile[] {
    return this.farmers();
  }

  setCurrentFarmer(farmerId: string): void {
    this.currentFarmerId.set(farmerId);
  }

  // --- Farmer Offers ---
  getOffersByFarmer(farmerId: string): { product: Product; offer: FarmerOffer }[] {
    const result: { product: Product; offer: FarmerOffer }[] = [];
    for (const prod of this.products()) {
      for (const off of prod.offers) {
        if (off.farmerId === farmerId) {
          result.push({ product: prod, offer: off });
        }
      }
    }
    return result;
  }

  // Add a new product to catalogue
  addProduct(newProduct: Product): Product {
    this.products.update(list => [newProduct, ...list]);
    return newProduct;
  }

  // Add offer to an existing product
  addOfferToProduct(productId: string, offer: FarmerOffer): boolean {
    let success = false;
    this.products.update(list =>
      list.map(prod => {
        if (prod.id === productId) {
          success = true;
          return {
            ...prod,
            offers: [offer, ...prod.offers]
          };
        }
        return prod;
      })
    );
    return success;
  }

  // Update an existing offer
  updateOffer(productId: string, offerId: string, updatedOffer: Partial<FarmerOffer>): boolean {
    let success = false;
    this.products.update(list =>
      list.map(prod => {
        if (prod.id === productId) {
          const updatedOffers = prod.offers.map(off => {
            if (off.id === offerId) {
              success = true;
              return { ...off, ...updatedOffer };
            }
            return off;
          });
          return { ...prod, offers: updatedOffers };
        }
        return prod;
      })
    );
    return success;
  }

  // Delete an offer
  deleteOffer(productId: string, offerId: string): boolean {
    let success = false;
    this.products.update(list =>
      list.map(prod => {
        if (prod.id === productId) {
          success = true;
          return {
            ...prod,
            offers: prod.offers.filter(off => off.id !== offerId)
          };
        }
        return prod;
      })
    );
    return success;
  }

  // Atomic creation of a new product AND its offer
  createProductWithOffer(
    productData: {
      name: string;
      category: 'Fruits' | 'Legumes' | 'Herbes' | 'Epicerie';
      unit: string;
      image: string;
      isBio: boolean;
      isNonTraite: boolean;
      shortDescription: string;
      origin?: string;
    },
    offerData: {
      price: number;
      originalPrice?: number;
      availableStock: number;
      minOrderQuantity?: number;
      harvestDate: string;
      certification?: string;
      deliveryTime?: string;
      farmerNote?: string;
    }
  ): { product: Product; offer: FarmerOffer } {
    const farmer = this.getCurrentFarmer();
    const productId = 'prod-' + Date.now();
    const offerId = 'off-' + Date.now();

    const offer: FarmerOffer = {
      id: offerId,
      farmerId: farmer.id,
      farmerName: farmer.name,
      farmName: farmer.farmName,
      farmerAvatar: farmer.avatar,
      location: farmer.location,
      region: farmer.region,
      distanceKm: 25,
      rating: farmer.rating,
      reviewsCount: farmer.reviewsCount,
      price: offerData.price,
      originalPrice: offerData.originalPrice,
      unit: productData.unit.replace(/[()]/g, '').trim() || 'Kg',
      minOrderQuantity: offerData.minOrderQuantity || 1,
      availableStock: offerData.availableStock,
      harvestDate: offerData.harvestDate || 'Récolté ce matin',
      certification: offerData.certification || (productData.isBio ? '100% Bio Certifié' : 'Agriculture Raisonnée'),
      deliveryTime: offerData.deliveryTime || 'Livraison en 24h',
      farmerNote: offerData.farmerNote || `Cultivé avec soin à ${farmer.location} par ${farmer.farmName}.`,
      isBestPrice: true,
      status: 'Actif'
    };

    const newProduct: Product = {
      id: productId,
      name: productData.name,
      frenchName: productData.name,
      category: productData.category,
      isBio: productData.isBio,
      isNonTraite: productData.isNonTraite,
      unit: productData.unit.startsWith('(') ? productData.unit : `(${productData.unit})`,
      image: productData.image || 'https://images.unsplash.com/photo-1540420773420-3366772f4999?w=600&q=80',
      gallery: [productData.image || 'https://images.unsplash.com/photo-1540420773420-3366772f4999?w=600&q=80'],
      sku: 'AGRI-' + Math.floor(1000 + Math.random() * 9000),
      tags: [productData.category.toUpperCase(), 'FRAIS', 'TERROIR'],
      shortDescription: productData.shortDescription || 'Produit frais récolté directement sur les parcelles de notre terroir.',
      fullDescription: productData.shortDescription || 'Produit frais récolté directement sur les parcelles de notre terroir.',
      additionalInfo: {
        origin: productData.origin || farmer.location,
        conservation: 'Conserver dans un endroit frais et aéré',
        variety: productData.name,
        packaging: 'Caissette aérée recyclable'
      },
      rating: 5.0,
      reviewsCount: 1,
      viewersCount: 5,
      soldLastHours: 1,
      offers: [offer]
    };

    this.addProduct(newProduct);
    return { product: newProduct, offer };
  }

  // --- Harvests Management ---
  getHarvestsByFarmer(farmerId: string): Harvest[] {
    return this.harvests().filter(h => h.farmerId === farmerId);
  }

  getAllHarvests(): Harvest[] {
    return this.harvests();
  }

  addHarvest(harvest: Harvest): void {
    this.harvests.update(list => [harvest, ...list]);
  }

  updateHarvest(harvestId: string, updated: Partial<Harvest>): boolean {
    let success = false;
    this.harvests.update(list =>
      list.map(h => {
        if (h.id === harvestId) {
          success = true;
          return { ...h, ...updated };
        }
        return h;
      })
    );
    return success;
  }

  deleteHarvest(harvestId: string): boolean {
    let success = false;
    this.harvests.update(list => {
      const filtered = list.filter(h => h.id !== harvestId);
      if (filtered.length !== list.length) success = true;
      return filtered;
    });
    return success;
  }

  // Create product with harvest
  createProductWithHarvest(
    productData: {
      name: string;
      category: 'Fruits' | 'Legumes' | 'Herbes' | 'Epicerie';
      unit: string;
      image: string;
      isBio: boolean;
      isNonTraite: boolean;
      shortDescription: string;
      origin?: string;
    },
    harvestData: {
      parcelName: string;
      harvestDate: string;
      quantity: number;
      qualityGrade: string;
      method: string;
      status: 'Planifiée' | 'En cours' | 'Récoltée' | 'Commercialisée';
      notes?: string;
    }
  ): { product: Product; harvest: Harvest } {
    const farmer = this.getCurrentFarmer();
    const productId = 'prod-' + Date.now();
    const harvestId = 'harv-' + Date.now();

    const newProduct: Product = {
      id: productId,
      name: productData.name,
      frenchName: productData.name,
      category: productData.category,
      isBio: productData.isBio,
      isNonTraite: productData.isNonTraite,
      unit: productData.unit.startsWith('(') ? productData.unit : `(${productData.unit})`,
      image: productData.image || 'https://images.unsplash.com/photo-1540420773420-3366772f4999?w=600&q=80',
      gallery: [productData.image || 'https://images.unsplash.com/photo-1540420773420-3366772f4999?w=600&q=80'],
      sku: 'AGRI-' + Math.floor(1000 + Math.random() * 9000),
      tags: [productData.category.toUpperCase(), 'RÉCOLTE', 'TERROIR'],
      shortDescription: productData.shortDescription || 'Produit frais récolté sur nos parcelles.',
      fullDescription: productData.shortDescription || 'Produit frais récolté sur nos parcelles.',
      additionalInfo: {
        origin: productData.origin || farmer.location,
        conservation: 'Conserver dans un endroit frais et aéré',
        variety: productData.name,
        packaging: 'Caisse fermière recyclable'
      },
      rating: 5.0,
      reviewsCount: 0,
      viewersCount: 2,
      soldLastHours: 0,
      offers: []
    };

    const harvest: Harvest = {
      id: harvestId,
      farmerId: farmer.id,
      productId: newProduct.id,
      productName: newProduct.name,
      productImage: newProduct.image,
      productCategory: newProduct.category,
      parcelName: harvestData.parcelName,
      harvestDate: harvestData.harvestDate,
      quantity: harvestData.quantity,
      unit: productData.unit.replace(/[()]/g, '').trim() || 'Kg',
      qualityGrade: harvestData.qualityGrade,
      method: harvestData.method,
      status: harvestData.status,
      notes: harvestData.notes,
      syncedWithStock: false
    };

    this.addProduct(newProduct);
    this.addHarvest(harvest);
    return { product: newProduct, harvest };
  }
}
