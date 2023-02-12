//import com.gargoylesoftware.htmlunit.WebClient;
//import com.gargoylesoftware.htmlunit.ElementNotFoundException;
//import com.gargoylesoftware.htmlunit.html.*;
//
//import java.net.URL;
//import java.net.MalformedURLException;
//import java.io.IOException;
//
///**
// * Created by IntelliJ IDEA.
// * User: alpesh.s
// * Date: Mar 15, 2006
// * Time: 6:38:50 PM
// * To change this template use File | Settings | File Templates.
// */
//public class TransactionPageTest
//{
//    public static void main(String[] args)
//    {
//        WebClient webClient=new WebClient();
//        try
//        {
//            URL url = new URL("https://secure..com/icici/icicicredit/newpay.php3");
//            HtmlPage page1 = (HtmlPage)webClient.getPage(url);
//           // System.out.println( page1.getTitleText() );
//
//            HtmlForm form1 = (HtmlForm)page1.getForms().get(0);
//
//            HtmlSelect selectField = (HtmlSelect)form1.getSelectByName("toid");
//            page1=(HtmlPage)selectField.setSelectedAttribute("1378",true);
//
//            HtmlSubmitInput button1= (HtmlSubmitInput)form1.getInputByValue("Submit");
//
//            HtmlPage page2 = (HtmlPage)button1.click();
//
//            HtmlForm form2 = page2.getFormByName("mainForm");
//
//
//            HtmlSelect selectField1 = (HtmlSelect)form2.getSelectByName("EXPIRE_MONTH");
//
//            page2=(HtmlPage)selectField1.setSelectedAttribute("05",true);
//
//            HtmlSelect selectField2 = (HtmlSelect)form2.getSelectByName("EXPIRE_YEAR");
//
//            page2=(HtmlPage)selectField2.setSelectedAttribute("2009",true);
//
//
//            HtmlTextInput textField1 = (HtmlTextInput)form2.getInputByName("CARDHOLDER");
//            textField1.setValueAttribute("Alpesh Shah");
//
//
//            HtmlTextInput textField2 = (HtmlTextInput)form2.getInputByName("PAN");
//            textField2.setValueAttribute("4012888888881881");
//
//            HtmlTextInput textField3 = (HtmlTextInput)form2.getInputByName("ccid");
//            textField3.setValueAttribute("123");
//
//            HtmlSubmitInput button2= (HtmlSubmitInput)form2.getInputByValue("Continue");
//
//            try
//            {
//
//                HtmlPage page3 = (HtmlPage) button2.click();
//
//                // System.out.println( page3.getTitleText() );
//
//                HtmlForm form3 = (HtmlForm) page3.getForms().get(0);
//
//                HtmlButtonInput button3 = null;
//
//                //button3 = (HtmlButtonInput) form3.getInputByValue("Continue");
//                button3 = (HtmlButtonInput) form3.getInputByValue("Cancel Transaction");
//                HtmlPage page4 = (HtmlPage) button3.click();
//                String content =page4.getWebResponse().getContentAsString();
//                // System.out.println( page4.getTitleText() );
//               // System.out.println(page4.getWebResponse().getContentAsString());
//                if("N".equals(content) || "Y".equals(content))
//                {
//                    //We got valid response
//                    System.out.println("Y");
//                }
//            }
//            catch (ElementNotFoundException e)
//            {
//                e.printStackTrace();
//                System.out.println("N");
//            }
//
//        }
//        catch (MalformedURLException e)
//        {
//            e.printStackTrace();
//            System.out.println("\n All Pages are not loaded");
//        }
//        catch (IOException e)
//        {
//            e.printStackTrace();
//            System.out.println("\n All Pages are not loaded");
//        }
//        catch (ElementNotFoundException e)
//        {
//            e.printStackTrace();
//            System.out.println("\n All Pages are not loaded");
//        }
//        catch(IndexOutOfBoundsException e)
//        {
//           e.printStackTrace();
//           System.out.println("\n All Pages are not loaded");
//        }
//
//    }
//}
