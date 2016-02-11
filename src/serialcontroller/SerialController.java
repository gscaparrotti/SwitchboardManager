package serialcontroller;

public interface SerialController {

    void setParameters(int roomStart, int roomEnd, int inputLenght) throws IllegalArgumentException;

    void setRS232Configuration(int baudRate, int dataBits, int stopBits, int parity);

    void startRS232Port(String comNmbr);

    void startListening();

    void stopListening();

}
