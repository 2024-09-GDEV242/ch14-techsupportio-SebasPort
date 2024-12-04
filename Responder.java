import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.*;

/**
 * The responder class represents a response generator object.
 * It is used to generate an automatic response, based on specified input.
 * Input is presented to the responder as a set of words, and based on those
 * words the responder will generate a String that represents the response.
 *
 * Internally, the reponder uses a HashMap to associate words with response
 * strings and a list of default responses. If any of the input words is found
 * in the HashMap, the corresponding response is returned. If none of the input
 * words is recognized, one of the default responses is randomly chosen.
 * 
 * @author Sebastian portillo.
 * @version 12/4/24
 */
public class Responder
{
    // Used to map key words to responses.
    private HashMap<String, String> responseMap;
    // Default responses to use if we don't recognise a word.
    private ArrayList<String> defaultResponses;
    // The name of the file containing the default responses.
    private static final String FILE_OF_DEFAULT_RESPONSES = "default.txt";
    private Random randomGenerator;

    /**
     * Construct a Responder
     */
    public Responder()
    {
        responseMap = new HashMap<>();
        defaultResponses = new ArrayList<>();
        randomGenerator = new Random();
    // Attempt to populate the response map with known keywords and responses
    try {
        fillResponseMap();
    } catch (Exception e) {
        // Log the failure to populate the response map for debugging purposes
        System.err.println("Failed to populate responseMap: " + e.getMessage());
    }
    // Attempt to populate the default responses list from the specified file
    try {
        fillDefaultResponses();
    } catch (Exception e) {
        // Log the failure to populate default responses for debugging purposes
        System.err.println("Failed to populate defaultResponses: " + e.getMessage());
    }
    // Ensure the default responses list is not empty, even if an error occurred
     if (defaultResponses.isEmpty()) {
        defaultResponses.add("Could you elaborate on that?");
    }
    } 

     /**
     * Generate a response from a given set of input words.
     * If no matching word is found, returns a default response.
     * 
     * @param words  A set of words entered by the user
     * @return       A string that should be displayed as the response
     */
    public String generateResponse(HashSet<String> words)
    {
    // Handles null or empty input
    if (words == null || words.isEmpty()) {
        return pickDefaultResponse();
    }
    // Iterate through the words to find a matching response
    for (String word : words) {
        // Check if the word exists in the response map
        if (responseMap != null && responseMap.containsKey(word)) {
            return responseMap.get(word); // Return immediately if a match is found
        }
    }
    // If no match is found, return a default response
    return pickDefaultResponse();

    
   }
     
    /**
     * Enter all the known keywords and their associated responses
     * into our response map.
     */
    private void fillResponseMap()
    {
        responseMap.put("crash", 
                        "Well, it never crashes on our system. It must have something\n" +
                        "to do with your system. Tell me more about your configuration.");
        responseMap.put("crashes", 
                        "Well, it never crashes on our system. It must have something\n" +
                        "to do with your system. Tell me more about your configuration.");
        responseMap.put("slow", 
                        "I think this has to do with your hardware. Upgrading your processor\n" +
                        "should solve all performance problems. Have you got a problem with\n" +
                        "our software?");
        responseMap.put("performance", 
                        "Performance was quite adequate in all our tests. Are you running\n" +
                        "any other processes in the background?");
        responseMap.put("bug", 
                        "Well, you know, all software has some bugs. But our software engineers\n" +
                        "are working very hard to fix them. Can you describe the problem a bit\n" +
                        "further?");
        responseMap.put("buggy", 
                        "Well, you know, all software has some bugs. But our software engineers\n" +
                        "are working very hard to fix them. Can you describe the problem a bit\n" +
                        "further?");
        responseMap.put("windows", 
                        "This is a known bug to do with the Windows operating system. Please\n" +
                        "report it to Microsoft. There is nothing we can do about this.");
        responseMap.put("macintosh", 
                        "This is a known bug to do with the Mac operating system. Please\n" +
                        "report it to Apple. There is nothing we can do about this.");
        responseMap.put("expensive", 
                        "The cost of our product is quite competitive. Have you looked around\n" +
                        "and really compared our features?");
        responseMap.put("installation", 
                        "The installation is really quite straight forward. We have tons of\n" +
                        "wizards that do all the work for you. Have you read the installation\n" +
                        "instructions?");
        responseMap.put("memory", 
                        "If you read the system requirements carefully, you will see that the\n" +
                        "specified memory requirements are 1.5 giga byte. You really should\n" +
                        "upgrade your memory. Anything else you want to know?");
        responseMap.put("linux", 
                        "We take Linux support very seriously. But there are some problems.\n" +
                        "Most have to do with incompatible glibc versions. Can you be a bit\n" +
                        "more precise?");
        responseMap.put("bluej", 
                        "Ahhh, BlueJ, yes. We tried to buy out those guys long ago, but\n" +
                        "they simply won't sell... Stubborn people they are. Nothing we can\n" +
                        "do about it, I'm afraid.");
    }
   /**
 * Build up a list of default responses from which we can pick
 * if we don't know what else to say.
 */
private void fillDefaultResponses() {
    Charset charset = Charset.forName("UTF-8"); // Use UTF-8 for better compatibility
    Path path = Paths.get(FILE_OF_DEFAULT_RESPONSES);

    try (BufferedReader reader = Files.newBufferedReader(path, charset)) {
        String line;
        StringBuilder currentResponse = new StringBuilder();

        while ((line = reader.readLine()) != null) {
            line = line.trim(); // Remove leading/trailing whitespace

            if (line.isEmpty()) { // A blank line marks the end of a response
                if (currentResponse.length() > 0) { // Only add if there's content
                    defaultResponses.add(currentResponse.toString());
                    currentResponse.setLength(0); // Clear the builder for the next response
                }
            } else {
                if (currentResponse.length() > 0) {
                    currentResponse.append("\n"); // Add new line between the lines of the same response
                }
                currentResponse.append(line); // Append the line to the current response
            }
        }

        // Add the last response if there is one
        if (currentResponse.length() > 0) {
            defaultResponses.add(currentResponse.toString());
        }
    } catch (FileNotFoundException e) {
        System.err.println("Unable to open " + FILE_OF_DEFAULT_RESPONSES);
    } catch (IOException e) {
        System.err.println("A problem was encountered reading " + FILE_OF_DEFAULT_RESPONSES);
    }

    // Ensure we have at least one default response to avoid issues
    if (defaultResponses.isEmpty()) {
        defaultResponses.add("Could you elaborate on that?");
    }
}
   

   /**
     * Randomly select and return one of the default responses.
     * @return     A random default response
     */
    private String pickDefaultResponse()
    {
        // Pick a random number for the index in the default response list.
        // The number will be between 0 (inclusive) and the size of the list (exclusive).
        int index = randomGenerator.nextInt(defaultResponses.size());
        return defaultResponses.get(index);
    }
}
