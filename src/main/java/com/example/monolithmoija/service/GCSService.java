package com.example.monolithmoija.service;


import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;
import com.google.common.primitives.Longs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gcp.storage.GoogleStorageResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class GCSService {
    @Value("${gcs.resource.profile.bucket}")
    private String profileBucketname;
    @Value("${gcs.resource.profile.cdn.keyfile}")
    private String profileKey;
    @Value("${gcs.resource.image.cdn.keyfile}")
    private String imageKey;

    @Value("${gcs.resource.recruitimage.bucket}")
    private String imageBucketname;

    @Value("classpath:/pictures/no_profile.png")
    private Resource defaultFile;

    @Value("${my.domain.name}")
    private String domain;

    @Value("${spring.cloud.gcp.storage.project-id}")
    private String projectId;
    @Value("${spring.cloud.gcp.storage.credentials.location}")
    private String serviceKey;

    private Storage storage;

    private GCSService() throws IOException {
        StorageOptions storageOptions = StorageOptions.newBuilder()
                .setProjectId("506812411622")
                .setCredentials(GoogleCredentials.fromStream(
                        getClass().getClassLoader().getResourceAsStream("./key/shaped-utility-412306-781de969d21c.json"))).build();
        this.storage = storageOptions.getService();;
    }

    public String writeProfile(String fileName, MultipartFile image) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        return generateV4PutObjectSignedUrl(profileBucketname,fileName,image,"new-key",profileKey);
    }
    public String writeRecruitImage(String fileName, MultipartFile image) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        return generateV4PutObjectSignedUrl(imageBucketname,fileName,image,"imnew-key",imageKey);
    }


    //업로드할 때 gs://로 시작하는 이미지 주소가 아닌 http로 누구나 접근할 수 있는 이미지 주소를 뽑아오기!
    public String generateV4PutObjectSignedUrl(
            String bucketName, String filename, MultipartFile image, String keyName, String keyFile) throws StorageException, IOException, NoSuchAlgorithmException, InvalidKeyException {
        filename = bucketName.split("-moija")[0]+"/"+filename;
        //String uuid = UUID.randomUUID().toString(); // Google Cloud Storage에 저장될 파일 이름
        String ext = image.getContentType(); // 파일의 형식 ex) JPG
        BlobInfo blobInfo = storage.create(
                BlobInfo.newBuilder(BlobId.of(bucketName, filename))
                        .setContentType(ext).build(),image.getInputStream());

        // Generate Signed URL
//        Map<String, String> extensionHeaders = new HashMap<>();
//        extensionHeaders.put("Content-Type", "application/octet-stream");
//
//        URL url =
//                storage.signUrl(
//                        blobInfo,
//                        15,
//                        TimeUnit.MINUTES,
//                        Storage.SignUrlOption.httpMethod(HttpMethod.PUT),
//                        Storage.SignUrlOption.withExtHeaders(extensionHeaders),
//                        Storage.SignUrlOption.withV4Signature());
        storage.create(blobInfo,image.getBytes());

        String url = signUrl("http://"+"resource."+domain+"/"+filename, getClass().getClassLoader().getResourceAsStream(keyFile).readAllBytes() , keyName,new Date(Long.MAX_VALUE ));
        return url.toString();
    }
    public static String signUrl(String url,
                                 byte[] key,
                                 String keyName,
                                 Date expirationTime)
            throws InvalidKeyException, NoSuchAlgorithmException {

        final long unixTime = expirationTime.getTime() / 1000;

        String urlToSign = url
                + (url.contains("?") ? "&" : "?")
                + "Expires=" + unixTime
                + "&KeyName=" + keyName;

        String encoded = getSignature(key, urlToSign);
        return urlToSign + "&Signature=" + encoded;
    }

    public static String getSignature(byte[] privateKey, String input)
            throws InvalidKeyException, NoSuchAlgorithmException {

        privateKey = Base64.getDecoder().decode(privateKey);
        final String algorithm = "HmacSHA1";
        final int offset = 0;
        Key key = new SecretKeySpec(privateKey, offset, privateKey.length, algorithm);
        Mac mac = Mac.getInstance(algorithm);
        mac.init(key);
        return  Base64.getUrlEncoder().encodeToString(mac.doFinal(input.getBytes()));
    }
}
