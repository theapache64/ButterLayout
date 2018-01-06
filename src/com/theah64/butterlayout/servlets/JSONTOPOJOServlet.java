package com.theah64.butterlayout.servlets;

import com.theah64.butterlayout.RequestException;
import com.theah64.butterlayout.Response;
import com.theah64.butterlayout.utils.CodeGen;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by theapache64 on 6/1/18.
 */
@WebServlet(urlPatterns = {"/json_to_pojo"})
public class JSONTOPOJOServlet extends HttpServlet {


    private static final String KEY_JSON_DATA = "json_data";
    private static final String KEY_CLASS_NAME = "class_name";
    private static final String KEY_PACKAGE_NAME = "package_name";
    private static final String KEY_IS_RETROFIT_MODEL = "is_retrofit_model";


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");

        final String jsonData = req.getParameter(KEY_JSON_DATA);
        final String className = req.getParameter(KEY_CLASS_NAME);
        final String packageName = req.getParameter(KEY_PACKAGE_NAME);
        System.out.println(req.getParameter(KEY_IS_RETROFIT_MODEL));
        final boolean isRetrofitModel = req.getParameter(KEY_IS_RETROFIT_MODEL).equals("true");

        try {

            if (packageName == null) {
                throw new RequestException("Package name can't be empty");
            }

            if (className == null) {
                throw new RequestException("Empty class name");
            }

            if (jsonData == null || jsonData.trim().isEmpty()) {
                throw new RequestException("JSON Data missing");
            }

            if (isJSONValid(jsonData)) {
                resp.getWriter().write(new Response("Ok", "output",
                        CodeGen.getFinalCode(packageName, jsonData, className, isRetrofitModel)).getResponse());
            }

        } catch (RequestException | JSONException e) {
            e.printStackTrace();
            resp.getWriter().write(new Response(e.getMessage()).getResponse());
        }

    }

    private static boolean isJSONValid(String data) throws JSONException {
        try {
            new JSONObject(data);
        } catch (JSONException ex) {
            new JSONArray(data);
        }
        return true;
    }

}
