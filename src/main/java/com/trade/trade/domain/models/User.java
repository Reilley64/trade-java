package com.trade.trade.domain.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JoinFormula;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

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
    @NotNull @Enumerated(EnumType.STRING) private Role role = Role.USER;

    @JoinFormula("(SELECT us.id"
            + " FROM user_snapshots us"
            + " WHERE us.user_id = id"
            + " ORDER BY us.created_at DESC"
            + " LIMIT 1)")
    @ManyToOne UserSnapshot latestSnapshot;

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
