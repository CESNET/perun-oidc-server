package cz.metacentrum.perun.oidc.client;

/**
 * Class represents facility from Perun system.
 *
 * @author Jiri Mauritz
 */
public class PerunFacility extends PerunBean {

    private int id;
    private String name;
    private String description;

    public PerunFacility() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();

        return str.append(getClass().getSimpleName()).append( ":[id='").append(getId()).append("', name='").append(name).append(
                "', description='").append(description).append("']").toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        PerunFacility facility = (PerunFacility) o;

        return id == facility.id;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + id;
        return result;
    }
}

