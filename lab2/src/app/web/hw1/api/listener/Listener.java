package app.web.hw1.api.listener;

import app.web.hw1.api.msg.AnswerMessage;
import app.web.hw1.api.msg.RequestMessage;

import java.util.List;

public interface Listener {
	void onReceiverRequestMessage(RequestMessage message);

	void onReceiverAnswersMessages(List<AnswerMessage> answerMessages);
}
