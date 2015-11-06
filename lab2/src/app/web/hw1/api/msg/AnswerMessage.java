package app.web.hw1.api.msg;

public class AnswerMessage extends Message {
	private RequestMessage mReceiverMessage;

	public AnswerMessage(RequestMessage receiverMessage) {
		mReceiverMessage = receiverMessage;
	}

	public RequestMessage getReceiverMessage() {
		return mReceiverMessage;
	}
}
