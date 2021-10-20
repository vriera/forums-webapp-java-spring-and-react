<%----%><!DOCTYPE html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">

    <title><spring:message code="community.create.title"/></title>


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
                    <div class="h1 text-primary"><spring:message code="community.create"/></div>
                </div>
                <div class="p"><spring:message code="community.create.callToAction"/></div>
                <hr>
                <c:url value="/community/create" var="postPath"/>
                <form:form modelAttribute="communityForm" action="${postPath}" method="post">
                    <%--Nombre--%>
                    <div class="form-group mt-3">
                        <form:label path="name"  class="text-black"><spring:message code="community.name"/></form:label>
                        <form:input path="name" class="form-control" placeholder="Tu comunidad" id="name"/>
                        <form:errors path="name" cssClass="text-warning" element="p"/>
                        <c:if test="${nameTaken}">
                            <p class="p text-warning"><spring:message code="community.nameTaken"/></p>
                        </c:if>
                    </div>
                    <%--Descripcion--%>
                    <div class="form-group">
                        <form:label path="description"><spring:message code="community.description"/></form:label>
                        <spring:message code="placeholder.community.description" var="placeholderDescription"/>
                        <form:textarea path="description" class="form-control" id="description" rows="3" placeholder="${placeholderDescription}"/>
                    </div>
                    <%--Botones--%>
                    <div class="d-flex justify-content-center">
                        <a class="btn btn-light align-self-start" href="<c:url value="javascript:history.back()"/>"><spring:message code="back"/></a>
                        <input class="btn btn-primary mb-3" type="submit" value="<spring:message code="button.continue"/>">
                    </div>
                    <hr>
                </form:form>
            </div>
        </div>

    </div>
</div>


</body>