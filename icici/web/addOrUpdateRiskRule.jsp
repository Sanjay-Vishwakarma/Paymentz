<%--suppress XmlDuplicatedId --%>
<%@ page import="com.manager.vo.ActionVO" %>
<%@ page import="org.owasp.esapi.ValidationErrorList" %>
<%@ page import="org.owasp.esapi.errors.ValidationException" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.manager.vo.riskRuleVOs.RuleVO" %>
<%@ page import="com.manager.ProfileManagementManager" %>
<%@ page import="com.manager.vo.PayIfeTableInfo" %>
<%@ page import="java.util.*" %>
<%@ page import="com.manager.vo.riskRuleVOs.RuleOperation" %>
<%@ page import="com.manager.enums.*" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%--
  Created by IntelliJ IDEA.S
  User: PZ
  Date: 29/6/15
  Time: 3:42 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="index.jsp" %>
<html>
<head>
  <title></title>
  <style type="text/css">
    .none
    {
      display: none;
    }
  </style>
  <script>
    var payIFETableArray=[];
  </script>
</head>
<body>
<%!
  private ProfileManagementManager profileManagementManager = new ProfileManagementManager();

  private Functions functions= new Functions();

%>
<%
  ActionVO actionVO =null;
  RuleVO ruleVO =new RuleVO();
%>
<div class="row">
  <div class="col-lg-12">
    <div class="panel panel-default">
      <div class="panel-heading" >
        Risk Rule Details
        <div style="float: right;">

        </div>
      </div>
      <%


        if(request.getAttribute("error")!=null)
        {
          ValidationErrorList error = (ValidationErrorList) request.getAttribute("error");
          for (ValidationException errorList : error.errors())
          {

            out.println("<center><font class=\"textb\">" + errorList.getMessage() + "</font></center>");
          }
        }
        if(request.getAttribute("catchError")!=null)
        {
          out.println("<center><font class=\"textb\">" + request.getAttribute("catchError") + "</font></center>");
        }

      %>

      <%
        Set<String> tableAlliasName=new HashSet<String>();
        List<RuleVO>  riskRule=profileManagementManager.getListOfRiskRuleDetails(null, null);
        Map<String,PayIfeTableInfo> payIfeTableInfoMap =profileManagementManager.getAllPayIfeFieldsInformation(null,null,tableAlliasName);

        StringBuffer payIFETableArray=new StringBuffer();

        StringBuffer selectOptionTag=new StringBuffer();
        StringBuffer fromOptionTag=new StringBuffer();
        StringBuffer whereOptionTag=new StringBuffer();
        StringBuffer groupOptionTag=new StringBuffer();

        StringBuffer comparatorComparator=new StringBuffer();
        StringBuffer flat_FileComparator=new StringBuffer();
        StringBuffer regexComparator=new StringBuffer();
        StringBuffer databaseComparator=new StringBuffer();

        StringBuffer selectQuery=new StringBuffer();
        StringBuffer fromQuery=new StringBuffer();
        StringBuffer whereQuery=new StringBuffer();
        StringBuffer groupByQuery=new StringBuffer();
        StringBuffer buttonQuery=new StringBuffer();

        List<AggregateFunctions> aggreGateFunction= new ArrayList<AggregateFunctions>();
        List<String> joinList=new ArrayList<String>();
        List<String> onList=new ArrayList<String>();
        List<String> whereOperator=new ArrayList<String>();
        List<String> whereInputName=new ArrayList<String>();
        List<String> whereComparator=new ArrayList<String>();

        List<String> selectFieldList=new ArrayList<String>();
        List<String> fromTableList=new ArrayList<String>();
        List<String> whereFieldList=new ArrayList<String>();
        List<String> groupByList=new ArrayList<String>();

        List<String> multipleSelectedItem=new ArrayList<String>();
      %>
      <form action="/icici/servlet/RiskRuleDetails?ctoken=<%=ctoken%>" method="post" name="forms" >
        <input type="hidden" value="<%=ctoken%>" name="ctoken">
        <table  align="center" width="100%" cellpadding="2" cellspacing="2" style="margin-left:2.5%;margin-right: 2.5% ">

          <tr>
            <td>

              <table border="0" cellpadding="5" cellspacing="0" width="100%"  align="center">


                <tr><td colspan="8">&nbsp;</td></tr>
                <tr>
                  <td width="2%" class="textb">&nbsp;</td>
                  <td width="11%" class="textb" >Rule Name</td>
                  <td width="3%" class="textb"></td>
                  <td width="12%" class="textb">
                    <select name="ruleid" class="txtbox">
                      <%=profileManagementManager.getOptionTagForRiskRuleWithoutId(riskRule, request.getParameter("ruleid")).toString()%>
                    </select>
                  </td>
                  <td width="6%" class="textb">&nbsp;</td>
                  <td width="11%" class="textb" ></td>
                  <td width="3%" class="textb"><button type="submit" class="buttonform" name="action">
                    <i class="fa fa-clock-o"></i>
                    &nbsp;&nbsp;Search
                  </button></td>
                  <td width="10%" class="textb">

                  </td>

                </tr>

                <td width="4%" class="textb">&nbsp;</td>

                </tr>
              </table>
            </td>
          </tr>
        </table>
      </form>

    </div>
  </div>
