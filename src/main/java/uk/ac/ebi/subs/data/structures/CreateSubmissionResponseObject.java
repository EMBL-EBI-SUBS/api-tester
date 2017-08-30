package uk.ac.ebi.subs.data.structures;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class CreateSubmissionResponseObject {
    private Submitter submitter;

    private Team team;

    private Link _links;
}

@Getter @Setter @ToString
class Submitter{
    private String email;
}

@Getter @Setter @ToString
class Team{
    private String name;
}
