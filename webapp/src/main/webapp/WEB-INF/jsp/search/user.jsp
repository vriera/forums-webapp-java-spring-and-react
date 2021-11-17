<%----%><!DOCTYPE html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>

<html>
<head>

    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta charset="utf-8">
    <title><spring:message code="community.title.all"/></title>


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

    <script>
        function saveSelectsAndQuery(){
            let url = new URL(window.location.href);
            let query = url.searchParams.get("query");
            let filter = url.searchParams.get("filter");
            let order = url.searchParams.get("order");
            let filterSelect = document.getElementById('filterSelect');
            let orderSelect = document.getElementById('orderSelect');
            let searchBar = document.getElementById('query');
            if( filter ) {
                filterSelect.selectedIndex = filter;
            }
            if(order) {
                orderSelect.selectedIndex = order;
            }
            if(query) {
                searchBar.value = query;
            }
        }
    </script>



</head>
<body onload="saveSelectsAndQuery()">

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
        <div class="shape shape-style-1 shape-default shape-skew">
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

        <div>
            <%--TARJETA SUPERIOR--%>
            <div class="col-6 center">
                <div class="white-pill h-75 ">
                    <div class="align-items-start d-flex justify-content-center my-3">
                        <p class="h1 text-primary bold"><strong><spring:message code="askAway"/></strong></p>
                    </div>
                    <div class="text-gray text-center mt--4 mb-2"><spring:message code="users"/></div>
                    <%--BARRA DE BÚSQUEDAS--%>
                    <div class="form-group mx-5">
                        <form action="<c:url value="/user/search"/>" method="get">
                            <div class="input-group">
                                <input class="form-control rounded" type="search" name="query" id="query" placeholder="<spring:message code="placeholder.users.search"/>">
                                <input class="btn btn-primary" type="submit" value="<spring:message code="button.search"/>">
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>

        <div class="row">
            <%--COMUNIDADES--%>
            <div class="col-3 ">
                <div class="white-pill mt-5 ml-3">
                    <div class="card-body">
                        <p class="h3 text-primary text-center"><spring:message code="community.communities"/></p>
                        <hr>
                        <%--Badges de las comunidades--%>
                        <div class="container-fluid">
                            <a class="btn btn-light badge-pill badge-lg my-3" href="<c:url value="/community/view/all"/>"><spring:message code="community.all"/></a>
                            <c:forEach items="${communityList}" var="community">
                                <a class="btn btn-outline-primary badge-pill badge-lg my-3" href="<c:url value="/community/view/${community.id}"/>"><c:out value="${community.name}"/></a>
                            </c:forEach>
                        </div>
                    </div>
                </div>
            </div>

            <%--USUARIOS--%>
            <div class="col-6">
                <div class="white-pill mt-5">
                    <div class="card-body">
                        <%--todas los usuarios--%>
                        <div class="h2 text-primary">
                            <ul class="nav nav-tabs">
                                <li class="nav-item">
                                    <a class="nav-link" href="<c:url value="/community/view/all?query=${param.query}"/>"><spring:message code="questions"/></a>
                                </li>
                                <li class="nav-item">
                                    <a class="nav-link" href="<c:url value="/community/search?query=${param.query}"/>"><spring:message code="communities"/></a>
                                </li>
                                <li class="nav-item">
                                    <a class="nav-link active" href="#"><spring:message code="users"/></a>
                                </li>
                            </ul>
                        </div>
                        <c:if test="${userList.size() == 0}">
                            <p class="row h1 text-gray"><spring:message code="community.noResults"/></p>
                            <div class="d-flex justify-content-center">
                                <img class="row w-25 h-25" src="<c:url value="/resources/images/empty.png"/>" alt="No hay nada para mostrar">
                            </div>
                        </c:if>
                        <div class="overflow-auto">
                            <c:forEach items="${userList}" var="user">
                                <a class="d-block" href="<c:url value="/user/${user.id}"/>">
                                    <jsp:include page="/WEB-INF/jsp/components/userCard.jsp">
                                        <jsp:param name="username" value="${user.username}"/>
                                        <jsp:param name="email" value="${user.email}"/>
                                    </jsp:include>
                                </a>
                            </c:forEach>
                        </div>
                    </div>
                    <c:if test="${count > 0}">
                        <nav aria-label="...">
                            <form method="get" id="paginationForm">
                                <input type="hidden" name="page" id="page" value=""/>
                                <ul class="pagination">
                                    <c:if test="${currentPage > 1}">
                                        <li>
                                            <a class="page-link mr-2 " onclick="submit(${currentPage - 1})" tabindex="-1"><spring:message code="community.previous"/></a>
                                            <span class="sr-only"></span>
                                        </li>
                                    </c:if>

                                    <c:forEach begin="1" end="${count}" var="i">
                                        <li class="page-item ${currentPage == i ? "active":""}"><a class="page-link" onclick="submit(${i})"><c:out value="${i}"/></a></li>
                                    </c:forEach>

                                    <c:if test="${currentPage < count}">
                                        <li >
                                            <a class="page-link ml-1" onclick="submit(${currentPage + 1})"><spring:message code="community.next"/></a>
                                            <span class="sr-only"></span>
                                        </li>
                                    </c:if>
                                </ul>
                            </form>
                        </nav>
                    </c:if>
                </div>
            </div>

            <%--HACER PREGUNTA--%>
            <div class="col-3">
                <div class="white-pill mt-5 mr-3">
                    <div class="card-body">
                        <p class="h3 text-primary text-center"><spring:message code="landing.question.trigger"/></p>
                        <hr>
                        <p class="h5 my-3"><spring:message code="landing.question.callToAction"/></p>
                        <a class="btn btn-primary" href="<c:url value="/question/ask/community"/>"><spring:message code="ask"/></a>
                    </div>
                </div>
            </div>

        </div>


    </div>
</div>
<script>
    function submit(page){
        document.querySelector("#page").value = page;
        document.querySelector("#paginationForm").submit();

    }
</script>

</body>
</html>
