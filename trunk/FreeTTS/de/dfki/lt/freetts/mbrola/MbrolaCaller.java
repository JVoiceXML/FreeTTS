/**
 * Copyright 2002 DFKI GmbH.
 * All Rights Reserved.  Use is subject to license terms.
 *
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 */

package de.dfki.lt.freetts.mbrola;

import com.sun.speech.freetts.UtteranceProcessor;
import com.sun.speech.freetts.ProcessException;
import com.sun.speech.freetts.Utterance;
import com.sun.speech.freetts.Relation;
import com.sun.speech.freetts.Item;

import java.io.*;

/**
 * Calls external MBROLA binary to synthesise the utterance.
 */
public class MbrolaCaller implements UtteranceProcessor {

    private String cmd;
    private long closeDelay = 0l;

    /**
     * Create an Mbrola caller which will call an external MBROLA binary
     * using the command <code>cmd</code>. The command string is used
     * as it is, which means that it must contain full path specifications
     * and the correct file separators.
     */
    public MbrolaCaller(String cmd) {
        this.cmd = cmd;
	closeDelay = Long.getLong
	    ("de.dfki.lt.freetts.mbrola.MbrolaCaller.closeDelay",
	     100L).longValue();
    }

    /**
     * Call external MBROLA binary to synthesise the utterance.
     *
     * @param  utterance  the utterance to process
     *
     * @throws ProcessException if an error occurs while
     *         processing of the utterance
     */
    public void processUtterance(Utterance utterance) throws ProcessException {
        // Open Mbrola
        Process process;
        try {
            process = Runtime.getRuntime().exec(cmd);
        } catch (Exception e) {
            throw new ProcessException("Cannot start mbrola program: " + cmd);
        }
        PrintWriter toMbrola = new PrintWriter(process.getOutputStream());
        BufferedInputStream fromMbrola =
            new BufferedInputStream(process.getInputStream());

        // Go through Segment relation and print values into Mbrola
	Relation segmentRelation = utterance.getRelation(Relation.SEGMENT);
        Item segment = segmentRelation.getHead();
        while (segment != null) {
            String name = segment.getFeatures().getString("name");
            // Individual duration of segment, in milliseconds:
            int dur = segment.getFeatures().getInt("mbr_dur");
            // List of time-f0 targets. In each target, the first value
            // indicates where on the time axis the target is reached,
            // expressed in percent of segment duration; the second value in
            // each pair is f0, in Hz.
            String targets = segment.getFeatures().getString("mbr_targets");
            toMbrola.println(name + " " + dur + targets);
            segment = segment.getNext();
        }
        toMbrola.flush();

	// BUG:
	// There is a  bug that causes the final 'close' on a stream
	// going to a sub-process to not be seen by the sub-process on
	// occasion. This seems to occur mainly when the close occurs
	// very soon after the creation and writing of data to the
	// sub-process.  This delay can help work around the problem
	// If we delay before the close by 
	// a small amount (100ms), the hang is averted.  This is a WORKAROUND
	// only and should be removed once the bug in the 'exec' is
	//  fixed.  We get the delay from the property:
	//
	// de.dfki.lt.freetts.mbrola.MbrolaCaller.closeDelay,
	// 

	if (closeDelay > 0l) {
	    try {
		Thread.sleep(closeDelay);
	    } catch (InterruptedException ie) {
	    }
	}
        toMbrola.close();

        // remember the BufferedInputStream in the utterance:
        utterance.setObject("fromMbrola", fromMbrola);
    }

    public String toString() {
        return "MbrolaCaller";
    }
}
