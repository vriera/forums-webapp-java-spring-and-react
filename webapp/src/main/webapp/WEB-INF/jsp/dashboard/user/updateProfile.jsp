<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
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
                        <p class="h1 text-primary"><c:out value="${user.username}"/></p>
                    </div>
                    <div class="d-flex justify-content-center">
                        <p><spring:message code="emailEquals"/></p>
                        <p><c:out value="${user.email}"/></p>
                    </div>
                    <!-- DASHBOARD - OPCIONES VERTICALES -->
                    <ul class="nav nav-pills flex-column mb-auto">

                        <li>
                            <a href="<c:url value="/dashboard/user/myProfile"/>" class="h5 nav-link link-dark active">
                                <i class="fas fa-users mr-3"></i>
                                <spring:message code="dashboard.myProfile"/>
                            </a>
                        </li>

                        <li>
                            <a href="<c:url value="/dashboard/question/view?page=0"/>" class="h5 nav-link" aria-current="page">
                                <i class="fas fa-question mr-3"></i>
                                <spring:message code="dashboard.questions"/>
                            </a>
                        </li>
                        <li>
                            <a href="<c:url value="/dashboard/answer/view?page=0"/>" class="h5 nav-link link-dark">
                                <i class="fas fa-reply mr-3"></i>
                                <spring:message code="dashboard.answers"/>
                            </a>
                        </li>
                        <li>
                            <a href="<c:url value="/dashboard/community/admitted?page=0"/>" class="h5 nav-link link-dark">
                                <i class="fas fa-users mr-3"></i>
                                <spring:message code="dashboard.communities"/>
                                <span class="badge badge-secondary bg-warning text-white ml-1" > ${notifications.getTotal()}</span>
                            </a>
                        </li>


                    </ul>
                </div>
            </div>


            <%--Update profile--%>
            <div class="col-6">
                <div class="white-pill mt-5">
                    <div class="card-body overflow-hidden">
                        <p class="h3 text-primary text-center"><spring:message code="title.profile"/></p>
                        <hr class="mb-1">
                        <div class="text-center">
                            <img class="rounded-circle" src="https://avatars.dicebear.com/api/avataaars/${user.email}.svg" style="height: 80px; width: 80px;">
                        </div>

                        <c:url value="/dashboard/user/updateProfile" var="postPath"/>
                        <form:form modelAttribute="updateUserForm" action="${postPath}" method="post">
                            <p class="h5"><spring:message code="profile.updateUsername"/></p>
                            <div class="mb-3 text-center">
                                <form:input path="newUsername" type="text" class="form-control" value="${user.username}"/>
                                <form:errors path="newUsername" cssClass="error text-warning" element="p"/>
                            </div>

                            <p class="h5">Email</p>
                            <div class="mb-3 text-center">
                                <input type="email" class="form-control" placeholder="${user.email}" readonly>
                            </div>

                            <p class="h5"><spring:message code="profile.changePassword"/></p>
                            <spring:message code="profile.optional" var="optional"/>
                            <div class="mb-3 text-center">
                                <form:input path="newPassword" type="password" class="form-control" id="password" placeholder="${optional}"/>
                                <form:errors path="newPassword" cssClass="error text-warning" element="p"/>
                            </div>

                            <div class="d-flex">
                                <p class="h5"><spring:message code="profile.currentPassword"/></p>
                                <p class="h5 text-warning bold">*</p>
                            </div>
                            <div class="mb-3 text-center">
                                <form:input path="currentPassword" type="password" class="form-control" id="password"/>
                                <form:errors path="currentPassword" cssClass="error text-warning" element="p"/>
                                <p class="h6 text-gray"><spring:message code="profile.whyCurrentPassword"/></p>
                                <c:if test="${isOldPasswordCorrect == true}">
                                    <p class="text-warning"><spring:message code="profile.incorrectCurrentPassword"/></p>
                                </c:if>

                            </div>

                            <div class="text-center">
                                <a href="<c:url value="/dashboard/user/myProfile"/>" class="btn btn-secondary text-center"><spring:message code="profile.back"/></a>
                                <button type="submit" class="btn btn-primary text-center"><spring:message code="profile.save"/></button>
                            </div>

                        </form:form>

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