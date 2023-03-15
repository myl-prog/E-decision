package com.example.edecision.model.user;

import com.example.edecision.model.common.BodyId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserRoleBody {
    @ApiModelProperty(notes = "Identifiant du rôle de l'utilisateur", required = true)
    private BodyId userRole;
}
