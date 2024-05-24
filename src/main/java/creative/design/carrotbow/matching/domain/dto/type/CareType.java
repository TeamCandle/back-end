package creative.design.carrotbow.matching.domain.dto.type;

public enum CareType {
    WALKING("산책"),
    BOARDING("돌봄"),
    GROOMING("외견 케어"),
    PLAYTIME("놀아주기"),
    ETC("기타");

    private final String actualName;

    CareType(String actualName) {
        this.actualName = actualName;
    }

    public String getActualName() {
        return actualName;
    }
}
