package com.exadel.service.impl;

import com.exadel.service.GDriveService;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;

import java.io.IOException;
import java.util.Arrays;


public class GDriveServiceImpl implements GDriveService {

    private String clientId;
    private String clientSecret;
    private String redirectUri;

    private String accessToken;
    private String refreshToken;

    private HttpTransport httpTransport = new NetHttpTransport();
    private JsonFactory jsonFactory = new JacksonFactory();

    private GoogleAuthorizationCodeFlow flow;
    private GoogleCredential credential;

    private Drive driveApi;

    @Override
    public String getAuthorizationUrl() {
        HttpTransport httpTransport = new NetHttpTransport();
        JsonFactory jsonFactory = new JacksonFactory();

        flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, jsonFactory,
                clientId, clientSecret, Arrays.asList(DriveScopes.DRIVE))
                .setAccessType("offline")
                .setApprovalPrompt("auto").build();

        return flow.newAuthorizationUrl().setRedirectUri(redirectUri).build();
    }

    @Override
    public void initAuthToken(String code) {
        try {
            GoogleTokenResponse tokenResponse = flow.newTokenRequest(code).setRedirectUri(redirectUri).execute();

            accessToken = tokenResponse.getAccessToken();
            refreshToken = tokenResponse.getRefreshToken();

            credential = new GoogleCredential.Builder().setJsonFactory(jsonFactory).setTransport(httpTransport)
                    .setClientSecrets(clientId, clientSecret).build().setFromTokenResponse(tokenResponse);
            driveApi = new Drive.Builder(httpTransport, jsonFactory, credential).build();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void uploadFile(java.io.File file, String title, String description, String mime) {
        if (mime == null) {
            mime = "application/octet-stream";
        }

        File body = new File();
        body.setTitle(title);
        body.setDescription(description);
        body.setMimeType(mime);

        FileContent mediaContent = new FileContent(mime, file);

        try {
            driveApi.files().insert(body, mediaContent).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    @Override
    public boolean hasAccessToken() {
        return (accessToken != null) && (refreshToken != null);
    }
}
