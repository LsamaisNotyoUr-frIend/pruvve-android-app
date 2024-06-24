package com.fluture.pruvve.fragments

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.fluture.pruvve.R
import com.fluture.pruvve.SearchPage
import com.fluture.pruvve.adapters.PitchAdapter
import com.fluture.pruvve.adapters.Pitches
import com.fluture.pruvve.auth.AuthInterceptor
import com.fluture.pruvve.auth.LoginManager
import com.fluture.pruvve.databinding.FragmentBookPitchBinding
import com.fluture.pruvve.essentials.TextManager
import com.fluture.pruvve.retrofittcalls.GetPitches
import com.fluture.pruvve.retrofittcalls.RequestObjects
import com.fluture.pruvve.retrofittcalls.UserService
import okhttp3.OkHttpClient
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class BookPitchFragment : Fragment(R.layout.fragment_book_pitch){
    private lateinit var binding: FragmentBookPitchBinding

    private lateinit var adapter: PitchAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentBookPitchBinding.bind(view)
        LoginManager.init(requireContext())
        super.onViewCreated(view, savedInstanceState)
        val ctx = requireActivity().applicationContext
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))
        val mapView = binding.mvBookPitch
        val tileSource = TileSourceFactory.WIKIMEDIA
        mapView.setTileSource(tileSource)
        mapView.setBuiltInZoomControls(false)
        mapView.setMultiTouchControls(true)
        mapView.setUseDataConnection(true)
        val locationOverlay = MyLocationNewOverlay(mapView)
        locationOverlay.setEnabled(true)
        mapView.getOverlays().add(locationOverlay)
        locationOverlay.enableMyLocation()
        locationOverlay.enableFollowLocation()
        val nigeriaCenter = GeoPoint(9.0579, 7.4951)
        val nextCenter = GeoPoint(8.8834, 7.2302)
        val mapController = mapView.controller
        mapController.setCenter(nigeriaCenter)
        mapController.setZoom(3)
        val startMarker =  Marker(mapView)
        val pitchMarker =  Marker(mapView)
        startMarker.position = nigeriaCenter
        pitchMarker.position = nextCenter
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        mapView.overlays.add(startMarker)
        mapView.overlays.add(pitchMarker)

        binding.tvSearchBar.text = TextManager.getText()

        binding.tvSearchBar.setOnClickListener {
            Intent(requireContext(), SearchPage::class.java).also {
                startActivity(it)
            }
        }

        adapter = PitchAdapter(arrayListOf())
        val recycler = binding.rvPitches
        recycler.adapter = adapter
        recycler.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL, false)
    }

    override fun onStart() {
        super.onStart()

        val token = LoginManager.getToken()
        Log.d("RetrofitToken", token.toString())

        val httpClient = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(token.toString()))
            .build()

        val service = Retrofit.Builder()
            .baseUrl("https://pruvve-backend-9a89de78d2a1.herokuapp.com/api/")
            .client(httpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(UserService::class.java)

        getPitches(service = service)
    }

    private fun getPitches(service: UserService){
        val requestObject = RequestObjects(
            1,
            10
        )
        val pitchList = mutableListOf<Pitches>()
        service.getPitches(requestObject).enqueue(object : Callback<GetPitches>{
            override fun onResponse(call: Call<GetPitches>, response: Response<GetPitches>) {
                if (response.isSuccessful){
                    val list = response.body()?.data?.list
                    if (list != null){
                        for (pitchItems in list){
                            val profUrl = "https://i.pinimg.com/236x/b4/ab/a3/b4aba328a23ff375814d694409a52fa7.jpg"
                            val format = pitchItems.format
                            val surface = pitchItems.surface
                            val facilities = returnFacilities1(pitchItems.hasFreeParking)
                            val facilities2 = returnFacilities2(pitchItems.hasChangingRoom)
                            val facilities3 = returnFacilities3(pitchItems.hasFloodLights)
                            val title = pitchItems.title
                            val address = pitchItems.address
                            val pitches = Pitches(title, address, profUrl, 2, format, surface, facilities, facilities2, facilities3, pitchItems.id, pitchItems.description)
                            pitchList.add(pitches)
                        }
                        adapter.update(pitchList)
                    } else {
                        Log.e("RetrofitListError", "your list is empty")
                    }
                }else{
                    Log.e("RetrofitError", "there was an error ${response.errorBody().toString()}")
                }
            }

            override fun onFailure(call: Call<GetPitches>, t: Throwable) {
                Log.e("RetrofitFailure", "couldn't reach the server ${t.message.toString()}")
            }
        })
    }
    private fun returnFacilities1(boolean: Boolean):String{
        val stringToReturn : String
        if (boolean){
            stringToReturn = "Free Parking"
        }else{
            stringToReturn = "empty"
        }
        return stringToReturn
    }
    private fun returnFacilities2(boolean: Boolean):String{
        val stringToReturn : String
        if (boolean){
            stringToReturn = "Changing room"
        }else{
            stringToReturn = "empty"
        }
        return stringToReturn
    }
    private fun returnFacilities3(boolean: Boolean):String{
        val stringToReturn : String
        if (boolean){
            stringToReturn = "Floodlights"
        }else{
            stringToReturn = "empty"
        }
        return stringToReturn
    }
}