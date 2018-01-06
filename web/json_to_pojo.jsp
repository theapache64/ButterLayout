<html>
<head>
    <title>JSON to POJO / ButterLayout
    </title>

    <%@include file="common_headers.jsp" %>

    <script>

        $(document).ready(function () {


            var jsonEditor = CodeMirror.fromTextArea(document.getElementById("jsonCode"), {
                lineNumbers: true,
                mode: "application/json",
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
                jsonEditor.setOption('readOnly', 'nocursor');
                $("button#bGenButterLayout").prop('disabled', true);
            }

            function stopLoading() {
                jsonEditor.setOption('readOnly', false);
                $("button#bGenButterLayout").prop('disabled', false);
            }

            $("button#bGenButterLayout").click(function () {

                $("p#error_message").text("");

                var jsonData = jsonEditor.getDoc().getValue();

                $.ajax({
                    type: "POST",
                    beforeSend: function (request) {
                        startLoading();
                    },
                    url: "json_to_pojo",
                    data: {
                        json_data: jsonData,
                        is_retrofit_model: $("input#isRetrofitModel").is(":checked"),
                        package_name: $("input#iPackageName").val(),
                        class_name: $("input#iClassName").val()
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
            <h1>JSON to POJO</h1>
            <p class="text-muted">Generate POJO from JSON</p>
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
            <textarea id="jsonCode" placeholder="Paste your JSON here" class="form-control"
                      style="width: 100%;height: 80%">{
    "data": {
        "user": {
        	"id":"1",
          	"name":"theapache64"
        }
    },
    "error": false,
    "message": "OK"
}</textarea>
        </div>
        <div class="col-md-2 text-center">

            <label class="pull-left" for="iClassName">Class name</label>
            <input id="iClassName" class="form-control" placeholder="Class name" value="MyPojo"/>

            <br>
            <label class="pull-left" for="iPackageName">Package name</label>
            <input id="iPackageName" class="form-control" placeholder="Package name" value="com.my.packagename"/>

            <br>
            <input id="isRetrofitModel" type="checkbox" checked/>
            <label for="isRetrofitModel">Retrofit Model</label>
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