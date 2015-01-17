//Setup your test environment
//To avoid flakiness, we highly recommend that you turn off system animations on the virtual or physical device(s) used for testing.
//    Under 'Settings->Developer options' disable the following 3 settings and restart the device:



import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.LargeTest;

import com.dummies.silentmodetoggle.MainActivity;
import com.dummies.silentmodetoggle.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions
    .matches;
import static android.support.test.espresso.matcher.ViewMatchers
    .isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@LargeTest
public class SilentModeToggleEspressoTest 
    extends ActivityInstrumentationTestCase2<MainActivity> 
{

    public SilentModeToggleEspressoTest() {
        super(MainActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        
        // Get the Activity under test, starting it if necessary.
        // This must be done before you can run any testcases
        // that use the activity.
        getActivity();
    }

    public void testPhoneIconIsDisplayed() {
        onView(withId(R.id.phone_icon)).check(matches(isDisplayed()));
    }
}
