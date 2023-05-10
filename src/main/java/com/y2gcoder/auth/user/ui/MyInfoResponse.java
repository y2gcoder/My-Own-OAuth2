package com.y2gcoder.auth.user.ui;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.y2gcoder.auth.user.domain.User;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class MyInfoResponse {

    private String id;
    private String email;
    private String name;
    @JsonProperty("is_deleted")
    private boolean isDeleted;

    public MyInfoResponse(User user) {
        this.id = user.getId().getValue();
        this.email = user.getEmail();
        this.name = user.getName();
        this.isDeleted = user.isDeleted();
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    @JsonProperty("is_deleted")
    public boolean isDeleted() {
        return isDeleted;
    }
}
