package uk.ac.ebi.subs.utils;

public class TestJsonUtils {

    public static String getCreateSubmissionJson(String submitterEmail, String teamName) {
        return "{\n" +
                "  \"submitter\" : {\n" +
                "    \"email\" : \"" + submitterEmail + "\"\n" +
                "  },\n" +
                "  \"team\" : {\n" +
                "    \"name\" : \"" + teamName + "\"\n" +
                "  }\n" +
                "}";
    }

    public static String getCreateSampleJson(String submissionUrl) {
        return "{\n" +
                "  \"alias\" : \"D1\",\n" +
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

    public static String getUpdateSampleRelationshipsJson(String submissionUrl) {
        return "{\n" +
                "  \"alias\" : \"D1\",\n" +
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

    public static String getDeleteSampleRelationshipsJson(String submissionUrl) {
        return "{\n" +
                "  \"alias\" : \"D1\",\n" +
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
