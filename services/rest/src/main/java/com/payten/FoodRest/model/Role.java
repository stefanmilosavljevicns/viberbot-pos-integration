package com.payten.FoodRest.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;


// POJO koji implementira Spring Security GrantedAuthority kojim se mogu definisati role u aplikaciji
@Document(collection = "ROLE")

public class Role implements GrantedAuthority {
    private static final long serialVersionUID = 1L;
    Long id;
    String name;

    @JsonIgnore
    @Override
    public String getAuthority() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    @JsonIgnore
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
