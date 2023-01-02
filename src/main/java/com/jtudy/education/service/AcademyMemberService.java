package com.jtudy.education.service;

public interface AcademyMemberService {

    /*
    Page<AcademyMemberDTO> getList();

    AcademyMemberDTO get(Long amNum);

    Long register(AcademyMemberDTO academyMemberDTO);

    // Long update(AcademyMemberDTO academyMemberDTO);

    Long addMember(AcademyMemberDTO academyMemberDTO, MemberDTO memberDTO);

    Long removeMember(Long amNum, Long memNum);

    void delete(Long amNum);

    default SubjectClass dtoToEntity(AcademyMemberDTO academyMemberDTO) {
        Academy academy = Academy.builder()
                .acaNum(academyMemberDTO.getAcaNum())
                .acaName(academyMemberDTO.getAcaName())
                .build();

        Member member = Member.builder()
                .memNum(academyMemberDTO.getMemNum())
                .name(academyMemberDTO.getMemName())
                .build();

        //멤버 리스트를 주고받아야.

        SubjectClass academyMember = SubjectClass.builder()
                .amNum(academyMemberDTO.getAmNum())
                .className(academyMemberDTO.getClassName())
                .build();

        return academyMember;
    }

    default AcademyMemberDTO entityToDTO(SubjectClass academyMember) {
        AcademyMemberDTO academyMemberDTO = AcademyMemberDTO.builder()
                .amNum(academyMember.getAmNum())
                .className(academyMember.getClassName())
                .acaNum(academyMember.getAcademy().getAcaNum())
                .acaName(academyMember.getAcademy().getAcaName())
                //.memNum(academyMember.getMembers().getMemNum())
                //.memName(academyMember.getMembers().getName())
                .build();

        return academyMemberDTO;
    }

 */

}
