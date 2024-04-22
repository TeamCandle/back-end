package creative.design.carrotbow.domain;

public enum CareType {
    WALKING("산책"),
    BOARDING("돌봄"),
    GROOMING("외견 케어"),
    PLAYTIME("놀아주기"),
    ETC("기타");

    private String actualName;

    CareType(String actualName) {
        this.actualName = actualName;
    }

    public String getActualName() {
        return actualName;
    }
}
