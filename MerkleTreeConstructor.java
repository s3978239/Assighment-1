import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MerkleTreeConstructor {

    public static void main(String[] args) {
        // --- Test Case 1: Even number of transactions ---
        List<String> transactionsEven = Arrays.asList(
            "TxA: Alice sends 1 BTC to Bob",
            "TxB: Bob sends 0.5 BTC to Charlie",
            "TxC: Charlie sends 0.2 BTC to David",
            "TxD: David sends 0.1 BTC to Alice"
        );
        System.out.println("--- Building Merkle Tree for 4 (Even) Transactions ---");
        String merkleRootEven = buildMerkleRoot(transactionsEven);
        System.out.println("Original Transactions: " + transactionsEven);
        System.out.println("Calculated Merkle Root: " + merkleRootEven);
        System.out.println();

        // --- Test Case 2: Odd number of transactions ---
        List<String> transactionsOdd = Arrays.asList(
            "Tx1: Order 123",
            "Tx2: Order 456",
            "Tx3: Order 789",
            "Tx4: Order 101",
            "Tx5: Order 112"
        );
        System.out.println("--- Building Merkle Tree for 5 (Odd) Transactions ---");
        String merkleRootOdd = buildMerkleRoot(transactionsOdd);
        System.out.println("Original Transactions: " + transactionsOdd);
        System.out.println("Calculated Merkle Root: " + merkleRootOdd);
    }

    /**
     * Constructs a Merkle Tree from a list of data items and returns the Merkle Root.
     * @param dataItems The list of data items (e.g., transactions).
     * @return The Merkle Root hash as a hex string.
     * THis part is done with the help of Gemini
     */
    public static String buildMerkleRoot(List<String> dataItems) {
        // Handle empty or single-item lists as edge cases.
        if (dataItems == null || dataItems.isEmpty()) {
            return null;
        }
        if (dataItems.size() == 1) {
            return getSHA256Hash(dataItems.get(0));
        }

        // 1. Create the initial list of leaf hashes.
        List<String> levelHashes = new ArrayList<>();
        for (String item : dataItems) {
            levelHashes.add(getSHA256Hash(item));
        }

        // 2. Build the tree level by level until only one root hash remains.
        while (levelHashes.size() > 1) {
            List<String> nextLevelHashes = new ArrayList<>();
            // Iterate through the current level in pairs.
            for (int i = 0; i < levelHashes.size(); i += 2) {
                String leftHash = levelHashes.get(i);
                String rightHash;

                // Check if a right-hand pair exists.
                if (i + 1 < levelHashes.size()) {
                    rightHash = levelHashes.get(i + 1);
                } else {
                    // This is an odd-numbered level. Duplicate the last hash. 
                    rightHash = leftHash;
                }

                // Combine and hash the pair to create the parent hash.
                String parentHash = getSHA256Hash(leftHash + rightHash);
                nextLevelHashes.add(parentHash);
            }
            // Move to the next level up.
            levelHashes = nextLevelHashes;
        }

        // 3. The last remaining hash is the Merkle Root.
        return levelHashes.get(0);
    }

    /**
     * Helper function to compute the SHA-256 hash for a given input string.
     * @param input The string to hash.
     * @return The SHA-256 hash as a 64-character hexadecimal string.
     */

     //The same one used in HashFunctionDemo.java
    private static String getSHA256Hash(String input) {
        try {
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
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}