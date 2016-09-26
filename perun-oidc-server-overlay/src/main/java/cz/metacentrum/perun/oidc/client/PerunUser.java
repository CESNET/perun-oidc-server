package cz.metacentrum.perun.oidc.client;

/**
 * @author Ondrej Velisek <ondrejvelisek@gmail.com>
 */
public class PerunUser extends PerunBean {

    private boolean serviceUser;
    private String middleName;
    private String firstName;
    private String lastName;
    private String titleAfter;
    private boolean sponsoredUser;
    private boolean specificUser;
    private String majorSpecificType;
    private String titleBefore;
    private int id;
    private String beanName;


    public String getDisplayName() {
        return ifHas(titleBefore+" ")+
                ifHas(firstName+" ")+
                ifHas(middleName+" ")+
                ifHas(lastName+" ")+
                ifHas(titleAfter);
    }

    private String ifHas(String name) {
        if (name == null) {
            return "";
        }
        if (name.trim().length() == 0) {
            return "";
        }
        if (name.trim().equals("null")) {
            return "";
        }
        return name;
    }

    public boolean isServiceUser() {
        return serviceUser;
    }

    public void setServiceUser(boolean serviceUser) {
        this.serviceUser = serviceUser;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getTitleAfter() {
        return titleAfter;
    }

    public void setTitleAfter(String titleAfter) {
        this.titleAfter = titleAfter;
    }

    public boolean isSponsoredUser() {
        return sponsoredUser;
    }

    public void setSponsoredUser(boolean sponsoredUser) {
        this.sponsoredUser = sponsoredUser;
    }

    public boolean isSpecificUser() {
        return specificUser;
    }

    public void setSpecificUser(boolean specificUser) {
        this.specificUser = specificUser;
    }

    public String getMajorSpecificType() {
        return majorSpecificType;
    }

    public void setMajorSpecificType(String majorSpecificType) {
        this.majorSpecificType = majorSpecificType;
    }

    public String getTitleBefore() {
        return titleBefore;
    }

    public void setTitleBefore(String titleBefore) {
        this.titleBefore = titleBefore;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    @Override
    public String toString() {
        return "PerunUser{" +
                "serviceUser=" + serviceUser +
                ", middleName='" + middleName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", titleAfter='" + titleAfter + '\'' +
                ", sponsoredUser=" + sponsoredUser +
                ", specificUser=" + specificUser +
                ", majorSpecificType='" + majorSpecificType + '\'' +
                ", titleBefore='" + titleBefore + '\'' +
                ", id=" + id +
                ", beanName='" + beanName + '\'' +
                '}';
    }
}
