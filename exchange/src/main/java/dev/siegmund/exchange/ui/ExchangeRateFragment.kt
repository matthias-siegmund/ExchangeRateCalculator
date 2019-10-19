package dev.siegmund.exchange.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import androidx.recyclerview.widget.SimpleItemAnimator
import dagger.android.support.AndroidSupportInjection
import dev.siegmund.exchange.R
import dev.siegmund.exchange.ui.adapter.ExchangeRateAdapter
import dev.siegmund.exchange.ui.model.ExchangeRateViewModel
import dev.siegmund.exchange.ui.model.ExchangeRateViewModelFactory
import kotlinx.android.synthetic.main.fragment_exchange_rate.*
import javax.inject.Inject

class ExchangeRateFragment : Fragment() {
    @Inject
    lateinit var exchangeRateViewModelFactory: ExchangeRateViewModelFactory

    private val exchangeRateViewModel: ExchangeRateViewModel by lazy {
        ViewModelProviders.of(this, exchangeRateViewModelFactory)
            .get(ExchangeRateViewModel::class.java)
    }

    private val adapter: ExchangeRateAdapter by lazy {
        ExchangeRateAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_exchange_rate, container, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidSupportInjection.inject(this)
        exchangeRateViewModel.onCreate()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        initRecyclerView()
        observeExchangeRates()
        observeShowError()
        observeScrollToTop()
    }

    private fun initAdapter() {
        with(adapter) {
            onItemClick = { exchangeRateViewModel.onItemClick(it) }
            onValueChanged = { item, items -> exchangeRateViewModel.onValueChanged(item, items) }
        }
    }

    private fun initRecyclerView() {
        with(recyclerView) {
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
            layoutManager = LinearLayoutManager(context, VERTICAL, false)
            adapter = this@ExchangeRateFragment.adapter
        }
    }

    private fun observeExchangeRates() {
        exchangeRateViewModel.exchangeRateItems.observe(this, Observer { exchangeRates ->
            adapter.items = exchangeRates
        })
    }

    private fun observeShowError() {
        exchangeRateViewModel.showError.observe(this, Observer {
            Toast.makeText(requireContext(), R.string.error, Toast.LENGTH_LONG).show()
        })
    }

    private fun observeScrollToTop() {
        exchangeRateViewModel.scrollToTop.observe(this, Observer {
            recyclerView.scrollToPosition(0)
        })
    }
}