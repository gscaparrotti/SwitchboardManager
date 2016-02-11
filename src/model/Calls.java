package model;

import java.util.List;
import java.util.Map;

public interface Calls {

    void addCall(int id, String details);

    Map<Integer, List<String>> getAllCalls();

    List<String> getCallsByID(int id);

    void deleteAllCalls();

    void deleteCallsByID(int id);

}
