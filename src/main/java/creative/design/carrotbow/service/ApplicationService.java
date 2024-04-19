package creative.design.carrotbow.service;

import creative.design.carrotbow.domain.Application;
import creative.design.carrotbow.domain.MatchStatus;
import creative.design.carrotbow.domain.Requirement;
import creative.design.carrotbow.dto.ListMatchDto;
import creative.design.carrotbow.dto.MatchDto;
import creative.design.carrotbow.error.InvalidAccessException;
import creative.design.carrotbow.error.NotFoundException;
import creative.design.carrotbow.error.WrongApplicationException;
import creative.design.carrotbow.repository.ApplicationRepository;
import creative.design.carrotbow.repository.RequirementRepository;
import creative.design.carrotbow.security.auth.AuthenticationUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final RequirementRepository requirementRepository;


    private final S3Service s3Service;
    private final GeoService geoService;
    private final UserService userService;



    public List<ListMatchDto> getApplications(AuthenticationUser authenticationUser){
        List<Application> applicationList = applicationRepository.findListWithRequirementByUsername(authenticationUser.getUsername());

        List<ListMatchDto> applications = new ArrayList<>();

        for (Application application : applicationList) {
            applications.add(
                    ListMatchDto.builder()
                            .id(application.getId())
                            .image(s3Service.loadImage(application.getRequirement().getDog().getImage()))
                            .breed(application.getRequirement().getDog().getBreed())
                            .careType(application.getRequirement().getCareType())
                            .status(application.getActualStatus()) //status 생각 not matched & requirement current time -> waiting
                            .build());
        }

        return applications;
    }


    public MatchDto getApplication(Long id, String username){
        Application application = applicationRepository.findWithRequirementById(id).orElseThrow(() -> new NotFoundException("can't find application. id:" + id));


        if(!username.equals(application.getUser().getUsername())){
            throw new InvalidAccessException("this access is not authorized");
        }

        return MatchDto.builder()
                .id(application.getId())
                .userName(application.getRequirement().getUser().getUsername())
                .dogId(application.getRequirement().getDog().getId())
                .dogImage(s3Service.loadImage(application.getRequirement().getDog().getImage()))
                .careType(application.getRequirement().getCareType())
                .startTime(application.getRequirement().getStartTime())
                .endTime(application.getRequirement().getEndTime())
                .careLocation(geoService.makePoint(application.getRequirement().getCareLocation()))
                .description(application.getRequirement().getDescription())
                .status(application.getActualStatus())     //status 생각 not matched & requirement current time -> waiting
                .build();
    }


    @Transactional
    public Long apply(Long requirementId, String username){

        Requirement requirement = requirementRepository.findWithApplicationsById(requirementId).orElseThrow(() -> new NotFoundException("can't find requirement. id:" + requirementId));

        if(!requirement.getActualStatus().equals(Requirement.RECRUITING)){  //NOT MATCHED and <currentTime
            throw new WrongApplicationException("this requirement is expired. id:" + requirementId);
        }

        for(Application application: requirement.getApplications()){
            if(application.getUser().getUsername().equals(username)){
                throw new WrongApplicationException("this user already applied. username:" + username);
            }
        }

        Application application = Application.builder()
                .user(userService.findRead(username))
                .status(MatchStatus.NOT_MATCHED)  //NOT MATCHED
                .createTime(LocalDateTime.now())
                .build();

        application.apply(requirement);

        return applicationRepository.save(application);
    }



    @Transactional
    public void cancelApplication(Long id, String username){
        Application application = applicationRepository.findWithRequirementById(id).orElseThrow(() -> new NotFoundException("can't find application. id:" + id));

        if(!username.equals(application.getUser().getUsername())){
            throw new InvalidAccessException("this access is not authorized");
        }

        if(!application.getActualStatus().equals(Application.WAITING)){
            throw new WrongApplicationException("this application is expired. id:" + id);
        }

        application.changeStatus(MatchStatus.CANCELLED);
    }


}