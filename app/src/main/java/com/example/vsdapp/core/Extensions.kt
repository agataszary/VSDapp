package com.example.vsdapp.core

import java.security.MessageDigest

fun String.sha512(): String {
    return MessageDigest.getInstance("SHA-512").digest(this.toByteArray()).fold(StringBuilder(), { sb, it -> sb.append("%02x".format(it)) }).toString()
}