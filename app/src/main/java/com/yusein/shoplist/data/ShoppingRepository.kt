package com.yusein.shoplist.data

import androidx.lifecycle.LiveData

interface ShoppingRepository {
    fun getAll(): LiveData<List<ShoppingItem>>

    suspend fun getById(id: Long): ShoppingItem?

    suspend fun insert(item: ShoppingItem): Long

    suspend fun update(item: ShoppingItem)

    suspend fun delete(item: ShoppingItem)
}

class RoomShoppingRepository(
    private val dao: ShoppingItemDao,
) : ShoppingRepository {
    override fun getAll(): LiveData<List<ShoppingItem>> = dao.getAll()

    override suspend fun getById(id: Long): ShoppingItem? = dao.getById(id)

    override suspend fun insert(item: ShoppingItem): Long = dao.insert(item)

    override suspend fun update(item: ShoppingItem) = dao.update(item)

    override suspend fun delete(item: ShoppingItem) = dao.delete(item)
}