package engine;

import java.util.HashMap;

public class StartupArguments {
    public int windowHeight;
    public int windowWidth;
    public HashMap<String, String> map;

    public StartupArguments(String[] unparsedArgs){
        HashMap<String, String> parsedArgs = new HashMap<>();
        /*
        -test=abc
        -aaa=bbb
        */
        for(String unparsed : unparsedArgs){
            if(unparsed.startsWith("-")){
                String key = unparsed.substring(1, unparsed.indexOf("="));
                String value = unparsed.substring(unparsed.indexOf("=") + 1);
                parsedArgs.put(key, value);
            } else {
                System.out.println("Ignoring argument \"" + unparsed + "\", it doesn't start with \"-\"");
            }
        }

        this.windowWidth = Integer.parseInt(parsedArgs.getOrDefault("width", "800"));
        this.windowHeight = Integer.parseInt(parsedArgs.getOrDefault("height", "600"));

        map = parsedArgs;
    }
}
