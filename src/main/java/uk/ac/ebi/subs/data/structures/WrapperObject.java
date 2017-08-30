package uk.ac.ebi.subs.data.structures;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import uk.ac.ebi.subs.data.objects.Sample;
import uk.ac.ebi.subs.data.objects.Submission;

@Getter @Setter @ToString
public class WrapperObject {

    private Embedded _embedded;

    public int getSamplesLength() {
        return _embedded.getSamples().length;
    }

    public String getNthSampleAlias(int n) {
        return _embedded.getSamples()[n].getAlias();
    }
}

@Getter @Setter @ToString
class Embedded {

    private Submission[] submissions;

    private Sample[] samples;
}
