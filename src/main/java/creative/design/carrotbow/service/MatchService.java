package creative.design.carrotbow.service;

import creative.design.carrotbow.domain.*;
import creative.design.carrotbow.dto.*;
import creative.design.carrotbow.error.NotFoundException;
import creative.design.carrotbow.error.WrongApplicationException;
import creative.design.carrotbow.repository.ApplicationRepository;
import creative.design.carrotbow.repository.MatchRepository;
import creative.design.carrotbow.repository.RequirementRepository;
import creative.design.carrotbow.security.auth.AuthenticationUser;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MatchService {

    private final RequirementRepository requirementRepository;
    private final ApplicationRepository applicationRepository;
    private final MatchRepository matchRepository;

    private final UserService userService;
    private final DogService dogService;
    private final S3Service s3Service;

    private final GeometryFactory geometryFactory;



    private Point makeGeoData(org.springframework.data.geo.Point carePoint){
        final Coordinate coordinate = new Coordinate(carePoint.getX(), carePoint.getY());
        Point point = geometryFactory.createPoint(coordinate);
        point.setSRID(4326); // SRID 설정

        return point;
    }

    public org.springframework.data.geo.Point makePoint(Point point){
        return new org.springframework.data.geo.Point(point.getX(), point.getY());
    }


    //Requirement
    @Transactional
    public Long registerRequirement(RequireRegisterForm requireRegisterForm){
        Dog dog = dogService.find(requireRegisterForm.getDogId());

        System.out.println("test");

        return requirementRepository.save(
                Requirement.builder()
                .user(dog.getOwner())
                .dog(dog)
                .careType(CareType.valueOf(requireRegisterForm.getCareType()))
                .careLocation(makeGeoData(requireRegisterForm.getCareLocation()))
                .careTime(requireRegisterForm.getCareTime())
                .description(requireRegisterForm.getDescription())
                .status(RequirementStatus.RECRUITING)
                        .createTime(LocalDateTime.now())
                        .build());
    }

    public List<ListMatchDto> getRequirementsByUser(AuthenticationUser authenticationUser){
        List<Requirement> requirementList = requirementRepository.findListByUsername(authenticationUser.getUsername());

        List<ListMatchDto> requirements = new ArrayList<>();

        for (Requirement requirement : requirementList) {
            requirements.add(
                    ListMatchDto.builder()
                            .id(requirement.getId())
                            .image(s3Service.loadImage(requirement.getDog().getImage()))
                            .breed(requirement.getDog().getBreed())
                            .careType(requirement.getCareType())
                            .status(requirement.getStatus().toString())
                            .build());
        }

        return requirements;
    }

    public List<ListMatchDto> getRequirementsByLocation(RequirementCondForm condForm){
        List<Requirement> requirementList = requirementRepository.findListByLocation(condForm);

        List<ListMatchDto> requirements = new ArrayList<>();

        for (Requirement requirement : requirementList) {
            requirements.add(
                    ListMatchDto.builder()
                            .id(requirement.getId())
                            .image(s3Service.loadImage(requirement.getDog().getImage()))
                            .breed(requirement.getDog().getBreed())
                            .careType(requirement.getCareType())
                            .status(requirement.getStatus().toString())
                            .build());
        }

        return requirements;
    }

    public HashMap<String, Object> getRequirement(Long id){
        Requirement requirement = requirementRepository.findWithApplicationsById(id).orElseThrow(() -> new NotFoundException("can't find requirement. id:" + id));


        MatchDto details = MatchDto.builder()
                .id(requirement.getId())
                .userName(requirement.getUser().getUsername())
                .dogId(requirement.getDog().getId())
                .dogImage(s3Service.loadImage(requirement.getDog().getImage()))
                .careType(requirement.getCareType())
                .careTime(requirement.getCareTime())
                .careLocation(makePoint(requirement.getCareLocation()))
                .description(requirement.getDescription())
                .status(requirement.getStatus().toString())
                .build();

        HashMap<String, Object> requirementDetail = new HashMap<>();
        requirementDetail.put("details", details);


        if(requirement.getStatus()==RequirementStatus.RECRUITING) {
            List<ListAppDto> applications = new ArrayList<>();

            for (Application application : requirement.getApplications()) {
                if (application.getStatus() == ApplicationStatus.WAITING) {
                    applications.add(ListAppDto.builder()
                            .id(application.getId())
                            .userId(application.getUser().getId())
                            .rate("not implemented")
                            .name(application.getUser().getName())
                            .gender(application.getUser().getGender())
                            .image(s3Service.loadImage(application.getUser().getImage()))
                            .build());
                }
            }

            requirementDetail.put("applications", applications);
        }

        return requirementDetail;
    }

    @Transactional
    public void cancelRequirement(Long id){
        Requirement requirement = requirementRepository.findWithApplicationsById(id).orElseThrow(() -> new NotFoundException("can't find requirement. id:" + id));

        if(requirement.getStatus() != RequirementStatus.RECRUITING){
            throw new WrongApplicationException("this requirement is expired. id:" + id);
        }

        for (Application application : requirement.getApplications()) {
            if(application.getStatus()==ApplicationStatus.WAITING){
                application.changeStatus(ApplicationStatus.REJECTED);
            }
        }

        requirement.changeStatus(RequirementStatus.CANCELLED);

    }




    //Application

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
                            .status(application.getStatus().toString())
                            .build());
        }

        return applications;
    }


    public MatchDto getApplication(Long id){
        Application application = applicationRepository.findWithRequirementById(id).orElseThrow(() -> new NotFoundException("can't find application. id:" + id));

        return MatchDto.builder()
                .id(application.getId())
                .userName(application.getRequirement().getUser().getUsername())
                .dogId(application.getRequirement().getDog().getId())
                .dogImage(s3Service.loadImage(application.getRequirement().getDog().getImage()))
                .careType(application.getRequirement().getCareType())
                .careTime(application.getRequirement().getCareTime())
                .careLocation(makePoint(application.getRequirement().getCareLocation()))
                .description(application.getRequirement().getDescription())
                .status(application.getStatus().toString())
                .build();
    }


    @Transactional
    public Long apply(Long requirementId, String username){

        Requirement requirement = requirementRepository.findWithApplicationsById(requirementId).orElseThrow(() -> new NotFoundException("can't find requirement. id:" + requirementId));

        if(requirement.getStatus()!=RequirementStatus.RECRUITING){
            throw new WrongApplicationException("this requirement is expired. id:" + requirementId);
        }

        for(Application application: requirement.getApplications()){
            if(application.getUser().getUsername().equals(username)){
                throw new WrongApplicationException("this user already applied. username:" + username);
            }
        }

        Application application = Application.builder()
                .user(userService.findRead(username))
                .status(ApplicationStatus.WAITING)
                .createTime(LocalDateTime.now())
                .build();

        application.apply(requirement);

        return applicationRepository.save(application);
    }



    @Transactional
    public void cancelApplication(Long id){
        Application application = applicationRepository.findById(id).orElseThrow(() -> new NotFoundException("can't find application. id:" + id));

        if(application.getStatus()!=ApplicationStatus.WAITING){
            throw new WrongApplicationException("this application is expired. id:" + id);
        }

        application.changeStatus(ApplicationStatus.CANCELLED);
    }

    @Transactional
    public void rejectApplication(Long id){
        Application application = applicationRepository.findById(id).orElseThrow(() -> new NotFoundException("can't find application. id:" + id));

        if(application.getStatus()!=ApplicationStatus.WAITING){
            throw new WrongApplicationException("this application is expired. id:" + id);
        }

        application.changeStatus(ApplicationStatus.REJECTED);
    }





    //Match

    public List<ListMatchDto> getMatchs(AuthenticationUser authenticationUser){
        List<MatchEntity> matchList = matchRepository.findListWithRequirementByUsername(authenticationUser.getUsername());

        List<ListMatchDto> matchs = new ArrayList<>();

        for (MatchEntity match : matchList) {
            matchs.add(
                    ListMatchDto.builder()
                            .id(match.getId())
                            .image(s3Service.loadImage(match.getRequirement().getDog().getImage()))
                            .breed(match.getRequirement().getDog().getBreed())
                            .careType(match.getRequirement().getCareType())
                            .status(match.getStatus().toString())
                            .build());
        }

        return matchs;
    }


    public MatchDto getMatch(Long id, AuthenticationUser authenticationUser){
        MatchEntity match = matchRepository.findWithRequirementById(id).orElseThrow(()->new NotFoundException("can't find match. id:" + id));

        String requestPerson = authenticationUser.getUsername();
        String requirePerson=match.getRequirement().getUser().getUsername();

        return MatchDto.builder()
                .id(match.getId())
                .userName(requestPerson.equals(requirePerson)?requirePerson:match.getApplication().getUser().getName())
                .dogId(match.getRequirement().getDog().getId())
                .dogImage(s3Service.loadImage(match.getRequirement().getDog().getImage()))
                .careType(match.getRequirement().getCareType())
                .careTime(match.getRequirement().getCareTime())
                .careLocation(makePoint(match.getRequirement().getCareLocation()))
                .description(match.getRequirement().getDescription())
                .status(match.getStatus().toString())
                .build();
    }


    @Transactional
    public Long makeMatch(Long requirementId, Long applicationId){
        Requirement requirement = requirementRepository.findWithApplicationsById(requirementId).orElseThrow(() -> new NotFoundException("can't find requirement. id:" + requirementId));
        Application matchedApplication = applicationRepository.findById(applicationId).orElseThrow(() -> new NotFoundException("can't find application. id:" + applicationId));

        if(requirement.getStatus()!=RequirementStatus.RECRUITING){
            throw new WrongApplicationException("this requirement is expired. id:" + requirementId);
        }
        if(matchedApplication.getStatus()!=ApplicationStatus.WAITING){
            throw new WrongApplicationException("this application is expired. id:" + applicationId);
        }

        requirement.changeStatus(RequirementStatus.MATCHED);
        matchedApplication.changeStatus(ApplicationStatus.MATCHED);


        for(Application application : requirement.getApplications()){
            if(application.getStatus()==ApplicationStatus.WAITING){
                application.changeStatus(ApplicationStatus.REJECTED);
            }
        }

        return matchRepository.save(MatchEntity.builder()
                .requirement(requirement)
                .application(matchedApplication)
                .status(MatchStatus.IN_PROGRESS)
                .createTime(LocalDateTime.now())
                .build());
    }

}
