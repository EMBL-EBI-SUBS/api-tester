package uk.ac.ebi.subs.data.structures;

import lombok.Data;

@Data
public class ValidationResultStatusAndLink {

    private String validationStatus;

    private Link _links;
}
