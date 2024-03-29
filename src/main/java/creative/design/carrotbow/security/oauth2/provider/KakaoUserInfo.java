package creative.design.carrotbow.security.oauth2.provider;

import java.time.LocalDate;
import java.util.Date;
import java.util.Map;

public class KakaoUserInfo implements Oauth2UserInfo{

    private Map<String, Object> attributes;

    private Map<String, Object> profileDatas;

    public KakaoUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
        this.profileDatas = (Map)attributes.get("kakao_account");

        System.out.println(profileDatas);
    }

    @Override
    public String getProviderId() {
        return attributes.get("id").toString();
    }

    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public String getName() {
        return profileDatas.get("name").toString();
    }

    @Override
    public String getEmail() {
        return profileDatas.get("email").toString();
    }

    @Override
    public String getPhoneNumber(){
        return profileDatas.get("phone_number").toString();
    }

    @Override
    public int getBirthYear(){
        return Integer.parseInt(profileDatas.get("birthyear").toString());
    }

    @Override
    public String getGender() {
        return profileDatas.get("gender").toString();
    }
}
