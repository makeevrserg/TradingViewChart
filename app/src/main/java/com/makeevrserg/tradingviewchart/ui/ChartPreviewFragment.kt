package com.makeevrserg.tradingview.ui

import android.graphics.Color
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.makeevrserg.tradingview.viewmodels.ChartPreviewViewModel
import com.makeevrserg.tradingview.viewmodels.WatchListViewModel
import com.makeevrserg.tradingviewchart.R
import com.makeevrserg.tradingviewchart.databinding.ChartPreviewFragmentBinding

class ChartPreviewFragment : Fragment() {

    private val viewModel: ChartPreviewViewModel by lazy {
        ViewModelProvider(this).get(ChartPreviewViewModel::class.java)
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "GOOG"
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //Binding
        val binding: ChartPreviewFragmentBinding =
            DataBindingUtil.inflate(inflater, R.layout.chart_preview_fragment, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }


}