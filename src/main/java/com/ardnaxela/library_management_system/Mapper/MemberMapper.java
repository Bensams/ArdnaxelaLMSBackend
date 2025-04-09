package com.ardnaxela.library_management_system.Mapper;

import com.ardnaxela.library_management_system.Member.Member;
import com.ardnaxela.library_management_system.Member.MemberDTO;

import java.time.format.DateTimeFormatter;

public class MemberMapper {

    public static MemberDTO toDTO(Member member) {
        if (member == null) {
            return null;
        }
        MemberDTO memberDTO = new MemberDTO();
        memberDTO.setId(member.getId());
        memberDTO.setName(member.getName());
        memberDTO.setEmail(member.getEmail());
        memberDTO.setPhoneNumber(member.getPhoneNumber());
        memberDTO.setAddress(member.getAddress());
        memberDTO.setUsername(member.getUser().getUsername()); // Assuming Member has a User object
        memberDTO.setCreatedAt(member.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        return memberDTO;
    }

    public static Member toEntity(MemberDTO memberDTO) {
        if (memberDTO == null) {
            return null;
        }
        Member member = new Member();
        member.setId(memberDTO.getId());
        member.setName(memberDTO.getName());
        member.setEmail(memberDTO.getEmail());
        member.setPhoneNumber(memberDTO.getPhoneNumber());
        member.setAddress(memberDTO.getAddress());
        return member;
    }
}
