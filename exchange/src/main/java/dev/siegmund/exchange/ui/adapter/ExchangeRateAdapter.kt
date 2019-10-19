package dev.siegmund.exchange.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import dev.siegmund.exchange.R
import dev.siegmund.exchange.ui.model.ExchangeRate

class ExchangeRateAdapter : RecyclerView.Adapter<ExchangeRateViewHolder>() {
    lateinit var onItemClick: (ExchangeRate) -> Unit

    lateinit var onValueChanged: (ExchangeRate, List<ExchangeRate>) -> Unit

    var items: List<ExchangeRate> = emptyList()
        set(value) {
            val diffCallback = ExchangeRateDiffUtilCallback(field, value)
            val diffResult = DiffUtil.calculateDiff(diffCallback)
            field = value
            diffResult.dispatchUpdatesTo(this)
        }

    override fun getItemCount() = items.count()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExchangeRateViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_exchange_rate, parent, false)
        return ExchangeRateViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExchangeRateViewHolder, position: Int) {
        val item = items[position]
        holder.bind(
            item,
            { onItemClick(item) },
            { onValueChanged(it, items) }
        )
    }

    override fun onViewRecycled(holder: ExchangeRateViewHolder) {
        super.onViewRecycled(holder)
        holder.unbind()
    }

}