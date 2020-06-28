package com.trade.trade.domain.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Entity
@NoArgsConstructor
@Table(name = "users")
public class User extends Model {
    public enum Role {
        USER, ADMIN;
    }

    @NotEmpty @Column(unique = true) @Email private String username;
    @NotEmpty private String password;
    @NotEmpty private String firstName;
    @NotEmpty private String lastName;
    @NotNull @Enumerated(EnumType.ORDINAL) private Role role = Role.USER;

    public String getName() {
        return firstName + " " + lastName;
    }

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    @JsonProperty
    public void setPassword(String password) {
        this.password = password;
    }
}
