package com.fuzzylite;

import com.fuzzylite.terms.Term;
import java.util.logging.Logger;

/**
 *
 * @author jcrada
 */
public class Fuzzylite {
    
    public static final String NAME = "jFuzzylite";
    public static final String VERSION = "1.0";
    public static final String LONG_VERSION = "1.0b1307";
    public static final String AUTHOR = "Juan Rada-Vilela";
    
    public static int DECIMALS = 3;
    public static double PRECISION = 1e-5;
    public static int DEFAULT_DIVISIONS = 500;
    
    
    public static Logger logger(){
        return Logger.getLogger("fuzzylite");
    }
    
    public static void main(String[] args) {
        logger().info("Some log");
    }
}
