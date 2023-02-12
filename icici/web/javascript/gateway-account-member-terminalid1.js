/**
 * Created by Ajit on 5/14/2018.
 */

$(function () {
    $("#gateway1").autocomplete({
        source: function( request, response ) {
            $.ajax( {
                url: "/icici/servlet/GetDetailsAPI",
                dataType: "json",
                data: {
                    ctoken: $('#ctoken').val(),
                    method:"gatewayList1",
                    term: request.term
                },
                success: function( data ) {
                    //response( data );
                    const ordered = {};
                    Object.keys(data).sort().forEach(function(key) {
                        ordered[key] = data[key];
                    });

                    response($.map(ordered, function (value, key) {
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

    $("#accountid1").autocomplete({
        source: function( request, response ) {
            $.ajax( {
                url: "/icici/servlet/GetDetailsAPI",
                dataType: "json",
                data: {
                    ctoken: $('#ctoken').val(),
                    method:"accountList",
                    memberid: $('#memberid1').val(),
                    gatewayid1: $('#gateway1').val(),
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

    $("#accountid2").autocomplete({
        source: function( request, response ) {
            $.ajax( {
                url: "/icici/servlet/GetDetailsAPI",
                dataType: "json",
                data: {
                    ctoken: $('#ctoken').val(),
                    method:"MembersAccountList",
                    memberid: $('#memberid1').val(),
                    gatewayid1: $('#gateway1').val(),
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


    $("#memberid1").autocomplete({
        source: function( request, response ) {
            $.ajax( {
                url: "/icici/servlet/GetDetailsAPI",
                dataType: "json",
                data: {
                    ctoken: $('#ctoken').val(),
                    method:"memberList",
                    gatewayid1: $('#gateway1').val(),
                    accountid1: $('#accountid1').val(),
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

    $("#fromMemberId").autocomplete({
        source: function( request, response ) {
            $.ajax( {
                url: "/icici/servlet/GetDetailsAPI",
                dataType: "json",
                data: {
                    ctoken: $('#ctoken').val(),
                    method:"memberList",
                    gatewayid1: $('#gateway1').val(),
                    accountid1: $('#accountid1').val(),
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
        minLength: 3


    });

    $("#toMemberId").autocomplete({
        source: function( request, response ) {
            $.ajax( {
                url: "/icici/servlet/GetDetailsAPI",
                dataType: "json",
                data: {
                    ctoken: $('#ctoken').val(),
                    method:"memberList",
                    gatewayid1: $('#gateway1').val(),
                    accountid1: $('#accountid1').val(),
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
        minLength: 3


    });

    $("#tid1").autocomplete({
        source: function( request, response ) {
            $.ajax( {
                url: "/icici/servlet/GetDetailsAPI",
                dataType: "json",
                data: {
                    ctoken: $('#ctoken').val(),
                    method:"terminalDetailList",
                    gatewayid1: $('#gateway1').val(),
                    accountid1: $('#accountid1').val(),
                    memberid1: $('#memberid1').val(),
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

    $("#tid2").autocomplete({
        source: function( request, response ) {
            $.ajax( {
                url: "/icici/servlet/GetDetailsAPI",
                dataType: "json",
                data: {
                    ctoken: $('#ctoken').val(),

                    method:"terminalDetailList",
                    gatewayid1: $('#gateway1').val(),
                    accountid1: $('#accountid1').val(),
                    memberid1: $('#memberid1').val(),
                    flag: "true",
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

    $("#tid3").autocomplete({
        source: function( request, response ) {
            $.ajax( {
                url: "/icici/servlet/GetDetailsAPI",
                dataType: "json",
                data: {
                    ctoken: $('#ctoken').val(),

                    method:"terminalDetailList",
                    gatewayid1: $('#gateway1').val(),
                    accountid1: $('#accountid1').val(),
                    memberid1: $('#memberid1').val(),
                    flag: "false",
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
    $("#tid4").autocomplete({
        source: function( request, response ) {
            $.ajax( {
                url: "/icici/servlet/GetDetailsAPI",
                dataType: "json",
                data: {
                    ctoken: $('#ctoken').val(),

                    method:"terminalDetailList",
                    gatewayid1: $('#gateway1').val(),
                    accountid1: $('#accountid1').val(),
                    memberid1: $('#memberid1').val(),
                    flag: "false",
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

    $("#fromTerminal").autocomplete({
        source: function( request, response ) {
            $.ajax( {
                url: "/icici/servlet/GetDetailsAPI",
                dataType: "json",
                data: {
                    ctoken: $('#ctoken').val(),

                    method:"terminalDetailList",
                    gatewayid1: $('#gateway1').val(),
                    accountid1: $('#accountid1').val(),
                    memberid1: $('#fromMemberId').val(),
                    flag: "false",
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
    $("#tid5").autocomplete({
        source: function( request, response ) {
            $.ajax( {
                url: "/icici/servlet/GetDetailsAPI",
                dataType: "json",
                data: {
                    ctoken: $('#ctoken').val(),

                    method:"terminalDetailList",
                    gatewayid1: $('#gateway1').val(),
                    accountid1: $('#accountid1').val(),
                    memberid1: $('#memberid1').val(),
                    flag: "false",
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

    $("#pgtypeid").autocomplete({
        source: function (request, response)
        {
            $.ajax({
                url: "/icici/servlet/GetDetailsAPI",
                dataType: "json",
                data: {
                    ctoken: $('#ctoken').val(),
                    method: "gatewayList1",
                    term: request.term
                },
                success: function (data)
                {
                    //response( data );
                    const ordered = {};
                    Object.keys(data).sort().forEach(function (key)
                    {
                        ordered[key] = data[key];
                    });

                    response($.map(ordered, function (value, key)
                    {
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

    $("#accid").autocomplete({
        source: function (request, response)
        {
            $.ajax({
                url: "/icici/servlet/GetDetailsAPI",
                dataType: "json",
                data: {
                    ctoken: $('#ctoken').val(),
                    method: "accountList",
                    gatewayid1: $('#pgtypeid').val(),
                    term: request.term


                },
                success: function (data)
                {

                    response($.map(data, function (value, key)
                    {
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
    $("#parent_bankwireId").autocomplete({
        source: function (request, response)
        {
            $.ajax({
                url: "/icici/servlet/GetDetailsAPI",
                dataType: "json",
                data: {
                    ctoken: $('#ctoken').val(),
                    method: "parent_bankwireIdList",
                    gatewayid: $('#pgtypeid').val(),
                    accountid: $('#accid').val(),
                    term: request.term

                },
                success: function (data)
                {
                    response($.map(data, function (value, key)
                    {
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

    $("#parent_bankwireId1").autocomplete({
        source: function (request, response)
        {
            $.ajax({
                url: "/icici/servlet/GetDetailsAPI",
                dataType: "json",
                data: {
                    ctoken: $('#ctoken').val(),
                    method: "parent_bankwireIdList",
                    gatewayid: $('#gateway1').val(),
                    accountid: $('#accountid1').val(),
                    term: request.term
                },
                success: function (data)
                {
                    response($.map(data, function (value, key)
                    {
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

    $("#memid").autocomplete({
        source: function (request, response)
        {
            $.ajax({
                url: "/icici/servlet/GetDetailsAPI",
                dataType: "json",
                data: {
                    ctoken: $('#ctoken').val(),
                    method: "memberList",
                    gatewayid1: $('#pgtypeid').val(),
                    accountid1: $('#accid').val(),
                    term: request.term


                },
                success: function (data)
                {

                    response($.map(data, function (value, key)
                    {
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

    $("#t-id").autocomplete({
        source: function (request, response)
        {
            $.ajax({
                url: "/icici/servlet/GetDetailsAPI",
                dataType: "json",
                data: {
                    ctoken: $('#ctoken').val(),
                    method: "terminalDetailList",
                    gatewayid1: $('#pgtypeid').val(),
                    accountid1: $('#accid').val(),
                    memberid1: $('#memid').val(),
                    flag: "false",
                    term: request.term


                },
                success: function (data)
                {

                    response($.map(data, function (value, key)
                    {
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

    $("#terminalid").autocomplete({

        source: function (request, response)
        {
            $.ajax({
                url: "/icici/servlet/GetDetailsAPI",
                dataType: "json",
                data: {
                    ctoken      : $('#ctoken').val(),
                    method      : "terminalDetailList",
                    gatewayid1  : $('#pgtypeid').val(),
                    accountid1  : $('#accid').val(),
                    memberid1   : $('#mid').val(),
                    flag        : "false",
                    term        : request.term


                },
                success: function (data)
                {

                    response($.map(data, function (value, key)
                    {
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

    $("#bankwireid1").autocomplete({
        source: function(request, response){

            $.ajax({
                url: "/icici/servlet/GetDetailsAPI",
                dataType: "json",
                data:{

                    ctoken:$('#ctoken').val(),
                    method: "bankwireidList",
                    term: request.term
                },

                success: function (data)
                {
                    response($.map(data, function (value, key)
                    {
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


});


