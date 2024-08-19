package org.cherret;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigManager {

    private Properties properties = new Properties();
    private String configFilePath = "config.conf";

    public ConfigManager() {
        File configFile = new File(configFilePath);
        if (!configFile.exists()) {
            setDefaults();
            saveConfig();
        }
        loadConfig();
    }

    public void loadConfig() {
        try (FileInputStream input = new FileInputStream(configFilePath)) {
            properties.load(input);
        } catch (IOException e) {
        }
    }

    public void saveConfig() {
        try (FileOutputStream output = new FileOutputStream(configFilePath)) {
            properties.store(output, "Application Configurations");
        } catch (IOException e) {
        }
    }

    public int getApi_Id() {
        return Integer.parseInt(properties.getProperty("api_id", "0"));
    }

    public void setApi_Id(String api_id) {
        properties.setProperty("api_id", api_id);
    }

    public String getApi_Hash() {
        return properties.getProperty("api_hash", "0");
    }

    public void setApi_Hash(String api_hash) {
        properties.setProperty("api_hash", api_hash);
    }

    public String getPhone_Number() {
        return properties.getProperty("phone_number", "0");
    }

    public void setPhone_Number(String phone_number) {
        properties.setProperty("phone_number", phone_number);
    }

    private void setDefaults() {
        properties.setProperty("api_id", "0");
        properties.setProperty("api_hash", "0");
        properties.setProperty("phone_number", "0");
    }
}
