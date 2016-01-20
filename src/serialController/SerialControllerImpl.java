package serialController;

import java.util.Date;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import mainController.MainController;

public class SerialControllerImpl extends AbstractSerialController implements SerialPortEventListener {

    private static int LENGHT = 64;
    private static int[] r_num = new int[] { 0, 3 };
    private String input = "";
    private SerialPort port = null;
    private Date time = new Date();
    private boolean started = false;
    private final int period = 5000;

    public SerialControllerImpl(MainController ctrl) {
	super(ctrl);
    }

    @Override
    public void startRS232Port(String comNmbr) {
	try {
	    port = new SerialPort(comNmbr);
	    port.openPort();
	} catch (SerialPortException e) {
	    ctrl.showMessageOnView("Impossibile inizializzare la porta seriale RS232 (1)");
	}
    }

    @Override
    public void setParameters(int roomStart, int roomEnd, int inputLenght) throws IllegalArgumentException {
	if (roomEnd < roomStart || roomEnd < inputLenght || inputLenght <= 0) {
	    throw new IllegalArgumentException();
	}
	SerialControllerImpl.r_num[0] = roomStart;
	SerialControllerImpl.r_num[1] = roomEnd;
	SerialControllerImpl.LENGHT = inputLenght;
    }

    @Override
    public void setRS232Configuration(int baudRate, int dataBits, int stopBits, int parity) {
	if (port != null && port.isOpened()) {
	    try {
		port.setParams(baudRate, dataBits, stopBits, parity);
	    } catch (SerialPortException e) {
		ctrl.showMessageOnView("Impossibile inizializzare la porta seriale RS232 (2)");
	    }
	}
    }

    @Override
    public void startListening() {
	if (port != null) {
	    try {
		port.addEventListener(this);
	    } catch (SerialPortException e) {
		ctrl.showMessageOnView("Impossibile inizializzare la porta seriale RS232 (3)");
	    }
	} else {
	    ctrl.showMessageOnView("Impossibile inizializzare la porta seriale RS232 (4)");
	}
    }

    @Override
    public void stopListening() {
	if (port != null) {
	    try {
		port.closePort();
	    } catch (SerialPortException e) {
		ctrl.showMessageOnView("Impossibile chiudere la porta seriale RS232");
	    }
	}
    }

    @Override
    public void serialEvent(SerialPortEvent arg0) {
	if (arg0.getEventType() == SerialPortEvent.RXCHAR) {
	    if (!started) {
		time = new Date();
		started = true;
	    } else if (System.currentTimeMillis() - time.getTime() > period) {
		ctrl.addCall(-1, input);
		input = "";
		time = new Date();
	    }
	    try {
		String buf = port.readString();
		if (buf != null) {
		    input = input.concat(buf);
		}
		if (input.length() > LENGHT) {
		    started = false;
		    ctrl.addCall(noExceptionsParseInt(input.substring(r_num[0], r_num[1])), input);
		    ctrl.save();
		    input = "";
		    port.purgePort(SerialPort.PURGE_RXCLEAR);
		    port.purgePort(SerialPort.PURGE_TXCLEAR);
		}
	    } catch (SerialPortException e) {
		ctrl.showMessageOnView(
			"Errore nella ricezione dei dati della chiamata. \nUna telefonata potrebbe non essere stata registrata.");
		ctrl.addCall(-2, input);
		input = "";
	    }
	}

    }

    private int noExceptionsParseInt(String value) {
	int result = 0;
	for (int i = 0; i < value.length(); i++) {
	    if (value.charAt(i) >= 48 && value.charAt(i) <= 57) {
		result = 10 * result + (value.charAt(i) - 48);
	    }
	}
	return result;
    }

}
