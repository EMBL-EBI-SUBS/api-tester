package uk.ac.ebi.subs.data.objects;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter @Setter @ToString
public class ApiError {

    private String type;

    private String title;

    private int status;

    private String instance;

    private List<String> errors;
}
