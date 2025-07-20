package com.kardelencetin.sortingalgorithm.domain.usecase

import com.kardelencetin.sortingalgorithm.domain.model.SortResult
import com.kardelencetin.sortingalgorithm.domain.repository.SortRepository

class GetSortResultsUseCase(private val repository: SortRepository) {
    fun executeAll(list: List<Int>): List<SortResult> =
        repository.getAllSortResults(list)
}