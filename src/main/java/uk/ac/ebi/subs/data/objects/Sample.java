package uk.ac.ebi.subs.data.objects;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class Sample {
    private String name;
    private String accession;
    private ExternalReference[] externalReferences;
}

