<html>
<head>
    <title>XML to ButterJava / ButterLayout
    </title>

    <%@include file="common_headers.jsp" %>

    <script>

        $(document).ready(function () {


            var xmlViewCode = CodeMirror.fromTextArea(document.getElementById("xmlViewCode"), {
                lineNumbers: true,
                mode: "text/html",
                matchBrackets: true,
                extraKeys: {
                    "F11": function (cm) {
                        cm.setOption("fullScreen", !cm.getOption("fullScreen"));
                    },
                    "Esc": function (cm) {
                        if (cm.getOption("fullScreen")) cm.setOption("fullScreen", false);
                    }
                }
            });

            var xmlStyleCode = CodeMirror.fromTextArea(document.getElementById("xmlStyleCode"), {
                lineNumbers: true,
                mode: "text/html",
                matchBrackets: true,
                extraKeys: {
                    "F11": function (cm) {
                        cm.setOption("fullScreen", !cm.getOption("fullScreen"));
                    },
                    "Esc": function (cm) {
                        if (cm.getOption("fullScreen")) cm.setOption("fullScreen", false);
                    }
                }
            });

            function startLoading() {
                xmlViewCode.setOption('readOnly', 'nocursor');
                xmlStyleCode.setOption('readOnly', 'nocursor');
                $("button#bGenStyleXml").prop('disabled', true);
            }

            function stopLoading() {
                xmlViewCode.setOption('readOnly', false);
                xmlStyleCode.setOption('readOnly', false);
                $("button#bGenStyleXml").prop('disabled', false);
            }

            $("button#bGenStyleXml").click(function () {

                $("p#error_message").text("");

                var xmlViewData = xmlViewCode.getDoc().getValue();

                $.ajax({
                    type: "POST",
                    beforeSend: function (request) {
                        startLoading();
                    },
                    url: "view_xml_to_style_xml",
                    data: {
                        xml_view_data: xmlViewData
                    },
                    success: function (data) {
                        stopLoading();

                        if (!data.error) {
                            xmlStyleCode.getDoc().setValue(data.data.output);
                            $("p#error_message").text("");
                        } else {
                            $("p#error_message").text(data.message);
                        }
                    },
                    error: function () {
                        stopLoading();
                        $("p#error_message").text("Network error occurred, Please check your connection");
                    }
                });


            });


        });
    </script>

</head>
<body>

<%@include file="navbar.jsp"%>

<div class="container">

    <div class="row">
        <div class="col-md-12">
            <h1>View XML to Style XML</h1>
            <p class="text-muted">Generates Android style resource from given layout XML</p>
        </div>
    </div>


    <br>


    <div class="row">
        <div class="col-md-12">
            <p id="error_message" class="text-danger"></p>
        </div>
    </div>

    <div class="row">
        <div class="col-md-5">
            <textarea id="xmlViewCode" placeholder="Paste your XML code here" class="form-control"
                      style="width: 100%;height: 80%"></textarea>
        </div>
        <div class="col-md-2 text-center">
            <button id="bGenStyleXml" class="btn btn-primary"><span
                    class="glyphicon glyphicon glyphicon-cog"></span> Generate
            </button>
        </div>
        <div class="col-md-5">
            <textarea id="xmlStyleCode" placeholder="Your XML style code will get generate here" class="form-control"
                      style="width: 100%;height: 80%"></textarea>
        </div>
    </div>


</div>
</body>


</html>