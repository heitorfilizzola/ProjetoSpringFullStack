package com.example.demo.dto;

import com.example.demo.models.Enterprise;
import com.example.demo.models.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;


import java.util.ArrayList;
import java.util.List;

public class CreateEnterpriseDTO {
    @NotBlank(message = "O nome da empresa não pode ser nulo")
    @Column(unique = true)
    private String enterpriseName;

    private List<User> userList = new ArrayList<>( );

    public String getEnterpriseName() {
        return enterpriseName;
    }

    public void setEnterpriseName(String enterpriseName) {
        this.enterpriseName = enterpriseName;
    }

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }


    public Enterprise toEntity() {
        Enterprise enterprise = new Enterprise();
        enterprise.setName(this.enterpriseName);
        enterprise.setUserList(this.userList);
        return enterprise;
    }

    public static CreateEnterpriseDTO fromEntity(Enterprise enterprise) {
        CreateEnterpriseDTO dto = new CreateEnterpriseDTO();
        dto.setEnterpriseName(enterprise.getName());
        dto.setUserList(enterprise.getUserList());
        return dto;
    }

}