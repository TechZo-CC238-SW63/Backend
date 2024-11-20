package com.techzo.cambiazo.iam.interfaces.rest.resources;

public record AuthenticatedUserResource(
        Long id,
        String username,
        String name,
        String phoneNumber,
        String profilePicture,
        Boolean isActive,
        Boolean isGoogleAccount,
        String token
) {
    public AuthenticatedUserResource {
        if (isGoogleAccount == null) {
            isGoogleAccount = false;
        }
    }
}