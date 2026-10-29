package com.yusein.shoplist.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.yusein.shoplist.ShopListApplication
import com.yusein.shoplist.data.ShoppingItem
import com.yusein.shoplist.data.ShoppingRepository
import kotlinx.coroutines.launch

class ShoppingViewModel(
    private val repository: ShoppingRepository,
) : ViewModel() {
    val items: LiveData<List<ShoppingItem>> = repository.getAll()

    fun loadItem(id: Long): LiveData<ShoppingItem?> = liveData { emit(repository.getById(id)) }

    // existing == null -> нов продукт (Create), иначе редакция (Update)
    fun save(
        existing: ShoppingItem?,
        name: String,
        quantityText: String,
        category: String,
    ): ValidationResult {
        val result = ItemValidator.validate(name, quantityText)
        if (result != ValidationResult.Valid) return result

        val quantity = quantityText.trim().toInt()
        val item = existing?.copy(name = name.trim(), quantity = quantity, category = category.trim())
            ?: ShoppingItem(name = name.trim(), quantity = quantity, category = category.trim())

        viewModelScope.launch {
            if (existing == null) repository.insert(item) else repository.update(item)
        }
        return result
    }

    fun toggleBought(item: ShoppingItem) {
        viewModelScope.launch { repository.update(item.copy(isBought = !item.isBought)) }
    }

    fun delete(item: ShoppingItem) {
        viewModelScope.launch { repository.delete(item) }
    }

    fun restore(item: ShoppingItem) {
        viewModelScope.launch { repository.insert(item) }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as ShopListApplication
                ShoppingViewModel(app.container.repository)
            }
        }
    }
}