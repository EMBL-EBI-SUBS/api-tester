package uk.ac.ebi.subs.data.structures;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import uk.ac.ebi.subs.data.objects.ProcessingStatus;
import uk.ac.ebi.subs.data.objects.Sample;
import uk.ac.ebi.subs.data.objects.Submission;

@Getter @Setter @ToString
public class Embedded {

    private Submission[] submissions;

    private Sample[] samples;

    private ProcessingStatus processingStatus;
}
