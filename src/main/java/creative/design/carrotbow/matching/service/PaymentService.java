package creative.design.carrotbow.matching.service;

import creative.design.carrotbow.matching.domain.dto.PayApproveResponseDto;
import creative.design.carrotbow.matching.domain.dto.PayCancelResponseDto;
import creative.design.carrotbow.matching.domain.dto.PayReadyResponseDto;
import creative.design.carrotbow.error.InvalidAccessException;
import creative.design.carrotbow.error.NotFoundException;
import creative.design.carrotbow.error.PayProcessException;
import creative.design.carrotbow.matching.domain.MatchEntity;
import creative.design.carrotbow.matching.domain.Payment;
import creative.design.carrotbow.matching.domain.Requirement;
import creative.design.carrotbow.matching.domain.dto.type.MatchEntityStatus;
import creative.design.carrotbow.matching.domain.dto.type.PaymentStatus;
import creative.design.carrotbow.matching.domain.repository.MatchRepository;
import creative.design.carrotbow.matching.domain.repository.PaymentRepository;
import creative.design.carrotbow.security.auth.AuthenticationUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j(topic = "ACCESS_LOG")
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final MatchRepository matchRepository;


    @Value("${payment.kakao.cid}")
    private String cid;

    @Value("${payment.kakao.secretKey}")
    private String secretKey;

    @Value("${payment.kakao.partnerServer}")
    private String partnerServer;

    private HttpHeaders getHeader(){
        HttpHeaders headers=new HttpHeaders();
        String auth = "SECRET_KEY "+secretKey;
        headers.set("Content-type","application/json");
        headers.set("Authorization",auth);

        return  headers;
    }

    public PayReadyResponseDto payReady(Long matchId, AuthenticationUser user){


        Payment payment = paymentRepository.findWithMatchByMatchId(matchId).orElse(null);
        MatchEntity match;
        Requirement requirement;

        if(payment==null){
            match = matchRepository.findWithRequirementById(matchId).orElseThrow(()->new NotFoundException("can't find match. id:" + matchId));
        }
        else{
            match= payment.getMatch();
        }
        requirement = match.getRequirement();


        //테스트 용도로 주석처리

        if(!user.getId().equals(requirement.getUser().getId())){
            throw new InvalidAccessException("this access is not authorized");
        }

        if(match.getStatus()!= MatchEntityStatus.WAITING_PAYMENT){
            throw new InvalidAccessException("this access is not authorized");
        }

        Long paymentId;

        if(payment==null){
            payment = Payment.builder()
                    .user(requirement.getUser())
                    .status(PaymentStatus.NOT_APPROVED)
                    .amount(match.getRequirement().getReward())
                    .match(match)
                    .build();

            paymentId = paymentRepository.save(payment);

            log.info("페이먼트 생성. 페이먼트 Id={}", paymentId);
        }

        paymentId = payment.getId();
        log.info("페이먼트 조회. 페이먼트 Id={}", paymentId);

        String orderId = paymentId.toString();

        HashMap<String,String> map=new HashMap<>();
        map.put("cid",cid);
        map.put("partner_order_id", orderId);
        map.put("partner_user_id", requirement.getUser().getUsername());
        map.put("item_name",requirement.getCareType().getActualName() + "대행");
        map.put("quantity","1");
        map.put("total_amount", Integer.toString(requirement.getReward()));
        map.put("tax_free_amount", "0");

        map.put("approval_url", partnerServer+"/approve?partner_order_id="+orderId); // 성공 시 redirect url
        map.put("cancel_url", partnerServer); // 취소 시 redirect url
        map.put("fail_url", partnerServer); // 실패 시 redirect url

        HttpHeaders headers = this.getHeader();

        HttpEntity<HashMap<String, String>> urlRequest = new HttpEntity<>(map, headers);

        RestTemplate rt = new RestTemplate();
        PayReadyResponseDto readyResponse = rt.postForObject(
                "https://open-api.kakaopay.com/online/v1/payment/ready",
                urlRequest,
                PayReadyResponseDto.class);

        if(readyResponse==null){
            throw new PayProcessException("There's a problem while processing payment");
        }

        payment.setTid(readyResponse.getTid());
        payment.setCreatedTime(readyResponse.getCreated_at());

        log.info("결제 준비. 페이먼트 Id={}", orderId);

        return readyResponse;
    }

    //대체용
    public HashMap<String, String> alternateApprove(Long matchId, AuthenticationUser user){
        Payment payment = paymentRepository.findWithMatchByMatchId(matchId).orElseThrow(() -> new NotFoundException("can't find payment. matchId:" + matchId));
        MatchEntity match = payment.getMatch();

        if(!user.getId().equals(payment.getUser().getId())){
            throw new InvalidAccessException("this access is not authorized");
        }

        if(payment.getStatus()!=PaymentStatus.NOT_APPROVED){
            throw new InvalidAccessException("this access is not authorized");
        }

        match.changeStatus(MatchEntityStatus.NOT_COMPLETED);
        payment.setPaymentMethod("MONEY");
        payment.setApproveTime(LocalDateTime.now());
        payment.changeStatus(PaymentStatus.APPROVED);

        log.info("결제 승인. 페이먼트 Id={}", payment.getId());

        HashMap<String, String> result = new HashMap<>();
        result.put("payment cost", payment.getAmount().toString());
        result.put("approve time", payment.getApproveTime().toString());

        return result;
    }

    public HashMap<String, String> alternateRefund(Long matchId, AuthenticationUser user){
        Payment payment = paymentRepository.findWithMatchByMatchId(matchId).orElseThrow(() -> new NotFoundException("can't find payment. matchId:" + matchId));
        MatchEntity match = payment.getMatch();

        if(!user.getId().equals(payment.getUser().getId()) && !user.getId().equals(match.getApplication().getUser().getId())){
            throw new InvalidAccessException("this access is not authorized");
        }

        if(match.getStatus()!=MatchEntityStatus.NOT_COMPLETED){
            throw new InvalidAccessException("this access is not authorized");
        }

        match.changeStatus(MatchEntityStatus.CANCELLED);
        payment.changeStatus(PaymentStatus.REFUNDED);
        payment.setCancelTime(LocalDateTime.now());
        log.info("환불 승인. 페이먼트 Id={}", payment.getId());


        HashMap<String, String> result = new HashMap<>();
        result.put("payment cost", payment.getAmount().toString());
        result.put("approve time", payment.getApproveTime().toString());

        return result;
    }


    public PayApproveResponseDto payApprove(Long orderId, String pgToken) {

        Payment payment = paymentRepository.findWithMatchById(orderId).orElseThrow(() -> new NotFoundException("can't find payment. id:" + orderId));
        MatchEntity match = payment.getMatch();

        if(payment.getStatus()!=PaymentStatus.NOT_APPROVED){
            throw new InvalidAccessException("this access is not authorized");
        }

        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("cid", cid);
        parameters.put("tid", payment.getTid());
        parameters.put("partner_order_id", orderId.toString());
        parameters.put("partner_user_id", payment.getUser().getUsername());
        parameters.put("pg_token", pgToken);

        HttpHeaders headers = getHeader();
        HttpEntity<HashMap<String, String>> requestEntity = new HttpEntity<>(parameters, headers);

        RestTemplate restTemplate = new RestTemplate();

        PayApproveResponseDto approveResponse = restTemplate.postForObject(
                "https://open-api.kakaopay.com/online/v1/payment/approve",
                requestEntity,
                PayApproveResponseDto.class);

        if(approveResponse==null){
            throw new PayProcessException("There's a problem while processing payment");
        }

        match.changeStatus(MatchEntityStatus.NOT_COMPLETED);
        payment.setPaymentMethod(approveResponse.getPayment_method_type());
        payment.setApproveTime(approveResponse.getApproved_at());
        payment.changeStatus(PaymentStatus.APPROVED);

        log.info("결제 승인. 페이먼트 Id={}", orderId);

        return approveResponse;
    }

    public PayCancelResponseDto payRefund(Long matchId, AuthenticationUser user) {
        Payment payment = paymentRepository.findWithMatchByMatchId(matchId).orElseThrow(() -> new NotFoundException("can't find payment. matchId:" + matchId));
        MatchEntity match = payment.getMatch();

        //테스트 용도 주석처리

        if(!user.getId().equals(payment.getUser().getId()) && !user.getId().equals(match.getApplication().getUser().getId())){
            throw new InvalidAccessException("this access is not authorized");
        }


        if(match.getStatus()!=MatchEntityStatus.NOT_COMPLETED){
            throw new InvalidAccessException("this access is not authorized");
        }

        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("cid", cid);
        parameters.put("tid", payment.getTid());
        parameters.put("cancel_amount", payment.getAmount().toString());
        parameters.put("cancel_tax_free_amount", "0");


        HttpHeaders headers=getHeader();
        HttpEntity<HashMap<String, String>> requestEntity = new HttpEntity<>(parameters, headers);

        RestTemplate restTemplate = new RestTemplate();

        PayCancelResponseDto cancelResponse = restTemplate.postForObject(
                "https://open-api.kakaopay.com/online/v1/payment/cancel",
                requestEntity,
                PayCancelResponseDto.class);

        if(cancelResponse==null){
            throw new PayProcessException("There's a problem while processing payment");
        }

        match.changeStatus(MatchEntityStatus.CANCELLED);
        payment.changeStatus(PaymentStatus.REFUNDED);
        payment.setCancelTime(cancelResponse.getCanceled_at());

        log.info("환불 승인. 페이먼트 Id={}", payment.getId());

        return cancelResponse;
    }

}
