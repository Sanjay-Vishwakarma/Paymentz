/**
 * Created by admin on 24-07-2017.
 */

/**
 * Created by admin on 12-04-2017.
 */
$(function(){
    $('#mid').on('change', function(){

        var val = $(this).val();
        var sub2 = $('#tid');
        // alert("function---");
        if(val == '0')//gateway all
        {
            //alert("if---");
            sub2.find('option').not(':first').hide();
            $('option', sub2).filter(function(){

                if(navigator.appName == 'Microsoft Internet Explorer'|| navigator.appName =='Netscape') {

                    if (this.nodeName.toUpperCase() === 'OPTION') {
                        var span = $(this).parent();
                        var opt = this;

                        if($(this).parent().is('span')) {

                            $(span).replaceWith(opt);

                        }
                        if($(this).attr('value') != "0")
                        {
                            $(opt).wrap((navigator.appName == 'Microsoft Internet Explorer'|| navigator.appName =='Netscape') ? '<span>' : null);
                        }


                    }

                }


                if(navigator.appName == 'Microsoft Internet Explorer'|| navigator.appName =='Netscape') {
                    if (this.nodeName.toUpperCase() === 'OPTION') {
                        var span = $(this).parent();
                        var opt = this;
                        $(opt).show();
                        if($(this).parent().is('span')) {

                            $(span).replaceWith(opt);
                        }
                    }
                } else {
                    $(this).show(); //all other browsers use standard .show()
                }

                //$(this).show();

            });
        }
        else
        {
            sub2.find('option').not(':first').hide();


            $('option', sub2).filter(function(){


                if(navigator.appName == 'Microsoft Internet Explorer'|| navigator.appName =='Netscape') {

                    if (this.nodeName.toUpperCase() === 'OPTION') {
                        var span = $(this).parent();
                        var opt = this;

                        if($(this).parent().is('span')) {

                            $(span).replaceWith(opt);

                        }
                        if($(this).attr('value') != "0")
                        {
                            $(opt).wrap((navigator.appName == 'Microsoft Internet Explorer'|| navigator.appName =='Netscape') ? '<span>' : null);
                        }


                    }

                }



                if($(this).attr('data-mid') == val || $(this).attr('data-mid') == "ALL"){

                    if(navigator.appName == 'Microsoft Internet Explorer'|| navigator.appName =='Netscape') 													{
                        if (this.nodeName.toUpperCase() === 'OPTION') {
                            var span = $(this).parent();
                            var opt = this;
                            $(opt).show();
                            if($(this).parent().is('span')) {

                                $(span).replaceWith(opt);
                            }
                        }
                    } else {
                        $(this).show(); //all other browsers use standard .show()
                    }

                    //$(this).show();
                }


            });
        }
        sub2.val(0);
    });
});

$(function(){
    var val = $('#mid').val();
    var sub2 = $('#tid');
    //  alert("function--2-");
    if(val == '0')//gateway all
    {
        //alert("if---");
        sub2.find('option').not(':first').hide();
        $('option', sub2).filter(function(){

            if(navigator.appName == 'Microsoft Internet Explorer'|| navigator.appName =='Netscape') {

                if (this.nodeName.toUpperCase() === 'OPTION') {
                    var span = $(this).parent();
                    var opt = this;

                    if($(this).parent().is('span')) {

                        $(span).replaceWith(opt);

                    }
                    if($(this).attr('value') != "0")
                    {
                        $(opt).wrap((navigator.appName == 'Microsoft Internet Explorer'|| navigator.appName =='Netscape') ? '<span>' : null);
                    }


                }

            }


            if(navigator.appName == 'Microsoft Internet Explorer'|| navigator.appName =='Netscape') {
                if (this.nodeName.toUpperCase() === 'OPTION') {
                    var span = $(this).parent();
                    var opt = this;
                    $(opt).show();
                    if($(this).parent().is('span')) {

                        $(span).replaceWith(opt);
                    }
                }
            } else {
                $(this).show(); //all other browsers use standard .show()
            }

            //$(this).show();

        });
    }
    else
    {
        sub2.find('option').not(':first').hide();


        $('option', sub2).filter(function(){


            if(navigator.appName == 'Microsoft Internet Explorer'|| navigator.appName =='Netscape') {

                if (this.nodeName.toUpperCase() === 'OPTION') {
                    var span = $(this).parent();
                    var opt = this;

                    if($(this).parent().is('span')) {

                        $(span).replaceWith(opt);

                    }
                    if($(this).attr('value') != "0")
                    {
                        $(opt).wrap((navigator.appName == 'Microsoft Internet Explorer'|| navigator.appName =='Netscape') ? '<span>' : null);
                    }


                }

            }

            if($(this).attr('data-mid') == val || $(this).attr('data-mid') == "ALL"){

                if(navigator.appName == 'Microsoft Internet Explorer'|| navigator.appName =='Netscape') 													{
                    if (this.nodeName.toUpperCase() === 'OPTION') {
                        var span = $(this).parent();
                        var opt = this;
                        $(opt).show();
                        if($(this).parent().is('span')) {

                            $(span).replaceWith(opt);
                        }
                    }
                } else {
                    $(this).show(); //all other browsers use standard .show()
                }

                //$(this).show();
            }
        });
    }
    //sub2.val(0);
});

