//Asked Gemini for library requirements
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner; 

public class HashFunctionDemo {

    public static void main(String[] args) {
        // Create a Scanner object to read user input
        Scanner scanner = new Scanner(System.in);
        
        // 1. Take an arbitrary string as input from the user.
        System.out.println("Enter a string to hash:");
        String originalInput = scanner.nextLine();
        
        try {
            // 2. Computes and displays its hash. 
            String originalHash = getSHA256Hash(originalInput);
            System.out.println("\nOriginal String: \"" + originalInput + "\"");
            System.out.println("SHA-256 Hash: " + originalHash);
            
            // 3. Demonstrate the avalanche effect 
            // Make a minimal change to the original string
            String modifiedInput = originalInput + "."; // Simply add a period
            
            // Compute the hash of the modified string
            String modifiedHash = getSHA256Hash(modifiedInput);
            
            // Display the Avalanche Effect
            System.out.println("\n--- Demonstrating Avalanche Effect ---");
            System.out.println("Modified String: \"" + modifiedInput + "\" (just one character added)");
            System.out.println("SHA-256 Hash: " + modifiedHash);
            System.out.println("\nNote how a tiny change to the input results in a completely different hash.");
            System.out.println("\nHamming distance in string: "+hammingDist(originalHash,modifiedHash)+"/"+originalHash.length());

            //Error block in case of library wasn't loaded
        } catch (NoSuchAlgorithmException e) {
            System.err.println("SHA-256 algorithm not found. Please check your Java environment.");
            e.printStackTrace();
        }
        
        scanner.close();
    }


    //Due to the prerequiste of byte type input for MessageDigest, Had Gemini help debug the string to byte conversion.
    /**
     * Computes the SHA-256 hash for a given input string.
     * @param input The string to hash.
     * @return The 32-byte array SHA-256 hash as a 64-character hexadecimal string.
     * @throws NoSuchAlgorithmException If the SHA-256 algorithm is not available.
     */
    private static String getSHA256Hash(String input) throws NoSuchAlgorithmException {
        // Get an instance of the SHA-256 message digest algorithm
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        
        // Compute the hash. The input string is converted to bytes using UTF-8 encoding.
        byte[] encodedhash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
        
        // The hash is in bytes, so we convert it to a hexadecimal string for display
        StringBuilder hexString = new StringBuilder(2 * encodedhash.length);
        for (byte b : encodedhash) {
            String hex = Integer.toHexString(0xff & b); /*toHexString requires a 32 bit Signed number. 
                                                        so an ff would be intreperted as ffffffff due to the java filling it with the sign bits. 
                                                        TO make this work, 0xff & b is used (& is an AND bitwise operator) to effectively "masking" the byte. This operation isolates only the last 8 bits of the value and discards any extended sign bits. 
                                                        Guess we know why you'd recommend python lol
                                                        Heck, .toHexString use int, not byte! and uh... conversion so when converting to int (32 bit signed), they add those uh... sign bit.*/
                                                        
            if (hex.length() == 1) {
                hexString.append('0'); //if hex is only a character long, append a 0 in hexString before it
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
    
    // function to calculate Hamming distance
    static int hammingDist(String str1, String str2)
    {
        int i = 0, count = 0;
        while (i < str1.length()) {
            if (str1.charAt(i) != str2.charAt(i))
                count++;
            i++;
        }
        return count;
    }
}

