package view;

import java.util.List;
import java.util.Map;

public interface View {

    void update(Map<Integer, List<String>> calls);

    void showMessage(String message);

    void makeSBMVisible();

}