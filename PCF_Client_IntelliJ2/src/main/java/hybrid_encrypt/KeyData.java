package hybrid_encrypt;


public class KeyData {

    private byte[] encryptedSessionKey;
    private byte[] encryptedData;

    public KeyData(byte[] encryptedSessionKey, byte[] encryptedData) {
        this.encryptedSessionKey = encryptedSessionKey;
        this.encryptedData = encryptedData;
    }

    public byte[] getEncryptedSessionKey() {
        return encryptedSessionKey;
    }

    public void setEncryptedSessionKey(byte[] encryptedSessionKey) {
        this.encryptedSessionKey = encryptedSessionKey;
    }

    public byte[] getEncryptedData() {
        return encryptedData;
    }

    public void setEncryptedData(byte[] encryptedData) {
        this.encryptedData = encryptedData;
    }

}
