package com.makeevrserg.tradingview.ui

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import com.makeevrserg.tradingviewchart.adapters.ItemListener
import com.makeevrserg.tradingviewchart.adapters.WatchListAdapter
import com.makeevrserg.tradingview.viewmodels.WatchListViewModel
import com.makeevrserg.tradingviewchart.R
import com.makeevrserg.tradingviewchart.databinding.WatchListFragmentBinding
import androidx.recyclerview.widget.RecyclerView


class WatchListFragment : Fragment() {


    private val viewModel: WatchListViewModel by lazy {
        ViewModelProvider(this).get(WatchListViewModel::class.java)
    }

    override fun onResume() {
        super.onResume()

//        (requireActivity() as MainActivity).title = "Watchlist"
    }
    lateinit var adapter:WatchListAdapter
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

        adapter = WatchListAdapter(ItemListener { group, view ->

            this.findNavController()
                .navigate(
                    WatchListFragmentDirections.actionWatchListFragmentToChartPreviewFragment(group.title)
                )
        })
        val itemTouchHelper = ItemTouchHelper(dragCallback)
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)
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
        val menuItem =  menu.findItem(R.id.action_search)
        val searchView = menuItem.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                adapter.filter.filter(p0?:return false)
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                adapter.filter.filter(p0?:return false)
                return true
            }

        })
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_settings->{
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Выберите список")
                builder.setItems(viewModel.getWatchListTitles()?.toTypedArray()?:return super.onOptionsItemSelected(item)) { dialog, which ->
                    viewModel.onWatchListSelected(which)
                }

                val dialog = builder.create()
                dialog.show()
                return true
            }

        }
        return super.onOptionsItemSelected(item)
    }


    var dragCallback: ItemTouchHelper.SimpleCallback = object : ItemTouchHelper.SimpleCallback(
        ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.START or ItemTouchHelper.END,
        ItemTouchHelper.LEFT
    ) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            val fromPosition = viewHolder.adapterPosition
            val toPosition = target.adapterPosition
            viewModel.onListReorganized(fromPosition,toPosition)
            recyclerView.adapter!!.notifyItemMoved(fromPosition, toPosition)
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

        }
    }


}