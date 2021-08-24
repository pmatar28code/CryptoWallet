package com.example.cryptowallet.codereader

import android.content.ClipData
import android.content.ClipboardManager
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
import com.example.cryptowallet.Repository
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.concurrent.Executors

class BarcodeResultBottomSheet: BottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.bottom_sheet_barcode_data, container, false)

    //We will call this function to update the URL displayed
    fun updateURL(url: String?) {
        if (url != null) {
            fetchUrlMetaData(url) { address ->
                view?.apply {
                    //findViewById<TextView>(R.id.text_view_desc)?.text = desc
                    findViewById<TextView>(R.id.text_view_address)?.text = address
                    findViewById<TextView>(R.id.text_view_store_address).setOnClickListener { _ ->
                        Toast.makeText(requireContext(),"Address copied to your clipboard",Toast.LENGTH_SHORT).show()
                        copyToClipboard(address)

                    //Intent(Intent.ACTION_VIEW).also {
                        // it.data = Uri.parse(url)
                        // startActivity(it)
                        // }
                    }
                }
            }
        }
    }
    fun copyToClipboard(text: CharSequence){
        val clipboard = ContextCompat.getSystemService(requireContext(), ClipboardManager::class.java)
        clipboard?.setPrimaryClip(ClipData.newPlainText("",text))
    }

    //this function will fetch URL data
    private fun fetchUrlMetaData(
        url: String,
        callback: (title: String) -> Unit
    ) {
        val executor = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())
        executor.execute {
            //val doc = Jsoup.connect(url).get()
            //val desc = doc.select("meta[name=description]")[0].attr("content")
            handler.post {
                callback(url)
            }
        }
    }
}