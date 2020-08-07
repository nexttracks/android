package org.nexttracks.android.ui

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.schibsted.spain.barista.rule.BaristaRule
import com.schibsted.spain.barista.rule.flaky.AllowFlaky
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.runner.RunWith
import org.nexttracks.android.R
import org.nexttracks.android.ScreenshotTakingOnFailureRule
import org.nexttracks.android.ui.status.StatusActivity

@LargeTest
@RunWith(AndroidJUnit4::class)
class StatusActivityTests {
    @get:Rule
    var baristaRule = BaristaRule.create(StatusActivity::class.java)

    private val screenshotRule = ScreenshotTakingOnFailureRule()

    @get:Rule
    val ruleChain: RuleChain = RuleChain
            .outerRule(baristaRule.activityTestRule)
            .around(screenshotRule)

    @Before
    fun setUp() {
        baristaRule.launchActivity()
    }

    @Test
    @AllowFlaky(attempts = 1)
    fun statusActivityShowsEndpointState() {
        assertDisplayed(R.string.status_endpoint_state_hint)
    }

    @Test
    @AllowFlaky(attempts = 1)
    fun statusActivityShowsLogsLauncher() {
        assertDisplayed(R.string.viewLogs)
    }
}