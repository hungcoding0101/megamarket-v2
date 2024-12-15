package hung.megamarketv2.common.utils;

import java.util.Random;

public class NumberUtils {
    private final Random random = new Random();

    private NumberUtils() {
    }

    public int generateRandomDigitWithinRange(int min, int max) {
        return random.nextInt((max - min) + 1) + min;
    }
}
