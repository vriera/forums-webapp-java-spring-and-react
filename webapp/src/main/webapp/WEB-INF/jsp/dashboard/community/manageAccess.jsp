<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<html>

<head>
    <meta charset="utf-8">
    <title>AskAway | Dashboard</title>

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
                        <p class="h1 text-primary"><c:out value="${currentUser.username}"/></p>
                    </div>
                    <div class="d-flex justify-content-center">
                        <p><spring:message code="emailEquals"/></p>
                        <p><c:out value="${currentUser.email}"/></p>
                    </div>
                    <!-- DASHBOARD - OPCIONES VERTICALES -->
                    <ul class="nav nav-pills flex-column">
                        <li>
                            <a href="<c:url value="/dashboard/question/view"/>" class="h5 nav-link" aria-current="page">
                                <i class="fas fa-question mr-3"></i>
                                <spring:message code="dashboard.questions"/>
                            </a>
                        </li>
                        <li>
                            <a href="<c:url value="/dashboard/answer/view"/>" class="h5 nav-link link-dark">
                                <i class="fas fa-reply mr-3"></i>
                                <spring:message code="dashboard.answers"/>
                            </a>
                        </li>
                        <li>
                            <a href="<c:url value="/dashboard/community/admitted"/>" class="h5 nav-link link-dark active">
                                <i class="fas fa-users mr-3"></i>
                                <spring:message code="dashboard.communities"/>
                            </a>
                        </li>

                    </ul>
                </div>
            </div>

            <!-- TARJETA CENTRAL -->
            <div class="col-6">
                <%--CONTROL DE ACCESO--%>
                <div class="white-pill mt-5">
                    <div class="card-body">

                        <p class="h2 text-primary text-center mt-3 text-uppercase"><spring:message code="dashboard.access"/></p> <%--TODO: flujo para encontrar una comunidad--%>

                        <!-- tabs -->
                        <ul class="nav nav-tabs">
                            <li class="nav-item">
                                <a class="nav-link" href="<c:url value="/dashboard/community/admitted"/>"><spring:message code="dashboard.admitted"/></a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link" href="<c:url value="/dashboard/community/moderated"/>"><spring:message code="dashboard.moderated"/></a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link active" href="<c:url value="/dashboard/community/manageAccess"/>"><spring:message code="dashboard.manageAccess"/></a>
                            </li>
                        </ul>

                        <%--INVITACIONES ENTRANTES--%>
                        <c:if test="${invited.size() != 0}">
                            <div class="overflow-auto">
                                <p class="h3 text-primary"><spring:message code="dashboard.pendingInvites"/></p>
                                <c:forEach items="${invited}" var="community">
                                    <div class="card">
                                        <div class="d-flex flex-row mt-3" style="justify-content: space-between">
                                            <div>
                                                <p class="h4 card-title "><c:out value="${community.name}"/></p>
                                            </div>
                                            <div>
                                                <c:url value="/dashboard/community/${community.id}/acceptInvite" var="acceptPostPath"/>
                                                <form action="${acceptPostPath}" method="post">
                                                    <button class="btn mb-0" >
                                                        <div class="h4 mb-0">
                                                            <i class="fas fa-check-circle"></i>
                                                        </div>
                                                    </button>
                                                </form>
                                                <c:url value="/dashboard/community/${community.id}/rejectInvite" var="rejectPostPath"/>
                                                <form action="${rejectPostPath}" method="post">
                                                    <button class="btn mb-0" >
                                                        <div class="h4 mb-0">
                                                            <i class="fas fa-times-circle"></i>
                                                        </div>
                                                    </button>
                                                </form>
                                                <c:url value="/dashboard/community/${community.id}/blockCommunity" var="blockPostPath"/>
                                                <form action="${blockPostPath}" method="post">
                                                    <button class="btn mb-0" >
                                                        <div class="h4 mb-0">
                                                            <i class="fas fa-ban"></i>
                                                        </div>
                                                    </button>
                                                </form>
                                            </div> </div>

                                        </div>
                                    </div>
                                </c:forEach>

                                <%--PAGINACIÓN--%>
                                <nav>
                                        <ul class="pagination justify-content-center">
                                                <%--ANTERIOR--%>
                                            <c:if test="${invitedPage == 0}">
                                            <li class="page-item disabled">
                                                </c:if>
                                                <c:if test="${invitedPage != 0}">
                                            <li class="page-item">
                                                </c:if>
                                                <a class="page-link" href="<c:url value="/dashboard/community/admitted?requestedPage=${requestedPage}&invitedPage=${invitedPage-1}&rejectedPage=${rejectedPage}"/>">
                                                    <i class="fa fa-angle-left"></i>
                                                </a>
                                            </li>


                                            <%--PÁGINAS--%>
                                            <c:if test="${invitedPages > 1}">
                                            <c:forEach var="pageNumber" begin="1" end="${invitedPages}">
                                                <c:if test="${pageNumber-1 == invitedPage}">
                                                    <li class="page-item active">
                                                </c:if>
                                                <c:if test="${pageNumber-1 != invitedPage}">
                                                    <li class="page-item">
                                                </c:if>
                                                <a class="page-link" href="<c:url value="/dashboard/community/admitted?requestedPage=${requestedPage}&invitedPage=${pageNumber-1}&rejectedPage=${rejectedPage}"/>">${pageNumber}</a>
                                                </li>
                                            </c:forEach>
                                            </c:if>

                                                <%--SIGUIENTE--%>
                                            <c:if test="${invitedPage == invitedPages}">
                                            <li class="page-item disabled">
                                                </c:if>
                                                <c:if test="${invitedPage != invitedPages}">
                                            <li class="page-item">
                                                </c:if>
                                                <a class="page-link" href="<c:url value="/dashboard/community/admitted?requestedPage=${requestedPage}&invitedPage=${invitedPage+1}&rejectedPage=${rejectedPage}"/>">
                                                    <i class="fa fa-angle-right"></i>
                                                </a>
                                            </li>
                                        </ul>
                                    </nav>

                            </div>
                            <hr>
                        </c:if>

                        <%--PEDIDOS PENDIENTES--%>
                        <div class="">
                            <p class="h3 text-primary"><spring:message code="dashboard.pendingRequests"/></p>

                            <c:if test="${requested.size() == 0}">
                                <p class="h3 text-gray"><spring:message code="dashboard.noPendingRequests"/></p>
                            </c:if>

                            <div class="overflow-auto">
                                <c:forEach items="${requested}" var="community">
                                    <div class="card">
                                        <p class="h4 card-title"><c:out value="${community.name}"/></p>
                                    </div>
                                </c:forEach>
                                <%--PAGINACIÓN--%>
                                <nav>
                                    <ul class="pagination justify-content-center">
                                            <%--ANTERIOR--%>
                                        <c:if test="${requestedPage == 0}">
                                        <li class="page-item disabled">
                                            </c:if>
                                            <c:if test="${requestedPage != 0}">
                                        <li class="page-item">
                                            </c:if>
                                            <a class="page-link" href="<c:url value="/dashboard/community/admitted?requestedPage=${requestedPage-1}&invitedPage=${invitedPage}&rejectedPage=${rejectedPage}"/>">
                                                <i class="fa fa-angle-left"></i>
                                            </a>
                                        </li>

                                            <%--PÁGINAS--%>
                                        <c:if test="${requestedPages > 1 }">
                                        <c:forEach var="pageNumber" begin="1" end="${requestedPages}">
                                            <c:if test="${pageNumber-1 == requestedPage}">
                                                <li class="page-item active">
                                            </c:if>
                                            <c:if test="${pageNumber-1 != requestedPage}">
                                                <li class="page-item">
                                            </c:if>
                                            <a class="page-link" href="<c:url value="/dashboard/community/admitted?requestedPage=${pageNumber-1}&invitedPage=${invitedPage}&rejectedPage=${rejectedPage}"/>">${pageNumber}</a>
                                            </li>
                                        </c:forEach>
                                        </c:if>

                                            <%--SIGUIENTE--%>
                                        <c:if test="${requestedPage == requestedPages}">
                                        <li class="page-item disabled">
                                            </c:if>
                                            <c:if test="${requestedPage != requestedPages}">
                                        <li class="page-item disabled">
                                            </c:if>
                                            <a class="page-link" href="<c:url value="/dashboard/community/admitted?requestedPage=${requestedPage+1}&invitedPage=${invitedPage}&rejectedPage=${rejectedPage}"/>">
                                                <i class="fa fa-angle-right"></i>
                                            </a>
                                        </li>
                                    </ul>
                                </nav>
                            </div>
                        </div>

                        <%--PEDIDOS RECHAZADOS--%>
                        <c:if test="${rejected.size() != 0}">
                            <hr>
                            <div class="">
                                <div class="overflow-auto">
                                    <p class="h3 text-primary"><spring:message code="dashboard.rejectedRequests"/></p>
                                    <c:forEach items="${rejected}" var="community">
                                        <div class="card">
                                            <div class="d-flex flex-row mt-3" style="justify-content: space-between">
                                                <p class="h4 card-title ml-2"><c:out value="${community.name}"/></p>
                                                <c:url value="/dashboard/community/${community.id}/requestAccess" var="requestPostPath"/>
                                                <form action="${requestPostPath}" method="post">
                                                    <button class="btn mb-0" >
                                                        <div class="h4 mb-0">
                                                            <<i class="fas fa-redo-alt"></i>
                                                        </div>
                                                    </button>
                                                </form>
                                            </div>
                                        </div>
                                    </c:forEach>

                                    <%--PAGINACIÓN--%>
                                    <nav>
                                            <ul class="pagination justify-content-center">
                                                    <%--ANTERIOR--%>
                                                <c:if test="${rejectedPage == 0}">
                                                <li class="page-item disabled">
                                                    </c:if>
                                                    <c:if test="${rejectedPage != 0}">
                                                <li class="page-item">
                                                    </c:if>
                                                    <a class="page-link" href="<c:url value="/dashboard/community/manageAccess?requestedPage=${requestedPage}&invitedPage=${invitedPage}&rejectedPage=${rejectedPage-1}"/>">
                                                        <i class="fa fa-angle-left"></i>
                                                    </a>
                                                </li>

                                                    <%--PÁGINAS--%>
                                                <c:if test="${rejectedPages > 1 }">
                                                <c:forEach var="pageNumber" begin="1" end="${rejectedPages}">
                                                    <c:if test="${pageNumber-1 == rejectedPage}">
                                                        <li class="page-item active">
                                                    </c:if>
                                                    <c:if test="${pageNumber-1 != rejectedPage}">
                                                        <li class="page-item">
                                                    </c:if>
                                                    <a class="page-link" href="<c:url value="/dashboard/community/manageAccess?requestedPage=${requestedPage}&invitedPage=${invitedPage}&rejectedPage=${pageNumber-1}"/>">${pageNumber}</a>
                                                    </li>
                                                </c:forEach>
                                                </c:if>

                                                    <%--SIGUIENTE--%>
                                                <c:if test="${rejectedPage == rejectedPages}">
                                                <li class="page-item disabled">
                                                    </c:if>
                                                    <c:if test="${rejectedPage != rejectedPages}">
                                                <li class="page-item disabled">
                                                    </c:if>
                                                    <a class="page-link" href="<c:url value="/dashboard/community/manageAccess?requestedPage=${requestedPage}&invitedPage=${invitedPage}&rejectedPage=${rejectedPage+1}"/>">
                                                        <i class="fa fa-angle-right"></i>
                                                    </a>
                                                </li>
                                            </ul>
                                        </nav>
                                </div>
                            </div>
                        </c:if>
                    </div>
                </div>
            </div>

            <%--HACER PREGUNTA--%>
            <div class="col-3">
                <div class="white-pill mt-5 mr-3">
                    <div class="card-body">
                        <p class="h3 text-primary text-center"><spring:message code="title.createCommunity"/></p>
                        <hr>
                        <p class="h5 my-3"><spring:message code="subtitle.createCommunity"/></p>
                        <a class="btn btn-primary" href="<c:url value="/community/create"/>"><spring:message code="button.createCommunity"/></a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>


</body>
</html>
