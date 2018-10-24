package io.buybrain.util;

import lombok.AllArgsConstructor;
import lombok.NonNull;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.lang.Integer.parseInt;

/**
 * Utility for accessing environment variables
 */
@AllArgsConstructor
public class Env {
    private static final List<String> TRUTHY = Arrays.asList("1", "true", "t", "yes", "y");
    
    private final Map<String, String> env;

    /**
     * Construct an Env instance with the current system environment
     */
    public Env() {
        this(System.getenv());
    }

    /**
     * Get the value of an environment variable as a string
     *
     * @param key the environment variable
     * @return the string value
     * @throws IllegalArgumentException if the environment variable does not exist
     */
    public String getString(@NonNull String key) {
        if (!env.containsKey(key)) {
            throw new IllegalArgumentException("Environment variable " + key + " is not set");
        }
        return env.get(key);
    }

    /**
     * Get the value of an environment variable as a string, or a default value if it does not exist
     *
     * @param key the environment variable
     * @param defaultValue the value to use if the environment variable does not exist
     * @return the string value
     */
    public String getString(@NonNull String key, String defaultValue) {
        if (!env.containsKey(key)) {
            return defaultValue;
        }
        return env.get(key);
    }

    /**
     * Get the value of an environment variable as an integer
     *
     * @param key the environment variable
     * @return the integer value
     * @throws IllegalArgumentException if the environment variable does not exist
     * @throws NumberFormatException if the value cannot be parsed as an integer
     */
    public int getInt(@NonNull String key) {
        return parseInt(getString(key));
    }

    /**
     * Get the value of an environment variable as an integer, or a default value if it does not exist
     *
     * @param key the environment variable
     * @param defaultValue the value to use if the environment variable does not exist
     * @return the integer value
     * @throws NumberFormatException if the value cannot be parsed as an integer
     */
    public int getInt(@NonNull String key, int defaultValue) {
        return parseInt(getString(key, Integer.toString(defaultValue)));
    }
    
    public Boolean getBoolean(@NonNull String key) {
        return TRUTHY.contains(getString(key).toLowerCase());
    }

    public Boolean getBoolean(@NonNull String key, boolean defaultValue) {
        return TRUTHY.contains(getString(key, defaultValue ? "y" : "n").toLowerCase());
    }
}
