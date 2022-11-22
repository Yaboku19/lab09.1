package it.unibo.oop.reactivegui02;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.lang.reflect.InvocationTargetException;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Second example of reactive GUI.
 */
public final class ConcurrentGUI extends JFrame {
    private static final long serialVersionUID = 5L;
    private static final double WIDTH_PERC = 0.2;
    private static final double HEIGHT_PERC = 0.1;
    private static final String NAME = "CouncurrentGUI02";
    private final JLabel counterText = new JLabel("0");

    /**
     * constructor with 0 arguments, with all the set of the view.
     */
    public ConcurrentGUI() {
        super();
        final JPanel canvas = new JPanel(new FlowLayout(FlowLayout.CENTER));
        final JButton up = new JButton("Up");
        final JButton down = new JButton("Down");
        final JButton stop = new JButton("Stop");
        // assembling
        canvas.add(counterText);
        canvas.add(up);
        canvas.add(down);
        canvas.add(stop);
        this.setContentPane(canvas);
        this.setTitle(NAME);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        final Agent agent = new Agent();
        new Thread(agent).start();
        // actions
        up.addActionListener(e -> {
            agent.setFlag(true);
        });

        down.addActionListener(e -> {
            agent.setFlag(false);
        });

        stop.addActionListener(e -> {
            agent.stopCounting();
            up.setEnabled(false);
            down.setEnabled(false);
        });
    }

    /**
     * set the method visible.
     */
    public void display() {
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize((int) (screenSize.getWidth() * WIDTH_PERC), (int) (screenSize.getHeight() * HEIGHT_PERC));
        this.setVisible(true);
    }

    /**
     * class in which the Thread will operate.
     */
    private class Agent implements Runnable {
        private boolean flag = true;
        private int counter;
        private boolean stop;

        @Override
        public void run() {
            while (!this.stop) {
                    try {
                        if (this.flag) {
                            this.counter++;
                        } else {
                            this.counter--;
                        }
                        SwingUtilities.invokeAndWait(() -> ConcurrentGUI.this.counterText.setText(Integer.toString(counter)));
                        Thread.sleep(100);
                    } catch (InvocationTargetException | InterruptedException ex) {
                        ex.printStackTrace(); // NOPMD: i need it
                    }
            }
        }

        /**
         * set the flag on true for adding and flas for subtracting.
         * @param flag the flag to set
         */
        public void setFlag(final Boolean flag) {
            this.flag = flag;
        }

        /**
         * stop the thread.
         */
        public void stopCounting() {
            this.stop = true;
        }
    }
}
