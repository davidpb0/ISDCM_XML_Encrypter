/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package isdcm.xmlencrypter;

/**
 *
 * @author davidpb0
 */
import org.apache.xml.security.encryption.XMLCipher;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import javax.crypto.SecretKey;
import java.util.ArrayList;
import java.util.List;

public class Decryptor {
    private XMLCipher xmlCipher;
    private Document document;

    public Decryptor(Document document, SecretKey key) throws Exception {
        this.document = document;
        xmlCipher = XMLCipher.getInstance(XMLCipher.AES_128);
        xmlCipher.init(XMLCipher.DECRYPT_MODE, key);
    }

    public void decrypt() throws Exception {
        NodeList encryptedDataList = document.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "EncryptedData");
        List<Element> elementsToDecrypt = new ArrayList<>();
        for (int i = 0; i < encryptedDataList.getLength(); i++) {
            elementsToDecrypt.add((Element) encryptedDataList.item(i));
        }
        System.out.println("Processing " + elementsToDecrypt.size() + " encrypted elements.");
        for (Element encryptedData : elementsToDecrypt) {
            try {
                xmlCipher.doFinal(document, encryptedData);
            } catch (Exception e) {
                System.err.println("Failed to decrypt element: " + e.getMessage());
            }
        }
        System.out.println("Decryption complete.");
    }
}
