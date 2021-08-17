package com.example.cryptowallet.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.cryptowallet.R
import com.example.cryptowallet.databinding.FragmentRequestBinding

class RequestFragment: Fragment(R.layout.fragment_request) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentRequestBinding.bind(view)
    }
}