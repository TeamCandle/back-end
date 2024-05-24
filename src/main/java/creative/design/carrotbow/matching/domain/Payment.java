package creative.design.carrotbow.matching.domain;

import creative.design.carrotbow.matching.domain.dto.type.PaymentStatus;
import creative.design.carrotbow.profile.domain.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class Payment {

    @Id
    @GeneratedValue
    private Long id;

    private String tid;

    private String paymentMethod;

    private Integer amount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private LocalDateTime approveTime;

    private LocalDateTime cancelTime;

    private LocalDateTime createdTime;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(mappedBy = "payment", cascade = CascadeType.ALL, orphanRemoval = true)
    private MatchEntity match;


    @Builder
    public Payment(Integer amount, PaymentStatus status, User user) {
        this.amount = amount;
        this.status = status;
        this.user = user;
    }


    public void linkMatch(MatchEntity match){
        this.match = match;
    }

    public void changeStatus(PaymentStatus status){
        this.status=status;
    }

    public void setTid(String tid){
        this.tid=tid;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void setApproveTime(LocalDateTime approveTime) {
        this.approveTime = approveTime;
    }

    public void setCancelTime(LocalDateTime cancelTime) {
        this.cancelTime = cancelTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }
}
