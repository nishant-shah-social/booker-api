package utils;

import config.Endpoints;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import pojo.LoginRequest;
import pojo.LoginResponse;

public class Auth {
    private static final Logger LOGGER = LogManager.getLogger(Auth.class);
    private final RestClient restClient;

    // Constructor to inject RestClient dependency
    public Auth(RestClient restClient) {
        this.restClient = restClient;
    }

    public String login(String username, String password) {
        long start = System.currentTimeMillis();
        LOGGER.info("POST /auth (login)");
        try {
            JSONObject loginRequest = new JSONObject();
            loginRequest.put("username", username);
            loginRequest.put("password", password);


            // Use RestClient to send the POST request
            LoginResponse loginResponse = restClient
                    .withJsonBody(loginRequest)
                    .post(Endpoints.LOGIN, 200)
                    .as(LoginResponse.class);

            long duration = System.currentTimeMillis() - start;
            LOGGER.info("POST /auth -> SUCCESS ({} ms)", duration);
            return loginResponse.getToken();

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - start;
            LOGGER.error("POST /auth failed ({} ms)", duration, e);
            throw e;
        }
    }

}

