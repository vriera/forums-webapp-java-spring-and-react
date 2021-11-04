<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<html>

<head>
    <meta charset="utf-8">
    <title><spring:message code="question.view.title" arguments="${question.forum.community.name}"/></title>
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


        <div class="row">
            <%--OTRAS COMUNIDADES--%>
            <div class="col-3">
                <div class="white-pill mt-5 ml-3">
                    <div class="card-body">
                        <p class="h3 text-primary"><spring:message code="comunities"/></p>
                        <hr>
                        <%--BADGES--%>
                        <div class="container-fluid">
                            <a class="btn btn-outline-primary badge-pill badge-lg my-3" href="<c:url value="/community/view/all"/>"><spring:message code="community.all"/></a>
                            <c:forEach items="${communityList}" var="community">
                                <c:if test="${community.id == question.community.id}">
                                    <a class="btn btn-light badge-pill badge-lg my-3" href="<c:url value="/community/view/${community.id}"/>"><c:out value="${community.name}"/></a>
                                </c:if>
                                <c:if test="${community.id != question.community.id}">
                                    <a class="btn btn-outline-primary badge-pill badge-lg my-3" href="<c:url value="/community/view/${community.id}"/>"><c:out value="${community.name}"/></a>
                                </c:if>
                            </c:forEach>
                        </div>
                    </div>
                </div>
            </div>

            <%--PREGUNTA PRINCIPAL--%>
            <div class="col-6">
                <div class="white-pill mt-5">
                    <div class="card-body">
                        <div class="d-flex justify-content-center">
                            <p class="h1 text-primary"><c:out value="${question.title}"/></p>
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
                                            <img src="<c:url value="/resources/images/upvote.png"/>" width="30"
                                                 height="30"/>
                                        </i>
                                        <p class="h5" style="text-align: center"><c:out value="${question.votes}"/></p>
                                        <i class="clickable" onclick="downVote('Q' + ${question.id})">
                                            <img src="<c:url value="/resources/images/downvote.png"/>" width="30"
                                                 height="30"/>
                                        </i>
                                    </form:form>
                                </div>
                            </div>

                            <%--Cuerpo de la pregunta --%>
                            <div class="col">

                                <div class="d-flex flex-column justify-content-center ml-3">
                                    <div class="justify-content-center">
                                        <p><span class="badge badge-primary badge-pill"><c:out value="${question.community.name}"/></span></p>
                                    </div>
                                    <div class="justify-content-center">
                                        <p class="h6"><spring:message code="question.askedBy"/> <c:out value="${question.owner.username}"/></p>
                                    </div>

                                </div>
                                <div class="col-12 text-wrap-ellipsis justify-content-center">
                                    <p class="h5"><c:out value="${question.body}"/></p>
                                </div>

                                <%--foto de la pregunta --%>
                                <c:if test="${question.imageId != null && question.imageId != 0 }">
                                    <img src="<c:url value="/image/${question.imageId}"/>" style="object-fit: cover; width: 100%; height: 70%;">
                                </c:if>

                                <div class="d-flex ml-3 align-items-center ">
                                    <div class="h4">
                                        <i class="fas fa-calendar"></i>
                                    </div>
                                    <p class="ml-3 h6">${question.smartDate.date}</p>
                                </div>
                            </div>

                        </div>


                        <hr>
                        <div class="d-flex justify-content-center">
                            <p class="h3 text-primary"><spring:message code="answers.title"
                                                                       arguments="${countAnswers}"/></p>
                        </div>
                        <c:if test="${answerList.size() == 0}">
                            <div class="d-flex justify-content-center">
                                <p class="row h3 text-gray mx-3"><spring:message code="notFound"/></p>
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


                                        <div class="col-auto d-flex justify-content-sm-end">
                                            <c:url value="/question/answer/${answer.id}/vote" var="postPath"/>
                                            <form:form id="voteForm${answer.id}" method="post" action="${postPath}">
                                                <input type="hidden" name="vote" id="vote${answer.id}"/>
                                                <c:choose>
                                                    <c:when test="${answer.getAnswerVote(currentUser).equals(true)}">
                                                        <i class="clickable" aria-pressed="true" onclick="nullVote(${answer.id})">
                                                        <img src="<c:url value="/resources/images/upvotep.png"/>" width="30"
                                                             height="30"/>
                                                    </c:when>
                                                    <c:otherwise>
                                                            <i class="clickable" aria-pressed="true" onclick="upVote(${answer.id})">
                                                            <img src="<c:url value="/resources/images/upvote.png"/>" width="30"
                                                             height="30"/>
                                                    </c:otherwise>
                                                    </c:choose>

                                                </i>
                                                <p class="h5" style="text-align: center"><c:out value="${answer.vote}"/></p>
                                                <c:choose>
                                                    <c:when test="${answer.getAnswerVote(currentUser).equals(false)}">
                                                        <i class="clickable" onclick="nullVote(${answer.id})">
                                                        <img src="<c:url value="/resources/images/downvotep.png"/>"
                                                         width="30" height="30"/>
                                                        </i>
                                                    </c:when>
                                                    <c:otherwise>
                                                            <i class="clickable" onclick="downVote(${answer.id})">
                                                        <img src="<c:url value="/resources/images/downvote.png"/>"
                                                             width="30" height="30"/>
                                                            </i>
                                                    </c:otherwise>
                                                    </c:choose>
                                            </form:form>
                                        </div>

                                        <div class="col justify-content-center mt-3">
                                            <p class="h5"><c:out value="${answer.body}"/></p>
                                            <div class="d-flex justify-content-start">
                                                <p class="h7"><spring:message code="answer.owner"
                                                                              arguments="${answer.owner.username}"/></p>
                                            </div>
                                        </div>

                                        <!--TICK DE VERIF -->

                                        <c:if test="${answer.verify == true}">
                                            <spring:message code="question.view.ownerMarkedAsCorrect" var="imageTitle"/>
                                            <div class="col-auto">
                                                <div class="d-flex justify-content-sm-start">
                                                    <img width="30" height="30" data-toggle="tooltip"
                                                         data-placement="top"
                                                         title="${imageTitle}"
                                                         src="<c:url value="/resources/images/success.png"/> ">

                                                </div>
                                            </div>
                                        </c:if>

                                    </div>
                                    <div class="row">
                                    <!--Boton verif -->
                                    <c:if test="${question.owner.id == currentUser.id}">
                                        <c:url value="/question/answer/${answer.id}/verify/" var="postPath"/>
                                        <form:form id="verifyForm${answer.id}" method="post" action="${postPath}">
                                            <input type="hidden" name="verify" id="verify${answer.id}"/>

                                        <c:if test="${answer.verify == false || answer.verify == null}">
                                                <div class="d-flex justify-content-sm-end">
                                                    <button onclick="verifyAnswer(${answer.id})"
                                                            class="btn btn-primary"><spring:message
                                                            code="verify"/></button>
                                                </div>
                                        </c:if>

                                        <c:if test="${answer.verify == true}">
                                                <div class="d-flex justify-content-sm-end">
                                                    <button onclick="unverify(${answer.id})"
                                                            class="btn btn-primary"><spring:message
                                                            code="unverify"/></button>
                                                </div>
                                        </c:if>
                                        </form:form>
                                    </c:if>
                                        <div class="d-flex flex-row">
                                        <c:if test="${question.owner.id == currentUser.id}">
                                            <div>
                                                <c:url value="/question/answer/${answer.id}/delete" var="postPath"/>
                                                <form:form method="post" action="${postPath}">
                                                    <button type="submit" class="btn btn-secondary">
                                                        <img width="30" height="30" title="${imageTitle}" src="<c:url value="/resources/images/trash.svg"/> ">
                                                    </button>
                                                </form:form>
                                            </div>
                                        </c:if>
                                            <div>
                                                <p class="h7 mt-3" ><c:out value="${answer.time.toGMTString()}"/></p>
                                            </div>


                                        </div>

                                </div>
                                </div>
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
                                            <a class="page-link mr-2 " onclick="submit(${currentPage - 1})"
                                               tabindex="-1">Previous</a>
                                            <span class="sr-only"></span>
                                        </li>
                                    </c:if>

                                    <c:forEach begin="1" end="${count}" var="i">
                                        <li class="page-item ${currentPage == i ? "active":""}"><a class="page-link"
                                                                                                   onclick="submit(${i})">${i}</a>
                                        </li>
                                    </c:forEach>

                                    <c:if test="${currentPage < count}">
                                        <li>
                                            <a class="page-link ml-1" onclick="submit(${currentPage + 1})">Next</a>
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
    function verifyAnswer(id) {
        document.querySelector("#verify" + id).value = true;
        submitVerify(id);

    }

    function unverify(id) {
        document.querySelector("#verify" + id).value = false;
        submitVerify(id);
    }

    function submitVerify(id){
        document.querySelector("#verifyForm" + id).submit();
    }

    function send(id) {
        document.querySelector("#voteForm" + id).submit();
    }

    function upVote(id) {
        document.querySelector("#vote" + id).value = true;
        send(id);
    }

    function downVote(id) {
        document.querySelector("#vote" + id).value = false;
        send(id);
    }

    function nullVote(id) {
        document.querySelector("#vote" + id).value = null;
        send(id);
    }

    function submit(page) {
        document.querySelector("#page").value = page;
        document.querySelector("#paginationForm").submit();

    }


</script>
</body>
</html>



