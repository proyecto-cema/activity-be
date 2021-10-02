package com.cema.activity.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper=true)
public class Weighing extends Activity {

    @ApiModelProperty(notes = "The weight measures in kilograms", example = "5000")
    @NotNull(message = "Weight is required")
    private Long weight;
    @ApiModelProperty(notes = "The category of the animal", example = "bull")
    private String category;
    @ApiModelProperty(notes = "The drug inoculated", example = "small teeth")
    private String dentalNotes;
    @ApiModelProperty(notes = "The tag this activity is associated to. Either this or batchName must be populated", example = "1234")
    @NotEmpty(message = "Bovine Tag is required")
    private String bovineTag;

    @Builder
    public Weighing(UUID id, @NotEmpty(message = "Name is required") String name,
                    @NotEmpty(message = "Type is required") @Pattern(regexp = "(?i)inoculation|feeding") String type,
                    String description, Date executionDate,
                    @NotEmpty(message = "Cuig is required") String establishmentCuig,
                    Long weight, String category, String dentalNotes, String bovineTag) {
        super(id, name, type, description, executionDate, establishmentCuig);
        this.weight = weight;
        this.category = category;
        this.dentalNotes = dentalNotes;
        this.bovineTag = bovineTag;
    }

}
