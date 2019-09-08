package ruon.rssapi;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

import ruon.rssapi.common.Util;
import ruon.rssapi.http.Http;
import ruon.rssapi.http.ResponseListener;

import static ruon.rssapi.common.Util.eq;

public class API {

    public abstract static class AuthenticateListener implements ResponseListener {
        @Override
        public void result(String result) {

            try {
                Element element = Util.parse(result);
                if (eq("error", element.getTagName())) {
                    error(new Exception(element.getTextContent()));
                } else if (eq("guid", element.getTagName())) {
                    authenticated(element.getTextContent());
                } else {
                    throw new Exception("Unexpected result: "+result);
                }
            } catch (Exception e) {
                error(e);
            }
        }
        protected abstract void authenticated(String guid);
    }

    public static abstract class FetchAlarmsListener implements ResponseListener {
        @Override
        public void result(String result) {
            try {
                Element element = Util.parse(result);
                Element channel = (Element) element.getElementsByTagName("channel").item(0);
                NodeList items = channel.getElementsByTagName("item");
                List<Alarm> alarms = new ArrayList<Alarm>();
                for (int i=0;i<items.getLength();++i) {
                    alarms.add(new Alarm((Element) items.item(i)));
                }
                alarms(alarms);
            } catch (Exception e) {
                error(e);
            }
        }
        protected abstract void alarms(List<Alarm> alarms);
    }

    public static void authenticate(String email, String password, AuthenticateListener authenticateListener) {
        Http.get(authenticateListener, "https://www.r-u-on.com/resolveGUID?email=%s&password=%s", email, password);
    }

    public static void alarms(String guid, boolean filtered, FetchAlarmsListener listener) {

        String uri = "https://rss.r-u-on.com/rssalarms?id=%s";
        if (filtered) {
            uri+="&filtered";
        }
        Http.get(listener, uri, guid);
    }




}
