package com.yusein.shoplist.ui

import com.yusein.shoplist.data.ShoppingItem
import org.junit.Assert.assertEquals
import org.junit.Test

class ListSummaryTest {
    @Test
    fun emptyListHasZeroProgress() {
        val summary = ListSummary.of(emptyList())

        assertEquals(0, summary.total)
        assertEquals(0, summary.remaining)
        assertEquals(0, summary.progressPercent)
    }

    @Test
    fun countsBoughtAndRemainingItems() {
        val items =
            listOf(
                ShoppingItem(name = "Мляко", quantity = 2, category = "Храна", isBought = true),
                ShoppingItem(name = "Хляб", quantity = 1, category = "Храна"),
                ShoppingItem(name = "Кафе", quantity = 1, category = "Напитки"),
                ShoppingItem(name = "Сапун", quantity = 3, category = "Хигиена", isBought = true),
            )

        val summary = ListSummary.of(items)

        assertEquals(4, summary.total)
        assertEquals(2, summary.bought)
        assertEquals(2, summary.remaining)
        assertEquals(50, summary.progressPercent)
    }

    @Test
    fun allBoughtGivesFullProgress() {
        val items = listOf(ShoppingItem(name = "Мляко", quantity = 2, category = "Храна", isBought = true))

        assertEquals(100, ListSummary.of(items).progressPercent)
    }
}
