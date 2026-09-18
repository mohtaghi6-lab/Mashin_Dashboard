package peugeot.platform.android.diagnostics

object ErrorScanner {

    private val errors =
        mutableListOf<ErrorCode>()

    fun scan(): List<ErrorCode> {
        return errors.toList()
    }

    fun addError(
        error: ErrorCode
    ) {
        errors.add(error)
    }

    fun clearErrors() {
        errors.clear()
    }

    fun hasErrors(): Boolean {
        return errors.isNotEmpty()
    }

    fun getErrorCount(): Int {
        return errors.size
    }

    fun getCriticalErrors(): List<ErrorCode> {
        return errors.filter {
            it.severity == Severity.CRITICAL
        }
    }
}
