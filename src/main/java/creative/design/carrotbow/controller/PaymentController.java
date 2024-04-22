package creative.design.carrotbow.controller;


import creative.design.carrotbow.domain.PaymentStatus;
import creative.design.carrotbow.dto.pay.PayApproveResponseDto;
import creative.design.carrotbow.dto.pay.PayCancelResponseDto;
import creative.design.carrotbow.dto.pay.PayReadyResponseDto;
import creative.design.carrotbow.security.auth.PrincipalDetails;
import creative.design.carrotbow.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentService paymentService;


    //Test용 Get 후에 적절히 변경
    @GetMapping("/ready")
    public String payReady(@RequestParam Long matchId/*, @AuthenticationPrincipal PrincipalDetails principalDetails*/){

        PayReadyResponseDto readyResponse = paymentService.payReady(matchId/*, principalDetails.getUser()*/);

        return "redirect:" + readyResponse.getNext_redirect_pc_url();
    }


    @GetMapping("/approve")
    public ResponseEntity<?> payApprove(@RequestParam String partner_order_id, @RequestParam String pg_token){
        PayApproveResponseDto approveResponse = paymentService.payApprove(partner_order_id, pg_token);

        return ResponseEntity.ok().body("success payment total:" + approveResponse.getAmount().getTotal());
    }

    @GetMapping("/fail")
    public ResponseEntity<?> payFail(){
        return ResponseEntity.badRequest().body("fail payment");
    }

    @GetMapping("/cancel")
    public ResponseEntity<?> payCancel(){
        return ResponseEntity.ok().body("cancel payment");
    }

    @GetMapping("/refund")
    public ResponseEntity<?> payRefund(@RequestParam Long matchId/*,  @AuthenticationPrincipal PrincipalDetails principalDetails*/){
        PayCancelResponseDto payCancel = paymentService.payRefund(matchId/*, principalDetails.getUser()*/);

        return ResponseEntity.ok().body("refund payment total:" + payCancel.getAmount().getTotal());
    }
}
