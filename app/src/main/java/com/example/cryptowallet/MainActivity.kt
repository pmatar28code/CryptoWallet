package com.example.cryptowallet

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import com.example.cryptowallet.databinding.ActivityMainBinding
import com.example.cryptowallet.fragments.*
import com.example.cryptowallet.network.classesapi.AccessToken
import com.example.cryptowallet.utilities.EncSharedPreferences
import com.google.android.material.bottomsheet.BottomSheetDialog

class MainActivity : AppCompatActivity() {
    companion object {
        const val MY_CLIENT_ID = "c77416def5b58698219596f44ecf6236658c426805a522d517f45867b0348188"
        const val CLIENT_SECRET = "311b687baee92bbd8e584527bb757f27c9ed363f7a3922a18b381bcd4309b5b4"
        const val MY_REDIRECT_URI = "urn:ietf:wg:oauth:2.0:oob"
        const val keyStringAccesskey = "Access_key"
        const val keyStringCode = "Auth_code"
        var codeFromShared:String ?= null
        var stringTokenFromShared:String ?= null
        var accessTokenFromShared:AccessToken ?= null

        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = LayoutInflater.from(this)
        val binding = ActivityMainBinding.inflate(inflater)
        setContentView(binding.root)

        if (allPermissionsGranted()) {
        } else {
            ActivityCompat.requestPermissions(
                this,REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }
        codeFromShared = EncSharedPreferences.getValueString(keyStringCode,applicationContext)
        stringTokenFromShared = EncSharedPreferences.getValueString(
            keyStringAccesskey,applicationContext
        )

        if (codeFromShared == null || codeFromShared == "") {
            binding.bottomNavigationContainer.isGone = true

            swapFragments(AuthorizationFragment())
        } else {
            stringTokenFromShared = EncSharedPreferences.getValueString(
                keyStringAccesskey,applicationContext
            )

            binding.apply {
                bottomNavigationContainer.setOnNavigationItemSelectedListener {
                    handleBottomNavigation(it.itemId, binding)
                }
            }
            swapFragments(WalletFragment())
        }
    }
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
            } else {
                Toast.makeText(this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun handleBottomNavigation(
        menuItemId: Int, binding: ActivityMainBinding
    ): Boolean = when (menuItemId) {

        R.id.menu_wallet -> {
            swapFragments(WalletFragment())
            true
        }
        R.id.menu_request -> {
            Repository.accounts.clear()
            swapFragments(RequestFragment())
            true
        }
        R.id.menu_send -> {
            Repository.accounts.clear()
            swapFragments(SendFragment())
            true
        }
        R.id.menu_transactions -> {
            swapFragments(ShowTransactionsFragment())
            true
        }
        R.id.menu_other -> {
            val dialog = BottomSheetDialog(this)
            val view = layoutInflater.inflate(R.layout.bottom_sheet_menu, null)
            val btnClose = view.findViewById<Button>(R.id.idBtnDismiss)
            val btnConfirm = view.findViewById<Button>(R.id.button_confirm)

            btnClose.setOnClickListener {
                dialog.dismiss()
            }
            btnConfirm.setOnClickListener {
                EncSharedPreferences.saveToEncryptedSharedPrefsString(
                    keyStringCode,"",this@MainActivity
                )
                val intent = Intent(
                    this@MainActivity, MainActivity::class.java
                )
                startActivity(intent)
            }
            dialog.setCancelable(false)
            dialog.setContentView(view)
            dialog.show()
            true
        }
        else -> false
    }
    private fun swapFragments(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack("back")
            .commit()
    }
}
