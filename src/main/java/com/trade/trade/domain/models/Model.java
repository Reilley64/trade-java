package com.trade.trade.domain.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Data;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@TypeDefs({
        @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class),
})
@Data
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class Model {
    @JsonIgnore @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(unique = true) private UUID uuid = UUID.randomUUID();
    @CreatedDate private Date createdAt;
    @LastModifiedDate private Date updatedAt;
}
