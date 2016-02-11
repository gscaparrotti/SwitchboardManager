package serialcontroller;

import maincontroller.MainController;

public abstract class AbstractSerialController implements SerialController {

    protected MainController ctrl;

    @SuppressWarnings("unused")
    private AbstractSerialController() {
    }

    public AbstractSerialController(final MainController ctrl) {
	super();
	this.ctrl = ctrl;
    }

}