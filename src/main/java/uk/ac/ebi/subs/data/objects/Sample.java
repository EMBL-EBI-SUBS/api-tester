package uk.ac.ebi.subs.data.objects;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import uk.ac.ebi.subs.data.structures.Link;

@Getter @Setter @ToString
public class Sample {

    private String alias;

    private Team team;

    private String title;

    private String description;

    private SampleRelationship[] sampleRelationships;

    private Link _links;
}

@Getter @Setter @ToString
class SampleRelationship {

    private String accession;

    private String relationshipNature;
}
