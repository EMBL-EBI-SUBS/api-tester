package uk.ac.ebi.subs.data.structures;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class CreateSampleResponseObject {
    private String alias;

    private Team team;

    private Link _links;
}
