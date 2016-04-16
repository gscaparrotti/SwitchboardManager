package view;

import java.util.List;
import java.util.Map;

import maincontroller.MainController;

public interface View {
    
    void onCreate(MainController ctrl);

    void update(Map<Integer, List<String>> calls);

    void showMessage(String message);

    void makeSBMVisible();

}