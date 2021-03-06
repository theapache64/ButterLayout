package com.theah64.butterlayout.servlets;

import com.theah64.butterlayout.RequestException;
import com.theah64.butterlayout.Response;
import org.json.JSONException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by theapache64 on 1/11/17.
 */
@WebServlet(urlPatterns = "/xml_to_binder_java")
public class XmlToBinderJavaServlet extends HttpServlet {

    private static final String KEY_XML_DATA = "xml_data";
    private static final String KEY_ROOT_VIEW = "root_view";
    private static final String KEY_DATA_MODEL = "data_model";

    private static String getFirstCharUppercase(String input) {
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");

        final String xmlData = req.getParameter(KEY_XML_DATA);
        String rootView = req.getParameter(KEY_ROOT_VIEW);
        String dataModel = req.getParameter(KEY_DATA_MODEL);


        if (rootView.trim().isEmpty()) {
            rootView = null;
        }

        try {

            if (dataModel.trim().isEmpty()) {
                throw new RequestException("Data model name can't be empty");
            }

            if (xmlData == null || xmlData.trim().isEmpty()) {
                throw new RequestException("Missing xml_data");
            }

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(new InputSource(new StringReader(xmlData)));
            doc.getDocumentElement().normalize();
            NodeList nodeList = doc.getElementsByTagName("*");
            StringBuilder codeBuilder = new StringBuilder();
            final List<String> buttons = new ArrayList<>();

            for (int i = 0; i < nodeList.getLength(); i++) {


                Node node = nodeList.item(i);
                String nodeName = node.getNodeName();

                if (nodeName.equals("include")) {
                    continue;
                }

                if (node.getNodeType() == Node.ELEMENT_NODE) {

                    // do something with the current element
                    final Node idNode = node.getAttributes().getNamedItem("android:id");

                    if (idNode != null) {

                        if (codeBuilder.length() == 0) {
                            codeBuilder = new StringBuilder("//Generated with ButterLayout (http://github.com/theapache64/butterLayout): " + new Date() + "\n");
                        }

                        final String id = idNode.getNodeValue().split("/")[1];
                        if (nodeName.contains(".")) {
                            final String[] nodeNameChunks = nodeName.split("\\.");
                            nodeName = nodeNameChunks[nodeNameChunks.length - 1];
                        }

                        //vtilCompanyName.setText(employeeExperience.getCompanyName());
                        final String fromFirstChar = getFromFirstUpperChar(id);
                        if (nodeName.endsWith("CheckBox")) {
                            codeBuilder.append("\n")
                                    .append(rootView != null ? rootView + "." : "")
                                    .append(id).append(String.format(".setChecked(%s.is", dataModel)).append(fromFirstChar).append("());");
                        } else {
                            codeBuilder.append("\n")
                                    .append(rootView != null ? rootView + "." : "")
                                    .append(id).append(String.format(".setText(%s.get", dataModel)).append(fromFirstChar).append("());");
                        }

                    }
                }

            }


            if (codeBuilder.length() == 0) {
                throw new RequestException("No view with id found");
            }


            resp.getWriter().write(new Response("OK", "output", codeBuilder.toString()).getResponse());

        } catch (RequestException | JSONException | SAXException | ParserConfigurationException e) {
            e.printStackTrace();
            resp.getWriter().write(new Response(e.getMessage()).getResponse());
        }

    }

    private static String getFromFirstUpperChar(String string) {
        final String[] chunks = string.split("(?=[A-Z])");
        if (chunks.length > 1) {
            return string.substring(chunks[0].length());
        }
        return string;
    }


}
