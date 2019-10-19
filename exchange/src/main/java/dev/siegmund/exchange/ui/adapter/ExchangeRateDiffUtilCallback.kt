package dev.siegmund.exchange.ui.adapter

import androidx.recyclerview.widget.DiffUtil
import dev.siegmund.exchange.ui.model.ExchangeRateItem

class ExchangeRateDiffUtilCallback(
    private val old: List<ExchangeRateItem>,
    private val new: List<ExchangeRateItem>
) : DiffUtil.Callback() {
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        old[oldItemPosition].currencyCode == new[newItemPosition].currencyCode

    override fun getOldListSize() = old.size

    override fun getNewListSize() = new.size

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        old[oldItemPosition] == new[newItemPosition]

}