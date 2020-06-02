package com.teamnusocial.nusocial.ui.you

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.teamnusocial.nusocial.R

class YouFragment : Fragment() {

    companion object {
        fun newInstance() = YouFragment()
    }

    private lateinit var viewModel: YouViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_you, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(YouViewModel::class.java)
        // TODO: Use the ViewModel
    }

}