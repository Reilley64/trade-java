package com.trade.trade.domain.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@Entity
@NoArgsConstructor
@Table(name = "user_valuations")
public class UserValuation extends Model {
    @NotNull private long value;
    @ManyToOne @JsonIgnore @NotNull private User user;
}
