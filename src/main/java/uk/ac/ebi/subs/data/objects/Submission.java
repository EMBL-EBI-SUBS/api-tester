package uk.ac.ebi.subs.data.objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import uk.ac.ebi.subs.data.structures.Links;

@Getter @Setter @ToString
public class Submission {

    private String id;

    private Submitter submitter;

    private Team team;

    @JsonProperty("_links")
    private Links links;

    private String createdBy;

}
