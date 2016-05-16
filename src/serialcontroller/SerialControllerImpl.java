package serialcontroller;

import java.util.Date;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import maincontroller.MainController;

public class SerialControllerImpl implements SerialController, SerialPortEventListener {

    private static final int PERIODMILLIS = 2000;
    private int lenght = 65;
    private int[] rNum = new int[] {0, 3};
    private String input = "";
    private SerialPort port;
    private Date time = new Date();
    private boolean started;
    private MainController ctrl;

    @Override
    public void onCreate(final MainController ctrl) {
        this.ctrl = ctrl;        
    }

    @Override
    public void startRS232Port(final String comNmbr) {
	try {
	    port = new SerialPort(comNmbr);
	    port.openPort();
	} catch (SerialPortException e) {
	    ctrl.showMessageOnView("Impossibile inizializzare la porta seriale RS232 (1)");
	}
    }

    @Override
    public void setParameters(final int roomStart, final int roomEnd, final int inputLenght) {
	if (roomEnd < roomStart || roomEnd < inputLenght || inputLenght <= 0) {
	    throw new IllegalArgumentException();
	}
	rNum[0] = roomStart;
	rNum[1] = roomEnd;
	lenght = inputLenght;
    }

    @Override
    public void setRS232Configuration(final int baudRate, final int dataBits, final int stopBits, final int parity) {
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
	if (port == null) {
	    ctrl.showMessageOnView("Impossibile inizializzare la porta seriale RS232 (4)");
	} else {
	    try {
		port.addEventListener(this);
	    } catch (SerialPortException e) {
		ctrl.showMessageOnView("Impossibile inizializzare la porta seriale RS232 (3)");
	    }
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
    public void serialEvent(final SerialPortEvent arg0) {
	if (arg0.getEventType() == SerialPortEvent.RXCHAR) {
	    if (!started) { //NOPMD
	        input = "";
		time = new Date();
		started = true;
	    } else if (System.currentTimeMillis() - time.getTime() > PERIODMILLIS) {
		ctrl.addCall(-1, input);
		started = false;
		//time = new Date();
	    }
	    try {
		final String buf = port.readString();
		if (buf != null) {
		    input = input.concat(buf);
		}
		if (input.length() >= lenght) {
		    System.out.println(input);
		    ctrl.addCall(noExceptionsParseInt(input.substring(rNum[0], rNum[1])), input);
	            started = false;
                    try {
                        port.purgePort(SerialPort.PURGE_RXCLEAR);
                        port.purgePort(SerialPort.PURGE_TXCLEAR);
                    } catch (SerialPortException e) {
                        serialPortExceptionHandler(e);
                    }
		}
	    } catch (SerialPortException e) {
	        serialPortExceptionHandler(e);
	    }
	}

    }
    
    private void serialPortExceptionHandler(final SerialPortException e) {
        ctrl.showMessageOnView(
                "Errore nella ricezione dei dati della chiamata. \nUna telefonata potrebbe non essere stata registrata. \n" + e);
        ctrl.addCall(-2, input);
        started = false;
    }

    private int noExceptionsParseInt(final String value) {
	int result = 0;
	for (int i = 0; i < value.length(); i++) {
	    if (value.charAt(i) >= 48 && value.charAt(i) <= 57) {
		result = 10 * result + value.charAt(i) - 48;
	    }
	}
	return result;
    }

}
