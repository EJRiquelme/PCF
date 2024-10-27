/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hybrid_encryption;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

/**
 *
 * @author william
 */
public class GeneratePrivateAndPublicKeys {

    private KeyPairGenerator keyGen;
    private KeyPair pair;
    private PrivateKey privateKey;
    private PublicKey publicKey;

    public GeneratePrivateAndPublicKeys(int keylength) {
        try {
            this.keyGen = KeyPairGenerator.getInstance("RSA"); // RSA: Algoritmo de encriptación asimetrica, usando la clave publica y privada para encriptar/desencriptar
            this.keyGen.initialize(keylength);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public void createKeys() {
        this.pair = this.keyGen.generateKeyPair();
        this.privateKey = pair.getPrivate();
        this.publicKey = pair.getPublic();
    }

    public PrivateKey getPrivateKey() {
        return this.privateKey;
    }

    public PublicKey getPublicKey() {
        return this.publicKey;
    }

    public void writeToFile(String path, byte[] key) throws IOException {
        FileOutputStream fos = new FileOutputStream(new File(path));
        fos.write(key);
        fos.flush();
        fos.close();
    }

    // Para acceder
    public byte[] getFileInBytes(File f) throws IOException {
        FileInputStream fis = new FileInputStream(f);
        byte[] fbytes = new byte[(int) f.length()];
        fis.read(fbytes);
        fis.close();
        return fbytes;
    }
    
/*
    public static void main(String[] args) { // Para hacer pruebas
        try {
            GeneratePrivateAndPublicKeys gkServer;

            File directory = new File("users/Paco");
            directory.mkdirs();

            gkServer = new GeneratePrivateAndPublicKeys(1024);
            gkServer.createKeys();
            gkServer.writeToFile("users/Paco/publicKey_server", gkServer.getPublicKey().getEncoded());
            gkServer.writeToFile("users/Paco/privateKey_server", gkServer.getPrivateKey().getEncoded());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
*/
}
