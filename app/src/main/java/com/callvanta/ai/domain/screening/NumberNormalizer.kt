package com.callvanta.ai.domain.screening

/**
 * Canonicalizes Indian phone numbers to their 10-digit national significant
 * number. Other explicit international numbers remain in +E.164 form.
 */
object NumberNormalizer {
    private val formatting = Regex("[\\s().-]")

    fun normalizeExact(input: String): String? {
        val parsed = parse(input) ?: return null
        val (digits, hadPlus) = parsed
        return when {
            digits.length == 10 -> digits
            digits.length == 11 && digits.startsWith("0") -> digits.drop(1)
            digits.length == 12 && digits.startsWith("91") -> digits.drop(2)
            hadPlus && digits.length in 8..15 -> "+$digits"
            else -> null
        }
    }

    fun normalizeIncoming(input: String): String? = normalizeExact(input)

    fun normalizePrefix(input: String): String? {
        val parsed = parse(input) ?: return null
        var (digits, hadPlus) = parsed
        if (digits.startsWith("0") && digits.length in 7..11) digits = digits.drop(1)

        if (digits.startsWith("91") && (hadPlus || digits.length > 10)) {
            digits = digits.drop(2)
            hadPlus = false
        } else if (hadPlus) {
            if (digits.length !in 7..14) return null
            return "+$digits"
        }

        if (digits.length !in 6..10) return null
        return digits
    }

    private fun parse(input: String): Pair<String, Boolean>? {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) return null
        val hadPlus = trimmed.startsWith("+")
        if (trimmed.any { !(it.isDigit() || it == '+' || it.isWhitespace() || it in "().-") }) return null
        if (trimmed.count { it == '+' } > 1 || ('+' in trimmed && !hadPlus)) return null

        val digits = trimmed.replace(formatting, "").removePrefix("+")
        if (digits.isEmpty() || !digits.all { it.isDigit() }) return null
        return digits to hadPlus
    }
}
