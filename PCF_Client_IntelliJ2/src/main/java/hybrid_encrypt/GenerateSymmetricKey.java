package hybrid_encrypt;


import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author william
 */
public class GenerateSymmetricKey {

    private SecretKeySpec secretKey;
    private byte[] secretKeyEncoded;

    public GenerateSymmetricKey(int length, String algorithm) {
        this.secretKey = new SecretKeySpec(new byte[length], algorithm);
        this.secretKeyEncoded = secretKey.getEncoded();
    }

    public SecretKeySpec getKey() {
        return this.secretKey;
    }

    public byte[] getEncodedKey() {
        return this.secretKeyEncoded;
    }
/*
    public void writeToFile(String path, byte[] key) throws IOException {
        FileOutputStream fos = new FileOutputStream(new File(path));
        fos.write(secretKeyEncoded);
        fos.flush();
        fos.close();
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchPaddingException, IOException {
        GenerateSymmetricKey genSK = new GenerateSymmetricKey(16, "AES");
        genSK.writeToFile("secretKey", genSK.getKey().getEncoded());
    }
*/
}
