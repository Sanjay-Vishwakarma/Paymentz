package com.payment.payon.core.message;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created with IntelliJ IDEA.
 * User: Jignesh
 * Date: 10/9/12
 * Time: 7:58 AM
 * To change this template use File | Settings | File Templates.
 */
public class CreditCardAccount
{


    @XStreamAlias("Holder")
    private String holder;
    @XStreamAlias("Verification")
    private String verification;
    @XStreamAlias("Brand")
    private String brand ;

    public String getHolder()
    {
        return holder;
    }

    public String getVerification()
    {
        return verification;
    }

    public String getBrand()
    {
        return brand;
    }

    public String getNumber()
    {
        return number;
    }

    public CCDate getStart()
    {
        return start;
    }

    public CCDate getExpiry()
    {
        return expiry;
    }

    public String getCardIssueNumber()
    {
        return cardIssueNumber;
    }

    @XStreamAlias("Number")
    private String number ;
    @XStreamAlias("Start")
    private CCDate start;
    @XStreamAlias("Expiry")
    private CCDate expiry;

    public void setStart(CCDate start)
    {
        this.start = start;
    }

    public void setCardIssueNumber(String cardIssueNumber)
    {
        this.cardIssueNumber = cardIssueNumber;
    }

    private String cardIssueNumber;


    public void setHolder(String holder)
    {
        this.holder = holder;
    }

    public void setVerification(String verification)
    {
        this.verification = verification;
    }

    public void setBrand(String brand)
    {
        this.brand = brand;
    }

    public void setNumber(String number)
    {
        this.number = number;
    }

    public void setExpiry(CCDate CC)
    {
        this.expiry = CC;
    }


}


