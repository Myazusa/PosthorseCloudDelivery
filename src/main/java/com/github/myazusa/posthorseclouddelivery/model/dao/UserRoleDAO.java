package com.github.myazusa.posthorseclouddelivery.model.dao;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.UUID;

@Data
@Accessors(chain = true)
public class UserRoleDAO {
    private UUID userUuid;
    private UUID roleUuid;
}
