<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://java.sun.com/jsp/jstl/fmt" %>

<html>

    <body>
            <div class="card p-3 m-3 shadow-sm--hover ">

                <div class="d-flex">
                    <div class="text-center mr-3">
                        <img class="rounded-circle" src="https://avatars.dicebear.com/api/avataaars/${param.email}.svg" style="height: 80px; width: 80px;">
                    </div>
                    <%--<p class="h1 text-center text-primary">${user.username}</p>--%>
                    <p class="h1 text-center text-primary">${param.username}</p>
                </div>

            </div>

    </body>

</html>