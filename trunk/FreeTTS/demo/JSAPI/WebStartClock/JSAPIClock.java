import com.sun.speech.freetts.jsapi.FreeTTSEngineCentral; 

import java.util.Locale;

import javax.speech.EngineList; 
import javax.speech.EngineCreate; 
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerModeDesc;

/**
 * A talking clock powered by FreeTTS.
 */
public class JSAPIClock extends Clock {

    protected Synthesizer synthesizer;


    /**
     * Creates the synthesizer, called by the constructor.
     */
    public void createSynthesizer() {

        String synthesizerName =
            "Limited domain FreeTTS Speech Synthesizer from Sun Labs";

        try {
            SynthesizerModeDesc desc = 
                new SynthesizerModeDesc(synthesizerName, 
                                        null,
                                        Locale.US, 
                                        Boolean.FALSE,
                                        null);

            FreeTTSEngineCentral central = new FreeTTSEngineCentral();
            EngineList list = central.createEngineList(desc); 
            
            if (list.size() > 0) { 
                EngineCreate creator = (EngineCreate) list.get(0); 
                synthesizer = (Synthesizer) creator.createEngine(); 
            } 
            if (synthesizer == null) {
                System.err.println("Cannot create synthesizer");
                System.exit(1);
            }
            synthesizer.allocate();
            synthesizer.resume();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Speaks the given time in full text.
     *
     * @param time time in full text
     */
    protected void speak(String time) {
        synthesizer.speakPlainText(time, null);
    }


    /**
     * main() method to run the JSAPIClock.
     */
    public static void main(String args[]) {
        Clock frame = new JSAPIClock();
        frame.pack();
        frame.show();
        frame.createSynthesizer();
        frame.startClock();
    }
}
