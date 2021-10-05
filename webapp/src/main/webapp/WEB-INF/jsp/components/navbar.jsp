
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
    <body>
        <nav class="navbar border-bottom">
            <div class="container-fluid navbar-brand">

                <a  class="navbar-brand" href="<c:url value="/"/>">
                    <img src="<c:url value="/resources/images/birb.png"/>" width="30" height="30"/>
                    AskAway
                </a>
                <div class="d-flex justify-content-md-start">
                    <div class="nav-item">
                        <a class="nav-link" aria-current="page" href="<c:url value="/credentials/register"/> "><spring:message key="register.register"/> </a>
                    </div>
                    <div class="nav-item">
                        <a class="nav-link" href="<c:url value="/credentials/login"/> "><spring:message key="register.login"/></a>
                    </div>
                </div>


            </div>
        </nav>
    </body>
</html>

