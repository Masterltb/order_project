package com.restaurant.backend.common.exception;

/**
 * Exception thrown when a requested resource is not found.
 * Returns HTTP 404.
 */
public class ResourceNotFoundException extends BusinessException {

    private final String resourceType;
    private final Object resourceId;

    public ResourceNotFoundException(String resourceType, Object resourceId) {
        super(String.format("%s with id %s not found", resourceType, resourceId));
        this.resourceType = resourceType;
        this.resourceId = resourceId;
    }

    public String getResourceType() {
        return resourceType;
    }

    public Object getResourceId() {
        return resourceId;
    }
}
