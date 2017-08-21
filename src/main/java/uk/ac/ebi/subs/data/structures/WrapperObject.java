package uk.ac.ebi.subs.data.structures;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class WrapperObject {
    Embedded _embedded;

    public String getFirstSubmissionUrl() {
        return _embedded.getSubmissions()[0].get_links().getSelf().getHref();
    }
}

@Getter @Setter @ToString
class Embedded {
    Submission[] submissions;
}

@Getter @Setter @ToString
class Submission {
    String submitter;

    String team;

    Link _links;
}

@Getter @Setter @ToString
class Link {
    Self self;
}

@Getter @Setter @ToString
class Self {
    String href;
}
