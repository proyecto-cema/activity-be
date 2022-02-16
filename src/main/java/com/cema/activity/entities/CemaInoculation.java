package com.cema.activity.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "inoculation")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CemaInoculation {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id")
    private UUID id;

    @Column(name = "name")
    private String name;

    @Column(name = "dose")
    private Long dose;

    @Column(name = "brand")
    private String brand;

    @Column(name = "drug")
    private String drug;

    @Column(name = "product")
    private String product;

    @Column(name = "description")
    private String description;

    @Column(name = "execution_date")
    private Date executionDate;

    @Column(name = "establishment_cuig")
    private String establishmentCuig;

    @Column(name = "batch_name")
    private String batchName;

    @Column(name = "bovine_tag")
    private String bovineTag;

    @Column(name = "worker_username")
    private String workerUsername;
}
