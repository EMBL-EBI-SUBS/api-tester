package uk.ac.ebi.subs.data;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class SubmissionStatusResource {

    String status;

    String createdDate;

    String lastModifiedDate;

    String createdBy;

    String lastModifiedBy;

}
