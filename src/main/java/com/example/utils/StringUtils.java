import java.util.Random;

public class StringUtils {
    public static String generateRandomString(int length) {
        char[] characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length);
            sb.append(characters[index]);
        }
        return sb.toString();
    }
}
```

EXPLANATION: The StringUtils class contains a method called generateRandomString that takes an integer parameter length and returns a random string of the specified length. The method uses a char array containing all possible characters, including uppercase letters, lowercase letters, and digits. It then creates a StringBuilder to store the generated string and uses a Random object to select characters from the array at random. Finally, it returns the generated string as a String.