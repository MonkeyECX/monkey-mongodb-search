package br.com.monkey.ecx.core.exception;

import br.com.monkey.ecx.core.exception.model.MessageType;

import java.util.List;

public class MonkeyRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private List<String> notifications;

	private MessageType messageType;

	private Integer errorCode;

	public MonkeyRuntimeException() {
	}

	public MonkeyRuntimeException(MessageType messageType, List<String> notifications, Integer errorCode) {
		super(new Throwable(notifications.toString()));
		this.messageType = messageType;
		this.notifications = notifications;
		this.errorCode = errorCode;
	}

	public MonkeyRuntimeException(MessageType messageType, List<String> notifications, Integer errorCode,
			boolean enableSuppression) {
		super(notifications.toString(), null, enableSuppression, false);
		this.messageType = messageType;
		this.notifications = notifications;
		this.errorCode = errorCode;
	}

	public MonkeyRuntimeException(MessageType messageType, List<String> notifications, Throwable cause,
			Integer errorCode) {
		super(cause);
		this.messageType = messageType;
		this.notifications = notifications;
		this.errorCode = errorCode;
	}

	public MessageType getMessageType() {
		return messageType;
	}

	public List<String> getNotifications() {
		return notifications;
	}

	public int getHttpErrorCode() {
		return errorCode;
	}

}
