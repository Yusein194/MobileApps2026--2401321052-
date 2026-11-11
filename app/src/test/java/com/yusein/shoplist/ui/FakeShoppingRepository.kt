package com.yusein.shoplist.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.yusein.shoplist.data.ShoppingItem
import com.yusein.shoplist.data.ShoppingRepository

// Фалшиво repository, което пази данните в паметта вместо в Room
class FakeShoppingRepository : ShoppingRepository {
    val storedItems = mutableListOf<ShoppingItem>()
    private val liveItems = MutableLiveData<List<ShoppingItem>>(emptyList())
    private var nextId = 1L

    override fun getAll(): LiveData<List<ShoppingItem>> = liveItems

    override suspend fun getById(id: Long): ShoppingItem? = storedItems.find { it.id == id }

    override suspend fun insert(item: ShoppingItem): Long {
        val id = if (item.id == 0L) nextId++ else item.id
        storedItems.add(item.copy(id = id))
        publish()
        return id
    }

    override suspend fun update(item: ShoppingItem) {
        val index = storedItems.indexOfFirst { it.id == item.id }
        storedItems[index] = item
        publish()
    }

    override suspend fun delete(item: ShoppingItem) {
        storedItems.removeAll { it.id == item.id }
        publish()
    }

    private fun publish() {
        liveItems.value = storedItems.toList()
    }
}
