package com.tomkeuper.bedwars.api.configuration;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class ConfigManagerTest {

    @Mock
    private Plugin mockPlugin;

    @Mock
    private Logger mockLogger;

    @Mock
    private World mockWorld;

    private ConfigManager configManager;
    private File tempDir;
    private File configFile;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(mockPlugin.getLogger()).thenReturn(mockLogger);

        // Create a temporary directory for tests
        tempDir = new File(System.getProperty("java.io.tmpdir"), "bedwars-test-" + System.currentTimeMillis());
        tempDir.mkdirs();

        String configName = "test-config";
        configFile = new File(tempDir, configName + ".yml");

        configManager = new ConfigManager(mockPlugin, configName, tempDir.getAbsolutePath());
    }

    @AfterEach
    public void tearDown() {
        // Clean up temporary files
        if (configFile.exists()) {
            configFile.delete();
        }
        if (tempDir.exists()) {
            tempDir.delete();
        }
    }

    @Test
    public void testSaveArenaLoc_FormatsCorrectly() {
        // Arrange
        String path = "arena.spawn";
        when(mockWorld.getName()).thenReturn("world");

        // Use values that we can easily verify
        Location location = new Location(mockWorld, 10.5, 64.0, -5.5, 90.0f, -45.0f);

        // Act
        configManager.saveArenaLoc(path, location);

        // Assert
        // The expected format for arena locations is: X,Y,Z,Yaw,Pitch
        // World name is NOT included in arena locations, unlike standard config locations
        String expectedData = "10.5,64.0,-5.5,90.0,-45.0";

        // Verify it was written correctly to the YAML configuration object
        YamlConfiguration yml = configManager.getYml();
        assertEquals(expectedData, yml.getString(path));
    }

    @Test
    public void testSaveArenaLoc_WithWholeNumbers() {
        // Arrange
        String path = "arena.generator";
        when(mockWorld.getName()).thenReturn("world");

        Location location = new Location(mockWorld, 10, 64, -5, 0f, 0f);

        // Act
        configManager.saveArenaLoc(path, location);

        // Assert
        // The format should convert integers to doubles, and float yaw/pitch to doubles
        String expectedData = "10.0,64.0,-5.0,0.0,0.0";

        YamlConfiguration yml = configManager.getYml();
        assertEquals(expectedData, yml.getString(path));
    }
}
