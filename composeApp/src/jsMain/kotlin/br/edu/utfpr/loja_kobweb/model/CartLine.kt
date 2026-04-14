package br.edu.utfpr.loja_kobweb.model

data class CartLine(
	val product: Product,
	val quantity: Int,
) {
	val subtotal: Double
		get() = product.price * quantity
}

