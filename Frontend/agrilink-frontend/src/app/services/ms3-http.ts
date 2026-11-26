export function developmentCustomerHeader(enabled:boolean,id:number):string {
  if(!enabled||!Number.isSafeInteger(id)||id<=0)throw new Error('Identité client de développement indisponible.');
  return String(id);
}
export function orderHttpErrorMessage(status:number,detail=''):string {
  if(status===0)return 'Le service de commandes est indisponible. Démarrez le backend dev-mock.';
  if(status===400)return detail||'Vérifiez les produits et les quantités saisis.';
  if(status===401||status===403)return 'Accès refusé. Vérifiez votre identité de développement.';
  if(status===404){const id=detail.match(/^Product (\d+) not found$/)?.[1];return id?`Produit ${id} introuvable.`:detail||'Commande ou produit introuvable.';}
  if(status===409){const id=detail.match(/^Insufficient stock for product (\d+)$/)?.[1];return id?`Stock insuffisant pour le produit ${id}.`:detail||'Stock insuffisant ou opération impossible dans cet état.';}
  return 'Le service de commandes a rencontré une erreur. Réessayez plus tard.';
}
