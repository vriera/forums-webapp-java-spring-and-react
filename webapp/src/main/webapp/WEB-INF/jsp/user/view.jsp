<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<html>

<head>
    <meta charset="utf-8">
    <title>AskAway | Dashboard</title>
    <!-- Argon CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0-beta3/dist/css/bootstrap.min.css" rel="stylesheet"
          integrity="sha384-eOJMYsd53ii+scO/bJGFsiCZc+5NDVN2yr8+0RDqr0Ql0h+rP48ckxlpbzKgwra6" crossorigin="anonymous">
    <link type="text/css" href="<c:url value="/resources/styles/argon-design-system.css"/>" rel="stylesheet">
    <link rel="stylesheet" href="<c:url value="/resources/styles/general.css"/>" type="text/css">


    <!--Creo que es el JS de bootstrap -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0-beta3/dist/js/bootstrap.bundle.min.js"
            integrity="sha384-JEW9xMcG8R+pH31jmWH6WWP0WintQrMb4s7ZOdauHnUtxwoG2vI5DkLtS3qm9Ekf"
            crossorigin="anonymous"></script>

    <!--Font Awsome -->
    <script src="https://kit.fontawesome.com/eda885758a.js" crossorigin="anonymous"></script>

    <!--Material design -->
    <link rel="stylesheet" href="https://fonts.googleapis.com/icon?family=Material+Icons">
    <link rel="icon" href="<c:url value="/resources/images/favicon.ico"/>">


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
    <div class="section section-hero section-shaped pt-3">
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


        <div class="row">
            <div class="col-3">
                <div class="white-pill d-flex flex-column mt-5" >
                    <!-- INFORMACION DE USUARIO -->
                    <div class="d-flex justify-content-center">
                        <p class="h1 text-primary"><c:out value="${otherUser.username}"/></p>
                    </div>
                    <div class="d-flex justify-content-center">
                        <p><spring:message code="emailEquals"/></p>
                        <p><c:out value="${otherUser.email}"/></p>
                    </div>
                    <!-- DASHBOARD - OPCIONES VERTICALES -->
                    <ul class="nav nav-pills flex-column mb-auto">

                        <li>
                            <a href="<c:url value="/user/${otherUser.id}"/>" class="h5 nav-link link-dark active">
                                <i class="fas fa-users mr-3"></i>
                                <spring:message code="dashboard.profile"/>
                            </a>
                        </li>

                        <li>
                            <a href="<c:url value="/user/moderatedCommunities/${otherUser.id}"/>" class="h5 nav-link link-dark">
                                <i class="fas fa-users mr-3"></i>
                                <spring:message code="dashboard.Modcommunities"/>
                            </a>
                        </li>


                    </ul>
                </div>
            </div>


            <%--PERFIL--%>
            <div class="col-6">
                <div class="white-pill mt-5">
                    <div class="card-body overflow-hidden">
                        <p class="h3 text-primary text-center"><spring:message code="title.profile"/></p>
                        <hr class="mb-1">
                        <div class="text-center">
                            <img class="rounded-circle" src="https://avatars.dicebear.com/api/avataaars/${user.email}.svg" style="height: 80px; width: 80px;">
                        </div>
                        <p class="h1 text-center text-primary">${otherUser.username}</p>
                        <p class="h4 text-center">Email: ${otherUser.email}</p>
                        <div class="d-flex justify-content-center">
                            <p class="h4 text-center"><spring:message code="profile.karma"/>${karma}</p>
                            <c:if test="${karma>0}">
                                <div class="h4 mr-2 text-success mb-0 mt-1 ml-3">
                                    <i class="fas fa-arrow-alt-circle-up"></i>
                                </div>
                            </c:if>
                            <c:if test="${karma < 0}">
                                <div class="h4 mr-2 text-warning mb-0 mt-1 ml-3">
                                    <i class="fas fa-arrow-alt-circle-down"></i>
                                </div>
                            </c:if>
                        </div>

                    </div>
                </div>
            </div>

            <%--HACER PREGUNTA--%>
            <div class="col-3">
                <div class="white-pill mt-5 mr-3">
                    <div class="card-body">
                        <p class="h3 text-primary text-center"><spring:message code="title.askQuestion"/></p>
                        <hr>
                        <p class="h5 my-3"><spring:message code="subtitle.askQuestion"/></p>
                        <a class="btn btn-primary" href="<c:url value="/question/ask/community"/>"><spring:message code="button.askQuestion"/></a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>


</body>
</html>