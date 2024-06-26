package creative.design.carrotbow.profile.service;

import creative.design.carrotbow.profile.domain.Dog;
import creative.design.carrotbow.profile.domain.User;
import creative.design.carrotbow.error.NotFoundException;
import creative.design.carrotbow.profile.repository.DogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class DogService {

    private final DogRepository dogRepository;

    public Long register(User owner, Dog dog){
        dog.setOwner(owner);
        dogRepository.save(dog);
        return dog.getId();
    }

    public void delete(Long id){
        Dog dog = dogRepository.findById(id).orElseThrow(() -> new NotFoundException("can't find dog. id:" + id));
        dog.delete();
    }

    public Dog findWithUser(Long id){
        return dogRepository.findByIdWithUser(id).orElseThrow(()->new NotFoundException("can't find dog. id:"+id));
    }

    public List<Dog> findDogsByUserId(Long userId){
        return dogRepository.findListByUserId(userId);
    }

    public Dog find(Long id){
        return dogRepository.findById(id).orElseThrow(()->new NotFoundException("can't find dog. id:"+id));
    }
}
