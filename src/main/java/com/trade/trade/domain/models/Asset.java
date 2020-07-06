package com.trade.trade.domain.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@Table(
        name = "assets",
        uniqueConstraints={
                @UniqueConstraint(columnNames = {"symbol", "exchange_id"})
        }
)
public class Asset extends Model {
    @NotEmpty private String symbol;
    @NotEmpty private String name;
    @Column(columnDefinition = "text") private String description;
    @NotEmpty private String image;
    @NotEmpty private String industry;
    @NotEmpty private String sector;
    private String website;

    @ManyToOne @NotNull Exchange exchange;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "asset") @JsonIgnore private List<AssetValuation> assetValuation = new ArrayList<>();
}
