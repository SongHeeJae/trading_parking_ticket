package com.kuke.parkingticket.entity;


import com.kuke.parkingticket.entity.date.CommonDateEntity;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class User extends CommonDateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String uid;

    private String password;

    @Column(nullable = false, unique = true, length = 30)
    private String nickname;

    private String refreshToken;

    @ManyToOne(fetch = FetchType.LAZY)
    private Town town;

    @Builder.Default
    @OneToMany(mappedBy = "writer")
    private List<Ticket> tickets = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "buyer")
    private List<Review> typingReviews = new ArrayList<>(); // 작성한 리뷰

    @Builder.Default
    @OneToMany(mappedBy = "seller")
    private List<Review> typedReviews = new ArrayList<>(); // 본인에게 작성된 리뷰

    @Builder.Default
    @OneToMany(mappedBy = "buyer")
    private List<History> purchases = new ArrayList<>(); // 구매 내역

    @Builder.Default
    @OneToMany(mappedBy = "seller")
    private List<History> sales = new ArrayList<>(); // 판매 내역

    public static User createUser(String uid, String password, String nickname, Town town) {
        return User.builder()
                .uid(uid)
                .password(password)
                .nickname(nickname)
                .town(town)
                .build();
    }

    public void update(String nickname, Town town) {
        this.nickname = nickname;
        this.town = town;
    }

    public void changeRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
