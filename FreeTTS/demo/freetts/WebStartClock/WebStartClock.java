import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.audio.JavaClipAudioPlayer;

import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.*;

/**
 * A talking clock powered by FreeTTS.
 */
public class WebStartClock extends JFrame {

    private JLabel timeLabel;
    private JCheckBox announceCheckBox;
    private JTextField intervalTextField;
    private int timeFontSize = 24;

    private String voiceClassName = 
    "com.sun.speech.freetts.en.us.CMUTimeAWBVoice";
    private String databaseName = "cmu_awb/cmu_time_awb.bin";
            
    private Voice clockVoice;
    private GregorianCalendar calendar;
    private SimpleDateFormat dateFormat;

    private long lastSpeakTime;            // in milliseconds
    private int speakInterval = 300000;    // in milliseconds
    private int sleepTime = 5000;          // in milliseconds

    private static char announceMnemonic = 'A';
    private static char minutesMnemonic = 'M';
    private static char speakMnemonic = 'S';

    private boolean debug = true;
    

    /**
     * Constructs a default WebStartClock.
     */
    public WebStartClock() {
        super("WebStart Clock");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JPanel timePanel = new JPanel(new BorderLayout());
        timePanel.setAlignmentY(JPanel.CENTER_ALIGNMENT);

        timeLabel = new JLabel("Time", JLabel.CENTER);
        Font oldFont = timeLabel.getFont();
        timeLabel.setFont(new Font(oldFont.getFontName(), oldFont.getStyle(),
                                   timeFontSize));

        timePanel.add(timeLabel, BorderLayout.CENTER);
        timePanel.add(createAnnouncePanel(), BorderLayout.SOUTH);
        
        getContentPane().add(timePanel, BorderLayout.CENTER);

        JButton speakButton = new JButton("Speak");
        speakButton.setMnemonic(speakMnemonic);

        speakButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                Runnable speaker = new Runnable() {
                    public void run() {
                        speakTime();
                    }
                };
                (new Thread(speaker)).start();
            }
        });

        getContentPane().add(speakButton, BorderLayout.SOUTH);
        
        createCalendar();
        createClockVoice();

        ClockThread clock = new ClockThread();
        clock.start();
    }


    /**
     * Creates the JPanel that allows you to specify the time announcing
     * interval.
     *
     * @return a JPanel
     */ 
    private JPanel createAnnouncePanel() {
        JPanel announcePanel = new JPanel(new FlowLayout());
        announceCheckBox = new JCheckBox("announce every", true);
        announceCheckBox.setMnemonic(announceMnemonic);
        announceCheckBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent ie) {
                if (ie.getStateChange() == ItemEvent.SELECTED) {
                    lastSpeakTime = calendar.getTimeInMillis();
                    debugPrintln
                        ("Last speak time: "  + String.valueOf(lastSpeakTime));
                }
            }
        });

        // a text field to enter the time announcing interval
        intervalTextField = new JTextField("5", 3);
        intervalTextField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String text = intervalTextField.getText();
                if (text.matches("[1-9][0-9]*")) {
                    debugPrintln("New announce interval : " + text);
                    speakInterval = Integer.parseInt(text) * 60000;
                } else {
                    debugPrintln("Invalid minutes input: " + text);
                    intervalTextField.setText
                        (String.valueOf(speakInterval / 60000));
                }
            }
        });

        JLabel minutesLabel = new JLabel("mins");
        minutesLabel.setDisplayedMnemonic(minutesMnemonic);
        minutesLabel.setLabelFor(intervalTextField);

        announcePanel.add(announceCheckBox);
        announcePanel.add(intervalTextField);
        announcePanel.add(minutesLabel);
        return announcePanel;
    }


    /**
     * Create the GregorianCalendar that keeps track of the time.
     */
    private void createCalendar() {
        calendar = new GregorianCalendar();
        dateFormat = new SimpleDateFormat("h:mm a");
        dateFormat.setCalendar(calendar);
    }


    /**
     * Creates the AWB Voice for this Clock.
     */
    private void createClockVoice() {
        try {
            Class voiceClass = Class.forName(voiceClassName);
            clockVoice = (Voice) voiceClass.newInstance();
            clockVoice.getFeatures().setString
                (Voice.DATABASE_NAME, databaseName);
            clockVoice.setAudioPlayer(new JavaClipAudioPlayer());
            clockVoice.load();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Sets the time label.
     *
     * @time time the time to set
     */
    public void setTimeLabel(final String time) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                timeLabel.setText(time);
            }
        });
    };


    /**
     * Updates the calendar and the display with the current time.
     */
    private void updateTime() {
        Date currentTime = new Date();
        calendar.setTime(currentTime);
        setTimeLabel(dateFormat.format(currentTime));
    }


    /**
     * Speaks the current time.
     */
    private void speakTime() {
        lastSpeakTime = calendar.getTimeInMillis();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);        

        if (hour < 0 || hour > 23) {
            throw new IllegalArgumentException("Bad time format: hour");
        }
        if (min < 0 || min > 59) {
            throw new IllegalArgumentException("Bad time format: min");
        }
        
        String theTime = TimeUtils.timeToString(hour, min);
        clockVoice.speak(theTime);
    }


    /**
     * Return true if we enough time has elapsed since the last announce
     * time.
     *
     * @return true its time to speak, false otherwise
     */
    private boolean isTimeToSpeak() {
        return ((lastSpeakTime + speakInterval) < calendar.getTimeInMillis());
    }


    /**
     * Print method for debug purposes.
     *
     * @param the debug message to print
     */
    private void debugPrintln(String line) {
        if (debug) {
            System.out.println(line);
        }
    }


    /**
     * A thread for the clock.
     */
    class ClockThread extends Thread {

        public void run() {
            while (true) {
                updateTime();
                if (announceCheckBox.isSelected() && isTimeToSpeak()) {
                    speakTime();
                }

                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                }
            }
        }

    }

    /**
     * main() method to run the WebStartClock.
     */
    public static void main(String args[]) {
        WebStartClock frame = new WebStartClock();
        frame.pack();
        frame.show();
    }
}
