package uk.ac.ebi.subs.data.structures;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

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
