package cz.metacentrum.perun.oidc.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.activation.UnsupportedDataTypeException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Facility Manager from Perun system.
 *
 * @author Jiri Mauritz
 */
public class FacilitiesManager extends Manager {

    private static FacilitiesManager manager;

    private FacilitiesManager() {
    }

    public static synchronized FacilitiesManager getInstance() {
        if (manager == null) {
            manager = new FacilitiesManager();
        }
        return manager;
    }

    public List<PerunFacility> getFacilitiesByAttribute(String attributeName, String attributeValue) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("attributeName", attributeName);
            params.put("attributeValue", attributeValue);
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(get("getFacilitiesByAttribute", params), new TypeReference<List<PerunFacility>>(){});
        } catch (UnsupportedDataTypeException e) {
            return null;
        } catch (IOException e) {
            throw new IllegalStateException("IO Error while calling getFacilitiesByAttribute", e);
        }
    }

    public List<PerunResource> getAssignedResources(int facilityId) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("facility", facilityId);
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(get("getAssignedResources", params), new TypeReference<List<PerunResource>>(){});
        } catch (UnsupportedDataTypeException e) {
            return null;
        } catch (IOException e) {
            throw new IllegalStateException("IO Error while calling getAssignedResources", e);
        }
    }

    @Override
    protected String getManagerName() {
        return "facilitiesManager";
    }
}
