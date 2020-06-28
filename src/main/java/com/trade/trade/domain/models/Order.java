package com.trade.trade.domain.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.trade.trade.domain.models.assets.Asset;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@Entity
@NoArgsConstructor
@Table(name = "orders")
public class Order extends Model {
    public enum Direction {
        BUY, SELL;
    }

    @ManyToOne @NotNull private Asset asset;
    @ManyToOne @JsonIgnore @NotNull private User user;
    @NotNull private long quantity;
    @NotNull private long price;
    @NotNull private long brokerage;
    @Enumerated(EnumType.ORDINAL) @NotNull private Direction direction;

    public long getSubTotal() {
        return this.quantity * this.price;
    }

    public long getTotal() {
        if (this.direction == Direction.BUY) {
            return (this.quantity * this.price) + this.brokerage;
        }
        return (this.quantity * this.price) - this.brokerage;
    }

    @JsonIgnore
    public User getUser() {
        return user;
    }

    @JsonProperty
    public void setUser(User user) {
        this.user = user;
    }
}
