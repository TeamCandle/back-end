package creative.design.carrotbow.matching.domain.dto.type;

public enum MatchEntityStatus {
    COMPLETED("완료"),
    NOT_COMPLETED("미완료"),
    WAITING_PAYMENT("결제 대기중"),
    CANCELLED("취소됨");

    private final String actualName;

    MatchEntityStatus(String actualName) {
        this.actualName = actualName;
    }

    public String getActualName() {
        return actualName;
    }
}
