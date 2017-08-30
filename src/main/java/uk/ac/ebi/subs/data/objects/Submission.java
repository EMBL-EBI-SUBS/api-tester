package uk.ac.ebi.subs.data.objects;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import uk.ac.ebi.subs.data.structures.Link;

@Getter @Setter @ToString
public class Submission {

    private Submitter submitter;

    private Team team;

    private Link _links;
}
