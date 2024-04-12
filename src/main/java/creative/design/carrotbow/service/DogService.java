package creative.design.carrotbow.service;

import creative.design.carrotbow.domain.Dog;
import creative.design.carrotbow.domain.User;
import creative.design.carrotbow.error.NotFoundException;
import creative.design.carrotbow.repository.DogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        dogRepository.deleteById(id);
    }

    public Dog findWithUser(Long id){
        return dogRepository.findByIdWithUser(id).orElseThrow(()->new NotFoundException("can't find dog. id:"+id));
    }

    public Dog find(Long id){
        return dogRepository.findById(id).orElseThrow(()->new NotFoundException("can't find dog. id:"+id));
    }
}
