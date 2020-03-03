<%--
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

<!-- Include Bootstrap -->
<link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/css/bootstrap.min.css"
      integrity="sha384-Vkoo8x4CGsO3+Hhxv8T/Q5PaXtkKtu6ug5TOeNV6gBiFeWPGFN9MuhOf23Q9Ifjh" crossorigin="anonymous">
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/js/bootstrap.min.js"
        integrity="sha384-wfSDF2E50Y2D1uUdj0O3uMBJnjuUD4Ih7YwaYd1iqfktj0Uod8GCExl3Og8ifwB6"
        crossorigin="anonymous"></script>


<div class="container">
    <h4>${fileSuccess}</h4>
    <form method="POST" action="${pageContext.request.contextPath}/uploadlog" enctype="multipart/form-data">
        <div class="form-group row">
            <label for="exampleInputFile" class="col-2 col-form-label">File to upload</label>
            <div class="col-10">
                <input type="file" class="form-control-file" id="exampleInputFile" name="logFile">
            </div>
        </div>

        <div class="form-group row">
            <label for="example-date-input" class="col-2 col-form-label">Year of the log file </label>
            <div class="col-10">
                <input type="date" class="form-control" id="example-date-input" name="date">
            </div>
        </div>
        <button type="submit" class="btn btn-primary">Submit</button>
    </form>
</div>

</body>
</html>
