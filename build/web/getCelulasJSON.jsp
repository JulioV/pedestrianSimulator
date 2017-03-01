<%@page contentType="application/json; charset=UTF-8" %>
<jsp:useBean id="simulacion" class="Beans.SimulacionBean" scope="session"></jsp:useBean>
 
  <%
      out.print(simulacion.getCelulasJSON());
  %>