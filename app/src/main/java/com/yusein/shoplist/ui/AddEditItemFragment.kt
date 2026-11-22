package com.yusein.shoplist.ui

import android.content.ActivityNotFoundException
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import coil.load
import coil.transform.RoundedCornersTransformation
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import com.yusein.shoplist.R
import com.yusein.shoplist.data.ShoppingItem
import com.yusein.shoplist.databinding.FragmentAddEditItemBinding
import java.io.File

class AddEditItemFragment : Fragment() {
    private var _binding: FragmentAddEditItemBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ShoppingViewModel by activityViewModels { ShoppingViewModel.Factory }

    private var existingItem: ShoppingItem? = null

    // Снимката на продукта и файлът, в който камерата записва в момента
    private var photoPath: String? = null
    private var pendingPhotoPath: String? = null

    // Camera Intent: отваря приложението за камера и чака резултата
    private val takePicture =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                photoPath = pendingPhotoPath
                showPhoto()
            } else {
                pendingPhotoPath?.let { File(it).delete() }
            }
            pendingPhotoPath = null
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAddEditItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState != null) {
            photoPath = savedInstanceState.getString(KEY_PHOTO_PATH)
            pendingPhotoPath = savedInstanceState.getString(KEY_PENDING_PHOTO_PATH)
        }

        val itemId = requireArguments().getLong(ARG_ITEM_ID)
        if (itemId != 0L) {
            viewModel.loadItem(itemId).observe(viewLifecycleOwner) { item ->
                if (item != null && existingItem == null) {
                    existingItem = item
                    if (savedInstanceState == null) {
                        binding.editName.setText(item.name)
                        binding.editQuantity.setText(item.quantity.toString())
                        binding.editCategory.setText(item.category)
                        photoPath = item.photoPath
                        showPhoto()
                    }
                }
            }
        }

        setupCategoryChips()
        showPhoto()
        binding.buttonTakePhoto.setOnClickListener { openCamera() }
        binding.buttonSave.setOnClickListener { save() }
    }

    private fun openCamera() {
        val context = requireContext()
        val photosDir = File(context.filesDir, "photos").apply { mkdirs() }
        val photoFile = File(photosDir, "photo_${System.currentTimeMillis()}.jpg")
        pendingPhotoPath = photoFile.absolutePath

        // FileProvider дава на камерата временен достъп само до този файл
        val photoUri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", photoFile)
        try {
            takePicture.launch(photoUri)
        } catch (ignored: ActivityNotFoundException) {
            // На телефона няма приложение за камера
            pendingPhotoPath = null
            Snackbar.make(binding.root, R.string.no_camera_app, Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun showPhoto() {
        val path = photoPath
        binding.imagePhoto.isVisible = path != null
        binding.buttonTakePhoto.setText(if (path == null) R.string.take_photo else R.string.retake_photo)
        if (path != null) {
            binding.imagePhoto.load(File(path)) {
                transformations(RoundedCornersTransformation(PHOTO_CORNER_RADIUS))
            }
        }
    }

    private fun setupCategoryChips() {
        resources.getStringArray(R.array.category_suggestions).forEach { category ->
            val chip = Chip(requireContext())
            chip.text = category
            chip.setOnClickListener {
                binding.editCategory.setText(category)
                binding.editCategory.setSelection(category.length)
            }
            binding.chipGroupCategories.addView(chip)
        }
    }

    private fun save() {
        binding.inputName.error = null
        binding.inputQuantity.error = null

        val result =
            viewModel.save(
                existing = existingItem,
                name = binding.editName.text.toString(),
                quantityText = binding.editQuantity.text.toString(),
                category = binding.editCategory.text.toString(),
                photoPath = photoPath,
            )
        when (result) {
            ValidationResult.Valid -> findNavController().navigateUp()
            ValidationResult.EmptyName -> binding.inputName.error = getString(R.string.error_empty_name)
            ValidationResult.InvalidQuantity -> binding.inputQuantity.error = getString(R.string.error_quantity)
        }
    }

    // Пази снимката при завъртане на екрана
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_PHOTO_PATH, photoPath)
        outState.putString(KEY_PENDING_PHOTO_PATH, pendingPhotoPath)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val ARG_ITEM_ID = "itemId"
        private const val KEY_PHOTO_PATH = "photoPath"
        private const val KEY_PENDING_PHOTO_PATH = "pendingPhotoPath"
        private const val PHOTO_CORNER_RADIUS = 32f
    }
}
