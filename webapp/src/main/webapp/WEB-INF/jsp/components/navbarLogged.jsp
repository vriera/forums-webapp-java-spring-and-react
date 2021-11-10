
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<html>
<body>
<nav class="navbar border-bottom">
    <div class="container-fluid navbar-brand">

        <div>
            <div class="d-flex justify-content-end">
                <a  class="navbar-brand" href="<c:url value="/"/>">
                    <img src="<c:url value="/resources/images/birb.png"/>" width="30" height="30"/>
                    AskAway
                </a>
                <div class="nav-item ml-3">
                    <a class="nav-link" aria-current="page" href="<c:url value="/question/ask/community"/> "><spring:message code="title.askQuestion"/> </a>
                </div>
                <div class="nav-item">
                    <a class="nav-link" href="<c:url value="/community/create"/> "><spring:message code="title.createCommunity"/></a>
                </div>
                <div class="nav-item">
                    <a class="nav-link" href="<c:url value="/community/view/all"/> "><spring:message code="title.viewAllQuestions"/></a>
                </div>
            </div>
        </div>

        <div class="dropdown " >
            <button class="btn btn-primary pb-0" data-bs-toggle="dropdown" type="button" aria-expanded="false">
                <c:if test="${param.user_notifications > 0 }">
                    <span class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-warning py-0 ">
                           <div class="text-white h6 mx-1 my-0">${param.user_notifications}</div>
                    </span>
                </c:if>
                <div class="dropdown_title row">
                    <div class="col-auto">
                        <img src="https://avatars.dicebear.com/api/avataaars/${param.user_email}.svg" class="img"  alt="profile"/>
                    </div>
                   <div class="col-auto">
                       <p class="margin-sides-3"><c:out value="${param.user_name}"/></p>
                   </div>
                    <div class="col-auto">
                        <i class="fas fa-caret-down"></i>
                    </div>
                </div>

            </button>

            <div class="dropdown-menu" aria-labelledby="dropdownMenuButton">
                <a class="dropdown-item" href="<c:url value="/dashboard/question/view"/>">Dashboard</a> <%--FIXME: cambiar el href al que corresponde, esto es para testeo--%>
                <a class="dropdown-item" href="<c:url value="/credentials/logout"/>">Logout</a>
            </div>
        </div>
    </div>
</nav>
</body>
</html>