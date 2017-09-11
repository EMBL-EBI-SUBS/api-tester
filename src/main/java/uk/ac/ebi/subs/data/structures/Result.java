package uk.ac.ebi.subs.data.structures;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class Result {

    private String validationStatus;

    private String entityUuid;

    private String message;
}