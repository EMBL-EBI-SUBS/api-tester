package uk.ac.ebi.subs.data.objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import uk.ac.ebi.subs.data.structures.Link;

@Data
public class ApiRoot {

    private ApiRootLinks _links;

    @Data
    public static class ApiRootLinks {

        @JsonProperty("tus-upload")
        private Link tusUpload;
    }

}
