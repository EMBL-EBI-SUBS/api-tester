package uk.ac.ebi.subs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Properties;

public class PropertiesManager {

    private static PropertiesManager propertiesManager = null;
    private Properties properties = null;

    private PropertiesManager() {
        properties = new Properties();
        String path = "../application.properties";
        File file = new File(path).getAbsoluteFile();
        try (FileInputStream fileInputStream = new FileInputStream(path)) {
            properties.load(fileInputStream);
        } catch (FileNotFoundException e) {
            if(System.getProperty("aapUsername") == null || System.getProperty("aapPassword") == null) {
                throw new PropertiesNotFoundException("ERROR: Required properties not provided.", e);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static PropertiesManager getInstance() {
        if (propertiesManager == null) {
            propertiesManager = new PropertiesManager();
        }
        return propertiesManager;
    }

    public String getSubmitterEmail() {
        return this.properties.getProperty("submitterEmail", "subs-internal@ebi.ac.uk");
    }

    public String getTeamName() {
        return this.properties.getProperty("teamName", "subs.api-tester-team-1");
    }

    public String getApiRoot() {
        return this.properties.getProperty("apiRoot", "https://submission-dev.ebi.ac.uk/api/");
    }

    public String getSubmissionsApiTemplatedUrl() {
        return this.properties.getProperty("submissionsApiBaseUrl", getApiRoot() + "teams/{teamName}/submissions/");
    }

    public String getSamplesInSubmissionByIdUrl() {
        return this.properties.getProperty("samplesInSubmissionByIdUrl", getApiRoot() + "samples/search/by-submission?submissionId=");
    }

    public String getAuthenticationUrl() {
        return this.properties.getProperty("authenticationUrl", "https://explore.api.aai.ebi.ac.uk/auth");
    }

    public String getAapUsername() {
        return this.properties.getProperty("aapUsername", System.getProperty("aapUsername"));
    }

    public String getAapPassword() {
        return this.properties.getProperty("aapPassword", System.getProperty("aapPassword"));
    }
}

class PropertiesNotFoundException extends RuntimeException {

    public PropertiesNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
