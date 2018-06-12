package uk.ac.ebi.subs.data.objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class FileList {

    @JsonProperty("_embedded")
    private Embedded contents;



    @Data
    public static class Embedded {
        private List<SubsFile> files = new ArrayList<>();
    }


}
