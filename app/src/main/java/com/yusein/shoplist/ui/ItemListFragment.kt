package com.yusein.shoplist.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.yusein.shoplist.R
import com.yusein.shoplist.databinding.FragmentItemListBinding

class ItemListFragment : Fragment() {
    private var _binding: FragmentItemListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ShoppingViewModel by activityViewModels { ShoppingViewModel.Factory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentItemListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = ShoppingAdapter(
            onItemClick = { item -> openEditor(item.id) },
            onBoughtClick = { item -> viewModel.toggleBought(item) },
        )
        binding.recyclerView.adapter = adapter
        attachSwipeToDelete(adapter)
        setupShareMenu()

        // Observer pattern: списъкът се обновява сам при промяна в базата
        viewModel.items.observe(viewLifecycleOwner) { items ->
            adapter.submitList(items)
            binding.emptyText.isVisible = items.isEmpty()
        }

        binding.fabAdd.setOnClickListener { openEditor(0L) }
    }

    private fun setupShareMenu() {
        // viewLifecycleOwner премахва менюто, когато излезем от този екран
        requireActivity().addMenuProvider(
            object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.menu_list, menu)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    if (menuItem.itemId != R.id.action_share) return false
                    shareList()
                    return true
                }
            },
            viewLifecycleOwner,
            Lifecycle.State.RESUMED,
        )
    }

    private fun shareList() {
        val items = viewModel.items.value.orEmpty()
        if (items.all { it.isBought }) {
            Snackbar.make(binding.root, R.string.nothing_to_share, Snackbar.LENGTH_SHORT).show()
            return
        }

        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, ShareTextBuilder.build(items))
        }
        startActivity(Intent.createChooser(sendIntent, getString(R.string.share_list)))
    }

    private fun openEditor(itemId: Long) {
        findNavController().navigate(
            R.id.action_list_to_addEdit,
            bundleOf(AddEditItemFragment.ARG_ITEM_ID to itemId),
        )
    }

    private fun attachSwipeToDelete(adapter: ShoppingAdapter) {
        val callback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder,
            ) = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val item = adapter.itemAt(viewHolder.bindingAdapterPosition)
                viewModel.delete(item)
                Snackbar.make(binding.root, R.string.item_deleted, Snackbar.LENGTH_LONG)
                    .setAction(R.string.undo) { viewModel.restore(item) }
                    .show()
            }
        }
        ItemTouchHelper(callback).attachToRecyclerView(binding.recyclerView)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}