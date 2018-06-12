package uk.ac.ebi.subs.data.objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import uk.ac.ebi.subs.data.structures.Embedded;
import uk.ac.ebi.subs.data.structures.Links;

@Getter @Setter @ToString
public class SubmittableTemplate {

    private String accession;

    private String alias;

    private Team team;

    private String title;

    private String description;

    private SampleRelationship[] sampleRelationships;

    @JsonProperty("_links")
    private Links links;

    public String getValidationResultsUrl() {
        return links.getValidationResult().getHref();
    }

    @JsonProperty("_embedded")
    private Embedded embedded;
}

@Getter @Setter @ToString
class SampleRelationship {

    private String accession;

    private String relationshipNature;
}
