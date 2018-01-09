<html>
<head>
    <title>Binder Java / ButterLayout
    </title>

    <%@include file="common_headers.jsp" %>

    <script>

        $(document).ready(function () {


            var xmlEditor = CodeMirror.fromTextArea(document.getElementById("xmlCode"), {
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

            var javaEditor = CodeMirror.fromTextArea(document.getElementById("javaCode"), {
                lineNumbers: true,
                mode: "text/x-java",
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
                xmlEditor.setOption('readOnly', 'nocursor');
                $("button#bGenButterLayout").prop('disabled', true);
            }

            function stopLoading() {
                xmlEditor.setOption('readOnly', false);
                $("button#bGenButterLayout").prop('disabled', false);
            }

            $("button#bGenButterLayout").click(function () {

                $("p#error_message").text("");

                var xmlData = xmlEditor.getDoc().getValue();

                $.ajax({
                    type: "POST",
                    beforeSend: function (request) {
                        startLoading();
                    },
                    url: "xml_to_binder_java",
                    data: {
                        xml_data: xmlData,
                        root_view : $("input#iRootView").val(),
                        data_model : $("input#iDataModel").val()
                    },
                    success: function (data) {
                        stopLoading();
                        console.log(data);


                        if (!data.error) {
                            javaEditor.getDoc().setValue(data.data.output);
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

<%@include file="navbar.jsp" %>

<div class="container">

    <div class="row">
        <div class="col-md-12">
            <h1>Binder Java</h1>
            <p class="text-muted">Generates ButterKnife annotation reference code from Android XML layout resource</p>
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
            <textarea id="xmlCode" placeholder="Paste your XML code here" class="form-control"
                      style="width: 100%;height: 80%"></textarea>
        </div>
        <div class="col-md-2 text-center">

            <%--Root view--%>
            <label for="iRootView" class="pull-left">ViewHolder</label>
            <input id="iRootView" class="form-control" placeholder="RootView" value="holder"/>
            <br>

            <%--Data model--%>
            <label for="iDataModel" class="pull-left">Date Model</label>
            <input id="iDataModel" class="form-control" placeholder="Date Model" value="item"/>
            <br>

            <button id="bGenButterLayout" class="btn btn-primary"><span
                    class="glyphicon glyphicon glyphicon-cog"></span> Generate
            </button>
        </div>
        <div class="col-md-5">
            <textarea id="javaCode" placeholder="Your java code will get generate here" class="form-control"
                      style="width: 100%;height: 80%"></textarea>
        </div>
    </div>


</div>
</body>


</html>