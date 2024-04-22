package creative.design.carrotbow.dto.pay;


import creative.design.carrotbow.dto.pay.Amount;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
public class PayCancelResponseDto {

    private String aid;
    private String tid;
    private String cid;
    private String status;
    private String partner_order_id;
    private String partner_user_id;
    private String payment_method_type;
    private Amount amount;
    private ApprovedCancelAmount approved_cancel_amount;
    private String item_name;
    private String item_code;
    private int quantity;
    private LocalDateTime created_at;
    private LocalDateTime approved_at;
    private LocalDateTime canceled_at;
    private String payload;

    /**
     * 이번 요청으로 취소된 금액
     */
    @Getter
    @Setter
    @ToString
    public static class ApprovedCancelAmount {

        private int total; // 이번 요청으로 취소된 전체 금액
        private int tax_free; // 이번 요청으로 취소된 비과세 금액
        private int vat; // 이번 요청으로 취소된 부가세 금액
        private int point; // 이번 요청으로 취소된 포인트 금액
        private int discount; // 이번 요청으로 취소된 할인 금액
        private int green_deposit; // 컵 보증금
    }
}
