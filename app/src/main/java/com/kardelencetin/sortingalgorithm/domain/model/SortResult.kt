package com.kardelencetin.sortingalgorithm.domain.model

data class SortResult(
    val algorithm: AlgorithmType,
    val steps: List<SortStep>,
    val stepCount: Int,
    val swapCount: Int,
    val durationMs: Long
)