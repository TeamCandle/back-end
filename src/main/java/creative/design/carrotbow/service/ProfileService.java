package creative.design.carrotbow.service;

import creative.design.carrotbow.domain.Dog;
import creative.design.carrotbow.domain.User;
import creative.design.carrotbow.dto.DogProfileDto;
import creative.design.carrotbow.dto.requestForm.DogRegisterForm;
import creative.design.carrotbow.dto.ListDogDto;
import creative.design.carrotbow.dto.UserProfileDto;
import creative.design.carrotbow.security.auth.AuthenticationUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProfileService {

    private final S3Service s3Service;
    private final UserService userService;
    private final DogService dogService;


    public UserProfileDto getUserProfile(Long id){

        User user = userService.findWithDogs(id);

        int age = LocalDate.now().getYear() - user.getBirthYear() + 1;
        List<Dog> dogs = user.getDogs();

        ArrayList<ListDogDto> dogList = new ArrayList<>();

        for (Dog dog : dogs) {
            dogList.add(ListDogDto.builder()
                            .id(dog.getId())
                            .name(dog.getName())
                            .gender(dog.getGender())
                            .image(s3Service.loadImage(dog.getImage()))
                    .build());
        }

        return UserProfileDto
                .builder()
                .name(user.getName())
                .gender(user.getGender())
                .age(age)
                .description(user.getDescription())
                .image(s3Service.loadImage(user.getImage()))
                .dogList(dogList)
                .build();
    }

    @Transactional
    public void changeUserDescription(AuthenticationUser authenticationUser, String description){
        User user = userService.find(authenticationUser.getId());
        user.changeDescription(description);
    }



    @Transactional
    public void changeUserImage(AuthenticationUser authenticationUser, MultipartFile image){
        User user = userService.find(authenticationUser.getId());
        s3Service.deleteImage(user.getImage());
        String objectKey = s3Service.saveUserImage(user.getUsername(), image);

        user.changeImage(objectKey);
    }



    @Transactional
    public Long registerDogProfile(AuthenticationUser user, DogRegisterForm dogRegister){

        String objectKey = s3Service.saveDogImage(user.getUsername(), dogRegister.getName(), dogRegister.getImage());

        Dog dog = Dog.builder()
                .name(dogRegister.getName())
                .age(dogRegister.getAge())
                .gender(dogRegister.getGender())
                .neutered(dogRegister.isNeutered())
                .size(dogRegister.getSize())
                .weight(dogRegister.getWeight())
                .breed(dogRegister.getBreed())
                .description(dogRegister.getDescription())
                .image(objectKey)
                .build();

        return dogService.register(new User(user.getId()), dog);
    }



    public DogProfileDto getDogProfile(Long id){
        Dog dog = dogService.find(id);

        return DogProfileDto.builder()
                .owner(dog.getOwner().getId())
                .name(dog.getName())
                .age(dog.getAge())
                .gender(dog.getGender())
                .neutered(dog.isNeutered())
                .breed(dog.getBreed())
                .size(dog.getSize())
                .weight(dog.getWeight())
                .description(dog.getDescription())
                .image(s3Service.loadImage(dog.getImage()))
                .build();
    }

    public List<ListDogDto> getDogProfileListByUserId(Long userId){
        List<Dog> dogList = dogService.findListByUserId(userId);

        return dogList.stream().map(dog -> ListDogDto.builder()
                .id(dog.getId())
                .name(dog.getName())
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
    }

    @Transactional
    public void deleteDogProfile(Long id){
        Dog dog = dogService.find(id);
        s3Service.deleteImage(dog.getImage());
        dogService.delete(id);
    }
}
