package br.com.monkey.ecx.core.exception.model;

public enum MessageType {

	Invalid_Token("Invalid Token"),

	Mult_Status_Error("The request is finished with errors."),

	No_Content_Error("The identifier that already exists."),

	Conflict_Error("The identifier that already exists."),

	Parameter_Error("A require param was missing, or malformed."),

	Bad_Request_Error("Request invalid or malformed."),

	Business_Logic_Error("Business logic error."),

	Resource_Not_Found("Resource not found."),

	Internal_Architecture_Error("Ooops! some big problem found."),

	List_Not_Found("List not found."),

	Method_Not_Allowed("Method Not Allowed"),

	Unsupported_Media_Type("The request entity is in a format not supported."),

	Access_Denied("Access denied."),

	Integration_Error("Error on services contract.");

	private final String description;

	MessageType(final String des) {
		description = des;
	}

	public String getDescription() {
		return description;
	}

}
