package uk.ac.ebi.subs.data.objects;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class ProcessingStatus {

    private String submittableType;

    private String alias;

    private String status;

    private String archive;

    private String accession;
}