</div>
<div class="reporttable">
  <%
    if(request.getAttribute("ruleVO")!=null)
     ruleVO= (RuleVO) request.getAttribute("ruleVO");
    profileManagementManager.getOptionTagForDatabaseRuleType(payIfeTableInfoMap,tableAlliasName,functions.isValueNull(ruleVO.getQuery())?ruleVO.getQuery():(functions.isValueNull(request.getParameter("businessRuleQuery"))?request.getParameter("businessRuleQuery"):null),selectOptionTag,fromOptionTag,whereOptionTag,groupOptionTag,payIFETableArray,aggreGateFunction,joinList,onList,whereOperator,whereInputName,whereComparator,selectFieldList,fromTableList,whereFieldList,groupByList);
    actionVO= (ActionVO) request.getAttribute("actionVO");

    if(functions.isValueNull(ruleVO.getRuleType()) && (RuleTypeEnum.COMPARATOR.name()).equals(ruleVO.getRuleType()) && ruleVO.getRuleOperation()!=null)
    {
      int i=1;
      for(RuleOperation ruleOperation:ruleVO.getRuleOperation())
      {

        multipleSelectedItem.add(ruleOperation.getInputName());
        comparatorComparator.append("<tr>");
        comparatorComparator.append("<td class=\"textb\" valign=\"middle\" >");
        comparatorComparator.append("<input name='comparatorInputName_"+i+"' class='txtbox' value='"+ruleOperation.getInputName()+"'  "+((actionVO.isView())? "readonly style='background-color: #EBEBE4'":"readonly")+">");
        comparatorComparator.append("</td>");
        comparatorComparator.append("<td class='textb' valign='middle'>");
        comparatorComparator.append("<select name='comparatorOperator_"+i+"' class='txtbox' style='background-color:#EBEBE4;width:100px' "+((actionVO.isView()) ?"disabled style='background-color: #EBEBE4'":"")+" onChange=\"enableText('comparatorOperator_"+i+"','riskRuleValue1_"+i+"','riskRuleValue2_"+i+"')\">"+(profileManagementManager.getOptionTagforOperators(ruleOperation.getOperator()))+"</select>");
        comparatorComparator.append("</td>");
        comparatorComparator.append("<td class=\"textb\" valign=\"middle\" >");
        comparatorComparator.append("<input name='riskRuleValue1_"+i+"' class='txtbox' value='"+(functions.isValueNull(ruleOperation.getValue1())?ruleOperation.getValue1():"")+"'  "+(((actionVO.isView()) )? "readonly style='background-color: #EBEBE4'":"")+">");
        comparatorComparator.append("</td>");
        comparatorComparator.append("<td class='textb' valign='middle'>");
        comparatorComparator.append("<input name='riskRuleValue2_"+i+"' class='txtbox' value='"+(functions.isValueNull(ruleOperation.getValue2())?ruleOperation.getValue2():"")+"'  "+(((actionVO.isView()) || !PZOperatorEnums.BETWEEN.name().equals(ruleOperation.getOperator()) )? "readonly style='background-color: #EBEBE4'":"")+">");
        comparatorComparator.append("</td>");

        if(i<ruleVO.getRuleOperation().size())
        {
          comparatorComparator.append("<td class=\"textb\" valign=\"middle\" colspan=\"1\">");
          comparatorComparator.append("<select name='comparatorComparator_"+i+"' class=\"txtbox\" style='background-color:#EBEBE4;width:100px' "+((actionVO.isView()) ?"disabled style='background-color: #EBEBE4":"")+">"+(profileManagementManager.getOptionTagForComparator(ruleOperation.getComparator(),null))+"</select>");
          comparatorComparator.append("</td>");
        }
        else
        {
          comparatorComparator.append("<td class=\"textb\" valign=\"middle\" colspan=\"1\">");
          comparatorComparator.append("</td>");
        }
        comparatorComparator.append("</tr>");
        i++;
      }

    }
    else if(functions.isValueNull(ruleVO.getRuleType()) && (RuleTypeEnum.FLAT_FILE.name()).equals(ruleVO.getRuleType()) && ruleVO.getRuleOperation()!=null)
    {
      int i=1;
      for(RuleOperation ruleOperation:ruleVO.getRuleOperation())
      {
        multipleSelectedItem.add(ruleOperation.getInputName());
        flat_FileComparator.append("<tr>");
        flat_FileComparator.append("<td class=\"textb\" valign=\"middle\" colspan=\"2\">");
        flat_FileComparator.append("<input name='flatFileInputName_"+i+"' class='txtbox' value='"+ruleOperation.getInputName()+"'  "+((actionVO.isView())? "readonly style='background-color: #EBEBE4'":"readonly")+">");
        flat_FileComparator.append("</td>");
        flat_FileComparator.append("<td class='textb' align='center' valign='middle' colspan='"+(i < ruleVO.getRuleOperation().size() ?"1":"1")+"'>");
        flat_FileComparator.append("<select name='flatFileOperator_"+i+"' class='txtbox' style='background-color:#EBEBE4;width:100px' "+((actionVO.isView()) ?"disabled style='background-color: #EBEBE4'":"")+">"+(profileManagementManager.getOptionTagforOperators(ruleOperation.getOperator()))+"</select>");
        flat_FileComparator.append("</td>");
        flat_FileComparator.append("<td class='textb' align='center' valign='middle' colspan='"+(i < ruleVO.getRuleOperation().size() ?"1":"1")+"'>");
        flat_FileComparator.append("<input name='riskRuleValue1_"+i+"' class='txtbox' value='"+(functions.isValueNull(ruleOperation.getValue1())?ruleOperation.getValue1():"")+"'  "+(((actionVO.isView()) )? "readonly style='background-color: #EBEBE4'":"")+">");
        flat_FileComparator.append("</td>");
        if(i<ruleVO.getRuleOperation().size())
        {
          flat_FileComparator.append("<td class=\"textb\" valign=\"middle\" colspan=\"1\">");
          flat_FileComparator.append("<select name='flatFileComparator_"+i+"' class=\"txtbox\" style='background-color:#EBEBE4;width:100px' "+((actionVO.isView()) ?"disabled style='background-color: #EBEBE4":"")+">"+(profileManagementManager.getOptionTagForComparator(ruleOperation.getComparator(),null))+"</select>");
          flat_FileComparator.append("</td>");
        }
        else
        {
          flat_FileComparator.append("<td class=\"textb\" valign=\"middle\" colspan=\"1\">");
          flat_FileComparator.append("</td>");
        }
        flat_FileComparator.append("</tr>");
        i++;
      }

    }
    else if(functions.isValueNull(ruleVO.getRuleType()) && (RuleTypeEnum.REGULAR_EXPRESSION.name()).equals(ruleVO.getRuleType()) && ruleVO.getRuleOperation()!=null)
    {
      int i=1;
      for(RuleOperation ruleOperation:ruleVO.getRuleOperation())
      {
        multipleSelectedItem.add(ruleOperation.getInputName());
        regexComparator.append("<tr>");

        regexComparator.append("<td class='textb' valign='middle' colspan='"+(i < ruleVO.getRuleOperation().size() ?"1":"1")+"'>");
        regexComparator.append("<input name='regexInputName_"+i+"' class=\"txtbox\" value='"+ruleOperation.getInputName()+"' readonly>");
        regexComparator.append("</td>");

        regexComparator.append("<td class=\"textb\" valign=\"middle\">");
        regexComparator.append("<select name=\"regexOperator_"+i+"\" class=\"txtbox\"  "+((actionVO.isView())?"disabled  style='background-color: #EBEBE4'":"")+">"+profileManagementManager.getOptionTagforOperators(ruleOperation.getOperator())+"</select>");
        regexComparator.append("</td>");

        regexComparator.append("<td class=\"textb\" valign=\"middle\">");
        regexComparator.append("<input name='regex_"+i+"' class=\"txtbox\" placeholder=\"REGEX\" value='"+ruleOperation.getRegex()+"'  "+((actionVO.isView())? "readonly style='background-color: #EBEBE4'":"")+">");
        regexComparator.append("</td>");

        regexComparator.append("<td class=\"textb\" valign=\"middle\">");
        regexComparator.append("<input type=\"checkbox\" name=\"isMandatory_"+i+"\"  value='Y' "+(ruleOperation.isMandatory()?"checked":"")+" "+((actionVO.isView())? "disabled style='background-color: #EBEBE4'":"")+"/>");
        regexComparator.append("</td>");
        if(i<ruleVO.getRuleOperation().size())
        {
          regexComparator.append("<td class=\"textb\" valign=\"middle\" colspan=\"1\">");
          regexComparator.append("<select name='regexComparator_"+i+"' class=\"txtbox\" style='background-color:#EBEBE4;width:100px' "+((actionVO.isView()) ?"disabled style='background-color: #EBEBE4":"")+">"+(profileManagementManager.getOptionTagForComparator(ruleOperation.getComparator(),null))+"</select>");
          regexComparator.append("</td>");
        }
        else
        {
          regexComparator.append("<td class=\"textb\" valign=\"middle\" colspan=\"1\">");
          regexComparator.append("</td>");
        }
        regexComparator.append("</tr>");
        i++;
      }

    }
    else
    {
      if (functions.isValueNull(ruleVO.getRuleType()) && (RuleTypeEnum.DATABASE.name()).equals(ruleVO.getRuleType()) && ruleVO.getRuleOperation() != null)
      {
        int i = 1;
        int s = 1;
        int f = 1;
        int w = 1;
        int g = 1;
        for (String selectedField : selectFieldList)
        {
          selectQuery.append("<tr>");
          selectQuery.append("<td class=\"textb\" valign=\"middle\">" + (s == 1 ? "Select" : "") + "</td>");
          selectQuery.append("<td class=\"textb\" valign=\"middle\" colspan=\"2\">");
          selectQuery.append("<select name=\"selectAggregate_" + s + "\" class=\"txtbox\" style=\"background-color:#EBEBE4;width:100px\">" + profileManagementManager.getOptionTagForAggregateFunctionsQuery(aggreGateFunction.get(s - 1) != null ? aggreGateFunction.get(s - 1).toString() : null, null) + "</select>");
          selectQuery.append("</td>");
          selectQuery.append("<td class=\"textb\" valign=\"middle\" colspan=\"3\">");
          selectQuery.append("<input type=\"text\" name=\"selectField_" + s + "\" class=\"textb\" value=\"" + selectedField + "\" readonly>");
          selectQuery.append("</td>");

          selectQuery.append("</tr>");
          s++;
        }

        for (String fromTable : fromTableList)
        {
          fromQuery.append("<tr>");
          fromQuery.append("<td class=\"textb\" valign=\"middle\">" + (f == 1 ? "From" : "") + "</td>");
          fromQuery.append("<td class=\"textb\" valign=\"middle\" colspan=\"2\">");
          fromQuery.append("<input type=\"text\" name=\"fromTable_" + f + "\" class=\"textb\" value=\"" + fromTable + "\" readonly>");
          fromQuery.append("</td>");
          fromQuery.append("<td class=\"textb\" valign=\"middle\" colspan=\"3\">");
          if (f < fromTableList.size())
          {
            fromQuery.append("<select name=\"fromJoin_" + f + "\" class=\"txtbox\" style=\"background-color:#EBEBE4;width:100px\">" + profileManagementManager.getOptionTagForJoinTableQuery(joinList.get(f - 1) != null ? joinList.get(f - 1) : null) + "</select>");
          }
          fromQuery.append("</td>");
          fromQuery.append("</tr>");
          if (f > 1)
          {
            fromQuery.append("<tr>");
            fromQuery.append("<td class=\"textb\" valign=\"middle\">ON</td>");
            fromQuery.append("<td class=\"textb\" valign=\"middle\">");
            fromQuery.append("<select name=\"onLeft_" + (f - 1) + "\" id=\"onLeft_" + (f - 1) + "\" class=\"txtbox\" style=\"background-color:#EBEBE4;width:100px\"><option value=''>Select Field</option>" + selectOptionTag + "</select>");
            fromQuery.append("</td>");
            fromQuery.append("<td class=\"textb\" valign=\"middle\" align='center'>=</td>");
            fromQuery.append("<td class=\"textb\" valign=\"middle\" colspan='3'>");
            fromQuery.append("<select name=\"onRight_" + (f - 1) + "\" id=\"onRight_" + (f - 1) + "\" class=\"txtbox\" style=\"background-color:#EBEBE4;width:100px\"><option value=''>Select Field</option>" + selectOptionTag + "</select>");
            fromQuery.append("</td>");
            fromQuery.append("</tr>");
          }
          f++;
        }


        for (String whereField : whereFieldList)
        {
          List<String> whereSingleInput = new ArrayList<String>();
          boolean isFreetextSelected = false;

          if (whereInputName.get(w - 1) != null)
          {
            whereSingleInput.add(whereInputName.get(w - 1));
          }
          String operatorName = null;
          if (whereOperator.get(w - 1) != null)
          {
            String operator = whereOperator.get(w - 1);
            if ((PZOperatorEnums.EQUAL_TO.toString().replaceFirst("=", "")).equals(operator))
            {
              operatorName = PZOperatorEnums.EQUAL_TO.name();
            }
            else if ((PZOperatorEnums.LESS_THAN.toString()).equals(operator))
            {
              operatorName = PZOperatorEnums.LESS_THAN.name();
            }
            else if ((PZOperatorEnums.LESS_THAN_EQUALS_TO.toString()).equals(operator))
            {
              operatorName = PZOperatorEnums.LESS_THAN_EQUALS_TO.name();
            }
            else if ((PZOperatorEnums.GREATER_THAN.toString()).equals(operator))
            {
              operatorName = PZOperatorEnums.GREATER_THAN.name();
            }
            else if ((PZOperatorEnums.GREATER_THAN_EQUALS_TO.toString()).equals(operator))
            {
              operatorName = PZOperatorEnums.GREATER_THAN_EQUALS_TO.name();
            }
            else if ((PZOperatorEnums.BETWEEN.toString()).equals(operator))
            {
              operatorName = PZOperatorEnums.BETWEEN.name();
            }


          }
          whereQuery.append("<tr>");
          whereQuery.append("<td class=\"textb\" valign=\"middle\">" + (w == 1 ? "Where" : "") + "</td>");
          whereQuery.append("<td class=\"textb\" valign=\"middle\" >");
          whereQuery.append("<input type=\"text\" name=\"whereField_" + w + "\" class=\"textb\" value=\"" + whereField + "\" readonly>");
          whereQuery.append("</td>");
          whereQuery.append("<td class=\"textb\" valign=\"middle\" >");
          whereQuery.append("<select name=\"whereOperator_" + w + "\" class=\"txtbox\" style=\"background-color:#EBEBE4;width:100px\">" + profileManagementManager.getOptionTagForOperatorsForSymbol(operatorName).toString().replace("<option value=''>Select a Operator</option>", "").replaceFirst("==", "=") + "</select>");
          whereQuery.append("</td>");
          if (payIfeTableInfoMap != null && payIfeTableInfoMap.containsKey(whereField) && ("Y".equals(request.getParameter("isFreeText_" + w)) || (whereSingleInput.size() > 0 && InputNameEnum.getEnum(whereSingleInput.get(0)) == null)))
          {
            isFreetextSelected = true;
            PayIfeTableInfo payIfeTableInfo = payIfeTableInfoMap.get(whereField);
            whereQuery.append("<td class=\"textb\" valign=\"middle\">");
            whereQuery.append("<table width=\"100%\" height=\"100%\" id=\"inputNameWhereTable_" + w + "\"><tr><td>");
            if (DataType.ENUM.name().equals(payIfeTableInfo.getDataType()) && functions.isValueNull(payIfeTableInfo.getEnumValue()))
            {
              whereQuery.append("<select name=\"whereInputFieldFreeText_" + w + "\" class=\"txtbox\" style=\"background-color:#EBEBE4;width:100px\">");
              String[] singlePayInfo = payIfeTableInfo.getEnumValue().split(",");
              for (int inner = 0; inner < singlePayInfo.length; inner++)
              {
                whereQuery.append("<option value=\"" + singlePayInfo[inner] + "\" " + (singlePayInfo[inner].equals(request.getParameter("whereInputFieldFreeText_" + w)) || whereSingleInput.contains(singlePayInfo[inner]) ? "selected" : "") + ">" + singlePayInfo[inner] + "</option>");
              }
              whereQuery.append("</select>");
            }
            else
            {
              whereQuery.append("<input name=\"whereInputField_" + w + "\" class=\"txtbox\" value=\"" + whereSingleInput.get(0) + "\">");
            }
            whereQuery.append("</td></tr></table>");
          }
          else
          {
            whereQuery.append("<td class=\"textb\" valign=\"middle\">");
            whereQuery.append("<table width=\"100%\" height=\"100%\" id=\"inputNameWhereTable_" + w + "\"><tr><td><select name=\"whereInputField_" + w + "\" class=\"txtbox\" style=\"background-color:#EBEBE4;width:100px\">" + profileManagementManager.getOptionTagForInputName(whereSingleInput, null) + "</select></td></tr></table>");
            whereQuery.append("</td>");
          }
          whereQuery.append("<td class=\"textb\" valign=\"middle\">");
          whereQuery.append("<input type=\"checkbox\" name=\"isFreeText_" + w + "\" value=\"Y\" onclick=\"getFreeText(this.checked,'" + w + "','" + whereField + "')\" " + (isFreetextSelected ? "checked" : "") + ">");
          whereQuery.append("</td>");
          whereQuery.append("<td class=\"textb\" valign=\"middle\" align=\"center\">");
          if (w < whereFieldList.size())
            whereQuery.append("<select name=\"whereComparator_" + w + "\" class=\"txtbox\" style=\"background-color:#EBEBE4;width:100px\">" + profileManagementManager.getOptionTagForComparator(whereComparator.get(w - 1) != null ? whereComparator.get(w - 1) : null, null) + "</select>");
          whereQuery.append("</td>");
          whereQuery.append("</tr>");
          w++;
        }

        for (String groupField : groupByList)
        {
          groupByQuery.append("<tr>");
          groupByQuery.append("<td class=\"textb\" valign=\"middle\">" + (g == 1 ? "Group By" : "") + "</td>");
          groupByQuery.append("<td class=\"textb\" valign=\"middle\" colspan=\"5\">");
          groupByQuery.append("<input type=\"text\" name=\"groupBy_" + g + "\" class=\"textb\" value=\"" + groupField + "\" readonly>");
          groupByQuery.append("</td>");
          groupByQuery.append("</tr>");
          g++;

        }
        buttonQuery.append("<tr>");
        buttonQuery.append("<td class=\"textb\" valign=\"middle\" align=\"center\" colspan=\"6\">");
        buttonQuery.append("<button type=\"button\" id=\"processFinalQuery\" class=\"buttonform\"  " + (!actionVO.isView() ? "onclick=\"processQuery()\"" : "") + ">Final&nbsp;Query</button>");
        buttonQuery.append("</td>");
        buttonQuery.append("</tr>");


        for (RuleOperation ruleOperation : ruleVO.getRuleOperation())
        {

          multipleSelectedItem.add(ruleOperation.getInputName());
          databaseComparator.append("<tr>");
          databaseComparator.append("<td class=\"textb\" valign=\"middle\" colspan=\"1\">");
          databaseComparator.append("<input name='select_" + i + "' class='txtbox' value='" + ruleOperation.getInputName() + "'  " + ((actionVO.isView()) ? "readonly style='background-color: #EBEBE4'" : "readonly") + ">");
          databaseComparator.append("</td>");
          databaseComparator.append("<td class='textb' valign='middle' colspan='" + (i < ruleVO.getRuleOperation().size() ? "1" : "1") + "'>");
          databaseComparator.append("<select name='selectOperator_" + i + "' class='txtbox' style='background-color:#EBEBE4;width:100px' " + ((actionVO.isView()) ? "disabled style='background-color: #EBEBE4'" : "") + " onChange=\"enableText('selectOperator_" + i + "','riskRuleValue1_" + i + "','riskRuleValue2_" + i + "')\" >" + (profileManagementManager.getOptionTagforOperators(ruleOperation.getOperator())) + "</select>");
          databaseComparator.append("</td>");

          databaseComparator.append("<td class='textb' valign='middle' colspan='" + (i < ruleVO.getRuleOperation().size() ? "1" : "1") + "'>");
          databaseComparator.append("<input type=\"text\" name=\"riskRuleValue1_" + i + "\" class=\"txtbox\"  value='" + (functions.isValueNull(ruleOperation.getValue1())? ruleOperation.getValue1() : "") + "' " + ((actionVO.isView()) ? "readonly style='background-color: #EBEBE4'" : "") + "/>");
          databaseComparator.append("</td>");
          databaseComparator.append("<td class='textb' valign='middle' colspan='" + (i < ruleVO.getRuleOperation().size() ? "1" : "1") + "'>");
          databaseComparator.append("<input type=\"text\" name=\"riskRuleValue2_" + i + "\" class=\"txtbox\"  value='" + (functions.isValueNull(ruleOperation.getValue2())? ruleOperation.getValue2() : "") + "' " + ((actionVO.isView() || !PZOperatorEnums.BETWEEN.name().equals(ruleOperation.getOperator())) ? "readonly style='background-color: #EBEBE4'" : "") + "/>");
          databaseComparator.append("</td>");

          databaseComparator.append("<td class='textb' valign='middle' colspan='" + (i < ruleVO.getRuleOperation().size() ? "1" : "1") + "'>");
          databaseComparator.append("<input type=\"checkbox\" name=\"isMandatory_" + i + "\"  value='Y' " + (ruleOperation.isMandatory() ? "checked" : "") + "/>");
          databaseComparator.append("</td>");
          if (i < ruleVO.getRuleOperation().size())
          {
            databaseComparator.append("<td class=\"textb\" valign=\"middle\" colspan=\"1\">");
            databaseComparator.append("<select name='selectComparator_" + i + "' class=\"txtbox\" style='background-color:#EBEBE4;width:100px' " + ((actionVO.isView()) ? "disabled style='background-color: #EBEBE4" : "") + ">" + (profileManagementManager.getOptionTagForComparator(ruleOperation.getComparator(), null)) + "</select>");
            databaseComparator.append("</td>");

          }
          else
          {
            databaseComparator.append("<td class=\"textb\" valign=\"middle\" colspan=\"1\">");
            databaseComparator.append("</td>");
          }
          databaseComparator.append("</tr>");
          i++;
        }

      }
    }

  %>
  <form action="/icici/servlet/AddRiskRuleDetails?ctoken=<%=ctoken%>" method="post" name="myForm">

    <table align=center width="80%">
      <tr>
        <td colspan="2"><center><B>
          <%if (actionVO.isView())
          {%>
          View Risk Rule Details
          <%
          }
          else if (actionVO.isEdit())
          {
          %>
          Edit Risk Rule Details
          <%
          }
          else if(actionVO.isAdd())
          {
          %>
          Add New Risk Rule Details
          <%
            }
          %>
        </B></center> </td>
      </tr>
      <tr><td>&nbsp;</td></tr>

      <tr class="tr0">
        <td valign="middle" align="center">Risk&nbsp;Rule&nbsp;Name</td>
        <td>
          <input name="riskRuleName" class="txtbox" <%if(actionVO.isView()){%>readonly style="background-color: #EBEBE4" <%}%> value="<%=functions.isValueNull(ruleVO.getName())?ruleVO.getName():""%>">

        </td>
      </tr>
      <tr><td>&nbsp;</td></tr>
      <tr>
        <td valign="middle" align="center" class="textb">Risk&nbsp;Rule&nbsp;Label</td>
        <td class="textb">
          <input type="text" class="txtbox" name="riskRuleLabel" value="<%=functions.isValueNull(ruleVO.getLabel())?ESAPI.encoder().encodeForHTMLAttribute(ruleVO.getLabel()):""%>" <%if(actionVO.isView()){%>readonly style="background-color: #EBEBE4" <%}%>>
        </td>
      </tr>
      <tr><td>&nbsp;</td></tr>
      <tr class="tr0">
        <td valign="middle" align="center">Risk&nbsp;Rule&nbsp;Description</td>
        <td>
          <input type="text" class="txtbox" name="riskRuleDescription" value="<%=functions.isValueNull(ruleVO.getDescription())?ESAPI.encoder().encodeForHTMLAttribute(ruleVO.getDescription()):""%>"  <%if(actionVO.isView()){%>readonly style="background-color: #EBEBE4" <%}%>>
        </td>
      </tr>
      <tr><td>&nbsp;</td></tr>
      <tr>
        <td valign="middle" align="center" class="textb">Risk&nbsp;Rule&nbsp;Type</td>
        <td class="textb">
          <select  class="txtbox" name="ruleType" <%if(actionVO.isView()){%>disabled <%}%> onchange="selectedDropdown('ruleType')" > <%=profileManagementManager.getOptionTagForRuleType(functions.isValueNull(ruleVO.getRuleType()) ? ESAPI.encoder().encodeForHTMLAttribute(ruleVO.getRuleType()) : (functions.isValueNull(request.getParameter("ruleType")) ? ESAPI.encoder().encodeForHTMLAttribute(request.getParameter("ruleType")) : ""))%></select>
        </td>
      </tr>
      <tr><td>&nbsp;</td></tr>

      <tr class="tr0">
        <td valign="middle" align="center" colspan="2">
          <div id="comparator" align="center" style="<%=((functions.isValueNull(ruleVO.getRuleType()) && "COMPARATOR".equals(ruleVO.getRuleType())) || (functions.isValueNull(request.getParameter("ruleType"))  && "COMPARATOR".equals(request.getParameter("ruleType"))))?"":"display:none;"%>">
            <table width="100%"  align="center">
              <tr>
                <td valign="middle" align="center" class="textb" width="45%" >Input&nbsp;Name</td>
                <td valign="middle" width="50%">
                  <select name="comparatorInputName" class="textb" multiple id="comparatorInputName" <%=(actionVO.isView())? "disabled style=\"background-color: #EBEBE4\"":"" %>>
                    <%=profileManagementManager.getOptionTagForInputName(multipleSelectedItem, null).toString().replaceAll("<option value=''>Select Input Name</option>", "")%>
                  </select>
                </td>
              </tr>
              <tr><td>&nbsp;</td></tr>
              <tr>
                <td valign="middle" align="center" class="textb" colspan="2" ><button type="button" name="Process" <%=!actionVO.isView()?"id=\"processComparator\"":""%> value="Y" class="buttonform">Process</button></td>
              </tr>
              <tr><td>&nbsp;</td></tr>
              <tr>
                <td valign="middle" align="center" class="textb" colspan="2" >
                  <div id="processFinalComparatorDiv" align="center" style="<%=(actionVO!=null&&(!actionVO.isAdd() || ((request.getAttribute("error")!=null || request.getAttribute("catchError")!=null) && RuleTypeEnum.COMPARATOR.name().equals(request.getParameter("ruleType")))))?"":"display:none;"%>">
                    <table width="100%" id="processFinalComparatorTable" align="center"  class="table table-striped table-bordered table-green dataTable">
                      <tr>
                        <td valign="middle" align="center" class="th0" width="25%" colspan="6" >Comparator</td>
                      </tr>
                      <%=comparatorComparator%>
                    </table>


                  </div>
                </td>
              </tr>

              <tr><td>&nbsp;</td></tr>
            </table>
          </div>
          <div id="flatfile" align="center" style="<%=((functions.isValueNull(ruleVO.getRuleType()) || functions.isValueNull(request.getParameter("ruleType"))) && ( "FLAT_FILE".equals(ruleVO.getRuleType())  || "FLAT_FILE".equals(request.getParameter("ruleType"))) )?"":"display:none;"%>">
            <table width="100%"  align="center">
              <tr>
                <td valign="middle" align="center" class="textb" width="45%" >Input&nbsp;Name</td>
                <td valign="middle" width="50%">
                  <select  name="flatFileInputName" id="flatFileInputName" multiple class="txtbox" <%=(actionVO.isView())? "disabled style=\"background-color: #EBEBE4\"":"" %>>
                    <%=profileManagementManager.getOptionTagForInputName(multipleSelectedItem, null).toString().replaceAll("<option value=''>Select Input Name</option>", "")%>
                  </select>
                </td>
              </tr>
              <tr><td>&nbsp;</td></tr>
              <tr>
                <td valign="middle" align="center" class="textb" colspan="2" ><button type="button" name="Process" <%=!actionVO.isView()?"id=\"processFlatFile\"":""%> value="Y" class="buttonform">Process</button></td>
              </tr>
              <tr><td>&nbsp;</td></tr>
              <tr>
                <td valign="middle" align="center" class="textb" colspan="2" >
                  <div id="processFlatFileDIV" align="center" style="<%=(actionVO!=null&&(!actionVO.isAdd() || ((request.getAttribute("error")!=null || request.getAttribute("catchError")!=null) && RuleTypeEnum.FLAT_FILE.name().equals(request.getParameter("ruleType")))))?"":"display:none;"%>">
                    <table width="100%" id="processFlatFileTable" align="center"  class="table table-striped table-bordered table-green dataTable">
                      <tr>
                        <td valign="middle" align="center" class="th0" width="25%" colspan="6" >Flat&nbsp;File</td>
                      </tr>
                      <%=flat_FileComparator%>
                    </table>

                  </div>
                </td>
              </tr>
              <tr><td>&nbsp;</td></tr>
            </table>
          </div>
          <div id="regex" align="center" style="<%=((functions.isValueNull(ruleVO.getRuleType()) || functions.isValueNull(request.getParameter("ruleType"))) && ("REGULAR_EXPRESSION".equals(ruleVO.getRuleType()) || "REGULAR_EXPRESSION".equals(request.getParameter("ruleType"))))?"":"display:none;"%>">
            <table width="100%"  align="center">
              <tr>
                <td valign="middle" align="center" class="textb" width="45%" >Input&nbsp;Name</td>
                <td valign="middle" width="50%">
                  <select  name="regexInputName" class="txtbox" multiple id="regexInputName" <%=(actionVO.isView())? "disabled style=\"background-color: #EBEBE4\"":"" %> >
                    <%=profileManagementManager.getOptionTagForInputName(multipleSelectedItem, null).toString().replaceAll("<option value=''>Select Input Name</option>", "")%>
                  </select>
                </td>
              </tr>
              <tr><td>&nbsp;</td></tr>
              <tr>
                <td valign="middle" align="center" class="textb" colspan="2" ><button type="button" name="ProcessRegex" <%=!actionVO.isView()?"id=\"processRegex\"":""%> value="Y" class="buttonform">Process</button></td>
              </tr>
              <tr><td>&nbsp;</td></tr>
              <tr>
                <td valign="middle" align="center" class="textb" colspan="2" >
                  <div id="processRegexDIV" align="center" style="<%=(actionVO!=null&&(!actionVO.isAdd() || ((request.getAttribute("error")!=null || request.getAttribute("catchError")!=null) && RuleTypeEnum.REGULAR_EXPRESSION.name().equals(request.getParameter("ruleType")))))?"":"display:none;"%>">
                    <table width="100%" id="processRegexTable" align="center"  class="table table-striped table-bordered table-green dataTable">
                      <tr>
                        <td valign="middle" align="center" class="th0" width="25%" colspan="6" >Regex</td>
                      </tr>
                      <%=regexComparator%>
                    </table>


                  </div>
                </td>
              </tr>
              <tr><td>&nbsp;</td></tr>
            </table>
          </div>
          <div id="query" align="center" style="<%=((functions.isValueNull(ruleVO.getRuleType()) || functions.isValueNull(request.getParameter("ruleType")))&& ("DATABASE".equals(ruleVO.getRuleType()) || "DATABASE".equals(request.getParameter("ruleType"))))?"":"display:none;"%>">
            <table width="100%"  align="center">
              <tr>
                <td valign="middle" align="center" class="textb" width="25%" >Select</td>
                <td valign="middle" width="25%">
                  <select multiple class="textb" id="select" name="querySelect" <%=(actionVO.isView())? "disabled style=\"background-color: #EBEBE4\"":"" %>>
                    <%=selectOptionTag%>
                  </select>
                </td>
                <td valign="middle" align="center" class="textb" width="25%">From</td>
                <td valign="middle" width="25%">
                  <select multiple class="textb" id="from" <%=(actionVO.isView())? "disabled style=\"background-color: #EBEBE4\"":"" %>>
                    <%=fromOptionTag%>
                  </select>
                </td>
              </tr>
              <tr><td>&nbsp;</td></tr>
              <tr>
                <td valign="middle" align="center" class="textb" width="25%" >Where</td>
                <td valign="middle" width="25%">
                  <select multiple class="textb" id="where" <%=(actionVO.isView())? "disabled style=\"background-color: #EBEBE4\"":"" %>>
                    <%=whereOptionTag%>
                  </select>
                </td>
                <td valign="middle" align="center" class="textb" width="25%">Group&nbsp;By</td>
                <td valign="middle" width="25%">
                  <select multiple class="textb" id="group" <%=(actionVO.isView())? "disabled style=\"background-color: #EBEBE4\"":"" %>>
                    <%=groupOptionTag%>
                  </select>
                </td>
              </tr>
              <tr><td>&nbsp;</td></tr>
              <tr><td valign="middle" align="center" class="textb" colspan="4"><button type="button" <%=!actionVO.isView()?"id=\"process\"":""%>  value="Y" class="buttonform">Process</button></td></tr>
              <tr><td>&nbsp;</td></tr>
              <tr><td colspan="4">
                <div id="processQuery" align="center" style="<%=(actionVO!=null&&(!actionVO.isAdd() || ((request.getAttribute("error")!=null || request.getAttribute("catchError")!=null) && RuleTypeEnum.DATABASE.name().equals(request.getParameter("ruleType")))))?"":"display:none;"%>">
                  <table width="100%"  align="center" id="processQueryTable" class="table table-striped table-bordered table-green dataTable">
                    <tr>
                      <td valign="middle" align="center" class="th0" width="25%" colspan="6" >Query&nbsp;Operations</td>
                    </tr>
                    <%=selectQuery%>
                    <%=fromQuery%>
                    <%=whereQuery%>
                    <%=groupByQuery%>
                    <%=buttonQuery%>
                  </table>


                </div>
                <div id="processFinalQueryDiv" align="center" style="<%=(actionVO!=null&&(!actionVO.isAdd() || ((request.getAttribute("error")!=null || request.getAttribute("catchError")!=null) && RuleTypeEnum.DATABASE.name().equals(request.getParameter("ruleType")) && functions.isValueNull(request.getParameter("businessRuleQuery")))))?"":"display:none;"%>">
                  <table width="100%" id="processFinalQueryTable" align="center"  class="table table-striped table-bordered table-green dataTable">
                    <tr>
                      <td valign="middle" align="center" class="th0" width="25%" colspan="6" >Final&nbsp;Query</td>
                    </tr>
                    <tr>
                      <td valign="middle" align="center"  width="25%" colspan="6" ><textarea type="text" id="businessRuleQuery" name="businessRuleQuery" class="txtbox"   style="background-color:#EBEBE4;font-size: 12px;min-width:200px;min-height:100px;max-height: 300px;max-width: 500px" readonly><%=(ruleVO!=null && RuleTypeEnum.DATABASE.name().equals(ruleVO.getRuleType()) && functions.isValueNull(ruleVO.getQuery()))?ruleVO.getQuery():(functions.isValueNull(request.getParameter("businessRuleQuery"))?request.getParameter("businessRuleQuery"):"")%></textarea></td>
                    </tr>
                    <%=databaseComparator%>
                  </table>


                </div>
              </td></tr>
            </table>
          </div>
        </td>
      </tr>
      <tr><td>&nbsp;</td></tr>

      <%
        if(actionVO.isEdit())
        {
      %>
      <tr class="tr0">
        <td ><button type="submit" class="buttonform" id="submit" name="action" value="<%=ruleVO.getId()%>_Edit" style="float: right">Update</button></td>
        <td class="textb" valign="middle" align="center" style="padding-left: 10px;">
          <button type="submit" class="buttonform" name="ruleid" style="float:left" VALUE="<%=functions.isValueNull(request.getParameter("ruleid"))?request.getParameter("ruleid"):""%>" onclick="cancel('<%=ctoken%>')">
            <i class="fa fa-clock-o"></i>
            &nbsp;&nbsp;Cancel
          </button>
        </td>
      </tr>
      <%
      }
      else if(actionVO.isAdd())
      {
      %>
      <tr class="tr0">
        <td  style="margin-right: 10%"><button type="submit" id="submit" style="float:right" class="buttonform" name="action" value="1_Add">Add</button></td>
        <td class="textb" style="padding-left: 10px;">
          <button type="submit" class="buttonform" name="ruleid"  style="float:left" VALUE="<%=functions.isValueNull(request.getParameter("ruleid"))?request.getParameter("ruleid"):""%>" onclick="cancel('<%=ctoken%>')">
            <i class="fa fa-clock-o"></i>
            &nbsp;&nbsp;Cancel
          </button>
        </td>
      </tr>

      <%
        }
        else if(actionVO.isView())
        {
      %>
      <tr>
        <td colspan="2" class="textb" valign="middle" align="center">
          <button type="submit" class="buttonform" name="ruleid"  VALUE="<%=functions.isValueNull(request.getParameter("ruleid"))?request.getParameter("ruleid"):""%>" onclick="cancel('<%=ctoken%>')">
            <i class="fa fa-clock-o"></i>
            &nbsp;&nbsp;Cancel
          </button>
        </td>
      </tr>
      <%
        }
      %>
    </table>
  </form>

