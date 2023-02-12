/**
 * Created by Ajit on 4/13/2018.
 */

$(function () {
    $("#mid").autocomplete({
        source: function( request, response ) {
            $.ajax( {
                url: "/icici/servlet/GetDetailsAPI",
                dataType: "json",
                data: {
                    ctoken: $('#ctoken').val(),
                    method:"memberid",
                    term: request.term
                },
                success: function( data ) {
                    //response( data );

                    response($.map(data, function (value, key) {
                        return {
                            label: value,
                            value: key
                        };
                    }));
                }
            } );
        },
        minLength: 0
    });


    $("#midy").autocomplete({
        source: function( request, response ) {
            $.ajax( {
                url: "/icici/servlet/GetDetailsAPI",
                dataType: "json",
                data: {
                    ctoken: $('#ctoken').val(),
                    method:"ActiveMemberid",
                    term: request.term
                },
                success: function( data ) {
                    //response( data );

                    response($.map(data, function (value, key) {
                        return {
                            label: value,
                            value: key
                        };
                    }));
                }
            } );
        },
        minLength: 0
    });

    $("#pid").autocomplete({
        source: function( request, response ) {
            $.ajax( {
                url: "/icici/servlet/GetDetailsAPI",
                dataType: "json",
                data: {
                    ctoken: $('#ctoken').val(),
                    method:"partnerid",
                    term: request.term
                },
                success: function( data ) {
                    //response( data );

                    response($.map(data, function (value, key) {
                        return {
                            label: value,
                            value: key
                        };
                    }));
                }
            } );
        },
        minLength: 0
    });

    $("#pmid").autocomplete({
        source: function( request, response ) {
            $.ajax( {
                url: "/icici/servlet/GetDetailsAPI",
                dataType: "json",
                data: {
                    ctoken: $('#ctoken').val(),
                    method:"pmemberList",
                    partnerid: $('#pid').val(),
                    term: request.term
                },
                success: function( data ) {
                    //response( data );

                    response($.map(data, function (value, key) {
                        return {
                            label: value,
                            value: key
                        };
                    }));
                }
            } );
        },
        minLength: 0
    });

    $("#pid1").autocomplete({
        source: function( request, response ) {
            $.ajax( {
                url: "/icici/servlet/GetDetailsAPI",
                dataType: "json",
                data: {
                    ctoken: $('#ctoken').val(),
                    method:"partnerid1",
                    term: request.term
                },
                success: function( data ) {
                    //response( data );
                    response($.map(data, function (value, key) {
                        return {
                            label: value,
                            value: key
                        };
                    }));
                }
            } );
        },
        minLength: 0
    });

    $("#allpid").autocomplete({
        source: function( request, response ) {
            $.ajax( {
                url: "/icici/servlet/GetDetailsAPI",
                dataType: "json",
                data: {
                    ctoken: $('#ctoken').val(),
                    method:"allpartnerid",
                    term: request.term
                },
                success: function( data ) {
                    //response( data );

                    response($.map(data, function (value, key) {
                        return {
                            label: value,
                            value: key
                        };
                    }));
                }
            } );
        },
        minLength: 0
    });

    $("#allpid_processor").autocomplete({
        source: function( request, response ) {
            $.ajax( {
                url: "/icici/servlet/GetDetailsAPI",
                dataType: "json",
                data: {
                    ctoken: $('#ctoken').val(),
                    method:"allpartnerid",
                    term: request.term
                },
                success: function( data ) {
                    //response( data );

                    response($.map(data, function (value, key) {
                        return {
                            label: value,
                            value: key
                        };
                    }));
                }
            } );
        },
        minLength: 0
    });

    $("#tid").autocomplete({
        source: function( request, response ) {
            $.ajax( {
                url: "/icici/servlet/GetDetailsAPI",
                dataType: "json",
                data: {
                    ctoken: $('#ctoken').val(),
                    method:"terminalList",
                    memberid: $('#midy').val(),
                    term: request.term


                },
                success: function( data ) {

                    response($.map(data, function (value, key) {
                        return {
                            label: value,
                            value: key
                        };
                    }));


                }
            } );
        },
        minLength: 0


    });

    $("#rn").autocomplete({
        source: function( request, response ) {
            $.ajax( {
                url: "/icici/servlet/GetDetailsAPI",
                dataType: "json",
                data: {
                    ctoken: $('#ctoken').val(),
                    method:"rulename",
                    memberid: $('#midy').val(),
                    terminalid: $('#tid').val(),
                    term: request.term


                },
                success: function( data ) {

                    response($.map(data, function (value, key) {
                        return {
                            label: value,
                            value: key
                        };
                    }));

                }
            } );
        },
        minLength: 0


    });

    $("#agnt").autocomplete({
        source: function( request, response ) {
            $.ajax( {
                url: "/icici/servlet/GetDetailsAPI",
                dataType: "json",
                data: {
                    ctoken: $('#ctoken').val(),
                    method:"agentList",
                    term: request.term


                },
                success: function( data ) {

                    response($.map(data, function (value, key) {
                        return {
                            label: value,
                            value: key
                        };
                    }));


                }
            } );
        },
        minLength: 0


    });

    $("#agnt-mid").autocomplete({
        source: function( request, response ) {
            $.ajax( {
                url: "/icici/servlet/GetDetailsAPI",
                dataType: "json",
                data: {
                    ctoken: $('#ctoken').val(),
                    method:"agentMemberList",
                    agentid: $('#agnt').val(),
                    term: request.term


                },
                success: function( data ) {

                    response($.map(data, function (value, key) {
                        return {
                            label: value,
                            value: key
                        };
                    }));


                }
            } );
        },
        minLength: 0
    });

    $("#agnt-accid").autocomplete({
        source: function( request, response ) {
            $.ajax( {
                url: "/icici/servlet/GetDetailsAPI",
                dataType: "json",
                data: {
                    ctoken: $('#ctoken').val(),
                    method:"memberAccountList",
                    agentid:$("#agnt").val(),
                    memberid: $('#agnt-mid').val(),
                    term: request.term

                },
                success: function( data ) {
                    response($.map(data, function (value, key) {
                        console.log(data);
                        return {
                            label: value,
                            value: key
                        };
                    }));
                }
            } );
        },
        minLength: 0
    });

    $("#bankwireid").autocomplete({
        source: function( request, response ) {
            $.ajax( {
                url: "/icici/servlet/GetDetailsAPI",
                dataType: "json",
                data: {
                    ctoken: $('#ctoken').val(),
                    method:"bankWireList",
                    accountid:$("#agnt-accid").val(),
                    term: request.term

                },
                success: function( data ) {
                    response($.map(data, function (value, key) {
                        console.log(data);
                        return {
                            label: value,
                            value: key
                        };
                    }));
                }
            } );
        },
        minLength: 0
    });

    $("#aid").autocomplete({
        source: function( request, response ) {
            $.ajax( {
                url: "/icici/servlet/GetDetailsAPI",
                dataType: "json",
                data: {
                    ctoken: $('#ctoken').val(),
                    method:"agentAllList",
                    term: request.term


                },
                success: function( data ) {

                    response($.map(data, function (value, key) {
                        return {
                            label: value,
                            value: key
                        };
                    }));


                }
            } );
        },
        minLength: 0


    });

    $("#username").autocomplete({
        source: function( request, response ) {
            console.log("error");
            $.ajax( {
                url: "/icici/servlet/GetDetailsAPI",
                dataType: "json",
                data: {
                    ctoken: $('#ctoken').val(),
                    role: $('#role').val(),
                    method:"usernamelist",
                    term: request.term


                },
                success: function( data ) {

                    response($.map(data, function (value, key) {
                        return {
                            label: value,
                            value: key
                        };
                    }));


                }
            } );
        },
        minLength: 0


    });
    $("#pmid2").autocomplete({
        source: function( request, response ) {
            $.ajax( {
                url: "/icici/servlet/GetDetailsAPI",
                dataType: "json",
                data: {
                    ctoken: $('#ctoken').val(),
                    method:"partnermember",
                    partnerid: $('#allpid').val(),
                    term: request.term
                },
                success: function( data ) {
                    //response( data );

                    response($.map(data, function (value, key) {
                        return {
                            label: value,
                            value: key
                        };
                    }));
                }
            } );
        },
        minLength: 0
    });
    $("#mtid1").autocomplete({
        source: function( request, response ) {
            $.ajax( {
                url: "/icici/servlet/GetDetailsAPI",
                dataType: "json",
                data: {
                    ctoken: $('#ctoken').val(),
                    method:"terminalmemberpartner",
                    memberid: $('#pmid2').val(),
                    term: request.term


                },
                success: function( data ) {

                    response($.map(data, function (value, key) {
                        return {
                            label: value,
                            value: key
                        };
                    }));


                }
            } );
        },
        minLength: 0


    });

$("#paymode1").autocomplete({
   source: function( request, response) {
       $.ajax({
           url: "/icici/servlet/GetDetailsAPI",
           dataType: "json",
           data: {
               ctoken: $('#ctoken').val(),
               method: "paymodeData",
               term: request.term
           },
           success: function(data){

               response($.map(data, function(value, key){
                   return {
                       label: value,
                       value: key
                   };
               }));
           }
       });
   },
    minLength: 0
});

    $("#ctype1").autocomplete({
        source: function(request , response){
            $.ajax({
                url      : "/icici/servlet/GetDetailsAPI",
                dataType : "json",
                data     : {
                    ctoken : $('#ctoken').val(),
                    method : "cardtypedetails",
                    term   : request.term
                },
                success : function(data){
                    response($.map(data,function(value,key){
                        return{
                            label : value,
                            value : key
                        };
                    }));
                }
            });
        },
        minLength : 0
    });

    $("#gatewayname").autocomplete({
        source: function(request , response){
            $.ajax({
                url      : "/icici/servlet/GetDetailsAPI",
                dataType : "json",
                data     : {
                    ctoken : $('#ctoken').val(),
                    method : "gatewayname",
                    term   : request.term
                },
                success : function(data){
                    response($.map(data,function(value,key){
                        return{
                            label : value,
                            value : key
                        };
                    }));
                }
            });
        },
        minLength : 0
    });

});