package uk.ac.ebi.subs.utils;

import org.apache.http.HttpResponse;
import uk.ac.ebi.subs.PropertiesManager;
import uk.ac.ebi.subs.data.objects.ApiRoot;

import java.io.File;

public class UploadUtils {

    private static PropertiesManager pm = PropertiesManager.getInstance();

    public static void uploadFile(String token, String submissionUrl, String testFileName) throws Exception {
        File testFile = new File(ClassLoader.getSystemClassLoader().getResource(testFileName).getFile());

        HttpResponse apiRootResponse = HttpUtils.httpGet(token, pm.getApiRoot());
        ApiRoot apiRoot = HttpUtils.retrieveResourceFromResponse(apiRootResponse,ApiRoot.class);

        String submissionUUID = submissionUrl.substring(submissionUrl.lastIndexOf('/') + 1);
        Uploader.uploadFile(token, apiRoot.getLinks().getTusUpload().getHref(), testFile, submissionUUID, testFileName);
    }
}
