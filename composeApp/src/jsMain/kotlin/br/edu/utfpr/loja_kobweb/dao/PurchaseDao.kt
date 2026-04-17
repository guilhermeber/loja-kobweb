package br.edu.utfpr.loja_kobweb.dao

import br.edu.utfpr.loja_kobweb.model.Purchase

object PurchaseDao {
    val purchases = DaoState.purchases

    fun checkout(paymentMethod: String): Purchase? {
        val lines = CartDao.cartLines()
        if (lines.isEmpty()) {
            return null
        }

        val hasInvalidStock = lines.any { line ->
            val currentProduct = ProductDao.findById(line.product.id)
            currentProduct == null || line.quantity > currentProduct.stock
        }
        if (hasInvalidStock) {
            return null
        }

        lines.forEach { line ->
            val reduced = ProductDao.reduceStock(line.product.id, line.quantity)
            if (!reduced) {
                return null
            }
        }

        val purchase = Purchase(
            id = DaoState.nextPurchaseId++,
            summary = lines.joinToString(", ") { "${it.quantity}x ${it.product.name}" },
            total = lines.sumOf { it.subtotal },
            paymentMethod = paymentMethod
        )

        purchases.add(purchase)
        DaoState.cart.clear()
        DaoState.persist()
        return purchase
    }
}
