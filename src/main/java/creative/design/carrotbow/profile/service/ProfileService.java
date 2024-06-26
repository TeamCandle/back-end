package creative.design.carrotbow.profile.service;

import creative.design.carrotbow.profile.domain.Dog;
import creative.design.carrotbow.profile.domain.User;
import creative.design.carrotbow.profile.domain.dto.*;
import creative.design.carrotbow.security.auth.AuthenticationUser;
import creative.design.carrotbow.external.s3.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j(topic = "ACCESS_LOG")
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProfileService {

    private final S3Service s3Service;
    private final UserService userService;
    private final DogService dogService;


    public UserProfileDto getUserProfile(Long id){

        User user = userService.find(id);
        List<Dog> dogs = dogService.findDogsByUserId(id);

        int age = LocalDate.now().getYear() - user.getBirthYear() + 1;

        ArrayList<ListDogDto> dogList = new ArrayList<>();

        for (Dog dog : dogs) {
            dogList.add(ListDogDto.builder()
                            .id(dog.getId())
                            .name(dog.getName())
                            .breed(dog.getBreed())
                            .gender(dog.getGender())
                            .image(s3Service.loadImage(dog.getImage()))
                    .build());
        }

        return UserProfileDto
                .builder()
                .id(user.getId())
                .name(user.getName())
                .gender(user.getGender())
                .age(age)
                .rating(user.getRating())
                .description(user.getDescription())
                .image(s3Service.loadImage(user.getImage()))
                .dogList(dogList)
                .build();
    }

    @Transactional
    public void changeUserDescription(AuthenticationUser authenticationUser, String description){
        User user = userService.find(authenticationUser.getId());
        user.changeDescription(description);

        log.info("설명 변경");
    }



    @Transactional
    public void changeUserImage(AuthenticationUser authenticationUser, MultipartFile image){
        User user = userService.find(authenticationUser.getId());
        s3Service.deleteImage(user.getImage());
        String objectKey = s3Service.saveUserImage(user.getUsername(), image);

        user.changeImage(objectKey);

        log.info("이미지 변경");
    }



    @Transactional
    public Long registerDogProfile(AuthenticationUser user, DogRegisterForm dogRegister){

        String objectKey = s3Service.saveDogImage(user.getUsername(), dogRegister.getName(), dogRegister.getImage());

        Dog dog = Dog.builder()
                .name(dogRegister.getName())
                .age(dogRegister.getAge())
                .gender(dogRegister.getGender())
                .neutered(dogRegister.isNeutered())
                .size(DogSize.valueOf(dogRegister.getSize()))
                .breed(dogRegister.getBreed())
                .description(dogRegister.getDescription())
                .image(objectKey)
                .build();

        Long dogId = dogService.register(new User(user.getId()), dog);

        log.info("애견 등록. 애견 Id={}", dogId);

        return dogId;
    }


    public DogProfileDto getDogProfile(Long id){
        Dog dog = dogService.find(id);

        return DogProfileDto.builder()
                .id(dog.getId())
                .owner(dog.getOwner().getId())
                .name(dog.getName())
                .age(dog.getAge())
                .gender(dog.getGender())
                .neutered(dog.isNeutered())
                .breed(dog.getBreed())
                .size(dog.getSize().getActualName())
                .description(dog.getDescription())
                .image(s3Service.loadImage(dog.getImage()))
                .build();
    }

    public List<ListDogDto> getDogProfileListByUserId(Long userId){
        List<Dog> dogList = dogService.findDogsByUserId(userId);

        return dogList.stream().map(dog -> ListDogDto.builder()
                .id(dog.getId())
                .name(dog.getName())
                .breed(dog.getBreed())
                .gender(dog.getGender())
                .image(s3Service.loadImage(dog.getImage()))
                .build()).collect(Collectors.toList());
    }

    @Transactional
    public void changeDogProfile(DogRegisterForm dogEdition){
        Dog dog = dogService.findWithUser(dogEdition.getId());
        s3Service.deleteImage(dog.getImage());
        String objectKey = s3Service.saveDogImage(dog.getOwner().getUsername(), dogEdition.getName(), dogEdition.getImage());
        dog.changeAttr(dogEdition, objectKey);

        log.info("애견 프로필 변경 dog Id={}", dog.getId());
    }

    @Transactional
    public void deleteDogProfile(Long id){
        dogService.delete(id);

        log.info("애견 프로필 삭제 dog Id={}", id);
    }
}
