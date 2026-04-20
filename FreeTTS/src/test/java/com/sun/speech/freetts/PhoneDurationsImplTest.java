/**
 * 
 */
package com.sun.speech.freetts;

import java.net.URL;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test case for the PhoneDurations.
 * @author Dirk Schnelle-Walka
 *
 */
public class PhoneDurationsImplTest {
    private PhoneDurations durations;
    
    /**
     * Set up the test environment.
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        URL url = getClass().getResource("/com/sun/speech/freetts/en/us/dur_stat.txt");
        durations = new PhoneDurationsImpl(url);
    }

    /**
     * Test method for {@link com.sun.speech.freetts.PhoneDurationsImpl#getPhoneDuration(java.lang.String)}.
     */
    @Test
    public void testGetPhoneDuration() {
        PhoneDuration duration = durations.getPhoneDuration("ey");
        Assert.assertEquals(0.165883f, duration.getMean(), 1e-6f);
        Assert.assertEquals(0.075700f, duration.getStandardDeviation(), 1e-6f);
    }

    /**
     * Test method for {@link com.sun.speech.freetts.PhoneDurationsImpl#getPhoneDuration(java.lang.String)}.
     */
    @Test
    public void testGetPhoneDurationUnknown() {
        Assert.assertNull(durations.getPhoneDuration("asdf"));
    }
}
