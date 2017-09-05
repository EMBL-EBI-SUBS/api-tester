package uk.ac.ebi.subs.data.structures;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import uk.ac.ebi.subs.data.objects.Team;

@Getter @Setter @ToString
public class PutSampleResponseObject {

    private String alias;

    private Team team;

    private Embedded _embedded;
}
