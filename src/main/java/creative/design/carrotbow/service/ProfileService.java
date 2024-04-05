package creative.design.carrotbow.service;

import com.amazonaws.AmazonClientException;
import creative.design.carrotbow.domain.Dog;
import creative.design.carrotbow.domain.User;
import creative.design.carrotbow.dto.DogProfileDto;
import creative.design.carrotbow.dto.DogRequestForm;
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

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProfileService {

    private final S3Service s3Service;
    private final UserService userService;
    private final DogService dogService;


    public UserProfileDto getUserProfile(AuthenticationUser authenticationUser){

        User user = userService.findReadWithDogs(authenticationUser.getUsername());

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
    public void registerDogProfile(AuthenticationUser authenticationUser, DogRequestForm dogRegister){

        User owner = userService.findRead(authenticationUser.getUsername());

        String objectKey = s3Service.saveDogImage(owner.getUsername(), dogRegister.getName(), dogRegister.getImage());

        System.out.println(dogRegister.getImage()==null?"true":"false");
        System.out.println(dogRegister.getImage());
        System.out.println(objectKey);

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

        dogService.register(owner, dog);
    }



    public DogProfileDto getDogProfile(Long id){
        Dog dog = dogService.findWithUser(id);

        return DogProfileDto.builder()
                .owner(dog.getOwner().getName())
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

    @Transactional
    public void changeDogProfile(DogRequestForm dogEdition){
        Dog dog = dogService.find(dogEdition.getId());
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
