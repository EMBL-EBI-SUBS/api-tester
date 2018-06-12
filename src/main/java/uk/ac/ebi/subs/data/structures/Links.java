package uk.ac.ebi.subs.data.structures;

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

    private Link processingStatuses;

    private Link contents;
}
