package com.cema.activity.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Activity {
    @ApiModelProperty(notes = "The id to identify this activity", example = "b000bba4-229e-4b59-8548-1c26508e459c")
    private UUID id;
    @ApiModelProperty(notes = "The name of this activity", example = "Vacunacion 2021")
    @NotEmpty(message = "Name is required")
    private String name;
    @ApiModelProperty(notes = "The type of activity", example = "Inoculation")
    @NotEmpty(message = "Type is required")
    @Pattern(regexp = "(?i)inoculation|feeding")
    private String type;
    @ApiModelProperty(notes = "The description of this activity", example = "vacuna de la vaca loca")
    private String description;
    @ApiModelProperty(notes = "The date when this activity was executed")
    private Date executionDate;
    @ApiModelProperty(notes = "The cuig of the establishment this activity belongs to.", example = "321")
    @NotEmpty(message = "Cuig is required")
    private String establishmentCuig;
}
