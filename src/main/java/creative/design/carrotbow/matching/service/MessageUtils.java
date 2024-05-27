package creative.design.carrotbow.matching.service;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class MessageUtils {

    public String generateApplyMessage(LocalDateTime time){
        return time.getMonthValue() + "월 " + time.getDayOfMonth() + "일자 건에 대한 신청이 도착했습니다.";
    }

    public String generateAcceptMessage(LocalDateTime time){
        return time.getMonthValue() + "월 " + time.getDayOfMonth() + "일자 건에 대한 신청이 수락되었습니다.";

    }

    public String generateListMatchMessage(LocalDateTime time){
        return time.getMonthValue() + "월 " + time.getDayOfMonth() + "일";
    }
}
