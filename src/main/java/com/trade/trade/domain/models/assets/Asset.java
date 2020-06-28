package com.trade.trade.domain.models.assets;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.trade.trade.domain.models.AssetValuation;
import com.trade.trade.domain.models.Model;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@Inheritance
@Table(name = "assets")
public class Asset extends Model {
    @Column(unique = true) @NotEmpty private String symbol;
    @NotEmpty private String name;
    @NotEmpty private String image;
    @OneToMany(cascade = CascadeType.ALL) @JsonIgnore private List<AssetValuation> assetValuation = new ArrayList<>();
}
