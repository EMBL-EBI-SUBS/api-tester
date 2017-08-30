package uk.ac.ebi.subs.data.structures;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class ErrorWrapperObject {

    private Error[] errors;

    public String getFirstErrorMessage() {
        return errors[0].getMessage();
    }
}

@Getter @Setter @ToString
class Error {

    private String entity;

    private String property;

    private InvalidValue invalidValue;

    private String message;
}

@Getter @Setter @ToString
class InvalidValue {

    private String email;
}
