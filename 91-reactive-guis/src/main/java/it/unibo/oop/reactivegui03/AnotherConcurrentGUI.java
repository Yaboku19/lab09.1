package it.unibo.oop.reactivegui03;

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
public final class AnotherConcurrentGUI extends JFrame {
    private static final String NAME = "CouncurrentGUI02";
    private final JLabel counterText = new JLabel("0");
    private static final long serialVersionUID = 5L;
    private static final double WIDTH_PERC = 0.2;
    private static final double HEIGHT_PERC = 0.1;
    private final JButton down = new JButton("Down");
    private final JButton stop = new JButton("Stop");
    private final JButton up = new JButton("Up");
    private static final int MAX_TIME = 10;
    private static final int TIME_SLEEP = 1000;

    /**
     * constructor with 0 arguments, with all the set of the view.
     */
    public AnotherConcurrentGUI() {
        super();
        final JPanel canvas = new JPanel(new FlowLayout(FlowLayout.CENTER));
        // assembling
        canvas.add(counterText);
        canvas.add(up);
        canvas.add(down);
        canvas.add(stop);
        this.setContentPane(canvas);
        this.setTitle(NAME);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        final Agent1 agent1 = new Agent1();
        new Thread(agent1).start();
        // actions
        down.addActionListener(e -> {
            agent1.setFlag(false);
        });

        stop.addActionListener(e -> {
            agent1.stopCounting();
            up.setEnabled(false);
            down.setEnabled(false);
        });

        up.addActionListener(e -> {
            agent1.setFlag(true);
        });

        new Thread(() -> {
            try {
                Thread.sleep(TIME_SLEEP * MAX_TIME);
            } catch (InterruptedException ex) {
                ex.printStackTrace();   // NOPMD
            }
            agent1.stopCounting();
            try {
                SwingUtilities.invokeAndWait(() -> up.setEnabled(false));
                SwingUtilities.invokeAndWait(() -> down.setEnabled(false));
                SwingUtilities.invokeAndWait(() -> stop.setEnabled(false));
            } catch (InvocationTargetException | InterruptedException e1) {
                e1.printStackTrace();   // NOPMD
            }
        }).start();
    }

    /**
     * set the method visible.
     */
    public void display() {
        final Dimension screenSizes = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize((int) (screenSizes.getWidth() * WIDTH_PERC), (int) (screenSizes.getHeight() * HEIGHT_PERC));
        this.setVisible(true);
    }

    /**
     * class in which the Thread will operate.
     */
    private class Agent1 implements Runnable {
        private boolean flag = true;
        private int counter;
        private volatile boolean stop;

        @Override
        public void run() {
            while (!this.stop) {
                    try {
                        if (this.flag) {
                            this.counter++;
                        } else {
                            this.counter--;
                        }
                        SwingUtilities.invokeAndWait(() -> 
                            AnotherConcurrentGUI.this.counterText.setText(Integer.toString(counter)));
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
