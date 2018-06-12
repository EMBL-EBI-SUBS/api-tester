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

    @JsonProperty("_links")
    private Links links;

    public String getStatusUpdateUrl() {
        if (links != null && links.getSelfUpdate() != null){
            return links.getSelfUpdate().getHref();
        }
        return null;
    }

    public String getSelfUrl() {
        return links.getSelf().getHref();
    }
}

@Getter @Setter @ToString
class Links {

    @JsonProperty("self:update")
    private SelfUpdate selfUpdate;

    private Self self;
}

@Getter @Setter @ToString
class SelfUpdate {

    private String href;
}

@Getter @Setter @ToString
class Self {

    private String href;
}