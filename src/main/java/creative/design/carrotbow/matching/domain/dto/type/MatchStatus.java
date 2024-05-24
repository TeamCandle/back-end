package creative.design.carrotbow.matching.domain.dto.type;

public enum MatchStatus {
    MATCHED("매칭"),
    NOT_MATCHED("매칭 실패"),
    CANCELLED("취소됨");


    private final String actualName;

    MatchStatus(String actualName) {
        this.actualName = actualName;
    }

    public String getActualName() {
        return actualName;
    }
}
