/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tools;

import communication.SharedStore;
import javafx.scene.control.Alert;

import java.io.*;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * @author william
 */
public class ReadJsonFromFile {

    private SharedStore sharedStore;
    private final String provincesFile;
    private final String townshipsFile;
    private TreeMap<Province, TreeSet<Township>> provincesTownships;
    private TreeMap<String, Province> keys;

    // CONSTRUCTOR
    public ReadJsonFromFile() {
        this.sharedStore = SharedStore.getInstance();

        // La ruta en la que se ejecuta el .jar es: "/home/william". Ergo es mejor usar rutas absolutas

        // Ruta IntelliJ
        //this.provincesFile = "." + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "pcf_client/executables/provincias";
        //this.townshipsFile = "." + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "pcf_client/executables/municipios";

        // Ruta .jar
        //this.provincesFile = "." + File.separator + "D.A.M" + File.separator + "Curso 2021 - 2022" + File.separator + "Proyecto" + File.separator + "Buscando Soluciones IntelliJ" + File.separator + "provincias.json";
        //this.townshipsFile = "." + File.separator + "D.A.M" + File.separator + "Curso 2021 - 2022" + File.separator + "Proyecto" + File.separator + "Buscando Soluciones IntelliJ" + File.separator + "municipios.json";

        this.provincesFile = File.separator + "home" + File.separator + "william" + File.separator + "D.A.M" + File.separator + "Curso 2021 - 2022" + File.separator + "Proyecto" + File.separator + "Buscando Soluciones IntelliJ" + File.separator + "provincias.json";
        this.townshipsFile = File.separator + "home" + File.separator + "william" + File.separator + "D.A.M" + File.separator + "Curso 2021 - 2022" + File.separator + "Proyecto" + File.separator + "Buscando Soluciones IntelliJ" + File.separator + "municipios.json";

        this.provincesTownships = new TreeMap();
        this.keys = new TreeMap();
    }

    // GETTERS
    public TreeMap<String, Province> getKeys() {
        return keys;
    }

    public TreeMap<Province, TreeSet<Township>> getProvincesTownships() {
        return provincesTownships;
    }

    // OPERATIONS
    public void readProvincesJsonFromFile(String file) throws IOException {
        BufferedReader br = null;

        try {
            br = new BufferedReader(new FileReader(file));

            Province provincia = null;
            TreeSet<Township> municipios = null;

            String texto = "";
            while ((texto = br.readLine()) != null) {
                String[] data = texto.split("\"");

                if (texto.equals("[{")) {
                } else if (texto.equals("}, {") || texto.equals("}]")) { // Fin del JsonObject
                    municipios = new TreeSet(); // Lista vacia de Municipios
                    provincesTownships.put(provincia, municipios);
                    keys.put(provincia.getId(), provincia);
                } else {
                    if (data[1].equals("id")) {
                        provincia = new Province(data[3]);
                    } else {
                        provincia.setName(data[3]);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            sharedStore.getApp().alertToHuman(Alert.AlertType.ERROR,
                    "ERROR",
                    "Archivo no encontrado.",
                    "No se ha encontrado el archivo Json.");
            throw new RuntimeException(e);
        } catch (IOException e) {
            sharedStore.getApp().alertToHuman(Alert.AlertType.ERROR,
                    "ERROR",
                    "Lectura de provincias fallida.",
                    "No se ha podido leer el archivo json correctamente.");
            throw new RuntimeException(e);
        } finally {
            br.close();
        }
    }

    public void readTownshipsJsonFromFile(String file) throws IOException {
        BufferedReader br = null;

        try {
            br = new BufferedReader(new FileReader(file));
            Township municipio = null;

            String texto = "";
            while ((texto = br.readLine()) != null) {
                String[] data = texto.split("\"");

                if (texto.equals("[{")) {
                } else if (texto.equals("}, {") || texto.equals("}]")) {
                    String idProvincia = municipio.getId().substring(0, 2);
                    Province provincia = keys.get(idProvincia);
                    provincesTownships.get(provincia).add(municipio);
                } else {
                    if (data[1].equals("id")) {
                        municipio = new Township(data[3]);
                    } else {
                        municipio.setName(data[3]);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            sharedStore.getApp().alertToHuman(Alert.AlertType.ERROR,
                    "ERROR",
                    "Archivo no encontrado.",
                    "No se ha encontrado el archivo Json.");
            throw new RuntimeException(e);
        } catch (IOException e) {
            sharedStore.getApp().alertToHuman(Alert.AlertType.ERROR,
                    "ERROR",
                    "Lectura de municipios fallida.",
                    "No se ha podido leer el archivo json correctamente.");
            throw new RuntimeException(e);
        } finally {
            br.close();
        }
    }

    public void read() {
        try {
            readProvincesJsonFromFile(provincesFile);
            readTownshipsJsonFromFile(townshipsFile);
        } catch (IOException e) {
            sharedStore.getApp().alertToHuman(Alert.AlertType.ERROR,
                    "ERROR",
                    "Cierre de lectura fallida.",
                    "Aunque el archivo se ha leído satisfactoriamente, el flujo de lectura no ha podido cerrarse correctamente.");
        }
    }

}
