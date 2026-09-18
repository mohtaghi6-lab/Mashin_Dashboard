package peugeot.platform.android.diagnostics

data class ErrorCode(
    val code: String,
    val description: String,
    val severity: Severity,
    val active: Boolean = true
)

enum class Severity {
    INFO,
    WARNING,
    CRITICAL
}
