package cz.metacentrum.perun.oidc.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author Ondrej Velisek <ondrejvelisek@gmail.com>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class PerunBean {

    public abstract int getId();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PerunBean bean = (PerunBean) o;

        return getId() == bean.getId();
    }

    @Override
    public int hashCode() {
        return Integer.valueOf(getId()).hashCode();
    }

}
