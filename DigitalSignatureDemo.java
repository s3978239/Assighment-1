import java.security.*;
import java.util.Base64;
import java.util.Scanner;

public class DigitalSignatureDemo {

    public static void main(String[] args) throws Exception {

        // 1. Generate a public-private key pair using RSA algorithm.
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048); // Use a 2048-bit key size for security
        KeyPair keyPair = keyGen.generateKeyPair();
        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();

        // 2. Take a message string as input.
        System.out.println("Enter a message to sign:");
        Scanner scanner = new Scanner(System.in);
        String message = scanner.nextLine();
        scanner.close();

        // 3. Sign the message using the private key.
        // The "SHA256withRSA" algorithm hashes the message with SHA-256 then signs the hash with RSA.
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(message.getBytes());
        byte[] digitalSignature = signature.sign();

        // 4. Verify the signature against the original message using the public key.
        signature.initVerify(publicKey);
        signature.update(message.getBytes());
        boolean isVerified = signature.verify(digitalSignature);

        // --- Output all the information ---
        System.out.println("\n--- Digital Signature Demonstration ---");
        // For academic purposes, we print the private key. In real applications, this MUST be kept secret.
        System.out.println("Private Key: " + Base64.getEncoder().encodeToString(privateKey.getEncoded()).substring(0, 60) + "...");
        System.out.println("Public Key: " + Base64.getEncoder().encodeToString(publicKey.getEncoded()).substring(0, 60) + "...");
        System.out.println("\nOriginal Message: " + message);
        System.out.println("Digital Signature: " + Base64.getEncoder().encodeToString(digitalSignature));
        System.out.println("\nVerification Result: " + (isVerified ? "SIGNATURE VERIFIED" : "SIGNATURE FAILED"));
    }
}