package uk.ac.ebi.subs.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TestJsonUtils {

    public static String getSubmissionJson(String submitterEmail, String teamName) throws IOException {
        File file = new File(ClassLoader.getSystemClassLoader().getResource("Submission.json").getFile());
        String template = new String(Files.readAllBytes(Paths.get(file.getPath())));

        String json = template.replace("{submitter.email.placeholder}", submitterEmail);
        return json.replace("{team.name.placeholder}", teamName);
    }

    public static String getCreateSampleJson(String submissionUrl, String alias) {
        return "{\n" +
                "  \"alias\" : \"" + alias + "\",\n" +
                "  \"title\" : \"NA12878_D1\",\n" +
                "  \"description\" : \"Material derived from cell line NA12878\",\n" +
                "  \"attributes\" : [ {\n" +
                "    \"name\" : \"Cell line type\",\n" +
                "    \"value\" : \"EBV-LCL cell line\",\n" +
                "    \"terms\" : [ {\n" +
                "      \"url\" : \"http://purl.obolibrary.org/obo/BTO_0003335\"\n" +
                "    } ]\n" +
                "  } ],\n" +
                "  \"sampleRelationships\" : [ {\n" +
                "    \"accession\" : \"SAME123392\",\n" +
                "    \"relationshipNature\" : \"Derived from\"\n" +
                "  } ],\n" +
                "  \"taxonId\" : 9606,\n" +
                "  \"taxon\" : \"Homo sapiens\",\n" +
                "  \"submission\" : \"" + submissionUrl + "\"\n" +
                "}";
    }

    public static String getUpdateSampleRelationshipsJson(String submissionUrl, String alias) {
        return "{\n" +
                "  \"alias\" : \"" + alias + "\",\n" +
                "  \"title\" : \"NA12878_D1\",\n" +
                "  \"description\" : \"Material derived from cell line NA12878\",\n" +
                "  \"attributes\" : [ {\n" +
                "    \"name\" : \"Cell line type\",\n" +
                "    \"value\" : \"EBV-LCL cell line\",\n" +
                "    \"terms\" : [ {\n" +
                "      \"url\" : \"http://purl.obolibrary.org/obo/BTO_0003335\"\n" +
                "    } ]\n" +
                "  } ],\n" +
                "  \"sampleRelationships\" : [ {\n" +
                "    \"accession\" : \"SAME123392\",\n" +
                "    \"relationshipNature\" : \"Derived from\"\n" +
                "  }, {\n" +
                "    \"alias\" : \"D0\",\n" +
                "    \"team\" : \"my-team\",\n" +
                "    \"relationshipNature\" : \"Child of\"\n" +
                "  } ],\n" +
                "  \"taxonId\" : 9606,\n" +
                "  \"taxon\" : \"Homo sapiens\",\n" +
                "  \"submission\" : \"" + submissionUrl + "\"\n" +
                "}";
    }

    public static String getDeleteSampleRelationshipsJson(String submissionUrl, String alias) {
        return "{\n" +
                "  \"alias\" : \"" + alias + "\",\n" +
                "  \"title\" : \"NA12878_D1\",\n" +
                "  \"description\" : \"Material derived from cell line NA12878\",\n" +
                "  \"attributes\" : [ {\n" +
                "    \"name\" : \"Cell line type\",\n" +
                "    \"value\" : \"EBV-LCL cell line\",\n" +
                "    \"terms\" : [ {\n" +
                "      \"url\" : \"http://purl.obolibrary.org/obo/BTO_0003335\"\n" +
                "    } ]\n" +
                "  } ],\n" +
                "  \"sampleRelationships\" : [],\n" +
                "  \"taxonId\" : 9606,\n" +
                "  \"taxon\" : \"Homo sapiens\",\n" +
                "  \"submission\" : \"" + submissionUrl + "\"\n" +
                "}";
    }
}
