package com.sorta.service.dagger;

import com.sorta.service.exceptions.InternalServerException;
import com.sorta.service.utils.S3KeyGenerator;
import dagger.Module;
import dagger.Provides;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import javax.inject.Named;
import javax.inject.Singleton;

@Module
public class S3Module {
    
    @Provides
    @Singleton
    public S3Client provideS3Client() {
        return S3Client.builder()
                .region(Region.US_WEST_2) // Default region, can be overridden by AWS_REGION env var
                .credentialsProvider(DefaultCredentialsProvider.builder().build())
                .httpClient(UrlConnectionHttpClient.builder().build()) // Lightweight HTTP client for Lambda
                .build();
    }

    @Provides
    @Singleton
    public S3Presigner provideS3Presigner() {
        return S3Presigner.builder()
                .region(Region.US_WEST_2)
                .build();
    }

    @Provides
    @Singleton
    public S3KeyGenerator providesS3KeyGenerator() {
        return new S3KeyGenerator();
    }
    
    @Provides
    @Named("declutterImagesBucketName")
    public String provideDeclutterImagesBucketName() {
        final String imageBucketName = System.getenv("DECLUTTER_IMAGES_BUCKET_NAME");
        if (imageBucketName == null) {
            throw new InternalServerException("Session table name shouldn't be null");
        }
        return imageBucketName;
    }
}
