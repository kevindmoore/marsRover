package com.raywenderlich.marsrovers

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.raywenderlich.marsrovers.models.Photo
import com.raywenderlich.marsrovers.models.PhotoList
import com.raywenderlich.marsrovers.models.PhotoRow
import com.raywenderlich.marsrovers.models.ROW_TYPE
import com.raywenderlich.marsrovers.recyclerview.PhotoAdapter
import com.raywenderlich.marsrovers.services.NASAPhotos
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    var currentRover = "curiosity"
    var currentRoverPosition = 0
    var currentCameraPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupSpinners()
        // Add Line separator
        recycler_view.addItemDecoration(DividerItemDecoration(this@MainActivity, DividerItemDecoration.VERTICAL))
        recycler_view.layoutManager = LinearLayoutManager(this@MainActivity)
        loadPhotos()
    }

    private fun setupSpinners() {
        setupRoverSpinner()
        setupCameraSpinner()
    }

    private fun setupCameraSpinner() {
        // Camera spinner
        val cameraStrings = resources.getStringArray(R.array.camera_values)
        val cameraAdapter = ArrayAdapter.createFromResource(this, R.array.camera_names, android.R.layout.simple_spinner_item)
        cameraAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        cameras.adapter = cameraAdapter
        cameras.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>) {
            }

            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                if (recycler_view.adapter != null && currentCameraPosition != position) {
                    (recycler_view.adapter as PhotoAdapter).filterCamera(cameraStrings[position])
                }
                currentCameraPosition = position
            }
        }
    }

    private fun setupRoverSpinner() {
        // Setup the spinners for selecting different rovers and cameras
        val roverStrings = resources.getStringArray(R.array.rovers)
        val adapter = ArrayAdapter.createFromResource(this, R.array.rovers, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        rovers.adapter = adapter
        rovers.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>) {
            }

            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                if (currentRoverPosition != position) {
                    currentRover = roverStrings[position].toLowerCase()
                    loadPhotos()
                }
                currentRoverPosition = position
            }
        }
    }

     fun loadPhotos() {
        NASAPhotos.getPhotos(currentRover).enqueue(object : Callback<PhotoList> {
            override fun onFailure(call: Call<PhotoList>?, t: Throwable?) {
                Snackbar.make(recycler_view, R.string.api_error, Snackbar.LENGTH_LONG)
                Log.e(TAG, "Problems getting Photos with error: $t.msg")
            }

            override fun onResponse(call: Call<PhotoList>?, response: Response<PhotoList>?) {
                response?.let { photoResponse ->
                    if (photoResponse.isSuccessful) {
                        Log.d(TAG, "Received ${photoResponse.body()!!.photos.size} photos")
                        if (recycler_view.adapter == null) {
                            val adapter = PhotoAdapter(sortPhotos(photoResponse.body()!!))
                            recycler_view.adapter = adapter
                            val touchHandler = ItemTouchHelper(SwipeHandler(adapter, 0, (ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)))
                            touchHandler.attachToRecyclerView(recycler_view)
                        } else {
                            (recycler_view.adapter as PhotoAdapter).updatePhotos(sortPhotos(photoResponse.body()!!))
                        }
                    }
                }
            }
        })
    }

    fun sortPhotos(photoList: PhotoList) : ArrayList<PhotoRow> {
        val map = HashMap<String, ArrayList<Photo>>()
        for (photo in photoList.photos) {
            var photos = map[photo.camera.full_name]
            if (photos == null) {
                photos = ArrayList<Photo>()
                map[photo.camera.full_name] = photos
            }
            photos.add(photo)
        }
        val newPhotos = ArrayList<PhotoRow>()
        for ((key, value) in map) {
            newPhotos.add(PhotoRow(ROW_TYPE.HEADER, null, key))
            for (photo in value) {
                newPhotos.add(PhotoRow(ROW_TYPE.PHOTO, photo, null))
            }
        }
        return newPhotos
    }

    /**
     * SwipeHandler. Romoves the row  when swiping left or right
     */
    class SwipeHandler(val adapter: PhotoAdapter, dragDirs : Int, swipeDirs : Int) : ItemTouchHelper.SimpleCallback(dragDirs, swipeDirs) {
        override fun onMove(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?, target: RecyclerView.ViewHolder?): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            adapter.removeRow(viewHolder.adapterPosition)
        }

    }
    companion object {
        const val TAG = "MarsRover"
    }
}
