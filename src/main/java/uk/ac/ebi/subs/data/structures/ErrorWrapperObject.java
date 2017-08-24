package uk.ac.ebi.subs.data.structures;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class ErrorWrapperObject {
    Error[] errors;

    public String getFirstErrorMessage() {
        return errors[0].getMessage();
    }
}

@Getter @Setter @ToString
class Error {
    String entity;

    String property;

    InvalidValue invalidValue;

    String message;
}

@Getter @Setter @ToString
class InvalidValue {
    String email;
}


/*
{
    "errors": [
        {
            "entity": "Submission",
            "property": "submitter",
            "invalidValue": {
                "email": "test@email"
            },
            "message": "resource_locked"
        }
    ]
}
*/
