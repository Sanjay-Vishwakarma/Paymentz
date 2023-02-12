/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 3/9/15
 * Time: 4:48 PM
 * To change this template use File | Settings | File Templates.
 */




function myjunk1(fromaction,toaction)
{

    if(this.document.forms["myformname"][fromaction].value!=null)
    {

        var hat1 = this.document.forms["myformname"][fromaction].selectedIndex ;

        var hatto1 = this.document.forms["myformname"][fromaction].options[hat1].value;
        if (hatto1 != '')
        {

            document.forms["myformname"][toaction].value = hatto1.split("|")[0];
            this.document.forms["myformname"][toaction].value = hatto1.split("|")[1];
            if( hatto1.split("|")[2]=="Y")
            {
                document.getElementById("eucompany").checked = false;
                document.getElementById("eucompany").disabled = true;
                document.myformname.EURegistrationNumber.disabled = true;
                document.myformname.registered_corporatename.disabled = true;
                document.myformname.registered_directors.disabled = true;
                document.myformname.registered_directors_country.disabled = true;
                document.myformname.registered_directors_street.disabled = true;
                $(document.getElementsByName("registered_directors_country")[0]).css('background-color','#EBEBE4');
                document.myformname.registered_directors_address.disabled = true;
                document.myformname.registered_directors_city.disabled = true;
                document.myformname.registered_directors_State.disabled = true;
                document.myformname.registered_directors_postalcode.disabled = true;
                document.myformname.registered_directors_addressproof.disabled = true;
                document.myformname.registered_directors_addressId.disabled = true;


            }
            else
            {
                document.getElementById("eucompany").checked = true;
                document.getElementById("eucompany").disabled = false;
                document.myformname.EURegistrationNumber.disabled = false;
                document.myformname.registered_corporatename.disabled = false;
                document.myformname.registered_directors.disabled = false;
                document.myformname.registered_directors_country.disabled = false;
                document.myformname.registered_directors_street.disabled = false;
                $(document.getElementsByName("registered_directors_country")[0]).css('background-color','#ffffff');
                document.myformname.registered_directors_address.disabled = false;
                document.myformname.registered_directors_city.disabled = false;
                document.myformname.registered_directors_State.disabled = false;
                document.myformname.registered_directors_postalcode.disabled = false;
                document.myformname.registered_directors_addressproof.disabled = false;
                document.myformname.registered_directors_addressId.disabled = false;

            }

            this.document.forms["myformname"][fromaction].options[hat1].selected=true
        }
        else
        {
            document.forms["myformname"][toaction].value = "";
            this.document.forms["myformname"][toaction].value = "";
        }
    }

}

function mycountrycode(fromaction,toaction)
{
    if(document.getElementById([fromaction]).value!=null)
    {
        var hat1 = document.getElementById([fromaction]).selectedIndex ;
        var hatto1 = document.getElementById([fromaction]).options[hat1].value;
        //alert("Selected Index="+hat1);
        //alert("Option="+hatto1);
        if (hatto1 != '')
        {

            /*document.forms["myformname"][toaction].value = hatto1.split("|")[0];
            this.document.forms["myformname"][toaction].value = hatto1.split("|")[1];*/
            if( hatto1.split("|")[2]=="N")
            {
                document.getElementById("eucompany").checked = true;
                document.getElementById("eucompany").disabled = false;
                document.myformname.registered_corporatename.disabled = false;
                document.myformname.registered_directors.disabled = false;
                document.myformname.EURegistrationNumber.disabled = false;
                document.myformname.registered_directors_country.disabled = false;
                document.myformname.registered_directors_address.disabled = false;
                document.myformname.registered_directors_city.disabled = false;
                document.myformname.registered_directors_State.disabled = false;
                document.myformname.registered_directors_postalcode.disabled = false;
                document.myformname.registered_directors_addressproof.disabled = false;
                document.myformname.registered_directors_addressId.disabled = false;
                document.myformname.registered_directors_street.disabled = false;
                //$(document.getElementsByName("registered_directors_country")[0]).css('background-color','#ffffff');

            }
            else
            {
                document.getElementById("eucompany").checked = false;
                document.getElementById("eucompany").disabled = true;
                document.myformname.registered_corporatename.disabled = true;
                document.myformname.registered_directors.disabled = true;
                document.myformname.EURegistrationNumber.disabled = true;
                document.myformname.registered_directors_country.disabled = true;
                document.myformname.registered_directors_address.disabled = true;
                document.myformname.registered_directors_city.disabled = true;
                document.myformname.registered_directors_State.disabled = true;
                document.myformname.registered_directors_postalcode.disabled = true;
                document.myformname.registered_directors_addressproof.disabled = true;
                document.myformname.registered_directors_addressId.disabled = true;
                document.myformname.registered_directors_street.disabled = true;
                //$(document.getElementsByName("registered_directors_country")[0]).css('background-color','#EBEBE4');
            }

            document.getElementById([fromaction]).options[hat1].selected=true
        }
        else
        {
            /*document.forms["myformname"][toaction].value = "";
            this.document.forms["myformname"][toaction].value = "";*/
        }
    }

}


