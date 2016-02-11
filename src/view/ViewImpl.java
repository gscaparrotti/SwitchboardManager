package view;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;

import maincontroller.MainController;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.print.PrinterException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.JButton;
import javax.swing.JSpinner;

public class ViewImpl extends AbstractView {

    /**
     * 
     */
    private static final long serialVersionUID = -8951129176811320061L;

    private static final String[] PROPS = new String[] { "Interno", "Dettagli Chiamata" };
    private static final Object[][] INIT_DATA = new Object[][] {};
    private final DefaultTableModel tm = new DefaultTableModel(INIT_DATA, PROPS);
    private boolean oneRoomOnly;
    private final JTable table;
    private final JSpinner spinner;
    private final JCheckBox checkbox;

    /**
     * Create the frame.
     */
    public ViewImpl(final MainController controller) {
	super(controller);
	this.setIconImage(java.awt.Toolkit.getDefaultToolkit().createImage(ClassLoader.getSystemResource("icon.png")));
	this.setTitle("SwitchBoard Manager");
	this.setLocationByPlatform(true);
	this.setResizable(false);
	setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	final JPanel contentPane = new JPanel();
	contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
	setContentPane(contentPane);
	contentPane.setLayout(new BorderLayout(0, 0));

	final JPanel panel = new JPanel();
	contentPane.add(panel, BorderLayout.CENTER);
	panel.setLayout(new BorderLayout(0, 0));

	table = new JTable();
	table.setModel(tm);
	table.setEnabled(false);
	table.getColumnModel().setColumnSelectionAllowed(false);
	table.getTableHeader().setReorderingAllowed(false);
	table.getTableHeader().setResizingAllowed(false);
	table.getColumnModel().getColumn(0).setMinWidth(100);
	table.getColumnModel().getColumn(0).setMaxWidth(100);
	table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
	table.setRowHeight(table.getRowHeight() * 2);
	table.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, (int) (table.getFont().getSize() * 1.5)));

	final JScrollPane scroll = new JScrollPane(table);
	panel.add(scroll, BorderLayout.CENTER);

	final JPanel panel_1 = new JPanel();
	contentPane.add(panel_1, BorderLayout.SOUTH);
	panel_1.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

	spinner = new JSpinner();
	spinner.setValue(201);

	checkbox = new JCheckBox();
	checkbox.setText("Solo Camere");
	checkbox.setSelected(true);

	panel_1.add(checkbox);
	panel_1.add(spinner);

	final JButton btnVisualizzaChiamatePer = new JButton("Visualizza interno");
	panel_1.add(btnVisualizzaChiamatePer);
	btnVisualizzaChiamatePer.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(final ActionEvent e) {
		oneRoomOnly = true;
		notifyShowEvent();
	    }
	});

	final JButton btnTutteChiamate = new JButton("Visualizza tutto");
	panel_1.add(btnTutteChiamate);
	btnTutteChiamate.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(final ActionEvent e) {
		oneRoomOnly = false;
		notifyShowEvent();
	    }
	});

	final JButton btnEliminaChiamamteDella = new JButton("Elimina interno");
	panel_1.add(btnEliminaChiamamteDella);
	btnEliminaChiamamteDella.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(final ActionEvent e) {
		notifyDeleteEvent((int) spinner.getValue());
	    }
	});

	final JButton btnEliminaTutteLe = new JButton("Elimina tutto");
	panel_1.add(btnEliminaTutteLe);

	btnEliminaTutteLe.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(final ActionEvent e) {
		notifyDeleteEvent(0);
	    }
	});

	final JButton stampa = new JButton("Stampa");
	panel_1.add(stampa);

	stampa.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(final ActionEvent e) {
		printShownCalls();
	    }
	});

	checkbox.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(final ActionEvent e) {
		notifyShowEvent();
	    }
	});
	
	spinner.addChangeListener(new ChangeListener() {
	    @Override
	    public void stateChanged(final ChangeEvent e) {
		notifyShowEvent();	
	    }
	});

	this.addWindowListener(new WindowAdapter() {
	    public void windowClosing(final WindowEvent e) {
		quitHandler();
	    }
	});

	this.pack();
    }

    @Override
    public void showMessage(final String message) {
	final JOptionPane pane = new JOptionPane("Informazione: " + message, JOptionPane.PLAIN_MESSAGE);
	final JDialog dialog = pane.createDialog("Messaggio");
	dialog.addWindowListener(new WindowAdapter() {
	    @Override
	    public void windowOpened(final WindowEvent e) {
		final ExecutorService ex = Executors.newSingleThreadExecutor();
		ex.submit(() -> {
		    try {
			Thread.sleep(10000);
			SwingUtilities.invokeLater(() -> dialog.dispose());
			ex.shutdownNow();
		    } catch (Exception e1) { 
			SwingUtilities.invokeLater(() -> dialog.dispose());
			ex.shutdownNow();
		    }
		});
	    }
	});
	dialog.setLocationByPlatform(true);
	dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	dialog.setModal(false);
	dialog.setVisible(true);
    }

    @Override
    public void update(final Map<Integer, List<String>> calls) {
	tm.setRowCount(0);
	if (oneRoomOnly) {
	    final int value = (int) (spinner.getValue());
	    if (calls.containsKey(value)) {
		for (final String s : calls.get(value)) {
		    tm.addRow(new Object[] { value, s });
		}
	    }
	} else {
	    for (final Integer i : calls.keySet()) {
		if (!checkbox.isSelected() || i >= ctrl.getBoundaries()[0] && i <= ctrl.getBoundaries()[1]) {
		    for (final String s : calls.get(i)) {
			tm.addRow(new Object[] { i, s });
		    }
		}
	    }
	}
    }

    @Override
    public void makeSBMVisible() {
	this.setVisible(true);
    }

    private void notifyShowEvent() {
	ctrl.showCallsEventFired();
    }

    private void notifyDeleteEvent(final int room) {
	final int n = JOptionPane.showConfirmDialog(this, "Vuoi davvero eliminare le chiamate?", "Elimina Chiamate",
		JOptionPane.YES_NO_OPTION);
	if (n == JOptionPane.YES_OPTION) {
	    ctrl.deleteCallsEventFired(room);
	}
    }

    private void printShownCalls() {
	if (table.getRowCount() > 0) {
	    try {
		table.print(JTable.PrintMode.FIT_WIDTH, new MessageFormat("Stampa chiamate"), new MessageFormat(""));
	    } catch (PrinterException e1) {
		showMessage(e1.getMessage() + "Impossibile Stampare");
	    }
	} else {
	    showMessage("Non ci sono chiamate da stampare.");
	}
    }

    private void quitHandler() {
	final int n = JOptionPane.showConfirmDialog(this, "Vuoi davvero uscire?", "Uscita", JOptionPane.YES_NO_OPTION);
	if (n == JOptionPane.YES_OPTION) {
	    exit();
	}
    }

    private void exit() {
	ctrl.exit();
    }

}
