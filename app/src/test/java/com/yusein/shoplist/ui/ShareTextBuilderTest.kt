package com.yusein.shoplist.ui

import com.yusein.shoplist.data.ShoppingItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ShareTextBuilderTest {
    @Test
    fun includesUnboughtItemWithDetails() {
        val items = listOf(ShoppingItem(name = "Мляко", quantity = 2, category = "Храна"))

        val text = ShareTextBuilder.build(items)

        assertEquals("Списък за пазаруване:\n• Мляко x2 (Храна)", text)
    }

    @Test
    fun skipsBoughtItems() {
        val items =
            listOf(
                ShoppingItem(name = "Мляко", quantity = 2, category = "Храна"),
                ShoppingItem(name = "Хляб", quantity = 1, category = "Храна", isBought = true),
            )

        val text = ShareTextBuilder.build(items)

        assertTrue(text.contains("Мляко"))
        assertFalse(text.contains("Хляб"))
    }
}
