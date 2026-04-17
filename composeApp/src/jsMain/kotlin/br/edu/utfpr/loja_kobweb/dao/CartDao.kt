package br.edu.utfpr.loja_kobweb.dao

import br.edu.utfpr.loja_kobweb.model.CartLine
import br.edu.utfpr.loja_kobweb.model.Product

object CartDao {
    val cart = DaoState.cart

    fun addToCart(product: Product) {
        val currentProduct = ProductDao.findById(product.id) ?: return
        if (cart.count { it.id == currentProduct.id } >= currentProduct.stock) {
            return
        }

        cart.add(currentProduct)
        DaoState.persist()
    }

    fun removeFromCart(product: Product) {
        val removed = cart.remove(product)
        if (removed) {
            DaoState.persist()
        }
    }

    fun clearCart() {
        if (cart.isEmpty()) return
        cart.clear()
        DaoState.persist()
    }

    fun cartLines(): List<CartLine> {
        return cart
            .groupBy { it.id }
            .mapNotNull { (_, items) -> items.firstOrNull()?.let { CartLine(it, items.size) } }
            .sortedBy { it.product.name }
    }

    fun cartTotal(): Double = cartLines().sumOf { it.subtotal }
}