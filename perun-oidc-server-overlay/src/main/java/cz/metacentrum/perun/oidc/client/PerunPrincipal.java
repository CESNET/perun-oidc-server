package cz.metacentrum.perun.oidc.client;

/**
 * @author Ondrej Velisek <ondrejvelisek@gmail.com>
 */
public class PerunPrincipal {

	private String extSourceName;
	private String userExtSourceLogin;
	private int extSourceLoa;
	private String extSourceType;

	public PerunPrincipal(String extSourceName, String userExtSourceLogin, int extSourceLoa, String extSourceType) {
		this.extSourceName = extSourceName;
		this.userExtSourceLogin = userExtSourceLogin;
		this.extSourceLoa = extSourceLoa;
		this.extSourceType = extSourceType;
	}

	public String getExtSourceName() {
		return extSourceName;
	}

	public String getUserExtSourceLogin() {
		return userExtSourceLogin;
	}

	public int getExtSourceLoa() {
		return extSourceLoa;
	}

	public String getExtSourceType() {
		return extSourceType;
	}

	@Override
	public String toString() {
		return "PerunPrincipal{" +
				"extSourceName='" + extSourceName + '\'' +
				", userExtSourceLogin='" + userExtSourceLogin + '\'' +
				", extSourceLoa=" + extSourceLoa +
				", extSourceType='" + extSourceType + '\'' +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		PerunPrincipal that = (PerunPrincipal) o;

		if (getExtSourceLoa() != that.getExtSourceLoa()) return false;
		if (getExtSourceName() != null ? !getExtSourceName().equals(that.getExtSourceName()) : that.getExtSourceName() != null)
			return false;
		if (getUserExtSourceLogin() != null ? !getUserExtSourceLogin().equals(that.getUserExtSourceLogin()) : that.getUserExtSourceLogin() != null)
			return false;
		return getExtSourceType() != null ? getExtSourceType().equals(that.getExtSourceType()) : that.getExtSourceType() == null;

	}

	@Override
	public int hashCode() {
		int result = getExtSourceName() != null ? getExtSourceName().hashCode() : 0;
		result = 31 * result + (getUserExtSourceLogin() != null ? getUserExtSourceLogin().hashCode() : 0);
		result = 31 * result + getExtSourceLoa();
		result = 31 * result + (getExtSourceType() != null ? getExtSourceType().hashCode() : 0);
		return result;
	}
}
