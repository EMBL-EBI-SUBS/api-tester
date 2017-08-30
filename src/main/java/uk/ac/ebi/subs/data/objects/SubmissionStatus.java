package uk.ac.ebi.subs.data.objects;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class SubmissionStatus {

    private String status;

    private String createdDate;

    private String lastModifiedDate;

    private String createdBy;

    private String lastModifiedBy;
}
