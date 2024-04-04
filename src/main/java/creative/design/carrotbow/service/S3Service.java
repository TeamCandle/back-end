package creative.design.carrotbow.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3Client s3Client;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;


    public String saveUserImage(String username, MultipartFile image){
        try {

            if(image==null){
                return null;
            }

            String objectKey = "users/" + username + "/profile." + getExtension(image.getOriginalFilename());

            ObjectMetadata metadata= new ObjectMetadata();
            metadata.setContentType(image.getContentType());
            metadata.setContentLength(image.getSize());

            metadata.addUserMetadata("username", username);
            metadata.addUserMetadata("uploadDate", new Date().toString());

            s3Client.putObject(bucket,objectKey,image.getInputStream(),metadata);
            return objectKey;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String saveDogImage(String username, String dogName, MultipartFile image){
        try {

            if(image==null){
                return null;
            }

            String objectKey = "users/" + username + "/dogs/"+ dogName + "/profile." + getExtension(image.getOriginalFilename());

            ObjectMetadata metadata= new ObjectMetadata();
            metadata.setContentType(image.getContentType());
            metadata.setContentLength(image.getSize());

            metadata.addUserMetadata("username", username);
            metadata.addUserMetadata("uploadDate", new Date().toString());

            s3Client.putObject(bucket,objectKey,image.getInputStream(),metadata);
            return objectKey;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public byte[] loadImage(String objectKey){

        if(objectKey==null){
            return null;
        }

        GetObjectRequest getObjectRequest = new GetObjectRequest(bucket, objectKey);

        byte[] imageBytes;

        S3Object ImageObject = s3Client.getObject(getObjectRequest);
        try {
            imageBytes = IOUtils.toByteArray(ImageObject.getObjectContent());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return imageBytes;
    }

    public void deleteImage(String objectKey){

        if(objectKey==null){
            return;
        }

        DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucket, objectKey);
        s3Client.deleteObject(deleteObjectRequest);
    }

    private String getExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex == -1) {
            return "";
        }
        return fileName.substring(lastDotIndex + 1);
    }

}
