package isdcm.xmlencrypter;

import org.apache.xml.security.encryption.XMLCipher;
import org.apache.xml.security.utils.EncryptionConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.xml.security.Init;
import org.w3c.dom.NodeList;


/**
 *
 * @author davidpb0
 */
public class XMLEncrypter {
    
     public static void main(String[] args) {
        System.out.println("Welcome to the XML Encrypter/Decrypter!");
        Init.init(); // Initialize the security library

        Scanner scanner = new Scanner(System.in);
        String action;
        String filePath;
        File file;
        
        while (true) {
            System.out.println("Do you want to (E)ncrypt or (D)ecrypt a file? Enter 'E' or 'D', or 'Q' to quit:");
            action = scanner.nextLine().trim().toUpperCase();
            if (action.equals("Q")) {
                System.out.println("Exiting program.");
                return;
            } else if (!action.equals("E") && !action.equals("D")) {
                System.out.println("Invalid action. Please enter 'E', 'D', or 'Q'.");
                continue;
            }
            System.out.println("Enter the filename (e.g., /path/to/your/file.xml):");
            filePath = scanner.nextLine();
            file = new File(filePath);
            if (!file.exists()) {
                System.out.println("File does not exist. Please enter a valid file path.");
                continue;
            }
            break;
        }

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);

        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(file);

            String symmetricKeyString = "1234567890123456"; // 128 bit key
            SecretKey symmetricKey = new SecretKeySpec(symmetricKeyString.getBytes(), "AES");

            XMLCipher xmlCipher = XMLCipher.getInstance(XMLCipher.AES_128);

            if (action.equals("E")) {
                System.out.println("Do you want to encrypt the whole file or specific elements? Enter 'Whole' or 'Elements':");
                String encryptChoice = scanner.nextLine().trim();
                if (encryptChoice.equalsIgnoreCase("Whole")) {
                    xmlCipher.init(XMLCipher.ENCRYPT_MODE, symmetricKey);
                    xmlCipher.doFinal(document, document.getDocumentElement(), false); // Encrypt the entire document
                    System.out.println("The entire file has been encrypted successfully.");
                } else {
                    System.out.println("Enter element tag names to encrypt, separated by commas (e.g., metadata):");
                    String[] tagsToEncrypt = scanner.nextLine().split(",");
                    xmlCipher.init(XMLCipher.ENCRYPT_MODE, symmetricKey);
                    for (String tag : tagsToEncrypt) {
                        NodeList elements = document.getElementsByTagNameNS("urn:mpeg:mpeg21:2002:02-DIDL-NS", tag.trim());
                        for (int i = 0; i < elements.getLength(); i++) {
                            Element element = (Element) elements.item(i);
                            xmlCipher.doFinal(document, element, false); // Encrypting only the content of each element
                        }
                    }
                    System.out.println("Specified elements have been encrypted successfully.");
                }
            } else if (action.equals("D")) {
                xmlCipher.init(XMLCipher.DECRYPT_MODE, symmetricKey);
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

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(file);
            transformer.transform(source, result);

        } catch (Exception e) {
            System.out.println("Error during processing:");
            System.err.println("Failed to process XML: " + e.getMessage());
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }

}
