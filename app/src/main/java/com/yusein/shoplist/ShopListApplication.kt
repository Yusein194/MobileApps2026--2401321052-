package com.yusein.shoplist

import android.app.Application
import android.content.Context
import com.yusein.shoplist.data.AppDatabase
import com.yusein.shoplist.data.RoomShoppingRepository
import com.yusein.shoplist.data.ShoppingRepository

class AppContainer(context: Context) {
    private val database = AppDatabase.getInstance(context)
    val repository: ShoppingRepository = RoomShoppingRepository(database.shoppingItemDao())
}

class ShopListApplication : Application() {
    val container: AppContainer by lazy { AppContainer(this) }
}