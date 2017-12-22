<html>
<head>
    <title>XML to ButterJava / ButterLayout
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
                var rSeries = $("select#rSeries").val();
                var clickListeners = $("input#isClickListeners").is(':checked')

                $.ajax({
                    type: "POST",
                    beforeSend: function (request) {
                        startLoading();
                    },
                    url: "butter_layout_engine",
                    data: {
                        xml_data: xmlData,
                        r_series: rSeries,
                        click_listeners: clickListeners
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
<div class="container">

    <div class="row">
        <div class="col-md-12">
            <h1>XML to ButterJava</h1>
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
            <select id="rSeries" class="form-control">
                <option value="R">R</option>
                <option value="R2">R2</option>
            </select>
            <br>

            <input id="isClickListeners" type="checkbox" checked/>
            <label for="isClickListeners">Click Listeners</label>
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