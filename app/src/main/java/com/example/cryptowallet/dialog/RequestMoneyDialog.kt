package com.example.cryptowallet.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
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
            .setPositiveButton("Request"){_,_ ->
                Toast.makeText(requireContext(),"Opening sharing drawer",Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel",null)
            .create()
    }
}