package com.example.kotlin_audioplayer

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment


class MainActivity : AppCompatActivity() {

    private val dialog = AskPermissionsDialog(::requestPerm)
    private val hasPermission
        get() = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if(hasPermission) showAppFragment()
        else {
            requestPerm()
        }
    }

    fun showAppFragment() {
        val transaction = supportFragmentManager.beginTransaction()
        val fragment = AudioFileListFragment()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.commit()
    }


    private fun requestPerm() = ActivityCompat.requestPermissions(
        this,
        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
        0
    )
    override fun onResume() {
        super.onResume()
        val currentFragment = supportFragmentManager
            .findFragmentById(R.id.fragment_container)
        if(currentFragment !is AudioFileListFragment && hasPermission)
            showAppFragment()
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        val permIdx = permissions.indexOf(
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        val result = grantResults.getOrElse(permIdx) {
            PackageManager.PERMISSION_DENIED
        }
        // Si nous avons la permission, nous affichons l'application
        if(result == PackageManager.PERMISSION_GRANTED) {
            showAppFragment()
        }
        // Sinon, si le système nous indique que nous
        // devons afficher un message alors nous l'affichons
        else {
            val showRequestRationnale = ActivityCompat
                .shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            if(showRequestRationnale) {
                dialog.show(
                    supportFragmentManager,
                    AskPermissionsDialog::class.simpleName
                )
            } else {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri: Uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)

            }
        }
    }

    class AskPermissionsDialog(
        private val requestPerm: () -> Unit
    ): DialogFragment() {
        override fun onCreateDialog(
            savedInstanceState: Bundle?
        ): Dialog {
            val builder = AlertDialog.Builder(requireContext())

            builder.apply {
                setMessage("le fonctionnement optimale de l'application nécessite votre permission")
                setPositiveButton("Ok") { _, _ ->
                    requestPerm()
                }
            }
            val dialog: AlertDialog = builder.create()
            return dialog
        }
    }

}