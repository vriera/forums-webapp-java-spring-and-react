<%----%><!DOCTYPE html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html>
<head>

    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta charset="utf-8">
    <title><spring:message code="landing.title"/></title>


    <!-- Icons -->
<%--    <link href="/assets/vendor/nucleo/css/nucleo-icons.css" rel="stylesheet">--%>
    <link href="https://use.fontawesome.com/releases/v5.0.6/css/all.css" rel="stylesheet">

    <!-- BLK• CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0-beta3/dist/css/bootstrap.min.css" rel="stylesheet"
          integrity="sha384-eOJMYsd53ii+scO/bJGFsiCZc+5NDVN2yr8+0RDqr0Ql0h+rP48ckxlpbzKgwra6" crossorigin="anonymous">
    <link type="text/css" href="<c:url value="/resources/styles/argon-design-system.css"/>" rel="stylesheet">
    <link type="text/css" href="<c:url value="/resources/styles/blk-design-system.css"/>" rel="stylesheet">
    <link type="text/css" href="<c:url value="/resources/styles/general.css"/>" rel="stylesheet">
    <link rel="icon" href="<c:url value="/resources/images/favicon.ico"/>">


    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0-beta3/dist/js/bootstrap.bundle.min.js"
            integrity="sha384-JEW9xMcG8R+pH31jmWH6WWP0WintQrMb4s7ZOdauHnUtxwoG2vI5DkLtS3qm9Ekf"
            crossorigin="anonymous"></script>



</head>
<body>

<c:choose>
    <c:when test="${is_user_present == true}">
        <jsp:include page="/WEB-INF/jsp/components/navbarLogged.jsp">
            <jsp:param name="user_name" value="${user.getUsername()}"/>
            <jsp:param name="user_email" value="${user.getEmail()}"/>
            <jsp:param name="user_notifications" value="${notifications.getTotal()}"/>
        </jsp:include>
    </c:when>
    <c:otherwise>
        <jsp:include page="/WEB-INF/jsp/components/navbar.jsp"/>
    </c:otherwise>
</c:choose>



<div class="wrapper">
    <div class="section section-hero section-shaped">
        <div class="shape shape-style-1 shape-default shape-skew viewheight-90">
            <span class="span-150 square1"></span>
            <span class="span-50 square2"></span>
            <span class="span-50 square3"></span>
            <span class="span-75 square4"></span>
            <span class="span-100 square5"></span>
            <span class="span-75 square6"></span>
            <span class="span-50 square7"></span>
            <span class="span-100 square3"></span>
            <span class="span-50 square2"></span>
            <span class="span-100 square4"></span>
        </div>

        <!-- encabezado centrado -->
        <div class="container">
            <div class="white-pill">
                <p class="h1 text-primary"><strong><spring:message code="askAway"/></strong></p>
                <p class="h3 mx-5"><spring:message code="landing.slogan"/></p>
                <%--BARRA DE BÚSQUEDAS--%>
                <div class="form-group mx-5">
                    <spring:message code="search" var="search"/>
                    <spring:message code="landing.searchCallToAction" var="searchPlaceholder"/>
                    <form action="<c:url value="/community/view/all"/>" method="get">
                        <div class="input-group">
                            <input class="form-control rounded" type="search" name="query" id="query" placeholder="${searchPlaceholder}">
                            <input class="btn btn-primary" type="submit" value="${search}">
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <div class="container-xl mt-5">
            <div class="row">
                <div class="col">
                    <div class="card card-lift--hover shadow border-0">
                        <div class="card-body">
                            <div class="icon icon-shape icon-shape-primary rounded-circle mb-4">
                                <i class="ni ni-check-bold"></i>
                            </div>
                            <p class="h3 text-primary"><spring:message code="landing.question.trigger"/></p>
                            <p class="fs-5 description my-3"><spring:message code="landing.question.callToAction"/></p>
                            <a class="btn btn-primary" href="<c:url value="question/ask/community"/>"><spring:message code="ask"/></a>
                        </div>
                    </div>
                </div>

                <div class="col">
                    <div class="card card-lift--hover shadow border-0">
                        <div class="card-body">
                            <div class="icon icon-shape icon-shape-primary rounded-circle mb-4">
                                <i class="ni ni-check-bold"></i>
                            </div>
                            <p class="h3 text-primary"><spring:message code="landing.communities"/></p>
                            <p class="fs-5 description my-3"><spring:message code="landing.communities.callToAction"/></p>
                            <a class="btn btn-primary" href="<c:url value="/community/create"/>"><spring:message code="create"/></a>
                        </div>

                    </div>
                </div>

                <div class="col">
                    <div class="card card-lift--hover shadow border-0">
                        <div class="card-body">
                            <div class="icon icon-shape icon-shape-primary rounded-circle mb-4">
                                <i class="ni ni-check-bold"></i>
                            </div>
                            <p class="h3 text-primary"><spring:message code="landing.discover"/></p>
                            <p class="fs-5 description my-3"><spring:message code="landing.discoverCallToAction"/></p>
                            <a class="btn btn-primary" href="<c:url value="/community/view/all"/>"><spring:message code="discover"/></a>
                        </div>

                    </div>
                </div>

            </div>

        </div>
    </div>
</div>


</body>
</html>
