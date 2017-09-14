package uk.ac.ebi.subs.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;

public class TestJsonUtils {

    private static final String SUBMITTER_EMAIL = "{submitter.email.placeholder}";
    private static final String TEAM_NAME = "{team.name.placeholder}";

    private static final String ALIAS = "{alias.placeholder}";
    private static final String SUBMISSION_URL = "{submissionUrl.placeholder}";

    private static final String RELEASE_DATE = "{release.date.placeholder}";

    public static String getSubmissionJson(String submitterEmail, String teamName) throws IOException {
        File file = new File(ClassLoader.getSystemClassLoader().getResource("Submission.json").getFile());
        String template = new String(Files.readAllBytes(Paths.get(file.getPath())));

        String json = template.replace(SUBMITTER_EMAIL, submitterEmail);
        return json.replace(TEAM_NAME, teamName);
    }

    public static String getCreateSampleJson(String submissionUrl, String alias) throws IOException {
        File file = new File(ClassLoader.getSystemClassLoader().getResource("CreateSample.json").getFile());
        String template = new String(Files.readAllBytes(Paths.get(file.getPath())));

        String json = template.replace(ALIAS, alias);
        return json.replace(SUBMISSION_URL, submissionUrl);
    }

    public static String getUpdateSampleJson(String submissionUrl, String alias) throws IOException {
        File file = new File(ClassLoader.getSystemClassLoader().getResource("UpdateSample.json").getFile());
        String template = new String(Files.readAllBytes(Paths.get(file.getPath())));

        String json = template.replace(ALIAS, alias);
        return json.replace(SUBMISSION_URL, submissionUrl);
    }

    public static String getDeleteSampleRelationshipsJson(String submissionUrl, String alias) throws IOException {
        File file = new File(ClassLoader.getSystemClassLoader().getResource("DeleteSampleRelationships.json").getFile());
        String template = new String(Files.readAllBytes(Paths.get(file.getPath())));

        String json = template.replace(ALIAS, alias);
        return json.replace(SUBMISSION_URL, submissionUrl);
    }

    public static String getSampleJson(String submissionUrl, String alias) throws IOException {
        File file = new File(ClassLoader.getSystemClassLoader().getResource("Sample.json").getFile());
        String template = new String(Files.readAllBytes(Paths.get(file.getPath())));

        String json = template.replace(ALIAS, alias);
        json = json.replace(SUBMISSION_URL, submissionUrl);
        return json.replace(RELEASE_DATE, LocalDateTime.now().toString());
    }

    public static String getStudyJson(String submissionUrl, String alias, String releaseDate) throws IOException {
        File file = new File(ClassLoader.getSystemClassLoader().getResource("Study.json").getFile());
        String template = new String(Files.readAllBytes(Paths.get(file.getPath())));

        String json = template.replace(ALIAS, alias);
        json = json.replace(SUBMISSION_URL, submissionUrl);
        return json.replace(RELEASE_DATE, releaseDate);
    }
}
