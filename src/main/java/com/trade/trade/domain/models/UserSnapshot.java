package com.trade.trade.domain.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.trade.trade.domain.datatransferobjects.AssetHolding;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@Table(name = "user_snapshots")
public class UserSnapshot extends Model {
    @NotNull private long accountBalance;
    @NotNull private long accountValue;
    @NotNull @Column(columnDefinition = "jsonb") @Type(type = "jsonb") private List<AssetHolding> assetHoldings;

    @ManyToOne @JsonIgnore @NotNull private User user;
}
