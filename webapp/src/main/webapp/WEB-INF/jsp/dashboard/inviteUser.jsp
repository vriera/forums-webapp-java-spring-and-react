<%----%><!DOCTYPE html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">

    <title>AskAway | Crear pregunta</title>


    <!-- Icons -->
    <%--    <link href="/assets/vendor/nucleo/css/nucleo-icons.css" rel="stylesheet">--%>
    <link href="https://use.fontawesome.com/releases/v5.0.6/css/all.css" rel="stylesheet">

    <!-- BLK• CSS -->
    <%--<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0-beta3/dist/css/bootstrap.min.css" rel="stylesheet"
          integrity="sha384-eOJMYsd53ii+scO/bJGFsiCZc+5NDVN2yr8+0RDqr0Ql0h+rP48ckxlpbzKgwra6" crossorigin="anonymous">--%>
    <link type="text/css" href="<c:url value="/resources/styles/argon-design-system.css"/>" rel="stylesheet">
    <link type="text/css" href="<c:url value="/resources/styles/general.css"/>" rel="stylesheet">
    <link type="text/css" href="<c:url value="/resources/styles/stepper.css"/>" rel="stylesheet">
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
        </jsp:include>
    </c:when>
    <c:otherwise>
        <jsp:include page="/WEB-INF/jsp/components/navbar.jsp"/>
    </c:otherwise>
</c:choose>

<div class="wrapper">
    <div class="section section-hero section-shaped">
        <div class="shape shape-style-1 shape-default shape-skew viewheight-90">
            <span class="span-150"></span>
            <span class="span-50"></span>
            <span class="span-50"></span>
            <span class="span-75"></span>
            <span class="span-100"></span>
            <span class="span-75"></span>
            <span class="span-50"></span>
            <span class="span-100"></span>
            <span class="span-50"></span>
            <span class="span-100"></span>
        </div>
        <%--Tarjeta--%>
        <div class="container">
            <div class="white-pill">
                <div class="d-flex justify-content-center">
                    <div class="h1 text-primary">INVITAR A ALGUIEN</div>
                </div>
                <hr>
                <c:url value="/dashboard/community/${communityId}/invite" var="postPath"/>
                <form:form modelAttribute="inviteUserForm" action="${postPath}" method="post">
                    <%--Nombre--%>
                    <div class="form-group mt-3">
                        <form:label path="email"  class="text-black">Email del usuario</form:label>
                        <spring:message code="placeholder.email" var="placeholderEmail"/>
                        <form:input path="email" class="form-control" placeholder="${placeholderEmail}" id="name"/>
                        <c:if test="${displayError==true}">
                            <p class="text-warning">El email no pertenece a un usuario valido</p>
                        </c:if>
                    </div>

                    <%--Botones--%>
                    <div class="d-flex justify-content-center">
                        <input class="btn btn-primary mb-3" type="submit" value="<spring:message code="button.continue"/>"/>
                    </div>
                    <hr>
                </form:form>
            </div>
        </div>

    </div>
</div>


</body>