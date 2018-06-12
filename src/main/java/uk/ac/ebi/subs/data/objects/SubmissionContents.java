package uk.ac.ebi.subs.data.objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import uk.ac.ebi.subs.data.structures.Link;

@Data
public class SubmissionContents {

    @JsonProperty("_links")
    private Links links;

    @Data
    public static class Links {
        private Link assayData;
        private Link assays;
        private Link samples;
        private Link studies;
        private Link files;
        private Link project;
        private Link samplesSheets;

    }

}
