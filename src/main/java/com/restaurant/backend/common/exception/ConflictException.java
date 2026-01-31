package com.restaurant.backend.common.exception;

/**
 * Exception thrown when there is a version conflict (optimistic locking).
 * Returns HTTP 409.
 */
public class ConflictException extends BusinessException {

    public ConflictException(String message) {
        super(message);
    }

    public ConflictException(String resourceType, Object resourceId, Integer expectedVersion, Integer actualVersion) {
        super(String.format("%s with id %s has been modified. Expected version %d but actual is %d. Please retry.",
                resourceType, resourceId, expectedVersion, actualVersion));
    }
}
