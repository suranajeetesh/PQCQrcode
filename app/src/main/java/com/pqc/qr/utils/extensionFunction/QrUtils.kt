package com.pqc.qr.utils.extensionFunction

import android.content.Intent
import android.content.Intent.CATEGORY_BROWSABLE
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.net.Uri
import android.provider.ContactsContract
import androidx.fragment.app.FragmentActivity


fun FragmentActivity.resultWebString(data: String) {
    startActivity(Intent(Intent.ACTION_VIEW).apply {
        addCategory(CATEGORY_BROWSABLE)
        addFlags(FLAG_ACTIVITY_NEW_TASK)
        setData(Uri.parse(data))
    })
}

fun FragmentActivity.resultEmailString(data: String) {
    val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
        setData(Uri.parse(data))
    }
    return startActivity(emailIntent)
}

fun FragmentActivity.resultPhoneString(data: String) {
    val intent = Intent(Intent.ACTION_DIAL)
    intent.setData(Uri.parse(data))
    return startActivity(intent)
}

fun FragmentActivity.resultContactString(data: String) {
    val contactIntent = Intent(Intent.ACTION_INSERT_OR_EDIT).apply {
        type = ContactsContract.Contacts.CONTENT_ITEM_TYPE
        putExtra(
            ContactsContract.Intents.Insert.NAME,
            data.substringAfter("FN:").substringBefore("\n")
        )
        putExtra(
            ContactsContract.Intents.Insert.PHONE,
            data.substringAfter("TEL:").substringBefore("\n")
        )
        putExtra(
            ContactsContract.Intents.Insert.EMAIL,
            data.substringAfter("EMAIL:").substringBefore("\n")
        )
    }
    return startActivity(contactIntent)
}

fun FragmentActivity.resultSmsString(data: String) {
    val splitResult = data.split(":")
    if (splitResult.size >= 3 && splitResult[0].lowercase().startsWith("smsto")) {
        val phoneNumber = splitResult[1]
        val message = splitResult.subList(2, splitResult.size).joinToString(":")
        val smsIntent = Intent(Intent.ACTION_VIEW).apply {
            putExtra("sms_body", message)
        }
        smsIntent.data = Uri.parse("sms:${phoneNumber}")
        return startActivity(smsIntent)
    }
}

fun String.createSmsString(data: String): String {
    return "smsto:$this:$data"
}

fun String.createPhoneNumberString(data: String): String {
    return "tel:$data"


}

fun String.createEMailString(subject: String, data: String): String {
    return "mailto:$this?subject=$subject&body=$data"
}

fun String.createVCardString(phoneNumber: String, name: String, email: String? = null): String {
    val formattedEmail = email?.let { "EMAIL:$it" } ?: ""
    return """
        BEGIN:VCARD
        VERSION:3.0
        FN:$name
        TEL:$phoneNumber
        $formattedEmail
        END:VCARD
    """.trimIndent()
}
fun String.createPlayStoreDeepLink(isDefault: Boolean = false): String {
    return if (isDefault) {
        "https://apps.apple.com/us/app/id$this"
    } else {
        "https://play.google.com/store/apps/details?id=$this"
    }
}
fun String.extractPackageNameFromPlayStoreUrl(): String? {
    val regex = Regex("id=([^&]+)")
    val matchResult = regex.find(this)
    return matchResult?.groupValues?.get(1)
}

fun String.isPackageNameValidPlayStore(): Boolean {
    val packageNameRegex = Regex("^[a-zA-Z][a-zA-Z0-9_]*(\\.[a-zA-Z][a-zA-Z0-9_]*)*$")
    return packageNameRegex.matches(this)
}

fun String.extractPackageNameFromAppStoreUrl(): String? {
    val regex = Regex("id(\\d+)")
    val matchResult = regex.find(this)
    return matchResult?.groupValues?.get(1)
}

fun String.isPackageNameValidAppStore(): Boolean {
    val bundleIdRegex = Regex("^[0-9]+$")
    return bundleIdRegex.matches(this)
}

private const val overlayTransparency: Float = 0.3f // replace with your desired transparency value
private const val overlayToQRCodeRatio: Float = 0.9f // replace with your desired ratio

fun getQRCodeWithOverlay(qrcode: Bitmap, overlay: Bitmap): Bitmap {
    val scaledOverlay = scaleOverlay(qrcode, overlay)
    val deltaHeight = qrcode.height - scaledOverlay.height
    val deltaWidth = qrcode.width - scaledOverlay.width
    val combined = Bitmap.createBitmap(qrcode.width, qrcode.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(combined)
    canvas.drawBitmap(qrcode, 0f, 0f, null)
    val overlayPaint = android.graphics.Paint().apply {
        xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)
        alpha = (overlayTransparency * 255).toInt()
    }
    canvas.drawBitmap(
        scaledOverlay,
        (deltaWidth / 2).toFloat(),
        (deltaHeight / 2).toFloat(),
        overlayPaint
    )
    return combined
}

private fun scaleOverlay(qrcode: Bitmap, overlay: Bitmap): Bitmap {
    val scaledWidth = (qrcode.width * overlayToQRCodeRatio).toInt()
    val scaledHeight = (qrcode.height * overlayToQRCodeRatio).toInt()
    val scaledOverlay = Bitmap.createBitmap(scaledWidth, scaledHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(scaledOverlay)
    val overlayScaled = Bitmap.createScaledBitmap(overlay, scaledWidth, scaledHeight, true)
    canvas.drawBitmap(overlayScaled, 0f, 0f, android.graphics.Paint())
    return scaledOverlay
}
