package dev.lobanov.raynetassignment.utils

/**
 * Headers for client CSV file.
 *
 * @property REG_NUMBER Client registration number
 * @property TITLE Client name
 * @property EMAIL Client email address
 * @property PHONE Client phone number
 * @property ALL List of all required headers
 */
object CsvHeaders {
    const val REG_NUMBER = "regNumber"
    const val TITLE = "title"
    const val EMAIL = "email"
    const val PHONE = "phone"

    val ALL = listOf(REG_NUMBER, TITLE, EMAIL, PHONE)
}