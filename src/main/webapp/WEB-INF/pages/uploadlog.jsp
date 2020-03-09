<%@ page import="java.io.IOException" %><%--
  Created by IntelliJ IDEA.
  User: cgong
  Date: 12/02/2020
  Time: 14:52
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Upload</title>
</head>
<body>

<script src="${pageContext.request.contextPath}/jQuery/jquery-3.4.1.min.js"></script>
<!-- Include Bootstrap -->
<link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/css/bootstrap.min.css"
      integrity="sha384-Vkoo8x4CGsO3+Hhxv8T/Q5PaXtkKtu6ug5TOeNV6gBiFeWPGFN9MuhOf23Q9Ifjh" crossorigin="anonymous">
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/js/bootstrap.min.js"
        integrity="sha384-wfSDF2E50Y2D1uUdj0O3uMBJnjuUD4Ih7YwaYd1iqfktj0Uod8GCExl3Og8ifwB6"
        crossorigin="anonymous"></script>


<div class="container">
    <h3>
        Reactome Log File Uploader
    </h3>
    </br>
    <form method="POST" action="${pageContext.request.contextPath}/uploadlog" enctype="multipart/form-data">
        <div class="form-group row">
            <label for="inputGroupFile01" class="col-sm-2 col-form-label">Upload</label>
            <div class="col">
                <input type="file" class="custom-file-input" id="inputGroupFile01"
                       aria-describedby="inputGroupFileAddon01" placeholder="Chose" name="logFile"
                       accept=".csv, .txt, text/csv, text/plain">
                <p class="help-block">
                    Only .csv and .txt files can be uploaded
                </p>
                <label class="custom-file-label" style="margin-left: 13px; margin-right: 13px" for="inputGroupFile01">Choose
                    file</label>
            </div>
        </div>

        <div class="form-group row">
            <label for="example-date-input" class="col-sm-2 col-form-label">Select year</label>
            <div class="col">
                <input type="date" class="form-control" id="example-date-input" min="2005-01-01" value="2019-01-01"
                       name="date">
                <p class="help-block">
                    Please select a date for yearly log, month and days can be any value
                </p>
            </div>
        </div>

        <div class="form-group row">
            <div class="col">
                <p class="text-warning">${errormsg}</p>
            </div>
        </div>

        <button type="submit" class="btn btn-primary">Submit</button>

    </form>

    </br>

    <h4>Declaration</h4>
    <p>
        Please note that the uploader is sensitive with the format of log file for now,
        we only have two columns in log file, first is the stId without species prefix,
        second is the hits value of that pathways, the header content doesn't bother.
    </p>

    <p>
        Please see below as an example.
    </p>

    <div class="d-flex justify-content-around">
        <p>CSV File</p>
        <p>TXT File</p>

    </div>
    <div class="d-flex justify-content-around">
        <img alt="Bootstrap Image Preview"
             src="images/log_csv_ep.png"
             style="width: 500px"/>

        <img alt="Bootstrap Image Preview"
             src="images/log_txt_ep.png"
             style="width: 500px"/>
    </div>
</div>
<script>

    // make the name of the file appear on select
    $(".custom-file-input").on("change", function () {
        var fileName = $(this).val().split("\\").pop();
        $(this).siblings(".custom-file-label").addClass("selected").html(fileName);
    });
</script>

</body>
</html>
