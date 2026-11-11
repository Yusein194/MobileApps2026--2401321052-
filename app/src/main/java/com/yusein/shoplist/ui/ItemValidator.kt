package com.yusein.shoplist.ui

import com.yusein.shoplist.data.ShoppingItem

sealed interface ValidationResult {
    data object Valid : ValidationResult

    data object EmptyName : ValidationResult

    data object InvalidQuantity : ValidationResult
}

object ItemValidator {
    fun validate(
        name: String,
        quantityText: String,
    ): ValidationResult {
        val quantity = quantityText.trim().toIntOrNull()
        return when {
            name.isBlank() -> ValidationResult.EmptyName
            quantity == null || quantity <= 0 -> ValidationResult.InvalidQuantity
            else -> ValidationResult.Valid
        }
    }
}

object ShareTextBuilder {
    fun build(items: List<ShoppingItem>): String =
        items
            .filter { !it.isBought }
            .joinToString(separator = "\n", prefix = "Списък за пазаруване:\n") {
                "• ${it.name} x${it.quantity} (${it.category})"
            }
}
