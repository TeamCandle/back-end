package creative.design.carrotbow.matching.service;

import com.google.firebase.messaging.FirebaseMessagingException;
import creative.design.carrotbow.matching.domain.Application;
import creative.design.carrotbow.matching.domain.MatchEntity;
import creative.design.carrotbow.matching.domain.Requirement;
import creative.design.carrotbow.matching.domain.dto.ListMatchDto;
import creative.design.carrotbow.matching.domain.dto.MatchDto;
import creative.design.carrotbow.error.InvalidAccessException;
import creative.design.carrotbow.error.NotFoundException;
import creative.design.carrotbow.error.WrongApplicationException;
import creative.design.carrotbow.matching.domain.dto.type.MatchEntityStatus;
import creative.design.carrotbow.matching.domain.dto.type.MatchStatus;
import creative.design.carrotbow.matching.domain.repository.ApplicationRepository;
import creative.design.carrotbow.matching.domain.repository.MatchRepository;
import creative.design.carrotbow.matching.domain.repository.RequirementRepository;
import creative.design.carrotbow.security.auth.AuthenticationUser;
import creative.design.carrotbow.external.geo.GeoService;
import creative.design.carrotbow.external.fcm.FcmService;
import creative.design.carrotbow.external.redis.RedisService;
import creative.design.carrotbow.external.s3.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@Slf4j(topic = "ACCESS_LOG")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MatchService {

    private final RequirementRepository requirementRepository;
    private final ApplicationRepository applicationRepository;
    private final MatchRepository matchRepository;

    private final FcmService fcmService;
    private final GeoService geoService;
    private final S3Service s3Service;
    private final MessageUtils messageUtils;



    public List<ListMatchDto> getMatches(int offset, AuthenticationUser user){
        List<MatchEntity> matchList = matchRepository.findListWithRequirementByUserId(user.getId(), offset);

        List<ListMatchDto> matches = new ArrayList<>();

        for (MatchEntity match : matchList) {
            matches.add(
                    ListMatchDto.builder()
                            .id(match.getId())
                            .image(s3Service.loadImage(match.getRequirement().getDog().getImage()))
                            .breed(match.getRequirement().getDog().getBreed())
                            .careType(match.getRequirement().getCareType().getActualName())
                            .time(messageUtils.generateListMatchMessage(match.getRequirement().getStartTime()))
                            .status(match.getStatus().getActualName())
                            .build());
        }

        return matches;
    }

    public ListMatchDto getUpcomingMatch(AuthenticationUser user){
        MatchEntity match = matchRepository.findUpcomingByUserId(user.getId()).orElseThrow(() -> new NotFoundException("can't find match. userId:" + user.getId()));

        return ListMatchDto.builder()
                .id(match.getId())
                .image(s3Service.loadImage(match.getRequirement().getDog().getImage()))
                .breed(match.getRequirement().getDog().getBreed())
                .careType(match.getRequirement().getCareType().getActualName())
                .time(messageUtils.generateListMatchMessage(match.getRequirement().getStartTime()))
                .status(match.getStatus().getActualName())
                .build();
    }

    public MatchEntity getMatch(Long id){
        return matchRepository.findWithFullById(id).orElseThrow(()->new NotFoundException("can't find match. id:" + id));
    }

    public HashMap<String, Object> getMatch(Long id, AuthenticationUser authenticationUser){
        MatchEntity match = matchRepository.findWithFullById(id).orElseThrow(()->new NotFoundException("can't find match. id:" + id));

        Long requestPerson = authenticationUser.getId();
        Long requirePerson=match.getRequirement().getUser().getId();
        Long applyPerson=match.getApplication().getUser().getId();

        if(!requestPerson.equals(requirePerson)&&!requestPerson.equals(applyPerson)){
            throw new InvalidAccessException("this access is not authorized");
        }

        MatchDto details = MatchDto.builder()
                .id(match.getId())
                .userId(requestPerson.equals(requirePerson) ? applyPerson : requirePerson)
                .dogId(match.getRequirement().getDog().getId())
                .dogImage(s3Service.loadImage(match.getRequirement().getDog().getImage()))
                .careType(match.getRequirement().getCareType().getActualName())
                .startTime(match.getRequirement().getStartTime())
                .endTime(match.getRequirement().getEndTime())
                .careLocation(geoService.makePoint(match.getRequirement().getCareLocation()))
                .description(match.getRequirement().getDescription())
                .reward(match.getRequirement().getReward())
                .status(match.getStatus().getActualName())
                .build();

        HashMap<String, Object> result = new HashMap<>();

        result.put("details", details);
        result.put("requester", requestPerson.equals(requirePerson));

        return result;
    }


    @Transactional
    public Long makeMatch(Long requirementId, Long applicationId, AuthenticationUser user) throws FirebaseMessagingException {
        Requirement requirement = requirementRepository.findWithApplicationsById(requirementId).orElseThrow(() -> new NotFoundException("can't find requirement. id:" + requirementId));
        Application matchedApplication = applicationRepository.find(applicationId).orElseThrow(() -> new NotFoundException("can't find application. id:" + applicationId));

        if(!user.getId().equals(requirement.getUser().getId())){
            throw new InvalidAccessException("this access is not authorized");
        }

        boolean check = false;

        for(Application application: requirement.getApplications()){
            if(application.getId().equals(applicationId)){
                check = true;
                break;
            }
        }
        if(!check){
            throw new InvalidAccessException("this access is not authorized");
        }

        if(!requirement.getActualStatus().equals(Requirement.RECRUITING)){
            throw new WrongApplicationException("this requirement is expired. id:" + requirementId);
        }
        if(matchedApplication.getStatus()!= MatchStatus.NOT_MATCHED){
            throw new WrongApplicationException("this application is expired. id:" + applicationId);
        }

        requirement.changeStatus(MatchStatus.MATCHED);
        matchedApplication.changeStatus(MatchStatus.MATCHED);


        String message = messageUtils.generateAcceptMessage(requirement.getStartTime());

        String token = fcmService.getToken(matchedApplication.getUser().getId());
        fcmService.sendMessageByToken(requirement.getCareType().getActualName(), message, token);

        Long matchId = matchRepository.save(MatchEntity.builder()
                .requirement(requirement)
                .application(matchedApplication)
                .status(MatchEntityStatus.WAITING_PAYMENT)
                .createTime(LocalDateTime.now())
                .build());


        log.info("신청 수락 & 매칭. 요구사항 Id={}, 신청 Id={}, 매칭 Id={}", requirementId, applicationId, matchId);

        return matchId;
    }

    @Transactional
    public void completeMatch(Long id, AuthenticationUser user){

        MatchEntity match = matchRepository.findWithRequirementById(id).orElseThrow(() -> new NotFoundException("can't find match. id:" + id));

        if(!user.getId().equals(match.getRequirement().getUser().getId())){
            throw new InvalidAccessException("this access is not authorized");
        }

        /*
        if(match.getStatus()!=MatchEntityStatus.NOT_COMPLETED){
            throw new InvalidAccessException("this access is not authorized");
        }*/

        match.changeStatus(MatchEntityStatus.COMPLETED);

        log.info("요구사항 완료. 매칭 Id={}", id);
    }

    @Transactional
    public void cancelMatch(Long id, AuthenticationUser user){

        MatchEntity match = matchRepository.findWithFullById(id).orElseThrow(() -> new NotFoundException("can't find match. id:" + id));

        if(!user.getId().equals(match.getRequirement().getUser().getId()) && !user.getId().equals(match.getApplication().getUser().getId())){
            throw new InvalidAccessException("this access is not authorized");
        }
        if(match.getStatus()!=MatchEntityStatus.WAITING_PAYMENT){
            throw new InvalidAccessException("this access is not authorized");
        }

        match.changeStatus(MatchEntityStatus.CANCELLED);

        log.info("매칭 취소. 매칭 Id={}", id);
    }

}
