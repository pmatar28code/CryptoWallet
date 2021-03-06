package com.example.cryptowallet

import android.Manifest
import android.content.ContentProvider
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuItem
import android.webkit.CookieManager
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import com.example.cryptowallet.databinding.ActivityMainBinding
import com.example.cryptowallet.fragments.*
import com.example.cryptowallet.network.networkcalls.LogoutNetwork
import com.example.cryptowallet.network.networkcalls.UserNetwork
import com.example.cryptowallet.utilities.EncSharedPreferences
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import android.webkit.ValueCallback

import android.webkit.CookieSyncManager




@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    companion object {
        const val MY_CLIENT_ID = BuildConfig.MY_CLIENT_ID//"c77416def5b58698219596f44ecf6236658c426805a522d517f45867b0348188"
        const val CLIENT_SECRET = BuildConfig.CLIENT_SECRET//"311b687baee92bbd8e584527bb757f27c9ed363f7a3922a18b381bcd4309b5b4"
        const val MY_REDIRECT_URI = BuildConfig.MY_REDIRECT_URI//"urn:ietf:wg:oauth:2.0:oob"
        const val keyStringAccesskey = "Access_key"
        const val keyStringCode = "Auth_code"
        var codeFromShared:String ?= null
        var stringTokenFromShared:String ?= null

        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
    @Inject
    lateinit var logoutNetwork: LogoutNetwork

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = LayoutInflater.from(this)
        val binding = ActivityMainBinding.inflate(inflater)
        setContentView(binding.root)

        initializeMenuDrawerAndTopAppBar(binding)

        codeFromShared = EncSharedPreferences.getValueString(keyStringCode,applicationContext)

        if (codeFromShared == null || codeFromShared == "") {
            binding.bottomNavigationContainer.isGone = true

            swapFragments(AuthorizationFragment())
        } else {
            stringTokenFromShared = EncSharedPreferences.getValueString(
                keyStringAccesskey,
                applicationContext
            )
            Repository.tempAccessToken = stringTokenFromShared?.let {
                EncSharedPreferences.convertJsonStringToTestClass(
                    it)
            }

            binding.apply {
                bottomNavigationContainer.setOnItemSelectedListener {
                    handleBottomNavigation(it.itemId)
                }
            }
            swapFragments(WalletFragment())
        }
    }
    private fun handleBottomNavigation(
        menuItemId: Int
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
        else -> false
    }
    private fun swapFragments(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack("back")
            .commit()
    }

    private fun initializeMenuDrawerAndTopAppBar(binding:ActivityMainBinding){
        binding.topAppBar.setNavigationOnClickListener {
            binding.drawerLayout.openDrawer(Gravity.LEFT)
        }

        binding.navigationView.setNavigationItemSelectedListener  { menuItem ->
            handleDrawerMenu(menuItem)
            menuItem.isChecked = true
            binding.drawerLayout.closeDrawers()
            true
        }
    }

    private fun handleDrawerMenu(menuItem: MenuItem) {
        when (menuItem.itemId) {
            R.id.logout -> {
                drawerMenuFunctionForLogout(this)
            }
            else -> false
        }
    }

    fun drawerMenuFunctionForLogout(context:Context){
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
            codeFromShared = ""
            EncSharedPreferences.saveToEncryptedSharedPrefsString(
                keyStringAccesskey,
                "",
                this@MainActivity
            )
            Repository.tempAccessToken = null

            logoutNetwork.logout {
                Log.e("LOGOUT:", it.toString())
            }
            CookieSyncManager.createInstance(this)
            val cookieManager = CookieManager.getInstance()
            cookieManager.removeAllCookie()

            findViewById<BottomNavigationView>(R.id.bottom_navigation_container).isGone = true
            val intent = Intent(
              this@MainActivity, MainActivity::class.java
            )
            startActivity(intent)
        }
        dialog.setCancelable(false)
        dialog.setContentView(view)
        dialog.show()
    }
}
