/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 *
 * @author william
 */
public class ReadJsonFromUrl {

    private String townshipsUrl;
    private String provincesUrl;
    private TreeMap<Province, TreeSet<Township>> provincesTownships;
    private TreeMap<String, Province> keys;

    // CONSTRUCTOR
    public ReadJsonFromUrl() {
        this.townshipsUrl = "https://raw.githubusercontent.com/IagoLast/pselect/master/data/municipios.json";
        this.provincesUrl = "https://raw.githubusercontent.com/IagoLast/pselect/master/data/provincias.json";
        this.provincesTownships = new TreeMap();
        this.keys = new TreeMap();
    }

    // GETTERS & SETTERS
    public TreeMap<Province, TreeSet<Township>> getProvincesTownships() {
        return provincesTownships;
    }

    public void setProvincesTownships(TreeMap<Province, TreeSet<Township>> provinciaMunicipios) {
        this.provincesTownships = provinciaMunicipios;
    }

    public TreeMap<String, Province> getKeys() {
        return keys;
    }

    public void setKeys(TreeMap<String, Province> keys) {
        this.keys = keys;
    }
    
    public TreeSet<Township> getMunicipios(Province provincia){
        return provincesTownships.get(provincia);
    }

    // OPERATIONS
    public void readProvincesJsonFromUrl(String link) throws IOException {
        InputStream input = null;
        BufferedReader br = null;

        try {
            input = new URL(link).openStream();
            br = new BufferedReader(new InputStreamReader(input, Charset.forName("UTF-8")));

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
        } finally {
            br.close();
            input.close();
        }
    }

    public void readTownshipsJsonFromUrl(String link) throws IOException {
        InputStream input = null;
        BufferedReader br = null;

        try {
            input = new URL(link).openStream();
            br = new BufferedReader(new InputStreamReader(input, Charset.forName("UTF-8")));

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
        } finally {
            br.close();
            input.close();
        }
    }
/*
    public void probando() {
        for (Map.Entry<Provincia, TreeSet<Municipio>> entry : provinciaMunicipios.entrySet()) {
            System.out.println("Province: " + entry.getKey().getName());

            for (Township entrada : entry.getValue()) {
                System.out.println("\tMunicipio: " + entrada.getName());
            }
        }
    }
*/
    public void read() {
        try {
            readProvincesJsonFromUrl(provincesUrl);
            readTownshipsJsonFromUrl(townshipsUrl);
            //probando();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
