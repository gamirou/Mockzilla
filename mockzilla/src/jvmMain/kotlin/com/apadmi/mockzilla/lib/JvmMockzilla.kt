package com.apadmi.mockzilla.lib

import com.apadmi.mockzilla.BuildKonfig
import com.apadmi.mockzilla.lib.internal.utils.FileIo
import com.apadmi.mockzilla.lib.models.MetaData
import com.apadmi.mockzilla.lib.models.MockzillaConfig
import com.apadmi.mockzilla.lib.models.MockzillaRuntimeParams
import java.nio.file.Files

/**
 * Starts the Mockzilla server,
 *
 * @param appName The name of the client app
 * @param appVersion The version of the client app
 * @param config The config with which to initialise mockzilla.
 * @return runtimeParams Configuration of the mockzilla runtime environment
 */
fun startMockzilla(
    appName: String,
    appVersion: String,
    config: MockzillaConfig,
): MockzillaRuntimeParams = startMockzilla(
    config,
    MetaData(
        appName = appName,
        appPackage = "-",  // Not really a thing on non-mobile platforms
        operatingSystemVersion = System.getProperty("os.version"),
        deviceModel = "-",  // Covered by `operatingSystem`
        appVersion = appVersion,
        operatingSystem = System.getProperty("os.name"),
        mockzillaVersion = BuildKonfig.VERSION_NAME
    ),
    FileIo(Files.createTempDirectory("").toFile()),
)
