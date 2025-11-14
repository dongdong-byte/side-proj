package kim.onbidproperty.exception;

public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message) {
        super(message);
    }

    public DuplicateResourceException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format(" 이미 존재하는 %s입니다. %s : '%s'", resourceName, fieldName, fieldValue));
    }

}
