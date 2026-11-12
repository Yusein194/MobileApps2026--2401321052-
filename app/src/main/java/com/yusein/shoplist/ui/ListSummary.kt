package com.yusein.shoplist.ui

import com.yusein.shoplist.data.ShoppingItem

data class ListSummary(
    val total: Int,
    val bought: Int,
) {
    val remaining: Int get() = total - bought

    val progressPercent: Int get() = if (total == 0) 0 else bought * FULL_PERCENT / total

    companion object {
        private const val FULL_PERCENT = 100

        fun of(items: List<ShoppingItem>): ListSummary =
            ListSummary(
                total = items.size,
                bought = items.count { it.isBought },
            )
    }
}
