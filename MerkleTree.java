import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MerkleTree {

    // A small inner class to hold a proof hash and its position (left/right)
    static class ProofNode {
        String hash;
        Position position;

        enum Position { LEFT, RIGHT }

        ProofNode(String hash, Position position) {
            this.hash = hash;
            this.position = position;
        }

        @Override
        public String toString() {
            return "ProofNode{" + "hash='" + hash.substring(0, 10) + "..., position=" + position + '}';
        }
    }

    public static void main(String[] args) {
        List<String> transactions = Arrays.asList("TxA", "TxB", "TxC", "TxD", "TxE");
        
        // 1. Construct the tree and get the root
        System.out.println("--- 1. Merkle Tree Construction ---");
        String merkleRoot = buildMerkleRoot(transactions);
        System.out.println("Original Transactions: " + transactions);
        System.out.println("Merkle Root: " + merkleRoot);
        System.out.println("-------------------------------------\n");

        // 2. Generate a Merkle Proof for a specific transaction ("TxC")
        System.out.println("--- 2. Merkle Proof Generation ---");
        String itemToProve = "TxC";
        List<ProofNode> proof = generateMerkleProof(transactions, itemToProve);
        System.out.println("Generating proof for: '" + itemToProve + "'");
        System.out.println("Generated Proof Path: " + proof);
        System.out.println("-------------------------------------\n");
        
        // 3. Verify the Merkle Proof
        System.out.println("--- 3. Merkle Proof Verification ---");
        // Test Case 1: Verification should succeed with correct data
        boolean isValid = verifyMerkleProof(itemToProve, proof, merkleRoot);
        System.out.println("Verifying proof for '" + itemToProve + "' against the correct root...");
        System.out.println("Verification Result: " + (isValid ? "SUCCESS" : "FAILURE"));
        
        // Test Case 2: Verification should fail with incorrect data
        boolean isInvalid = verifyMerkleProof("FakeTx", proof, merkleRoot);
        System.out.println("\nVerifying proof for 'FakeTx' against the correct root...");
        System.out.println("Verification Result: " + (isInvalid ? "SUCCESS" : "FAILURE"));
        System.out.println("-------------------------------------\n");
    }

    /**
     * Generates a Merkle proof for a specific data item.
     * The proof is the list of sibling hashes needed to reconstruct the root.
     */
    public static List<ProofNode> generateMerkleProof(List<String> dataItems, String itemToProve) {
        List<ProofNode> proof = new ArrayList<>();
        String itemHash = getSHA256Hash(itemToProve);

        // We need all levels of the tree to find the siblings.
        List<List<String>> allLevels = new ArrayList<>();
        List<String> currentLevel = new ArrayList<>();
        for (String item : dataItems) {
            currentLevel.add(getSHA256Hash(item));
        }
        allLevels.add(currentLevel);

        while (currentLevel.size() > 1) {
            currentLevel = buildNextLevel(currentLevel);
            allLevels.add(currentLevel);
        }

        // Find the index of our item's hash in the leaf level.
        int itemIndex = -1;
        for(int i=0; i < allLevels.get(0).size(); i++) {
            if(allLevels.get(0).get(i).equals(itemHash)){
                itemIndex = i;
                break;
            }
        }
        if(itemIndex == -1) return null; // Item not in the list

        // Iterate up the tree from the leaf level to collect proof nodes.
        for (int level = 0; level < allLevels.size() - 1; level++) {
            List<String> currentLevelNodes = allLevels.get(level);
            String siblingHash;
            ProofNode.Position siblingPosition;

            // Check if the index is even or odd to find the sibling.
            if (itemIndex % 2 == 0) {
                // Sibling is to the right.
                siblingPosition = ProofNode.Position.RIGHT;
                // Handle the odd-node case where the sibling is the node itself.
                if (itemIndex + 1 >= currentLevelNodes.size()) {
                    siblingHash = currentLevelNodes.get(itemIndex);
                } else {
                    siblingHash = currentLevelNodes.get(itemIndex + 1);
                }
            } else {
                // Sibling is to the left.
                siblingPosition = ProofNode.Position.LEFT;
                siblingHash = currentLevelNodes.get(itemIndex - 1);
            }
            
            proof.add(new ProofNode(siblingHash, siblingPosition));
            // Move up to the parent's index for the next level.
            itemIndex = itemIndex / 2;
        }

        return proof;
    }

    /**
     * Verifies a Merkle proof against a known Merkle root.
     */
    public static boolean verifyMerkleProof(String item, List<ProofNode> proof, String merkleRoot) {
        if(proof == null) return false;
        
        // 1. Start with the hash of the item itself.
        String computedHash = getSHA256Hash(item);

        // 2. Loop through the proof, combining and hashing at each step.
        for (ProofNode proofNode : proof) {
            if (proofNode.position == ProofNode.Position.LEFT) {
                computedHash = getSHA256Hash(proofNode.hash + computedHash);
            } else { // RIGHT
                computedHash = getSHA256Hash(computedHash + proofNode.hash);
            }
        }

        // 3. Compare the final computed hash with the expected Merkle root.
        return computedHash.equals(merkleRoot);
    }

    // --- Helper methods from the previous steps and questions ---
    public static String buildMerkleRoot(List<String> dataItems) {
        if (dataItems == null || dataItems.isEmpty()) return null;
        List<String> levelHashes = new ArrayList<>();
        for (String item : dataItems) {
            levelHashes.add(getSHA256Hash(item));
        }
        while (levelHashes.size() > 1) {
            levelHashes = buildNextLevel(levelHashes);
        }
        return levelHashes.get(0);
    }

    private static List<String> buildNextLevel(List<String> levelHashes) {
        List<String> nextLevelHashes = new ArrayList<>();
        for (int i = 0; i < levelHashes.size(); i += 2) {
            String left = levelHashes.get(i);
            String right = (i + 1 < levelHashes.size()) ? levelHashes.get(i + 1) : left;
            nextLevelHashes.add(getSHA256Hash(left + right));
        }
        return nextLevelHashes;
    }

    private static String getSHA256Hash(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder(2 * encodedhash.length);
            for (byte b : encodedhash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}