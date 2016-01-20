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
    public synchronized void addCall(int id, String details) {
	calls.putIfAbsent(id, new LinkedList<String>());
	calls.get(id).add(details);
    }

    @Override
    public synchronized Map<Integer, List<String>> getAllCalls() {
	HashMap<Integer, List<String>> temp = new HashMap<>();
	for (Entry<Integer, List<String>> e : calls.entrySet()) {
	    temp.put(e.getKey(), new LinkedList<String>(e.getValue()));
	}
	return temp;
    }

    @Override
    public synchronized List<String> getCallsByID(int id) {
	return calls.getOrDefault(id, new LinkedList<String>());
    }

    @Override
    public synchronized void deleteAllCalls() {
	calls = new HashMap<>();

    }

    @Override
    public synchronized void deleteCallsByID(int id) {
	calls.replace(id, new LinkedList<String>());
    }

}
