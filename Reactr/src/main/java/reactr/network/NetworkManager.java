package reactr.network;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by vova on 30.08.13.
 */
public class NetworkManager {

    public String sendRequest(String url, HashMap<String, ContentBody> parameters)
    {
        HttpClient httpClient = new DefaultHttpClient();

        try{
            HttpPost postQuery = new HttpPost(url);

            MultipartEntity multipartEntity = new MultipartEntity();

            for (Map.Entry<String, ContentBody> entry : parameters.entrySet())
            {
                multipartEntity.addPart(entry.getKey(), entry.getValue());
            }

            postQuery.setEntity(multipartEntity);
            System.out.println("Executing request: " + postQuery.getRequestLine());
            HttpResponse httpResponse = httpClient.execute(postQuery);
            HttpEntity httpEntity = httpResponse.getEntity();

            System.out.println("----------------------------------------");
            System.out.println(httpResponse.getStatusLine());

            if (httpEntity != null) {
                return EntityUtils.toString(httpEntity);
            }
        } catch (IOException e) {
            Log.d("Network Manager", e.getMessage());
        }
        finally {
            try { httpClient.getConnectionManager().shutdown(); } catch (Exception ignore) {}
        }
        return "{\"error\":\"bad query\"}";
    }
}
