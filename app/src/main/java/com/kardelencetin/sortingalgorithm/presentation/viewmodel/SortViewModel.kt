package com.kardelencetin.sortingalgorithm.presentation.viewmodel

import androidx.lifecycle.*
import com.kardelencetin.sortingalgorithm.domain.model.SortResult
import com.kardelencetin.sortingalgorithm.domain.model.SortStep
import com.kardelencetin.sortingalgorithm.domain.usecase.GetSortResultsUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SortViewModel(
    private val getSortResultsUseCase: GetSortResultsUseCase
) : ViewModel() {

    private val _results = MutableLiveData<List<SortResult>>()
    val results: LiveData<List<SortResult>> = _results

    private val _currentResult = MutableLiveData<SortResult?>()
    val currentResult: LiveData<SortResult?> = _currentResult

    private val _currentStepIndex = MutableLiveData(0)
    val currentStepIndex: LiveData<Int> = _currentStepIndex

    private val _elapsedTime = MutableLiveData<Long>()
    val elapsedTime: LiveData<Long> = _elapsedTime

    private var playJob: Job? = null

    val currentStep = MediatorLiveData<SortStep?>()

    init {
        currentStep.addSource(_currentResult) { updateCurrentStep() }
        currentStep.addSource(_currentStepIndex) { updateCurrentStep() }
    }

    private fun updateCurrentStep() {
        currentStep.value = _currentResult.value?.steps?.getOrNull(_currentStepIndex.value ?: 0)
    }

    fun simulateAll(list: List<Int>) {
        val compareList = getSortResultsUseCase.executeAll(list)
        _results.value = compareList
        selectAlgorithm(0)
    }

    fun selectAlgorithm(position: Int) {
        _results.value?.getOrNull(position)?.let { result ->
            _currentResult.value = result
            _currentStepIndex.value = 0
        }
    }

    fun nextStep() {
        val steps = _currentResult.value?.steps ?: return
        val current = _currentStepIndex.value ?: 0
        if (current < steps.lastIndex) {
            _currentStepIndex.value = current + 1
        }
    }

    fun prevStep() {
        val current = _currentStepIndex.value ?: 0
        if (current > 0) {
            _currentStepIndex.value = current - 1
        }
    }

    fun autoPlayStep(intervalMillis: Long = 400, onEnd: (() -> Unit)? = null) {
        playJob?.cancel()
        _currentStepIndex.value = 0
        val startMillis = System.currentTimeMillis()

        playJob = viewModelScope.launch {
            val steps = _currentResult.value?.steps ?: return@launch
            for (i in steps.indices) {
                _currentStepIndex.postValue(i)
                _elapsedTime.postValue(System.currentTimeMillis() - startMillis)
                delay(intervalMillis)
            }
            _elapsedTime.postValue(System.currentTimeMillis() - startMillis)
            onEnd?.invoke()
        }
    }

    fun stopAutoPlay() {
        playJob?.cancel()
    }
}
