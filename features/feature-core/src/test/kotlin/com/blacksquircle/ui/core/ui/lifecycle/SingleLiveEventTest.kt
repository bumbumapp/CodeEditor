package com.blacksquircle.ui.core.ui.lifecycle

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

/** Regression: call() emitted null, which crashed non-null Kotlin observers (explorer actions). */
class SingleLiveEventTest {

    @get:Rule
    val instantExecutor = InstantTaskExecutorRule()

    @Test
    fun callEmitsUnitNotNull() {
        val event = SingleLiveEvent<Unit>()
        val received = mutableListOf<Any?>()
        val observer = Observer<Unit> { received.add(it) }
        event.observeForever(observer)
        event.call()
        event.removeObserver(observer)
        assertEquals(listOf<Any?>(Unit), received)
    }
}