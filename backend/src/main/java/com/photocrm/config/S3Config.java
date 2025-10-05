package com.photocrm.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.net.URI;

@Configuration
public class S3Config {

    @Value("${aws.s3.endpoint:http://localhost:9000}")
    private String s3Endpoint;

    @Value("${aws.s3.access-key:minioadmin}")
    private String accessKey;

    @Value("${aws.s3.secret-key:minioadmin123}")
    private String secretKey;

    @Value("${aws.s3.region:us-east-1}")
    private String region;

    @Bean
    public S3Client s3Client() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
        
        S3Configuration s3Config = S3Configuration.builder()
            .pathStyleAccessEnabled(true) // Required for MinIO
            .build();

        return S3Client.builder()
            .endpointOverride(URI.create(s3Endpoint))
            .region(Region.of(region))
            .credentialsProvider(StaticCredentialsProvider.create(credentials))
            .serviceConfiguration(s3Config)
            .build();
    }
}