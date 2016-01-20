package serialController;

import mainController.MainController;

public abstract class AbstractSerialController implements SerialController {

    protected MainController ctrl;

    @SuppressWarnings("unused")
    private AbstractSerialController() {
    }

    public AbstractSerialController(MainController ctrl) {
	super();
	this.ctrl = ctrl;
    }

}