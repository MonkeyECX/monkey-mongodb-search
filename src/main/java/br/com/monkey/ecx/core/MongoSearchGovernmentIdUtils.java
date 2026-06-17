package br.com.monkey.ecx.core;

import br.com.caelum.stella.format.CNPJFormatter;
import br.com.caelum.stella.format.CPFFormatter;
import br.com.caelum.stella.validation.CNPJValidator;
import br.com.caelum.stella.validation.CPFValidator;
import br.com.caelum.stella.validation.InvalidStateException;

public class MongoSearchGovernmentIdUtils {

	public static final String GOV_ID_REGEX_CPF_BR = "\\d{11}";

	public static final String GOV_ID_REGEX_CNPJ_BR = "\\d{14}";

	public static final String GOV_ID_REGEX_RUT_CL = "\\d{7,8}[Kk\\d]";

	private static final CPFValidator cpfValidator = new CPFValidator();

	private static final CNPJValidator cnpjValidator = new CNPJValidator();

	private static final CPFFormatter cpfFormatter = new CPFFormatter();

	private static final CNPJFormatter cnpjFormatter = new CNPJFormatter();

	public static String format(String governmentId) {
		return isValidCPF(governmentId) ? cpfFormatter.format(governmentId) : formatLegal(governmentId);
	}

	public static String unformat(String governmentId) {
		return isValidCPF(governmentId) ? cpfFormatter.unformat(governmentId) : cnpjFormatter.unformat(governmentId);
	}

	public static boolean isValidGovernmentId(String governmentId) {
		return isValidCPF(governmentId) || isValidCNPJ(governmentId);
	}

	private static String formatLegal(String governmentId) {
		return isValidCNPJ(governmentId) ? cnpjFormatter.format(governmentId)
				: (MongoSearchRUTUtils.isValidRUT(governmentId) ? MongoSearchRUTUtils.format(governmentId)
						: governmentId);
	}

	public static boolean isValidCPF(String governmentId) {
		boolean isValidCPF = true;
		try {
			cpfValidator.assertValid(governmentId);
		}
		catch (InvalidStateException e) {
			isValidCPF = false;
		}

		return isValidCPF;
	}

	public static boolean isValidCNPJ(String governmentId) {
		boolean isValidCNPJ = true;
		try {
			cnpjValidator.assertValid(governmentId);
		}
		catch (InvalidStateException e) {
			isValidCNPJ = false;
		}

		return isValidCNPJ;
	}

	public static boolean isValidLegalGovernmentId(String governmentId) {
		return isValidCNPJ(governmentId) || MongoSearchRUTUtils.isValidRUT(governmentId);
	}

}
