package br.com.monkey.ecx.core.exception;

import br.com.monkey.ecx.core.exception.model.MessageType;
import org.springframework.http.HttpStatus;

import java.util.List;

import static java.util.Collections.singletonList;

public class BadRequestException extends MonkeyRuntimeException {

	public BadRequestException(String notification) {
		super(MessageType.Bad_Request_Error, singletonList(notification), HttpStatus.BAD_REQUEST.value());
	}

	public BadRequestException(List<String> notifications) {
		super(MessageType.Bad_Request_Error, notifications, HttpStatus.BAD_REQUEST.value());
	}

	public BadRequestException(List<String> notifications, Throwable cause) {
		super(MessageType.Bad_Request_Error, notifications, cause, HttpStatus.BAD_REQUEST.value());
	}

}
