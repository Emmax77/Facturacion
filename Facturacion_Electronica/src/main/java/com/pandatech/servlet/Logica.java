/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pandatech.servlet;

import com.google.gson.Gson;
import com.pandatech.bean.IdentificacionEmisor;
import com.pandatech.bean.IdentificacionReceptor;
import com.pandatech.bean.Recepcion;
import com.pandatech.bean.Validacion;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import javax.json.JsonObject;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import static java.lang.System.out;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.Scanner;
import javax.xml.bind.Element;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import jdk.internal.org.xml.sax.XMLReader;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

// Clases para la creacion y manejo de XML
/**
 *
 * @author Emmanuel GR
 */
@WebServlet(name = "Logica", urlPatterns = {"/Logica"})
@MultipartConfig(fileSizeThreshold = 6291456, // 6 MB
        maxFileSize = 10485760L, // 10 MB
        maxRequestSize = 20971520L // 20 MB
)

public class Logica extends HttpServlet {

    private static final String IDP_URI = "https://idp.comprobanteselectronicos.go.cr/auth/realms/rut-stag/protocol/openid-connect";
    private static final String IDP_CLIENT_ID = "api-stag";
    private static String usuario = "cpj-3-101-684401@stag.comprobanteselectronicos.go.cr";
    private static String password = "X=!:&OvjqB#C_)XO@#B]";

    private static final String UPLOAD_DIR = "uploads";
    private static final String URI = "https://api.comprobanteselectronicos.go.cr/recepcion-sandbox/v1/";

    private String accessToken;
    private String refreshToken;

    //private static final String JAR_DIR = " C:/Users/PCPTUser/Desktop/FirmaXadesEpes-master/compilado/firmar-xades.jar ";
    private static final String JAR_DIR = " C:/Users/Emmanuel Guzman/Desktop/Facturacion_Electronica/Facturacion_Electronica/target/classes/archivos ";
    private static final String LLAVE_DIR = " C:/Users/Emmanuel Guzman/Desktop/Facturacion_Electronica/Facturacion_Electronica/target/classes/archivos/recursos/llavecriptografica_310168440106.p12 ";
    private static final String LLAVE_CLAVE_DIR = " 8888 ";
    private static String XML = "";
    private static String XML_firmado = "";

    Recepcion recepcion = new Recepcion();
    String archivoxml = null;

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

        response.setContentType("text/html;charset=UTF-8");

        boolean isMultipart = ServletFileUpload.isMultipartContent(request);

