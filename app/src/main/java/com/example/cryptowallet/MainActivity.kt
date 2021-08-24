package com.example.cryptowallet

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.cryptowallet.databinding.ActivityMainBinding
import com.example.cryptowallet.fragments.AuthorizationFragment
import com.example.cryptowallet.fragments.RequestFragment
import com.example.cryptowallet.fragments.SendFragment
import com.example.cryptowallet.fragments.WalletFragment
import com.example.cryptowallet.network.classesapi.AccessToken
import com.example.cryptowallet.utilities.EncSharedPreferences
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity() {
    companion object {
        const val MY_CLIENT_ID = "c77416def5b58698219596f44ecf6236658c426805a522d517f45867b0348188"
        const val CLIENT_SECRET = "311b687baee92bbd8e584527bb757f27c9ed363f7a3922a18b381bcd4309b5b4"
        const val MY_REDIRECT_URI = "urn:ietf:wg:oauth:2.0:oob"//"cryptowallet://callback"
        const val keyStringAccesskey = "Access_key"
        const val keyStringCode = "Auth_code"
        var codeFromShared:String ?= null
        var stringTokenFromShared:String ?= null
        var accessTokenFromShared:AccessToken ?= null
        //lateinit var mainContext: Context


        private const val TAG = "CameraXBasic"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = LayoutInflater.from(this)
        val binding = ActivityMainBinding.inflate(inflater)
        setContentView(binding.root)

        if (allPermissionsGranted()) {
            //startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this,REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }

        runBlocking {
            val job:Job = launch(IO) {
                codeFromShared = EncSharedPreferences.getValueString(keyStringCode,applicationContext)
                Log.e("HOW CODE VALUE STRING LOOKS MAIN:",codeFromShared?:"not looking good")
                stringTokenFromShared = EncSharedPreferences.getValueString(keyStringAccesskey,applicationContext)
                joinAll()
            }
        }

        if (codeFromShared == null) {
           // mainContext = this
            binding.bottomNavigationContainer.isGone = true

            swapFragments(AuthorizationFragment())


        } else {
            stringTokenFromShared = EncSharedPreferences.getValueString(keyStringAccesskey,applicationContext)


            Log.e(
                "WHATS NEXT",
                "DO API Requests WITH TOKEN AVAILABLE CODE:$codeFromShared, Token:${
                    accessTokenFromShared?.access_token
                }"
            )

    /*
            UserNetwork.getUser {
                runBlocking {
                    var job:Job = launch(IO) {
                        Log.e("SHOWING USER", "${it.name}, id: ${it.id} WITH TOKEN = ${accessTokenFromShared?.access_token}}")
                        Repository.userId = it.id.toString()
                        Repository.userName = it.name.toString()
                    }
                }
            }
            ListAccountsNetwork.getAccounts {
                Repository.accounts = it as MutableList<ListAccounts.Data>
                runBlocking {
                    var job:Job = launch(IO) {
                        Log.e(
                            "LIST OF ACCOUNTS MAIN OJO: ",
                            "ID: ${it[0].id}, ${it[0].name},type = ${it[0].type},primary = ${it[0].primary}, ${it[0].balance}, ${it[0].currency} WITH TOKEN = ${accessTokenFromShared?.access_token}"
                        )
                        Log.e("ALL THE LIST OFF ACCOUNTS MAIN:","$it")
                        swapFragments(WalletFragment())
                    }
                }
            }
            */

            binding.apply {
                bottomNavigationContainer.setOnNavigationItemSelectedListener {
                    handleBottomNavigation(it.itemId, binding)
                }
            }
            //swap fragment to wallet which is going to be the main one to show to the user
            //listing accounts that have balance > 0
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
                //startCamera()
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
        else -> false
    }
    fun swapFragments(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack("back")
            .commit()
    }


}
