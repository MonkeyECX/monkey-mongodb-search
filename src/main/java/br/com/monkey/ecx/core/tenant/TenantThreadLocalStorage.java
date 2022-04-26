package br.com.monkey.ecx.core.tenant;

public class TenantThreadLocalStorage {

	public static final String DEFAULT_TENANT = "defaultTenant";

	private static final String SPLITTER = "-";

	private static ThreadLocal<String> tenant = new ThreadLocal<>();

	public static void setTenantName(String tenantName) {
		tenant.set(tenantName);
	}

	public static String getTenantName() {
		return tenant.get();
	}

	public static String getProgram() {
		String program = "";
		if (!TenantThreadLocalStorage.getTenantName().equalsIgnoreCase(DEFAULT_TENANT)) {
			String[] tenant = TenantThreadLocalStorage.getTenantName().split(SPLITTER);
			program = tenant[tenant.length - 1];
		}
		return program;
	}

	public static boolean isDefaultProgram() {
		return TenantThreadLocalStorage.getTenantName().equalsIgnoreCase(DEFAULT_TENANT);
	}

}
