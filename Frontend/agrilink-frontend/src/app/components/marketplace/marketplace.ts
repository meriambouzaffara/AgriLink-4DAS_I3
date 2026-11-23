import { Component, computed, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ProductService } from '../../services/product.service';
import { Product } from '../../models/product.model';

@Component({
  selector: 'app-marketplace',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './marketplace.html',
  styleUrl: './marketplace.css'
})
export class Marketplace {
  private productService = inject(ProductService);

  // Filters & State
  selectedCategory = signal<string>('Fruits');
  searchQuery = signal<string>('');
  onlyBio = signal<boolean>(false);
  onlyNonTraite = signal<boolean>(false);
  sortBy = signal<string>('popular');

  categories = [
    { id: 'Tous les produits', label: 'Tous les produits', icon: '✨', count: 8 },
    { id: 'Bio / Non Traité', label: 'Bio / Non Traité', icon: '🌱', count: 7 },
    { id: 'Recettes', label: 'Paniers de Saison', icon: '🧺', count: 3 },
    { id: 'Fruits', label: 'Fruits', icon: '🍎', count: 5 },
    { id: 'Legumes', label: 'Légumes', icon: '🥬', count: 2 },
    { id: 'Herbes', label: 'Herbes Aromatiques', icon: '🌿', count: 2 },
    { id: 'Epicerie', label: 'Épicerie Fermière', icon: '🍯', count: 2 }
  ];

  allProducts = computed(() => this.productService.getProducts());

  filteredProducts = computed(() => {
    let list = this.allProducts();
    const cat = this.selectedCategory();
    const query = this.searchQuery().trim().toLowerCase();
    const bio = this.onlyBio();
    const nonTraite = this.onlyNonTraite();

    if (cat === 'Fruits') {
      list = list.filter(p => p.category === 'Fruits');
    } else if (cat === 'Legumes') {
      list = list.filter(p => p.category === 'Legumes');
    } else if (cat === 'Epicerie') {
      list = list.filter(p => p.category === 'Epicerie');
    } else if (cat === 'Herbes') {
      list = list.filter(p => p.category === 'Herbes');
    } else if (cat === 'Bio / Non Traité') {
      list = list.filter(p => p.isBio || p.isNonTraite);
    }

    if (query) {
      list = list.filter(p =>
        p.name.toLowerCase().includes(query) ||
        p.tags.some(t => t.toLowerCase().includes(query)) ||
        p.offers.some(o => o.farmerName.toLowerCase().includes(query) || o.location.toLowerCase().includes(query))
      );
    }

    if (bio) {
      list = list.filter(p => p.isBio);
    }

    if (nonTraite) {
      list = list.filter(p => p.isNonTraite);
    }

    // Sort
    if (this.sortBy() === 'offers') {
      list = [...list].sort((a, b) => b.offers.length - a.offers.length);
    } else if (this.sortBy() === 'rating') {
      list = [...list].sort((a, b) => b.rating - a.rating);
    }

    return list;
  });

  selectCategory(catId: string) {
    this.selectedCategory.set(catId);
  }

  clearFilters() {
    this.selectedCategory.set('Tous les produits');
    this.searchQuery.set('');
    this.onlyBio.set(false);
    this.onlyNonTraite.set(false);
  }

  removeCategoryFilter() {
    this.selectedCategory.set('Tous les produits');
  }

  getFarmersCount(product: Product): number {
    return product.offers.length;
  }
}
