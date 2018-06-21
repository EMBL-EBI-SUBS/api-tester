package uk.ac.ebi.subs.data.structures;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Links {

    private Link self;

    private Link validationResult;

    private Link submissionStatus;

    @JsonProperty("submissionStatus:update")
    private Link submissionStatusUpdate;

    private Link processingStatuses;

    private Link contents;
}