class Main
{
    public static void main(String[] args)
    {
//        Message request = new Request();
//        XStream xstream = new XStream();
//        xstream.autodetectAnnotations(true);
//        xstream.aliasPackage("", "com.payment.payon.core");
//        StringBuilder sb = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
//        StringBuilder xmlOut = sb.append(System.getProperty("line.separator")).append(xstream.toXML(request));
//        System.out.println(xmlOut);

        XStream xstream1 = new XStream();
        xstream1.autodetectAnnotations(true);
////        String xmlOutput="<Response> <Transaction mode=\"INTEGRATION\" response=\"SYNC\"> <Identification> <ShortID>7409.5437.6866</ShortID> <UUID>40288b16293c069701293fbcf93a2945</UUID>  </Identification> <Processing requestTimestamp=\"2009-11-16 15:08:09\" responseTimestamp=\"2009-11-16 15:08:09\" payPipeProcessingTime=\"17\" connectorTime=\"0\"> <ConnectorTxID1>1a6bbf12a38c408a9298f113e3d747ad</ConnectorTxID1> <ConnectorTxID2>3a6bbf12</ConnectorTxID2> <ConnectorTxID3>8c408a9298f113e3d747ad</ConnectorTxID3> <Return code=\"000.000.000\">Transaction succeeded</Return> </Processing> <ConnectorReceived timestamp=\"2009-11-16 15:08:09\"> <Returned code=\"00\">Successfully processed</Returned> <Body><![CDATA[<?xml version=\"1.0\" encoding=\"UTF-8\"?><SimulatorResponse><Message code=\"00\">Successfully processed</Message></SimulatorResponse>]]></Body> </ConnectorReceived> </Transaction> </Response>";
//        //String xmlOutput="<?xml version=\"1.0\" encoding=\"UTF-8\"?> <Response version=\"1.0\"> <Transaction mode=\"INTEGRATION\" response=\"ASYNC\"> <Identification> <ShortID>7409.5437.6866</ShortID> <UUID>40288b16293c069701293fbcf93a2945</UUID> </Identification> <Processing requestTimestamp=\"2010-06-16 07:44:16\" responseTimestamp=\"2010-06-16 07:44:16\" payPipeProcessingTime=\"212\" connectorTime=\"0\"> <Return code=\"000.000.000\">Transaction succeeded</Return> <ConnectorTxID1>edcwercf04a3292acdd230792</ConnectorTxID1> <ConnectorTxID2>9eawerac44605bfe9a9fd</ConnectorTxID2> </Processing> <ConnectorReceived timestamp=\"2010-06-16 07:44:16\"> <Returned code=\"200\">There were no errors during the execution of the operation.</Returned> <Body><![CDATA[ tid=edwerwer04a3292acdd230792&pst=200&cbv=1&uid=9ea9a2742f11bf71cac44605bfe9a9fd]]></Body> </ConnectorReceived> </Transaction> </Response>";
//        //String xmlOutput="<?xml version=\"1.0\" encoding=\"UTF-8\"?> <Response version=\"1.0\"> <Transaction mode=\"INTEGRATION\" response=\"ASYNC\"> <Identification> <ShortID>5863.3555.4210</ShortID> <UUID>40288b16293c069701293fa94b07285d</UUID> </Identification> <Processing requestTimestamp=\"2010-06-16 07:29:22\" responseTimestamp=\"2010-06-16 07:29:23\" payPipeProcessingTime=\"2\" connectorTime=\"234\"> <Return code=\"000.000.000\">Transaction succeeded</Return> <ConnectorTxID1>23163</ConnectorTxID1> </Processing> <ConnectorReceived timestamp=\"2010-06-16 07:29:23\"> <Returned code=\"0\">Successful</Returned> <Body></Body> </ConnectorReceived> </Transaction> </Response>";
        String xmlOutput="<Response version=\"1.0\">\n" +
                "    <Transaction mode=\"TEST\" response=\"SYNC\">\n" +
                "        <Identification>\n" +
                "            <ShortID>0724673110068</ShortID>\n" +
                "            <UUID>itjvEvguSnbrOjrIkLtoeJaPUCBdp</UUID>\n" +
                "        </Identification>\n" +
                "        <Processing requestTimestamp=\"2012-10-17 17:16:53\" responseTimestamp=\"2012-10-17 17:16:53\" payPipeProcessingTime=\"42\" connectorTime=\"0\">\n" +
                "            <Return code=\"000.000.000\">Transaction succeeded</Return>\n" +
                "            <ConnectorTxID1 description=\"ConnectorTxID1 description\">itjvEvguSnbrOjrIkLtoeJaPUCBdp</ConnectorTxID1>\n" +
                "            <ConnectorTxID2 description=\"ConnectorTxID2 description\">itjvEvgu</ConnectorTxID2>\n" +
                "            <ConnectorTxID3 description=\"ConnectorTxID3 description\">brOjrIkLtoeJaPUCBdp</ConnectorTxID3>\n" +
                "            <ConnectorDetails>\n" +
                "                <Result name=\"AuthCode\">f2e7a815c3</Result>\n" +
                "                <Result name=\"EXTERNAL_SYSTEM_LINK\">https://csi-test.retaildecisions.com/RS60/TransDetail.aspx?oid=000194001101S2E20110926045038668&amp;support=Link+to+Risk+Details</Result>\n" +
                "                <Result name=\"OrderID\">8316384413</Result>\n" +
                "                <Result name=\"ProcStatus\">0</Result>\n" +
                "                <Result name=\"TermID\">71F00820</Result>\n" +
                "            </ConnectorDetails>\n" +
                "        </Processing>\n" +
                "        <ConnectorReceived timestamp=\"2012-10-17 17:16:53\">\n" +
                "            <Returned code=\"00\">Successfully processed</Returned>\n" +
                "            <Body><![CDATA[<?xml version=\"1.0\" encoding=\"UTF-8\"?><SimulatorResponse><Message code=\"00\">Successfully processed</Message></SimulatorResponse>]]></Body>\n" +
                "        </ConnectorReceived>\n" +
                "    </Transaction>\n" +
                "</Response>";
//
//
        xstream1.alias("Response",Response.class);
//
        Response response = (Response)xstream1.fromXML(xmlOutput);
        /*System.out.println(response.getResponseTransaction().getProcessing().getReturnresp().getCode());
        System.out.println(response.getResponseTransaction().getProcessing().getReturnresp().getMessage());*/
//
//
//
//
//        System.out.println(response);

//        for(int i=0;i<300;i++)
//        System.out.println(RandomUtils.nextInt()+1);



    }


}
