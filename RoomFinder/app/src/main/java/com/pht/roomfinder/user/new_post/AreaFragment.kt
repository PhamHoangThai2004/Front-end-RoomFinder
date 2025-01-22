package com.pht.roomfinder.user.new_post

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.pht.roomfinder.R
import com.pht.roomfinder.databinding.FragmentAreaBinding
import com.pht.roomfinder.viewmodel.NewPostViewModel

class AreaFragment : Fragment() {
    private lateinit var bin: FragmentAreaBinding
    private lateinit var newPostViewModel: NewPostViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val requestPermissions =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                getNowLocation()
            } else {
                Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bin = FragmentAreaBinding.inflate(inflater, container, false)
        newPostViewModel = ViewModelProvider(requireActivity())[NewPostViewModel::class.java]
        bin.lifecycleOwner = this
        bin.newPostVM = newPostViewModel

        bin.mapViewLocation.onCreate(savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        setSpinner()

        changeSelect()

        newPostViewModel.ward.observe(viewLifecycleOwner) {
            newPostViewModel.getLocation()
        }
        newPostViewModel.village.observe(viewLifecycleOwner) {
            newPostViewModel.getLocation()
        }
        newPostViewModel.locationIsNull.observe(viewLifecycleOwner) {
            if (it == false) {
                setupMap()
            }
        }

        bin.buttonGetLocation.setOnClickListener {
            getNowLocation()
        }

        return bin.root
    }

    private fun setupMap() {
        val latitude = newPostViewModel.latitude.value
        val longitude = newPostViewModel.longitude.value

        bin.mapViewLocation.getMapAsync {
            if (latitude != null && longitude != null) {
                it.clear()
                val location = LatLng(latitude, longitude)
                it.addMarker(MarkerOptions().position(location).title(getString(R.string.location)))
                it.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
            } else {
                Toast.makeText(requireContext(), "Map Error", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkGPS(): Boolean {
        val locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun getNowLocation() {
        if (!checkGPS()) {
            Toast.makeText(requireContext(), "Vui lòng bật GPS", Toast.LENGTH_SHORT).show()
            return
        }
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        newPostViewModel.latitude.value = location.latitude
                        newPostViewModel.longitude.value = location.longitude
                        newPostViewModel.locationIsNull.value = false
                    } else {
                        updateLocationSystem()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        requireContext(),
                        "Failed to get location: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    newPostViewModel.locationIsNull.value = true
                }
        } else {
            // Yêu cầu cấp quyền
            requestPermissions.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    @SuppressLint("MissingPermission")
    private fun updateLocationSystem() {
        val locationRequest  = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000).apply {
            setMinUpdateIntervalMillis(2000)
        }.build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                val location = p0.lastLocation
                if (location != null) {
                    newPostViewModel.latitude.value = location.latitude
                    newPostViewModel.longitude.value = location.longitude
                    newPostViewModel.locationIsNull.value = false
                } else {
                    Toast.makeText(requireContext(), "Có lỗi xảy ra, hãy thử lại", Toast.LENGTH_SHORT).show()
                    newPostViewModel.locationIsNull.value = true
                }
                fusedLocationClient.removeLocationUpdates(this) // Dừng cập nhập
            }
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    private fun changeSelect() {
        bin.spinnerProvinces.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                newPostViewModel.listProvinces.value!![position].let {
                    newPostViewModel.province.value = it.name
                    newPostViewModel.getDistricts(it.code)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

        bin.spinnerDistricts.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                newPostViewModel.listDistricts.value!![position].let {
                    newPostViewModel.district.value = it.name
                    newPostViewModel.getWards(it.code)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

        bin.spinnerWards.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                newPostViewModel.listWards.value!![position].let {
                    newPostViewModel.ward.value = it.name
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }
    }

    private fun setSpinner() {

        newPostViewModel.getProvinces()
        newPostViewModel.listProvinces.observe(viewLifecycleOwner) {
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, it)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            bin.spinnerProvinces.adapter = adapter

            val defaultIndex = it.indexOfFirst { it.name == "Hà Nội" }
            if (defaultIndex != -1) {
                bin.spinnerProvinces.setSelection(defaultIndex)
            }
        }

        newPostViewModel.listDistricts.observe(viewLifecycleOwner) {
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, it)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            bin.spinnerDistricts.adapter = adapter
        }

        newPostViewModel.listWards.observe(viewLifecycleOwner) {
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, it)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            bin.spinnerWards.adapter = adapter
        }
    }


}