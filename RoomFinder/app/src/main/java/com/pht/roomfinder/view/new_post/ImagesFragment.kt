package com.pht.roomfinder.view.new_post

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.pht.roomfinder.adapters.ChoiceImgAdapter
import com.pht.roomfinder.databinding.FragmentImagesBinding
import com.pht.roomfinder.utils.Const
import com.pht.roomfinder.viewmodel.NewPostViewModel

class ImagesFragment : Fragment() {
    private lateinit var bin: FragmentImagesBinding
    private lateinit var newPostViewModel: NewPostViewModel
    private var listImages = mutableListOf<Uri>()
    private lateinit var adapter: ChoiceImgAdapter

    @SuppressLint("NotifyDataSetChanged")
    private val selectImage =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                val imageUri = data?.data
                imageUri?.let {
                    val imagePath = Const.changeToPathFile(it, requireContext())
                    if (imagePath != null) {
                        val listImagesPath = newPostViewModel.images.value ?: mutableListOf()
                        listImagesPath.add(imagePath)
                        newPostViewModel.images.value = listImagesPath
                        listImages.add(it)
                        adapter.notifyDataSetChanged()
                    }
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        bin = FragmentImagesBinding.inflate(inflater, container, false)
        newPostViewModel = ViewModelProvider(requireActivity())[NewPostViewModel::class.java]
        bin.newPostVM = newPostViewModel
        bin.lifecycleOwner = viewLifecycleOwner

        setImagesAdapter()

        bin.textViewChoiceImages.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK).apply {
                type = "image/*"
            }
            selectImage.launch(intent)
        }

        return bin.root
    }

    private fun setImagesAdapter() {
        adapter = ChoiceImgAdapter(listImages)
        bin.recyclerViewImagesList.layoutManager = GridLayoutManager(requireContext(), 3)
        bin.recyclerViewImagesList.adapter = adapter
    }

}