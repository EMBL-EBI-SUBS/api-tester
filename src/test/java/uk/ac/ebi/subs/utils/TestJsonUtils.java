package uk.ac.ebi.subs.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;

public class TestJsonUtils {

    private static final String SUBMITTER_EMAIL = "{submitter.email.placeholder}";
    private static final String TEAM_NAME = "{team.name.placeholder}";

    private static final String ALIAS = "{alias.placeholder}";
    private static final String SUBMISSION_URL = "{submissionUrl.placeholder}";
    private static final String PROJECT_ALIAS = "{projectAlias.placeholder}";
    private static final String RELEASE_DATE = "{release.date.placeholder}";

    private static final String STUDY_ALIAS = "{studyAlias.placeholder}";
    private static final String SAMPLE_ALIAS = "{sampleAlias.placeholder}";
    private static final String ASSAY_ALIAS = "{assayAlias.placeholder}";
    private static final String FILE_NAME = "{fileName.placeholder}";
    private static final String FILE_TYPE = "{fileType.placeholder}";


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
        json = json.replace(SUBMISSION_URL, submissionUrl);
        return json.replace(RELEASE_DATE, LocalDate.now().toString());
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
        return json.replace(RELEASE_DATE, LocalDate.now().toString());
    }

    public static String getStudyJson(String submissionUrl, String alias, String projectAlias, String teamName) throws IOException {
        File file = new File(ClassLoader.getSystemClassLoader().getResource("Study.json").getFile());
        String template = new String(Files.readAllBytes(Paths.get(file.getPath())));

        String json = template.replace(ALIAS, alias);
        json = json.replace(SUBMISSION_URL, submissionUrl);
        json = json.replace(PROJECT_ALIAS, projectAlias);
        json = json.replace(TEAM_NAME, teamName);
        return json;
    }

    public static String getProjectJson(String submissionUrl, String alias, String releaseDate) throws IOException {
        File file = new File(ClassLoader.getSystemClassLoader().getResource("Project.json").getFile());
        String template = new String(Files.readAllBytes(Paths.get(file.getPath())));

        String json = template.replace(ALIAS, alias);
        json = json.replace(SUBMISSION_URL, submissionUrl);
        return json.replace(RELEASE_DATE, releaseDate);
    }

    public static String getAssayJson(String submissionUrl, String alias, String sampleAlias, String studyAlias) throws IOException {
        File file = new File(ClassLoader.getSystemClassLoader().getResource("Assay.json").getFile());
        String template = new String(Files.readAllBytes(Paths.get(file.getPath())));

        String json = template.replace(ALIAS, alias);
        json = json.replace(SUBMISSION_URL, submissionUrl);

        json = json.replace(SAMPLE_ALIAS, sampleAlias);
        json = json.replace(STUDY_ALIAS, studyAlias);
        return json;
    }

    public static String getAssayDataJson(String submissionUrl, String alias, String assayAlias, String fileName, String filetype) throws IOException {
        File file = new File(ClassLoader.getSystemClassLoader().getResource("AssayData.json").getFile());
        String template = new String(Files.readAllBytes(Paths.get(file.getPath())));

        String json = template.replace(ALIAS, alias);
        json = json.replace(SUBMISSION_URL, submissionUrl);
        json = json.replace(ASSAY_ALIAS, assayAlias);
        json = json.replace(FILE_NAME, fileName);
        json = json.replace(FILE_TYPE,filetype);

        return json;
    }

    public static String createSampleForSubmissionJson(String submissionUrl, String alias) throws IOException {
        File file = new File(ClassLoader.getSystemClassLoader().getResource("SampleForSubmission.json").getFile());
        String template = new String(Files.readAllBytes(Paths.get(file.getPath())));

        String json = template.replace(ALIAS, alias);
        json = json.replace(SUBMISSION_URL, submissionUrl);
        return json.replace(RELEASE_DATE, LocalDate.now().toString());
    }
}
