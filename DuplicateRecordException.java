public class DuplicateRecordException extends PlacementException {
    private final String recordId;

    public DuplicateRecordException(String entityType, String recordId) {
        super(entityType + " with identifier '" + recordId + "' already exists in the system.");
        this.recordId = recordId;
    }

    public String getRecordId() {
        return recordId;
    }
}
