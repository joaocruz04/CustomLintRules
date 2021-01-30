package com.joaocruz04.certificaterule

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.detector.api.Issue

class CertificateIssuesRegistry : IssueRegistry() {

    override val issues: List<Issue>
        get() = listOf(
            CertificateDetector.ALMOST_EXPIRED_CERT_ISSUE,
            CertificateDetector.EXPIRED_CERT_ISSUE,
            CertificateDetector.WRONG_DATE_FORMAT_ISSUE
        )
}
