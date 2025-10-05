package com.photocrm.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.net.URL;
import java.time.Duration;
import java.util.UUID;

@Service
public class S3Service {

    @Autowired
    private S3Client s3Client;

    @Autowired
    private S3Presigner s3Presigner;

    @Value("${aws.s3.bucket-name:photocrm-photos}")
    private String bucketName;

    @Value("${app.photo.cdn-url:}")
    private String cdnUrl;

    public String generatePresignedUploadUrl(UUID tenantId, UUID orderId, String filename, String mimeType) {
        String key = generatePhotoKey(tenantId, orderId, filename);
        
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .contentType(mimeType)
            .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
            .signatureDuration(Duration.ofMinutes(10))
            .putObjectRequest(putObjectRequest)
            .build();

        URL presignedUrl = s3Presigner.presignPutObject(presignRequest).url();
        return presignedUrl.toString();
    }

    public String generatePresignedDownloadUrl(String s3Key) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
            .bucket(bucketName)
            .key(s3Key)
            .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
            .signatureDuration(Duration.ofHours(1))
            .getObjectRequest(getObjectRequest)
            .build();

        URL presignedUrl = s3Presigner.presignGetObject(presignRequest).url();
        return presignedUrl.toString();
    }

    public String generatePhotoUrl(String s3Key) {
        if (cdnUrl != null && !cdnUrl.isEmpty()) {
            return cdnUrl + "/" + s3Key;
        }
        return "https://" + bucketName + ".s3.amazonaws.com/" + s3Key;
    }

    public String generateThumbnailUrl(String s3Key) {
        String thumbnailKey = s3Key.replace("/original/", "/thumbnail/");
        return generatePhotoUrl(thumbnailKey);
    }

    public String generatePreviewUrl(String s3Key) {
        String previewKey = s3Key.replace("/original/", "/preview/");
        return generatePhotoUrl(previewKey);
    }

    public void deleteFile(String s3Key) {
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                .build();

            s3Client.deleteObject(deleteObjectRequest);
        } catch (Exception e) {
            // Log error but don't throw exception
            System.err.println("Failed to delete file from S3: " + s3Key + ", Error: " + e.getMessage());
        }
    }

    public boolean fileExists(String s3Key) {
        try {
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                .build();

            s3Client.headObject(headObjectRequest);
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public long getFileSize(String s3Key) {
        try {
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                .build();

            HeadObjectResponse response = s3Client.headObject(headObjectRequest);
            return response.contentLength();
        } catch (Exception e) {
            return 0;
        }
    }

    private String generatePhotoKey(UUID tenantId, UUID orderId, String filename) {
        return String.format("tenants/%s/orders/%s/original/%s", 
                           tenantId.toString(), orderId.toString(), filename);
    }

    public String generateThumbnailKey(UUID tenantId, UUID orderId, String filename) {
        return String.format("tenants/%s/orders/%s/thumbnail/%s", 
                           tenantId.toString(), orderId.toString(), filename);
    }

    public String generatePreviewKey(UUID tenantId, UUID orderId, String filename) {
        return String.format("tenants/%s/orders/%s/preview/%s", 
                           tenantId.toString(), orderId.toString(), filename);
    }
}