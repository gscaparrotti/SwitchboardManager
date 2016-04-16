package serialcontroller;

import maincontroller.MainController;

public interface SerialController {
    
    void onCreate(MainController ctrl);

    void setParameters(int roomStart, int roomEnd, int inputLenght) throws IllegalArgumentException;

    void setRS232Configuration(int baudRate, int dataBits, int stopBits, int parity);

    void startRS232Port(String comNmbr);

    void startListening();

    void stopListening();

}
