package creative.design.carrotbow.security.oauth2.provider;

public interface Oauth2UserInfo {

    String getProviderId();

    String getProvider();

    String getEmail();

    String getName();

    public String getPhoneNumber();

    public int getBirthYear();


    public String getGender();
}
