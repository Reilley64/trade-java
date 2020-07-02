package com.trade.trade.domain.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Data
@Entity
@NoArgsConstructor
@Table(name = "transactions")
public class Transaction extends Model {
    public enum Direction {
        CREDIT, DEBIT;
    }

    @NotNull private long value;
    @NotNull private String description;
    @NotNull private Direction direction;

    @ManyToOne @JsonIgnore @NotNull private User user;
}
