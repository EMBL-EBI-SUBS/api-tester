package uk.ac.ebi.subs.data.structures;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class ValidationResultStatusAndLink {

    private String validationStatus;

    private Links _links;
}
