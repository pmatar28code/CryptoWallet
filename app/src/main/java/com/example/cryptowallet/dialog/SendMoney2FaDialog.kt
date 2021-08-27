package com.example.cryptowallet.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import com.example.cryptowallet.R
import com.example.cryptowallet.Repository
import com.example.cryptowallet.databinding.FragmentSendMoney2faDialogBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class SendMoney2FaDialog: DialogFragment() {
    companion object {
        fun create(onItemAddedListener: () -> Unit): SendMoney2FaDialog {
            return SendMoney2FaDialog().apply {
                this.onItemAddedListener = onItemAddedListener
            }
        }
    }
    private var onItemAddedListener: () -> Unit = {}

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = LayoutInflater.from(requireContext())
        val binding = FragmentSendMoney2faDialogBinding.inflate(inflater)

        return MaterialAlertDialogBuilder(
            requireContext(), R.style.MyRounded_MaterialComponents_MaterialAlertDialog
        )
            .setView(binding.root)
            .setPositiveButton(getString(R.string.dialog_send)) { _, _ ->
                setPositiveButton(binding)
            }
            .setNegativeButton(getString(R.string.dialog_cancel), null)
            .create()
    }
    private fun setPositiveButton(binding: FragmentSendMoney2faDialogBinding){
        Repository.token2fa = binding.outlinedTextField2FaToken.editText?.text.toString()
        onItemAddedListener()
    }
}