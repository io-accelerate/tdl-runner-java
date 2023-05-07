package befaster.solutions.TST;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class OneTest {
    private One one;

    @BeforeEach
    public void name() {
        one = new One();
    }

    @Test
    public void run() {
        assertThat(one.apply(), equalTo(1));
    }
}