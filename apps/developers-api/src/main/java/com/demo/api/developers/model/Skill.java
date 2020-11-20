package com.demo.api.developers.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Skill {

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Skill(){}
    public Skill(String code, String description){
        this.code = code;
        this.description = description;
    }

    @JsonProperty
    private String code;

    @JsonProperty
    private String description;
}
