package serialController;

public interface SerialController {

    public void setParameters(int roomStart, int roomEnd, int inputLenght) throws IllegalArgumentException;

    public void setRS232Configuration(int baudRate, int dataBits, int stopBits, int parity);

    public void startRS232Port(String comNmbr);

    void startListening();

    void stopListening();

}
