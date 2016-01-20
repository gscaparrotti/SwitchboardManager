package view;

import java.util.List;
import java.util.Map;

public interface View {

    public void update(Map<Integer, List<String>> calls);

    public void showMessage(String message);

    public void MakeSBMVisible();

}