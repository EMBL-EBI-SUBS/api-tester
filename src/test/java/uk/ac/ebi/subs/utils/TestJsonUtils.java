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

    public static String getCreateSampleJson(String alias) throws IOException {
        File file = new File(ClassLoader.getSystemClassLoader().getResource("CreateSample.json").getFile());
        String template = new String(Files.readAllBytes(Paths.get(file.getPath())));

        String json = template.replace(ALIAS, alias);
        return json.replace(RELEASE_DATE, LocalDate.now().toString());
    }

    public static String getUpdateSampleJson(String alias) throws IOException {
        File file = new File(ClassLoader.getSystemClassLoader().getResource("UpdateSample.json").getFile());
        String template = new String(Files.readAllBytes(Paths.get(file.getPath())));

        String json = template.replace(ALIAS, alias);
        return json.replace(RELEASE_DATE, LocalDate.now().toString());
    }

    public static String getDeleteSampleRelationshipsJson(String alias) throws IOException {
        File file = new File(ClassLoader.getSystemClassLoader().getResource("DeleteSampleRelationships.json").getFile());
        String template = new String(Files.readAllBytes(Paths.get(file.getPath())));

        String json = template.replace(ALIAS, alias);
        return json.replace(RELEASE_DATE, LocalDate.now().toString());
    }

    public static String getSampleJson(String alias) throws IOException {
        File file = new File(ClassLoader.getSystemClassLoader().getResource("Sample.json").getFile());
        String template = new String(Files.readAllBytes(Paths.get(file.getPath())));

        String json = template.replace(ALIAS, alias);
        return json.replace(RELEASE_DATE, LocalDate.now().toString());
    }

    public static String getSeqVarAnalysisJson(

            String alias,
            String studyAlias,
            String sampleAlias,
            String fileName,
            String fileType
    ) throws IOException {
        File file = new File(ClassLoader.getSystemClassLoader().getResource("SeqVarAnalysis.json").getFile());
        String template = new String(Files.readAllBytes(Paths.get(file.getPath())));

        String json = template.replace(ALIAS, alias);
        json = json.replace(STUDY_ALIAS, studyAlias);
        json = json.replace(SAMPLE_ALIAS, sampleAlias);
        json = json.replace(FILE_NAME, fileName);
        json = json.replace(FILE_TYPE, fileType);

        return json;
    }

    public static String getStudyJson(String alias, String projectAlias, String teamName) throws IOException {
        File file = new File(ClassLoader.getSystemClassLoader().getResource("Study.json").getFile());
        String template = new String(Files.readAllBytes(Paths.get(file.getPath())));

        String json = template.replace(ALIAS, alias);
        json = json.replace(PROJECT_ALIAS, projectAlias);
        json = json.replace(TEAM_NAME, teamName);
        return json;
    }

    public static String getProjectJson(String alias, String releaseDate) throws IOException {
        File file = new File(ClassLoader.getSystemClassLoader().getResource("Project.json").getFile());
        String template = new String(Files.readAllBytes(Paths.get(file.getPath())));

        String json = template.replace(ALIAS, alias);
        return json.replace(RELEASE_DATE, releaseDate);
    }

    public static String getAssayJson(String alias, String sampleAlias, String studyAlias) throws IOException {
        File file = new File(ClassLoader.getSystemClassLoader().getResource("Assay.json").getFile());
        String template = new String(Files.readAllBytes(Paths.get(file.getPath())));

        String json = template.replace(ALIAS, alias);

        json = json.replace(SAMPLE_ALIAS, sampleAlias);
        json = json.replace(STUDY_ALIAS, studyAlias);
        return json;
    }

    public static String getAssayDataJson(String alias, String assayAlias, String fileName, String filetype) throws IOException {
        File file = new File(ClassLoader.getSystemClassLoader().getResource("AssayData.json").getFile());
        String template = new String(Files.readAllBytes(Paths.get(file.getPath())));

        String json = template.replace(ALIAS, alias);
        json = json.replace(ASSAY_ALIAS, assayAlias);
        json = json.replace(FILE_NAME, fileName);
        json = json.replace(FILE_TYPE, filetype);

        return json;
    }

    public static String createSampleForSubmissionJson(String alias) throws IOException {
        File file = new File(ClassLoader.getSystemClassLoader().getResource("SampleForSubmission.json").getFile());
        String template = new String(Files.readAllBytes(Paths.get(file.getPath())));

        String json = template.replace(ALIAS, alias);
        return json.replace(RELEASE_DATE, LocalDate.now().toString());
    }
}
