package com.books_recommend.book_recommend.entity;

import com.books_recommend.book_recommend.common.entity.BaseTimeEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String email;

    @Column
    private String nickName;

    @Column
    private String password;

    @Enumerated(value = EnumType.STRING)
    @Column
    private ROLES roles;

    @Column
    private LocalDateTime deletedAt;

    @JsonIgnore
    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();

    @Getter
    @RequiredArgsConstructor
    public enum ROLES {
        MEMBER("일반회원"),
        ADMIN("관리자");

        private final String roles;
    }

    public Member(String email,
                  String nickName,
                  String password) {
        this.email = email;
        this.nickName = nickName;
        this.password = password;
        this.roles = ROLES.MEMBER;
    }

    public void remove(){
        this.deletedAt = LocalDateTime.now();
    }

    public void update(String nickName){
        this.nickName = nickName;
    }
}