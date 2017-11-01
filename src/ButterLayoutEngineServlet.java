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

/**
 * Created by theapache64 on 1/11/17.
 */
@WebServlet(urlPatterns = "/butter_layout_engine")
public class ButterLayoutEngineServlet extends HttpServlet {

    private static final String KEY_XML_DATA = "xml_data";
    private static final String KEY_R_SERIES = "r_series";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");

        final String xmlData = req.getParameter(KEY_XML_DATA);
        final String rSeries = req.getParameter(KEY_R_SERIES);


        try {

            if (xmlData == null || xmlData.trim().isEmpty()) {
                throw new RequestException("Missing xml_data");
            }

            if (rSeries == null || rSeries.trim().isEmpty()) {
                throw new RequestException("Missing r_series");
            }


            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();

            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(new InputSource(new StringReader(xmlData)));
            //Document doc = dBuilder.parse(new File("/home/theapache64/Documents/projects/cybaze/staynodes/android/lakkidi_village/staynodes/src/main/res/layout/content_base_room.xml"));
            doc.getDocumentElement().normalize();

            System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

            NodeList nodeList = doc.getElementsByTagName("*");

            StringBuilder butterLayoutBuilder = null;

            for (int i = 0; i < nodeList.getLength(); i++) {


                Node node = nodeList.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE) {

                    // do something with the current element
                    final Node idNode = node.getAttributes().getNamedItem("android:id");
                    if (idNode != null) {

                        if (butterLayoutBuilder == null) {
                            butterLayoutBuilder = new StringBuilder();
                        }

                        final String id = idNode.getNodeValue().split("/")[1];
                        butterLayoutBuilder.append(String.format("@BindView(%s.id.%s)\n", rSeries, id));
                        String nodeName = node.getNodeName();
                        if (nodeName.contains(".")) {
                            final String[] nodeNameChunks = nodeName.split("\\.");
                            nodeName = nodeNameChunks[nodeNameChunks.length - 1];
                        }
                        butterLayoutBuilder.append(String.format("%s %s;\n\n", nodeName, id));

                    }

                }
            }


            if (butterLayoutBuilder == null) {
                throw new RequestException("No view with id found");
            }

            resp.getWriter().write(new Response("OK", "butter_layout", butterLayoutBuilder.toString()).getResponse());

        } catch (RequestException | JSONException | SAXException | ParserConfigurationException e) {
            e.printStackTrace();
            resp.getWriter().write(new Response(e.getMessage()).getResponse());
        }

    }


}
