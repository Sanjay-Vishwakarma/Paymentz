package practice;

import com.payment.exceptionHandler.PZTechnicalViolationException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.swing.text.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Admin on 4/30/2020.
 */
public class p6
{ public static Map<String,String>getQueryMap(String query)
{
    String var1[]=query.split("&");
    Map<String,String> var2= new HashMap<>();
    for(String var3:var1)
    {
        String[] var4 = var3.split("=");
        String name = var4[0];
        if (var4.length > 1)
        {
            String value = var4[1];
            var2.put(name, value);

        }

  }
return var2;
}
    private Document createDocumentFromString (String xmlstring)throws PZTechnicalViolationException
    {
        Document document = null;
        try
        {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder= documentBuilderFactory.newDocumentBuilder();
            document= (Document) documentBuilder.parse(new InputSource(new StringReader(xmlstring.toString().trim())));
        }
        catch (ParserConfigurationException e)
        {
            e.printStackTrace();
        }
        catch (SAXException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return document;
    }

}