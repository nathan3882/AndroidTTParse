package me.nathan3882.androidttrainparse;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.nathan3882.requests.UserdataRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class Request {

    private final Client client;
    private String action;

    private List<String> parameters;

    public Request(Client client, String action, List<String> parameters) {
        this.action = action;
        this.parameters = parameters;
        this.client = client;
    }

    public Response getResponse() {
        String webService = client.getWebService();
        Response response = null;
        try {
            String paramRoute = parametersToRoute(getParameters());
            URL url = new URL(webService + getAction() + "/" + paramRoute); //my localhost

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            response = new Response(webService, getAction(), conn.getResponseCode());
            if (response.getResponseCode() == 404) return response;
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String responseString = "";
            while (true) {
                String rl = in.readLine();
                if (rl == null) break;
                responseString += rl;
            }
            //create ObjectMapper instance
            ObjectMapper objectMapper = new ObjectMapper();

            //convert json string to object
            UserdataRequest emp = objectMapper.readValue(responseString, UserdataRequest.class);
            System.out.println("salty too " + emp.getSalt());
            System.out.println("sdaalty too " + emp.getPassword());

            response.setJsonResult(emp);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    private String parametersToRoute(List<String> params) {
        String route = "";
        for (String param : params) {
            route += param + "/";
        }
        return route;
    }

    public String getAction() {
        return action;
    }

    public List<String> getParameters() {
        return parameters;
    }
}
