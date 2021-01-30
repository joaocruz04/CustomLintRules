package com.joaocruz04.certificaterule

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import com.android.tools.lint.detector.api.TextFormat
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UElement
import org.jetbrains.uast.getQualifiedName
import org.jetbrains.uast.util.isConstructorCall
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date

class CertificateDetector : Detector(), Detector.UastScanner {

    companion object {
        val EXPIRED_CERT_ISSUE = Issue.create(
            id = "CertificateExpired",
            briefDescription = "Certificate expired!",
            explanation = "This certificate expired. Make sure you included a newer certificate.",
            severity = Severity.ERROR,
            androidSpecific = true,
            priority = 9,
            category = Category.SECURITY,
            implementation = Implementation(CertificateDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )

        val ALMOST_EXPIRED_CERT_ISSUE = Issue.create(
            id = "CertificateNearExpiration",
            briefDescription = "Certificate is close to be expired!",
            explanation = "Please provide a new certificate soon.",
            severity = Severity.WARNING,
            androidSpecific = true,
            priority = 7,
            category = Category.SECURITY,
            implementation = Implementation(CertificateDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )

        val WRONG_DATE_FORMAT_ISSUE = Issue.create(
            id = "WrongCertificateDateFormat",
            briefDescription = "Wrong date pattern",
            explanation = "Please make sure you use the pattern DD/MM/YYYY",
            severity = Severity.ERROR,
            androidSpecific = true,
            priority = 8,
            category = Category.CORRECTNESS,
            implementation = Implementation(CertificateDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }


    override fun getApplicableUastTypes(): List<Class<out UElement>> {
        return listOf(UCallExpression::class.java)
    }

    override fun createUastHandler(context: JavaContext): UElementHandler {
        return object : UElementHandler() {

            private val now = Date().time
            private val dateFormat = SimpleDateFormat("dd/MM/yyyy").apply { isLenient = false }
            private val margin = 10368000000L // ~ 4 months

            override fun visitCallExpression(node: UCallExpression) {
                if (node.isConstructorCall()) {
                    if (node.classReference?.getQualifiedName()?.startsWith("com.joaocruz04.certificate.Certificate") == true) {
                        try {
                            val date = node.getArgumentForParameter(1)?.evaluate() as String
                            if (date.isEmpty()) return
                            val millis = dateFormat.parse(date).time
                            if (now >= millis)
                                reportIssue(EXPIRED_CERT_ISSUE, node)
                            else if (millis - now <= margin)
                                reportIssue(ALMOST_EXPIRED_CERT_ISSUE, node)

                        } catch (e: ParseException) {
                            reportIssue(WRONG_DATE_FORMAT_ISSUE, node)
                        } catch (e: Exception) {
                            reportIssue(WRONG_DATE_FORMAT_ISSUE, node)
                        }
                    }
                }
            }

            private fun reportIssue(issue: Issue, node: UCallExpression) {
                context.report(
                    issue = issue,
                    scope = node,
                    location = context.getLocation(node),
                    message = issue.formattedExplanation()
                )
            }

            private fun Issue.formattedExplanation(): String {
                return """${getBriefDescription(TextFormat.TEXT)}
                    ${getExplanation(TextFormat.TEXT)}
                """.trimIndent()
            }
        }
    }
}
