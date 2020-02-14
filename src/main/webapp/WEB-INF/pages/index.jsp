<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: cgong
  Date: 05/02/2020
  Time: 11:59
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Popular pathways</title>
    <style>
        body, html {
            position: relative;
            height: 95%;
            margin: 0;
        }

        /** Set some preferred visualization size, but cap it to the maximum screen size */
        #visualization {
            width: 100%;
            height: 100%;
            max-width: 100%;
            max-height: 100%;
        }
    </style>
</head>
<body>
<!-- Include FoamTree implementation. -->
<script src="foamtree/carrotsearch.foamtree.js"></script>
<script src="https://code.jquery.com/jquery-3.4.1.min.js"></script>
<script>
    // Initialize FoamTree after the whole page loads to make sure
    // the element has been laid out and has non-zero dimensions.
    window.addEventListener("load", function () {
        var foamtree = new CarrotSearchFoamTree({
            // Identifier of the HTML element defined above
            id: "visualization",

           // layout: "ordered",
            // Color of the outline stroke for the selected groups
            groupSelectionOutlineColor: "#E86365"
        });

        // add hits value to label
        foamtree.set({
            groupLabelDecorator: function (opts, props, vars) {
                vars.labelText = vars.labelText + " [" + props.group.hits + " " + props.group.weight + "]";
            }
        });

        foamtree.set({
            groupColorDecorator: function (opts, props, vars) {
                vars.groupColor = "#58C3E5";
                vars.labelColor = "#000";
            }
        });
        // load data
        $.ajax({
            url: ${pageContext.request.contextPath}"/results/${file}",
            dataType: "json",
            success: function (data) {
                foamtree.set({
                    dataObject: {
                        groups: data
                    }
                });
            }
        });

        window.addEventListener("resize", (function() {
            var timeout;
            return function() {
                window.clearTimeout(timeout);
                timeout = window.setTimeout(foamtree.resize, 300);
            };
        })());
    });


</script>
<div id="visualization"></div>


<h3>Year: ${year}</h3>

<a href="${pageContext.request.contextPath}/upload">click to upload</a>

</body>
</html>
