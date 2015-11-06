package app.web.hw1.api;

import app.web.hw1.api.listener.Listener;
import app.web.hw1.api.msg.AnswerMessage;
import app.web.hw1.api.msg.Message;
import app.web.hw1.api.msg.RequestMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.zip.DataFormatException;

public class ApiPostOffice {
	private final Thread mThreadReceiver;
	private final Thread mThreadHandler;
	private int mPort;
	private String mIpAddress;
	private Set<Listener> mListeners;
	private LinkedBlockingQueue<RequestMessage> mReqMessages;
	private static ApiPostOffice mApiPostOffice;
	private static Map<RequestMessage, List<AnswerMessage>> mRequestMessageListHashMap;


	private ApiPostOffice(String ipAddress, int port) {
		mIpAddress = ipAddress;
		mPort = port;
		mListeners = Collections.synchronizedSet(new HashSet<>());
		mReqMessages = new LinkedBlockingQueue<>();
		mRequestMessageListHashMap = Collections.synchronizedMap(new HashMap<>());

		mThreadReceiver = new Thread() {
			@Override
			public void run() {
				try (
						Socket socketConnection = new Socket(mIpAddress, mPort);
						ObjectInputStream stream = new
								ObjectInputStream(socketConnection.getInputStream())) {
					while (!isInterrupted()) {
						Message msg = (Message) stream.readObject();
						if (msg instanceof AnswerMessage) {
							AnswerMessage message = (AnswerMessage) msg;
							synchronized (mRequestMessageListHashMap) {
								if (mRequestMessageListHashMap.containsKey(message.getReceiverMessage())) {
									List<AnswerMessage> list
											= mRequestMessageListHashMap.get(message.getReceiverMessage());
									list.add(message);
									mRequestMessageListHashMap.replace(
											message.getReceiverMessage(), list
									);
								}
							}
						} else if (msg instanceof RequestMessage) {
							mReqMessages.add((RequestMessage) msg);
						} else {
							throw new DataFormatException();
						}
					}
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (DataFormatException e) {
					e.printStackTrace();
				}
			}
		};
		mThreadReceiver.start();

		mThreadHandler = new Thread() {
			@Override
			public void run() {
				while (!isInterrupted()) {
					try {
						RequestMessage message = mReqMessages.take();
						for (Listener listener : mListeners) {
							listener.onReceiverRequestMessage(message);
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		mThreadHandler.start();
	}

	public static ApiPostOffice getInstance() {
		if (mApiPostOffice == null) throw new NullPointerException();
		return mApiPostOffice;
	}

	public static ApiPostOffice getInstance(String ipAddress, int port) {
		if (mApiPostOffice != null) {
			if (!mApiPostOffice.mIpAddress.equals(ipAddress) || mApiPostOffice.mPort != port) {
				mApiPostOffice.realize();
				mApiPostOffice = new ApiPostOffice(ipAddress, port);
			}
		} else
			mApiPostOffice = new ApiPostOffice(ipAddress, port);
		return mApiPostOffice;
	}

	public void realize() {
		mThreadHandler.interrupt();
		mThreadReceiver.interrupt();
	}

	public boolean register(Listener listener) {
		return mListeners.add(listener);
	}

	public boolean unregister(Listener listener) {
		return mListeners.remove(listener);
	}

	public void sendRequestWithAnswer(Listener listener, RequestMessage message, int time) {
		Thread thread = new Thread() {
			@Override
			public void run() {
				try {
					if (!mRequestMessageListHashMap.containsKey(message))
						mRequestMessageListHashMap.put(message, new ArrayList<>());
					sleep(time);
					listener.onReceiverAnswersMessages(mRequestMessageListHashMap.get(message));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
		thread.start();
	}


	public void sendMessage(Message message) {
		try (
				Socket socketConnection = new Socket(mIpAddress, mPort);
				ObjectOutputStream stream =
						new ObjectOutputStream(socketConnection.getOutputStream())) {
			stream.writeObject(message);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
