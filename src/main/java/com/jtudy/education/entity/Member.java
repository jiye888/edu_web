package com.jtudy.education.entity;

import com.jtudy.education.constant.Roles;
import com.jtudy.education.constant.RolesConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mem_num")
    private Long memNum;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column
    private String address;

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    private List<AcademyMember> academyMember = new ArrayList<>();

    @OneToMany(mappedBy = "writer", fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    private List<Review> review = new ArrayList<>();

    @Convert(converter = RolesConverter.class)
    @Builder.Default
    private List<Roles> rolesList = new ArrayList<>();

    public void updateMember(String password, String name, String address) {
        this.password = password;
        this.name = name;
        this.address = address;
    }

    public void addRoles(Roles roles) {
        this.rolesList.add(roles);
    }

    public void removeRoles(Roles roles) {
        this.rolesList.remove(roles);
    }

}
