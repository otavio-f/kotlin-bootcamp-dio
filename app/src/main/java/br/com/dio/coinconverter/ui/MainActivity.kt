package br.com.dio.coinconverter.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import br.com.dio.coinconverter.R
import br.com.dio.coinconverter.core.extensions.*
import br.com.dio.coinconverter.data.model.CoinTypes
import br.com.dio.coinconverter.databinding.ActivityMainBinding
import br.com.dio.coinconverter.presentation.MainViewModel
import br.com.dio.coinconverter.ui.history.HistoryActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val dialog by lazy { createProgressDialog() }
    private val viewModel by viewModel<MainViewModel>()
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setSupportActionBar(binding.tbMain)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        bindAdapters()
        bindListeners()

        bindObservers()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_item_history) {
            startActivity(Intent(this, HistoryActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

    private fun bindObservers() {
        viewModel.state.observe(this) {
            when (it) {
                MainViewModel.State.Loading -> dialog.show()
                is MainViewModel.State.Error -> error(it)
                is MainViewModel.State.Success -> success(it)
                is MainViewModel.State.Saved -> saved()
            }
        }
    }

    /**
     * Action to execute when a coin exchange is saved successfully
     */
    private fun saved() {
        dialog.dismiss()
        createDialog { setMessage(getString(R.string.complete_saved_item)) }.show()
    }

    /**
     * Actions to execute when coin exchange fails
     * Present the error on screen
     */
    private fun error(it: MainViewModel.State.Error) {
        // Present error
        dialog.dismiss()
        createDialog { setMessage(it.throwable.message) }.show()
    }

    /**
     * Actions to execute when the coin exchange is successful
     * Present the result correctly on screen
     */
    private fun success(it: MainViewModel.State.Success) {
        // Calculate value and present the result
        dialog.dismiss()
        val selectedCoin = binding.tilTo.text
        val coin = CoinTypes.values().find { it.name == selectedCoin } ?: CoinTypes.BRL
        val result = it.value.bid * binding.tilValue.text.toDouble()
        binding.tvResult.text = result.formatCurrency(coin.locale)
        binding.btnSave.isEnabled = true
    }

    private fun bindListeners() {
        binding.tilValue.editText?.doAfterTextChanged {
            // Numeric keyboards allow spaces, beware!
            binding.btnConvert.isEnabled = (it != null && it.isNotBlank())
            binding.btnSave.isEnabled = false //Only enable when result is shown
        }

        binding.btnConvert.setOnClickListener {
            it.hideSoftKeyboard()
            val coins = "${binding.actvFrom.text}-${binding.actvTo.text}"
            viewModel.getExchangeValue(coins)
        }

        binding.btnSave.setOnClickListener {
            val exchange = viewModel.state.value
            (exchange as? MainViewModel.State.Success)?.let {
                val lastExchange = it.value.copy(
                    bid=it.value.bid * binding.tilValue.text.toDouble())
                viewModel.saveExchange(lastExchange)
            }
        }
    }

    /**
     * Bind coin selector dropdown to coin names
     */
    private fun bindAdapters() {
        val list = CoinTypes.values()
        val adapter = ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1, list)

        binding.actvFrom.setAdapter(adapter)
        binding.actvTo.setAdapter(adapter)

        binding.actvFrom.setText(CoinTypes.BRL.name, false)
        binding.actvTo.setText(CoinTypes.USD.name, false)
    }
}