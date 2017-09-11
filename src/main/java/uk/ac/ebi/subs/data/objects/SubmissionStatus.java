package uk.ac.ebi.subs.data.objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class SubmissionStatus {

    private String status;

    private String createdDate;

    private String lastModifiedDate;

    private String createdBy;

    private String lastModifiedBy;

    private _Links _links;

    public String getStatusUpdateUrl() {
        return _links.getSelf_update().getHref();
    }
}

@Getter @Setter @ToString
class _Links {

    @JsonProperty("self:update")
    private Self_Update self_update;
}

@Getter @Setter @ToString
class Self_Update {

    private String href;
}
