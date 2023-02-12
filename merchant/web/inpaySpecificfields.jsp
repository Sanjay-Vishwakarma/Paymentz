<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: 1/21/15
  Time: 3:03 PM
  To change this template use File | Settings | File Templates.
--%>

<div class="row">
    <div class="col-sm-12 portlets ui-sortable">
        <div class="widget">

            <div class="widget-header transparent">
                <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;Billing Address (where you receive your Credit Card bills)</strong></h2>
                <div class="additional-btn">
                    <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                    <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                    <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                </div>
            </div>

            <br>

            <div class="widget-content padding">

                <div class="form-group col-md-6">
                    <label>First Name</label>
                    <input type="text" title="" name="firstname" size="50"  maxlength="50" class="form-control" >
                </div>

                <div class="form-group col-md-6">
                    <label>Last Name</label>
                    <input type="text" name="lastname" size="50" title="" maxlength="50" class="form-control">
                </div>

                <div class="form-group col-md-4">
                    <label>Address</label>
                    <input type="text" title="" name="street" size="40"  maxlength="150" class="form-control" >
                </div>

                <div class="form-group col-md-4">
                    <label>City</label>
                    <input type="text" name="city" size="20" title="" maxlength="35" class="form-control">
                </div>

                <div class="form-group col-md-4">
                    <label >Zip Code</label>
                    <input type="text" name="zip" size="10" title="" maxlength="15" class="form-control">
                </div>

                <div class="form-group col-md-4">
                    <label>ISO Country Code</label>
                    <select name="country" onchange="myjunk();"  class="form-control">
                        <option value="Select one">Select a Country</option>
                        <option value="AF|093">Afghanistan</option>
                        <option value="AX|358">Aland Islands</option>
                        <option value="AL|355">Albania</option>
                        <option value="DZ|231">Algeria</option>
                        <option value="AS|684">American Samoa</option>
                        <option value="AD|376">Andorra</option>
                        <option value="AO|244">Angola</option>
                        <option value="AI|001">Anguilla</option>
                        <option value="AQ|000">Antarctica</option>
                        <option value="AG|001">Antigua and Barbuda</option>
                        <option value="AR|054">Argentina</option>
                        <option value="AM|374">Armenia</option>
                        <option value="AW|297">Aruba</option>
                        <option value="AU|061">Australia</option>
                        <option value="AT|043">Austria</option>
                        <option value="AZ|994">Azerbaijan</option>
                        <option value="BS|001">Bahamas</option>
                        <option value="BH|973">Bahrain</option>
                        <option value="BD|880">Bangladesh</option>
                        <option value="BB|001">Barbados</option>
                        <option value="BY|375">Belarus</option>
                        <option value="BE|032">Belgium</option>
                        <option value="BZ|501">Belize</option>
                        <option value="BJ|229">Benin</option>
                        <option value="BM|001">Bermuda</option>
                        <option value="BT|975">Bhutan</option>
                        <option value="BO|591">Bolivia</option>
                        <option value="BA|387">Bosnia and Herzegovina</option>
                        <option value="BW|267">Botswana</option>
                        <option value="BV|000">Bouvet Island</option>
                        <option value="BR|055">Brazil</option>
                        <option value="IO|246">British Indian Ocean Territory</option>
                        <option value="VG|001">British Virgin Islands</option>
                        <option value="BN|673">Brunei</option>
                        <option value="BG|359">Bulgaria</option>
                        <option value="BF|226">Burkina Faso</option>
                        <option value="BI|257">Burundi</option>
                        <option value="KH|855">Cambodia</option>
                        <option value="CM|237">Cameroon</option>
                        <option value="CA|001">Canada</option>
                        <option value="CV|238">Cape Verde</option>
                        <option value="KY|001">Cayman Islands</option>
                        <option value="CF|236">Central African Republic</option>
                        <option value="TD|235">Chad</option>
                        <option value="CL|056">Chile</option>
                        <option value="CN|086">China</option>
                        <option value="CX|061">Christmas Island</option>
                        <option value="CC|061">Cocos (Keeling) Islands</option>
                        <option value="CO|057">Colombia</option>
                        <option value="KM|269">Comoros</option>
                        <option value="CK|682">Cook Islands</option>
                        <option value="CR|506">Costa Rica</option>
                        <option value="CI|225">Cote d'Ivoire</option>
                        <option value="HR|385">Croatia</option>
                        <option value="CU|053">Cuba</option>
                        <option value="CY|357">Cyprus</option>
                        <option value="CZ|420">Czech Republic</option>
                        <option value="CD|243">Democratic Republic of the Congo</option>
                        <option value="DK|045">Denmark</option>
                        <option value="DJ|253">Djibouti</option>
                        <option value="DM|001">Dominica</option>
                        <option value="DO|001">Dominican Republic</option>
                        <option value="EC|593">Ecuador</option>
                        <option value="EG|020">Egypt</option>
                        <option value="SV|503">El Salvador</option>
                        <option value="GQ|240">Equatorial Guinea</option>
                        <option value="ER|291">Eritrea</option>
                        <option value="EE|372">Estonia</option>
                        <option value="ET|251">Ethiopia</option>
                        <option value="FK|500">Falkland Islands</option>
                        <option value="FO|298">Faroe Islands</option>
                        <option value="FJ|679">Fiji</option>
                        <option value="FI|358">Finland</option>
                        <option value="FR|033">France</option>
                        <option value="GF|594">French Guiana</option>
                        <option value="PF|689">French Polynesia</option>
                        <option value="TF|000">French Southern and Antarctic Lands</option>
                        <option value="GA|241">Gabon</option>
                        <option value="GM|220">Gambia</option>
                        <option value="GE|995">Georgia</option>
                        <option value="DE|049">Germany</option>
                        <option value="GH|233">Ghana</option>
                        <option value="GI|350">Gibraltar</option>
                        <option value="GR|030">Greece</option>
                        <option value="GL|299">Greenland</option>
                        <option value="GD|001">Grenada</option>
                        <option value="GP|590">Guadeloupe</option>
                        <option value="GU|001">Guam</option>
                        <option value="GT|502">Guatemala</option>
                        <option value="GG|000">Guernsey</option>
                        <option value="GN|224">Guinea</option>
                        <option value="GW|245">Guinea-Bissau</option>
                        <option value="GY|592">Guyana</option>
                        <option value="HT|509">Haiti</option>
                        <option value="HM|672">Heard Island & McDonald Islands</option>
                        <option value="HN|504">Honduras</option>
                        <option value="HK|852">Hong Kong</option>
                        <option value="HU|036">Hungary</option>
                        <option value="IS|354">Iceland</option>
                        <option value="IN|091">India</option>
                        <option value="ID|062">Indonesia</option>
                        <option value="IR|098">Iran</option>
                        <option value="IQ|964">Iraq</option>
                        <option value="IE|353">Ireland</option>
                        <option value="IL|972">Israel</option>
                        <option value="IT|039">Italy</option>
                        <option value="JM|001">Jamaica</option>
                        <option value="JP|081">Japan</option>
                        <option value="JE|044">Jersey</option>
                        <option value="JO|962">Jordan</option>
                        <option value="KZ|007">Kazakhstan</option>
                        <option value="KE|254">Kenya</option>
                        <option value="KI|686">Kiribati</option>
                        <option value="KW|965">Kuwait</option>
                        <option value="KG|996">Kyrgyzstan</option>
                        <option value="LA|856">Laos</option>
                        <option value="LV|371">Latvia</option>
                        <option value="LB|961">Lebanon</option>
                        <option value="LS|266">Lesotho</option>
                        <option value="LR|231">Liberia</option>
                        <option value="LY|218">Libya</option>
                        <option value="LI|423">Liechtenstein</option>
                        <option value="LT|370">Lithuania</option>
                        <option value="LU|352">Luxembourg</option>
                        <option value="MO|853">Macau, China</option>
                        <option value="MK|389">Macedonia</option>
                        <option value="MG|261">Madagascar</option>
                        <option value="MW|265">Malawi</option>
                        <option value="MY|060">Malaysia</option>
                        <option value="MV|960">Maldives</option>
                        <option value="ML|223">Mali</option>
                        <option value="MT|356">Malta</option>
                        <option value="MH|692">Marshall Islands</option>
                        <option value="MQ|596">Martinique</option>
                        <option value="MR|222">Mauritania</option>
                        <option value="MU|230">Mauritius</option>
                        <option value="YT|269">Mayotte</option>
                        <option value="MX|052">Mexico</option>
                        <option value="FM|691">Micronesia, Federated States of</option>
                        <option value="MD|373">Moldova</option>
                        <option value="MC|377">Monaco</option>
                        <option value="MN|976">Mongolia</option>
                        <option value="ME|382">Montenegro</option>
                        <option value="MS|001">Montserrat</option>
                        <option value="MA|212">Morocco</option>
                        <option value="MZ|258">Mozambique</option>
                        <option value="MM|095">Myanmar</option>
                        <option value="NA|264">Namibia</option>
                        <option value="NR|674">Nauru</option>
                        <option value="NP|977">Nepal</option>
                        <option value="AN|599">Netherlands Antilles</option>
                        <option value="NL|031">Netherlands</option>
                        <option value="NC|687">New Caledonia</option>
                        <option value="NZ|064">New Zealand</option>
                        <option value="NI|505">Nicaragua</option>
                        <option value="NE|227">Niger</option>
                        <option value="NG|234">Nigeria</option>
                        <option value="NU|683">Niue</option>
                        <option value="NF|672">Norfolk Island</option>
                        <option value="KP|850">North Korea</option>
                        <option value="MP|001">Northern Mariana Islands</option>
                        <option value="NO|047">Norway</option>
                        <option value="OM|968">Oman</option>
                        <option value="PK|092">Pakistan</option>
                        <option value="PW|680">Palau</option>
                        <option value="PS|970">Palestinian Authority</option>
                        <option value="PA|507">Panama</option>
                        <option value="PG|675">Papua New Guinea</option>
                        <option value="PY|595">Paraguay</option>
                        <option value="PE|051">Peru</option>
                        <option value="PH|063">Philippines</option>
                        <option value="PN|064">Pitcairn Islands</option>
                        <option value="PL|048">Poland</option>
                        <option value="PT|351">Portugal</option>
                        <option value="PR|001">Puerto Rico</option>
                        <option value="QA|974">Qatar</option>
                        <option value="CG|242">Republic of the Congo</option>
                        <option value="RE|262">Reunion</option>
                        <option value="RO|040">Romania</option>
                        <option value="RU|007">Russia</option>
                        <option value="RW|250">Rwanda</option>
                        <option value="BL|590">Saint Barthelemy</option>
                        <option value="SH|290">Saint Helena, Ascension & Tristan daCunha</option>
                        <option value="KN|001">Saint Kitts and Nevis</option>
                        <option value="LC|001">Saint Lucia</option>
                        <option value="MF|590">Saint Martin</option>
                        <option value="PM|508">Saint Pierre and Miquelon</option>
                        <option value="VC|001">Saint Vincent and Grenadines</option>
                        <option value="WS|685">Samoa</option>
                        <option value="SM|378">San Marino</option>
                        <option value="ST|239">Sao Tome and Principe</option>
                        <option value="SA|966">Saudi Arabia</option>
                        <option value="SN|221">Senegal</option>
                        <option value="RS|381">Serbia</option>
                        <option value="SC|248">Seychelles</option>
                        <option value="SL|232">Sierra Leone</option>
                        <option value="SG|065">Singapore</option>
                        <option value="SK|421">Slovakia</option>
                        <option value="SI|386">Slovenia</option>
                        <option value="SB|677">Solomon Islands</option>
                        <option value="SO|252">Somalia</option>
                        <option value="ZA|027">South Africa</option>
                        <option value="GS|000">South Georgia & South Sandwich Islands</option>
                        <option value="KR|082">South Korea</option>
                        <option value="ES|034">Spain</option>
                        <option value="LK|094">Sri Lanka</option>
                        <option value="SD|249">Sudan</option>
                        <option value="SR|597">Suriname</option>
                        <option value="SJ|047">Svalbard and Jan Mayen</option>
                        <option value="SZ|268">Swaziland</option>
                        <option value="SE|046">Sweden</option>
                        <option value="CH|041">Switzerland</option>
                        <option value="SY|963">Syria</option>
                        <option value="TW|886">Taiwan</option>
                        <option value="TJ|992">Tajikistan</option>
                        <option value="TZ|255">Tanzania</option>
                        <option value="TH|066">Thailand</option>
                        <option value="TL|670">Timor-Leste</option>
                        <option value="TG|228">Togo</option>
                        <option value="TK|690">Tokelau</option>
                        <option value="TO|676">Tonga</option>
                        <option value="TT|001">Trinidad and Tobago</option>
                        <option value="TN|216">Tunisia</option>
                        <option value="TR|090">Turkey</option>
                        <option value="TM|993">Turkmenistan</option>
                        <option value="TC|001">Turks and Caicos Islands</option>
                        <option value="TV|688">Tuvalu</option>
                        <option value="UG|256">Uganda</option>
                        <option value="UA|380">Ukraine</option>
                        <option value="AE|971">United Arab Emirates</option>
                        <option value="GB|044">United Kingdom</option>
                        <option value="US|001">United States</option>
                        <option value="VI|001">United States Virgin Islands</option>
                        <option value="UY|598">Uruguay</option>
                        <option value="UZ|998">Uzbekistan</option>
                        <option value="VU|678">Vanuatu</option>
                        <option value="VA|379">Vatican City</option>
                        <option value="VE|058">Venezuela</option>
                        <option value="VN|084">Vietnam</option>
                        <option value="WF|681">Wallis and Futuna</option>
                        <option value="EH|212">Western Sahara</option>
                        <option value="YE|967">Yemen</option>
                        <option value="ZM|260">Zambia</option>
                        <option value="ZW|263">Zimbabwe</option>
                    </select>
                </div>
                <div class="form-group col-md-2">
                    <label>Country Code</label>
                    <input type="text" name="countrycode" class="form-control" readonly="readonly">
                </div>

                <div class="form-group col-md-4">
                    <label>State</label>
                    <select name="us_states" id="us_states_id" onchange="usstate();" class="form-control">
                        <option value="Select one">Select a State for US</option>
                        <option value="AL">ALABAMA</option>
                        <option value="AK">ALASKA</option>
                        <option value="AS">AMERICAN SAMOA</option>
                        <option value="AZ">ARIZONA</option>
                        <option value="AR">ARKANSAS</option>
                        <option value="CA">CALIFORNIA</option>
                        <option value="CO">COLORADO</option>
                        <option value="CT">CONNECTICUT</option>
                        <option value="DE">DELAWARE</option>
                        <option value="DC">DISTRICT OF COLUMBIA</option>
                        <option value="FM">FEDERATED STATES OF MICRONESIA</option>
                        <option value="FL">FLORIDA</option>
                        <option value="GA">GEORGIA</option>
                        <option value="GU">GUAM GU</option>
                        <option value="HI">HAWAII</option>
                        <option value="ID">IDAHO</option>
                        <option value="IL">ILLINOIS</option>
                        <option value="IN">INDIANA</option>
                        <option value="IA">IOWA</option>
                        <option value="KS">KANSAS</option>
                        <option value="KY">KENTUCKY</option>
                        <option value="LA">LOUISIANA</option>
                        <option value="ME">MAINE</option>
                        <option value="MH">MARSHALL ISLANDS</option>
                        <option value="MD">MARYLAND</option>
                        <option value="MA">MASSACHUSETTS</option>
                        <option value="MI">MICHIGAN</option>
                        <option value="MN">MINNESOTA</option>
                        <option value="MS">MISSISSIPPI</option>
                        <option value="MO">MISSOURI</option>
                        <option value="MT">MONTANA</option>
                        <option value="NE">NEBRASKA</option>
                        <option value="NV">NEVADA</option>
                        <option value="NH">NEW HAMPSHIRE</option>
                        <option value="NJ">NEW JERSEY</option>
                        <option value="NM">NEW MEXICO</option>
                        <option value="NY">NEW YORK</option>
                        <option value="NC">NORTH CAROLINA</option>
                        <option value="ND">NORTH DAKOTA</option>
                        <option value="MP">NORTHERN MARIANA ISLANDS</option>
                        <option value="OH">OHIO</option>
                        <option value="OK">OKLAHOMA</option>
                        <option value="OR">OREGON</option>
                        <option value="PW">PALAU</option>
                        <option value="PA">PENNSYLVANIA</option>
                        <option value="PR">PUERTO RICO</option>
                        <option value="RI">CRHODE ISLAND</option>
                        <option value="SC">SOUTH CAROLINA</option>
                        <option value="SD">SOUTH DAKOTA</option>
                        <option value="TN">TENNESSEE</option>
                        <option value="TX">TEXAS</option>
                        <option value="UT">UTAH</option>
                        <option value="VT">VERMONT</option>
                        <option value="VI">VIRGIN ISLANDS</option>
                        <option value="VA">VIRGINIA</option>
                        <option value="WA">WASHINGTON</option>
                        <option value="WV">WEST VIRGINIA</option>
                        <option value="WI">WISCONSIN</option>
                        <option value="WY">WYOMING</option>

                    </select>
                </div>
                <div class="form-group col-md-2">
                    <label>State Code</label>
                    <input name="state" type="text" id="b_state" class="form-control" readonly="readonly">
                </div>

                <div class="form-group col-md-2">
                    <label>Phone CC</label>
                    <input type="text" name="telnocc" class="form-control" readonly="readonly" title="Example(Country Code - Phone Number)">
                </div>
                <div class="form-group col-md-4">
                    <label>Phone Number</label>
                    <input type="text" name="telno" size="20" maxlength="20" class="form-control" title="Example(Country Code - Phone Number)">
                </div>

                <div class="form-group col-md-6">
                    <label>Email ID</label>
                    <input type="text" name="emailaddr" title="Ex: abc@xyz.com" value="" class="form-control"/>
                </div>
            </div>
        </div>
    </div>
</div>