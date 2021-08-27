package com.example.cryptowallet.dialog

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.example.cryptowallet.R
import com.example.cryptowallet.Repository
import com.example.cryptowallet.databinding.FragmentRequestMoneyDialogBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class RequestMoneyDialog:DialogFragment() {
    companion object {
        fun create(listener: () -> Unit): RequestMoneyDialog {
            return RequestMoneyDialog().apply {

            }
        }
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = LayoutInflater.from(requireContext())
        val binding = FragmentRequestMoneyDialogBinding.inflate(inflater)

        binding.apply {
                Glide.with(requireContext())
                    .load("https://api.coinicons.net/icon/${Repository.currency}/128x128")
                    .into(requestDialogIcon)
            requestDialogAddressText.text = Repository.address
            requestDialogTitleText.text = Repository.currency
            val urlForQr = "http://api.qrserver.com/v1/create-qr-code/?data=${Repository.address}&size=1600x1600"
            Glide.with(requireContext())
                .load(urlForQr)
                .into(requestDialogQrcodeImage)
        }

        return MaterialAlertDialogBuilder(
            requireContext(),R.style.MyRounded_MaterialComponents_MaterialAlertDialog)
            .setView(binding.root)
            .setPositiveButton(getString(R.string.dialog_request)){ _, _ ->
                val bitmapDrawable =
                    binding.requestDialogQrcodeImage.drawable.toBitmap(1000,1000).toDrawable(resources)// get the from imageview or use your drawable from drawable folder

                val bitmap1 = bitmapDrawable.bitmap
                val imgBitmapPath = MediaStore.Images.Media.insertImage(
                    requireContext().contentResolver,
                    bitmap1,
                    Repository.address,
                    null
                )
                val imgBitmapUri: Uri = Uri.parse(imgBitmapPath)
                val shareText = binding.requestDialogAddressText.text
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "*/*"
                shareIntent.putExtra(Intent.EXTRA_STREAM, imgBitmapUri)
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareText)
                startActivity(Intent.createChooser(shareIntent, "Share Wallpaper using"))
            }
            .setNegativeButton(R.string.dialog_cancel,null)
            .create()
    }
}