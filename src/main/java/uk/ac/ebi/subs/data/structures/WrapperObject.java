package uk.ac.ebi.subs.data.structures;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class WrapperObject {

    @JsonProperty("_embedded")
    private Embedded embedded;

    public int getSamplesLength() {
        return embedded.getSamples().length;
    }

    public String getNthSampleAlias(int n) {
        return embedded.getSamples()[n].getAlias();
    }
}
