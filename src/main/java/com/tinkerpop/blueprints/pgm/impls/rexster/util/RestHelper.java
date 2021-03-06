package com.tinkerpop.blueprints.pgm.impls.rexster.util;

import com.tinkerpop.blueprints.pgm.impls.rexster.RexsterTokens;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class RestHelper {

    private static final JSONParser parser = new JSONParser();
    private static final String POST = "POST";
    private static final String DELETE = "DELETE";

    public static JSONObject get(final String uri) {
        try {
            final URL url = new URL(safeUri(uri));
            final URLConnection connection = url.openConnection();
            connection.connect();
            final InputStreamReader reader = new InputStreamReader(connection.getInputStream());
            final JSONObject object = (JSONObject) parser.parse(reader);
            reader.close();
            return object;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static JSONArray getResultArray(final String uri) {
        return (JSONArray) RestHelper.get(safeUri(uri)).get(RexsterTokens.RESULTS);
    }

    public static JSONObject getResultObject(final String uri) {
        return (JSONObject) RestHelper.get(safeUri(uri)).get(RexsterTokens.RESULTS);
    }

    public static JSONObject postResultObject(final String uri) {
        try {
            final URL url = new URL(safeUri(uri));
            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(POST);
            InputStreamReader reader = new InputStreamReader(connection.getInputStream());
            final JSONObject retObject = (JSONObject) ((JSONObject) parser.parse(reader)).get(RexsterTokens.RESULTS);
            reader.close();
            return retObject;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static void post(final String uri) {
        try {
            final URL url = new URL(safeUri(uri));
            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(POST);
            InputStreamReader reader = new InputStreamReader(connection.getInputStream());
            reader.close();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }


    public static void delete(final String uri) {
        try {
            final URL url = new URL(uri);
            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(DELETE);
            final InputStreamReader reader = new InputStreamReader(connection.getInputStream());
            reader.close();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static Object typeCast(final String type, final Object value) {
        if (type.equals(RexsterTokens.STRING))
            return value.toString();
        else if (type.equals(RexsterTokens.INTEGER))
            return Integer.valueOf(value.toString());
        else if (type.equals(RexsterTokens.LONG))
            return Long.valueOf(value.toString());
        else if (type.equals(RexsterTokens.DOUBLE))
            return Double.valueOf(value.toString());
        else if (type.equals(RexsterTokens.FLOAT))
            return Float.valueOf(value.toString());
        else
            return value;
    }

    public static String uriCast(final Object value) {
        if (value instanceof String)
            return value.toString();
        else if (value instanceof Integer)
            return "(" + RexsterTokens.INTEGER + "," + value + ")";
        else if (value instanceof Long)
            return "(" + RexsterTokens.LONG + "," + value + ")";
        else if (value instanceof Double)
            return "(" + RexsterTokens.DOUBLE + "," + value + ")";
        else if (value instanceof Float)
            return "(" + RexsterTokens.FLOAT + "," + value + ")";
        else
            return value.toString();

    }

    private static String safeUri(String uri) {
        // todo: make this way more safe
        return uri.replace(" ", "%20");
    }
}
