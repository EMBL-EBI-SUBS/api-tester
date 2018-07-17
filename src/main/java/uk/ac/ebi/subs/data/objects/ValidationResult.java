package uk.ac.ebi.subs.data.objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import uk.ac.ebi.subs.data.structures.Links;
import uk.ac.ebi.subs.data.structures.Result;

import java.util.List;
import java.util.Map;

@Getter @Setter @ToString
public class ValidationResult {

    private String validationStatus;

    private String entityUuid;

    private int version;

    private String submissionId;

    private Map<String, List<String>> errorMessages;

    private Map<String, String> overallValidationOutcomeByAuthor;

    @JsonProperty("_links")
    private Links links;

    private Map<String, Result[]> expectedResults;

    public Result[] getValidationResultsFromEna() {
        return expectedResults.get("Ena");
    }

    public Result[] getValidationResultsFromCore() {
        return expectedResults.get("Core");
    }

    public Result[] getValidationResultsFromTaxonomy() {
        return expectedResults.get("Taxonomy");
    }

    public Result[] getValidationResultsFromBiosamples() {
        return expectedResults.get("Biosamples");
    }
}
