package alpha.dex.crypto.ui.adapter

import alpha.dex.crypto.R
import alpha.dex.crypto.model.CryptoCurrency
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide

class CurrencySpinnerAdapter(
    context: Context,
    private val currencyList: List<CryptoCurrency>
) : ArrayAdapter<CryptoCurrency>(context, R.layout.spinner_item, currencyList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createViewFromResource(position, convertView, parent, R.layout.spinner_item)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createViewFromResource(position, convertView, parent, R.layout.spinner_item_dropdown)
    }

    private fun createViewFromResource(
        position: Int, convertView: View?, parent: ViewGroup, resource: Int
    ): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(resource, parent, false)
        val item = currencyList[position]

        val imageView = view.findViewById<ImageView>(R.id.currencyImageView)
        val textView = view.findViewById<TextView>(R.id.currencyTextView)

        Glide.with(context)
            .load("https://s2.coinmarketcap.com/static/img/coins/64x64/${item.id}.png")
            .into(imageView)

        textView.text = item.symbol

        return view
    }
}
