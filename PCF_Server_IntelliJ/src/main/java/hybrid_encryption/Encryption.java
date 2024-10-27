/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hybrid_encryption;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author william
 */
public class Encryption {

    private PublicKey publicServerKey;
    private Cipher cipher;
    private byte[] symmetricKey;
    private byte[] encryptedKey;
    private byte[] encryptedData;

    public Cipher getCipher(){
        return cipher;
    }
    
    public SecretKeySpec getSecretKey(String algorithm, byte[] sessionKey) throws IOException {
        return new SecretKeySpec(sessionKey, algorithm);
    }
    
    public byte[] encryptData(byte[] originalData, byte[] sessionKey) throws NoSuchAlgorithmException, NoSuchPaddingException, IOException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        this.cipher = Cipher.getInstance("AES");
        this.cipher.init(Cipher.ENCRYPT_MODE, getSecretKey("AES", sessionKey));
        this.encryptedData = this.cipher.doFinal(originalData);
        return encryptedData;
    }

}
