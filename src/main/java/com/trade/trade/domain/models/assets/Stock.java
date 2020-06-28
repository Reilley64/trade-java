package com.trade.trade.domain.models.assets;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotEmpty;

@Data
@Entity
@NoArgsConstructor
public class Stock extends Asset {
    @NotEmpty private String exchange;
    @Column(columnDefinition = "text") @NotEmpty private String description;
    @NotEmpty private String industry;
    @NotEmpty private String sector;
    @NotEmpty private String website;
}
