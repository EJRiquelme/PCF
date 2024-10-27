/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hybrid_encryption;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author william
 */
public class Decryption {

    private Cipher cipher;
    private byte[] decryptKey;
    private byte[] decryptData;

    public Decryption() {

    }

    public byte[] getDecryptKey() {
        return decryptKey;
    }

    public byte[] getDecryptData() {
        return decryptData;
    }
/*
    public PrivateKey getPrivate(String filename, String algorithm) throws Exception {
        byte[] keyBytes = Files.readAllBytes(new File(filename).toPath());
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance(algorithm);
        return kf.generatePrivate(spec);
    }

    public PublicKey getPublic(String filename, String algorithm) throws Exception {
        byte[] keyBytes = Files.readAllBytes(new File(filename).toPath());
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance(algorithm);
        return kf.generatePublic(spec);
    }
*/
    public SecretKeySpec getSecretKey(byte[] keyBytes, String algorithm) throws IOException {
        //byte[] keyBytes = Files.readAllBytes(new File(filename).toPath());
        return new SecretKeySpec(keyBytes, algorithm);
    }
/*
    public void decrypt(String path, byte[] encryptedSymmetricalKey, byte[] encryptedData) throws IOException, GeneralSecurityException, Exception {
        Decryption startEnc = new Decryption();

        // Desencripta la Clave
        this.cipher = Cipher.getInstance("RSA");
        this.cipher.init(Cipher.DECRYPT_MODE, startEnc.getPrivate(path + "privateKey_server", "RSA"));
        decryptKey = this.cipher.doFinal(encryptedSymmetricalKey);

        // Desencripta la información
        this.cipher = Cipher.getInstance("AES");
        this.cipher.init(Cipher.DECRYPT_MODE, getSecretKey(decryptKey, "AES"));
        decryptData = this.cipher.doFinal(encryptedData);
        
        File encryptedFileReceived = new File("EncryptedFiles/encryptedFile");
        File decryptedFile = new File("DecryptedFiles/decryptedFile");
        new DecryptData(encryptedFileReceived, decryptedFile, startEnc.getSecretKey("DecryptedFiles/SecretKey", "AES"), "AES");
    }
*/
    public PrivateKey getPrivateKey(byte[] keyBytes, String algorithm) throws Exception {
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance(algorithm);
        return kf.generatePrivate(spec);
    }

    public PublicKey getPublicKey(byte[] keyBytes, String algorithm) throws Exception {
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance(algorithm);
        return kf.generatePublic(spec);
    }

    public byte[] decryptSymmetricKey(byte[] privateKey, byte[] encryptedSymmetricKey) {
        try {
            Decryption startEnc = new Decryption();

            // Desencripta la Clave Simétrica
            this.cipher = Cipher.getInstance("RSA");
            this.cipher.init(Cipher.DECRYPT_MODE, startEnc.getPrivateKey(privateKey, "RSA"));
            decryptKey = this.cipher.doFinal(encryptedSymmetricKey);
            
            return decryptKey;
        } catch (Exception ex) {
            Logger.getLogger(Decryption.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public byte[] decryptData(byte[] symmetricalKey, byte[] encryptedData) {
        try {
            Decryption startEnc = new Decryption();

            // Desencripta la información
            this.cipher = Cipher.getInstance("AES");
            this.cipher.init(Cipher.DECRYPT_MODE, getSecretKey(symmetricalKey, "AES"));
            decryptData = this.cipher.doFinal(encryptedData);
            
            return decryptData;
        } catch (Exception ex) {
            Logger.getLogger(Decryption.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
