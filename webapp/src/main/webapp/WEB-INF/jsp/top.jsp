<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<html>

<head>
    <meta charset="utf-8">
    <title>AskAway | ${question.community.name}</title>
    <!-- Argon CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0-beta3/dist/css/bootstrap.min.css" rel="stylesheet"
          integrity="sha384-eOJMYsd53ii+scO/bJGFsiCZc+5NDVN2yr8+0RDqr0Ql0h+rP48ckxlpbzKgwra6" crossorigin="anonymous">
    <link type="text/css" href="<c:url value="/resources/styles/argon-design-system.css"/>" rel="stylesheet">
    <link rel="stylesheet" href="<c:url value="/resources/styles/general.css"/>" type="text/css">

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
                        <c:if test="${answerList.size() == 0}">
                            <div class="d-flex justify-content-center">
                                <p class="row h3 text-gray mx-3"><spring:message code="notFound"></spring:message></p>
                            </div>
                            <div class="d-flex justify-content-center">
                                <img class="row w-25 h-25" src="<c:url value="/resources/images/empty.png"/>"
                                     alt="No hay nada para mostrar">
                            </div>
                        </c:if>
                        <div class="overflow-auto">
                            <c:forEach items="${answerList}" var="answer">
                                <div class="card p-3 m-3 justify-content-center">

                                    <div class="row">
                                        <div class="col justify-content-center mt-3">
                                            <p class="h5">${answer.body}</p>
                                            <div class="d-flex justify-content-start">
                                                <p class="h7"><spring:message code="answer.owner" arguments="${answer.owner.username}"/></p>
                                            </div>
                                        </div>

                                        <!--TICK DE VERIF -->

                                        <c:if test="${answer.verify == true}">
                                            <div class="col-auto">
                                                <div class="d-flex justify-content-sm-start">
                                                    <img width="30" height="30" data-toggle="tooltip" data-placement="top" title="El propietario de la pregunta marco la respuesta como correcta" src="<c:url value="/resources/images/success.png"/> ">
                                                </div>
                                            </div>
                                        </c:if>

                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<script>
    function send(id){
        document.querySelector("#voteForm" + id).submit();
    }

    function upVote(id){
        document.querySelector("#vote"+ id).value = true;
        send(id);
    }

    function downVote(id){
        document.querySelector("#vote"+ id).value = false;
        send(id);
    }
</script>
</body>
</html>



