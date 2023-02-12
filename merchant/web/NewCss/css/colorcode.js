/**
 * Created by Nikhil Poojari on 30-Nov-17.
 */

var getid = document.getElementById("widgetcolor_current").value;
var getid2 = document.getElementById("widgetcolor_default").value;
if (getid == 'GradientGreen')
{
    document.getElementById("colorcode").value = "#4d906e,#abb7b7,#edce8c,#303030";
}
else if (getid == 'GradientBlue')
{
    document.getElementById("colorcode").value = "#03b0e2,#abb7b7,#edce8c,#303030";
}
else if (getid == 'orange')
{
    document.getElementById("colorcode").value = "#E67E22,#abb7b7,#edce8c,#303030";
}
else if (getid == 'pz')
{
    document.getElementById("colorcode").value = "#68c39f,#abb7b7,#edce8c,#4a525f";
}
else if ((getid == 'null' || getid == '') && (getid2 == 'pz'))
{
    document.getElementById("colorcode").value = "#68c39f,#abb7b7,#edce8c,#4a525f";
}
else if ((getid == 'null' || getid == '' ) && (getid2 == 'flyingmerchant'))
{
    document.getElementById("colorcode").value = "#ab0000,#abb7b7,#edce8c,#303030";
}
else if ((getid == 'null' || getid == '') && (getid2 == 'condorgaming'))
{
    document.getElementById("colorcode").value = "#e91e63,#00bcd4,#dd3333,#323437";
}
else if ((getid == 'null' || getid == '') && (getid2 == 'igamingcloud'))
{
    document.getElementById("colorcode").value = "#704b44,#9bd6ba,#c7e0dc,#323437";
}
else if ((getid == 'null' || getid == '') && (getid2 =='alea'))
{

    document.getElementById("colorcode").value = "#ee614a,#f4b147,#95b4db,#998ba8,#4ca967,#f2824c";
}
else
{
    document.getElementById("colorcode").value = "#68c39f,#abb7b7,#edce8c,#4a525f";
}
/*var colorcd = document.getElementById("colorcode").value;
 var widget1 = colorcd.split("|")[0];
 var widget2 = colorcd.split("|")[1];
 var widget3 = colorcd.split("|")[2];
 var widget4 = colorcd.split("|")[3];

 document.getElementById("colorcode1").value = widget1;
 document.getElementById("colorcode2").value = widget2;
 document.getElementById("colorcode3").value = widget3;
 document.getElementById("colorcode4").value = widget4;*/
