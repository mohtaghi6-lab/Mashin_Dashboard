package peugeot.platform.android.diagnostics

class DiagnosticManager {

    fun scan(): List<ErrorCode> {
        return ErrorScanner.scan()
    }

    fun hasErrors(): Boolean {
        return ErrorScanner.hasErrors()
    }

    fun getErrorCount(): Int {
        return ErrorScanner.getErrorCount()
    }

    fun getCriticalErrors(): List<ErrorCode> {
        return ErrorScanner.getCriticalErrors()
    }

    fun clearErrors() {
        ErrorScanner.clearErrors()
    }

    fun addError(
        code: String,
        description: String,
        severity: Severity
    ) {
        ErrorScanner.addError(
            ErrorCode(
                code = code,
                description = description,
                severity = severity
            )
        )
    }
}
