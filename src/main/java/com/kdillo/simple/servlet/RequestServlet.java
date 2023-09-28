package com.kdillo.simple.servlet;

import com.kdillo.simple.db.PostgresqlConnectionProvider;
import com.kdillo.simple.db.UserDBImpl;
import com.kdillo.simple.entities.User;
import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;

import jakarta.json.JsonObjectBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;

/**
 * @author kdill
 */
//@WebService(name="/api/*")
public class RequestServlet extends HttpServlet {

    private static PostgresqlConnectionProvider pgConProvider;
    private static Properties props;

    @Override
    public void init() {
        try (InputStream propsStream = getServletContext().getResourceAsStream("/WEB-INF/classes/config.properties")) {
            props = new Properties();

            props.load(propsStream);
            pgConProvider = new PostgresqlConnectionProvider(props);

        } catch (IOException ex) {
//            LOGGER.debug("Unable to load app properties");
            ex.printStackTrace();
        }
    }

    private static String myAppUrl(Properties props) {

        if (props == null || props.isEmpty()) {
            return null;
        }

        boolean useSsl = props.getProperty("app.ssl").equalsIgnoreCase("true");
        String appUrl = useSsl ? "https://" : "http://";
        appUrl += props.getProperty("app.hostname");
        appUrl += props.getProperty("app.port") != null ? ":" + props.getProperty("app.port") : "";
        appUrl += "/";

        return appUrl;
    }
    
    private static List<String> parsePath(String pathInfo) {
        
        if (pathInfo == null)
            return null;
        
        List<String> orderedPathParts = new ArrayList<String>();
        try {
            for (var pathPart : pathInfo.split("/")) {
                orderedPathParts.add(URLDecoder.decode(pathPart, "UTF-8"));
            }
        } catch (UnsupportedEncodingException ex) {
            //couldn't parse, return null;
            return null;
        }
        
        // if the first item is blank (because of pathinfo starting with /), remove it.
        if (orderedPathParts.get(0).isEmpty())
            orderedPathParts.remove(0);
        
        return orderedPathParts;
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        //set allow cors from my own origin:
        resp.addHeader("AccessControl-Allow-Origin", myAppUrl(props));

        super.service(req, resp);
    }

    /**
     * Process get for business objects which are displayed by UI
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        //parse the URI
//        List<String> pathParts = parsePath(request.getPathInfo());
//        System.out.println(pathParts);
//        System.out.println(request.getRequestURI());
        
        //the path could contain many different parts...
        
        
        
        
        //my "get" method will need to parse by "query multiple" or "query by ID"        
        String parmId = request.getParameter("id");

        UUID id = null;
        try {
            if (parmId != null) {
                id = UUID.fromString(parmId);
            }
        } catch (IllegalArgumentException ilex) {

            response.setContentType("text/html");

            //hello
            PrintWriter out = response.getWriter();
            out.println("<html><body>");
            out.println("<h1>" + "Failed to parse id" + "</h1>");
            out.println("</body></html>");

            return;
        }

        //if ID, then "get by Id", else "query by parms"
        if (id != null) {
            getById(id, request, response);
        } else {
            getMultiple(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPut(req, resp); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doDelete(req, resp); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
    }

    @Override
    public void destroy() {
        pgConProvider = null;
        props = null;
    }

    private void getById(UUID id, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
        resp.setContentType("text/json");
        Writer out = resp.getWriter();

        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.isBlank()) {

            out.write("{}");
            return;
        }

        if (pathInfo.startsWith("/user")) {
            UserDBImpl userDbImpl = new UserDBImpl(pgConProvider);
            Optional<User> optUser = userDbImpl.getById(id);

            jsonBuilder.add("user", optUser.isPresent() ? optUser.get().toJson() : JsonObject.NULL);
        }

        out.write(jsonBuilder.build().toString());
    }

    private void getMultiple(HttpServletRequest request, HttpServletResponse response) throws IOException {

        JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
        response.setContentType("text/json");
        Writer out = response.getWriter();

        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.isBlank()) {

            out.write("{}");
            return;
        }

        if (pathInfo.startsWith("/users")) {
            //query by what? just last name for now

            User queryUser = new User();
            queryUser.setLastName(request.getParameter("last"));

            UserDBImpl userDbImpl = new UserDBImpl(pgConProvider);
            List<User> users = userDbImpl.getAll(queryUser);

            JsonArrayBuilder userJsonArray = Json.createArrayBuilder();
            for (User user : users) {
                userJsonArray.add(user.toJson());
            }

            jsonBuilder.add("users", userJsonArray);
        }

        out.write(jsonBuilder.build().toString());
    }
}
