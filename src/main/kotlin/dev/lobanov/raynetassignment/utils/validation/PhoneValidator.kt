package dev.lobanov.raynetassignment.utils.validation

import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil

class PhoneValidator {
    private val phoneUtil: PhoneNumberUtil = PhoneNumberUtil.getInstance()

    fun isValid(phone: String, region: String): Boolean = try {
        val number = phoneUtil.parse(phone, region)
        phoneUtil.isValidNumber(number)
    } catch (e: NumberParseException) {
        false
    }
}