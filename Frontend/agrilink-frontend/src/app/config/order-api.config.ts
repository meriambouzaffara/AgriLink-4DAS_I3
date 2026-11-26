import { InjectionToken, isDevMode } from '@angular/core';
export interface OrderApiConfig { baseUrl: string; developmentMode: boolean; developmentCustomerId: number; }
// Override this token when verified production integrations become available.
export const ORDER_API_CONFIG = new InjectionToken<OrderApiConfig>('ORDER_API_CONFIG', {
  providedIn: 'root', factory: () => ({ baseUrl: 'http://localhost:8083', developmentMode: isDevMode(), developmentCustomerId: 12 })
});
