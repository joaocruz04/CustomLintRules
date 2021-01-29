package com.joaocruz04.customlintrules.rules

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.Category.Companion.COMPLIANCE
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import com.android.tools.lint.detector.api.SourceCodeScanner
import com.android.tools.lint.detector.api.TextFormat
import org.jetbrains.uast.UClass
import org.jetbrains.uast.UElement
import java.util.EnumSet

class DealerDetector : Detector(), SourceCodeScanner {

    override fun getApplicableUastTypes(): List<Class<out UElement>> = listOf(UClass::class.java)

    override fun createUastHandler(context: JavaContext): UElementHandler {
        return DealerPatternHandler(context)
    }

    class DealerPatternHandler(private val context: JavaContext) : UElementHandler() {
        private val regex = Regex("(Dealer|Dealers)(?!ship)(?=[A-Z|_])|(Dealer\$)|(Dealers\$)")

        override fun visitClass(node: UClass) {
            node.name?.takeIf { regex.containsMatchIn(it) }?.run {
                reportIssue(context, node)
            }
        }

        private fun reportIssue(context: JavaContext, node: UElement) {
            context.report(
                issue = DEALER_NAME_ISSUE,
                scope = node,
                location = context.getLocation(node),
                message = DEALER_NAME_ISSUE.getBriefDescription(TextFormat.TEXT)
            )
        }
    }

    companion object {
        val DEALER_NAME_ISSUE = Issue.create(
            id = "Dealer naming detector",
            briefDescription = "Forbidden use of word ´Dealer´ in class naming.",
            explanation = "Please consider use the word ´Dealership naming instead.",
            category = COMPLIANCE,
            priority = 5,
            severity = Severity.ERROR,
            implementation = Implementation(DealerDetector::class.java, EnumSet.of(Scope.JAVA_FILE))
        )
    }
}
