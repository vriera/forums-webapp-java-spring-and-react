<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<html>

<head>
    <meta charset="utf-8">
    <title>AskAway | ${community.name}</title>
    <!-- Argon CSS -->
    <%--<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0-beta3/dist/css/bootstrap.min.css" rel="stylesheet"
          integrity="sha384-eOJMYsd53ii+scO/bJGFsiCZc+5NDVN2yr8+0RDqr0Ql0h+rP48ckxlpbzKgwra6" crossorigin="anonymous">--%>
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
    <c:when test="${user == true}">
        <jsp:include page="/WEB-INF/jsp/components/navbarLogged.jsp">
            <jsp:param name="user_name" value="${user_name}"/>
            <jsp:param name="user_email" value="user_email"/>
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

        <%--Toast para cuando creo la comunidad --%>
        <c:choose>
            <c:when test="${justCreated == true}">
                <div class="position-fixed top-0 p-3 animate" style="z-index: 11; margin-left: 75%;">
                    <div id="toast" role="alert" class="toast show" >
                        <div class="toast-header" >
                            <img src="<c:url value="/resources/images/birb.png"/>" style="width: 50px; height: 50px;" class="rounded me-2" alt="...">
                            <strong class="me-auto">¡Felicitaciones!</strong>
                        </div>
                        <div class="toast-body">
                            Tu comunidad fue creada con éxito.
                        </div>
                    </div>
                </div>
            </c:when>
        </c:choose>



        <div>
            <%--TARJETA SUPERIOR--%>
            <div class="col-6 center">
                <div class="white-pill">
                    <div class="card-body">
                        <div class="d-flex justify-content-center">
                            <p class="h1 text-primary"><c:out value="${community.name}"/></p>
                        </div>
                        <div class="d-flex justify-content-center">
                            <p><c:out value="${community.description}"/></p>
                        </div>
                        <c:if test="${community.moderator.id != 0}">
                        <div class="d-flex justify-content-center">
                            <p><c:out value=""/><spring:message code="question.owner" arguments="${community.moderator.username}"/></p>
                        </div>
                        </c:if>
                        <%--BARRA DE BÚSQUEDAS--%>
                        <div class="form-group mx-5">
                            <form action="<c:url value="/community/view/${community.id}"/>" method="get">
                                <div class="input-group">
                                    <input class="form-control rounded" type="search" name="query" id="query" placeholder="Buscá una pregunta acá">
                                    <input class="btn btn-primary" type="submit" value="Buscar">
                                </div>
                                <c:if test="${query != null}">
                                    <p class="h4">Resultados para: ${query}</p>
                                </c:if>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="row">
            <%--OTRAS COMUNIDADES--%>
            <div class="col-3">
                <div class="white-pill mt-5 ml-3">
                    <div class="card-body">
                        <p class="h3 text-primary text-center">OTRAS COMUNIDADES</p>
                        <hr>
                        <%--BADGES--%>
                        <div class="container-fluid">

                            <a class="btn btn-outline-primary badge-pill badge-lg my-3" href="<c:url value="/community/view/all"/>">Todas</a>
                            <c:forEach items="${communityList}" var="community">
                                <c:if test="${community.id == communityId}">
                                    <a class="btn btn-light badge-pill badge-lg my-3" href="<c:url value="/community/view/${community.id}"/>">${community.name}</a>
                                </c:if>
                                <c:if test="${community.id != communityId}">
                                    <a class="btn btn-outline-primary badge-pill badge-lg my-3" href="<c:url value="/community/view/${community.id}"/>">${community.name}</a>
                                </c:if>
                            </c:forEach>
                        </div>
                    </div>
                </div>
            </div>

            <%--PREGUNTAS--%>
            <div class="col-6">
                <div class="white-pill mt-5">
                    <div class="card-body">

                        <p class="h3 text-primary text-center">PREGUNTAS</p>
                        <hr>
                        <c:if test="${canAccess==false}">
                            <p class="h3 text-gray">No fuiste admitido a esta comunidad, para ver su contenido pedí acceso!</p>
                            <a class="btn btn-primary" href="/dashboard/community/${communityId}/requestAccess">Pedir</a>
                        </c:if>
                        <c:if test="${canAccess==true}">
                        <c:if test="${questionList.size() == 0}">
                            <p class="row h1 text-gray">No encontramos nada :(</p>
                            <div class="d-flex justify-content-center">
                                <img class="row w-25 h-25" src="<c:url value="/resources/images/empty.png"/>" alt="No hay nada para mostrar">
                            </div>
                        </c:if>
                        <div class="overflow-auto">
                            <c:forEach items="${questionList}" var="question">
                                <a class="d-block" href="<c:url value="/question/view/${question.id}"/>">
                                    <div class="card p-3 m-3 shadow-sm--hover ">
                                        <div class="row">
                                            <div class="d-flex flex-column justify-content-start ml-3">
                                                <div class="h2 text-primary"><c:out value="${question.title}"/></div>
                                            </div>
                                            <div class="col-12 text-wrap-ellipsis">
                                                <p class="h5"><c:out value="${question.body}"/></p>
                                            </div>
                                        </div>
                                    </div>
                                </a>
                            </c:forEach>
                        </div>
                        </c:if>
                    </div>
                </div>
            </div>

            <%--HACER PREGUNTA--%>
            <div class="col-3">
                <div class="white-pill mt-5 mr-3">
                    <div class="card-body">
                        <p class="h3 text-primary text-center">HACÉ UNA PREGUNTA</p>
                        <hr>
                        <p class="h5 my-3">Enviá una pregunta a nuestros distintos foros para que la comunidad la responda.</p>
                        <a class="btn btn-primary" href="<c:url value="/question/ask/content?communityId=${community.id}"/>">Preguntar</a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>


</body>
</html>