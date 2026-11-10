package com.yusein.shoplist.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ShoppingViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var repository: FakeShoppingRepository
    private lateinit var viewModel: ShoppingViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        repository = FakeShoppingRepository()
        viewModel = ShoppingViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun saveNewItemInsertsTrimmedValues() {
        val result = viewModel.save(null, "  Мляко ", " 2 ", " Храна ")

        assertEquals(ValidationResult.Valid, result)
        val saved = repository.storedItems.single()
        assertEquals("Мляко", saved.name)
        assertEquals(2, saved.quantity)
        assertEquals("Храна", saved.category)
    }

    @Test
    fun saveWithEmptyNameDoesNotInsert() {
        val result = viewModel.save(null, "", "2", "Храна")

        assertEquals(ValidationResult.EmptyName, result)
        assertTrue(repository.storedItems.isEmpty())
    }

    @Test
    fun saveWithInvalidQuantityDoesNotInsert() {
        val result = viewModel.save(null, "Мляко", "0", "Храна")

        assertEquals(ValidationResult.InvalidQuantity, result)
        assertTrue(repository.storedItems.isEmpty())
    }

    @Test
    fun saveExistingItemUpdatesItAndKeepsBoughtState() {
        viewModel.save(null, "Мляко", "2", "Храна")
        viewModel.toggleBought(repository.storedItems.single())
        val existing = repository.storedItems.single()

        viewModel.save(existing, "Кисело мляко", "3", "Храна")

        val updated = repository.storedItems.single()
        assertEquals("Кисело мляко", updated.name)
        assertEquals(3, updated.quantity)
        assertTrue(updated.isBought)
    }

    @Test
    fun toggleBoughtSwitchesState() {
        viewModel.save(null, "Мляко", "2", "Храна")

        viewModel.toggleBought(repository.storedItems.single())
        assertTrue(repository.storedItems.single().isBought)

        viewModel.toggleBought(repository.storedItems.single())
        assertFalse(repository.storedItems.single().isBought)
    }

    @Test
    fun deleteRemovesItemAndRestoreBringsItBack() {
        viewModel.save(null, "Мляко", "2", "Храна")
        val item = repository.storedItems.single()

        viewModel.delete(item)
        assertTrue(repository.storedItems.isEmpty())

        viewModel.restore(item)
        assertEquals(item, repository.storedItems.single())
    }

    @Test
    fun itemsLiveDataReflectsRepository() {
        viewModel.save(null, "Мляко", "2", "Храна")

        assertEquals("Мляко", viewModel.items.value?.single()?.name)
    }
}