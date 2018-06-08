package uk.ac.ebi.subs.data.objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import uk.ac.ebi.subs.data.structures.Link;

import java.util.ArrayList;
import java.util.List;

@Data
public class ProcessingStatuses {

    @JsonProperty("_embedded")
    private Content content;

    @JsonProperty("_links")
    private Links links;

    @Data
    public static class Content {
        private List<ProcessingStatus> processingStatuses = new ArrayList<>();
    }

    @Data
    public static class Links {
        private Link next;
    }
}
