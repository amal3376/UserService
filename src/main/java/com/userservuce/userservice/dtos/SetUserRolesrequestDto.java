package com.userservuce.userservice.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SetUserRolesrequestDto {
    private List<Long> roleIds;
}
