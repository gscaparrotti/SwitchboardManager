package maincontroller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import jssc.SerialPortList;
import model.Calls;
import serialcontroller.SerialController;
import view.View;

public class MainControllerImpl implements MainController {

    public static final String DIR = System.getProperty("user.home"); // cartella
								      // personale
								      // dell'utente
    public static final String SEPARATOR = System.getProperty("file.separator");
    private static final String SAVE_FILE = "SwitchBoardData.dat";
    private static final String SETTINGS_FILE = "SwitchBoardSettings.txt";
    private static final String HOTEL_FILE = "SwitchBoardHotel.txt";
    private static final String PATH = DIR + SEPARATOR;
    private static final int SETTINGS_PARAMETERS = 7;
    private static final int BOUNDARIES_PARAMETERS = 2;
    private static int lowLimit = 201;
    private static int highLimit = 640;
    private final SerialController serialCtrl;
    private Calls calls;
    private final View view;

    public MainControllerImpl(final Calls calls, final SerialController serialCtrl, final View view) {
	this.calls = calls;
	this.view = view;
	this.serialCtrl = serialCtrl;
    }

    @Override
    public void start() {
	final String[] portsList = SerialPortList.getPortNames();
	if (portsList.length > 0) {
	    serialCtrl.startRS232Port(portsList[0]);
	    serialCtrl.setRS232Configuration(9600, 8, 1, 0);
	    loadSettingsFromFile();
	}
	this.load();
	view.update(calls.getAllCalls());
	serialCtrl.startListening();
    }

    @Override
    public void addCall(final int room, final String details) {
	if (calls != null) {
	    calls.addCall(room, details);
	}
	if (view != null) {
	    view.update(calls.getAllCalls());
	}
    }

    @Override
    public void showCallsEventFired() {
	if (view != null) {
	    view.update(calls.getAllCalls());
	}
    }

    @Override
    public void deleteCallsEventFired(final int ID) {
	if (ID == 0) {
	    calls.deleteAllCalls();
	} else {
	    calls.deleteCallsByID(ID);
	}
	if (view != null) {
	    view.update(calls.getAllCalls());
	}
    }

    @Override
    public int[] getBoundaries() {
	return new int[] { lowLimit, highLimit };
    }

    @Override
    public void load() {
	try {
	    final ObjectInput ois = new ObjectInputStream(new FileInputStream(PATH + SAVE_FILE));
	    this.calls = (Calls) ois.readObject();
	    ois.close();
	} catch (Exception e) {
	    this.showMessageOnView("Impossibile caricare i dati da file: Dati non presenti");
	}
    }

    @Override
    public void save() {
	try {
	    final ObjectOutput oos = new ObjectOutputStream(new FileOutputStream(PATH + SAVE_FILE));
	    oos.writeObject(this.calls);
	    oos.close();
	} catch (Exception e) {
	    this.showMessageOnView("Impossibile salvare i dati su file");
	}
    }

    @Override
    public void exit() {
	serialCtrl.stopListening();
	System.exit(0);
    }

    @Override
    public void showMessageOnView(final String err) {
	if (view == null) {
	    System.out.println(err);
	} else {
	    view.showMessage(err);
	}
    }

    private void loadSettingsFromFile() {
	if (new File(DIR + SEPARATOR + SETTINGS_FILE).exists()) {
	    try {
		final BufferedReader r = new BufferedReader(
			new InputStreamReader(new FileInputStream(DIR + SEPARATOR + SETTINGS_FILE), "UTF8"));
		if (r.ready()) {
		    final String[] settings = r.readLine().split(",");
		    if (settings.length >= SETTINGS_PARAMETERS) {
			serialCtrl.setRS232Configuration(Integer.parseUnsignedInt(settings[0]),
				Integer.parseUnsignedInt(settings[1]), Integer.parseUnsignedInt(settings[2]),
				Integer.parseUnsignedInt(settings[3]));
			serialCtrl.setParameters(Integer.parseUnsignedInt(settings[4]),
				Integer.parseUnsignedInt(settings[5]), Integer.parseUnsignedInt(settings[6]));
		    }
		}
		r.close();
	    } catch (Exception e) {
		view.showMessage("Impossibile caricare i dati da file: " + e.getMessage());
	    }
	}
	if (new File(DIR + SEPARATOR + HOTEL_FILE).exists()) {
	    try {
		final BufferedReader r = new BufferedReader(
			new InputStreamReader(new FileInputStream(DIR + SEPARATOR + HOTEL_FILE), "UTF8"));
		if (r.ready()) {
		    final String[] settings = r.readLine().split(",");
		    if (settings.length >= BOUNDARIES_PARAMETERS) {
			MainControllerImpl.lowLimit = Integer.parseUnsignedInt(settings[0]);
			MainControllerImpl.highLimit = Integer.parseUnsignedInt(settings[1]);
		    }
		}
		r.close();
	    } catch (Exception e) {
		view.showMessage("Impossibile caricare i dati da file: " + e.getMessage());
		MainControllerImpl.lowLimit = 201;
		MainControllerImpl.highLimit = 640;
	    }
	}
    }

}
