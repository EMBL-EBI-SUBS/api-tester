package uk.ac.ebi.subs.data.objects;

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

    private Links _links;

    public String getValidationResultsUrl() {
        return _links.getValidationResult().getHref();
    }

    private Embedded _embedded;
}

@Getter @Setter @ToString
class SampleRelationship {

    private String accession;

    private String relationshipNature;
}
