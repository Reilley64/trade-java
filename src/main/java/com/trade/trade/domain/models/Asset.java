package com.trade.trade.domain.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@Table(name = "assets")
public class Asset extends Model {
    @Column(unique = true) @NotEmpty private String symbol;
    @NotEmpty private String name;
    @Column(columnDefinition = "text") @NotEmpty private String description;
    @NotEmpty private String image;
    @NotEmpty private String exchange;
    @NotEmpty private String industry;
    @NotEmpty private String sector;
    @NotEmpty private String website;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "asset") @JsonIgnore private List<AssetValuation> assetValuation = new ArrayList<>();
}
