<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html>

<body>
<div class="card p-3 m-3 shadow-sm--hover ">
        <%--<p class="h1 text-center text-primary">${user.username}</p>--%>
        <p class="h1 text-primary">${param.name}</p>
        <c:if test="${param.isModerator == false}">
            <p class="h4 text-gray">${param.description}</p>
            <p class="h6 text-gray"><spring:message code="mod.moderatedBy" arguments="${param.owner}"/></p>
        </c:if>
</div>

</body>

</html>