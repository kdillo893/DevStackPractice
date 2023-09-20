package com.kdillo.simple.servlet;

import com.kdillo.simple.db.PostgresqlConnectionProvider;
import com.kdillo.simple.db.UserDBImpl;
import com.kdillo.simple.entities.User;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;

/**
 * @author kdill
 */
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

    @Override
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

            response.setContentType("text/json");

            //hello
            PrintWriter out = response.getWriter();
            out.print(user.toJson());
            
            return;
        }

        response.setContentType("text/html");

        //hello
        PrintWriter out = response.getWriter();
        out.println("<html><body>");
        out.println("<h1>" + "something" + "</h1>");
        out.println("</body></html>");
    }

    @Override
    public void destroy() {
        pgConProvider = null;
        props = null;
    }
}
