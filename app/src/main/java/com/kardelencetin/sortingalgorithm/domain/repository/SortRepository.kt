package com.kardelencetin.sortingalgorithm.domain.repository

import com.kardelencetin.sortingalgorithm.domain.model.AlgorithmType
import com.kardelencetin.sortingalgorithm.domain.model.SortResult

interface SortRepository {
    fun getSortResult(list: List<Int>, algorithm: AlgorithmType): SortResult
    fun getAllSortResults(list: List<Int>): List<SortResult>
}