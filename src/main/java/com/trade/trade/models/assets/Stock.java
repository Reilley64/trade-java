package com.trade.trade.models.assets;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.validation.constraints.NotEmpty;

@Data
@Entity
@NoArgsConstructor
public class Stock extends Asset {
    @NotEmpty private String exchange;
    @NotEmpty private String description;
    @NotEmpty private String industry;
    @NotEmpty private String sector;
    @NotEmpty private String website;
}
