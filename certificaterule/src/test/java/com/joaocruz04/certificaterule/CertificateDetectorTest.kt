package com.joaocruz04.certificaterule

import com.android.tools.lint.checks.infrastructure.LintDetectorTest
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Issue
import com.joaocruz04.certificaterule.CertificateDetector.Companion.ALMOST_EXPIRED_CERT_ISSUE
import com.joaocruz04.certificaterule.CertificateDetector.Companion.EXPIRED_CERT_ISSUE
import com.joaocruz04.certificaterule.CertificateDetector.Companion.WRONG_DATE_FORMAT_ISSUE
import org.junit.jupiter.api.Test

class CertificateDetectorTest : LintDetectorTest() {

    private val detector = CertificateDetector()
    private val issues = mutableListOf<Issue>()

    @Test
    fun `Expired certificate dates should generate expiration issue`() {
        expectIssues(listOf(EXPIRED_CERT_ISSUE, ALMOST_EXPIRED_CERT_ISSUE, WRONG_DATE_FORMAT_ISSUE))
        val stub = kotlin("""

        package com.joaocruz04.customlintrules

        import com.joaocruz04.certificate.Certificate

        class MyCertificates {
            val certificate = Certificate("01/01/2020")
        }

        """).indented()

        val result = lint()
            .allowMissingSdk()
            .files(certificateStub, stub)
            .run()

        result
            .expectErrorCount(1)
            .expect("""
            src/com/joaocruz04/customlintrules/MyCertificates.kt:7: Error: Wrong date pattern
                                Please make sure you use the pattern DD/MM/YYYY [WrongCertificateDateFormat]
                val certificate = Certificate("01/01/2020")
                                  ~~~~~~~~~~~~~~~~~~~~~~~~~
            1 errors, 0 warnings
            """.trimIndent())

    }

    // Helpers

    private fun expectIssues(expectedIssues: List<Issue>) {
        issues.clear()
        issues.addAll(expectedIssues)
    }

    // Stubs

    private val certificateStub: TestFile = kotlin("""
        package com.joaocruz04.certificate

        data class Certificate(val sha: String, val expirationDate: String?)

    """)

    // Lint Detector Test interface

    override fun getDetector(): Detector = detector

    override fun getIssues(): MutableList<Issue> = issues

}
