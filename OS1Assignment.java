import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OS1Assignment {
    public static void main(String[] args) {
        if (args.length != 1) {
            return;
        }

        String inputFile = args[0];

        // Constants
        Map<Integer, Integer> dict = new HashMap<>();
        dict.put(0, 2);
        dict.put(1, 4); 
        dict.put(2, 1);
        dict.put(3, 7);
        dict.put(4, 3);
        dict.put(5, 5);
        dict.put(6, 6);
        int pageSize = 128;
        int frameNumber = 0;

        // Method to decode and read binary elements into a list (hexList)
        List<String> hexList = new ArrayList<>();
        List<String> newHexList = new ArrayList<>();
        try {
            byte[] binaryData = Files.readAllBytes(Paths.get(inputFile));
            StringBuilder hexString = new StringBuilder();
            for (byte b : binaryData) {
                hexString.append(String.format("%02x", b));
            }
            for (int i = 0; i < hexString.length(); i += 2) {
                hexList.add(hexString.substring(i, i + 2));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Method to remove the leading zeros from each element
        for (int i = hexList.size() - 1; i >= 0; i--) {
            String str = hexList.get(i);
            if (str.charAt(0) == '0') {
                newHexList.add(str.substring(1));
            } else {
                newHexList.add(str);
            }
        }

        // Method to get the values of the first 8-bits
        String text = " ";
        String newText = " ";
        int count = 0;
        int defaults = 0;
        String default_text = " ";
        for (String hexByte : newHexList) {
            text = text + hexByte;
            count = count + 1;
            if (count == 8) {
                count = defaults;
                newText = newText + text + " ";
                text = default_text;
            }
        }
        //System.out.println(newText);

        // Algorithm for paging
        List<String> physicalAddressList = new ArrayList<>();
        List<String> virtualAddressList = new ArrayList<>(List.of(newText.split(" ")));
        Collections.reverse(virtualAddressList);

        for (String virtualAddress : virtualAddressList) {
            if (!virtualAddress.isEmpty()) {  // Check for empty strings
                int virtualAddressInt = Integer.parseInt(virtualAddress, 16);
                int pageNumber = virtualAddressInt / pageSize;
                for (Map.Entry<Integer, Integer> entry : dict.entrySet()) {
                    if (entry.getKey() == pageNumber) {
                        frameNumber = entry.getValue();
                        break;
                    }
                }
                int offset = virtualAddressInt % pageSize;
                int physicalAddress = (frameNumber * pageSize) + offset;
                physicalAddressList.add(Integer.toHexString(physicalAddress));
            }
        }

        // Write to file
        try (PrintWriter writer = new PrintWriter("output-OS1")) {
            for (String physicalAddress : physicalAddressList) {
                writer.print("0x" + physicalAddress.toUpperCase() + " ");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}







