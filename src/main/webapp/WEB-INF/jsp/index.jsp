<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <title>Drive user</title>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/jquery/js/jquery-1.11.0.js"></script>
  </head>
  <body>
    <c:if test="${errorMessage}">
      <div style="color: #ff0000">${errorMessage}</div>
    </c:if>

    <a href="${pageContext.request.contextPath}/gdrive/authenticate">Authenticate</a>
    <button id="tokens-button">Get tokens</button>

    <script>
      $('#tokens-button').click(function() {
        $.get('/gdrive/getTokens', null, function(result) {
          alert(result);
        });
      });
    </script>
  </body>
</html>