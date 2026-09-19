package peugeot.platform.android.diagnostics

import peugeot.platform.android.vehicle.VehicleData


object ErrorScanner {


    private val errors =
        mutableListOf<ErrorCode>()



    fun scan(): List<ErrorCode> {

        return errors.toList()

    }



    fun scanVehicle(
        data: VehicleData
    ): List<ErrorCode> {


        errors.clear()



        if (!data.canConnected) {

            errors.add(
                ErrorCode(
                    code = "CAN001",
                    title = "CAN Connection",
                    description = "ارتباط CAN برقرار نیست",
                    severity = Severity.WARNING
                )
            )
        }



        if (data.checkEngine) {

            errors.add(
                ErrorCode(
                    code = "ENG001",
                    title = "Check Engine",
                    description = "خطای موتور ثبت شده",
                    severity = Severity.CRITICAL
                )
            )
        }



        if (data.absWarning) {

            errors.add(
                ErrorCode(
                    code = "ABS001",
                    title = "ABS",
                    description = "خطای سیستم ABS",
                    severity = Severity.WARNING
                )
            )
        }



        if (data.airbagWarning) {

            errors.add(
                ErrorCode(
                    code = "AIR001",
                    title = "Airbag",
                    description = "خطای ایربگ",
                    severity = Severity.CRITICAL
                )
            )
        }



        return scan()
    }




    fun addError(
        error: ErrorCode
    ) {


        if (!errors.contains(error)) {

            errors.add(error)

        }

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
