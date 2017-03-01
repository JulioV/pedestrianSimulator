<%@page contentType="application/json; charset=UTF-8" %>
<jsp:useBean id="simulacion" class="Beans.SimulacionBean" scope="session"></jsp:useBean>
 
  <%
      int intervalo = Integer.parseInt(request.getParameter("i"));
      out.print(simulacion.getCampoSeguimientoEnIntervaloJSON(intervalo));
  %>