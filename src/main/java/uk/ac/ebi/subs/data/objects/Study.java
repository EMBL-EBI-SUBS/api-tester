package uk.ac.ebi.subs.data.objects;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class Study {
    private String accno;
    private BioStudyLink[] links;
}

