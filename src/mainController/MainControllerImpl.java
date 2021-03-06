package mainController;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import jssc.SerialPortList;
import model.Calls;
import serialController.AbstractSerialController;
import serialController.SerialController;
import view.AbstractView;
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
    private static int LOW_LIMIT = 201;
    private static int HIGH_LIMIT = 640;
    private SerialController serial;
    private Calls calls;
    private View view = null;

    public MainControllerImpl(Calls calls, Class<? extends AbstractSerialController> serial,
	    Class<? extends AbstractView> view) {
	try {
	    Constructor<? extends AbstractView> viewConstructor = view.getDeclaredConstructor(MainController.class);
	    viewConstructor.setAccessible(true);
	    this.view = viewConstructor.newInstance(this);
	    this.view.MakeSBMVisible();
	    Constructor<? extends AbstractSerialController> serialConstructor = serial
		    .getDeclaredConstructor(MainController.class);
	    serialConstructor.setAccessible(true);
	    this.serial = serialConstructor.newInstance(this);
	    this.calls = calls;
	} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
		| NoSuchMethodException | SecurityException e) {
	    if (this.view != null) {
		this.view.showMessage("Impossibile avviare il programma: " + e.getMessage());
	    } else {
		System.out.println("Impossibile avviare il programma: " + e.getMessage());
	    }
	    System.exit(1);
	}
    }

    @Override
    public void start() {
	String[] ports_list = SerialPortList.getPortNames();
	if (ports_list.length > 0) {
	    serial.startRS232Port(ports_list[0]);
	    serial.setRS232Configuration(9600, 8, 1, 0);
	    loadSettingsFromFile();
	}
	this.load();
	view.update(calls.getAllCalls());
	serial.startListening();
    }

    @Override
    public void addCall(int room, String details) {
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
    public void deleteCallsEventFired(int ID) {
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
	return new int[] { LOW_LIMIT, HIGH_LIMIT };
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
	serial.stopListening();
	System.exit(0);
    }

    @Override
    public void showMessageOnView(String err) {
	if (view != null) {
	    view.showMessage(err);
	} else {
	    System.out.println(err);
	}
    }

    private void loadSettingsFromFile() {
	if (new File(DIR + SEPARATOR + SETTINGS_FILE).exists()) {
	    try {
		BufferedReader r = new BufferedReader(
			new InputStreamReader(new FileInputStream(DIR + SEPARATOR + SETTINGS_FILE), "UTF8"));
		if (r.ready()) {
		    String[] settings = r.readLine().split(",");
		    if (settings.length >= SETTINGS_PARAMETERS) {
			serial.setRS232Configuration(Integer.parseUnsignedInt(settings[0]),
				Integer.parseUnsignedInt(settings[1]), Integer.parseUnsignedInt(settings[2]),
				Integer.parseUnsignedInt(settings[3]));
			serial.setParameters(Integer.parseUnsignedInt(settings[4]),
				Integer.parseUnsignedInt(settings[5]), Integer.parseUnsignedInt(settings[6]));
		    }
		}
		r.close();
	    } catch (Exception e) {
		/*
		 * serial.setRS232Configuration(9600, 8, 1, 0);
		 * serial.setParameters(0, 3, 64);
		 */
	    }
	}
	if (new File(DIR + SEPARATOR + HOTEL_FILE).exists()) {
	    try {
		BufferedReader r = new BufferedReader(
			new InputStreamReader(new FileInputStream(DIR + SEPARATOR + HOTEL_FILE), "UTF8"));
		if (r.ready()) {
		    String[] settings = r.readLine().split(",");
		    if (settings.length >= BOUNDARIES_PARAMETERS) {
			MainControllerImpl.LOW_LIMIT = Integer.parseUnsignedInt(settings[0]);
			MainControllerImpl.HIGH_LIMIT = Integer.parseUnsignedInt(settings[1]);
		    }
		}
		r.close();
	    } catch (Exception e) {
		MainControllerImpl.LOW_LIMIT = 201;
		MainControllerImpl.HIGH_LIMIT = 640;
	    }
	}
    }

}
