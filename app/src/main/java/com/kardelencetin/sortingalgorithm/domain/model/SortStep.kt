package com.kardelencetin.sortingalgorithm.domain.model

data class SortStep(
    val list: List<Int>,
    val highlighted: Pair<Int, Int>? = null,
    val merged: List<Int>? = null
)