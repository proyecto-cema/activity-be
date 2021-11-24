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
@Table(name = "ultrasound")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CemaUltrasound {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id")
    private UUID id;

    @Column(name = "service_number")
    private String serviceNumber;

    @Column(name = "result_status")
    private String resultStatus;

    @Column(name = "description")
    private String description;

    @Column(name = "name")
    private String name;

    @Column(name = "execution_date")
    private Date executionDate;

    @Column(name = "establishment_cuig")
    private String establishmentCuig;

    @Column(name = "bovine_tag")
    private String bovineTag;
}
