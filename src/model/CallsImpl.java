package model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class CallsImpl implements Calls, Serializable {

    private static final long serialVersionUID = -6095582628803034978L;
    private Map<Integer, List<String>> calls;

    public CallsImpl() {
	calls = new HashMap<>();
    }

    @Override
    public void addCall(final int id, final String details) {
	calls.putIfAbsent(id, new LinkedList<String>());
	calls.get(id).add(details);
    }

    @Override
    public Map<Integer, List<String>> getAllCalls() {
	final HashMap<Integer, List<String>> temp = new HashMap<>();
	for (final Entry<Integer, List<String>> e : calls.entrySet()) {
	    temp.put(e.getKey(), new LinkedList<String>(e.getValue()));
	}
	return temp;
    }

    @Override
    public List<String> getCallsByID(final int id) {
	return calls.getOrDefault(id, new LinkedList<String>());
    }

    @Override
    public void deleteAllCalls() {
	calls = new HashMap<>();

    }

    @Override
    public void deleteCallsByID(final int id) {
	calls.replace(id, new LinkedList<String>());
    }

}