</div>
<script>
  <%=payIFETableArray.toString()%>
  $('#processFlatFile').click(function()
  {
    document.getElementById("processFlatFileDIV").style.display = "block";
    var tbl = document.getElementById('processFlatFileTable');
    var lastRow = tbl.rows.length;
    // if there's no header row in the table, then iteration = lastRow + 1

    for(var i=lastRow-1;i>0;i--)
    {
      tbl.deleteRow(i);
    }

    lastRow=  tbl.rows.length;

    var iteration =lastRow;
    $('#flatFileInputName option:selected').each(function(){
      var selectedInput=$(this).val();

      var row = tbl.insertRow(lastRow);

      var cellLeft = row.insertCell(0);
      var textNode = "<input name=\"flatFileInputName_!count!\" class=\"txtbox\" value=\""+selectedInput+"\" readonly/>";
      cellLeft.innerHTML=textNode.replace(/!count!/g, iteration);
      cellLeft.setAttribute("class","textb");
      cellLeft.setAttribute("valign","middle");

      var cellRightSel = row.insertCell(1);
      var strHtml1 = "<select name=\"flatFileOperator_!count!\" class=\"txtbox\"  style=\"background-color:#EBEBE4;width:100px\"><%=profileManagementManager.getOptionTagforOperators(null)%></select>";
      cellRightSel.innerHTML = strHtml1.replace(/!count!/g, iteration);
      cellRightSel.setAttribute("class","textb");
      cellRightSel.setAttribute("valign","middle");
      cellRightSel.setAttribute("align","center");

      var cellRightSelFilePath = row.insertCell(2);
      var strHtml1FilePath = "<input type=\"text\" name=\"riskRuleValue1_!count!\" class=\"txtbox\" value=\"\">";
      cellRightSelFilePath.innerHTML = strHtml1FilePath.replace(/!count!/g, iteration);
      cellRightSelFilePath.setAttribute("class","textb");
      cellRightSelFilePath.setAttribute("valign","middle");
      cellRightSelFilePath.setAttribute("align","center");

      // right cell
      if($('#flatFileInputName option:selected').length>iteration)
      {

        var cellRight = row.insertCell(3);
        var strHtml2 = "<select name=\"flatFileComparator_!count!\" class=\"txtbox\"  style=\"background-color:#EBEBE4;width:100px\"><%=profileManagementManager.getOptionTagForComparator(null,null)%></select>";
        cellRight.innerHTML = strHtml2.replace(/!count!/g, iteration);
        cellRight.setAttribute("class", "textb");
        cellRight.setAttribute("valign", "middle");
        cellRight.setAttribute("colspan", "1");
      }
      else
      {
        var cellRight = row.insertCell(3);
      }
      lastRow++;
      iteration++;
    });
  });

  $('#processRegex').click(function()
  {
    document.getElementById("processRegexDIV").style.display = "block";
    var tbl = document.getElementById('processRegexTable');
    var lastRow = tbl.rows.length;
    // if there's no header row in the table, then iteration = lastRow + 1

    for(var i=lastRow-1;i>0;i--)
    {
      tbl.deleteRow(i);
    }
    lastRow=  tbl.rows.length;

    var iteration =lastRow;
    $('#regexInputName option:selected').each(function(){
      var selectedInput=$(this).val();

      var row = tbl.insertRow(lastRow);

      var cellLeft = row.insertCell(0);
      var textNode = "<input name=\"regexInputName_!count!\" class=\"txtbox\" value=\""+selectedInput+"\" readonly/>";
      cellLeft.innerHTML=textNode.replace(/!count!/g, iteration);
      cellLeft.setAttribute("class","textb");
      cellLeft.setAttribute("valign","middle");

      var cellRightSel = row.insertCell(1);
      var strHtml1 = "<select name=\"regexOperator_!count!\" class=\"txtbox\"  style=\"background-color:#EBEBE4;width:100px\"><%=profileManagementManager.getOptionTagforOperators(null)%></select>"
      cellRightSel.innerHTML = strHtml1.replace(/!count!/g, iteration);
      cellRightSel.setAttribute("class","textb");
      cellRightSel.setAttribute("valign","middle");
      cellRightSel.setAttribute("align","center");

      var cellLeft = row.insertCell(2);
      var textNode = "<input name=\"regex_!count!\" class=\"txtbox\" placeholder=\"REGEX\" />";
      cellLeft.innerHTML=textNode.replace(/!count!/g, iteration);
      cellLeft.setAttribute("class","textb");
      cellLeft.setAttribute("valign","middle");
      //cellLeft.setAttribute("colspan","2");

      var cellLeft = row.insertCell(3);
      var textNode = "<input type=\"checkbox\" name=\"isMandatory_!count!\"  value='Y'/>";
      cellLeft.innerHTML=textNode.replace(/!count!/g, iteration);
      cellLeft.setAttribute("class","textb");
      cellLeft.setAttribute("valign","middle");
      cellLeft.setAttribute("align","center");


      // right cell
      if($('#regexInputName option:selected').length>iteration)
      {
        var cellRight = row.insertCell(4);
        var strHtml2 = "<select name=\"regexComparator_!count!\" class=\"txtbox\"  style=\"background-color:#EBEBE4;width:100px\"><%=profileManagementManager.getOptionTagForComparator(null,null)%></select>";
        cellRight.innerHTML = strHtml2.replace(/!count!/g, iteration);
        cellRight.setAttribute("class", "textb");
        cellRight.setAttribute("valign", "middle");
        cellRight.setAttribute("colspan", "1");
      }
      else
      {
        var cellRight = row.insertCell(4);
      }
      lastRow++;
      iteration++;
    });
  });

  $('#processComparator').click(function()
  {
    document.getElementById("processFinalComparatorDiv").style.display = "block";
    var tbl = document.getElementById('processFinalComparatorTable');
    var lastRow = tbl.rows.length;
    // if there's no header row in the table, then iteration = lastRow + 1

    for(var i=lastRow-1;i>0;i--)
    {
      tbl.deleteRow(i);
    }
    lastRow=  tbl.rows.length;

    var iteration =lastRow;
    $('#comparatorInputName option:selected').each(function(){
      var selectedInput=$(this).val();

      var row = tbl.insertRow(lastRow);

      var cellLeft = row.insertCell(0);
      var textNode = "<input name=\"comparatorInputName_!count!\" class=\"txtbox\" value=\""+selectedInput+"\" readonly/>";
      cellLeft.innerHTML=textNode.replace(/!count!/g, iteration);
      cellLeft.setAttribute("class","textb");
      cellLeft.setAttribute("valign","middle");


      var cellRightSel = row.insertCell(1);
      var strHtml1 = "<select name=\"comparatorOperator_!count!\" class=\"txtbox\"  style=\"background-color:#EBEBE4;width:100px\" onChange=\"enableText('comparatorOperator_!count!','riskRuleValue1_!count!','riskRuleValue2_!count!')\"><%=profileManagementManager.getOptionTagforOperators(null)%></select>";
      cellRightSel.innerHTML = strHtml1.replace(/!count!/g, iteration);
      cellRightSel.setAttribute("class","textb");
      cellRightSel.setAttribute("valign","middle");

      var cellRightSel = row.insertCell(2);
      var strHtml1 = "<input type=\"text\" name=\"riskRuleValue1_!count!\" class=\"txtbox\" value=''>";
      cellRightSel.innerHTML = strHtml1.replace(/!count!/g, iteration);
      cellRightSel.setAttribute("class","textb");
      cellRightSel.setAttribute("valign","middle");
      cellRightSel.setAttribute("align","center");

      var cellRightSel = row.insertCell(3);
      var strHtml1 = "<input type=\"text\" name=\"riskRuleValue2_!count!\" class=\"txtbox\" value='' style='background-color:#EBEBE4' readonly>";
      cellRightSel.innerHTML = strHtml1.replace(/!count!/g, iteration);
      cellRightSel.setAttribute("class","textb");
      cellRightSel.setAttribute("valign","middle");
      cellRightSel.setAttribute("align","center");

      /* cellRightSel.appendChild(sel);*/

      // right cell
      if($('#comparatorInputName option:selected').length>iteration)
      {
        cellRightSel.setAttribute("colspan","1");
        var cellRight = row.insertCell(4);
        var strHtml2 = "<select name=\"comparatorComparator_!count!\" class=\"txtbox\"  style=\"background-color:#EBEBE4;width:100px\"><%=profileManagementManager.getOptionTagForComparator(null,null)%></select>";
        cellRight.innerHTML = strHtml2.replace(/!count!/g, iteration);
        cellRight.setAttribute("class", "textb");
        cellRight.setAttribute("valign", "middle");
        cellRight.setAttribute("colspan", "1");
      }
      else
      {
        var cellRight = row.insertCell(4);
      }
      lastRow++;
      iteration++;
    });
  });
  $('#process').click(function(){
    document.getElementById("processQuery").style.display="block";
    document.getElementById("processFinalQueryDiv").style.display="none";
    var tbl = document.getElementById('processQueryTable');
    var lastRow = tbl.rows.length;
    // if there's no header row in the table, then iteration = lastRow + 1

    for(var i=lastRow-1;i>0;i--)
    {
      tbl.deleteRow(i);
    }
    lastRow=  tbl.rows.length;
    var iteration =lastRow;
    $('#select option:selected').each(function(){


      var selectField=$(this).val();

      var row = tbl.insertRow(lastRow);
      var selectText="";
      // left cell
      if(iteration==1)
      {
        selectText="Select";
      }
      var cellLeft = row.insertCell(0);
      var textNode = document.createTextNode(selectText);
      cellLeft.appendChild(textNode);
      cellLeft.setAttribute("class","textb");
      cellLeft.setAttribute("valign","middle");

      var cellRightSel = row.insertCell(1);
      var strHtml1 = "<select name=\"selectAggregate_!count!\" class=\"txtbox\"  style=\"background-color:#EBEBE4;width:100px\"><%=profileManagementManager.getOptionTagForAggregateFunctionsQuery(null,null)%></select>";
      cellRightSel.innerHTML = strHtml1.replace(/!count!/g, iteration);
      cellRightSel.setAttribute("class","textb");
      cellRightSel.setAttribute("valign","middle");
      cellRightSel.setAttribute("colspan","2");
      /* cellRightSel.appendChild(sel);*/

      // right cell
      var cellRight = row.insertCell(2);
      var strHtml2 = "<input type=\"text\" name='selectField_!count!' class='textb' value='"+selectField+"' readonly />";
      cellRight.innerHTML=strHtml2.replace(/!count!/g, iteration);
      cellRight.setAttribute("class","textb");
      cellRight.setAttribute("valign","middle");
      cellRight.setAttribute("colspan","3");

      /*var cellRight5 = row.insertCell(3);
       var strHtml4 = "<select name=\"selectOperator_!count!\" class=\"txtbox\"  style=\"background-color:#EBEBE4;width:100px\"><%--<%=profileManagementManager.getOptionTagforOperators(null)%>--%></select>";
       cellRight5.innerHTML=strHtml4.replace(/!count!/g, iteration);

       var cellRightFull = row.insertCell(4);
       var strHtml3 = "<select name=\"selectComparator_!count!\" class=\"txtbox\"  style=\"background-color:#EBEBE4;width:100px\"><%--<%=profileManagementManager.getOptionTagForComparator(null,null)%>--%></select>";
       cellRightFull.innerHTML=strHtml3.replace(/!count!/g, iteration);*/

      $('#from > option').each(function(){

        if(selectField.indexOf($(this).attr("id"))>=0)
        {
          this.setAttribute("selected", "");
        }
      });
      // select cell


      iteration++;
      lastRow++;
    });

    var fromIteration=1;
    var onIteration=1;
    var previousTable=""
    var currentTable=""
    $('#from option:selected').each(function(){
      if(iteration==1 && fromIteration==1)
      {
        document.getElementById("processQuery").style.display="none";
        alert("Please Select Fields from select");
        fromIteration++;
      }
      else if(fromIteration>=1 && iteration==1)
      {

      }
      else
      {
        var selectField=$(this).val();

        if(previousTable=="")
        {
          previousTable=$(this).attr("id");
        }
        else
        {
          previousTable=currentTable;
        }
        currentTable=$(this).attr("id");

        var row = tbl.insertRow(lastRow);
        var selectText="";
        // left cell
        if(fromIteration==1)
        {
          selectText="From";
        }
        var cellLeft = row.insertCell(0);
        var textNode = document.createTextNode(selectText);
        cellLeft.appendChild(textNode);
        cellLeft.setAttribute("class","textb");
        cellLeft.setAttribute("valign","middle");

        var cellRightSel = row.insertCell(1);
        var strHtml1 = "<input type=\"text\" name='fromTable_!count!' class='textb' value='"+selectField+"' readonly />";
        cellRightSel.innerHTML = strHtml1.replace(/!count!/g, fromIteration);
        cellRightSel.setAttribute("class","textb");
        cellRightSel.setAttribute("valign","middle");

        /* cellRightSel.appendChild(sel);*/

        if($("#from :selected").length>1 && $("#from :selected").length>fromIteration)
        {
          cellRightSel.setAttribute("colspan","2");
          // right cell
          var cellRight = row.insertCell(2);
          var strHtml2 = "<select name=\"fromJoin_!count!\" class=\"txtbox\"  style=\"background-color:#EBEBE4;width:100px\"><%=profileManagementManager.getOptionTagForJoinTableQuery(null)%></select>";
          cellRight.innerHTML = strHtml2.replace(/!count!/g, fromIteration);
          cellRight.setAttribute("class", "textb");
          cellRight.setAttribute("valign", "middle");
          cellRight.setAttribute("colspan", "3");
        }
        else
        {
          cellRightSel.setAttribute("colspan","5");
        }

        /*var cellRight5 = row.insertCell(3);
         var strHtml4 = "<select name=\"selectOperator_!count!\" class=\"txtbox\"  style=\"background-color:#EBEBE4;width:100px\"><%--<%=profileManagementManager.getOptionTagforOperators(null)%>--%></select>";
       cellRight5.innerHTML=strHtml4.replace(/!count!/g, iteration);

       var cellRightFull = row.insertCell(4);
       var strHtml3 = "<select name=\"selectComparator_!count!\" class=\"txtbox\"  style=\"background-color:#EBEBE4;width:100px\"><%--<%=profileManagementManager.getOptionTagForComparator(null,null)%>--%></select>";
       cellRightFull.innerHTML=strHtml3.replace(/!count!/g, iteration);*/

        if(fromIteration>1)
        {
          lastRow++;
          var on = tbl.insertRow(lastRow);
          var selectOn="ON";
          var centerON="=";
          // left cell
          var cellLeftOn = on.insertCell(0);
          var textNodeOn = document.createTextNode(selectOn);
          cellLeftOn.appendChild(textNodeOn);
          cellLeftOn.setAttribute("class","textb");
          cellLeftOn.setAttribute("valign","middle");

          var cellRightSelOn = on.insertCell(1);
          var strHtml1On = "<select name=\"onLeft_!count!\" id=\"onLeft_!count!\" class=\"txtbox\"  style=\"background-color:#EBEBE4;width:100px\"><option value=\"\">Select Field</option><%=selectOptionTag%></select>";
          cellRightSelOn.innerHTML = strHtml1On.replace(/!count!/g, onIteration);
          cellRightSelOn.setAttribute("class","textb");
          cellRightSelOn.setAttribute("valign","middle");


          var cellCenterOn = on.insertCell(2);
          var textNodeCenterOn = document.createTextNode(centerON);
          cellCenterOn.appendChild(textNodeCenterOn);
          cellCenterOn.setAttribute("class","textb");
          cellCenterOn.setAttribute("valign","middle");
          cellCenterOn.setAttribute("align","center");
          cellCenterOn.setAttribute("colspan","1");
          /* cellRightSel.appendChild(sel);*/

          // right cell
          var cellRightOn = on.insertCell(3);
          var strHtml2On = "<select name=\"onRight_!count!\" id=\"onRight_!count!\" class=\"txtbox\"  style=\"background-color:#EBEBE4;width:100px\"><option value=\"\">Select Field</option><%=selectOptionTag%></select>";
          cellRightOn.innerHTML=strHtml2On.replace(/!count!/g, onIteration);
          cellRightOn.setAttribute("class","textb");
          cellRightOn.setAttribute("valign","middle");
          cellRightOn.setAttribute("colspan","3");


          $("#onLeft_"+onIteration+" > option").each(function() {
            /* alert(this.text + ' ' + this.value);*/
            if(this.value.indexOf(previousTable) == -1 && this.value!="")
            {
              this.setAttribute("style","display:none")
            }
          });

          $("#onRight_"+onIteration+" > option").each(function() {
            /*alert(this.text + ' ' + this.value);*/
            if(this.value.indexOf(currentTable) == -1 && this.value!="")
            {
              this.setAttribute("style","display:none")
            }
          });
          onIteration++;

        }
        fromIteration++;
        lastRow++;


      }

    });


    var whereIteration=1;

    $('#where option:selected').each(function(){
      if((iteration==1 || fromIteration==1) && whereIteration==1)
      {
        document.getElementById("processQuery").style.display="none";
        alert("Please Select Fields from select and from");
        whereIteration++;
      }
      else if(whereIteration>=1 && (iteration==1 || fromIteration==1))
      {

      }
      else
      {
        var selectField=$(this).val();

        var row = tbl.insertRow(lastRow);
        var selectWhere="";
        // left cell
        if(whereIteration==1)
        {
          selectWhere="Where";
        }
        var cellLeft = row.insertCell(0);
        var textNode = document.createTextNode(selectWhere);
        cellLeft.appendChild(textNode);
        cellLeft.setAttribute("class","textb");
        cellLeft.setAttribute("valign","middle");

        var cellRightSel = row.insertCell(1);
        var strHtml1 = "<input type=\"text\" name='whereField_!count!' class='textb' value='"+selectField+"' readonly />";
        cellRightSel.innerHTML = strHtml1.replace(/!count!/g, whereIteration);
        cellRightSel.setAttribute("class","textb");
        cellRightSel.setAttribute("valign","middle");


        var cellRight5 = row.insertCell(2);
        var strHtml4 = "<select name=\"whereOperator_!count!\" class=\"txtbox\"  style=\"background-color:#EBEBE4;width:100px\"><%=profileManagementManager.getOptionTagForOperatorsForSymbol(null).toString().replace("<option value=''>Select a Operator</option>","")%></select>".replace("==","=");
        cellRight5.innerHTML=strHtml4.replace(/!count!/g, whereIteration);

        var cellRightFull = row.insertCell(3);
        var strHtml3 = "<table width=\"100%\" height=\"100%\" id=\"inputNameWhereTable_!count!\"><tr><td><select name=\"whereInputField_!count!\" class=\"txtbox\"  style=\"background-color:#EBEBE4;width:100px\"><%=profileManagementManager.getOptionTagForInputName(null,null)%></select></td></tr></table>";
        cellRightFull.innerHTML=strHtml3.replace(/!count!/g, whereIteration);
        cellRightFull.setAttribute("class","textb");
        cellRightFull.setAttribute("valign","middle");

        var cellRightFull = row.insertCell(4);
        var strHtml3 = "<input type=\"checkbox\" name=\"isFreeText_!count!\" value=\"Y\" onclick=\"getFreeText(this.checked,'!count!','"+selectField+"')\">";
        cellRightFull.innerHTML=strHtml3.replace(/!count!/g, whereIteration);
        cellRightFull.setAttribute("class","textb");
        cellRightFull.setAttribute("valign","middle");
        cellRightFull.setAttribute("width","20px");


        if($("#where :selected").length>1 && $("#where :selected").length>whereIteration)
        {
          var cellRightOperator = row.insertCell(5);
          var strHtml5 = "<select name=\"whereComparator_!count!\" class=\"txtbox\"  style=\"background-color:#EBEBE4;width:100px\"><%=profileManagementManager.getOptionTagForComparator(null,null)%></select>";
          cellRightOperator.innerHTML = strHtml5.replace(/!count!/g, whereIteration);
          cellRightOperator.setAttribute("class", "textb");
          cellRightOperator.setAttribute("valign", "middle");
        }
        else
        {
          var cellRightOperator = row.insertCell(5);
        }

        whereIteration++;
        lastRow++;
      }

    });

    var groupIteration=1;
    $('#group option:selected').each(function(){
      if((iteration==1 || fromIteration==1) && groupIteration==1)
      {
        document.getElementById("processQuery").style.display="none";
        alert("Please Select Fields from select and from");
        groupIteration++;
      }
      else if(groupIteration>=1 && (iteration==1 || fromIteration==1))
      {

      }
      else
      {
        var selectField=$(this).val();

        var row = tbl.insertRow(lastRow);
        var selectWhere="";
        // left cell
        if(groupIteration==1)
        {
          selectWhere="Group By";
        }
        var cellLeft = row.insertCell(0);
        var textNode = document.createTextNode(selectWhere);
        cellLeft.appendChild(textNode);
        cellLeft.setAttribute("class","textb");
        cellLeft.setAttribute("valign","middle");

        var cellRightSel = row.insertCell(1);
        var strHtml1 = "<input type=\"text\" name='groupBy_!count!' class='textb' value='"+selectField+"' readonly />";
        cellRightSel.innerHTML = strHtml1.replace(/!count!/g, groupIteration);
        cellRightSel.setAttribute("class","textb");
        cellRightSel.setAttribute("valign","middle");
        cellRightSel.setAttribute("colspan","5");


        groupIteration++;
        lastRow++;
      }

    });

    var button = tbl.insertRow(lastRow);

    var cellRightSelButton = button.insertCell(0);
    var strHtml1 = "<button type=\"button\" id='processFinalQuery' class=\"buttonform\"  onclick=\"processQuery()\">Final&nbsp;Query</button>";
    cellRightSelButton.innerHTML = strHtml1.replace(/!count!/g,"");
    cellRightSelButton.setAttribute("class","textb");
    cellRightSelButton.setAttribute("valign","middle");
    cellRightSelButton.setAttribute("align","center");
    cellRightSelButton.setAttribute("colspan","6");

    lastRow++;


  });
  function processQuery()
  {
    document.getElementById("processFinalQueryDiv").style.display="block";
    document.getElementById("processFinalQueryTable").style.display="block";
    var tbl = document.getElementById('processFinalQueryTable');
    var lastRow = tbl.rows.length;
    // if there's no header row in the table, then iteration = lastRow + 1

    for(var i=lastRow-1;i>1;i--)
    {
      tbl.deleteRow(i);
    }
    lastRow=  tbl.rows.length;
    var selectIteration=1;
    var messageJoin="";
    var messageOn="";
    var messageWhereField="";
    var messageWhereInput="";
    var messageWhereComparator="";
    $('#businessRuleQuery').text('');
    var inputs = document.getElementsByTagName("input");

    var selectStatement="";
    var fromStatement="";
    var whereStatement="";
    var groupStatement="";
    for(var i = 0; i < inputs.length; i++) {
      var currentSelectStatement="";
      if(inputs[i].name.indexOf('selectField_') == 0) {

        if(selectStatement.length>0)
          selectStatement+=",";
        if($('select[name=selectAggregate_'+inputs[i].name.split("_")[1]+']').val()!="")
        {
          selectStatement+=($('select[name=selectAggregate_'+inputs[i].name.split("_")[1]+']').val());
          selectStatement+="(";
          selectStatement+=(inputs[i].value);
          selectStatement+=")";

          currentSelectStatement+=($('select[name=selectAggregate_'+inputs[i].name.split("_")[1]+']').val());
          currentSelectStatement+="(";
          currentSelectStatement+=(inputs[i].value);
          currentSelectStatement+=")";

        }
        else
        {
          selectStatement+=(inputs[i].value);
          currentSelectStatement+=(inputs[i].value);
        }

        var select = tbl.insertRow(lastRow);

        // left cell
        var cellLeftOn = select.insertCell(0);
        var textNodeOn = "<input type=\"text\" class=\"txtbox\" name=\"select_!count!\" value=\""+currentSelectStatement+"\" readonly />";
        cellLeftOn.innerHTML = textNodeOn.replace(/!count!/g,selectIteration);
        cellLeftOn.setAttribute("class","textb");
        cellLeftOn.setAttribute("valign","middle");

        var cellRightSelOn = select.insertCell(1);
        var strHtml1On = "<select name=\"selectOperator_!count!\" class=\"txtbox\"  style=\"background-color:#EBEBE4;width:100px\" onChange=\"enableText('selectOperator_!count!','riskRuleValue1_!count!','riskRuleValue2_!count!')\"><%=profileManagementManager.getOptionTagforOperators(null)%></select>";
        cellRightSelOn.innerHTML = strHtml1On.replace(/!count!/g, selectIteration);
        cellRightSelOn.setAttribute("class","textb");
        cellRightSelOn.setAttribute("valign","middle");

        var cellRightSelOnVal1 = select.insertCell(2);
        var strHtml1OnVal1 = "<input type=\"text\" class=\"txtbox\" name=\"riskRuleValue1_!count!\" value=\"\">";
        cellRightSelOnVal1.innerHTML = strHtml1OnVal1.replace(/!count!/g, selectIteration);
        cellRightSelOnVal1.setAttribute("class","textb");
        cellRightSelOnVal1.setAttribute("valign","middle");

        var cellRightSelOnVal2 = select.insertCell(3);
        var strHtml1OnVal2 = "<input type=\"text\" class=\"txtbox\" name=\"riskRuleValue2_!count!\" value=\"\" style='background-color:#EBEBE4' readonly>";
        cellRightSelOnVal2.innerHTML = strHtml1OnVal2.replace(/!count!/g, selectIteration);
        cellRightSelOnVal2.setAttribute("class","textb");
        cellRightSelOnVal2.setAttribute("valign","middle");

        var cellRightSelMan = select.insertCell(4);
        var strHtml1Man = "<input type=\"checkbox\" name=\"isMandatory_!count!\"  value='Y'/>";
        cellRightSelMan.innerHTML = strHtml1Man.replace(/!count!/g, selectIteration);
        cellRightSelMan.setAttribute("class","textb");
        cellRightSelMan.setAttribute("valign","middle");
        cellRightSelMan.setAttribute("align","center");


        if($('input[name=selectField_'+(Number(inputs[i].name.split("_")[1])+1)+']').val()!=null)
        {
          var cellRightSelOn = select.insertCell(5);
          var strHtml2On = "<select name=\"selectComparator_!count!\" class=\"txtbox\"  style=\"background-color:#EBEBE4;width:100px\"><%=profileManagementManager.getOptionTagForComparator(null,null)%></select>";
          cellRightSelOn.innerHTML = strHtml2On.replace(/!count!/g, selectIteration);
          cellRightSelOn.setAttribute("class", "textb");
          cellRightSelOn.setAttribute("valign", "middle");
        }
        else
        {
          var cellRightSelOn = select.insertCell(5);
        }
        selectIteration++;
        lastRow++;
      }
      if(inputs[i].name.indexOf('fromTable_') == 0) {

        if(fromStatement.length>0)
          fromStatement+=" ";
        fromStatement+=inputs[i].value;
        fromStatement+=" as " ;
        $('#from > option').each(function()
        {
          if($(this).val()==inputs[i].value)
            fromStatement +=$(this).attr("id");
        });

        fromStatement+=" ";
        if($('select[name=fromJoin_'+inputs[i].name.split("_")[1]+']').val()!=null && $('select[name=fromJoin_'+inputs[i].name.split("_")[1]+']').val()!="")
        {
          fromStatement+=($('select[name=fromJoin_'+inputs[i].name.split("_")[1]+']').val());
        }
        else if($('select[name=fromJoin_'+inputs[i].name.split("_")[1]+']').val()!=null)
        {
          messageJoin="select a Join\n";
        }
        fromStatement+=" ";


        if((Number(inputs[i].name.split("_")[1])>1) && $('select[name=onLeft_'+(Number(inputs[i].name.split("_")[1])-1)+']').val()!=null && $('select[name=onLeft_'+(Number(inputs[i].name.split("_")[1])-1)+']').val()!="")
        {
          fromStatement+=" ON ";
          fromStatement+=($('select[name=onLeft_'+(Number(inputs[i].name.split("_")[1])-1)+']').val());
          fromStatement+="=";
          fromStatement+=($('select[name=onRight_'+(Number(inputs[i].name.split("_")[1])-1)+']').val());
        }
        if(($('select[name=onLeft_'+(Number(inputs[i].name.split("_")[1])-1)+']').val()!=null && $('select[name=onLeft_'+(Number(inputs[i].name.split("_")[1])-1)+']').val()=="") || ($('select[name=onRight_'+(Number(inputs[i].name.split("_")[1])-1)+']').val()!=null && $('select[name=onRight_'+(Number(inputs[i].name.split("_")[1])-1)+']').val()==""))
        {
          messageWhereField="select On Clause\n";
        }


      }
      if(inputs[i].name.indexOf('whereField_') == 0) {

        if(whereStatement.length>0)
          whereStatement+=" ";
        whereStatement+=inputs[i].value;


        if($('select[name=whereOperator_'+inputs[i].name.split("_")[1]+']').val()!=null && $('select[name=whereOperator_'+inputs[i].name.split("_")[1]+']').val()!="")
        {
          whereStatement+=($("option:selected",$('select[name=whereOperator_'+inputs[i].name.split("_")[1]+']')).text());
        }
        if($('select[name=whereInputField_'+inputs[i].name.split("_")[1]+']').val()!=null && $('select[name=whereInputField_'+inputs[i].name.split("_")[1]+']').val()!="")
        {
          whereStatement+="<#";
          whereStatement+=($('select[name=whereInputField_'+inputs[i].name.split("_")[1]+']').val());
          whereStatement+="#>";
        }
        else if($('select[name=whereInputFieldFreeText_'+inputs[i].name.split("_")[1]+']').val()!=null && $('select[name=whereInputFieldFreeText_'+inputs[i].name.split("_")[1]+']').val()!="")
        {
          whereStatement+="'";
          whereStatement+=($('select[name=whereInputFieldFreeText_'+inputs[i].name.split("_")[1]+']').val());
          whereStatement+="'";
        }
        else if($('input[name=whereInputField_'+inputs[i].name.split("_")[1]+']').val()!=null && $('input[name=whereInputField_'+inputs[i].name.split("_")[1]+']').val()!="")
        {
          whereStatement+="'";
          whereStatement+=($('input[name=whereInputField_'+inputs[i].name.split("_")[1]+']').val());
          whereStatement+="'";
        }
        else
        {
          messageWhereInput="Select Input Field\n";
        }

        if($('select[name=whereComparator_'+inputs[i].name.split("_")[1]+']').val()!=null && $('select[name=whereComparator_'+inputs[i].name.split("_")[1]+']').val()!="")
        {
          whereStatement+=" ";
          whereStatement+=($('select[name=whereComparator_'+inputs[i].name.split("_")[1]+']').val());
        }
        else if($('select[name=whereComparator_'+inputs[i].name.split("_")[1]+']').val()!=null)
        {
          messageWhereComparator="select Comparator\n";
        }

      }
      if(inputs[i].name.indexOf('groupBy_') == 0) {

        if(groupStatement.length>0)
          groupStatement+=",";
        groupStatement+=inputs[i].value;

      }

    }
    if(messageJoin!="" || messageOn!=""||messageWhereComparator!=""||messageWhereField!=""||messageWhereInput!="")
    {
      document.getElementById("processFinalQueryDiv").style.display="none";
      document.getElementById("processFinalQueryTable").style.display="none";
      alert("Please Provide below data:\n" +messageJoin+messageOn+messageWhereComparator+messageWhereField+messageWhereInput);
    }
    else
    {
      $('#businessRuleQuery').append("Select " + selectStatement);
      $('#businessRuleQuery').append(" From " + fromStatement);
      if(whereStatement!="")
        $('#businessRuleQuery').append(" WHERE " + whereStatement);
      if(groupStatement!="")
        $('#businessRuleQuery').append(" GROUP BY " + groupStatement);

      $('#businessRuleQuery').val($('#businessRuleQuery').text());
    }
  }

  function enableText(fromaction,action1,action2)
  {
    var hat1 = this.document.getElementsByName(fromaction)[0].selectedIndex ;
    var hatto1 = this.document.getElementsByName(fromaction)[0].options[hat1].value;
    if(hatto1=="BETWEEN")
    {
      document.getElementsByName(action2)[0].readOnly = false
      $(document.getElementsByName(action2)[0]).css('background-color', '#ffffff');
    }
    else
    {
      document.getElementsByName(action2)[0].readOnly = false
      $(document.getElementsByName(action2)[0]).css('background-color', '#EBEBE4');
    }
  }

  function selectedDropdown(name)
  {
    var hat1 = this.document.getElementsByName(name)[0].selectedIndex ;
    var hatto1 = this.document.getElementsByName(name)[0].options[hat1].value;

    if(hatto1=="DATABASE")
    {
      document.getElementById("query").style.display="block";
      document.getElementById("comparator").style.display="none";
      document.getElementById("regex").style.display="none";
      document.getElementById("flatfile").style.display="none";
    }
    else
    {
      if(hatto1=="COMPARATOR")
      {
        document.getElementById("comparator").style.display="block";
      }
      else
      {
        document.getElementById("comparator").style.display="none";
      }

      if(hatto1=="FLAT_FILE")
      {
        document.getElementById("flatfile").style.display="block";
      }
      else
      {
        document.getElementById("flatfile").style.display="none";
      }
      if(hatto1=="REGULAR_EXPRESSION")
      {
        document.getElementById("regex").style.display="block";
      }
      else
      {
        document.getElementById("regex").style.display="none";
      }
      document.getElementById("query").style.display="none";

    }
  }

  function selectedDataType(name)
  {
    var val1="comparatorDataType_"+name;
    var val2="comparatorEnumValue_"+name;
    var hat1 = this.document.getElementsByName(val1)[0].selectedIndex ;
    var hatto1 = this.document.getElementsByName(val1)[0].options[hat1].value;

    if(hatto1=="<%=DataType.ENUM.name()%>")
    {
      document.getElementsByName(val2)[0].readOnly = false
      $(document.getElementsByName(val2)[0]).css('background-color', '#ffffff');
    }
    else
    {
      document.getElementsByName(val2)[0].readOnly = true
      $(document.getElementsByName(val2)[0]).css('background-color', '#EBEBE4');
    }
  }

  function cancel(ctoken) {
    document.myForm.action="/icici/servlet/RiskRuleDetails?ctoken="+ctoken;
  }

  function getFreeText(status,name,whereField)
  {
    var freeTextOrInputName = "inputNameWhereTable_" + name;
    var tbl = document.getElementById(freeTextOrInputName);
    var lastRow = tbl.rows.length;
    // if there's no header row in the table, then iteration = lastRow + 1
    var row = tbl.insertRow(0);
    if(status==true)
    {
      var isEnum="";
      for (var k = 0; k < payIFETableArray.length; k++) {
        if(payIFETableArray[k].indexOf(whereField)>=0)
        {
          var payIFEString=payIFETableArray[k];
          isEnum=payIFEString.split(":")[2];
          break;
        }
      }
      var cellRightSel = row.insertCell(0);
      if(isEnum=="")
      {
        strHtml1 = "<input type=\"text\" name='whereInputField_"+name+"' class='txtbox' value='' />";
      }
      else
      {
        strHtml1 = "<select  name='whereInputFieldFreeText_"+name+"' class='txtbox' >";
        for(var j=0;j<isEnum.split(",").length;j++)
        {
          strHtml1+="<option value='"+isEnum.split(",")[j]+"'>"+isEnum.split(",")[j]+"</option>";
        }
        strHtml1+="</select>";
      }

      cellRightSel.innerHTML = strHtml1.replace(/!count!/g, name);
      cellRightSel.setAttribute("class","textb");
      cellRightSel.setAttribute("valign","middle");
    }
    else
    {
      var cellRightSel = row.insertCell(0);
      var strHtml1 = "<select name=\"whereInputField_!count!\" class=\"txtbox\"  style=\"background-color:#EBEBE4;width:100px\"><%=profileManagementManager.getOptionTagForInputName(null,null)%></select>";
      cellRightSel.innerHTML = strHtml1.replace(/!count!/g, name);
      cellRightSel.setAttribute("class","textb");
      cellRightSel.setAttribute("valign","middle");
    }

    var lastRow = tbl.rows.length;
    for(var i=lastRow-1;i>0;i--)
    {
      tbl.deleteRow(i);
    }
  }

  $( "#regexInputName" ).change(function () {

  $("#submit").removeAttr("onclick");
  $("#submit").attr("onclick","return clickOnProcessAlert()");
  });

  $( "#processRegex").click(function ()
  {
    $("#submit").removeAttr("onclick");
  });

  $( "#select" ).change(function () {
    $("#submit").removeAttr("onclick");
    $("#submit").attr("onclick","return clickOnProcessAlert()");
  });

  $( "#process" ).click(function ()
  {
    $("#submit").removeAttr("onclick");
  });


  $( "#comparatorInputName" ).change(function () {
    $("#submit").removeAttr("onclick");
    $("#submit").attr("onclick","return clickOnProcessAlert()");
  });

  $( "#processComparator" ).click(function ()
  {
    $("#submit").removeAttr("onclick");
  });


  $( "#flatFileInputName" ).change(function () {
    $("#submit").removeAttr("onclick");
    $("#submit").attr("onclick","return clickOnProcessAlert()");
  });

  $( "#processFlatFile" ).click(function ()
  {
    $("#submit").removeAttr("onclick");
  });

  function clickOnProcessAlert()
  {
    alert("Please Click On process before submiting the Rule Details");
    return false;
  }
</script>
</body>
</html>
