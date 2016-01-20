package model;

import java.util.List;
import java.util.Map;

public interface Calls {

    public void addCall(int id, String details);

    public Map<Integer, List<String>> getAllCalls();

    public List<String> getCallsByID(int id);

    public void deleteAllCalls();

    public void deleteCallsByID(int id);

}
