package kim.onbidproperty.exception;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
//전역 예외처리기 GlobalExceptionHandler
//    역할 : 코드 유효성검사 오류만 전담으로한다
//    왜 필요한가? : 지금은 컨트롤러에 메서드가 별로 없어서 메서드마다 try-catch로 예외처리를했지만 메서드가 많다면 일일히 다 할수가 없다
//    그래서 전체 컨트롤러에서 코드 유효성만 전담하는 예외처리기가 필요하다
//    비유를하자면 이전까지는  RestController가 요리 + 재료 품질 검사를 했다면 전역 예외 처리기라는 재료 품질검사 전문가를 도입한거다 그리고 rest는 요리만 전념할수 있게 되었고
//    지금은 RestController가 try-catch로 예외처리를했서 예외처리기는 안전망역할을한다.


//    필요한개념
//    1.@Slf4j-> log란 이름에 logger객체가 생성된다. 이걸로 log.info를 logger객체 작성할 필요 없이 작성한다
//    2.@ControllerAdvice-> 모든 컨트롤러 (REst 포함)에서 발생하는 예외를 전역적으로 처리하는 역할을 하도록 spring에 알려준다
//    3.@ExceptionHandler -> @ControllerAdvice 클래스내에서 특정타입의 예외를 처리할 메서드를 지정한다.
//    괄호안에 지정도니 예외 클래스IllegalAccessException.class 와 일치 하거나 그 클래스의 자식 클래스인 예외가 발생하면 , sts가 이 어노테이션이 붙은 메서드를호풀하여 예외를 처리한다.


//    핸들러 메서드
//    1.



//    // 1. 리소스를 찾을 수 없음 (404 Not Found) - 커스텀 예외
//
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String ,Object>> handleResourceNotFoundException(ResourceNotFoundException exception){
        log.warn("Global Exception: 리소스를 찾을 수 없음 (404) - {}",exception.getMessage());
        Map<String ,Object> response = new HashMap<>();
        response.put("success",false);
        response.put("message", exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
//    2.2. 중복 리소스 (409 Conflict) - 커스텀 예외
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<Map<String ,Object>> handleDuplicateResourceException(DuplicateResourceException exception){
        log.warn("Global Exception: 중복 리소스 (409) - {}", exception.getMessage());
        Map<String ,Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", exception.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }
//    3. 잘못된 인자 (400 Bad Request)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String ,Object>> handleIllegalArgumentException(IllegalArgumentException exception){
        log.warn("Global Exception: 잘못된 인자 (400) - {}", exception.getMessage());
        Map<String ,Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

//    4. @Valid 유효성 검사 실패 (400 Bad Request)
//    컨트롤러에ㅓ 처리하기 어려운 유효성 검사 예외를 여기서 일괄처리합니다.
//    @valid 제약 조건에 붙은 조건을 만족하지 못할(나이가 1000살 인경우)경우 발동
//    응답: HTTP 상태 코드 **400 Bad Request**를 반환하며, 응답 본문에 어떤 필드에서 어떤 오류가 발생했는지(errors Map)를 담아 클라이언트에게 전달합니다. 이는 유효성 검사 오류에 대한 표준적인 REST API 응답 방식입니다.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String ,Object>>handleValidationExceptions(MethodArgumentNotValidException exception ){
        Map<String ,String> errors = new HashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField() ,error.getDefaultMessage());
        } );
        log.warn("Global Exception: 유효성 검사 실패 (400) - {}", errors);
        // 응답 형식을 통일
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", "입력값 유효성 검사 실패");
        response.put("errors", errors); // 필드별 상세 오류 추가
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
//   5. IO 예외 (500 Internal Server Error)
//    가장 최상위 클래스인 Exception.class
//    1,2에서 명시하지 못한 오류 발생시 호출되는 최종 안전망
//    응답: HTTP 상태 코드 **500 Internal Server Error**를 반환합니다. 클라이언트에게는 자세한 오류 내용 대신 일반적인 오류 메시지를 전달하여 서버 내부 정보를 숨기는 것이 보안상 좋습니다.
    @ExceptionHandler(IOException.class)
    public ResponseEntity<Map<String, Object>>handleIOException(IOException exception){
        log.error("Global Exception: 처리되지 않은 서버 오류 (500)", exception);
        Map<String ,Object> response = new HashMap<>();
        response.put("success",false);
        response.put("message", "API 호출 중 오류가 발생했습니다:");

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);

    }

//    6. 정적 리소스 없음 (404)
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Void> handleNoResourceFoundException(NoResourceFoundException ex) {
//        favicon 같은 정적 리소스 404는 무시
        if (ex.getMessage().contains("favicon")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        log.warn("Global Exception: 리소스를 찾을 수 없음 (404) - {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    // 7. 그 외 모든 예외 (500 Internal Server Error) - 최종 안전망
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String,Object>> handleGlobalException(Exception ex) {


        log.error("Global Exception: 처리되지 않은 서버 오류 (500)", ex);
        Map<String ,Object> response = new HashMap<>();
        response.put("success",false);
        response.put("message", "서버 오류가 발생했습니다:"+ex.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }
//    8.잘못된 HTTP 메소드 요청 (405)(추가)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, Object>> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {

       log.warn("Global Exception: 잘못된 HTTP 메소드 요청 (405) - {} (supported) : {}", ex.getMessage() ,ex.getSupportedHttpMethods());
       String message = String.format("허용되지 않는 요청 방식입니다. (지원 : %s)" ,ex.getSupportedHttpMethods());
        return createErrorResponse(HttpStatus.METHOD_NOT_ALLOWED, message);
    }
//    9.잘못된 json 요청 본문(400)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public  ResponseEntity<Map<String, Object>>handleJsonParseException(HttpMessageNotReadableException ex){
        log.warn("Global Exception: 잘못된 JSON 요청 본문 (400) - {}", ex.getMessage());
        return createErrorResponse(HttpStatus.BAD_REQUEST, "잘못된 JSON 요청 본문입니다.");

    }
    // (Helper Method) 공통 응답 생성기
    private ResponseEntity<Map<String, Object>> createErrorResponse(HttpStatus status, String message) {
    Map<String ,Object> response = new HashMap<>();
    response.put("success", false);
    response.put("message", message);
    return ResponseEntity.status(status).body(response);

    }
}
