package jp.hkawasaki.util.net;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.Override;import java.lang.String;import java.lang.StringBuffer;import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

/**
 * Created by 22110115 on 2015/09/10.
 */
public class HttpPutClient extends HttpBaseClient {
    private static final String TAG = HttpPutClient.class.getSimpleName();
    private final static int BUF_SIZE = 4096;
    private String resBody;

    public HttpPutClient(URL url) throws IOException {
        super(url);
    }

    @Override
    protected String getRequestMethod() {
        return "PUT";
    }

    @Override
    protected boolean getOutputFlag() {
        return true;
    }

    /*
     * Put json data with string.
     */
    public String execute(JSONObject json) throws IOException {
        resBody = null;

        if (json != null) {
            OutputStream os = con.getOutputStream();
            BufferedOutputStream bos = new BufferedOutputStream(os);

            bos.write(json.toString().getBytes());
            bos.flush();
            bos.close();
        } else {
            con.connect();
        }

        //wait here until receiving the result.
        return getStringResBody();
    }

    public String getStringResBody() {
        if (resBody != null)
            return resBody;

        byte[] data = new byte[BUF_SIZE];
        String res = "";
        InputStream is = null;
        int rsize = 0;
        StringBuffer sb = new StringBuffer();

        try {
            if (getResponseCode() < HttpURLConnection.HTTP_BAD_REQUEST)
                is = con.getInputStream();
            else
                is = con.getErrorStream();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        while (true) {
            try {
                rsize = is.read(data);
                if (rsize < 0)
                    break;
                sb.append(new String(data, 0, rsize));

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }


        return resBody = sb.toString();
    }
}
