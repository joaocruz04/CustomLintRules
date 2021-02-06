package com.joaocruz04.customlintrules.rules

import com.android.tools.lint.checks.infrastructure.LintDetectorTest
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Issue
import com.joaocruz04.customlintrules.rules.DealerDetector.Companion.DEALER_NAME_ISSUE
import org.junit.jupiter.api.Test

class DealerDetectorTest : LintDetectorTest() {

    private val issues: MutableList<Issue> = mutableListOf()
    private var detector = DealerDetector()

    @Test
    fun `Class names should not end with word Dealer`() {
        expectedIssues(listOf(DEALER_NAME_ISSUE))
        val stub = kotlin("""

        package com.joaocruz04.customlintrules

        class MyDealer

        """).indented()

        val result = lint()
            .allowMissingSdk()
            .files(stub)
            .run()

        result
            .expectErrorCount(1)
            .expect("""
                src/com/joaocruz04/customlintrules/MyDealer.kt:4: Error: Forbidden use of word ´Dealer´ in class naming. [Dealer naming detector]
                class MyDealer
                ~~~~~~~~~~~~~~
                1 errors, 0 warnings
            """.trimIndent())
    }

    @Test
    fun `Class names should not start with word Dealer`() {
        expectedIssues(listOf(DEALER_NAME_ISSUE))
        val stub = kotlin("""

        package com.joaocruz04.customlintrules

        class DealerLocation

        """).indented()

        val result = lint()
            .allowMissingSdk()
            .files(stub)
            .run()

        result
            .expectErrorCount(1)
            .expect("""
                src/com/joaocruz04/customlintrules/DealerLocation.kt:4: Error: Forbidden use of word ´Dealer´ in class naming. [Dealer naming detector]
                class DealerLocation
                ~~~~~~~~~~~~~~~~~~~~
                1 errors, 0 warnings
            """.trimIndent())
    }

    // Helpers

    private fun expectedIssues(expectedIssues: List<Issue>) {
        issues.clear()
        issues.addAll(expectedIssues)
    }

    // Lint Detector

    override fun getDetector(): Detector = detector

    override fun getIssues(): MutableList<Issue> = issues

}
