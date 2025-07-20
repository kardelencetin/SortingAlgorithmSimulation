package com.kardelencetin.sortingalgorithm.presentation.ui.activity

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.kardelencetin.sortingalgorithm.R
import com.kardelencetin.sortingalgorithm.data.repository.SortRepositoryImpl
import com.kardelencetin.sortingalgorithm.databinding.ActivityMainBinding
import com.kardelencetin.sortingalgorithm.domain.model.AlgorithmType
import com.kardelencetin.sortingalgorithm.domain.usecase.GetSortResultsUseCase
import com.kardelencetin.sortingalgorithm.presentation.viewmodel.SortViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel: SortViewModel by lazy {
        SortViewModel(
            GetSortResultsUseCase(SortRepositoryImpl())
        )
    }

    private val algorithms = AlgorithmType.values()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAlgorithmSpinner()
        setupSimulateButton()
        setupObservers()

        showSimulationArea(false)
    }

    private fun setupAlgorithmSpinner() {
        val adapter = ArrayAdapter(
            this,
            R.layout.spinner_item,
            algorithms.map { it.displayName }
        )
        binding.algorithmSpinner.adapter = adapter
        binding.algorithmSpinner.setSelection(0)
        binding.algorithmSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                viewModel.selectAlgorithm(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupSimulateButton() {
        binding.btnSimulate.setOnClickListener {
            val numbers = binding.inputNumbers.text.toString()
                .split(",")
                .mapNotNull { it.trim().toIntOrNull() }
            if (numbers.isNotEmpty()) {
                viewModel.simulateAll(numbers)
                showSimulationArea(true)
                binding.btnSimulate.isEnabled = false

                viewModel.autoPlayStep(intervalMillis = 400) {
                    binding.btnSimulate.isEnabled = true
                }
            }
        }
    }

    private fun setupObservers() {
        viewModel.currentStep.observe(this) { step ->
            binding.sortStepView.step = step
        }
        viewModel.currentResult.observe(this) {
            updateResultCard(binding.algorithmSpinner.selectedItemPosition)
        }
        viewModel.elapsedTime.observe(this) { elapsed ->
            binding.tvTime.text = "Süre: ${elapsed} ms"
        }
    }

    private fun updateResultCard(selectedPos: Int) {
        val results = viewModel.results.value ?: return
        val res = results.getOrNull(selectedPos) ?: return
        binding.tvAlgorithmName.text = res.algorithm.displayName
        binding.tvStepCount.text = "Adım: ${res.stepCount}"
        binding.tvSwapCount.text = "Swap: ${res.swapCount}"
        binding.tvTime.text = "Süre: ${res.durationMs} ms"
    }

    private fun showSimulationArea(visible: Boolean) {
        binding.simulationGraph.visibility = if (visible) View.VISIBLE else View.GONE
    }
}
