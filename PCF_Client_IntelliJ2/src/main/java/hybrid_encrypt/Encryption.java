package hybrid_encrypt;

import communication.SharedStore;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

public class Encryption {

    private GenerateSymmetricKey generateSymmetricKey;
    //private PublicKey publicServerKey;
    private Cipher cipher;
    private byte[] symmetricKey;
    private byte[] encryptedKey;
    private byte[] encryptedData;
    private SharedStore sharedStore;

    public Encryption() {
        this.generateSymmetricKey = new GenerateSymmetricKey(16, "AES");
        this.symmetricKey = generateSymmetricKey.getEncodedKey(); // Genero la Clave Simétrica
        this.sharedStore = SharedStore.getInstance();
    }

    /*
    public Encryption(byte[] publicKeyBytes) throws InvalidKeySpecException, NoSuchAlgorithmException {
        this.generateSymmetricKey = new GenerateSymmetricKey(16, "AES");
        this.publicServerKey = getPublic(publicKeyBytes, "RSA");
    }
     */
    public PublicKey getPublic(byte[] keyBytes, String algorithm) throws InvalidKeySpecException, NoSuchAlgorithmException {
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance(algorithm);
        return kf.generatePublic(spec);
    }

    public SecretKeySpec getSecretKey(String algorithm) throws IOException {
        byte[] keyBytes = generateSymmetricKey.getEncodedKey();
        return new SecretKeySpec(keyBytes, algorithm);
    }

    public Cipher getCipher() {
        return cipher;
    }

    /*
    public byte[] getEncryptedKey() {
        return encryptedKey;
    }

    public byte[] getEncryptedData() {
        return encryptedData;
    }
     */
 /*
    public void savePublicServerKey() throws IOException, InvalidKeySpecException, NoSuchAlgorithmException, Exception { // Guarda la Clave del Servidor
        this.publicServerKey = getPublic(sharedStore.getPublicServerKey(), "RSA");
    }
     */
    public byte[] encryptSymmetricKey(byte[] publicServerKey) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException {
        PublicKey serverKey = getPublic(publicServerKey, "RSA");
        this.cipher = Cipher.getInstance("RSA");
        this.cipher.init(Cipher.ENCRYPT_MODE, serverKey);
        this.encryptedKey = this.cipher.doFinal(symmetricKey);
        return encryptedKey;
    }

    public byte[] encryptData(byte[] originalData) throws NoSuchAlgorithmException, NoSuchPaddingException, IOException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        this.cipher = Cipher.getInstance("AES");
        this.cipher.init(Cipher.ENCRYPT_MODE, getSecretKey("AES"));
        this.encryptedData = this.cipher.doFinal(originalData);
        return encryptedData;
    }

    /*
    public KeyData encrypt(byte[] originalData) throws IOException, GeneralSecurityException, Exception {
        // Encripta la Clave simétrica
        this.cipher = Cipher.getInstance("RSA");
        this.cipher.init(Cipher.ENCRYPT_MODE, publicServerKey);
        this.encryptedKey = this.cipher.doFinal(generateSymmetricKey.getEncodedKey());

        // Encripta la información
        this.cipher = Cipher.getInstance("AES");
        this.cipher.init(Cipher.ENCRYPT_MODE, getSecretKey("AES"));
        this.encryptedData = this.cipher.doFinal(originalData);

        return new KeyData(encryptedKey, encryptedData);
    }

    public KeyData testing(String account, String text) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException, Exception { // Guarda la Clave del Servidor
        File f = new File("accounts" + File.separator + account + File.separator + "publicKey_server");
        this.publicServerKey = getPublic(getFileInBytes(f), "RSA");
        KeyData kd = encrypt(text.getBytes());
        return kd;
    }

    public byte[] getFileInBytes(File f) throws IOException {
        FileInputStream fis = new FileInputStream(f);
        byte[] fbytes = new byte[(int) f.length()];
        fis.read(fbytes);
        fis.close();
        return fbytes;
    }
     */
    public byte[] getSymmetricKey() {
        return symmetricKey;
    }

}
