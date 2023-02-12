<%--
  Created by IntelliJ IDEA.
  User: Admin
  Date: 4/6/15
  Time: 4:30 PM
  To change this template use File | Settings | File Templates.
--%>
<style>
    @media(max-width:768px)
    {
        #h2carddetails
        {

            margin-bottom: 5px;
            margin-top: 5px;
        }
        #headtag
        {
            margin-left: -22px;
            width: 135%;

        }
        #creditlyfield
        {
            margin-left: -53px;
        }



    }
    @media(min-width:768px)
    {
        #headtag
        {
            margin-left: -15px;
            margin-top: 13px;
            width: 100%;
            margin-bottom: 15px;
        }
        #inputgrp
        {
            width:100%;
        }
        #h2carddetails
        {
            margin-left: -16px;
            font-size: 14px ;

        }
        #creditlyfield
        {
            margin-left: -30px;
        }
        #telnocc
        {width: 80%;}
        #formfield
        {
            margin-left: -15px;
        }
    }

</style>
<section class="creditly-wrapper" style="margin-bottom: -46px;">
    <div class="credit-card-wrapper"  id="creditlyfield">
        <div class="first-row form-group" id="form-group" style="display:table;">
            <div class=" form-group col-md-12 controls">
                <div class="col-md-12 portlets ui-sortable" id="headtag">

    <label>Birth Date*</label>

</div>
<div class="col-sm-2 controls" >
    <label class="control-label">DD</label>
    <select NAME="bday" class="form-control textbox_color">
        <option VALUE="01" selected>01</option>
        <option VALUE="02">02</option>
        <option VALUE="03">03</option>
        <option VALUE="04">04</option>
        <option VALUE="05">05</option>
        <option VALUE="06">06</option>
        <option VALUE="07">07</option>
        <option VALUE="08">08</option>
        <option VALUE="09">09</option>
        <option VALUE="10">10</option>
        <option VALUE="11">11</option>
        <option VALUE="12">12</option>
        <option VALUE="13">13</option>
        <option VALUE="14">14</option>
        <option VALUE="15">15</option>
        <option VALUE="16">16</option>
        <option VALUE="17">17</option>
        <option VALUE="18">18</option>
        <option VALUE="19">19</option>
        <option VALUE="20">20</option>
        <option VALUE="21">21</option>
        <option VALUE="22">22</option>
        <option VALUE="23">23</option>
        <option VALUE="24">24</option>
        <option VALUE="25">25</option>
        <option VALUE="26">26</option>
        <option VALUE="27">27</option>
        <option VALUE="28">28</option>
        <option VALUE="29">29</option>
        <option VALUE="30">30</option>
        <option VALUE="31">31</option>
    </select>
</div>
<div class="col-sm-2 controls">
    <label class="control-label">MM</label>
    <select NAME="bmonth" class="form-control textbox_color">
        <option VALUE="01" selected>01</option>
        <option VALUE="02">02</option>
        <option VALUE="03">03</option>
        <option VALUE="04">04</option>
        <option VALUE="05">05</option>
        <option VALUE="06">06</option>
        <option VALUE="07">07</option>
        <option VALUE="08">08</option>
        <option VALUE="09">09</option>
        <option VALUE="10">10</option>
        <option VALUE="11">11</option>
        <option VALUE="12">12</option>
    </select>
