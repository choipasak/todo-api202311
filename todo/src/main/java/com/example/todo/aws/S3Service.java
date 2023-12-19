package com.example.todo.aws;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import javax.annotation.PostConstruct;

@Service
@Slf4j
public class S3Service {

    // S3 버킷을 제어하는 객체
    private S3Client s3;

    @Value("${aws.credentials.accessKey}")
    private String accessKey;

    @Value("${aws.credentials.secretKey}")
    private String secretKey;

    @Value("${aws.region}")
    private String region;

    @Value("${aws.bucketName}")
    private String bucketName;
    
    // s3에 연결해서 인증을 처리하는 로직
    // S3Service가 생성될 때 1번만 실행 되게 한다는 아노테이션
        @PostConstruct
    private void initializeAmazon(){

        // 액세스 키와 시크릿 키를 이용해서 계정 인증 받기
        // static 메서드임
        // s3버킷에 접근할 수 있게 IAM계정으로 접근하는 것임 -> 그것을 인증
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);

        this.s3 = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();

    }

    /**
     * 버킷에 파일을 업로드하고, 업로드한 버킷의 url정보를 리턴하는 메서드
     * @param uploadfile - 업로들 할 파일의 실제 raw 데이터
     * @param fileName - 업로드 할 파일명 (UUID)
     * @return - 버킷에 업로드 된 버킷 경로(url)
     */
    public String uploadToS3Bucket(byte[] uploadfile, String fileName){

        // 업로드 할 파일을 S3 객체로 생성
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName) // 버킷의 이름
                .key(fileName) // 업로드 한 파일명
                .build();

        // 위에서 생성한 오브젝트를 버킷에 업로드하자!
        // 1번 매개 값: 위에서 생성한 오브젝트
        // 2번 매개 값: 업로드 하고자 하는 파일 (바이트 배열)
        s3.putObject(request, RequestBody.fromBytes(uploadfile));

        // DB에도 저장하자!
        // 업로드 된 파일의 url을 반환
        return s3.utilities()
                .getUrl(b -> b.bucket(bucketName).key(fileName))
                .toString();

    }

}
