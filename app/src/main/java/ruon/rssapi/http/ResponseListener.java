package ruon.rssapi.http;

public interface ResponseListener {

    void error(Exception e);
    void result(String result);

}
