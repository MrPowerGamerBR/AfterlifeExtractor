package com.mrpowergamerbr.afterlifeextractor

import java.nio.ByteBuffer
import java.nio.ByteOrder

class AfterlifeByteBuffer private constructor(private val byteBuffer: ByteBuffer) {
    companion object {
        fun fromByteArray(byteArray: ByteArray) = AfterlifeByteBuffer(ByteBuffer.wrap(byteArray))
        fun fromByteBuffer(byteBuffer: ByteBuffer) = AfterlifeByteBuffer(byteBuffer)
    }

    init {
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN)
    }

    var position: Int
        get() = byteBuffer.position()
        set(value) {
            byteBuffer.position(value)
        }

    fun readByte(): Byte {
        return byteBuffer.get()
    }

    fun readInt(): Int {
        return byteBuffer.getInt()
    }

    fun readBytes(length: Int): ByteArray {
        val bytes = ByteArray(length)
        byteBuffer.get(bytes)
        return bytes
    }

    fun readString(stringLength: Int): String {
        return readBytes(stringLength).toString(Charsets.US_ASCII)
    }

    fun hasRemaining() = byteBuffer.hasRemaining()
}