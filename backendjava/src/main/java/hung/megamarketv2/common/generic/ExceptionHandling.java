package hung.megamarketv2.common.generic;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import hung.megamarketv2.common.generic.exceptions.CommonExceptions.UnexpectedErrorException;
import hung.megamarketv2.common.generic.outcomes.Outcome;

@RestControllerAdvice
public class ExceptionHandling {

    public static record BeanValidationError(List<String> errors) {
    }

    @ExceptionHandler(UnexpectedErrorException.class)
    public ResponseEntity<Outcome<String>> handleUnexpectedErrorException(UnexpectedErrorException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Outcome.ofError(e.unexpectedError));
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<Outcome<BeanValidationError>> handleBeanvalidationException(MethodArgumentNotValidException e,
            WebRequest request) {

        List<String> errors = e.getFieldErrors().stream().map(f -> f.getDefaultMessage()).toList();
        BeanValidationError beanValidationError = new BeanValidationError(errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Outcome.ofError(beanValidationError));
    }
}
