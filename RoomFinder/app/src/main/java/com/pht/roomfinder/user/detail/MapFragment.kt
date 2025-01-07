package com.pht.roomfinder.user.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.pht.roomfinder.R
import com.pht.roomfinder.databinding.FragmentMapBinding
import com.pht.roomfinder.viewmodel.DetailViewModel

class MapFragment : Fragment() {
    private lateinit var bin: FragmentMapBinding
    private lateinit var detailViewModel: DetailViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        detailViewModel = ViewModelProvider(requireActivity())[DetailViewModel::class.java]
        bin = FragmentMapBinding.inflate(inflater, container, false)
        bin.lifecycleOwner = this
        bin.detailViewModel = detailViewModel

        bin.mapViewLocation.onCreate(savedInstanceState)

        detailViewModel.postDetail.observe(viewLifecycleOwner) {
            setupMap()
        }

        return bin.root
    }

    private fun setupMap() {
        val latitude = detailViewModel.postDetail.value?.location?.latitude
        val longitude = detailViewModel.postDetail.value?.location?.longitude

        bin.mapViewLocation.getMapAsync {
            if (latitude != null && longitude != null) {
                val location = LatLng(latitude, longitude)
                it.addMarker(MarkerOptions().position(location).title(getString(R.string.location)))
                it.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
            } else {
                Toast.makeText(requireContext(), "Map Error", Toast.LENGTH_SHORT).show()
            }
        }
    }


}