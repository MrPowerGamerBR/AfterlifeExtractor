package com.mrpowergamerbr.afterlifeextractor

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.parameters.arguments.argument
import java.io.File

class AfterlifeExtractor : CliktCommand() {
    val containerFile by argument()
    val dumpFolder by argument()

    override fun run() {
        val byteBuffer = AfterlifeByteBuffer.fromByteArray(File(containerFile).readBytes())

        val foundChunkCount = mutableMapOf<String, Int>()

        val dumpFolder = File(dumpFolder)
        dumpFolder.mkdirs()

        while (byteBuffer.hasRemaining()) {
            val header = byteBuffer.readString(4)
            val size = byteBuffer.readInt()
            val currentPosition = byteBuffer.position

            println("Header: $header; Size: $size; Position: $currentPosition (${currentPosition.toHexString()})")

            val i = (foundChunkCount.getOrDefault(header, 0)).also { foundChunkCount[header] = it + 1 }

            // The CSER header is a bit weird
            // The first 16 bytes seems to be some attributes associated with it
            if (header == "CSER") {
                val unknown = byteBuffer.readInt() // Seems to always be 2, maybe version?
                val sizeInBytes = byteBuffer.readInt() // Relative to the position AFTER the header and the size, but before the previous unknown field
                println("size in bytes: $sizeInBytes")
                val unknown2 = byteBuffer.readInt() // Seems to always be 0
                val unknown3 = byteBuffer.readInt() // Seems to always be 0

                val sizeInBytesNotIncludingHeader = sizeInBytes - 16
                val content = byteBuffer.readBytes(sizeInBytesNotIncludingHeader)

                // CSER's (which is RESC... which I suppose are RESOURCES) have different types of data within it
                // One of them is BMP files
                fun checkMagicValues(magicValues: List<Byte>): Boolean {
                    for ((index, magicValue) in magicValues.withIndex()) {
                        if (content[index] != magicValue)
                            return false
                    }

                    return true
                }

                if (checkMagicValues(listOf(0x42.toByte(), 0x4D.toByte()))) {
                    File(dumpFolder, "$header.$i.bmp").writeBytes(content)
                } else if (checkMagicValues(listOf(0x00.toByte(), 0x00.toByte(), 0x01.toByte(), 0x00.toByte()))) {
                    File(dumpFolder, "$header.$i.ico").writeBytes(content)
                } else if (checkMagicValues(listOf(0x00.toByte(), 0x00.toByte(), 0x02.toByte(), 0x00.toByte()))) {
                    File(dumpFolder, "$header.$i.cur").writeBytes(content)
                } else {
                    File(dumpFolder, "$header.$i.raw").writeBytes(content)
                }
            } else {
                // Dump as is
                val data = byteBuffer.readBytes(size)
                File(dumpFolder, "$header.$i.raw").writeBytes(data)
            }
        }
    }
}

fun main(args: Array<String>) = AfterlifeExtractor().main(args)