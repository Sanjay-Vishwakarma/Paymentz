/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 3/9/15
 * Time: 4:48 PM
 * To change this template use File | Settings | File Templates.
 */


function myjunk1(fromaction,toaction)
{
    if(this.document.myformname[fromaction].value!=null)
    {
        var hat1 = this.document.myformname[fromaction].selectedIndex ;
        var hatto1 = this.document.myformname[fromaction].options[hat1].value;
        if (hatto1 != '')
        {
            document.myformname[toaction].value = hatto1.split("|")[0];
            this.document.myformname[toaction].value = hatto1.split("|")[1];
            if( hatto1.split("|")[2]=="N")
            {

                document.myformname.EURegistrationNumber.disabled = true
                document.myformname.registered_corporatename.disabled = true
                document.myformname.registered_directors.disabled = true
                document.myformname.registered_directors_country.disabled = true
                document.myformname.registered_directors_street.disabled = true
                $(document.getElementsByName("registered_directors_country")[0]).css('background-color','#EBEBE4');
                document.myformname.registered_directors_address.disabled = true
                document.myformname.registered_directors_city.disabled = true
                document.myformname.registered_directors_State.disabled = true
                document.myformname.registered_directors_postalcode.disabled = true

            }
            else
            {

                document.myformname.EURegistrationNumber.disabled = false
                document.myformname.registered_corporatename.disabled = false
                document.myformname.registered_directors.disabled = false
                document.myformname.registered_directors_country.disabled = false
                document.myformname.registered_directors_street.disabled = false
                $(document.getElementsByName("registered_directors_country")[0]).css('background-color','#ffffff');
                document.myformname.registered_directors_address.disabled = false
                document.myformname.registered_directors_city.disabled = false
                document.myformname.registered_directors_State.disabled = false
                document.myformname.registered_directors_postalcode.disabled = false

            }

            this.document.myformname[fromaction].options[hat1].selected=true
        }
        else
        {
            document.myformname[toaction].value = "";
            this.document.myformname[toaction].value = "";
        }
    }

}

function mycountrycode(fromaction,toaction)
{
    if(this.document.myformname[fromaction].value!=null)//this.document.forms["myformname"][fromaction].value!=null
    {
        var hat1 = this.document.myformname[fromaction].selectedIndex ;
        var hatto1 = this.document.myformname[fromaction].options[hat1].value;
        if (hatto1 != '')
        {
            document.myformname[toaction].value = hatto1.split("|")[0];
            this.document.myformname[toaction].value = hatto1.split("|")[1];
            if( hatto1.split("|")[2]=="N")
            {


            }
            else
            {

            }

            this.document.myformname[fromaction].options[hat1].selected=true
        }
        else
        {
            document.myformname[toaction].value = "";
            this.document.myformname[toaction].value = "";
        }
    }

}
