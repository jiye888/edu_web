package com.jtudy.education.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class SubjectClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "am_num")
    private Long amNum;

    @Column(name = "class_name")
    @Enumerated(EnumType.STRING)
    private com.jtudy.education.constant.Subject className;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Academy academy;

    @OneToMany(mappedBy = "academyMember", targetEntity = Member.class, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Member> members = new ArrayList<>();

    public void addMember(Member member) {
        members.add(member);
    }

    public void removeMember(Member member) {
        members.remove(member);
    }

    public Member findMember(Member member) {
        int number = this.members.indexOf(member);
        Member result = this.members.get(number);
        return result;
    }

    /*
    public static AcademyMember createAcademyMember(Academy academy, List<Member> memberList) {
        AcademyMember academyMember = new AcademyMember();
        for (Member member : memberList) {
            academyMember.addMember(member);
        }
        return academyMember;
    }
     */

}
