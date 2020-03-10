package com.trade.trade.models.assets;

import com.trade.trade.models.Model;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

@Data
@Entity
@NoArgsConstructor
@Inheritance
@Table(name = "assets")
public class Asset extends Model {
    @Column(unique = true) @NotEmpty private String symbol;
    @NotEmpty private String name;
    @NotEmpty private String image;
}
