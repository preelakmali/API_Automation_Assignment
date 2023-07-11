package utils;

public class Properties {

    private static final PropertiesReader propertiesReader = new PropertiesReader();
    public static final String baseUri = propertiesReader.getBaseUri();
}
