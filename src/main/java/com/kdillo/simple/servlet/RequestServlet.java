package com.kdillo.simple.servlet;

import com.kdillo.simple.db.PostgresqlConnectionProvider;
import com.kdillo.simple.db.UserDBImpl;
import com.kdillo.simple.entities.User;
import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;

/**
 *
 * @author kdill
 */
//@WebServlet(value="/api")
public class RequestServlet extends HttpServlet {
    private static PostgresqlConnectionProvider pgConProvider;
    private static Properties props;
    
    public void init() {
        try (InputStream propsStream = new FileInputStream("src/main/resources/config.properties")) {
            props = new Properties();

            props.load(propsStream);
        } catch (IOException ex) {
//            LOGGER.debug("Unable to load app properties");
            ex.printStackTrace();
        }
        pgConProvider = new PostgresqlConnectionProvider(props);
    }
    
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        
        String parmUid = request.getParameter("uid");
        
        if (parmUid == null) {
            response.setContentType("text/html");
        
            //hello
            PrintWriter out = response.getWriter();
            out.println("<html><body>");
            out.println("<h1>" + "No UID Parm" + "</h1>");
            out.println("</body></html>");
            
            return;
        }
        
        UUID uid = null; 
        try {
            uid = UUID.fromString(parmUid);
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
        
        UserDBImpl userDbImpl = new UserDBImpl(pgConProvider);
        
        Optional<User> optUser = userDbImpl.getById(uid);
        
        if (optUser.isPresent()) {
            User user = optUser.get();
            
            response.setContentType("text/html");
        
            //hello
            PrintWriter out = response.getWriter();
            out.println("<html><body>");
            out.println("<h1>" + user.getFirstName() + " " + user.getLastName() + "</h1>");
            out.println("</body></html>");
        }
        
        response.setContentType("text/html");
        
        //hello
        PrintWriter out = response.getWriter();
        out.println("<html><body>");
        out.println("<h1>" + "something" + "</h1>");
        out.println("</body></html>");
    }
    
    public void destroy() {
        pgConProvider = null;
        props = null;
    }
}
