package com.yusein.shoplist.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.yusein.shoplist.R
import com.yusein.shoplist.data.ShoppingItem
import com.yusein.shoplist.databinding.FragmentAddEditItemBinding

class AddEditItemFragment : Fragment() {
    private var _binding: FragmentAddEditItemBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ShoppingViewModel by activityViewModels { ShoppingViewModel.Factory }

    private var existingItem: ShoppingItem? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAddEditItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val itemId = requireArguments().getLong(ARG_ITEM_ID)
        if (itemId != 0L) {
            viewModel.loadItem(itemId).observe(viewLifecycleOwner) { item ->
                if (item != null && existingItem == null) {
                    existingItem = item
                    binding.editName.setText(item.name)
                    binding.editQuantity.setText(item.quantity.toString())
                    binding.editCategory.setText(item.category)
                }
            }
        }

        binding.buttonSave.setOnClickListener { save() }
    }

    private fun save() {
        binding.inputName.error = null
        binding.inputQuantity.error = null

        val result = viewModel.save(
            existing = existingItem,
            name = binding.editName.text.toString(),
            quantityText = binding.editQuantity.text.toString(),
            category = binding.editCategory.text.toString(),
        )
        when (result) {
            ValidationResult.Valid -> findNavController().navigateUp()
            ValidationResult.EmptyName -> binding.inputName.error = getString(R.string.error_empty_name)
            ValidationResult.InvalidQuantity -> binding.inputQuantity.error = getString(R.string.error_quantity)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val ARG_ITEM_ID = "itemId"
    }
}