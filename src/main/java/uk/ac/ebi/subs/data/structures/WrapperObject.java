package uk.ac.ebi.subs.data.structures;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import uk.ac.ebi.subs.data.TeamResource;

@Getter @Setter @ToString
public class WrapperObject {
    Embedded _embedded;

    public int getSamplesLength() {
        return _embedded.getSamples().length;
    }
/*
    public String getFirstSubmissionUrl() {
        return _embedded.getSubmissions()[0].get_links().getSelf().getHref();
    }
*/
    public String[] getNSubmissionsUrls(int n) {
        String[] urls = new String[n];
        for(int i = 0; i < n; i++) {
            urls[i] = _embedded.getSubmissions()[i].get_links().getSelf().getHref();
        }
        return urls;
    }

    public String getFirstSampleUrl() {
        return _embedded.getSamples()[0].get_links().getSelf().getHref();
    }

    public String getNthSampleAlias(int n) {
        return _embedded.getSamples()[n].getAlias();
    }
}

@Getter @Setter @ToString
class Embedded {
    Submission[] submissions;
    Sample[] samples;
}

@Getter @Setter @ToString
class Submission {
    String submitter;

    String team;

    Link _links;
}

@Getter @Setter @ToString
class Sample {
    TeamResource team;

    Link _links;

    String title;

    String alias;

    String processingStatus;
}
