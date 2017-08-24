package uk.ac.ebi.subs.data;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class SampleResource {
    String alias;

    TeamResource team;

    String title;

    String description;

    SampleRelationship[] sampleRelationships;
}

@Getter @Setter @ToString
class SampleRelationship {
    String accession;

    String relationshipNature;
}
