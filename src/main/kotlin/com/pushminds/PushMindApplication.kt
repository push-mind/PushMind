package com.pushminds

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PushMindApplication

fun main(args: Array<String>) {
    runApplication<PushMindApplication>(*args)
}
