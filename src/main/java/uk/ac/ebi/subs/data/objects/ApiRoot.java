package uk.ac.ebi.subs.data.objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import uk.ac.ebi.subs.data.structures.Link;

@Data
public class ApiRoot {

    @JsonProperty("_links")
    private ApiRootLinks links;


    @Data
    public static class ApiRootLinks {

        @JsonProperty("tus-upload")
        private Link tusUpload;
    }

}
