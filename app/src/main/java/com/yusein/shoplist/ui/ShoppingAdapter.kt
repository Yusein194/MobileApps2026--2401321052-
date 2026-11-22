package com.yusein.shoplist.ui

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.dispose
import coil.load
import coil.transform.RoundedCornersTransformation
import com.yusein.shoplist.R
import com.yusein.shoplist.data.ShoppingItem
import com.yusein.shoplist.databinding.ItemShoppingBinding
import java.io.File

class ShoppingAdapter(
    private val onItemClick: (ShoppingItem) -> Unit,
    private val onBoughtClick: (ShoppingItem) -> Unit,
) : ListAdapter<ShoppingItem, ShoppingAdapter.ViewHolder>(DiffCallback) {
    class ViewHolder(
        val binding: ItemShoppingBinding,
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        val binding = ItemShoppingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
    ) {
        val item = getItem(position)
        with(holder.binding) {
            textName.text = item.name
            textName.paintFlags =
                if (item.isBought) {
                    textName.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                } else {
                    textName.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                }
            textCategory.text = item.category.ifBlank { root.context.getString(R.string.no_category) }
            textQuantity.text = root.context.getString(R.string.item_quantity, item.quantity)
            root.alpha = if (item.isBought) BOUGHT_ALPHA else 1f
            showThumbnail(imageThumb, item.photoPath)
            checkBought.isChecked = item.isBought
            checkBought.setOnClickListener { onBoughtClick(item) }
            root.setOnClickListener { onItemClick(item) }
        }
    }

    private fun showThumbnail(
        imageView: ImageView,
        photoPath: String?,
    ) {
        imageView.isVisible = photoPath != null
        if (photoPath == null) {
            imageView.dispose()
        } else {
            imageView.load(File(photoPath)) {
                transformations(RoundedCornersTransformation(THUMB_CORNER_RADIUS))
            }
        }
    }

    fun itemAt(position: Int): ShoppingItem = getItem(position)

    private object DiffCallback : DiffUtil.ItemCallback<ShoppingItem>() {
        override fun areItemsTheSame(
            oldItem: ShoppingItem,
            newItem: ShoppingItem,
        ) = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: ShoppingItem,
            newItem: ShoppingItem,
        ) = oldItem == newItem
    }

    private companion object {
        // Купените продукти са леко избледнели
        const val BOUGHT_ALPHA = 0.55f
        const val THUMB_CORNER_RADIUS = 16f
    }
}
