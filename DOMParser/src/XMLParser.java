/**
 * Created by Sid19 on 3/7/2016.
 */

import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;

public class XMLParser {
    public static void main(String[] args)
    {
        try
        {
            File inputFile = new File("Cordova.xml"); //Enter File Name here. 
            /*
            	Hardcoded file names to be exact. This Parser just extracts the necessary node values such as title and 
            	description from each XML file and this allows us to create the corpus.
            */
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();
            System.out.println("Root Element :" + doc.getDocumentElement().getNodeName());
            NodeList nList = doc.getElementsByTagName("item");
            System.out.println("\n------------------------------------------\n");
            for (int temp = 0; temp < nList.getLength(); temp++)
            {
                Node nNode = nList.item(temp);
                //System.out.println("\nCurrent Element: " + nNode.getNodeName());
                if (nNode.getNodeType() == Node.ELEMENT_NODE)
                {
                    Element eElement = (Element) nNode;
                    System.out.println("Title: " + eElement.getElementsByTagName("title").item(0).getTextContent());
                    System.out.println("Description: " + eElement.getElementsByTagName("description").item(0).getTextContent());
                    //Comment out respective print statements to get either Title Alone, Description alone or both together
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
