package com.joaocruz04.customlintrules.rules

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.detector.api.Issue

class IssuesRegistry : IssueRegistry() {

    override val issues: List<Issue>
        get() = listOf(DealerDetector.DEALER_NAME_ISSUE)
}
