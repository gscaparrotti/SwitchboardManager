package view;

import javax.swing.JFrame;

import mainController.MainController;

public abstract class AbstractView extends JFrame implements View {

    private static final long serialVersionUID = 9184560336946976459L;
    protected MainController ctrl;

    public AbstractView(MainController ctrl) {
	this.ctrl = ctrl;
    }

}