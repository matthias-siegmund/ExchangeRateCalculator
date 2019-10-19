package dev.siegmund.exchange.ui.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import dev.siegmund.exchange.ui.model.ExchangeRateItem
import kotlinx.android.synthetic.main.item_exchange_rate.view.*
import java.util.*

class ExchangeRateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private var textWatcher: TextWatcher? = null

    fun bind(
        item: ExchangeRateItem,
        onItemClick: () -> Unit,
        onValueChanged: (ExchangeRateItem) -> Unit
    ) {
        val drawable = getDrawable(itemView.context, item.currencyCode)
        itemView.imageView.setImageDrawable(drawable)
        itemView.nameTextView.text = item.currencyName
        itemView.codeTextView.text = item.currencyCode
        itemView.valueEditText.isEnabled = item.editable
        resetListeners()

        if (!itemView.valueEditText.hasFocus()) {
            itemView.valueEditText.setText(item.value.toString())
        }

        if (item.clickable) {
            itemView.setOnClickListener { onItemClick() }
        }

        if (item.editable) {
            setTextWatcher(item, onValueChanged)
        }
    }

    fun unbind() {
        resetListeners()
    }

    private fun setTextWatcher(item: ExchangeRateItem, onValueChanged: (ExchangeRateItem) -> Unit) {
        textWatcher = object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) = Unit

            override fun beforeTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                text?.toString()?.toDoubleOrNull()?.let {
                    onValueChanged(item.copy(value = it))
                }
            }
        }
        itemView.valueEditText.addTextChangedListener(textWatcher)
    }

    private fun resetListeners() {
        itemView.valueEditText.removeTextChangedListener(textWatcher)
        textWatcher = null
        itemView.setOnClickListener(null)
    }

    private fun getDrawable(context: Context, currencyCode: String): Drawable? {
        val resourceId = context.resources.getIdentifier(
            "flag_${currencyCode.toLowerCase(Locale.getDefault())}",
            "drawable",
            context.packageName
        )
        return ContextCompat.getDrawable(context, resourceId)
    }
}