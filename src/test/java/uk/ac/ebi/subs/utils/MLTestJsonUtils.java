package uk.ac.ebi.subs.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class MLTestJsonUtils {

    private static final String TEAM_NAME = "{team.name.placeholder}";
    private static final String ALIAS = "{alias.placeholder}";
    private static final String PROJECT_ALIAS = "{projectAlias.placeholder}";
    private static final String PROTOCOL_ALIAS = "{protocolAlias.placeholder}";

    public static String getMLStudyJson(String alias, String projectAlias, Map<String, String> metabolightsProtocolsRefs, String teamName) throws IOException {
        String template = loadMLStudyJson();
        String json = template.replace(ALIAS, alias);
        json = json.replace(PROJECT_ALIAS, projectAlias);
        json = json.replace(TEAM_NAME, teamName);
        for (Map.Entry<String,String> entry : metabolightsProtocolsRefs.entrySet()) {
            json = json.replace(entry.getKey(), entry.getValue());
        }
        return json;
    }

    public static String loadMLStudyJson() throws IOException {
        File file = new File(ClassLoader.getSystemClassLoader().getResource("MLTestData/MLStudyLCMS.json").getFile());
        return new String(Files.readAllBytes(Paths.get(file.getPath())));
    }

    public static String getSampleCollectionProtocol(String alias, String teamName) throws IOException {
        return loadMLProtocolJson("sampleCollection.json", alias, teamName);
    }

    public static String getdataTransformationProtocol(String alias, String teamName) throws IOException {
        return loadMLProtocolJson("dataTransformation.json", alias, teamName);
    }

    public static String getExtractionProtocol(String alias, String teamName) throws IOException {
        return loadMLProtocolJson("extraction.json", alias, teamName);
    }

    public static String getMassSpectrometryProtocol(String alias, String teamName) throws IOException {
        return loadMLProtocolJson("massSpectrometry.json", alias, teamName);
    }

    public static String getMetaboliteIdentificationProtocol(String alias, String teamName) throws IOException {
        return loadMLProtocolJson("metaboliteIdentification.json", alias, teamName);
    }

    public static String getChromatographyProtocol(String alias, String teamName) throws IOException {
        return loadMLProtocolJson("chromatography.json", alias, teamName);
    }

    public static String loadMLProtocolJson(String jsonName, String alias, String teamName) throws IOException {
        File file = new File(ClassLoader.getSystemClassLoader().getResource("MLTestData/MLProtocols/" + jsonName).getFile());
        String template = new String(Files.readAllBytes(Paths.get(file.getPath())));
        String json = template.replace(ALIAS, alias);
        json = json.replace(TEAM_NAME, teamName);
        return json;
    }

    public static Map<String, String> getLcmsProtocolsAliaskeys() {
        Map map = new HashMap();
        map.put("protocolAlias1.placeholder", null);
        map.put("protocolAlias2.placeholder", null);
        map.put("protocolAlias3.placeholder", null);
        map.put("protocolAlias4.placeholder", null);
        map.put("protocolAlias5.placeholder", null);
        map.put("protocolAlias6.placeholder", null);
        return map;
    }
}
