package launcher;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import maincontroller.MainController;
import maincontroller.MainControllerImpl;
import model.CallsImpl;
import serialcontroller.SerialControllerImpl;
import view.ViewImpl;

public final class Launcher {
    
    private Launcher() {}

    public static void main(String[] args) {

	try {
	    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
		| UnsupportedLookAndFeelException e) {
	    System.out.println("Errore nell'impostazione dell'interfaccia grafica.");
	}

	final MainController ctrl = new MainControllerImpl(new CallsImpl(), SerialControllerImpl.class, ViewImpl.class);

	ctrl.start();
    }
}
