import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.*;

public class PreImageAttackDemo {

    // Define the character set you want to use for the brute-force attack.
    private static final String CHARSET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%^&*()-=_+{}[]:;'\"\\|,./<>?~`";

    public static void main(String[] args) {
        try {
            String targetHash = "197d3b9b2170c13f11995d504950b79aae0142cf8f1f5b06e753c9820789f367";
            System.out.println("Target Hash: " + targetHash);
            System.out.println("The pre-image is the string \"RMIT\". Let's see if we can find it by brute force.");
            
            long maxAttempts = 10000000; // Limit the search to 10 million attempts
            boolean found = false;
            
            System.out.println("\nSearching for the pre-image (up to " + String.format("%,d", maxAttempts) + " attempts)");
            
            long startTime = System.currentTimeMillis();
            long attempts;

            for (attempts = 0; attempts < maxAttempts; attempts++) {
                String testInput = generateSystematicString(attempts);
                
                String testHash = getSHA256Hash(testInput);
                
                if (testHash.equals(targetHash)) {
                    System.out.println("!!! Pre-image FOUND !!!");
                    System.out.println("Input: \"" + testInput + "\"");
                    found = true;
                    break;
                }
            }

            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            System.out.println("\n--- Search Complete ---");
            if (found) {
                System.out.println("Success! The pre-image was found after " + String.format("%,d", attempts) + " attempts.");
            } else {
                System.out.println("Failure. The pre-image was NOT found after " + String.format("%,d", maxAttempts) + " attempts.");
                System.out.println("This demonstrates that finding a pre-image is computationally difficult.");
            }
            System.out.println("Time taken: " + duration + " milliseconds. Or " + (duration / 1000.0) + " seconds");

        } catch (NoSuchAlgorithmException e) {
            System.err.println("SHA-256 algorithm not found.");
            e.printStackTrace();
        }
    }

    /**
     * **NEW HELPER FUNCTION**
     * Generates a systematic string based on an index and a character set.
     * This is analogous to converting a number to a different base.
     * 0 -> "a", 1 -> "b", ..., 94 -> "`", 95 -> "aa"
     * Done with help from gemini
     */
    private static String generateSystematicString(long index) {
        if (index < 0) return "";
        
        long base = CHARSET.length();
        StringBuilder sb = new StringBuilder();
        
        if (index == 0) {
            return String.valueOf(CHARSET.charAt(0));
        }

        while (index > 0) {
            int remainder = (int) (index % base);
            sb.append(CHARSET.charAt(remainder));
            index = index / base;
        }
        
        // The string is built in reverse, so we need to reverse it back.
        return sb.reverse().toString();
    }
    
    // The same hashing algorithm from before
    private static String getSHA256Hash(String input) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] encodedhash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder(2 * encodedhash.length);
        for (byte b : encodedhash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}