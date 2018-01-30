package com.rc.abovesound.util;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * This class encapsulates methods for requesting a server via HTTP GET/POST and
 * provides methods for parsing response from the server.
 *
 * @author Md. Rashsadul Alam
 */
public class HttpRequestManager {

    public static HttpResponse doGetRequest(String requestURL) {

        HttpResponse response = null;
        HttpURLConnection httpConn = null;

        try {

            URL url = new URL(requestURL);
            httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setUseCaches(false);
            httpConn.setReadTimeout(15000);
            httpConn.setConnectTimeout(15000);
            httpConn.setRequestProperty("Content-type", "application/json");
            httpConn.setRequestProperty("Accept", "application/json");
            httpConn.setDoInput(true); // true if we want to read server's
            // response
            httpConn.setDoOutput(false); // false indicates this is a GET
            // request

            response = readStream(httpConn);

            return response;

        } catch (Exception e) {
            return new HttpResponse(e);
        } finally {
            disconnectHttpURLConnection(httpConn);
        }
    }

    public static HttpResponse doRestPostRequest(String URL, JSONObject param, ArrayList<HttpParameter> header) {

        URL url = null;
        HttpURLConnection urlConn = null;
        HttpResponse response = null;

        try {

            url = new URL(URL);
            urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setReadTimeout(15000);
            urlConn.setConnectTimeout(15000);
            urlConn.setDoInput(true);
            urlConn.setDoOutput(true);
            urlConn.setUseCaches(false);
            setHeader(urlConn, header);
            urlConn.connect();

            if (writeStream(urlConn, param)) {
                response = readStream(urlConn);
                Log.d("Post response:", response.getResult().toString());
            }

            return response;

        } catch (Exception e) {
            return new HttpResponse(e);
        } finally {
            disconnectHttpURLConnection(urlConn);
        }
    }

    private static void setHeader(HttpURLConnection urlConnection, ArrayList<HttpParameter> header) {
        if (urlConnection != null) {
            if (header != null && header.size() > 0) {
                for (int i = 0; i < header.size(); i++) {
                    HttpParameter mHeader = header.get(i);
                    urlConnection.setRequestProperty(mHeader.getKey().toString(), mHeader.getValue().toString());
                }
            } else {
                urlConnection.setRequestProperty("Content-type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");
            }
        }
    }

    public static boolean writeStream(HttpURLConnection httpURLConnection, JSONObject param) {
        if (httpURLConnection != null && param != null) {
            DataOutputStream printout = null;
            try {
                String stringParam = param.toString();
                Log.d("Parameter data:", stringParam);
                byte[] data = stringParam.getBytes("UTF-8");
                printout = new DataOutputStream(httpURLConnection.getOutputStream());
                printout.write(data);
                return true;
            } catch (Exception e) {
                return false;
            } finally {
                try {
                    if (printout != null) {
                        printout.flush();
                        printout.close();
                    }
                } catch (Exception e) {
                }
            }
        }
        return false;
    }

    public static void disconnectHttpURLConnection(HttpURLConnection httpConn) {
        if (httpConn != null) {
            httpConn.disconnect();
        }
    }

    public static HttpResponse readStream(HttpURLConnection httpURLConnection) {

        InputStream inputStream = null;
        String line = null;
        String result = null;
        BufferedReader reader = null;

        try {

            if (httpURLConnection != null && httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = new BufferedInputStream(httpURLConnection.getInputStream());
                reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder sb = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                result = sb.toString();
                return new HttpResponse(result);
            } else {
                return new HttpResponse(new Exception(httpURLConnection.getResponseMessage()));
            }

        } catch (Exception e) {
            return new HttpResponse(e);
        } finally {
            try {
                reader.close();
                inputStream.close();
            } catch (Exception e) {
            }
        }
    }

    public static class HttpResponse<T> {

        private Long[] mData = null;
        private Exception mError = null;
        private T mValue = null;
        private boolean isSuccess = false;

        public HttpResponse(Long... data) {
            mData = data;
            isSuccess = true;
        }

        public HttpResponse(T value) {
            mValue = value;
            isSuccess = true;
        }

        public HttpResponse(Exception error) {
            mError = error;
            isSuccess = false;
        }

        public Exception getError() {
            return mError;
        }

        public T getResult() {
            return mValue;
        }

        public boolean isSuccess() {
            return isSuccess;
        }

        public Long[] getData() {
            return mData;
        }
    }

    public static class HttpParameter<T> {

        private T mKey = null;
        private T mValue = null;
        private JSONObject mJSONParam = null;
        private ArrayList<HttpParameter> mArryListParam = null;
        private ArrayList<HttpParameter> mHeader = null;

        private HttpParameter(T key, T value) {
            mKey = key;
            mValue = value;
        }

        public static HttpParameter getInstance() {
            return new HttpParameter();
        }

        private HttpParameter() {
            mArryListParam = new ArrayList<HttpParameter>();
            mHeader = new ArrayList<HttpParameter>();
        }

        public T getKey() {
            return mKey;
        }

        public HttpParameter setKey(T mKey) {
            this.mKey = mKey;
            return getInstance();
        }

        public T getValue() {
            return mValue;
        }

        public HttpParameter setValue(T mValue) {
            this.mValue = mValue;
            return getInstance();
        }

        public HttpParameter addJSONParam(T key, T value) {
            try {
                if (mJSONParam == null) {
                    mJSONParam = new JSONObject();
                }
                mJSONParam.put(key.toString(), value.toString());

                return this;
            } catch (Exception e) {
                return null;
            }
        }

        public HttpParameter addArrayListParam(T key, T value) {
            try {
                mArryListParam.add(new HttpParameter(key, value));
                return this;
            } catch (Exception e) {
                return null;
            }
        }

        public ArrayList<HttpParameter> getArrayListParam() {
            if (mArryListParam != null && mArryListParam.size() > 0) {
                return mArryListParam;
            }
            return null;
        }

        public JSONObject getJSONParam() {
            if (mJSONParam != null) {
                return mJSONParam;
            }
            return null;
        }

        public HttpParameter addHeader(T key, T value) {
            try {
                mHeader.add(new HttpParameter(key, value));
                return this;
            } catch (Exception e) {
                return null;
            }
        }

        public ArrayList<HttpParameter> getHeader() {
            if (mHeader != null && mArryListParam.size() > 0) {
                return mHeader;
            }
            return null;
        }
    }
}