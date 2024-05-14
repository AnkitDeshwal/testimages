package com.example.test

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import com.example.test.adapter.TestAdapter
import com.example.test.databinding.ActivityMainBinding
import com.example.test.model.MainClass
import com.example.test.model.Thumbnail
import com.google.gson.Gson
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    var arrayList = ArrayList<Thumbnail>()
    private var TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val jsonString = Constant.jsonString
        val mainClass = parseJson(jsonString)
        Log.d("http", "onCreate: $mainClass")
        Dexter.withContext(this)
            .withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        if (mainClass != null) {
                            for(a in mainClass){
                                arrayList.add(a.thumbnail)
                            }
                        }
                        binding.recyclerView.layoutManager = GridLayoutManager(this@MainActivity, 3)
                        val adapter = TestAdapter(this@MainActivity,arrayList)
                        binding.recyclerView.adapter = adapter

                    } else if (report.isAnyPermissionPermanentlyDenied) {
                        val list = report.deniedPermissionResponses
                        for (s in list) {
                            Log.e(
                                TAG,
                                "onPermissionsChecked: name : isPermanentlyDenied " + s.isPermanentlyDenied
                            )
                            Log.e(TAG, "onPermissionsChecked: name : " + s.permissionName)
                            Log.e(TAG, "onPermissionsChecked: requested : " + s.requestedPermission)
                        }
                        //showPermissionsAlert()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            }).check()
    }

    private fun parseJson(jsonString: String): MainClass? {
        return try {
            val gson = Gson()
            gson.fromJson(jsonString, MainClass::class.java)
        } catch (e: Exception) {
            null
        }
    }
}