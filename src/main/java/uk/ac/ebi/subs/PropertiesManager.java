package uk.ac.ebi.subs;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class PropertiesManager {

    private static PropertiesManager propertiesManager = null;
    private Properties properties = null;

    private PropertiesManager() {
        properties = new Properties();
        String path = "./application.properties";
        try (FileInputStream fileInputStream = new FileInputStream(path)) {
            properties.load(fileInputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("WARNING: Using default properties.");
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
        return this.properties.getProperty("submitterEmail", "api-tester@ebi.ac.uk");
    }

    public String getTeamName() {
        return this.properties.getProperty("teamName", "api-tester");
    }

    public String getSubmissionsApiBaseUrl() {
        return this.properties.getProperty("submissionsApiBaseUrl", "http://submission-dev.ebi.ac.uk/api/submissions/");
    }

    public String getSamplesApiBaseUrl() {
        return this.properties.getProperty("samplesApiBaseUrl", "http://submission-dev.ebi.ac.uk/api/samples/");
    }

}
