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
@Table(name = "weighing")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CemaWeighing {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id")
    private UUID id;

    @Column(name = "category")
    private String category;

    @Column(name = "weight")
    private Long weight;

    @Column(name = "dental_notes")
    private String dentalNotes;

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
