package org.exxjofr.timetracker

import org.apache.logging.log4j.LogManager
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.FileInputStream

class ExcelWriter(var path: String) {
    private val logger = LogManager.getLogger("ExcelWriter")
    fun writeDay (day: BuisnessDay) {
        val date = day.date

        FileInputStream(path).use { fis ->
            logger.info("Writing business day to Excel: $day")

            val workbook = XSSFWorkbook(fis)

            val sheet = workbook.getSheet(concertMoth(day.date.monthValue))
            val row = sheet.getRow(date.dayOfMonth +2) //start with index 4 at day 1


            setExcelTime( row, 3, day.startTime.hour.toLong(), day.startTime.minute.toLong())
            setExcelTime( row, 4, day.endTime.hour.toLong(), day.endTime.minute.toLong())
            val breakHours = day.breakDuration.toHours()
            val breakMinutes = day.breakDuration.minusHours(breakHours).toMinutes()
            setExcelTime( row,7, breakHours, breakMinutes)
            workbook.creationHelper.createFormulaEvaluator().evaluateAll()
            workbook.setForceFormulaRecalculation(true)
            logger.info("Saving changes to Excel.")
            java.io.FileOutputStream(path).use { fos ->
                workbook.setForceFormulaRecalculation(true)
                workbook.write(fos)
            }
            workbook.close()
        }


    }

    private fun setExcelTime(row: Row, cellIndex: Int, hours: Long, minutes: Long) {
        val excelValue = hours / 24.0 + minutes / 1440.0
        val cell = row.getCell(cellIndex)
        cell.setCellValue(excelValue)
    }

    private fun concertMoth(month: Int) : String {
        return when (month) {
            1 -> "Januar"
            2 -> "Februar"
            3 -> "März"
            4 -> "April"
            5 -> "Mai"
            6 -> "Juni"
            7 -> "Juli"
            8 -> "August"
            9 -> "September"
            10 -> "Oktober"
            11 -> "November"
            12 -> "Dezember"
            else -> "Error"
        }
    }
}