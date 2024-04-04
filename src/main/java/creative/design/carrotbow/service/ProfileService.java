package creative.design.carrotbow.service;

import creative.design.carrotbow.domain.User;
import creative.design.carrotbow.dto.UserProfileDto;
import creative.design.carrotbow.repository.UserRepository;
import creative.design.carrotbow.security.auth.AuthenticationUser;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProfileService {

    private final S3Service s3Service;
    private final UserService userService;


    public UserProfileDto getUserProfile(AuthenticationUser authenticationUser){

        User user = userService.findReadUser(authenticationUser.getUsername());

        byte[] image = s3Service.loadImage(user.getImage());

        int age = LocalDate.now().getYear() - user.getBirthYear() + 1;

        return UserProfileDto
                .builder()
                .name(user.getName())
                .gender(user.getGender())
                .age(age)
                .description(user.getDescription())
                .image(image)
                .build();
    }

    @Transactional
    public void changeUserDescription(AuthenticationUser authenticationUser, String description){
        User user = userService.findUser(authenticationUser.getId());
        user.changeDescription(description);
    }



    @Transactional
    public void changeUserImage(AuthenticationUser authenticationUser, MultipartFile image){
        User user  = userService.findUser(authenticationUser.getId());
        s3Service.deleteImage(user.getImage());
        String objectKey = s3Service.saveUserImage(user.getUsername(), image);

        user.changeImage(objectKey);
    }


}
