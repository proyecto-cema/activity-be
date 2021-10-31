package com.cema.activity.entities;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "ultrasound")
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

    public UUID getId() {
        return this.id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getServiceNumber() {
        return this.serviceNumber;
    }

    public void setServiceNumber(String serviceNumber) {
        this.serviceNumber = serviceNumber;
    }

    public String getResultStatus() {
        return this.resultStatus;
    }

    public void setResultStatus(String resultStatus) {
        this.resultStatus = resultStatus;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getExecutionDate() {
        return this.executionDate;
    }

    public void setExecutionDate(Date executionDate) {
        this.executionDate = executionDate;
    }

    public String getEstablishmentCuig() {
        return this.establishmentCuig;
    }

    public void setEstablishmentCuig(String establishmentCuig) {
        this.establishmentCuig = establishmentCuig;
    }

    public String getBovineTag() {
        return this.bovineTag;
    }

    public void setBovineTag(String bovineTag) {
        this.bovineTag = bovineTag;
    }
}
