import test from 'node:test';
import assert from 'node:assert/strict';
import { ORDER_STATUS_LABELS, DELIVERY_STAGES, canConfirmOrder, canCancelOrder, orderStageIndex, orderMoney } from '../src/app/components/orders/order-view.ts';

test('uses the six French backend status labels', () => {
  assert.deepEqual(Object.values(ORDER_STATUS_LABELS), [
    'En attente', 'Confirmée', 'En préparation', 'Expédiée', 'Livrée', 'Annulée',
  ]);
});

test('allows customer confirmation only while pending', () => {
  assert.deepEqual(Object.keys(ORDER_STATUS_LABELS).filter(canConfirmOrder), ['PENDING']);
});

test('allows customer cancellation only before preparation', () => {
  assert.deepEqual(Object.keys(ORDER_STATUS_LABELS).filter(canCancelOrder), ['PENDING', 'CONFIRMED']);
});

test('tracks delivery in order and excludes cancellation from delivery progression', () => {
  assert.deepEqual(DELIVERY_STAGES.map(orderStageIndex), [0, 1, 2, 3, 4]);
  assert.equal(orderStageIndex('CANCELLED'), -1);
});

test('formats historical monetary values to three decimals', () => {
  assert.equal(orderMoney(65), '65.000');
  assert.equal(orderMoney(2.5), '2.500');
});
