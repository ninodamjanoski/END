package com.endumedia.benchmark

import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.recyclerview.widget.RecyclerView
import androidx.test.annotation.UiThreadTest
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import kotlinx.android.synthetic.main.activity_benchmark.*
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


/**
 * Created by Nino on 18.08.19
 */

@LargeTest
@RunWith(AndroidJUnit4::class)
class ProductsAdapterBenchmark {
    @get:Rule
    val benchmarkRule = BenchmarkRule()

    @get:Rule
    val activityRule = ActivityTestRule(BenchmarkActivity::class.java)

    @UiThreadTest
    @Test
    fun scrollItem() {
        val activity = activityRule.activity

        // If RecyclerView has children, the items are attached, bound, and gone through layout.
        // Ready to benchmark.
        Assert.assertTrue("RecyclerView expected to have children", activity.list.childCount > 0)

        benchmarkRule.measureRepeated {
            activity.list.scrollByOneItem()
            runWithTimingDisabled {
                activity.testExecutor.flush()
            }
        }
    }

    private fun RecyclerView.scrollByOneItem() {
        scrollBy(0, getChildAt(childCount - 1).height)
    }

}