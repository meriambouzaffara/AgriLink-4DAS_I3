import {
  Component, OnInit, OnDestroy, signal, computed, inject, PLATFORM_ID
} from '@angular/core';
import { isPlatformBrowser, CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

interface Slide {
  tag: string;
  title: string;
  desc: string;
  image: string;
}

interface ServiceCard {
  icon: string;
  title: string;
  desc: string;
}

interface StatItem {
  icon: string;
  value: number;
  label: string;
  suffix: string;
}

interface FeatureCard {
  icon: string;
  title: string;
  desc: string;
}

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './home.html',
  styleUrl: './home.css'
})
export class Home implements OnInit, OnDestroy {
  private platformId = inject(PLATFORM_ID);
  private slideInterval: any;
  private observerMap = new Map<Element, IntersectionObserver>();

  currentSlide = signal(0);
  animatedStats = signal<{ [key: number]: number }>({});
  visibleSections = signal<Set<string>>(new Set());

  slides: Slide[] = [
    {
      tag: 'De la ferme à votre table',
      title: 'Produits Agricoles\nFrais & Traçables',
      desc: 'AgriLink met en relation producteurs et clients : achetez des produits issus directement des exploitations, avec un suivi complet de leur origine.',
      image: 'https://images.unsplash.com/photo-1625246333195-78d9c38ad449?w=1600&q=80'
    },
    {
      tag: 'Marketplace Agricole',
      title: 'Vendez Vos Récoltes\nEn Quelques Clics',
      desc: 'Les agriculteurs publient leurs produits et offres, gèrent leurs stocks et touchent une clientèle plus large grâce à notre plateforme.',
      image: 'https://images.unsplash.com/photo-1500382017468-9049fed747ef?w=1600&q=80'
    },
    {
      tag: 'Qualité & Certifications',
      title: 'Une Agriculture\nTransparente',
      desc: 'Contrôles qualité, certifications agricoles et avis clients : toute la traçabilité de vos produits, de la récolte à la livraison.',
      image: 'https://images.unsplash.com/photo-1464226184884-fa280b87c399?w=1600&q=80'
    }
  ];

  featureCards: FeatureCard[] = [
    {
      icon: '🛒',
      title: 'Marketplace Agricole',
      desc: 'Achetez et vendez des produits frais directement auprès des producteurs locaux.'
    },
    {
      icon: '🔍',
      title: 'Traçabilité Complète',
      desc: "De la récolte à la livraison, suivez l'origine et le parcours de chaque produit."
    },
    {
      icon: '📦',
      title: 'Commandes Simplifiées',
      desc: 'Passez vos commandes en quelques clics et suivez-les en temps réel.'
    },
    {
      icon: '🚚',
      title: 'Livraisons Organisées',
      desc: 'Transporteurs et clients coordonnés pour des livraisons rapides et fiables.'
    },
    {
      icon: '🤝',
      title: 'Coopératives',
      desc: 'Regroupez-vous en coopératives pour mutualiser collecte, stockage et vente.'
    },
    {
      icon: '🏆',
      title: 'Qualité Certifiée',
      desc: 'Contrôles qualité et certifications agricoles garantissent des produits fiables.'
    }
  ];

  services: ServiceCard[] = [
    {
      icon: '🛒',
      title: 'Marketplace Agricole',
      desc: 'Producteurs et clients : consultez les offres, publiez vos produits et gérez vos stocks en toute simplicité.'
    },
    {
      icon: '📦',
      title: 'Commandes & Suivi',
      desc: 'Passez et suivez vos commandes en temps réel, du panier à la confirmation par le producteur.'
    },
    {
      icon: '🚚',
      title: 'Livraisons Organisées',
      desc: "Transporteurs et clients : attribuez et suivez les livraisons jusqu'à réception des produits."
    },
    {
      icon: '🏆',
      title: 'Qualité & Certifications',
      desc: 'Contrôles qualité et certifications agricoles pour garantir la transparence et la confiance.'
    }
  ];

  stats: StatItem[] = [
    { icon: '🌾', value: 1250, label: 'Produits Publiés', suffix: '' },
    { icon: '👨‍🌾', value: 340, label: 'Producteurs Actifs', suffix: '' },
    { icon: '📦', value: 5800, label: 'Commandes Livrées', suffix: '' },
    { icon: '🤝', value: 45, label: 'Coopératives', suffix: '' }
  ];

  benefits = [
    'Visibilité accrue pour les producteurs',
    'Traçabilité complète des produits',
    'Gestion simplifiée des commandes et livraisons'
  ];

  perks = [
    { icon: '👨‍🌾', title: 'ESPACE PRODUCTEUR' },
    { icon: '🛒', title: 'ESPACE CLIENT' }
  ];

  get currentSlideData() {
    return this.slides[this.currentSlide()];
  }

  ngOnInit() {
    if (isPlatformBrowser(this.platformId)) {
      this.startAutoSlide();
      this.initScrollAnimations();
    }
  }

  ngOnDestroy() {
    this.stopAutoSlide();
    this.observerMap.forEach(obs => obs.disconnect());
  }

  startAutoSlide() {
    this.slideInterval = setInterval(() => {
      this.nextSlide();
    }, 5000);
  }

  stopAutoSlide() {
    if (this.slideInterval) {
      clearInterval(this.slideInterval);
    }
  }

  nextSlide() {
    this.currentSlide.update(i => (i + 1) % this.slides.length);
  }

  prevSlide() {
    this.currentSlide.update(i => (i - 1 + this.slides.length) % this.slides.length);
  }

  goToSlide(index: number) {
    this.currentSlide.set(index);
    this.stopAutoSlide();
    this.startAutoSlide();
  }

  initScrollAnimations() {
    setTimeout(() => {
      const sections = document.querySelectorAll('[data-animate]');
      sections.forEach(el => {
        const observer = new IntersectionObserver(
          (entries) => {
            entries.forEach(entry => {
              if (entry.isIntersecting) {
                const id = (entry.target as HTMLElement).dataset['animate'] || '';
                this.visibleSections.update(s => new Set([...s, id]));
                if (id === 'stats') this.animateCounters();
                observer.unobserve(entry.target);
              }
            });
          },
          { threshold: 0.2 }
        );
        observer.observe(el);
        this.observerMap.set(el, observer);
      });
    }, 100);
  }

  animateCounters() {
    this.stats.forEach((stat, i) => {
      let start = 0;
      const end = stat.value;
      const duration = 2000;
      const step = (end / duration) * 16;
      const timer = setInterval(() => {
        start += step;
        if (start >= end) {
          start = end;
          clearInterval(timer);
        }
        this.animatedStats.update(s => ({ ...s, [i]: Math.floor(start) }));
      }, 16);
    });
  }

  getStatValue(index: number): number {
    return this.animatedStats()[index] ?? 0;
  }

  isVisible(section: string): boolean {
    return this.visibleSections().has(section);
  }

  scrollToServices() {
    document.getElementById('services')?.scrollIntoView({ behavior: 'smooth' });
  }
}
