package creative.design.carrotbow.service;

import creative.design.carrotbow.domain.*;
import creative.design.carrotbow.dto.*;
import creative.design.carrotbow.dto.MatchDto;
import creative.design.carrotbow.dto.requestForm.RequireRegisterForm;
import creative.design.carrotbow.dto.requestForm.RequirementCondForm;
import creative.design.carrotbow.error.InvalidAccessException;
import creative.design.carrotbow.error.NotFoundException;
import creative.design.carrotbow.error.WrongApplicationException;
import creative.design.carrotbow.repository.RequirementRepository;
import creative.design.carrotbow.security.auth.AuthenticationUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequirementService
{

    private final RequirementRepository requirementRepository;
    private final DogService dogService;
    private final S3Service s3Service;
    private final GeoService geoService;

    @Transactional
    public Long registerRequirement(RequireRegisterForm requireRegisterForm){
        Dog dog = dogService.find(requireRegisterForm.getDogId());

        return requirementRepository.save(
                Requirement.builder()
                        .user(dog.getOwner())
                        .dog(dog)
                        .careType(CareType.valueOf(requireRegisterForm.getCareType()))
                        .careLocation(geoService.makeGeoData(requireRegisterForm.getCareLocation()))
                        .startTime(requireRegisterForm.getStartTime())
                        .endTime(requireRegisterForm.getEndTime())
                        .description(requireRegisterForm.getDescription())
                        .status(MatchStatus.NOT_MATCHED)       //NOT MATCHED
                        .createTime(LocalDateTime.now())
                        .reward(requireRegisterForm.getReward())
                        .build());
    }

    public List<ListMatchDto> getRequirementsByUser(int offset, AuthenticationUser authenticationUser){
        List<Requirement> requirementList = requirementRepository.findListByUserId(authenticationUser.getId(), offset);

        List<ListMatchDto> requirements = new ArrayList<>();


        for (Requirement requirement : requirementList) {
            requirements.add(
                    ListMatchDto.builder()
                            .id(requirement.getId())
                            .image(s3Service.loadImage(requirement.getDog().getImage()))
                            .breed(requirement.getDog().getBreed())
                            .careType(requirement.getCareType())
                            .status(requirement.getActualStatus())     //status 생각 NOT MATCHED & current time -> recruiting
                            .build());
        }

        return requirements;
    }

    public List<ListMatchDto> getRequirementsByLocation(RequirementCondForm condForm, int offset){
        List<Requirement> requirementList = requirementRepository.findListByLocation(condForm, offset);

        List<ListMatchDto> requirements = new ArrayList<>();

        for (Requirement requirement : requirementList) {
            requirements.add(
                    ListMatchDto.builder()
                            .id(requirement.getId())
                            .image(s3Service.loadImage(requirement.getDog().getImage()))
                            .breed(requirement.getDog().getBreed())
                            .careType(requirement.getCareType())
                            .status(requirement.getActualStatus())     //status 생각
                            .build());
        }

        return requirements;
    }

    public HashMap<String, Object> getRequirementWithApplications(Long id, AuthenticationUser user){
        Requirement requirement = requirementRepository.findWithApplicationsById(id).orElseThrow(() -> new NotFoundException("can't find requirement. id:" + id));

        if(!user.getId().equals(requirement.getUser().getId())){
            throw new InvalidAccessException("this access is not authorized");
        }

        MatchDto details = MatchDto.builder()
                .id(requirement.getId())
                .userId(requirement.getUser().getId())
                .dogId(requirement.getDog().getId())
                .dogImage(s3Service.loadImage(requirement.getDog().getImage()))
                .careType(requirement.getCareType())
                .startTime(requirement.getStartTime())
                .endTime(requirement.getEndTime())
                .careLocation(geoService.makePoint(requirement.getCareLocation()))
                .description(requirement.getDescription())
                .reward(requirement.getReward())
                .status(requirement.getActualStatus())     //status 생각
                .build();

        HashMap<String, Object> requirementDetail = new HashMap<>();
        requirementDetail.put("details", details);


        if(requirement.getActualStatus().equals(Requirement.RECRUITING)) { //NOT MATCHED & <current time  -> status = recruiting
            List<ListAppDto> applications = new ArrayList<>();

            for (Application application : requirement.getApplications()) {
                if (application.getStatus()==MatchStatus.NOT_MATCHED) { //NOT MATCHED
                    applications.add(ListAppDto.builder()
                            .id(application.getId())
                            .username(application.getUser().getUsername())
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

    public MatchDto getRequirement(Long id){
        Requirement requirement = requirementRepository.findById(id).orElseThrow(() -> new NotFoundException("can't find requirement. id:" + id));

        return MatchDto.builder()
                .id(requirement.getId())
                .userId(requirement.getUser().getId())
                .dogId(requirement.getDog().getId())
                .dogImage(s3Service.loadImage(requirement.getDog().getImage()))
                .careType(requirement.getCareType())
                .startTime(requirement.getStartTime())
                .endTime(requirement.getEndTime())
                .careLocation(geoService.makePoint(requirement.getCareLocation()))
                .description(requirement.getDescription())
                .reward(requirement.getReward())
                .status(requirement.getActualStatus()) //recruiting 생각
                .build();
    }


    @Transactional
    public void cancelRequirement(Long id, AuthenticationUser user){
        Requirement requirement = requirementRepository.findById(id).orElseThrow(() -> new NotFoundException("can't find requirement. id:" + id));


        if(!user.getId().equals(requirement.getUser().getId())){
            throw new InvalidAccessException("this access is not authorized");
        }

        if(!requirement.getActualStatus().equals(Requirement.RECRUITING)){        //not matched & current time
            throw new WrongApplicationException("this requirement is expired. id:" + id);
        }


        requirement.changeStatus(MatchStatus.CANCELLED);

    }



}
