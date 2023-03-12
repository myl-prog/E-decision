package com.example.edecision.model.project;

import lombok.Data;

import java.util.List;

@Data
public class ProjectBody {
    private Project project;
    private List<Integer> teamIdList;
    private List<ProjectUser> projectUsers;
}
