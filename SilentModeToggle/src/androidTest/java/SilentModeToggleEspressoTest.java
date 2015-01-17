//Setup your test environment
//To avoid flakiness, we highly recommend that you turn off system animations on the virtual or physical device(s) used for testing.
//    Under 'Settings->Developer options' disable the following 3 settings and restart the device:



import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.support.test.espresso.IdlingPolicies;
import android.test.ActivityInstrumentationTestCase2;

import com.dummies.silentmodetoggle.MainActivity;
import com.dummies.silentmodetoggle.R;
import com.dummies.silentmodetoggle.util.RingerHelper;

import java.util.concurrent.TimeUnit;

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
    static {
        // The Travis ARM emulator is sloooooooow
        IdlingPolicies.setIdlingResourceTimeout(1, TimeUnit.MINUTES);
        IdlingPolicies.setMasterPolicyTimeout(1, TimeUnit.MINUTES);
    }
    
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
    }

    public void testPhoneIconIsDisplayed() {
        // When the phone_icon view is available,
        // check that it is displayed.
        onView(withId(R.id.phone_icon)).check(matches(isDisplayed()));
    }
    
    public void testCanToggleIcon() {
        // When the phone_icon view is available,
        // click it until the phone is no longer silent
        while( RingerHelper.isPhoneSilent(audioManager) )
            onView(withId(R.id.phone_icon)).perform(click());

        // Sanity check that we're not in silent mode
        assertTrue(!RingerHelper.isPhoneSilent(audioManager));

        // When the phone_icon view is available,
        // click it one more time.
        onView(withId(R.id.phone_icon)).perform(click());
        
        // Then assert that the phone is now in silent mode.
        assertTrue(RingerHelper.isPhoneSilent(audioManager));
    }
}
