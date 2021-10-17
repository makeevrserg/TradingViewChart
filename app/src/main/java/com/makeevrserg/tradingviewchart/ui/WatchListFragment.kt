package com.makeevrserg.tradingview.ui

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.makeevrserg.tradingviewchart.adapters.ItemListener
import com.makeevrserg.tradingviewchart.adapters.WatchListAdapter
import com.makeevrserg.tradingview.viewmodels.WatchListViewModel
import com.makeevrserg.tradingviewchart.R
import com.makeevrserg.tradingviewchart.databinding.WatchListFragmentBinding

class WatchListFragment : Fragment() {


    private val viewModel: WatchListViewModel by lazy {
        ViewModelProvider(this).get(WatchListViewModel::class.java)
    }

    override fun onResume() {
        super.onResume()

//        (requireActivity() as MainActivity).title = "Watchlist"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //Binding
        val binding: WatchListFragmentBinding = DataBindingUtil.inflate(inflater,
            R.layout.watch_list_fragment,container,false)
        binding.viewModel =viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        //RecyclerView
        setHasOptionsMenu(true)

        val adapter = WatchListAdapter(ItemListener { group, view ->

            this.findNavController()
                .navigate(
                    WatchListFragmentDirections.actionWatchListFragmentToChartPreviewFragment()
                )
        })
        binding.recyclerView.adapter = adapter
        //viewModel.observe
        viewModel.watchListItem.observe(viewLifecycleOwner, {
            it?.let {
                adapter.submitList(it)
            }
        })

        return  binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.watchlist_menu,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }


}