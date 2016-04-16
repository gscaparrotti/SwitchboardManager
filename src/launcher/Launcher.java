package launcher;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import maincontroller.MainController;
import maincontroller.MainControllerImpl;
import model.CallsImpl;
import serialcontroller.SerialController;
import serialcontroller.SerialControllerImpl;
import view.View;
import view.ViewImpl;

public final class Launcher {
    
    private Launcher() { }

    public static void main(final String[] args) { //NOPMD

	try {
	    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
		| UnsupportedLookAndFeelException e) {
	    System.out.println("Errore nell'impostazione dell'interfaccia grafica.");
	}

	final View view = new ViewImpl();
	final SerialController serialCtrl = new SerialControllerImpl();
	final MainController ctrl = new MainControllerImpl(new CallsImpl(), serialCtrl, view);
	view.onCreate(ctrl);
	serialCtrl.onCreate(ctrl);
	view.makeSBMVisible();
	ctrl.start();
    }
}
