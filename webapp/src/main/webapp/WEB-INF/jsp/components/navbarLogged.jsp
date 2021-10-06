
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<body>
<nav class="navbar border-bottom">
    <div class="container-fluid navbar-brand">

        <a  class="navbar-brand" href="<c:url value="/"/>">
            <img src="<c:url value="/resources/images/birb.png"/>" width="30" height="30"/>
            AskAway
        </a>

        <div class="dropdown " >
            <button class="btn btn-primary dropdown-toggle" data-bs-toggle="dropdown" type="button" aria-expanded="false">
                <div class="dropdown_title row">
                    <div class="col-auto">
                        <img src="<c:url value="/resources/images/birb.png"/>" class="img"  alt="profile"/>
                    </div>
                   <div class="col-auto">
                       <p class="margin-sides-3"><c:out value="${param.user_name}"/></p>
                   </div>
                </div>

            </button>

            <div class="dropdown-menu" aria-labelledby="dropdownMenuButton">
                <a class="dropdown-item" href="<c:url value="/dashboard/community/5/view/members?communityPage=0&admittedPage=0&bannedPage=0"/>">Dashboard</a> <%--FIXME: cambiar el href al que corresponde, esto es para testeo--%>
                <a class="dropdown-item" href="<c:url value="/credentials/logout"/>">Logout</a>
            </div>
        </div>
    </div>
</nav>
</body>
</html>