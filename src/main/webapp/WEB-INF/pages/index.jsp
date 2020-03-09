<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: cgong
  Date: 05/02/2020
  Time: 11:59
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Popular pathways</title>
    <style>
        body, html {
            position: relative;
            height: 100%;
            margin: 0;
        }

        /** Set some preferred visualization size, but cap it to the maximum screen size */
        #visualization {
            width: 100%;
            height: 94%;
            max-width: 100%;
            max-height: 100%;
        }
    </style>
</head>
<body>
<!-- Include FoamTree implementation. -->
<script src="${pageContext.request.contextPath}/foamtree/carrotsearch.foamtree.js"></script>
<script src="${pageContext.request.contextPath}/jQuery/jquery-3.4.1.min.js"></script>

<!-- Include Bootstrap -->
<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.9/umd/popper.min.js"></script>

<link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/css/bootstrap.min.css"
      integrity="sha384-Vkoo8x4CGsO3+Hhxv8T/Q5PaXtkKtu6ug5TOeNV6gBiFeWPGFN9MuhOf23Q9Ifjh" crossorigin="anonymous">
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/js/bootstrap.min.js"
        integrity="sha384-wfSDF2E50Y2D1uUdj0O3uMBJnjuUD4Ih7YwaYd1iqfktj0Uod8GCExl3Og8ifwB6"
        crossorigin="anonymous"></script>

<script>
    // Initialize FoamTree after the whole page loads to make sure
    // the element has been laid out and has non-zero dimensions.
    window.addEventListener("load", function () {
        var foamtree = new CarrotSearchFoamTree({
            // Identifier of the HTML element defined above
            id: "visualization",
            pixelRatio: window.devicePixelRatio || 1,
            stacking: "flattened",

            //The duration of the group exposure and unexposure animation.
            exposeDuration: 500,

            // Lower groupMinDiameter to fit as many groups as possible
            groupMinDiameter: 0,

            // Set a simple fading animation. Animated rollouts are very expensive for large hierarchies
            rolloutDuration: 0,
            pullbackDuration: 0,

            // Lower the border radius a bit to fit more groups
            groupBorderWidth: 2,
            groupInsetWidth: 3,
            groupBorderRadius: 0,

            // Don't use gradients and rounded corners for faster rendering
            groupFillType: "plain",

            // Lower the minimum label font size a bit to show more labels
            groupLabelMinFontSize: 3,

            //Attach and draw a maximum of 12 levels of groups
            maxGroupLevelsAttached: 12,
            maxGroupLevelsDrawn: 12,
            maxGroupLabelLevelsDrawn: 12,

            //Tune the border options to make them more visible
            groupBorderWidthScaling: 0.5,

            // Width of the selection outline to draw around selected groups
            groupSelectionOutlineWidth: 3,

            // Show labels during relaxation
            wireframeLabelDrawing: "always",

            // Make the description group (in flattened view) smaller to make more space for child groups
            descriptionGroupMaxHeight: 0.25,

            // Maximum duration of a complete high-quality redraw of the visualization
            finalCompleteDrawMaxDuration: 40000,
            finalIncrementalDrawMaxDuration: 40000,
            wireframeDrawMaxDuration: 4000,

            // Color of the outline stroke for the selected groups
            groupSelectionOutlineColor: "#E86365"
        });

        // add weight value to label
        foamtree.set({
            groupLabelDecorator: function (opts, props, vars) {
                vars.labelText = vars.labelText + " [" + props.group.age + " " + props.group.weight + "]";
            }
        });

        foamtree.set({
            groupColorDecorator: function (opts, props, vars) {
                vars.labelColor = "#000";

                var age = props.group.age;
                switch (true) {
                    case age === 0:
                        vars.groupColor = "#F0F8FF";
                        break;
                    case age >= 1 && age < 2:
                        vars.groupColor = "#B0E0E6";
                        break;
                    case age >= 2 && age < 4:
                        vars.groupColor = "#87CEFA";
                        break;
                    case age >= 4 && age < 6:
                        vars.groupColor = "#00BFFF";
                        break;
                    case age >= 6 && age < 8:
                        vars.groupColor = "#1E90FF";
                        break;
                    case age >= 8 && age < 10:
                        vars.groupColor = "#6495ED";
                        break;
                    case age >= 10 && age < 12:
                        vars.groupColor = "#4682B4";
                        break;
                    case age >= 12 && age < 14:
                        vars.groupColor = "#4169E1";
                        break;
                    case age >= 14 && age < 16:
                        vars.groupColor = "#0000FF";
                        break;
                    case age >= 16 && age < 18:
                        vars.groupColor = "#00008B";
                        break;
                    case age >= 18 && age < 20:
                        vars.groupColor = "#191970";
                        break;
                    case age >= 20 && age < 22:
                        vars.groupColor = "#4B0082";
                        break;
                    default:
                        vars.groupColor = "#8A2BE2";
                }
            }
        });

        // load data
        foamtree.set({
            dataObject: {
                groups: ${data}
            }
        });

        // hold a polygonal to jump to Reactome pathway page
        foamtree.set({
            onGroupHold: function (e) {
                e.preventDefault();
                window.open(e.group.url);
            }
        });

        window.addEventListener("resize", (function () {
            var timeout;
            return function () {
                window.clearTimeout(timeout);
                timeout = window.setTimeout(foamtree.resize, 300);
            };
        })());
    });

</script>
<div id="visualization"></div>

<div class="d-flex d-flex justify-content-around">
    <span>Year: ${year}</span>

    <!-- Dropdown button -->
    <div class="btn-group">
        <button type="button" class="btn btn-outline-primary btn-sm dropdown-toggle" data-toggle="dropdown"
                aria-haspopup="true"
                aria-expanded="false">
            Available Yearly Logs
        </button>
        <div class="dropdown-menu">
            <c:forEach items="${yearList}" var="item">
                <a class="dropdown-item" href="${pageContext.request.contextPath}/load/${item}">${item}</a>
            </c:forEach>
        </div>
    </div>

    <a href="${pageContext.request.contextPath}/upload" class="btn btn-outline-primary btn-sm">Upload</a>

</div>

</body>
</html>
