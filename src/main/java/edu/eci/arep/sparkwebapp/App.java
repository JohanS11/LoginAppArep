package edu.eci.arep.sparkwebapp;

import com.google.gson.Gson;
import edu.eci.arep.sparkwebapp.Services.URLReader;
import edu.eci.arep.sparkwebapp.model.User;
import spark.Request;
import spark.Response;
import spark.staticfiles.StaticFilesConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static edu.eci.arep.sparkwebapp.Services.cipherServices.*;
import static spark.Spark.*;

/**
 * This is a simple WebApplication using SparkWeb.
 * @author Johan Arias
 */
public class App 
{
    public static void main( String[] args ) {

        port(getPort());

        secure("keystores/ecikeystore.p12", "arep123", null, null);
        Map<String, String> users = new HashMap<>();
        users.put("test@mail.com", convertPass("123"));
        URLReader.load();
        Gson gson = new Gson();

        before("protected/*", (req, response) -> {
            req.session(true);
            if (req.session().isNew()) {
                req.session().attribute("isLogged", false);
            }
            boolean isLogged = req.session().attribute("isLogged");
            if (!isLogged) {
                halt(401, "<h1> 401 NOT AUTHORIZED </h1>");
            }
        });


        before("/login.html", ((req, response) -> {
            req.session(true);
            if (req.session().isNew()) {
                req.session().attribute("isLogged", false);
            }
            boolean isLogged = req.session().attribute("isLogged");
            if (isLogged) {
                response.redirect("protected/index.html");
            }
        }));


        StaticFilesConfiguration staticHandler = new StaticFilesConfiguration();
        staticHandler.configure("/static");

        before((request, response) ->
                staticHandler.consume(request.raw(), response.raw()));

        get("/",((request, response) -> {
            response.redirect("login.html");
            return "";
        }));

        get("/areyouhere",((request, response) -> {
            return "HOLAAA";
        }));

        get("/logout",((request, response) -> {
            request.session().attribute("isLogged",false);
            return "";
        }));

        post("/login", (request, response) -> {
            request.body();
            request.session(true);
            User user = gson.fromJson(request.body(), User.class);
            if (convertPass(user.getPassword()).equals(users.get(user.getUsername()))) {
                request.session().attribute("isLogged", true);
            } else {
                return "Invalid Username or password ";
            }
            return "";
        });

        get("/protected/service",(request, response) -> URLReader.readURL("https://ec2-54-242-161-34.compute-1.amazonaws.com:9002/hello"));

    }

    /**
     * @param req This is the object that represents the HTTP request
     *            in order to retrieve a resource from the web server.
     * @param res This is the object that represents the HTTP response
     *            given by the webserver
     * @return A string with html code that will build the web page, in this case
     *          the resource located at /inputdata
     */
    private static String inputDataPage(Request req, Response res) {
        ArrayList<User> users = new ArrayList<>();
        res.header("Content-Type","application/json");
        return new Gson().toJson(users);
    }

    static int getPort() {
        if (System.getenv("PORT") != null) {
            return Integer.parseInt(System.getenv("PORT"));
        }
        return 5001; //returns default port if heroku-port isn't set (i.e. on localhost)
    }

}
