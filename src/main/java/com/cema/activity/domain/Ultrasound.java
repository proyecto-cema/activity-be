package com.cema.activity.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
public class Ultrasound extends Activity {

    @ApiModelProperty(notes = "Service number for category", example = "5000")
    private String serviceNumber;
    @ApiModelProperty(notes = "The result of the ultrasound", example = "pregnant")
    private String result;
    @ApiModelProperty(notes = "The tag this activity is associated to.d", example = "1234")
    @NotEmpty(message = "Bovine Tag is required")
    private String bovineTag;

    @Builder
    public Ultrasound(UUID id, @NotEmpty(message = "Name is required") String name,
                      @NotEmpty(message = "Type is required")
                      @Pattern(regexp = "(?i)inoculation|feeding|weighing") String type, String description,
                      Date executionDate, @NotEmpty(message = "Cuig is required") String establishmentCuig,
                      String serviceNumber, String result, String bovineTag) {
        super(id, name, type, description, executionDate, establishmentCuig);
        this.serviceNumber = serviceNumber;
        this.result = result;
        this.bovineTag = bovineTag;
    }
}
