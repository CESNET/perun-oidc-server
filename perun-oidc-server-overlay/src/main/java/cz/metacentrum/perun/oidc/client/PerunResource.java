package cz.metacentrum.perun.oidc.client;

/**
 * Represents resource from Perun system.
 *
 * @author  Jiri Mauritz
 */
public class PerunResource extends PerunBean {

    private int id;
    private int facilityId;
    private int voId;
    private String name;
    private String description;

    /**
     * Constructs a new instance.
     */
    public PerunResource() {
    }

    /**
     * Gets the name for this instance.
     *
     * @return The name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets the name for this instance.
     *
     * @param name The name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the description for this instance.
     *
     * @return The description.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Sets the description for this instance.
     *
     * @param description The description.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    public int getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(int facilityId) {
        this.facilityId = facilityId;
    }

    public int getVoId() {
        return voId;
    }

    public void setVoId(int voId) {
        this.voId = voId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();

        return str.append(getClass().getSimpleName()).append(":[id='").append(getId()
        ).append("', voId='").append(voId
        ).append("', facilityId='").append(facilityId
        ).append("', name='").append(name
        ).append("', description='").append(description).append("']").toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        PerunResource that = (PerunResource) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + id;
        return result;
    }
}

