package app.web.hw1.api;

import app.web.hw1.api.listener.Listener;
import app.web.hw1.api.msg.AnswerMessage;
import app.web.hw1.api.msg.RequestMessage;

import java.util.*;

public class ApiPostOffice {
    private Set<Listener> listeners;

	private static ApiPostOffice apiPostOffice;

    private ApiPostOffice() {
	    listeners = Collections.synchronizedSet(new HashSet<>());
    }

    public static ApiPostOffice getInstance() {
        if (apiPostOffice == null) apiPostOffice = new ApiPostOffice();
        return apiPostOffice;
    }

    public boolean register(Listener listener) {
		return listeners.add(listener);
    }

    public boolean unregister(Listener listener) {
	    return listeners.remove(listener);
    }

    public void sendRequestWithAnswer(RequestMessage message, int time) {

    }

    public void sendRequest(RequestMessage message) {

    }

    public void sendAnswer(AnswerMessage message) {

    }



}
