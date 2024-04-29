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

public class Encryptor {
    private XMLCipher xmlCipher;
    private Document document;

    public Encryptor(Document document, SecretKey key) throws Exception {
        this.document = document;
        xmlCipher = XMLCipher.getInstance(XMLCipher.AES_128);
        xmlCipher.init(XMLCipher.ENCRYPT_MODE, key);
    }

    public void encryptWholeDocument() throws Exception {
        xmlCipher.doFinal(document, document.getDocumentElement(), false);
        System.out.println("The entire file has been encrypted successfully.");
    }

    public void encryptSpecificElements(String[] tagsToEncrypt, String namespaceURI) throws Exception {
        for (String tag : tagsToEncrypt) {
            NodeList elements = document.getElementsByTagNameNS(namespaceURI, tag.trim());
            for (int i = 0; i < elements.getLength(); i++) {
                Element element = (Element) elements.item(i);
                xmlCipher.doFinal(document, element, false);
            }
        }
        System.out.println("Specified elements have been encrypted successfully.");
    }
}
