package com.kardelencetin.sortingalgorithm.data.repository

import com.kardelencetin.sortingalgorithm.domain.model.AlgorithmType
import com.kardelencetin.sortingalgorithm.domain.model.SortResult
import com.kardelencetin.sortingalgorithm.domain.model.SortStep
import com.kardelencetin.sortingalgorithm.domain.repository.SortRepository

class SortRepositoryImpl : SortRepository {

    override fun getSortResult(list: List<Int>, algorithm: AlgorithmType): SortResult {
        return when (algorithm) {
            AlgorithmType.BUBBLE_SORT    -> bubbleSort(list)
            AlgorithmType.SELECTION_SORT -> selectionSort(list)
            AlgorithmType.INSERTION_SORT -> insertionSort(list)
            AlgorithmType.MERGE_SORT     -> mergeSort(list)
            AlgorithmType.QUICK_SORT     -> quickSort(list)
        }
    }

    override fun getAllSortResults(list: List<Int>): List<SortResult> =
        AlgorithmType.values().map { getSortResult(list, it) }

    private fun bubbleSort(input: List<Int>): SortResult {
        val arr = input.toMutableList()
        val steps = mutableListOf<SortStep>()
        var swapCount = 0
        val time = measureTimeMillis {
            for (i in 0 until arr.size - 1) {
                for (j in 0 until arr.size - i - 1) {
                    steps += SortStep(arr.toList(), Pair(j, j + 1))
                    if (arr[j] > arr[j + 1]) {
                        arr.swap(j, j + 1)
                        swapCount++
                        steps += SortStep(arr.toList(), Pair(j, j + 1))
                    }
                }
            }
        }
        steps += SortStep(arr.toList())
        return SortResult(AlgorithmType.BUBBLE_SORT, steps, steps.size, swapCount, time)
    }

    private fun selectionSort(input: List<Int>): SortResult {
        val arr = input.toMutableList()
        val steps = mutableListOf<SortStep>()
        var swapCount = 0
        val time = measureTimeMillis {
            for (i in arr.indices) {
                var minIdx = i
                for (j in i + 1 until arr.size) {
                    steps += SortStep(arr.toList(), Pair(minIdx, j))
                    if (arr[j] < arr[minIdx]) minIdx = j
                }
                if (minIdx != i) {
                    arr.swap(i, minIdx)
                    swapCount++
                    steps += SortStep(arr.toList(), Pair(i, minIdx))
                }
            }
        }
        steps += SortStep(arr.toList())
        return SortResult(AlgorithmType.SELECTION_SORT, steps, steps.size, swapCount, time)
    }

    private fun insertionSort(input: List<Int>): SortResult {
        val arr = input.toMutableList()
        val steps = mutableListOf<SortStep>()
        var swapCount = 0
        val time = measureTimeMillis {
            for (i in 1 until arr.size) {
                val key = arr[i]
                var j = i - 1
                while (j >= 0 && arr[j] > key) {
                    steps += SortStep(arr.toList(), Pair(j, j + 1))
                    arr[j + 1] = arr[j]
                    j--
                    swapCount++
                }
                arr[j + 1] = key
                steps += SortStep(arr.toList(), Pair(j + 1, i))
            }
        }
        steps += SortStep(arr.toList())
        return SortResult(AlgorithmType.INSERTION_SORT, steps, steps.size, swapCount, time)
    }

    private fun mergeSort(input: List<Int>): SortResult {
        val arr = input.toMutableList()
        val steps = mutableListOf<SortStep>()
        val swapCounter = intArrayOf(0)
        val time = measureTimeMillis {
            mergeSortHelper(arr, 0, arr.lastIndex, steps, swapCounter)
        }
        steps += SortStep(arr.toList())
        return SortResult(AlgorithmType.MERGE_SORT, steps, steps.size, swapCounter[0], time)
    }

    private fun mergeSortHelper(
        arr: MutableList<Int>,
        l: Int,
        r: Int,
        steps: MutableList<SortStep>,
        swapCounter: IntArray
    ) {
        if (l < r) {
            val m = (l + r) / 2
            mergeSortHelper(arr, l, m, steps, swapCounter)
            mergeSortHelper(arr, m + 1, r, steps, swapCounter)
            merge(arr, l, m, r, steps, swapCounter)
        }
    }

    private fun merge(
        arr: MutableList<Int>,
        l: Int,
        m: Int,
        r: Int,
        steps: MutableList<SortStep>,
        swapCounter: IntArray
    ) {
        val left = arr.subList(l, m + 1).toList()
        val right = arr.subList(m + 1, r + 1).toList()
        var i = 0
        var j = 0
        var k = l
        while (i < left.size && j < right.size) {
            if (left[i] <= right[j]) {
                arr[k] = left[i++]
            } else {
                arr[k] = right[j++]
                swapCounter[0]++
            }
            steps += SortStep(arr.toList(), Pair(k, k), merged = (l..r).map { arr[it] })
            k++
        }
        while (i < left.size) {
            arr[k] = left[i++]
            steps += SortStep(arr.toList(), Pair(k, k), merged = (l..r).map { arr[it] })
            k++
        }
        while (j < right.size) {
            arr[k] = right[j++]
            steps += SortStep(arr.toList(), Pair(k, k), merged = (l..r).map { arr[it] })
            k++
        }
    }

    private fun quickSort(input: List<Int>): SortResult {
        val arr = input.toMutableList()
        val steps = mutableListOf<SortStep>()
        val swapCounter = intArrayOf(0)
        val time = measureTimeMillis {
            quickSortHelper(arr, 0, arr.lastIndex, steps, swapCounter)
        }
        steps += SortStep(arr.toList())
        return SortResult(AlgorithmType.QUICK_SORT, steps, steps.size, swapCounter[0], time)
    }

    private fun quickSortHelper(
        arr: MutableList<Int>,
        low: Int,
        high: Int,
        steps: MutableList<SortStep>,
        swapCounter: IntArray
    ) {
        if (low < high) {
            val pi = partition(arr, low, high, steps, swapCounter)
            quickSortHelper(arr, low, pi - 1, steps, swapCounter)
            quickSortHelper(arr, pi + 1, high, steps, swapCounter)
        }
    }

    private fun partition(
        arr: MutableList<Int>,
        low: Int,
        high: Int,
        steps: MutableList<SortStep>,
        swapCounter: IntArray
    ): Int {
        val pivot = arr[high]
        var i = low - 1
        for (j in low until high) {
            steps += SortStep(arr.toList(), Pair(j, high))
            if (arr[j] < pivot) {
                i++
                arr.swap(i, j)
                swapCounter[0]++
                steps += SortStep(arr.toList(), Pair(i, j))
            }
        }
        arr.swap(i + 1, high)
        swapCounter[0]++
        steps += SortStep(arr.toList(), Pair(i + 1, high))
        return i + 1
    }

    private inline fun measureTimeMillis(block: () -> Unit): Long {
        val start = System.currentTimeMillis()
        block()
        return System.currentTimeMillis() - start
    }

    private fun MutableList<Int>.swap(i: Int, j: Int) {
        if (i == j) return
        val tmp = this[i]
        this[i] = this[j]
        this[j] = tmp
    }
}
