package uk.ac.ebi.subs.data.structures;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import uk.ac.ebi.subs.data.objects.ProcessingStatus;
import uk.ac.ebi.subs.data.objects.SubmittableTemplate;
import uk.ac.ebi.subs.data.objects.Submission;
import uk.ac.ebi.subs.data.objects.ValidationResult;

@Getter @Setter @ToString
public class Embedded {

    private Submission[] submissions;

    private SubmittableTemplate[] samples;

    private ProcessingStatus processingStatus;

    private ValidationResult validationResult;
}
