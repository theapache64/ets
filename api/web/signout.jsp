<%
  session.invalidate();
  response.sendRedirect("/client/signin");
%>
