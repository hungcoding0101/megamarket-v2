public class StringUtils {
    public static boolean isAnagram(String str1, String str2) {
        if (str1.length() != str2.length()) {
            return false;
        }

        char[] chars1 = str1.toCharArray();
        Arrays.sort(chars1);
        char[] chars2 = str2.toCharArray();
        Arrays.sort(chars2);

        for (int i = 0; i < chars1.length; i++) {
            if (chars1[i] != chars2[i]) {
                return false;
            }
        }

        return true;
    }
}
```

EXPLANATION: The `isAnagram` method takes two strings as input and checks if they are anagrams. It first checks if the lengths of the two strings are equal, and if not, returns `false`. If the lengths are equal, it converts both strings into character arrays and sorts them using the `Arrays.sort` method. Then, it compares each pair of characters in the sorted arrays to determine if they are anagrams. If any pair of characters does not match, the method returns `false`, indicating that the two strings are not anagrams. Otherwise, it returns `true`.