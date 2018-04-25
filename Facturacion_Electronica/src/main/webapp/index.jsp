<%-- 
    Document   : index
    Created on : 31/03/2018, 08:28:33 PM
    Author     : Emmanuel GR
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Adjuntar Archvo XML</title>
    </head>
    <body>
        <h3>File Upload:</h3>
      Seleccione documento a subir: <br />
      <form action = "Logica" method = "post"
         enctype = "multipart/form-data">
          
         <input type = "file" name = "file" size = "50" />
         <br />
         <input type = "submit" value = "Upload File" />
         
         
      </form>
    </body>
</html>
