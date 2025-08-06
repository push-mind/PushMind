package com.pushminds.common

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(e: IllegalArgumentException): ResponseEntity<String> {
        // 간단한 예외 처리, 필요에 따라 에러 응답 DTO를 만들어 사용 가능
        return ResponseEntity(e.message, HttpStatus.CONFLICT)
    }
}


