package uk.ac.ebi.subs.data.structures;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class ValidationResultStatusAndLink {

    private String validationStatus;

    @JsonProperty("_links")
    private Links links;
}
