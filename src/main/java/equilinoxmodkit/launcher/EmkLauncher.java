package equilinoxmodkit.launcher;

import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.minecraft.launchwrapper.Launch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Data
@Accessors(chain = true)
public class EmkLauncher {
	
	private static final Logger LOGGER = LogManager.getLogger(EmkLauncher.class);
	
	private final OperatingSystem os;
	private final File equilinoxExecutable;
	private final File equilinoxJavaExecutable;
	private final File nativesDirectory;
	private final File modsDirectory;
	private final File configsDirectory;
	private final File logsDirectory;
	private final boolean debugMode;
	
	public EmkLauncher(OperatingSystem os, File equilinoxExecutable, File equilinoxJavaExecutable, File nativesDirectory, File modsDirectory, File configsDirectory, File logsDirectory, boolean debugMode) {
		this.os = os;
		this.equilinoxExecutable = equilinoxExecutable;
		this.equilinoxJavaExecutable = equilinoxJavaExecutable;
		this.nativesDirectory = nativesDirectory;
		this.modsDirectory = modsDirectory;
		this.configsDirectory = configsDirectory;
		this.logsDirectory = logsDirectory;
		this.debugMode = debugMode;
	}
	
	public EmkLauncher configure() {
		System.setProperty( "org.lwjgl.librarypath",this.nativesDirectory.getAbsolutePath() );
		return this;
	}
	
	public EmkLauncher loadMods(){
		LOGGER.info("Loading mods...");
		
		return this;
	}
	
	public EmkLauncher launchEquilinox() {
		LOGGER.info("Launching Equilinox...");
		
		try {
			Launch.main(new String[]{
					"--tweakClass", EmkLaunchTweaker.class.getName()
			});
		} catch (NullPointerException e) {
				LOGGER.error("An error occurred while starting Equilinox: {}", e);
				LOGGER.warn("Exiting with code {}", -14);
				System.exit(-14);
			}
		
		return this;
	}
	
}
