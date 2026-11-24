public class ValidationException extends PlacementException {
    private final String fieldName;

    public ValidationException(String fieldName, String message) {
        super("Validation failed for field [" + fieldName + "]: " + message);
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }
}
