package ru.yandex.practicum.filmorate.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(NotFoundException exception) {
        log.warn("Объект не найден: {}", exception.getMessage());
        return new ErrorResponse(exception.getMessage());
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidation(ValidationException exception) {
        log.warn("Ошибка валидации: {}", exception.getMessage());
        return new ErrorResponse(exception.getMessage());
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(Throwable exception) {
        log.error("Непредвиденная ошибка", exception);
        return new ErrorResponse("Произошла непредвиденная ошибка.");
    }
}