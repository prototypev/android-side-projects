package prototypev.PermissiveFov.Tests;

import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Base class definition for unit tests.
 */
@RunWith(JUnit4.class)
public abstract class TestBase {
    @Rule
    public final ExpectedException exception = ExpectedException.none();
}
