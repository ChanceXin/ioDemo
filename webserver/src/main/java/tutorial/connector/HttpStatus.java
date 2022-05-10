package tutorial.connector;

public enum HttpStatus {
    SC_OK(200,"OK"),
    SC_NOT_FOUND(404,"File not found");

    public int getStatusCode() {
        return statusCode;
    }

    public String getReason() {
        return reason;
    }

    private int statusCode;
    private String reason;

    HttpStatus(int code,String reason){
        this.statusCode = code;
        this.reason = reason;
    }


}
