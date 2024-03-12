package com.pqc.qr.utils

import java.util.regex.Pattern

/**
 * Created by Kevin Patoliya.
 */
object EditTextValidator {
    // Regular expression for a simple email validation
    private val EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    private val urlPattern = "^((http|https)://)?([a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}(/\\S*)?$"
    private val phonePattern = "^\\(?(\\d{3})\\)?[- ]?(\\d{3})[- ]?(\\d{4})\$"
    private val appPattern = "^((http|https)://)?((play.google.|apps.apple.)com)?([a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}(/\\S*)?$"
    // Pattern object for the email regex
    private val pattern = Pattern.compile(EMAIL_REGEX)
    private val urlpattern = Pattern.compile(urlPattern)
    private val phonepattern = Pattern.compile(phonePattern)
    private val apppattern = Pattern.compile(appPattern)
    fun isValidAppUrl(appstringe:String):Boolean{
        if (!isTextFieldValid(appstringe)) {
            return false
        }
        // Create matcher object
        val matcher = apppattern.matcher(appstringe)
        // Check if the email matches the pattern
        return matcher.matches()
    }
    /**
     * Validates an phone number address.
     *
     * @param phone number The phone number address to validate.
     * @return true if the phone number is valid, false otherwise.
     */
    fun isValidPhoneNumber(phonenumber: String?): Boolean {
        if (!isTextFieldValid(phonenumber)) {
            return false
        }
        // Create matcher object
        val matcher = phonepattern.matcher(phonenumber)
        // Check if the phone-number matches the pattern
        return matcher.matches()
    }
    /**
     * Validates an url address.
     *
     * @param url The url address to validate.
     * @return true if the url is valid, false otherwise.
     */
    fun isValidUrl(url: String): Boolean {
        if (!isTextFieldValid(url)) {
            return false
        }
        // Create matcher object
        val matcher = urlpattern.matcher(url)
        // Check if the url matches the pattern
        return matcher.matches()
    }

    /**
     * Validates an email address.
     *
     * @param email The email address to validate.
     * @return true if the email is valid, false otherwise.
     */

    fun isValidEmail(email: String?): Boolean {
        if (!isTextFieldValid(email)) {
            return false
        }
        // Create matcher object
        val matcher = pattern.matcher(email)
        // Check if the email matches the pattern
        return matcher.matches()
    }

    /**
     * Checks if a text field is not null or empty.
     *
     * @param textField The text field to validate.
     * @return true if the text field is not null and not empty, false otherwise.
     */
    fun isTextFieldValid(textField: String?): Boolean {
        return textField != null && !textField.trim { it <= ' ' }.isEmpty()
    }

    /**
     * Checks if any of the provided text fields is null or empty.
     *
     * @param textFields The text fields to validate.
     * @return true if any of the text fields is not null and not empty, false otherwise.
     */
    fun areTextFieldsValid(vararg textFields: String?): Boolean {
        if (textFields == null) {
            return false
        }
        for (textField in textFields) {
            if (textField == null || textField.trim { it <= ' ' }.isEmpty()) {
                return false // If any field is null or empty, return false
            }
        }
        return true // AllQrList fields are not null and not empty
    }
}