package io.github.artsok.training

import java.util.*


/**
 * Generate random String
 */
fun ClosedRange<Char>.randomString(lenght: Int) =
        (1..lenght).map {
            (Random().nextInt(endInclusive.toInt() - start.toInt()) + start.toInt()).toChar()
        }.joinToString("")