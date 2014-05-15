package com.exadel.service;

public interface GDriveService {

    void uploadFile(java.io.File file, String title, String description, String mime);
    void initAuthToken(String code);
    String getAuthorizationUrl();
    boolean hasAccessToken();
    public String getAccessToken();
    public String getRefreshToken();
}
