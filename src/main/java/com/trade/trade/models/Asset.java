package com.trade.trade.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;

@Data
@Entity
@NoArgsConstructor
@Table(name = "assets")
public class Asset extends Model {
    @Column(unique = true) @NotEmpty private String symbol;
    @NotEmpty private String exchange;
    @NotEmpty private String companyName;
    @NotEmpty private String description;
    @NotEmpty private String industry;
    @NotEmpty private String sector;
    @NotEmpty private String website;
    @NotEmpty private String image;
}
