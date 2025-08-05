package com.pushminds.common

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class TestController {

    @GetMapping
    fun hello(): String = "Hello World"
}
