package me.nathan3882.androidttrainparse;

public class Response {

    private final int responseCode;
    private String webService;
    private String action;

    private Object jsonResult;

    public Response(String webService, String action, int responseCode) {
        this.webService = webService;
        this.action = action;
        this.responseCode = responseCode;
        if (getResponseCode() == 404) {
            System.out.println("There was a 404, A parameter doesnt exist in database");
        }
    }

    public String getAction() {
        return action;
    }

    public String getWebService() {
        return webService;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public Object getJsonResult() {
        return jsonResult;
    }

    public void setJsonResult(Object jsonResult) {
        this.jsonResult = jsonResult;
    }
}
