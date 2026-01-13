package com.smaato.iabtcf.utils;

/**
 * Custom functional interface for API 21+ compatibility.
 * Replaces java.util.function. Function which is only available in Android API 24+.
 * This interface allows the iabtcf library to work seamlessly with Android minSdkVersion 21.
 *
 * @author Smaato
 * @version 1.0
 */
public interface BitReaderFunction {
    /**
     * Apply this function to the given BitReader and return an Integer result.
     *
     * @param t the BitReader to apply this function to
     * @return the result of applying this function
     */
    Integer apply(BitReader t);
}