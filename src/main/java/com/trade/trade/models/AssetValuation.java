package com.trade.trade.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@Entity
@NoArgsConstructor
@Table(name = "asset_valuations")
public class AssetValuation extends Model {
    @NotNull @ManyToOne private Asset asset;
    @NotNull private Date date;
    @NotNull private long open;
    @NotNull private long close;
    @NotNull private long high;
    @NotNull private long low;
    @NotNull private long volume;
}
