package creative.design.carrotbow.matching.controller;


import creative.design.carrotbow.matching.domain.dto.PayApproveResponseDto;
import creative.design.carrotbow.matching.domain.dto.PayCancelResponseDto;
import creative.design.carrotbow.matching.domain.dto.PayReadyResponseDto;
import creative.design.carrotbow.matching.service.PaymentService;
import creative.design.carrotbow.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@Slf4j(topic = "ACCESS_LOG")
@RequiredArgsConstructor
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentService paymentService;


    //Test용 Get 후에 적절히 변경
    @GetMapping("/ready")
    @ResponseBody
    public String payReady(@RequestParam Long matchId, @AuthenticationPrincipal PrincipalDetails principalDetails){

        log.info("GET /payment/ready?matchId={}", matchId);

        PayReadyResponseDto readyResponse = paymentService.payReady(matchId, principalDetails.getUser());

        log.info("we are fucked up");


        //return "redirect:" + readyResponse.getNext_redirect_pc_url();

        return readyResponse.getNext_redirect_app_url();
    }

    @PatchMapping("/approve/alternative")
    public ResponseEntity<?> alternateApprove(@RequestParam Long matchId, @AuthenticationPrincipal PrincipalDetails principalDetails){
        log.info("GET /payment/approve/alternative?matchId={}", matchId);

        HashMap<String, String> result = paymentService.alternateApprove(matchId, principalDetails.getUser());
        return ResponseEntity.ok().body(result);
    }

    @PatchMapping("/refund/alternative")
    public ResponseEntity<?> alternateRefund(@RequestParam Long matchId, @AuthenticationPrincipal PrincipalDetails principalDetails){
        log.info("GET /payment/refund/alternative?matchId={}", matchId);

        HashMap<String, String> result = paymentService.alternateRefund(matchId, principalDetails.getUser());
        return ResponseEntity.ok().body(result);
    }

    @GetMapping("/approve")
    public ResponseEntity<?> payApprove(@RequestParam Long partner_order_id, @RequestParam String pg_token){

        log.info("GET /payment/approve?order_id={}", partner_order_id);

        PayApproveResponseDto approveResponse = paymentService.payApprove(partner_order_id, pg_token);

        HashMap<String, Object> result = new HashMap<>();
        result.put("payment cost", approveResponse.getAmount().getTotal());
        result.put("approve time", approveResponse.getApproved_at());


        return ResponseEntity.ok().body(result);
    }

    @GetMapping("/fail")
    public ResponseEntity<?> payFail(@RequestParam String partner_order_id){

        log.info("GET /payment/fail?order_id={}", partner_order_id);

        return ResponseEntity.badRequest().body("fail payment");
    }

    @GetMapping("/cancel")
    public ResponseEntity<?> payCancel(@RequestParam String partner_order_id){

        log.info("GET /payment/cancel?order_id={}", partner_order_id);

        return ResponseEntity.ok().body("cancel payment");
    }

    @PatchMapping("/refund")
    public ResponseEntity<?> payRefund(@RequestParam Long matchId, @AuthenticationPrincipal PrincipalDetails principalDetails){

        log.info("GET /payment/refund?matchId={}", matchId);

        PayCancelResponseDto payCancel = paymentService.payRefund(matchId, principalDetails.getUser());

        HashMap<String, Object> result = new HashMap<>();
        result.put("refund cost", payCancel.getApproved_cancel_amount().getTotal());
        result.put("approve time", payCancel.getApproved_at());

        return ResponseEntity.ok().body(result);
    }
}
