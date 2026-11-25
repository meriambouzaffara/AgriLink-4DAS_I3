import { Component, HostListener, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css'
})
export class Navbar {
  private router = inject(Router);

  isScrolled = signal(false);
  mobileMenuOpen = signal(false);
  isHomePage = signal(true);

  navLinks = [
    { label: 'Accueil', href: '/' },
    { label: 'Marketplace', href: '/marketplace' },
    { label: 'Espace Agriculteur', href: '/espace-agriculteur', isFarmer: true },
    { label: 'Offres Producteurs', href: '/product/orange-maltaise-succulente' },
    { label: 'Services', href: '/#services' },
    { label: 'Contact', href: '/#contact' },
  ];

  constructor() {
    this.router.events
      .pipe(filter(event => event instanceof NavigationEnd))
      .subscribe((event: any) => {
        const url = event.urlAfterRedirects || event.url;
        this.isHomePage.set(url === '/' || url === '');
        this.mobileMenuOpen.set(false);
      });
  }

  @HostListener('window:scroll')
  onScroll() {
    this.isScrolled.set(window.scrollY > 40);
  }

  toggleMobile() {
    this.mobileMenuOpen.update(v => !v);
  }
}
