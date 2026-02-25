package sv.project.e_commerce.exception;

import java.util.Map;
import java.util.Objects;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.validation.ConstraintViolation;
import lombok.extern.slf4j.Slf4j;
import sv.project.e_commerce.dto.response.ApiResponse;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // private static final String MIN_ATTRIBUTE = "min";

    @ExceptionHandler(value = Exception.class)
    @SuppressWarnings("rawtypes")
    ResponseEntity<ApiResponse> handlingRuntimeException(RuntimeException ex) {
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode());
        apiResponse.setMessage(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage());
        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(value = AppException.class)
    @SuppressWarnings("rawtypes")
    ResponseEntity<ApiResponse> handlingAppException(AppException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        ApiResponse apiResponse = new ApiResponse();

        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());

        return ResponseEntity.status(errorCode.getStatusCode()).body(apiResponse);
    }

    // @ExceptionHandler(value = AccessDeniedException.class)
    // @SuppressWarnings("rawtypes")
    // ResponseEntity<ApiResponse>
    // handlingAccessDeniedException(AccessDeniedException ex) {
    // ErrorCode errorCode = ErrorCode.UNAUTHORIZED;
    // return ResponseEntity.status(errorCode.getStatusCode())
    // .body(
    // ApiResponse.builder()
    // .code(errorCode.getCode())
    // .message(errorCode.getMessage())
    // .build());
    // }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationException( MethodArgumentNotValidException ex) {
        String errorKey = ex.getBindingResult()
                .getFieldError()
                .getDefaultMessage(); // PASSWORD_INVALID

        ErrorCode errorCode = ErrorCode.valueOf(errorKey);

        return ResponseEntity
                .status(errorCode.getStatusCode())
                .body(ApiResponse.builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build());
    }

    // private String mapAttribute(String message, Map<String, Object> attributes) {
    //     String minValue = String.valueOf(attributes.get(MIN_ATTRIBUTE));
    //     return message.replace("{" + MIN_ATTRIBUTE + "}", minValue);
    // }
}
