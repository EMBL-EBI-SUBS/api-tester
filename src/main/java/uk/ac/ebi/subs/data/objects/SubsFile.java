package uk.ac.ebi.subs.data.objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SubsFile {

    private String generatedTusId;
    private String filename;
    private String uploadPath;
    private String targetPath;
    private String status;
    private String checksum;

    private Long totalSize;
    private Long uploadedSize;

    @JsonProperty("_embedded")
    private Embedded embedded;

    @Data
    public static class Embedded {
        private ValidationResult validationResult;
    }

}
