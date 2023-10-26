package com.kdillo.simple.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URLDecoder;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kdillo.simple.db.PostgresqlConnectionProvider;
import com.kdillo.simple.db.UserDBImpl;
import com.kdillo.simple.entities.User;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonValue;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author kdill
 */
@WebServlet(name = "RequestServlet", urlPatterns = "/api/*")
public class RequestServlet extends HttpServlet {
    private static final Logger LOGGER  = LogManager.getLogger();

    private PostgresqlConnectionProvider pgConProvider;
    private Properties props;

    @Override
    public void destroy() {
        pgConProvider = null;
        props = null;
    }

    @Override
    public void init() {
        try (InputStream propsStream = getServletContext().getResourceAsStream("/WEB-INF/classes/config.properties")) {
            props = new Properties();

            props.load(propsStream);
            pgConProvider = new PostgresqlConnectionProvider(props);

        } catch (IOException ex) {
            //LOGGER.debug("Unable to load app properties");
            ex.printStackTrace();
        }
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        LOGGER.info("Reached requestServlet: {}, parms={}", req.getPathInfo(), req.getParameterMap().toString());
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
        List<String> pathParts = parsePath(request.getPathInfo());
        String resourceType = parseResourceType(pathParts);

        //if pathParts is empty (just reach here with /api), give a "basic info" thing
        if (resourceType == null) {
            returnBasicInfo(request, response);

            return;
        }

        UUID id = parseIdFromRequest(pathParts);
        //        System.out.printf("resourceType=%s, id=%s\n", resourceType, id);

        //if ID, then "get by Id", else "query by parms"
        if (id != null) {
            getById(id, resourceType, request, response);
        } else {
            getMultiple(resourceType, request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //parse the URI
        List<String> pathParts = parsePath(request.getPathInfo());
        String resourceType = parseResourceType(pathParts);

        //if pathParts is empty (just reach here with /api), give a "basic info" thing
        if (resourceType == null) {
            returnBasicInfo(request, response);

            return;
        }

        //check the data type of the request body; use that for parsing.
        String contentTypeString = request.getHeader("Content-Type");
        LOGGER.info("content-type for POST: {}", contentTypeString);

        JsonObject jsonObject = null;
        if (true || contentTypeString.contains("text/json")) {
            jsonObject = Json.createReader(request.getReader()).readObject(); 
        }

        //we have a resource type, try creating the resource type depending on the value:
        if (resourceType.equals("users")) {
            User userToCreate = new User();
            userToCreate.first_name = jsonObject.getString("first_name");
            userToCreate.last_name = jsonObject.getString("last_name");
            userToCreate.email = jsonObject.getString("email");


            if (jsonObject.get("password") != null) {
                userToCreate.setPassword(jsonObject.getString("password"));
                userToCreate.calculatePassHashWithNewSalt();
            }

            UserDBImpl userDBImpl = new UserDBImpl(pgConProvider);
            UUID theUserId = userDBImpl.add(userToCreate);

            if (theUserId != null)
                response.getWriter().print(userDBImpl.getById(theUserId).get().toJson());
            else 
                response.getWriter().print(JsonObject.EMPTY_JSON_OBJECT);
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //parse the URI
        List<String> pathParts = parsePath(request.getPathInfo());
        String resourceType = parseResourceType(pathParts);

        //if pathParts is empty (just reach here with /api), give a "basic info" thing
        if (resourceType == null) {
            returnBasicInfo(request, response);

            return;
        }

        UUID theId = parseIdFromRequest(pathParts);

        //check the data type of the request body; use that for parsing.
        String contentTypeString = request.getHeader("Content-Type");
        LOGGER.info("content-type for PUT: {}", contentTypeString);

        JsonObject jsonObject = null;
        if (true || contentTypeString.contains("text/json")) {
            jsonObject = Json.createReader(request.getReader()).readObject(); 
        }

        //we have a resource type, try creating the resource type depending on the value:
        if (resourceType.equals("users")) {
            UserDBImpl userDBImpl = new UserDBImpl(pgConProvider);

            User userToUpdate = new User();
            userToUpdate.uid = theId;
            userToUpdate.first_name = jsonObject.getString("first_name");
            userToUpdate.last_name = jsonObject.getString("last_name");
            userToUpdate.email = jsonObject.getString("email");


            if (jsonObject.get("password") != null) {
                userToUpdate.setPassword(jsonObject.getString("password"));
                userToUpdate.calculatePassHashWithNewSalt();
            }

            boolean wasUpdated = false;;
            try {
                wasUpdated = userDBImpl.update(userToUpdate);
            } catch (Exception ex) {
                //No user to update. exit with 400
                JsonObjectBuilder jObjectBuilder = Json.createObjectBuilder();
                JsonObject responseJson = jObjectBuilder.add("error", buildJsonErrorArray("No such user for ID " + theId, "rest")).build();
                response.getWriter().print(responseJson.toString());
                return;
            }


            if (wasUpdated)
                response.getWriter().print(userDBImpl.getById(theId).get().toJson());
            else 
                response.getWriter().print(JsonObject.EMPTY_JSON_OBJECT);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //parse the URI
        List<String> pathParts = parsePath(req.getPathInfo());
        String resourceType = parseResourceType(pathParts);

        //if pathParts is empty (just reach here with /api), no deleting done.
        if (resourceType == null) {
            resp.setStatus(400);
            resp.setContentType("text/json");
            Writer out = resp.getWriter();

            //have an object describing the "bad request" error
            JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
            JsonArray errorJsonArray = buildJsonErrorArray("No deleting for this endpoint", "rest");
            jsonObjectBuilder.add("error", errorJsonArray);

            out.write(jsonObjectBuilder.build().toString());

            return;
        }

        UUID id = parseIdFromRequest(pathParts);

        //if ID, then "get by Id", else "query by parms"
        if (id != null) {
            deleteById(id, resourceType, req, resp);
        } else {
            // no ID, can't delete the resource (don't allow multiple deletions)
            resp.setStatus(400);
            resp.setContentType("text/json");
            Writer out = resp.getWriter();

            //have an object describing the "bad request" error
            JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
            JsonArray errorJsonArray = buildJsonErrorArray("Id not specified for resource to delete", "rest");
            jsonObjectBuilder.add("error", errorJsonArray);

            out.write(jsonObjectBuilder.build().toString());
        }
    }

    private void getById(UUID id, String resourceType, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
        resp.setContentType("text/json");
        Writer out = resp.getWriter();

        if (resourceType.equals("users")) {
            UserDBImpl userDbImpl = new UserDBImpl(pgConProvider);
            Optional<User> optUser = userDbImpl.getById(id);

            jsonBuilder.add("user", optUser.isPresent() ? optUser.get().toJson() : JsonObject.NULL);
        }

        out.write(jsonBuilder.build().toString());
    }

    private void getMultiple(String resourceType, HttpServletRequest request, HttpServletResponse response) throws IOException {

        JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
        response.setContentType("text/json");
        Writer out = response.getWriter();

        if (resourceType.equals("users")) {
            //query by what? just last name for now

            User queryUser = new User();
            Date before = null;
            Date after = null;

            //check the parameters, put things on user that match expectations
            Iterator<String> parameterNames = request.getParameterNames().asIterator();
            while (parameterNames.hasNext()) {
                String parmEntry = parameterNames.next();
                //do I need to URL Decode?
                parmEntry = URLDecoder.decode(parmEntry, "UTF-8");

                if (parmEntry.equals("last_name")) {
                    queryUser.last_name = request.getParameter("last_name");
                }
                if (parmEntry.equals("first_name")) {
                    queryUser.first_name = request.getParameter("first_name");
                }
                if (parmEntry.equals("email")) {
                    queryUser.email = request.getParameter("email");
                }
                if (parmEntry.equals("before")) {
                    try {
                        before = Date.valueOf(request.getParameter(parmEntry));
                    } catch (Exception ex) {
                        LOGGER.error("Unable to parse date", request.getParameter("before"));
                    }
                }
                if (parmEntry.equals("after")) {
                    try {
                        after = Date.valueOf(request.getParameter(parmEntry));
                    } catch (Exception ex) {
                        LOGGER.error("Unable to parse date", request.getParameter("after"));
                    }
                }
            }

            // need a "before/after" to get users created before or after a certain date range...
            UserDBImpl userDbImpl = new UserDBImpl(pgConProvider);
            List<User> users = null; 
            if (before != null || after != null) {
                users = userDbImpl.getAll(queryUser, before, after);
            } else {
                users = userDbImpl.getAll(queryUser);
            }

            JsonArrayBuilder userJsonArray = Json.createArrayBuilder();
            for (User user : users) {
                userJsonArray.add(user.toJson());
            }

            JsonObjectBuilder usersObject = Json.createObjectBuilder();
            usersObject.add("users", userJsonArray);

            jsonBuilder.add("success", buildJsonSuccessArray("got users", "rest", usersObject.build()));

        }

        out.write(jsonBuilder.build().toString());
    }

    private void deleteById(UUID id, String resourceType, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
        resp.setContentType("text/json");
        Writer out = resp.getWriter();

        if (resourceType.equals("users")) {
            UserDBImpl userDbImpl = new UserDBImpl(pgConProvider);
            boolean successfulDelete = userDbImpl.deleteById(id);

            String message = successfulDelete ? "deleted user with id " + id.toString() : "No user deleted";
            jsonBuilder.add("success", buildJsonSuccessArray(message, "rest", null));
        }

        out.write(jsonBuilder.build().toString());
    }

    private String myAppUrl(Properties props) {

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

    private UUID parseIdFromRequest(List<String> pathParts) {

        if (pathParts == null) {
            return null;
        }

        if (pathParts.isEmpty() || pathParts.size() == 1) {
            return null;
        }

        //try to parse, if can't return null;
        try {
            String lastPart = pathParts.get(pathParts.size() - 1);
            UUID id = UUID.fromString(lastPart);

            return id;
        } catch (IllegalArgumentException ilex) {

        }

        return null;
    }

    private List<String> parsePath(String pathInfo) {

        if (pathInfo == null) {
            return null;
        }

        List<String> orderedPathParts = new ArrayList<>();
        try {
            for (var pathPart : pathInfo.split("/")) {
                orderedPathParts.add(URLDecoder.decode(pathPart, "UTF-8"));
            }
        } catch (UnsupportedEncodingException ex) {
            //couldn't parse, return null;
            return null;
        }

        // if the first item is blank (because of pathinfo starting with /), remove it.
        if (orderedPathParts.get(0).isEmpty()) {
            orderedPathParts.remove(0);
        }

        return orderedPathParts;
    }

    private String parseResourceType(List<String> pathParts) {
        if (pathParts == null || pathParts.isEmpty()) {
            return null;
        }

        //break out the beginning bit, the ending bit with the weird symbols needs to get deleted...
        String resourceType = "";

        String pathPart = pathParts.get(0);
        resourceType += pathPart;

        if (pathParts.size() == 1) {
            return resourceType;
        }

        for (int i = 1; i < pathParts.size() - 1; i++) {
            pathPart = pathParts.get(i);
            resourceType += "/" + pathPart;
        }

        return resourceType;
    }

    private void returnBasicInfo(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        JsonObjectBuilder jsonObject = Json.createObjectBuilder();

        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        jsonArrayBuilder.add("/users").add("/users/{id}");

        jsonObject.add("url", req.getRequestURL().toString());
        jsonObject.add("baseUri", req.getRequestURI());
        jsonObject.add("endpoints", jsonArrayBuilder);

        resp.setContentType("text/json");
        resp.getWriter().write(jsonObject.build().toString());
    }
    /**
     * Build a json array object for response given message and type of message
     * TODO might want to make it so there can be multiple errors later.
     */
    private JsonArray buildJsonErrorArray(String message, String type) {
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        jsonObjectBuilder.add("type", type);
        jsonObjectBuilder.add("message", message);

        return Json.createArrayBuilder().add(jsonObjectBuilder.build()).build();
    }

    /**
     * Build a json array object for response given message and type of message
     * TODO might want to make it so there can be multiple errors later.
     */
    private JsonArray buildJsonSuccessArray(String message, String type, JsonValue jsonDataToAdd) {
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        jsonObjectBuilder.add("type", type);
        jsonObjectBuilder.add("message", message);


        if (jsonDataToAdd != null) {
            jsonObjectBuilder.add("data", jsonDataToAdd);
        }

        return Json.createArrayBuilder().add(jsonObjectBuilder.build()).build();
    }



}
