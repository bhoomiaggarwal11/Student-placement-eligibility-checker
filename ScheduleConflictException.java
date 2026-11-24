public class ScheduleConflictException extends PlacementException {
    private final String conflictingResource;

    public ScheduleConflictException(String conflictingResource, String message) {
        super("Scheduling conflict on [" + conflictingResource + "]: " + message);
        this.conflictingResource = conflictingResource;
    }

    public String getConflictingResource() {
        return conflictingResource;
    }
}
