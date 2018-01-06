package com.theah64.butterlayout.servlets;

import com.theah64.butterlayout.RequestException;
import com.theah64.butterlayout.Response;
import com.theah64.butterlayout.models.View;
import org.json.JSONException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
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
@WebServlet(urlPatterns = "/view_xml_to_style_xml")
public class ViewXmlToStyleXmlServlet extends HttpServlet {

    private static final String KEY_XML_DATA = "xml_view_data";

    private static String getFirstCharUppercase(String input) {
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");

        try {
            final String xmlData = req.getParameter(KEY_XML_DATA);

            if (xmlData == null) {
                throw new RequestException("xml_data missing");
            }

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(new InputSource(new StringReader(xmlData)));
            doc.getDocumentElement().normalize();
            NodeList nodeList = doc.getElementsByTagName("*");

            List<View> views = new ArrayList<>();

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);

                NamedNodeMap attributes = node.getAttributes();
                final Node idNode = attributes.getNamedItem("android:id");
                final List<View.Property> properties = new ArrayList<>();

                for (int j = 0; j < attributes.getLength(); j++) {
                    final Node n = attributes.item(j);
                    properties.add(new View.Property(n.getNodeName(), n.getNodeValue()));
                }

                views.add(new View(idNode != null ? idNode.getNodeValue() : null, node.getNodeName(), properties));

            }

            if (views.isEmpty()) {
                throw new RequestException("No view found");
            }

            resp.getWriter().write(new Response("OK", "output", genCode(views)).getResponse());

        } catch (RequestException | ParserConfigurationException | SAXException | JSONException e) {
            e.printStackTrace();
            resp.getWriter().write(new Response(e.getMessage()).getResponse());
        }

    }

    /**
     * <style name="label_card">
     * <item name="android:background">@drawable/bg_card_header_label</item>
     * <item name="android:padding">8dp</item>
     * <item name="android:textColor">@android:color/white</item>
     * <item name="android:textSize">10sp</item>
     * <item name="android:layout_marginBottom">8dp</item>
     * </style>
     *
     * @param views
     * @return
     */

    private static String genCode(List<View> views) {

        final StringBuilder codeBuilder = new StringBuilder("<!-- Generated with ButterLayout (http://github.com/theapache64/butterLayout): " + new Date() + "-->\n");

        for (final View view : views) {

            if (view.getProperties().isEmpty()) {
                continue;
            }

            codeBuilder
                    .append(String.format("<style name=\"%sStyle\">", getStyleName(view))).append("\n");

            for (final View.Property property : view.getProperties()) {
                if (property.getKey().equals("android:id")) {
                    continue;
                }
                codeBuilder.append(String.format("\t<item name=\"%s\">%s</item>", sanitizeKey(property.getKey()), property.getValue())).append("\n");
            }

            codeBuilder.append("</style>\n\n");
        }


        return codeBuilder.toString();
    }

    private static String sanitizeKey(String key) {
        final String[] chunks = key.split(":");
        if (chunks[0].equals("android")) {
            return key;
        }
        return chunks.length > 1 ? chunks[1] : key;
    }

    private static String getStyleName(View view) {
        String styleName = view.getId() != null ? view.getId().split("/")[1] : view.getType();
        if (styleName.contains(".")) {
            final String[] chunks = styleName.split("\\.");
            return chunks[chunks.length - 1];
        }
        return styleName;
    }


}
