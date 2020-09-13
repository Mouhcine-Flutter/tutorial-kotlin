package com.example.kotlin_audioplayer

import android.Manifest
import android.app.Dialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import java.util.concurrent.atomic.AtomicReference


class MainActivity : AppCompatActivity() {

    private val viewModel: AudioFilesViewModel
        get() {
           return ViewModelProvider(this)[AudioFilesViewModel::class.java]
        }
    val player by lazy {
        PlayerServiceWrapper(this, viewModel, actualService)
    }
    private var actualService: AtomicReference<PlayerService?> = AtomicReference(null)
    private val dialog = AskPermissionsDialog(::requestPerm)
    private val hasPermission
        get() = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bindService(
            Intent(this, PlayerService::class.java),
            PlayerServiceConnection(),
            Context.BIND_AUTO_CREATE
        )
        setContentView(R.layout.activity_main)
        if (hasPermission)
            showAppFragment()
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
        1
    )

    override fun onResume() {
        super.onResume()
        val currentFragment = supportFragmentManager
            .findFragmentById(R.id.fragment_container)
        if (currentFragment !is AudioFileListFragment && hasPermission)
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
        if (result == PackageManager.PERMISSION_GRANTED) {
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
            if (showRequestRationnale) {
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
    ) : DialogFragment() {
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

    inner class PlayerServiceConnection : ServiceConnection {
        override fun onServiceConnected(
            name: ComponentName?,
            service: IBinder?
        ) = actualService.set(
            (service as? PlayerService.Binder)?.service
        )

        override fun onServiceDisconnected(name: ComponentName?) = actualService.set(null)
    }

}