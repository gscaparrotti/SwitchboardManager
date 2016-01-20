package mainController;

public interface MainController {

    public void start();

    public void load();

    public void save();

    public int[] getBoundaries();

    public void addCall(int room, String details);

    public void showCallsEventFired();

    public void deleteCallsEventFired(int ID);

    public void showMessageOnView(String err);

    public void exit();
}
