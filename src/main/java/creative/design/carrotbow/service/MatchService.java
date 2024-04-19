package creative.design.carrotbow.service;

import creative.design.carrotbow.domain.*;
import creative.design.carrotbow.dto.*;
import creative.design.carrotbow.error.InvalidAccessException;
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

    private final GeoService geoService;
    private final S3Service s3Service;



    public List<ListMatchDto> getMatches(AuthenticationUser authenticationUser){
        List<MatchEntity> matchList = matchRepository.findListWithRequirementByUsername(authenticationUser.getUsername());

        List<ListMatchDto> matches = new ArrayList<>();

        for (MatchEntity match : matchList) {
            matches.add(
                    ListMatchDto.builder()
                            .id(match.getId())
                            .image(s3Service.loadImage(match.getRequirement().getDog().getImage()))
                            .breed(match.getRequirement().getDog().getBreed())
                            .careType(match.getRequirement().getCareType())
                            .status(match.getStatus().toString())
                            .build());
        }

        return matches;
    }


    public MatchDto getMatch(Long id, AuthenticationUser authenticationUser){
        MatchEntity match = matchRepository.findWithRequirementById(id).orElseThrow(()->new NotFoundException("can't find match. id:" + id));

        String requestPerson = authenticationUser.getUsername();
        String requirePerson=match.getRequirement().getUser().getUsername();

        if(!requestPerson.equals(match.getRequirement().getUser().getName())&&!requestPerson.equals(match.getApplication().getUser().getName())){
            throw new InvalidAccessException("this access is not authorized");
        }


        return MatchDto.builder()
                .id(match.getId())
                .userName(requestPerson.equals(requirePerson)?requirePerson:match.getApplication().getUser().getName())
                .dogId(match.getRequirement().getDog().getId())
                .dogImage(s3Service.loadImage(match.getRequirement().getDog().getImage()))
                .careType(match.getRequirement().getCareType())
                .startTime(match.getRequirement().getStartTime())
                .endTime(match.getRequirement().getEndTime())
                .careLocation(geoService.makePoint(match.getRequirement().getCareLocation()))
                .description(match.getRequirement().getDescription())
                .status(match.getStatus().toString())
                .build();
    }


    @Transactional
    public Long makeMatch(Long requirementId, Long applicationId, String username){
        Requirement requirement = requirementRepository.findWithApplicationsById(requirementId).orElseThrow(() -> new NotFoundException("can't find requirement. id:" + requirementId));
        Application matchedApplication = applicationRepository.findById(applicationId).orElseThrow(() -> new NotFoundException("can't find application. id:" + applicationId));

        if(!username.equals(requirement.getUser().getUsername())){
            throw new InvalidAccessException("this access is not authorized");
        }

        if(!requirement.getActualStatus().equals(Requirement.RECRUITING)){
            throw new WrongApplicationException("this requirement is expired. id:" + requirementId);
        }
        if(matchedApplication.getStatus()!=MatchStatus.NOT_MATCHED){
            throw new WrongApplicationException("this application is expired. id:" + applicationId);
        }

        requirement.changeStatus(MatchStatus.MATCHED);
        matchedApplication.changeStatus(MatchStatus.MATCHED);


        return matchRepository.save(MatchEntity.builder()
                .requirement(requirement)
                .application(matchedApplication)
                .status(MatchEntityStatus.IN_PROGRESS)
                .createTime(LocalDateTime.now())
                .build());
    }

}
