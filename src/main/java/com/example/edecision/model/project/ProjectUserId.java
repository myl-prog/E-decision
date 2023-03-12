package com.example.edecision.model.project;

import lombok.Data;

import java.io.Serializable;

@Data
public class ProjectUserId implements Serializable {
    private int userId;
    private int projectId;
}
