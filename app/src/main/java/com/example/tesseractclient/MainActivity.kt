package com.example.tesseractclient

import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.*
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sdk.IRotation


class MainActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var rotationTextView:TextView
    lateinit var getRotation:Button
    var rotationService: IRotation? = null
    private val Tag = "Client Application"
    private val serverAppUri = "com.example.sdk"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        rotationTextView = findViewById(R.id.rotationTextView)
        getRotation = findViewById(R.id.getRotation)
        getRotation.setOnClickListener(this)
    }

    private fun initConnection() {
        if (rotationService == null) {
            val intent = Intent(IRotation::class.java.getName())
            intent.action = "service.rotation"
            intent.setPackage(serverAppUri)
            bindService(intent, serviceConnection, Service.BIND_AUTO_CREATE)
        }
    }

    private var serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, iBinder: IBinder) {
            rotationService = IRotation.Stub.asInterface(iBinder as IBinder)
            rotationService?.intiateSensor()
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            rotationService = null
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if(serviceConnection != null) {
            unbindService(serviceConnection)
            serviceConnection = null!!
        }
    }

    private fun appInstalledOrNot(uri: String): Boolean {
        val pm = packageManager
        val app_installed: Boolean
        app_installed = try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
        return app_installed
    }

    override fun onResume() {
        super.onResume()
        if (rotationService == null) {
            initConnection()
        }
    }

    override fun onClick(view: View?) {
        if (appInstalledOrNot(serverAppUri)) {
            when(view?.id) {
                R.id.getRotation->{
                    rotationTextView.text = rotationService?.rotation
                }
            }
        }
    }

}