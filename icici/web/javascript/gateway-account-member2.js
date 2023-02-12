$(function(){

    $('#bank1').on('change', function(){
        var val = $(this).val();
        var sub = $('#memberid1');
        var sub2 = $('#accountid1');
        var val2 = $('#accountid1').val();


        if(val == '0') {

            // Populate Currency drop down

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


            if(val2 == '0') {


                sub.find('option').not(':first').hide();


                $('option', sub).filter(function(){

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
            else {
                sub.find('option').not(':first').hide();


                $('option', sub).filter(function(){


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



                    if($(this).attr('data-accid') == val2){

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
        }
        else {


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




                if($(this).attr('data-bank') == val){

                    if(navigator.appName == 'Microsoft Internet Explorer'|| navigator.appName =='Netscape') 												{

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



            if(val2 == '0') {


                sub.find('option').not(':first').hide();

                $('option', sub).filter(function(){


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




                    if($(this).attr('data-bank') == val){

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
                    }



                });
            }
            else {
                sub.find('option').not(':first').hide();

                $('option', sub).filter(function(){


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



                    if($(this).attr('data-bank') == val && $(this).attr('data-accid') == val2){

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
                    }



                });
            }

        }

        sub.val(0);
        //sub2.val(0);
    });

});


$(function(){

    $('#accountid1').on('change', function(){
        var val = $(this).val();
        var sub = $('#memberid1');
        var val2 = $('#bank1').val();
        if(val == '0') {
            if(val2 == '0')
            {
                sub.find('option').not(':first').hide();

                $('option', sub).filter(function()
                {
                    if(navigator.appName == 'Microsoft Internet Explorer'|| navigator.appName =='Netscape') {
                        if (this.nodeName.toUpperCase() === 'OPTION')
                        {
                            var span = $(this).parent();
                            var opt = this;
                            $(opt).show();
                            if($(this).parent().is('span'))
                            {
                                //$(opt).show();
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

                });


            }
            else {
                sub.find('option').not(':first').hide();

                $('option', sub).filter(function(){

                    if(navigator.appName == 'Microsoft Internet Explorer'|| navigator.appName =='Netscape') {
                        if (this.nodeName.toUpperCase() === 'OPTION')
                        {
                            var span = $(this).parent();
                            var opt = this;
                            //$(opt).show();
                            if($(this).parent().is('span'))
                            {
                                $(span).replaceWith(opt);
                            }
                            if($(this).attr('value') != "0")
                            {
                                $(opt).wrap((navigator.appName == 'Microsoft Internet Explorer'|| navigator.appName =='Netscape') ? '<span>' : null);
                            }
                        }
                    }


                    if($(this).attr('data-bank') == val2){

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
                    }

                });



            }
        }
        else {

            if(val2 == '0') {
                sub.find('option').not(':first').hide();

                $('option', sub).filter(function(){

                    if(navigator.appName == 'Microsoft Internet Explorer'|| navigator.appName =='Netscape') {
                        if (this.nodeName.toUpperCase() === 'OPTION')
                        {
                            var span = $(this).parent();
                            var opt = this;
                            if($(this).parent().is('span'))
                            {
                                //$(opt).show();
                                $(span).replaceWith(opt);
                            }
                            if($(this).attr('value') != "0")
                            {
                                $(opt).wrap((navigator.appName == 'Microsoft Internet Explorer'|| navigator.appName =='Netscape') ? '<span>' : null);
                            }
                        }
                    }


                    if($(this).attr('data-accid') == val){
                        if(navigator.appName == 'Microsoft Internet Explorer'|| navigator.appName =='Netscape') {
                            if (this.nodeName.toUpperCase() === 'OPTION') {
                                var span = $(this).parent();
                                var opt = this;
                                $(opt).show();
                                if($(this).parent().is('span')) {
                                    //$(opt).show();
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
            else {
                sub.find('option').not(':first').hide();

                $('option', sub).filter(function(){

                    if(navigator.appName == 'Microsoft Internet Explorer'|| navigator.appName =='Netscape') {
                        if (this.nodeName.toUpperCase() === 'OPTION')
                        {
                            var span = $(this).parent();
                            var opt = this;
                            if($(this).parent().is('span'))
                            {
                                //$(opt).show();
                                $(span).replaceWith(opt);
                            }
                            if($(this).attr('value') != "0")
                            {
                                $(opt).wrap((navigator.appName == 'Microsoft Internet Explorer'|| navigator.appName =='Netscape') ? '<span>' : null);
                            }
                        }
                    }


                    if($(this).attr('data-accid') == val && $(this).attr('data-bank') == val2){
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
                    }
                });
            }

        }
        sub.val(0);
    });
});


$(function(){


    var val = $('#bank1').val();
    var sub = $('#memberid1');
    var sub2 = $('#accountid1');
    var val2 = $('#accountid1').val();


    if(val == '0') {

        // Populate Currency drop down

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


        if(val2 == '0') {


            sub.find('option').not(':first').hide();


            $('option', sub).filter(function(){

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
        else {
            sub.find('option').not(':first').hide();


            $('option', sub).filter(function(){


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



                if($(this).attr('data-accid') == val2){

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
    }
    else {


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




            if($(this).attr('data-bank') == val){

                if(navigator.appName == 'Microsoft Internet Explorer'|| navigator.appName =='Netscape') 												{

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



        if(val2 == '0') {


            sub.find('option').not(':first').hide();

            $('option', sub).filter(function(){


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




                if($(this).attr('data-bank') == val){

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
                }



            });
        }
        else {
            sub.find('option').not(':first').hide();

            $('option', sub).filter(function(){


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



                if($(this).attr('data-bank') == val && $(this).attr('data-accid') == val2){
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
                }



            });
        }

    }

    //sub.val(0);
    //sub2.val(0);


});

