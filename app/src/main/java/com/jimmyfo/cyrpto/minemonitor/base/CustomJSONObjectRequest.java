package com.jimmyfo.cyrpto.minemonitor.base;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

public class CustomJSONObjectRequest extends Request<JSONObject> {
    private final JSONObject gson = new JSONObject();
    //private final Map<String, String> headers;
    private final Response.Listener<JSONObject> listener;

    public CustomJSONObjectRequest(String url,
                                   Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(Request.Method.GET, url, errorListener);
        //this.headers = headers;
        this.listener = listener;
    }

//    @Override
//    public Map<String, String> getHeaders() throws AuthFailureError {
//        return headers != null ? headers : super.getHeaders();
//    }

    @Override
    protected void deliverResponse(JSONObject response) {
        listener.onResponse(response);
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {
            String stringResponse = new String(response.data);
            String encoding = response.headers.get("Content-Encoding");

            if (encoding != null) {
                StringBuilder sb = new StringBuilder();

                if (encoding.equals("gzip")) {

                    final GZIPInputStream gStream = new GZIPInputStream(new ByteArrayInputStream(response.data));
                    final InputStreamReader reader = new InputStreamReader(gStream);
                    final BufferedReader in = new BufferedReader(reader);
                    String read;
                    while ((read = in.readLine()) != null) {
                        sb.append(read);
                    }
                    reader.close();
                    in.close();
                    gStream.close();

                    stringResponse = sb.toString();

                } else if (encoding.equals("deflate")) {

                    Inflater inflater = new Inflater(true);
                    final InflaterInputStream inflaterInputStream = new InflaterInputStream(new ByteArrayInputStream(response.data), inflater);
                    final InputStreamReader reader = new InputStreamReader(inflaterInputStream);
                    final BufferedReader in = new BufferedReader(reader);
                    String read;
                    while ((read = in.readLine()) != null) {
                        sb.append(read);
                    }
                    reader.close();
                    in.close();
                    inflaterInputStream.close();

                    stringResponse = sb.toString();
                }
            }

            JSONObject jsonObject = new JSONObject(stringResponse);

            Response<JSONObject> r = Response.success(
                    jsonObject,
                    HttpHeaderParser.parseCacheHeaders(response));

            return r;
        } catch (UnsupportedEncodingException ex) {
            Log.e("JSONRequest", ex.getStackTrace().toString());

            return Response.error(new ParseError(ex));
        } catch (JSONException ex) {
            Log.e("JSONRequest", ex.getStackTrace().toString());

            return Response.error(new ParseError(ex));
        } catch (IOException ex) {
            Log.e("JSONRequest", ex.getStackTrace().toString());

            return Response.error(new ParseError(ex));
        } catch (Exception ex) {
            Log.e("JSONRequest", ex.getStackTrace().toString());

            return Response.error(new ParseError(ex));
        }
    }
}