public class RecordNotFoundException extends PlacementException {
    private final String recordId;

    public RecordNotFoundException(String entityType, String recordId) {
        super(entityType + " with identifier '" + recordId + "' was not found.");
        this.recordId = recordId;
    }

    public String getRecordId() {
        return recordId;
    }
}
