package com.trade.trade.domain.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@Entity
@NoArgsConstructor
@Table(name = "orders")
public class Order extends Model {
    public enum Direction {
        BUY, SELL;
    }

    @Min(1) @NotNull private long quantity;
    @NotNull private long price;
    @NotNull private long brokerage;
    @Enumerated(EnumType.ORDINAL) @NotNull private Direction direction;

    @ManyToOne @NotNull private Asset asset;
    @OneToOne(cascade = CascadeType.ALL) @NotNull private Transaction transaction;
    @ManyToOne @JsonIgnore @NotNull private User user;

    public long getSubTotal() {
        return this.quantity * this.price;
    }

    public long getTotal() {
        if (this.direction == Direction.BUY) {
            return (this.quantity * this.price) + this.brokerage;
        }
        return (this.quantity * this.price) - this.brokerage;
    }
}
