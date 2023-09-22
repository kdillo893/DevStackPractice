package com.kdillo.simple.servlet;

import com.kdillo.simple.db.PostgresqlConnectionProvider;
import com.kdillo.simple.db.UserDBImpl;
import com.kdillo.simple.entities.User;
import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;

import jakarta.json.JsonObjectBuilder;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.util.Enumeration;
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

    /**
     * Process get for business objects which are displayed by UI
     * @param request
     * @param response
     * @throws IOException 
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        
        //my "get" method will need to parse by "query multiple" or "query by ID"        
        
        String parmId = request.getParameter("id");

//        if (parmId == null) {
//            response.setContentType("text/html");
//
//            //hello
//            PrintWriter out = response.getWriter();
//            out.println("<html><body>");
//            out.println("<h1>" + "No ID Parm" + "</h1>");
//            out.println("</body></html>");
//
//            return;
//        }

        UUID id = null;
        try {
            if (parmId != null)
                id = UUID.fromString(parmId);
        } catch (IllegalArgumentException ilex) {
            ilex.printStackTrace();

            response.setContentType("text/html");

            //hello
            PrintWriter out = response.getWriter();
            out.println("<html><body>");
            out.println("<h1>" + "Failed to parse uid" + "</h1>");
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
    public void destroy() {
        pgConProvider = null;
        props = null;
    }
    
    private void getById(UUID id, HttpServletRequest req, HttpServletResponse resp) throws IOException {

        String pathInfo = req.getPathInfo();
        
        JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
        if (pathInfo.startsWith("/user")) {
            UserDBImpl userDbImpl = new UserDBImpl(pgConProvider);
            Optional<User> optUser = userDbImpl.getById(id);
            
            jsonBuilder.add("user", optUser.isPresent() ? optUser.get().toJson() : JsonObject.NULL);
        }
        
        resp.setContentType("text/json");
        
        Writer out = resp.getWriter();
        out.write(jsonBuilder.build().toString());
        
    }

    private void getMultiple(HttpServletRequest request, HttpServletResponse response) throws IOException {
        
        String pathInfo = request.getPathInfo();
        
        JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
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
        
        response.setContentType("text/json");
        
        Writer out = response.getWriter();
        out.write(jsonBuilder.build().toString());
    }
}
