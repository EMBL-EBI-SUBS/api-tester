package uk.ac.ebi.subs.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class HttpUtils {

    public static HttpResponse httpPost(String token, String url, String content) throws IOException {
        HttpPost request = new HttpPost(url);
        request.setHeaders(getContentTypeAcceptAndTokenHeaders(token));

        StringEntity payload = new StringEntity(content);
        request.setEntity(payload);

        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        return response;
    }

    public static HttpResponse httpPatch(String token, String url, String content) throws IOException {
        HttpPatch request = new HttpPatch(url);
        request.setHeaders(getContentTypeAcceptAndTokenHeaders(token));

        StringEntity payload = new StringEntity(content);
        request.setEntity(payload);

        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        return response;
    }

    public static HttpResponse httpPut(String token, String url, String content) throws IOException {
        HttpPut request = new HttpPut(url);
        request.setHeaders(getContentTypeAcceptAndTokenHeaders(token));

        StringEntity payload = new StringEntity(content);
        request.setEntity(payload);

        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        return response;
    }

    public static Header[] getContentTypeAcceptAndTokenHeaders(String token) {
        Header contentTypeHeader = new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/hal+json");
        Header acceptHeader = new BasicHeader(HttpHeaders.ACCEPT, "application/hal+json");
        Header tokenHeader = new BasicHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        Header[] headers = {contentTypeHeader, acceptHeader, tokenHeader};
        return headers;
    }

    public static Header[] getContentTypeAndTokenHeaders(String token) {
        Header contentTypeHeader = new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/hal+json");
        Header tokenHeader = new BasicHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        Header[] headers = {contentTypeHeader, tokenHeader};
        return headers;
    }

    public static void deleteResource(String token, String url) throws IOException {
        HttpDelete request = new HttpDelete(url);
        request.setHeaders(getContentTypeAcceptAndTokenHeaders(token));
        HttpClientBuilder.create().build().execute(request);
    }

    public static HttpResponse httpGet(String token, String url) throws IOException{
        HttpUriRequest request = new HttpGet(url);
        request.setHeaders(getContentTypeAcceptAndTokenHeaders(token));

        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        return response;
    }

    public static String extractResponseBody(HttpResponse response) {
        String body = null;
        try {
            body = IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8.name());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return body;
    }

    public static <T> T retrieveResourceFromResponse(HttpResponse response, Class<T> clazz) throws IOException {

        String jsonFromResponse = EntityUtils.toString(response.getEntity());
        ObjectMapper mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        T object = mapper.readValue(jsonFromResponse, clazz);
        return object;
    }
}
