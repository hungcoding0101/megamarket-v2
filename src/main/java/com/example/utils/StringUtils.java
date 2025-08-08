public class StringUtils {
    public static boolean isAnagram(String str1, String str2) {
        if (str1 == null || str2 == null) {
            return false;
        }
        char[] chars1 = str1.toCharArray();
        char[] chars2 = str2.toCharArray();
        Arrays.sort(chars1);
        Arrays.sort(chars2);
        return Arrays.equals(chars1, chars2);
    }
}
```

EXPLANATION: The `isAnagram` method takes two strings as input and checks if they are anagrams. It first checks if either of the strings is null, in which case it returns false. Then, it converts both strings into character arrays using the `toCharArray` method. Next, it sorts the characters in each array using the `Arrays.sort` method. Finally, it compares the sorted arrays using the `Arrays.equals` method and returns true if they are equal, indicating that the strings are anagrams.