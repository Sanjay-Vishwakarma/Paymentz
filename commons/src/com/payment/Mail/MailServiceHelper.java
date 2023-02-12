package com.payment.Mail;

import com.manager.dao.MerchantDAO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TerminalVO;
import com.manager.vo.TransactionDetailsVO;
import com.manager.vo.TransactionVO;
import com.manager.vo.merchantmonitoring.MonitoringAlertDetailVO;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Naushad
 * Date: 5/3/16
 * Time: 7:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class MailServiceHelper
{
    public String getDetailTable(List<MerchantDetailsVO> merchantDetailsVOList)
    {
        StringBuffer table = new StringBuffer();
        String style="class=td11";
        int i=0;

        table.append("<table align=\"center\" cellspacing=\"1\"  border=\"1\"  width=\"90%\">");
        table.append("<tr>");
        table.append("<td width=\"4%\" valign=\"middle\" align=\"center\" bgcolor=\"#34495e\"><b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">Sr No</font></p></b></td>");
        table.append("<td width=\"15%\" valign=\"middle\" align=\"center\" bgcolor=\"#34495e\"><b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">MemberId</font></p></b></td>");
        table.append("<td width=\"4%\" valign=\"middle\" align=\"center\" bgcolor=\"#34495e\"><b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">Activation Date</b></td>");
        table.append("<td width=\"4%\" valign=\"middle\" align=\"center\" bgcolor=\"#34495e\"><b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">Current Date</b></td>");
        table.append("</tr>");

        String currentStyle=" font bgcolor=\"#ffffff\" color=\"#001963\" font-family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" font-size=\"2px\" ";
        style=currentStyle;

        for (MerchantDetailsVO merchantDetailsVO:merchantDetailsVOList)
        {
            table.append("<tr>");
            table.append("<td "+style+" valign=\"middle\" align=\"center\" width=\"4%\" >"+(++i)+"</td>");
            table.append("<td "+style+" valign=\"middle\" align=\"center\" width=\"4%\">"+merchantDetailsVO.getMemberId()+"</td>");
            table.append("<td "+style+" valign=\"middle\" align=\"center\" width=\"4%\">"+merchantDetailsVO.getRegistrationDate()+"</td>");
            table.append("<td "+style+" valign=\"middle\" align=\"center\" width=\"4%\">"+merchantDetailsVO.getActivation()+"</td>");
            table.append("</tr>");
        }

        table.append("</table>");
        return table.toString();
    }
    public String getTabularFormat(List<TransactionDetailsVO> transactionDetailsVOList)
    {
        StringBuffer table = new StringBuffer();
        String style="class=td11";
        int i=0;
        table.append("<table align=\"center\" cellspacing=\"1\"  border=\"1\"  width=\"90%\">");
        table.append("<tr>");
        table.append("<td width=\"4%\" valign=\"middle\" align=\"center\" bgcolor=\"#34495e\"><b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">Sr No</font></p></b></td>");
        table.append("<td width=\"15%\" valign=\"middle\" align=\"center\" bgcolor=\"#34495e\"><b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">MemberId</font></p></b></td>");
        table.append("<td width=\"4%\" valign=\"middle\" align=\"center\" bgcolor=\"#34495e\"><b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">TrackingId</b></td>");
        table.append("<td width=\"4%\" valign=\"middle\" align=\"center\" bgcolor=\"#34495e\"><b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">Description</b></td>");
        table.append("<td width=\"4%\" valign=\"middle\" align=\"center\" bgcolor=\"#34495e\"><b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">Amount</font></p></b></td>");
        table.append("<td width=\"4%\" valign=\"middle\" align=\"center\" bgcolor=\"#34495e\"><b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">Status</font></p></b></td>");
        table.append("</tr>");

        String currentStyle=" font bgcolor=\"#ffffff\" color=\"#001963\" font-family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" font-size=\"2px\" ";
        style=currentStyle;

        for (TransactionDetailsVO transactionDetailsVO:transactionDetailsVOList)
        {
            table.append("<tr>");
            table.append("<td "+style+" valign=\"middle\" align=\"center\" width=\"4%\" >"+(++i)+"</td>");
            table.append("<td "+style+" valign=\"middle\" align=\"center\" width=\"4%\">"+transactionDetailsVO.getToid()+"</td>");
            table.append("<td "+style+" valign=\"middle\" align=\"center\" width=\"4%\">"+transactionDetailsVO.getTrackingid()+"</td>");
            table.append("<td "+style+" valign=\"middle\" align=\"center\" width=\"4%\">"+transactionDetailsVO.getDescription()+"</td>");
            table.append("<td " + style + " valign=\"middle\" align=\"center\" width=\"4%\">"+transactionDetailsVO.getAmount()+ "</td>");
            table.append("<td "+style+" valign=\"middle\" align=\"center\" width=\"4%\">"+transactionDetailsVO.getStatus()+"</td>");
            table.append("</tr>");
        }

        table.append("</table>");
        return table.toString();
    }
    public String getDetailTableListFromHash(Map<String, List<MonitoringAlertDetailVO>> adminMonitoringAlertDetailVOsMap, MerchantDetailsVO merchantDetailsVO)
    {

        StringBuffer table = new StringBuffer();
        String style="class=td11";
        int i=0;
        table.append("<table align=\"center\" text_align=\"left\" cellspacing=\"1\"  border=\"1\"  width=\"90%\">");
        table.append("<tr>");
        table.append("<th width=\"4%\"align=\"center\" bgcolor=\"#34495e\"><b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">Merchant Name:</font></p></b></th>");
        table.append("<td " + style + " valign=\"middle\" align=\"center\" width=\"4%\" >" + merchantDetailsVO.getCompany_name().substring(0, 1).toUpperCase() + merchantDetailsVO.getCompany_name().substring(1) + "</td>");
        table.append("</tr>");
        table.append("<tr>");
        table.append("<th width=\"4%\"align=\"center\" bgcolor=\"#34495e\"><b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">MID:</font></p></b></th>");
        table.append("<td "+style+" valign=\"middle\" align=\"center\" width=\"4%\" >"+merchantDetailsVO.getMemberId()+"</td>");
        table.append("</tr>");

        Set set=adminMonitoringAlertDetailVOsMap.keySet();
        Iterator iterator=set.iterator();
        while (iterator.hasNext())
        {
            String terminalId=(String)iterator.next();
            table.append("<tr>");
            table.append("<th width=\"4%\"align=\"center\" bgcolor=\"#34495e\"><b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">Monitored Period:</font></p></b></th>");
            table.append("<th width=\"4%\"align=\"center\" bgcolor=\"#34495e\"><b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">TID Alert(s):-"+terminalId+"</font></p></b></th>");
            table.append("</tr>");

            List<MonitoringAlertDetailVO> monitoringAlertDetailVOs=adminMonitoringAlertDetailVOsMap.get(terminalId);
            for (MonitoringAlertDetailVO monitoringAlertDetailVO:monitoringAlertDetailVOs)
            {
                String currentStyle=" font bgcolor=\"#ffffff\" color=\"#001963\" font-family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" font-size=\"2px\" ";
                style=currentStyle;

                table.append("<tr>");
                table.append("<td "+style+" valign=\"middle\" align=\"center\" width=\"4%\" >"+monitoringAlertDetailVO.getMonitoringAlertPeriod()+"</td>");
                table.append("<td " + style + " valign=\"middle\" align=\"center\" width=\"4%\" >" + monitoringAlertDetailVO.getAlertMsg() + "</td>");
                table.append("</tr>");
            }
        }

        SimpleDateFormat fmt = new SimpleDateFormat("dd MMM,YYYY");

        table.append("<tr>");
        table.append("<th width=\"4%\"align=\"center\" bgcolor=\"#34495e\"><b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">Date Of Alert(s):</font></p></b></th>");
        table.append("<td " + style + " valign=\"middle\" align=\"center\" width=\"4%\" bgcolor=\"#34495e\"><b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">" + fmt.format(new Date()) + "</font></p></b></td>");
        table.append("</tr>");

        table.append("</table>");
        return table.toString();
    }

    public String getDetailTableDataFromForSingleRule(MonitoringAlertDetailVO monitoringAlertDetailVO, MerchantDetailsVO merchantDetailsVO, String terminalId)
    {

        StringBuffer table = new StringBuffer();
        String style = "class=td11";
        int i = 0;
        table.append("<table align=\"center\" text_align=\"left\" cellspacing=\"1\"  border=\"1\"  width=\"90%\">");
        table.append("<tr>");
        table.append("<th width=\"4%\"align=\"center\" bgcolor=\"#34495e\"><b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">Merchant Name:</font></p></b></th>");
        table.append("<td " + style + " valign=\"middle\" align=\"center\" width=\"4%\" >" + merchantDetailsVO.getCompany_name().substring(0, 1).toUpperCase() + merchantDetailsVO.getCompany_name().substring(1) + "</td>");
        table.append("</tr>");
        table.append("<tr>");
        table.append("<th width=\"4%\"align=\"center\" bgcolor=\"#34495e\"><b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">MID:</font></p></b></th>");
        table.append("<td " + style + " valign=\"middle\" align=\"center\" width=\"4%\" >" + merchantDetailsVO.getMemberId() + "</td>");
        table.append("</tr>");

        table.append("<tr>");
        table.append("<th width=\"4%\"align=\"center\" bgcolor=\"#34495e\"><b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">Monitored Period:</font></p></b></th>");
        table.append("<th width=\"4%\"align=\"center\" bgcolor=\"#34495e\"><b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">TID Alert(s):-" + terminalId + "</font></p></b></th>");
        table.append("</tr>");

        String currentStyle = " font bgcolor=\"#ffffff\" color=\"#001963\" font-family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" font-size=\"2px\" ";
        style = currentStyle;

        table.append("<tr>");
        table.append("<td " + style + " valign=\"middle\" align=\"center\" width=\"4%\" >" + monitoringAlertDetailVO.getMonitoringAlertPeriod() + "</td>");
        table.append("<td " + style + " valign=\"middle\" align=\"center\" width=\"4%\" >" + monitoringAlertDetailVO.getAlertMsg() + "</td>");
        table.append("</tr>");

        SimpleDateFormat fmt = new SimpleDateFormat("dd MMM,YYYY");

        table.append("<tr>");
        table.append("<th width=\"4%\"align=\"center\" bgcolor=\"#34495e\"><b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">Date Of Alert(s):</font></p></b></th>");
        table.append("<td " + style + " valign=\"middle\" align=\"center\" width=\"4%\" bgcolor=\"#34495e\"><b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">" + fmt.format(new Date()) + "</font></p></b></td>");
        table.append("</tr>");

        table.append("</table>");
        return table.toString();
    }

    public String getDetailTableListFromHashConsolidate(Map<String, Map<String, List<MonitoringAlertDetailVO>>> adminMonitoringAlertDetailVOsMapMap)
    {
        StringBuffer table = new StringBuffer();
        MerchantDAO merchantDAO = new MerchantDAO();
        String style = "class=td11";
        Set memberSet = adminMonitoringAlertDetailVOsMapMap.keySet();
        Iterator memberIterator = memberSet.iterator();
        while (memberIterator.hasNext())
        {
            String memberId = (String) memberIterator.next();
            MerchantDetailsVO merchantDetailsVO = null;
            try
            {
                merchantDetailsVO = merchantDAO.getMemberDetails(memberId);
            }
            catch (Exception e)
            {

            }
            Map<String, List<MonitoringAlertDetailVO>> adminMonitoringAlertDetailVOsMap = adminMonitoringAlertDetailVOsMapMap.get(memberId);

            int i = 0;
            table.append("<table align=\"center\" text_align=\"left\" cellspacing=\"1\"  border=\"1\"  width=\"90%\">");
            table.append("<tr>");
            table.append("<th width=\"4%\"align=\"center\" bgcolor=\"#34495e\"><b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">Merchant Name:</font></p></b></th>");
            table.append("<td " + style + " valign=\"middle\" align=\"center\" width=\"4%\" >" + merchantDetailsVO.getCompany_name().substring(0, 1).toUpperCase() + merchantDetailsVO.getCompany_name().substring(1) + "</td>");
            table.append("</tr>");
            table.append("<tr>");
            table.append("<th width=\"4%\"align=\"center\" bgcolor=\"#34495e\"><b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">MID:</font></p></b></th>");
            table.append("<td " + style + " valign=\"middle\" align=\"center\" width=\"4%\" >" + memberId + "</td>");
            table.append("</tr>");

            Set set = adminMonitoringAlertDetailVOsMap.keySet();
            Iterator iterator = set.iterator();
            while (iterator.hasNext())
            {
                String terminalId = (String) iterator.next();
                table.append("<tr>");
                table.append("<th width=\"4%\"align=\"center\" bgcolor=\"#34495e\"><b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">Monitored Period:</font></p></b></th>");
                table.append("<th width=\"4%\"align=\"center\" bgcolor=\"#34495e\"><b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">TID Alert(s):-" + terminalId + "</font></p></b></th>");
                table.append("</tr>");

                List<MonitoringAlertDetailVO> monitoringAlertDetailVOs = adminMonitoringAlertDetailVOsMap.get(terminalId);
                for (MonitoringAlertDetailVO monitoringAlertDetailVO : monitoringAlertDetailVOs)
                {
                    String currentStyle = " font bgcolor=\"#ffffff\" color=\"#001963\" font-family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" font-size=\"2px\" ";
                    style = currentStyle;

                    table.append("<tr>");
                    table.append("<td " + style + " valign=\"middle\" align=\"center\" width=\"4%\" >" + monitoringAlertDetailVO.getMonitoringAlertPeriod() + "</td>");
                    table.append("<td " + style + " valign=\"middle\" align=\"center\" width=\"4%\" >" + monitoringAlertDetailVO.getAlertMsg() + "</td>");
                    table.append("</tr>");
                }
            }

            SimpleDateFormat fmt = new SimpleDateFormat("dd MMM,YYYY");

            table.append("<tr>");
            table.append("<th width=\"4%\"align=\"center\" bgcolor=\"#34495e\"><b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">Date Of Alert(s):</font></p></b></th>");
            table.append("<td " + style + " valign=\"middle\" align=\"center\" width=\"4%\" bgcolor=\"#34495e\"><b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">" + fmt.format(new Date()) + "</font></p></b></td>");
            table.append("</tr>");
            table.append("</table>");
            table.append("<BR>");
        }
        return table.toString();
    }

    public String getDetailTableList(List<MonitoringAlertDetailVO> merchantDetailsVOList)
    {

        StringBuffer table = new StringBuffer();
        String style="class=td11";
        int i=0;
        table.append("<table align=\"center\" cellspacing=\"1\"  border=\"1\"  width=\"90%\">");
        table.append("<tr>");
        table.append("<td width=\"4%\" valign=\"middle\" align=\"center\" bgcolor=\"#34495e\"><b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">Merchant Name</font></p></b></td>");
        table.append("<td width=\"4%\" valign=\"middle\" align=\"center\" bgcolor=\"#34495e\"><b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">MID</font></p></b></td>");
        table.append("<td width=\"4%\" valign=\"middle\" align=\"center\" bgcolor=\"#34495e\"><b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">Registration Date</b></td>");
        table.append("<td width=\"4%\" valign=\"middle\" align=\"center\" bgcolor=\"#34495e\"><b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">Email</font></p></b></td>");
        table.append("</tr>");

        String currentStyle=" font bgcolor=\"#ffffff\" color=\"#001963\" font-family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" font-size=\"2px\" ";
        style=currentStyle;

        for (MonitoringAlertDetailVO merchantDetailsVO:merchantDetailsVOList)
        {
            table.append("<tr>");
            table.append("<td "+style+" valign=\"middle\" align=\"center\" width=\"4%\" >"+(++i)+"</td>");
            table.append("<td "+style+" valign=\"middle\" align=\"center\" width=\"4%\">"+"10176"+"</td>");
            table.append("<td "+style+" valign=\"middle\" align=\"center\" width=\"4%\">"+"23:59:59"+"</td>");
            table.append("<td " + style + " valign=\"middle\" align=\"center\" width=\"4%\">" +"test@pz.com"+ "</td>");
            table.append("</tr>");
        }

        table.append("</table>");
        return table.toString();
    }
    public String getDetailTableListForMerchant(List<MerchantDetailsVO> merchantDetailsVOList)
    {

        StringBuffer table = new StringBuffer();
        String style="class=td11";
        int i=0;
        table.append("<table align=\"center\" cellspacing=\"1\"  border=\"1\"  width=\"90%\">");
        table.append("<tr>");
        table.append("<td width=\"4%\" valign=\"middle\" align=\"center\" bgcolor=\"#34495e\"><b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">Sr No</font></p></b></td>");
        table.append("<td width=\"4%\" valign=\"middle\" align=\"center\" bgcolor=\"#34495e\"><b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">MemberId</font></p></b></td>");
        table.append("<td width=\"4%\" valign=\"middle\" align=\"center\" bgcolor=\"#34495e\"><b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">Last Transaction Date</b></td>");
        table.append("</tr>");

        String currentStyle=" font bgcolor=\"#ffffff\" color=\"#001963\" font-family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" font-size=\"2px\" ";
        style=currentStyle;

        for (MerchantDetailsVO merchantDetailsVO:merchantDetailsVOList)
        {
            table.append("<tr>");
            table.append("<td "+style+" valign=\"middle\" align=\"center\" width=\"4%\" >"+(++i)+"</td>");
            table.append("<td "+style+" valign=\"middle\" align=\"center\" width=\"4%\">"+merchantDetailsVO.getMemberId()+"</td>");
            table.append("<td "+style+" valign=\"middle\" align=\"center\" width=\"4%\">"+merchantDetailsVO.getLastTransactionDate()+"</td>");
            table.append("</tr>");
        }
        table.append("</table>");
        return table.toString();
    }

    public String getSuccessfulListOfTerminals(List<TerminalVO> terminalVOs)
    {

        StringBuffer table=new StringBuffer();
        String style="class=td11";
        int i=0;
        table.append("<table align=\"center\" cellspacing=\"1\"  border=\"1\"  width=\"90%\">");
        table.append("<tr>");
        table.append("<td width=\"4%\" valign=\"middle\" align=\"center\" bgcolor=\"#34495e\"><b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">Sr No</font></p></b></td>");
        table.append("<td width=\"4%\" valign=\"middle\" align=\"center\" bgcolor=\"#34495e\"><b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">MemberId</font></p></b></td>");
        table.append("<td width=\"4%\" valign=\"middle\" align=\"center\" bgcolor=\"#34495e\"><b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">Terminal</b></td>");
        table.append("<td width=\"4%\" valign=\"middle\" align=\"center\" bgcolor=\"#34495e\"><b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">Action to be taken</b></td>");
        table.append("</tr>");
        String currentStyle=" font bgcolor=\"#ffffff\" color=\"#001963\" font-family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" font-size=\"2px\" ";
        style=currentStyle;

        for (TerminalVO terminalVO:terminalVOs)
        {
            table.append("<tr>");
            table.append("<td "+style+" valign=\"middle\" align=\"center\" width=\"4%\" >"+(++i)+"</td>");
            table.append("<td "+style+" valign=\"middle\" align=\"center\" width=\"4%\">"+terminalVO.getMemberId()+"</td>");
            table.append("<td "+style+" valign=\"middle\" align=\"center\" width=\"4%\">"+terminalVO.toString()+"</td>");
            table.append("<td "+style+" valign=\"middle\" align=\"center\" width=\"4%\">"+"Exceeding thresholds"+"</td>");
            table.append("</tr>");
        }
        table.append("</table>");
        return table.toString();


    }
    public String getSuccessfulListOfTerminals(List<TerminalVO> terminalVOs,String message)
    {

        StringBuffer table=new StringBuffer();
        String style="class=td11";
        int i=0;
        table.append("<table align=\"center\" cellspacing=\"1\"  border=\"1\"  width=\"90%\">");
        table.append("<tr>");
        table.append("<td width=\"4%\" valign=\"middle\" align=\"center\" bgcolor=\"#34495e\"><b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">Sr No</font></p></b></td>");
        table.append("<td width=\"4%\" valign=\"middle\" align=\"center\" bgcolor=\"#34495e\"><b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">MemberId</font></p></b></td>");
        table.append("<td width=\"4%\" valign=\"middle\" align=\"center\" bgcolor=\"#34495e\"><b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">Terminal</b></td>");
        table.append("<td width=\"4%\" valign=\"middle\" align=\"center\" bgcolor=\"#34495e\"><b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">Action to be taken</b></td>");
        table.append("</tr>");
        String currentStyle=" font bgcolor=\"#ffffff\" color=\"#001963\" font-family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" font-size=\"2px\" ";
        style=currentStyle;

        for (TerminalVO terminalVO:terminalVOs)
        {
            table.append("<tr>");
            table.append("<td "+style+" valign=\"middle\" align=\"center\" width=\"4%\" >"+(++i)+"</td>");
            table.append("<td "+style+" valign=\"middle\" align=\"center\" width=\"4%\">"+terminalVO.getMemberId()+"</td>");
            table.append("<td "+style+" valign=\"middle\" align=\"center\" width=\"4%\">"+terminalVO.toString()+"</td>");
            table.append("<td "+style+" valign=\"middle\" align=\"center\" width=\"4%\">"+message+"</td>");
            table.append("</tr>");
        }
        table.append("</table>");
        return table.toString();


    }
    public String getSuccessfulListOfTerminals(Map<String,List<TerminalVO>> listMapMailToSent)
    {

        Set set=listMapMailToSent.keySet();
        StringBuffer table = new StringBuffer();
        if(set.size()>0)
        {

            String style = "class=td11";
            int i = 0;
            table.append("<table align=\"center\" cellspacing=\"1\"  border=\"1\"  width=\"90%\">");
            table.append("<tr>");
            table.append("<td width=\"4%\" valign=\"middle\" align=\"center\" bgcolor=\"#34495e\"><b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">Sr No</font></p></b></td>");
            table.append("<td width=\"4%\" valign=\"middle\" align=\"center\" bgcolor=\"#34495e\"><b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">MemberId</font></p></b></td>");
            table.append("<td width=\"4%\" valign=\"middle\" align=\"center\" bgcolor=\"#34495e\"><b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">Terminal</b></td>");
            table.append("<td width=\"4%\" valign=\"middle\" align=\"center\" bgcolor=\"#34495e\"><b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">Action to be taken</b></td>");
            table.append("</tr>");
            String currentStyle = " font bgcolor=\"#ffffff\" color=\"#001963\" font-family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" font-size=\"2px\" ";
            style = currentStyle;

            Iterator iterator = set.iterator();
            while (iterator.hasNext())
            {
                String memberId = (String) iterator.next();
                List<TerminalVO> terminalVOList = listMapMailToSent.get(memberId);
                for (TerminalVO terminalVO : terminalVOList)
                {
                    table.append("<tr>");
                    table.append("<td " + style + " valign=\"middle\" align=\"center\" width=\"4%\" >" + (++i) + "</td>");
                    table.append("<td " + style + " valign=\"middle\" align=\"center\" width=\"4%\">" + terminalVO.getMemberId() + "</td>");
                    table.append("<td " + style + " valign=\"middle\" align=\"center\" width=\"4%\">" + terminalVO.toString() + "</td>");
                    table.append("<td " + style + " valign=\"middle\" align=\"center\" width=\"4%\">" + "Exceeding thresholds" + "</td>");
                    table.append("</tr>");
                }
            }
            table.append("</table>");
        }
        return table.toString();


    }
    public String getSuccessfulListOfTerminals(Map<String,List<TerminalVO>> listMapMailToSent,String message)
    {

        Set set=listMapMailToSent.keySet();
        StringBuffer table = new StringBuffer();
        if(set.size()>0)
        {

            String style = "class=td11";
            int i = 0;
            table.append("<table align=\"center\" cellspacing=\"1\"  border=\"1\"  width=\"90%\">");
            table.append("<tr>");
            table.append("<td width=\"4%\" valign=\"middle\" align=\"center\" bgcolor=\"#34495e\"><b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">Sr No</font></p></b></td>");
            table.append("<td width=\"4%\" valign=\"middle\" align=\"center\" bgcolor=\"#34495e\"><b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">MemberId</font></p></b></td>");
            table.append("<td width=\"4%\" valign=\"middle\" align=\"center\" bgcolor=\"#34495e\"><b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">Terminal</b></td>");
            table.append("<td width=\"4%\" valign=\"middle\" align=\"center\" bgcolor=\"#34495e\"><b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">Action to be taken</b></td>");
            table.append("</tr>");
            String currentStyle = " font bgcolor=\"#ffffff\" color=\"#001963\" font-family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" font-size=\"2px\" ";
            style = currentStyle;

            Iterator iterator = set.iterator();
            while (iterator.hasNext())
            {
                String memberId = (String) iterator.next();
                List<TerminalVO> terminalVOList = listMapMailToSent.get(memberId);
                for (TerminalVO terminalVO : terminalVOList)
                {
                    table.append("<tr>");
                    table.append("<td " + style + " valign=\"middle\" align=\"center\" width=\"4%\" >" + (++i) + "</td>");
                    table.append("<td " + style + " valign=\"middle\" align=\"center\" width=\"4%\">" + terminalVO.getMemberId() + "</td>");
                    table.append("<td " + style + " valign=\"middle\" align=\"center\" width=\"4%\">" + terminalVO.toString() + "</td>");
                    table.append("<td " + style + " valign=\"middle\" align=\"center\" width=\"4%\">" + message+ "</td>");
                    table.append("</tr>");
                }
            }
            table.append("</table>");
        }
        return table.toString();
    }
    public String getCurrentDayChargebackDetails(List<TransactionVO> listMapMailToSent,String message)
    {
        StringBuffer table = new StringBuffer();
        if(listMapMailToSent.size()>0)
        {

            String style = "class=td11";
            int i = 0;
            table.append("<table align=\"center\" cellspacing=\"1\"  border=\"1\"  width=\"90%\">");
            table.append("<tr>");
            table.append("<td width=\"4%\" valign=\"middle\" align=\"center\" bgcolor=\"#34495e\"><b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">Sr No</font></p></b></td>");
            table.append("<td width=\"4%\" valign=\"middle\" align=\"center\" bgcolor=\"#34495e\"><b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">MemberId</font></p></b></td>");
            table.append("<td width=\"4%\" valign=\"middle\" align=\"center\" bgcolor=\"#34495e\"><b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">Tracking Id</b></td>");
            table.append("<td width=\"4%\" valign=\"middle\" align=\"center\" bgcolor=\"#34495e\"><b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">Action to be taken</b></td>");
            table.append("</tr>");
            String currentStyle = " font bgcolor=\"#ffffff\" color=\"#001963\" font-family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" font-size=\"2px\" ";
            style = currentStyle;



            for (TransactionVO transactionVO : listMapMailToSent)
            {
                table.append("<tr>");
                table.append("<td " + style + " valign=\"middle\" align=\"center\" width=\"4%\" >" + (++i) + "</td>");
                table.append("<td " + style + " valign=\"middle\" align=\"center\" width=\"4%\">" + transactionVO.getToid() + "</td>");
                table.append("<td " + style + " valign=\"middle\" align=\"center\" width=\"4%\">" + transactionVO.getTrackingId() + "</td>");
                table.append("<td " + style + " valign=\"middle\" align=\"center\" width=\"4%\">" + message+ "</td>");
                table.append("</tr>");
            }
            table.append("</table>");
        }
        return table.toString();
    }

    public String getSameCardConsequntlyDetails(List<TerminalVO> listMapMailToSent,String message)
    {
        StringBuffer table = new StringBuffer();
        if(listMapMailToSent.size()>0)
        {

            String style = "class=td11";
            int i = 0;
            table.append("<table align=\"center\" cellspacing=\"1\"  border=\"1\"  width=\"90%\">");
            table.append("<tr>");
            table.append("<td width=\"4%\" valign=\"middle\" align=\"center\" bgcolor=\"#34495e\"><b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">Sr No</font></p></b></td>");
            table.append("<td width=\"4%\" valign=\"middle\" align=\"center\" bgcolor=\"#34495e\"><b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">MemberId</font></p></b></td>");
            table.append("<td width=\"4%\" valign=\"middle\" align=\"center\" bgcolor=\"#34495e\"><b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">Terminal Id</b></td>");
            table.append("<td width=\"4%\" valign=\"middle\" align=\"center\" bgcolor=\"#34495e\"><b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">Action to be taken</b></td>");
            table.append("</tr>");
            String currentStyle = " font bgcolor=\"#ffffff\" color=\"#001963\" font-family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" font-size=\"2px\" ";
            style = currentStyle;



            for (TerminalVO terminalVO : listMapMailToSent)
            {
                table.append("<tr>");
                table.append("<td " + style + " valign=\"middle\" align=\"center\" width=\"4%\" >" + (++i) + "</td>");
                table.append("<td " + style + " valign=\"middle\" align=\"center\" width=\"4%\">" + terminalVO.getMemberId() + "</td>");
                table.append("<td " + style + " valign=\"middle\" align=\"center\" width=\"4%\">" + terminalVO.getTerminalId() + "</td>");
                table.append("<td " + style + " valign=\"middle\" align=\"center\" width=\"4%\">" + message+ "</td>");
                table.append("</tr>");
            }
            table.append("</table>");
        }
        return table.toString();
    }
}
