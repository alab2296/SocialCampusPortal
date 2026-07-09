package com.campusportal.admin;

import java.util.ArrayList;
import java.util.List;

public class Department {
    public String id;
    public String name;
    public String code;
    public List<String> programs;

    public Department() {
        programs = new ArrayList<>();
    }

    public Department(String name, String code) {
        this.name = name;
        this.code = code;
        this.programs = new ArrayList<>();
    }
}
