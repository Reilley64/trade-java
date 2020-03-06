package com.trade.trade.models;

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
@Table(name = "asset_holdings")
public class AssetHolding extends Model {
    @NotNull private long quantity;

    @ManyToOne
    @NotNull private Asset asset;

    @ManyToOne
    @JsonIgnore @NotNull private User user;
}
