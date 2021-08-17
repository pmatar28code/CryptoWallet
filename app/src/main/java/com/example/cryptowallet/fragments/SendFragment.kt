package com.example.cryptowallet.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.cryptowallet.R
import com.example.cryptowallet.databinding.FragmentSendBinding

class SendFragment: Fragment(R.layout.fragment_send){
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentSendBinding.bind(view)



    }

}