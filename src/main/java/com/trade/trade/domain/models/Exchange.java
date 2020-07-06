package com.trade.trade.domain.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;

@Data
@Entity
@NoArgsConstructor
@Table(name = "exchanges")
public class Exchange extends Model {
    @Column(unique = true) @NotEmpty private String reference;
    @NotEmpty private String name;
    @NotEmpty private String region;
    private Boolean active = false;
}
