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


        <div class="row">
            <%--OTRAS COMUNIDADES--%>
                <div class="col-3">
                    <div class="white-pill mt-5 ml-3">
                        <div class="card-body">
                            <p class="h3 text-primary"><spring:message code="comunities"/></p>
                            <hr>
                            <%--BADGES--%>
                            <div class="container-fluid">
                                <a class="btn btn-light badge-pill badge-lg my-3" href="<c:url value="/community/view/${question.community.id}"/>">${question.community.name}</a>
                                <c:forEach items="${communityList}" var="community">
                                    <a class="btn btn-outline-primary badge-pill badge-lg my-3" href="<c:url value="/community/view/${community.id}"/>">${community.name}</a>
                                </c:forEach>
                                <a class="btn btn-outline-primary badge-pill badge-lg my-3" href="<c:url value="/community/create"/>">CREAR COMUNIDAD</a>
                            </div>
                        </div>
                    </div>
                </div>

            <%--PREGUNTA PRINCIPAL--%>
            <div class="col-6">
                <div class="white-pill mt-5">
                    <div class="card-body">
                        <div class="d-flex justify-content-center">
                            <p class="h1 text-primary">${question.title}</p>
                        </div>

                        <!--para que voting quede side by side con el cuerpo  -->
                        <div class="row">
                            <%-- VOTING --%>
                            <div class="col-auto">
                                <div class="d-flex justify-content-sm-start">
                                    <c:url value="/question/${question.id}/vote" var="postPath"/>
                                    <form:form id="voteFormQ${question.id}" method="post" action="${postPath}">
                                        <input type="hidden" name="vote" id="voteQ${question.id}"/>
                                        <i class="clickable" onclick="upVote('Q' +${question.id})">
                                            <img src="<c:url value="/resources/images/upvote.png"/>" width="30" height="30"/>
                                        </i>
                                        <p class="h5" style="text-align: center">${question.votes}</p>
                                        <i class="clickable" onclick="downVote('Q' + ${question.id})">
                                            <img src="<c:url value="/resources/images/downvote.png"/>" width="30" height="30"/>
                                        </i>
                                    </form:form>
                                </div>
                            </div>

                            <%--Cuerpo de la pregunta --%>
                            <div class="col-9">
                                <%--Formulada por y el nombre --%>
                                <div class="col-sm d-flex justify-content-center">
                                    <p class="h7"><spring:message code="question.owner" arguments="${question.owner.username}"/></p>
                                </div>
                                <div class="d-flex justify-content-center">
                                    <p class="h5">${question.body}</p>
                                </div>
                                    <%--foto de la pregunta --%>
                                <c:if test="${question.imageId != null }">
                                    <img src="<c:url value="/image/${question.imageId}"/>">
                                </c:if>

                            </div>

                        </div>


                        <hr>
                        <div class="d-flex justify-content-center">
                            <p class="h3 text-primary"><spring:message code="answers.title" arguments="${answerList.size()}"></spring:message></p>
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


                                        <!-- voting-->
                                        <div class="col-auto d-flex justify-content-sm-end">
                                            <c:url value="/question/answer/${answer.id}/vote" var="postPath"/>
                                            <form:form id="voteForm${answer.id}" method="post" action="${postPath}">
                                                <input type="hidden" name="vote" id="vote${answer.id}"/>
                                                <i class="clickable" onclick="upVote(${answer.id})">
                                                    <img src="<c:url value="/resources/images/upvote.png"/>" width="30" height="30"/>
                                                </i>
                                                <p class="h5" style="text-align: center">${answer.vote}</p>
                                                <i class="clickable" onclick="downVote(${answer.id})">
                                                    <img src="<c:url value="/resources/images/downvote.png"/>" width="30" height="30"/>
                                                </i>
                                            </form:form>
                                        </div>

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

            <%--HACER PREGUNTA--%>
            <div class="col-3">
                <div class="white-pill mt-5 mr-3">
                    <div class="card-body">
                        <p class="h3 text-primary"><spring:message code="respond"/></p>
                        <hr>
                        <c:url value="/question/${question.id}/answer" var="postPath"/>
                        <form:form method="post" modelAttribute="answersForm" action="${postPath}">
                            <div class="form-group">
                                <form:label path="body"><spring:message code="answer.your"/></form:label>
                                <form:textarea path="body" class="form-control" id="body" rows="3"/>
                                <form:errors path="body" cssClass="error" element="p"/>
                            </div>
                            <div class="d-flex justify-content-center mb-3 mt-3">
                                <button type="submit" class="btn btn-primary"><spring:message code="send"/></button>
                            </div>
                            </form:form>
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



