package dev.siegmund.exchange.ui.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import dev.siegmund.exchange.ui.model.ExchangeRate
import kotlinx.android.synthetic.main.item_exchange_rate.view.*
import java.util.*

class ExchangeRateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(item: ExchangeRate) {
        val drawable = getDrawable(itemView.context, item.currencyCode)
        itemView.imageView.setImageDrawable(drawable)
        itemView.nameTextView.text = item.currencyName
        itemView.codeTextView.text = item.currencyCode
        itemView.valueEditText.setText(item.value.toString())
    }

    private fun getDrawable(context: Context, currencyCode: String) : Drawable? {
        val resourceId = context.resources.getIdentifier(
            "flag_${currencyCode.toLowerCase(Locale.getDefault())}",
            "drawable",
            context.packageName
        )
        return ContextCompat.getDrawable(context, resourceId)
    }
}