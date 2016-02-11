package maincontroller;

public interface MainController {

    void start();

    void load();

    void save();

    int[] getBoundaries();

    void addCall(int room, String details);

    void showCallsEventFired();

    void deleteCallsEventFired(int id);

    void showMessageOnView(String err);

    void exit();
}
