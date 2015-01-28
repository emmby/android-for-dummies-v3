//Setup your test environment
//To avoid flakiness, we highly recommend that you turn off system animations on the virtual or physical device(s) used for testing.
//    Under 'Settings->Developer options' disable the following 3 settings and restart the device:



import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.test.ActivityInstrumentationTestCase2;

import com.dummies.silentmodetoggle.MainActivity;
import com.dummies.silentmodetoggle.R;
import com.dummies.silentmodetoggle.util.RingerHelper;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions
    .matches;
import static android.support.test.espresso.matcher.ViewMatchers
    .isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

public class SilentModeToggleEspressoTest
    extends ActivityInstrumentationTestCase2<MainActivity> 
{
    AudioManager audioManager;

    public SilentModeToggleEspressoTest() {
        super(MainActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        
        // Get the Activity under test, starting it if necessary.
        // This must be done before you can run any testcases
        // that use the activity.
        Activity activity = getActivity();
        
        // Retrieve an AudioManager from the activity
        audioManager = (AudioManager) 
            activity.getSystemService(Context.AUDIO_SERVICE);

        // Make sure the ringer mode is reset to normal
        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
    }

    public void testPhoneIconIsDisplayed() {
        // When the phone_icon view is available,
        // check that it is displayed.
        onView(withId(R.id.phone_icon)).check(matches(isDisplayed()));
    }
    
    public void testCanToggleIcon() {
        // When the phone_icon view is available, click it
        onView(withId(R.id.phone_icon)).perform(click());

        // Then assert that the phone is now in silent mode.
        assertTrue(RingerHelper.isPhoneSilent(audioManager));
    }
}
