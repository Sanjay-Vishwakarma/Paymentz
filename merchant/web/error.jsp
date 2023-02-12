<%@ page isErrorPage="true" import="java.lang.*,
                                    com.directi.pg.Functions" %>
<html>
<head>
	<%--	<link rel="stylesheet" type="text/css" href="/merchant/css/styles123.css"/>
        <link href="/merchant/css/icons/font-awesome/css/font-awesome.min.css" rel="stylesheet">--%>

	<!-- META SECTION -->
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<meta http-equiv="X-UA-Compatible" content="IE=edge" />
	<meta name="viewport" content="width=device-width, initial-scale=1" />

	<%--<link rel="icon" href="/merchant/NewCss/favicon.ico" type="image/x-icon" />--%>
	<!-- END META SECTION -->

	<!-- CSS INCLUDE -->
	<%--<link rel="stylesheet" type="text/css" id="theme" href="/merchant/NewCss/theme-default.css"/>
	<link rel="stylesheet" type="text/css"  href="/merchant/NewCss/bootstrap.min.css"/>
	<link rel="stylesheet" type="text/css"  href="/merchant/NewCss/fontawesome/font-awesome.min.css"/>
	<link rel="stylesheet" type="text/css"  href="/merchant/NewCss/fonts/fontawesome-webfont.eot"/>
	<link rel="stylesheet" type="text/css"  href="/merchant/NewCss/fonts/fontawesome-webfont.svg"/>
	<link rel="stylesheet" type="text/css"  href="/merchant/NewCss/fonts/fontawesome-webfont.woff"/>
	<link rel="stylesheet" type="text/css"  href="/merchant/NewCss/fonts/glyphicons-halflings-regular.eot"/>
	<link rel="stylesheet" type="text/css"  href="/merchant/NewCss/fonts/glyphicons-halflings-regular.svg"/>
	<link rel="stylesheet" type="text/css"  href="/merchant/NewCss/fonts/glyphicons-halflings-regular"/>
	<link rel="stylesheet" type="text/css"  href="/merchant/NewCss/fonts/glyphicons-halflings-regular.woff"/>--%>
	<!-- EOF CSS INCLUDE -->
</head>

<body>
<div class="content-page">
	<div class="content">
		<!-- Page Heading Start -->
		<div class="page-heading">

			<div class="row">

				<div class="col-sm-12 portlets ui-sortable">

					<div class="widget">

						<div class="widget-header transparent">
							<h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;Error Message</strong></h2>
							<div class="additional-btn">
								<a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
								<a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
								<a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
							</div>
						</div>
						<div class="widget-content padding">

							<%
								if(exception!=null)
								{
									String mess = exception.toString();

									out.println(Functions.NewShowConfirmation1("Error", mess));

								}
							%>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>


</body>

</html>