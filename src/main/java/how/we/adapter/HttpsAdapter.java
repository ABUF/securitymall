package how.we.adapter;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

public class HttpsAdapter implements /*TrustManager,*/ X509TrustManager, HostnameVerifier {

    /*
    public boolean isServerTrusted(java.security.cert.X509Certificate[] certs) {
        return true;
    }

    public boolean isClientTrusted(java.security.cert.X509Certificate[] certs) {
        return true;
    }
    */

    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
        return null;
    }

    public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType)
            throws java.security.cert.CertificateException {
        return;
    }

    public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType)
            throws java.security.cert.CertificateException {
        return;
    }

    @Override
    public boolean verify(String urlHostName, SSLSession session) { //允许所有主机
        return true;
    }

    public static String getResponse(HttpURLConnection con) {
        assert(con != null);
        StringBuffer sb = new StringBuffer("");
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String lines;
            while ((lines = reader.readLine()) != null) {
                lines = new String(lines.getBytes(), "utf-8");
                sb.append(lines);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return sb.toString();
    }

    public static HttpURLConnection connect(String path) {
        HttpURLConnection con = null;

        TrustManager[] trustAllCerts = new TrustManager[1];
        TrustManager tm = new HttpsAdapter();
        trustAllCerts[0] = tm;

        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, null);
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier((HostnameVerifier) tm);

            URL url = new URL(path);
            con = (HttpURLConnection) url.openConnection();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        return con;
    }

    public static HttpURLConnection post(String url, String body, Map<String, String> headers) {
        assert (url != null);
        assert (body != null);

        HttpURLConnection con = connect(url);
        con.setDoOutput(true);
        if (headers != null) {
            for (String key : headers.keySet()) {
                con.setRequestProperty(key, headers.get(key));
            }
        }

        try {
            con.setRequestMethod("POST");
            OutputStream out = con.getOutputStream();
            out.write(body.getBytes());
            out.close();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return con;
    }
}
