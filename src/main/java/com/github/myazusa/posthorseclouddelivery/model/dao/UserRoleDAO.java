package com.github.myazusa.posthorseclouddelivery.model.dao;

import lombok.Data;

import java.util.UUID;

@Data
public class UserRoleDAO {
    private UUID userUuid;
    private UUID roleUuid;
}