        if (isMultipart) {
            FileItemFactory factory = new DiskFileItemFactory();
            ServletFileUpload upload = new ServletFileUpload(factory);

            try {
                List<FileItem> fields = upload.parseRequest(request);
                Iterator<FileItem> it = fields.iterator();

                while (it.hasNext()) {
                    FileItem fileItem = it.next();
                    XML += fileItem.getString();
                }

            } catch (FileUploadException ex) {
                Logger.getLogger(Logica.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println("XML " + XML);
        }

        //Realiza el proceso en tiempo de ejecucion java -jar sobre el compilado jar para realizar la firma del archivo xml
        //Se utiliza 4 parametros para ejecutar el jar de firmado
        //Process cat = Runtime.getRuntime().exec("java -jar C:\\Users\\Emmanuel Guzman\\Desktop\\Facturacion_Electronica\\Facturacion_Electronica\\target\\classes\\archivos\\firmar-xades.jar C:\\Users\\Emmanuel Guzman\\Desktop\\Facturacion_Electronica\\Facturacion_Electronica\\target\\classes\\archivos\\llavecriptografica_310168440106.p12 8888 C:\\Users\\Emmanuel Guzman\\Desktop\\Facturacion_Electronica\\Facturacion_Electronica\\target\\classes\\archivos\\recursos\\demo-factura.xml C:\\Users\\Emmanuel Guzman\\Desktop\\Facturacion_Electronica\\Facturacion_Electronica\\target\\classes\\archivos\\recursos\\factura_firmada.xml");

        //Process cat = Runtime.getRuntime().exec("java -jar" + JAR_DIR + LLAVE_DIR + LLAVE_CLAVE_DIR + XML + XML_firmado);  
        Process cat = Runtime.getRuntime().exec("java -jar " + JAR_DIR + LLAVE_DIR + LLAVE_CLAVE_DIR + XML + " C:/Users/Emmanuel Guzman/Desktop/Facturacion_Electronica/Facturacion_Electronica/target/classes/archivos/recursos/factura_firmada.xml");
        //Lee el xml firmado en la ruta indicada
        String content = readFile("C:\\Users\\Emmanuel Guzman\\Desktop\\Facturacion_Electronica\\Facturacion_Electronica\\target\\classes\\archivos\\recursos\\factura_firmada.xml", StandardCharsets.UTF_8);
        System.out.println(content);

        Conversion codificar = new Conversion();
        String xmlBase64 = codificar.encode(content);
        System.out.println(xmlBase64);
        /*
        autenticar();
        creacionObjetoJson();
        enviarDocumento();
        validacionEstado();
        desconexion();
*/
        Gson gson = new Gson();
        String jsonString = gson.toJson(recepcion);
        System.out.println(jsonString);

    }
    //Ejecuta proceso para leer archivo xml

    public static String readFile(String path, Charset encoding)
            throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
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
        recepcion.setClave("506" + "010118" + "003101684401" + "0000000000000000013" + "1" + "999999999");
        //System.out.println(recepcion.getClave());
        recepcion.setFecha();

        IdentificacionEmisor emisor = new IdentificacionEmisor();
        emisor.setTipoIdentificacion("02");
        emisor.setNumeroIdentificacion("3101684401");

        recepcion.setIdentificacionEmisor(emisor);

        //El identificador del receptor es un valor opcional
        IdentificacionReceptor receptor = new IdentificacionReceptor();
        receptor.setTipoIdentificacion("02");
        receptor.setNumeroIdentificacion("3101684401");
        recepcion.setIdentificacionReceptor(receptor);

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
                    System.out.println(post.getHeaderString("X-Error-Cause"));
                    /*
                    System.out.println(post.getHeaderString("X-Ratelimit-Limit"));
                    System.out.println(post.getHeaderString("X-Ratelimit-Remaining"));
                    System.out.println(post.getHeaderString("X-Ratelimit-Reset"));
                    LOG.log(Level.SEVERE, xErrorCause);
                     */
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
                /*
                System.out.println(res.getStatusInfo());
                System.out.println(res);
                 */
                String output = res.readEntity(String.class).replace("ind-estado", "ind_estado").replace("respuesta-xml", "respuesta_xml");

                //Genera el xml en consola de la respuesta de hacienda
                System.out.println(output);

                try {
                    final Gson gson = new Gson();
                    final Validacion json = gson.fromJson(output, Validacion.class);
                    /*
                    System.out.println(json.getClave());
                    System.out.println(json.getFecha());
                    System.out.println(json.getInd_estado());
                    System.out.println(json.getRespuestaXml());
                     */
                    Conversion decodificar = new Conversion();
                    archivoxml = decodificar.decode(json.getRespuestaXml());
                    //System.out.println(archivoxml);

                    //Creación y Respuesta si se crea o no el comprobante de recepción de hacienda
                    System.out.println(comprobanteXml());

                    //Ejecucion de metodo para enviar xml por correo
                    System.out.println(envioCorreo("emmanuel.guzman@pandatechla.com", "", "emmanuel.guzman@pandatechla.com"));

                } catch (Exception e) {
                    System.out.println(e);
                }

                break;
            case 404:
                // Se presenta si no se localiza la clave brindada
                //LOG.log(Level.SEVERE, "La clave no esta registrada");
                System.out.println("La clave no esta registrada");
                break;
            case 400:
                System.out.println(res.getHeaderString("X-Error-Cause"));
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

    public String comprobanteXml() {
        String respuesta = "";
        try {
            String ruta = "C://temp/PT-" + recepcion.getClave() + ".xml";
            File archivo = new File(ruta);
            BufferedWriter bw = new BufferedWriter(new FileWriter(archivo));
            bw.write(archivoxml);
            bw.close();
            respuesta = "Comprobante Xml creado en la siguiente ruta: " + ruta;
        } catch (Exception e) {
            respuesta = e.toString();
        }
        return respuesta;
    }

    public String envioCorreo(String correoEmisor, String password, String correodestinatario) {
        String respuesta = "";
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.setProperty("mail.smtp.starttls.enable", "true");
        props.setProperty("mail.smtp.port", "587");
        props.setProperty("mail.smtp.user", correoEmisor);
        props.setProperty("mail.smtp.auth", "true");

        Session session = Session.getDefaultInstance(props, null);
        session.setDebug(true);

        //Construccion del correo
        try {
            BodyPart texto = new MimeBodyPart();
            texto.setText("Prueba de envío de xml");//texto dentro del correo
            //colocacion de adjunto
            BodyPart adjunto = new MimeBodyPart();
            adjunto.setDataHandler(new DataHandler(new FileDataSource("C://temp/PT-" + recepcion.getClave() + ".xml")));
            adjunto.setFileName(recepcion.getClave() + ".xml");//Nombre del archivo para que cliente lo lea antes de abrirlo
            //Juntar texto y adjunto
            MimeMultipart multiParte = new MimeMultipart();
            multiParte.addBodyPart(texto);
            multiParte.addBodyPart(adjunto);

            //Creación del cuerpo del mensaje
            MimeMessage message = new MimeMessage(session);

            // Se rellena el From
            message.setFrom(new InternetAddress(correoEmisor));

            // Se rellenan los destinatarios    
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(correodestinatario));

            // Se rellena el subject o asunto
            message.setSubject("Prueba de correo para xml");

            // Se mete el texto y la foto adjunta.
            message.setContent(multiParte);

            //Enviar correo
            Transport t = session.getTransport("smtp");
            t.connect(correoEmisor, password);
            t.sendMessage(message, message.getAllRecipients());
            t.close();
            respuesta = "Correo enviado";
        } catch (Exception e) {
            respuesta = e.toString();
        }
        return respuesta;
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
