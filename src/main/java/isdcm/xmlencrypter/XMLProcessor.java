/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package isdcm.xmlencrypter;

/**
 *
 * @author davidpb0
 */
import org.apache.xml.security.Init;
import org.apache.xml.security.utils.EncryptionConstants;
import org.w3c.dom.Document;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.Scanner;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class XMLProcessor {

    public static void main(String[] args) {
        System.out.println("Welcome to the XML Encrypter/Decrypter!");
        Init.init();  // Initialize the security library
        Scanner scanner = new Scanner(System.in);
        Document document = null;
        SecretKey key = new SecretKeySpec("1234567890123456".getBytes(), "AES");  // 128 bit key

        File file = getUserFile(scanner);
        if (file == null) {
            scanner.close();
            return;
        }

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.parse(file);

            String action = getUserAction(scanner);
            if (action.equals("E")) {
                Encryptor encryptor = new Encryptor(document, key);
                if (getUserEncryptChoice(scanner).equalsIgnoreCase("Whole")) {
                    encryptor.encryptWholeDocument();
                } else {
                    String[] tags = getUserTagsToProcess(scanner, "encrypt");
                    encryptor.encryptSpecificElements(tags, "urn:mpeg:mpeg21:2002:02-DIDL-NS");
                }
            } else if (action.equals("D")) {
                Decryptor decryptor = new Decryptor(document, key);
                decryptor.decrypt();
            }

            saveDocument(document, file);
        } catch (Exception e) {
            System.out.println("Error during processing:");
            System.err.println("Failed to process XML: " + e.getMessage());
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }

    private static File getUserFile(Scanner scanner) {
        File file;
        while (true) {
            System.out.println("Enter the filename (e.g., /path/to/your/file.xml):");
            String filePath = scanner.nextLine();
            file = new File(filePath);
            if (file.exists()) {
                return file;
            }
            System.out.println("File does not exist. Please enter a valid file path.");
        }
    }

    private static String getUserAction(Scanner scanner) {
        while (true) {
            System.out.println("Do you want to (E)ncrypt or (D)ecrypt a file? Enter 'E' or 'D', or 'Q' to quit:");
            String action = scanner.nextLine().trim().toUpperCase();
            if (action.equals("Q")) {
                System.out.println("Exiting program.");
                System.exit(0);
            } else if (action.equals("E") || action.equals("D")) {
                return action;
            }
            System.out.println("Invalid action. Please enter 'E', 'D', or 'Q'.");
        }
    }

    private static String getUserEncryptChoice(Scanner scanner) {
        System.out.println("Do you want to encrypt the whole file or specific elements? Enter 'Whole' or 'Elements':");
        return scanner.nextLine().trim();
    }

    private static String[] getUserTagsToProcess(Scanner scanner, String processType) {
        System.out.println("Enter element tag names to " + processType + ", separated by commas (e.g., metadata):");
        return scanner.nextLine().split(",");
    }

    private static void saveDocument(Document document, File file) throws Exception {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(file);
        transformer.transform(source, result);
    }
}
