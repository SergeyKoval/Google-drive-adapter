package com.exadel;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.ParentReference;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static final String clientId = "553246933889-agpcqbmg17febkrhg5jfv7p1mq1tgkn9.apps.googleusercontent.com";
    public static final String clientSecret = "5Loi-tzE3G5OAiUzrJaS3t0S";

    public static final String accessToken = "ya29.FwAMZxeD8CoyMhgAAAD1Wt3gdmjKsyBagr6A8O_ED7NRV9dKr-wuAsTttVjEWQ";
    public static final String refreshToken = "1/vLTFb9WAcDD-EuEWzwge31g1q1m_y47bYdMkwECxcZ0";

    public static final String GOOGLE_FOLDER_MIME = "application/vnd.google-apps.folder";

    public static void main(String[] args) throws Exception {
        /* INITIALIZATION SECTION */
        HttpTransport httpTransport = new NetHttpTransport();
        JsonFactory jsonFactory = new JacksonFactory();

        GoogleCredential credential = new GoogleCredential.Builder().setJsonFactory(jsonFactory)
                .setTransport(httpTransport).setClientSecrets(clientId, clientSecret).build();
        credential.setAccessToken(accessToken);
        credential.setRefreshToken(refreshToken);

        Drive service = new Drive.Builder(httpTransport, jsonFactory, credential).build();
        /* END OF INITIALIZATION SECTION */

        /* EXAMPLES OF USE SECTION */
        /* END OF EXAMPLES OF USE SECTION */
    }

    public static File getElement(Drive service, String elementId) {
        File file = null;

        try {
            file = service.files().get(elementId).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
    }

    public static InputStream downloadFile(Drive service, File file) {
        if (file.getDownloadUrl() != null && file.getDownloadUrl().length() > 0) {
            try {
                HttpResponse resp =
                        service.getRequestFactory().buildGetRequest(new GenericUrl(file.getDownloadUrl()))
                                .execute();
                return resp.getContent();
            } catch (IOException e) {
                // An error occurred.
                e.printStackTrace();
                return null;
            }
        } else {
            // The file doesn't have any content stored on Drive.
            return null;
        }
    }

    public static File insertElement(Drive service, String title, String description,
                                   String parentId, String mimeType, String filePath) {
        File body = new File();
        body.setTitle(title);
        body.setDescription(description);
        body.setMimeType(mimeType);

        // Set the parent folder.
        if (parentId != null && parentId.length() > 0) {
            body.setParents(
                    Arrays.asList(new ParentReference().setId(parentId)));
        }

        File file;

        try {
            if (filePath != null) {
                java.io.File fileContent = new java.io.File(filePath);
                FileContent mediaContent = new FileContent(mimeType, fileContent);

                file = service.files().insert(body, mediaContent).execute();
            } else {
                file = service.files().insert(body).execute();
            }
        } catch (IOException e) {
            file = null;
            e.printStackTrace();
        }

        return file;
    }

    public static void deleteElement(Drive service, String fileId) {
        try {
            service.files().delete(fileId).execute();
        } catch (IOException e) {
            System.out.println("An error occurred: " + e);
        }
    }

    public static List<File> getMetadata(Drive service, String folderId) throws Exception {
        if (!isFolder(service, folderId)) {
            throw new Exception("Folder, please:)");
        }

        List<File> result = new ArrayList<File>();
        Drive.Files.List request = service.files().list().setQ("'" + folderId + "'" + " in parents and trashed=false");

        do {
            try {
                FileList files = request.execute();

                result.addAll(files.getItems());
                request.setPageToken(files.getNextPageToken());
            } catch (IOException e) {
                System.out.println("An error occurred: " + e);
                request.setPageToken(null);
            }
        } while (request.getPageToken() != null &&
                request.getPageToken().length() > 0);

        return result;
    }

    public static boolean isFolder(Drive service, String elementId) {
        File element = getElement(service, elementId);
        return isFolder(element);
    }

    public static boolean isFolder(File element) {
        return element.getMimeType().equals(GOOGLE_FOLDER_MIME);
    }
}
