// ui/fragment/ConverterFragment.kt
package alpha.dex.crypto.ui.fragment

import alpha.dex.crypto.api.ApiInterface
import alpha.dex.crypto.api.ApiUtilities
import alpha.dex.crypto.databinding.FragmentConverterBinding
import alpha.dex.crypto.model.CryptoCurrency
import alpha.dex.crypto.ui.adapter.CurrencySpinnerAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ConverterFragment : Fragment() {
    private lateinit var binding: FragmentConverterBinding
    private lateinit var cryptoList: List<CryptoCurrency>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentConverterBinding.inflate(inflater, container, false)
        setupSpinners()
        binding.convertButton.setOnClickListener { convertCurrency() }
        return binding.root
    }

    private fun setupSpinners() {
        lifecycleScope.launch(Dispatchers.IO) {
            val res = ApiUtilities.getInstance().create(ApiInterface::class.java).getMarketData()
            if (res.body() != null) {
                cryptoList = res.body()!!.data.cryptoCurrencyList
                withContext(Dispatchers.Main) {
                    val adapter = CurrencySpinnerAdapter(requireContext(), cryptoList)
                    binding.fromCurrencySpinner.adapter = adapter
                    binding.toCurrencySpinner.adapter = adapter
                }
            }
        }

        binding.switchButton.setOnClickListener {
            val fromPosition = binding.fromCurrencySpinner.selectedItemPosition
            val toPosition = binding.toCurrencySpinner.selectedItemPosition
            binding.fromCurrencySpinner.setSelection(toPosition)
            binding.toCurrencySpinner.setSelection(fromPosition)
        }
    }

    private fun convertCurrency() {
        val amount = binding.amountEditText.text.toString().toDoubleOrNull() ?: return
        val fromCurrency = cryptoList[binding.fromCurrencySpinner.selectedItemPosition]
        val toCurrency = cryptoList[binding.toCurrencySpinner.selectedItemPosition]

        val fromPrice = fromCurrency.quotes[0].price
        val toPrice = toCurrency.quotes[0].price

        val result = (amount * fromPrice) / toPrice
        binding.resultTextView.text = "Result: %.4f %s".format(result, toCurrency.symbol)

        // Make resultCardView visible with animation
        if (!binding.resultCardView.isVisible) {
            binding.resultCardView.alpha = 0f
            binding.resultCardView.visibility = View.VISIBLE
            binding.resultCardView.animate().alpha(1f).setDuration(500).start()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("resultText", binding.resultTextView.text.toString())
        outState.putBoolean("resultCardVisible", binding.resultCardView.isVisible)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        savedInstanceState?.let {
            binding.resultTextView.text = it.getString("resultText", "")
            if (it.getBoolean("resultCardVisible", false)) {
                binding.resultCardView.visibility = View.VISIBLE
                binding.resultCardView.alpha = 1f
            }
        }
    }
}
