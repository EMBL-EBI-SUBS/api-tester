package uk.ac.ebi.subs.data.structures;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import uk.ac.ebi.subs.data.objects.Team;

@Getter @Setter @ToString
public class PutSampleResponseObject {

    private String alias;

    private Team team;

    @JsonProperty("_embedded")
    private Embedded embedded;
}