</div>
<div class="col-sm-2 controls">
    <label class="control-label">YY</label>
    <select NAME="byear" class="form-control textbox_color">
        <option VALUE="1910" SELECTED>1910</option>
        <option VALUE="1911">1911</option>
        <option VALUE="1912">1912</option>
        <option VALUE="1913">1913</option>
        <option VALUE="1914">1914</option>
        <option VALUE="1915">1915</option>
        <option VALUE="1916">1916</option>
        <option VALUE="1917">1917</option>
        <option VALUE="1918">1918</option>
        <option VALUE="1919">1919</option>
        <option VALUE="1920">1920</option>
        <option VALUE="1921">1921</option>
        <option VALUE="1922">1922</option>
        <option VALUE="1923">1923</option>
        <option VALUE="1924">1924</option>
        <option VALUE="1925">1925</option>
        <option VALUE="1926">1926</option>
        <option VALUE="1927">1927</option>
        <option VALUE="1928">1928</option>
        <option VALUE="1929">1929</option>
        <option VALUE="1930">1930</option>
        <option VALUE="1931">1931</option>
        <option VALUE="1932">1932</option>
        <option VALUE="1933">1933</option>
        <option VALUE="1934">1934</option>
        <option VALUE="1935">1935</option>
        <option VALUE="1936">1936</option>
        <option VALUE="1937">1937</option>
        <option VALUE="1938">1938</option>
        <option VALUE="1939">1939</option>
        <option VALUE="1940">1940</option>
        <option VALUE="1941">1941</option>
        <option VALUE="1942">1942</option>
        <option VALUE="1943">1943</option>
        <option VALUE="1944">1944</option>
        <option VALUE="1945">1945</option>
        <option VALUE="1946">1946</option>
        <option VALUE="1947">1947</option>
        <option VALUE="1948">1948</option>
        <option VALUE="1949">1949</option>
        <option VALUE="1950">1950</option>
        <option VALUE="1951">1951</option>
        <option VALUE="1952">1952</option>
        <option VALUE="1953">1953</option>
        <option VALUE="1954">1954</option>
        <option VALUE="1955">1955</option>
        <option VALUE="1956">1956</option>
        <option VALUE="1957">1957</option>
        <option VALUE="1958">1958</option>
        <option VALUE="1959">1959</option>
        <option VALUE="1960">1960</option>
        <option VALUE="1961">1961</option>
        <option VALUE="1962">1962</option>
        <option VALUE="1963">1963</option>
        <option VALUE="1964">1964</option>
        <option VALUE="1965">1965</option>
        <option VALUE="1966">1966</option>
        <option VALUE="1967">1967</option>
        <option VALUE="1968">1968</option>
        <option VALUE="1969">1969</option>
        <option VALUE="1970">1970</option>
        <option VALUE="1971">1971</option>
        <option VALUE="1972">1972</option>
        <option VALUE="1973">1973</option>
        <option VALUE="1974">1974</option>
        <option VALUE="1975">1975</option>
        <option VALUE="1976">1976</option>
        <option VALUE="1977">1977</option>
        <option VALUE="1978">1978</option>
        <option VALUE="1979">1979</option>
        <option VALUE="1980">1980</option>
        <option VALUE="1981">1981</option>
        <option VALUE="1982">1982</option>
        <option VALUE="1983">1983</option>
        <option VALUE="1984">1984</option>
        <option VALUE="1985">1985</option>
        <option VALUE="1986">1986</option>
        <option VALUE="1987">1987</option>
        <option VALUE="1988">1988</option>
        <option VALUE="1989">1989</option>
        <option VALUE="1990">1990</option>
        <option VALUE="1991">1991</option>
        <option VALUE="1992">1992</option>
        <option VALUE="1993">1993</option>
        <option VALUE="1994">1994</option>
        <option VALUE="1995">1995</option>
        <option VALUE="1996">1996</option>
        <option VALUE="1997">1997</option>
        <option VALUE="1998">1998</option>
        <option VALUE="1999">1999</option>
        <option VALUE="2000">2000</option>
        <option VALUE="2001">2001</option>
        <option VALUE="2002">2002</option>
        <option VALUE="2003">2003</option>
        <option VALUE="2004">2004</option>
        <option VALUE="2005">2005</option>
        <option VALUE="2006">2006</option>
        <option VALUE="2007">2007</option>
        <option VALUE="2008">2008</option>
        <option VALUE="2009">2009</option>
        <option VALUE="2010">2010</option>
    </select>
</div>
</div>
            </div>
        </div>
    </section>