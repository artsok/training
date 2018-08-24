package io.github.artsok.training.rules

import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import kotlin.annotation.AnnotationTarget.FUNCTION
import kotlin.annotation.AnnotationTarget.TYPE


@Retention(AnnotationRetention.RUNTIME)
@ExtendWith(DriverExtension::class)
internal annotation class Driver

@Retention(AnnotationRetention.RUNTIME)
@ExtendWith(DriverInjectResolver::class)
internal annotation class DriverResolver

@Retention(AnnotationRetention.RUNTIME)
@ExtendWith(RotateCondition::class)
internal annotation class Rotate

@Test
@Tag("android")
@Retention(AnnotationRetention.RUNTIME)
@Target(FUNCTION, TYPE)
internal annotation class AndroidTest

@Test
@Tag("ios")
@Retention(AnnotationRetention.RUNTIME)
@Target(FUNCTION, TYPE)
internal annotation class IOSTest



