package how.we.adapter;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

public class ApiAdapter {

    private final static String host = "https://192.168.11.22:9001";
    private final static String username = "app15";
    private final static String password = "Dbfscs0$";


    public static JsonObject subscribeLocationStream() {
        String token = login();
        if (token == null) {
            System.out.println("get token failed");
            return null;
        }
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("X-Auth-Token", token);

        String body = "{\"appid\":\"" + username + "\"}";
        HttpURLConnection conn = HttpsAdapter.post(host + "/enabler/catalog/locationstreamreg/json/v1.0", body, headers);
        String resp = HttpsAdapter.getResponse(conn);
        System.out.println(resp);

        JsonReader reader = Json.createReader(new StringReader(resp));
        JsonObject obj = reader.readObject();
        JsonObject subscribeInfo = obj.getJsonArray("Subscribe Information").getJsonObject(0);
        reader.close();

        return subscribeInfo;
    }

    public static String login() {
        String body = "{\"auth\":{\"identity\":{\"methods\":[\"password\"],\"password\":{\"user\":{\"domain\":\"Api\",\"name\":\""
                + username + "\",\"password\":\"" + password + "\"}}}}}";
        HttpURLConnection con = HttpsAdapter.post(host + "/v3/auth/tokens", body, null);
        try {
            if (con.getResponseCode() == 201) {
                return con.getHeaderField("X-Subject-Token");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
