<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<!doctype html>
<html lang="en">
<head>
    <!-- Required meta tags -->
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">

    <!-- Bootstrap CSS -->
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css"
          integrity="sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm" crossorigin="anonymous">

    <title>Hello, world!</title>
</head>
<body>
<script src="https://code.jquery.com/jquery-3.2.1.slim.min.js"
        integrity="sha384-KJ3o2DKtIkvYIK3UENzmM7KCkRr/rE9/Qpg6aAZGJwFDMVNA/GpGFF93hXpG5KkN"
        crossorigin="anonymous"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.9/umd/popper.min.js"
        integrity="sha384-ApNbgh9B+Y1QKtv3Rn7W3mgPxhU9K/ScQsAP7hUibX39j7fakFPskvXusvfa0b4Q"
        crossorigin="anonymous"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js"
        integrity="sha384-JZR6Spejh4U02d8jOt6vLEHfe/JQGiRRSQQxSfFWpi1MquVdAyjUar5+76PVCmYl"
        crossorigin="anonymous"></script>

<div class="container">
    <br class="row pt-3">
    <h4>
        Бронирование билетов на сеанс
    </h4>
    <form action="<%=request.getContextPath()%>/go" method="post">
        <table>
            <thead>
            <img src="<c:url value='screen.png'/>" width="700px" height="150px"/>
            </thead>
            <tbody>
            <c:forEach begin="1" end="9" varStatus="rows">
                <tr>
                    <th><c:out value="${rows.count} ряд"/></th>
                    <c:forEach begin="1" end="12" varStatus="cells">
                        <c:set var="myTest" value="empty"/>
                        <c:forEach items="${tickets}" var="ticet" varStatus="status">
                            <c:if test="${ticet.row == rows.count && ticet.cell == cells.count}">
                                <c:set var="myTest" value="occupied"/>
                                <c:if test="${ticet.sessionId == 0}">
                                    <td><img src="<c:url value='occupied.png'/>" width="50px" height="50px"/></td>
                                </c:if>
                                <c:if test="${ticet.sessionId != 0}">
                                    <td><img src="<c:url value='booked.png'/>" width="50px" height="50px"/></td>
                                </c:if>
                            </c:if>
                        </c:forEach>
                        <c:if test="${myTest == 'empty'}">
                        <td><a href='<c:url value="/ticket?row=${rows.count}&cell=${cells.count}"/>'> <img
                                src="<c:url value='empty.jpg'/>" width="50px" height="50px"/></a></td>
                        </c:if>
                    </c:forEach>
                </tr>
            </c:forEach>
            </tbody>
        </table>
        <div>
            <button type="submit" class="btn btn-success">Оплатить</button>
        </div>
    </form>
</div>
</body>
</html>