package com.example.cryptowallet.codereader

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.cryptowallet.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.concurrent.Executors

class BarcodeResultBottomSheet: BottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(
        R.layout.bottom_sheet_barcode_data,
        container,
        false
    )

    fun updateURL(url: String?) {
        url ?: return
        fetchUrlMetaData(url) { address ->
            view?.apply {
                setViewsAndToastAndCallCopyToClipboard(
                    this,
                    address,
                    requireContext()
                )
                }
            }
        }
    }
    private fun copyToClipboard(text: CharSequence,context:Context){
        val clipboard = ContextCompat.getSystemService(
            context,
            ClipboardManager::class.java)
        clipboard?.setPrimaryClip(ClipData.newPlainText("",text))
    }
    private fun fetchUrlMetaData(
        url: String,
        callback: (title: String) -> Unit
    ) {
        val executor = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())
        executor.execute {
            handler.post {
                callback(url)
            }
        }
    }
    fun setViewsAndToastAndCallCopyToClipboard(view:View,address:String,context:Context){
        view.findViewById<TextView>(R.id.text_view_address)?.text = address
        view.findViewById<TextView>(R.id.text_view_store_address).setOnClickListener { _ ->
            Toast.makeText(context,"Address copied to your clipboard"
                ,Toast.LENGTH_SHORT).show()
            copyToClipboard(address,context)
    }
}