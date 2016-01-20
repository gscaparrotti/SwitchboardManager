package launcher;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import mainController.MainController;
import mainController.MainControllerImpl;
import model.CallsImpl;
import serialController.SerialControllerImpl;
import view.ViewImpl;

public class Launcher {

    public static void main(String[] args) {

	try {
	    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
		| UnsupportedLookAndFeelException e) {
	    System.out.println("Errore nell'impostazione dell'interfaccia grafica.");
	}

	MainController ctrl = new MainControllerImpl(new CallsImpl(), SerialControllerImpl.class, ViewImpl.class);

	ctrl.start();
    }
}
