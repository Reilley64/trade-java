package com.trade.trade.domain.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Data
@Entity
@NoArgsConstructor
@Table(name = "reference_data")
public class ReferenceData extends Model {
    @NotNull private String key;
    @NotNull private String value;
}
