public class Dependencies {
    private static final String JETBRAINS_ANNOTATIONS_VERSION = "23.0.0";
    private static final String COMMONS_IO_VERSION = "2.11.0";
    private static final String JACKSON_VERSION = "2.13.1";
    private static final String XZ_VERSION = "1.9";
    private static final String LZ4_JAVA_VERSION = "1.8.0";


    public static final String JETBRAINS_ANNOTATIONS = "org.jetbrains:annotations:" + JETBRAINS_ANNOTATIONS_VERSION;
    public static final String COMMONS_IO = "commons-io:commons-io:" + COMMONS_IO_VERSION;
    public static final String JACKSON_DATABIND = "com.fasterxml.jackson.core:jackson-databind:" + JACKSON_VERSION;
    public static final String JACKSON_DATATYPE_JSR310 = "com.fasterxml.jackson.datatype:jackson-datatype-jsr310:" + JACKSON_VERSION;
    public static final String JACKSON_DATATYPE_JDK8 = "com.fasterxml.jackson.datatype:jackson-datatype-jdk8:" + JACKSON_VERSION;
    public static final String XZ = "org.tukaani:xz:" + XZ_VERSION;
    public static final String LZ4_JAVA = "org.lz4:lz4-java:" + LZ4_JAVA_VERSION;
}
