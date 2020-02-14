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

<h3 style="color:red">${fileSuccess}</h3>
<form method="POST" action="${pageContext.request.contextPath}/uploadlog" enctype="multipart/form-data">
    File to upload: <input type="file" name="file"><br/>
    Year of log file:<input type="date" name="date"><br/>
    Submit:<input type="submit" value="Upload File">
</form>
</body>
</html>
