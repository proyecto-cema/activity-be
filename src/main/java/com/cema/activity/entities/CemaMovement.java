package com.cema.activity.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "movement")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CemaMovement {
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

    @OneToOne()
    @JoinColumn(name = "location_id", referencedColumnName = "id")
    private CemaLocation location;
}
