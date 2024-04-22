package creative.design.carrotbow.domain;

public enum MatchEntityStatus {
    COMPLETED, //완료됨    (payment/refund)
    NOT_COMPLETED, //완료버튼   (payment/refund)
    WAITING_PAYMENT, //결제 버튼   (match/cancel)
    CANCELLED //취소 or 환불 (취소됨)
}
