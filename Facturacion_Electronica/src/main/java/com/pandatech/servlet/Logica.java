/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pandatech.servlet;

import com.google.gson.Gson;
import com.pandatech.bean.Callbackurl;
import com.pandatech.bean.IdentificacionEmisor;
import com.pandatech.bean.IdentificacionReceptor;
import com.pandatech.bean.Recepcion;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import static java.lang.System.out;
import java.util.Date;
import java.util.logging.Level;
import javax.json.JsonObject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject; 

/**
 *
 * @author Emmanuel GR
 */
@WebServlet(name = "Logica", urlPatterns = {"/Logica"})
public class Logica extends HttpServlet {

    private static final String IDP_URI = "https://idp.comprobanteselectronicos.go.cr/auth/realms/rut-stag/protocol/openid-connect";
    private static final String IDP_CLIENT_ID = "api-stag";
    private static String usuario = "cpj-3-101-684401@stag.comprobanteselectronicos.go.cr";
    private static String password = "X=!:&OvjqB#C_)XO@#B]";

    private static final String URI = "https://api.comprobanteselectronicos.go.cr/recepcion-sandbox/v1/";

    private String accessToken;
    private String refreshToken;

    Recepcion recepcion = new Recepcion();

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet Autenticacion</title>");
            out.println("</head>");
            out.println("<body>");
            //out.println("<h1>Servlet Autenticacion at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            //out.println("<script>alert('"+ "AccessToken: " + accessToken +"')</script>");
            out.println("</html>");

        }

    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
        autenticar();
        creacionObjetoJson();
        enviarDocumento();
        validacionEstado();
        desconexion();

        Gson gson = new Gson();
        String jsonString = gson.toJson(recepcion);
        System.out.println(jsonString);
    }

    public void autenticar() {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(IDP_URI + "/token");
        Form form = new Form();
        form.param("grant_type", "password")
                .param("username", usuario)
                .param("password", password)
                .param("client_id", IDP_CLIENT_ID);
        Response respuesta = target.request().post(Entity.form(form));

        //La respuesta debe ser un código 200. Debe considerarse el caso en que retorne un valor diferente
        // Indicador que se ha enviado un atributo incorrecto
        JsonObject responseJson = respuesta.readEntity(JsonObject.class);

        // De la respuesta procedemos a leer el access y refresh token
        accessToken = responseJson.getString("access_token");
        refreshToken = responseJson.getString("refresh_token");

        /*
         System.out.println(accessToken);
         System.out.println(" ");
         System.out.println(refreshToken);
         System.out.println(" ");
         System.out.println(respuesta);
         System.out.println(accessToken.length());
         */
    }

    public void creacionObjetoJson() {
        //Se creó un objeto recepcion global
        recepcion.setClave("506" + "010118" + "003101684401" + "0000000000000000010" + "1" + "999999999");
        //System.out.println(recepcion.getClave());
        recepcion.setFecha("2018-04-01T00:00:00-0600");

        IdentificacionEmisor emisor = new IdentificacionEmisor();
        emisor.setTipoIdentificacion("02");
        emisor.setNumeroIdentificacion("3101684401");

        recepcion.setIdentificacionEmisor(emisor);

        IdentificacionReceptor receptor = new IdentificacionReceptor();
        receptor.setTipoIdentificacion("02");
        receptor.setNumeroIdentificacion("3101684401");

        recepcion.setIdentificacionReceptor(receptor);
        
        /*
        Callbackurl callback = new Callbackurl();
        callback.setCallbackUrl("https://api.comprobanteselectronicos.go.cr/recepcion/v1/recepcion/" + recepcion.getClave() + "/");
        recepcion.setCallbackUrl(callback);
        System.out.println(callback.getCallbackUrl());
        */
        
        recepcion.setComprobanteXml("PD94bWwgdmVyc2lvbj0iMS4wIiA/Pg0KDQo8ZG9tYWluIHhtbG5zPSJ1cm46amJvc3M6ZG9tYWluOjQuMCI+DQogICAgPGV4dGVuc2lvbnM+DQogICAgICAgIDxleHRlbnNpb24gbW9kdWxlPSJvcmcuamJvc3MuYXMuY2x1c3RlcmluZy5pbmZpbmlzcGFuIi8+DQogICAgICAgIDxleHRlbnNpb24gbW9kdWxlPSJvcmcuamJvc3MuYXMuY2x1c3RlcmluZy5qZ3JvdXBzIi8+DQogICAgICAgIDxleHRlbnNpb24gbW9kdWxlPSJvcmcuamJvc3MuYXMuY29ubmVjdG9yIi8+DQogICAgICAgIDxleHRlbnNpb24gbW");

        /*
        System.out.println(recepcion.getClave());
        System.out.println(recepcion.getFecha());
        System.out.println(emisor.getTipoIdentificacion());
        System.out.println(emisor.getNumeroIdentificacion());
        System.out.println(receptor.getTipoIdentificacion());
        System.out.println(receptor.getNumeroIdentificacion());
        System.out.println(recepcion.getComprobanteXml());
         */
    }

    public void enviarDocumento() {
        try {
            Client client = ClientBuilder.newClient();
            WebTarget target = client.target(URI + "recepcion");
            Invocation.Builder solicitud = target.request();
            solicitud.header("Authorization", "Bearer " + accessToken);

            Gson gson = new Gson();
            String jsonString = gson.toJson(recepcion);
            Response post = solicitud.post(Entity.json(jsonString));

            //Response post = solicitud.post(Entity.json(recepcion));
            System.out.println(post.getStatus());

            switch (post.getStatus()) {
                case 202:
                    // Éste código de retorno se da por recibido a la plataforma el documento. Posteriormente
                    // debe validarse su estado de aceptación o rechazo. Es importante hacer notar que se
                    // regresa un header "Location" que corresponde a un URL. donde se puede validar el
                    // estado del documento, por ejemplo:
                    // https://api.comprobanteselectronicos.go.cr/recepcion-sandbox/v1/recepcion/50601011600310112345600100010100000000011999999999/
                    System.out.println("Factura recibida de forma Satisfactoria!");
                    break;
                case 400:
                    // Se da si se detecta un error en las validaciones, por ejemplo: si intento enviar más de una
                    // vez un documento. El encabezado "X-Error-Cause" indica la causa del problema.
                    String xErrorCause = post.getHeaderString("X-Error-Cause");
                    //LOG.log(Level.SEVERE, xErrorCause);
                    System.out.println(xErrorCause);
                    break;
            }
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public void validacionEstado() {
        Client client = ClientBuilder.newClient();
        // En éste caso, clave corresponde a la clave del documento a validar
        WebTarget target = client.target(URI + "recepcion/" + recepcion.getClave());
        Invocation.Builder request = target.request();

        // Se debe brindar un header "Authorization" con el valor del access token obtenido anteriormente.
        request.header("Authorization", "Bearer " + accessToken);

        // Se envía un GET. para tomar el estado
        Response res = request.get();

        System.out.println(res.getStatus());

        switch (res.getStatus()) {
            case 200:
                // Acá se debe procesar la respuesta para determinar si el atributo "ind-estado"
                // del JSON. de respuesta da por aceptado o rechazado el documento. Si no está
                // en ese estado se debe reintentar posteriormente.
                System.out.println(res.getStatusInfo());
                
                System.out.println(res.toString());
                
                /*
                JSONObject myResponse = new JSONObject(res.toString());
                System.out.println("result after Reading JSON Response");
                System.out.println(myResponse.getString("ind-estado"));
                */
                break;
            case 404:
                // Se presenta si no se localiza la clave brindada
                //LOG.log(Level.SEVERE, "La clave no esta registrada");
                System.out.println("La clave no esta registrada");
                break;

        }
    }

    public void desconexion() {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(IDP_URI + "/logout");

        // Los tokens son los obtenidos durante la fase de login inicial.
        Response response = target.request().header("Authorization", "Bearer " + accessToken).post(Entity.form(new Form("refresh_token", refreshToken)));
        //System.out.println(response.getStatusInfo());
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
