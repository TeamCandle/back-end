package creative.design.carrotbow.profile.domain.dto;

public enum DogSize {
    SMALL("소형"),
    MEDIUM("중형"),
    LARGE("대형");


    private final String actualName;

    DogSize(String actualName) {
        this.actualName = actualName;
    }

    public String getActualName() {
        return actualName;
    }
}
