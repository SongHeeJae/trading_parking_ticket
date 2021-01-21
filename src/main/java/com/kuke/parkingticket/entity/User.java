package com.kuke.parkingticket.entity;


import com.kuke.parkingticket.entity.date.CommonDateEntity;
import lombok.*;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Getter
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

    private String provider;

    private String refreshToken;

    @ManyToOne(fetch = FetchType.LAZY)
    private Town town;

    @ElementCollection(fetch = FetchType.LAZY)
    @Enumerated(EnumType.STRING)
    private List<Role> roles = new ArrayList<>();

    @OneToMany(mappedBy = "writer")
    private List<Ticket> tickets = new ArrayList<>();

    @OneToMany(mappedBy = "buyer")
    private List<Review> typingReviews = new ArrayList<>(); // 작성한 리뷰

    @OneToMany(mappedBy = "seller")
    private List<Review> typedReviews = new ArrayList<>(); // 본인에게 작성된 리뷰

    @OneToMany(mappedBy = "buyer")
    private List<History> purchases = new ArrayList<>(); // 구매 내역

    @OneToMany(mappedBy = "seller")
    private List<History> sales = new ArrayList<>(); // 판매 내역

    public static User createUser(String uid, String password, String nickname, Town town, String provider) {
        User user = new User();
        user.uid = uid;
        user.password = password;
        user.nickname = nickname;
        user.town = town;
        user.provider = provider;
        user.roles = Collections.singletonList(Role.ROLE_NORMAL);
        return user;
    }

    public void addRole(Role role) {
        ArrayList<Role> roles = new ArrayList<>(this.roles);
        roles.add(role);
        this.roles = roles;
    }

    public void update(String nickname, Town town) {
        this.nickname = nickname;
        this.town = town;
    }

    public void changeRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
