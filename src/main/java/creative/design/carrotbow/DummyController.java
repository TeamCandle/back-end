package creative.design.carrotbow;

import creative.design.carrotbow.error.InvalidAccessException;
import creative.design.carrotbow.external.geo.GeoService;
import creative.design.carrotbow.matching.domain.Application;
import creative.design.carrotbow.matching.domain.MatchEntity;
import creative.design.carrotbow.matching.domain.Requirement;
import creative.design.carrotbow.matching.domain.dto.type.CareType;
import creative.design.carrotbow.matching.domain.dto.type.MatchEntityStatus;
import creative.design.carrotbow.matching.domain.dto.type.MatchStatus;
import creative.design.carrotbow.matching.domain.repository.ApplicationRepository;
import creative.design.carrotbow.matching.domain.repository.MatchRepository;
import creative.design.carrotbow.matching.domain.repository.RequirementRepository;
import creative.design.carrotbow.profile.domain.Dog;
import creative.design.carrotbow.profile.domain.User;
import creative.design.carrotbow.profile.repository.DogRepository;
import creative.design.carrotbow.profile.repository.UserRepository;
import creative.design.carrotbow.security.jwt.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
import java.util.Optional;

@Controller
@Transactional
@RequestMapping("/dummy")
@RequiredArgsConstructor
public class DummyController {

    private final UserRepository userRepository;
    private final DogRepository dogRepository;
    private final RequirementRepository requirementRepository;
    private final ApplicationRepository applicationRepository;
    private final MatchRepository matchRepository;

    private final GeoService geoService;

    private final JwtUtils jwtUtils;


    @RequestMapping("/user")
    @ResponseBody
    public String getUser(@RequestParam Long id){
        User user = userRepository.find(id).orElseThrow(()->new InvalidAccessException("invalid access"));

        return "Bearer " + jwtUtils.generateAccessToken(user.getUsername());
    }


    @RequestMapping("")
    @ResponseBody
    public void makeDummy(){

        for(int i=1; i<=100; i++){
            makeUser(i);
        }
        for(int i=1; i<=200; i++){
            makeDog(i);
            makeRequirement(i);
            makeApplication(i);
        }

        for(int i=1; i<=100; i++){
            makeMatches(i);
        }
    }


    public void makeUser(int num){
        userRepository.save(User.builder()
                        .name("user_"+num)
                        .username("user_num")
                        .password("secret"+num)
                        .email("user@"+num)
                        .gender(num%2==0?"male":"female")
                        .birthYear(1970+num%30)
                        .phNum("010-"+num)
                        .reviewCount(0)
                        .totalRating(0)
                        .role("ROLE_USER")
                .build());
    }

    public void makeDog(int num){
        Dog dog = Dog.builder()
                .name("dog_" + num)
                .age(num)
                .breed("type_" + num)
                .size(num % 3 + 1)
                .weight(num % 10)
                .gender(num % 2 == 0 ? "male" : "female")
                .neutered(num % 2 == 0)
                .description("dogDes_"+num)
                .build();
        dog.setOwner(new User(num%100+1L));
        dogRepository.save(dog);
    }

    public void makeRequirement(int num){
        LocalDateTime start = LocalDateTime.now().plusDays(num%20+5);

        requirementRepository.save(Requirement.builder()
                        .dog(new Dog(num+0L))
                        .user(new User(num%100+1L))
                        .careLocation(geoService.makeGeoData(new Point(128.3+(num%10)*0.01, 36.1+(num%10)*0.01)))
                        .startTime(start)
                        .endTime(start.plusDays(num%5))
                        .careType(num%2==0? CareType.WALKING:CareType.BOARDING)
                        .description("req_"+num)
                        .reward(num+1000)
                        .status(MatchStatus.NOT_MATCHED)
                .build());
    }

    public void makeApplication(int num){

        Application application = Application.builder()
                .user(new User(num % 100 + 1L))
                .createTime(LocalDateTime.now())
                .description("app_" + num)
                .status(MatchStatus.NOT_MATCHED)
                .build();

        application.apply(new Requirement(num+0L));

        applicationRepository.save(application);
    }

    public void makeMatches(int num){

        if(num%2==0) {
            matchRepository.save(MatchEntity.builder()
                            .application(new Application(num + 0L))
                            .requirement(new Requirement(num + 0L))
                            .createTime(LocalDateTime.now())
                            .status(MatchEntityStatus.NOT_COMPLETED)
                    .build());
        }
    }
}
