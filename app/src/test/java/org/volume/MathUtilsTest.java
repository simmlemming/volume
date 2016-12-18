package org.volume;

import org.junit.Test;
import org.volume.util.MathUtils;

import static org.junit.Assert.assertEquals;

/**
 * Created by mtkachenko on 18/12/16.
 */

public class MathUtilsTest {

    @Test
    public void noiseLevel() {
        long db = MathUtils.rawNoiseLevelToDb(5774);
        assertEquals(-15, db);

        db = MathUtils.rawNoiseLevelToDb(3525);
        assertEquals(-19, db);
    }
}
