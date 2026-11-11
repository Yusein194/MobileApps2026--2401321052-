package com.yusein.shoplist.ui

import org.junit.Assert.assertEquals
import org.junit.Test

class ItemValidatorTest {
    @Test
    fun validInputReturnsValid() {
        assertEquals(ValidationResult.Valid, ItemValidator.validate("Мляко", "2"))
    }

    @Test
    fun quantityWithSpacesIsValid() {
        assertEquals(ValidationResult.Valid, ItemValidator.validate("Мляко", " 2 "))
    }

    @Test
    fun emptyNameReturnsEmptyName() {
        assertEquals(ValidationResult.EmptyName, ItemValidator.validate("", "2"))
    }

    @Test
    fun blankNameReturnsEmptyName() {
        assertEquals(ValidationResult.EmptyName, ItemValidator.validate("   ", "2"))
    }

    @Test
    fun emptyQuantityReturnsInvalidQuantity() {
        assertEquals(ValidationResult.InvalidQuantity, ItemValidator.validate("Мляко", ""))
    }

    @Test
    fun textQuantityReturnsInvalidQuantity() {
        assertEquals(ValidationResult.InvalidQuantity, ItemValidator.validate("Мляко", "abc"))
    }

    @Test
    fun zeroQuantityReturnsInvalidQuantity() {
        assertEquals(ValidationResult.InvalidQuantity, ItemValidator.validate("Мляко", "0"))
    }

    @Test
    fun negativeQuantityReturnsInvalidQuantity() {
        assertEquals(ValidationResult.InvalidQuantity, ItemValidator.validate("Мляко", "-1"))
    }
}
