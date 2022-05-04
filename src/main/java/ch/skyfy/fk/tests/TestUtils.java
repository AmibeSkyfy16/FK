package ch.skyfy.fk.tests;

public class TestUtils {
    public static void printVersion(Class<?> clazz) {
        Package p = clazz.getPackage();
        System.out.printf("%s%n  Title: %s%n  Version: %s%n  Vendor: %s%n",
                clazz.getName(),
                p.getImplementationTitle(),
                p.getImplementationVersion(),
                p.getImplementationVendor());
    }

}
