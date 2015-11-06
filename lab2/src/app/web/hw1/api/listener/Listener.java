package app.web.hw1.api.listener;

import app.web.hw1.api.msg.AnswerMessage;
import app.web.hw1.api.msg.Report;
import app.web.hw1.api.msg.RequestMessage;

/**
 * Created by Alex on 05.11.2015.
 */
public interface Listener {
	void onReceiverRequestMessage(RequestMessage message);
	void onReceiverAnswersMessages(AnswerMessage[] answerMessages);
}
