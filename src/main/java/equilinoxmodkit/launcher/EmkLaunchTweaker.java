package equilinoxmodkit.launcher;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.LaunchClassLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

@Data
@Accessors(chain = true)
@NoArgsConstructor
public class EmkLaunchTweaker implements ITweaker {
	
	private static final Logger LOGGER = LogManager.getLogger(EmkLaunchTweaker.class);
	
	@Override
	public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile) {
	
	}
	
	@Override
	public void injectIntoClassLoader(LaunchClassLoader classLoader) {
//		System.setProperty("org.lwjgl.librarypath", new File("natives").getAbsolutePath());
		
		try {
			System.err.println(EmkMain.LAUNCHER.getEquilinoxExecutable().toURI().toURL());
					classLoader.addURL(EmkMain.LAUNCHER.getEquilinoxExecutable().toURI().toURL());
			
			MixinBootstrap.init();
			Mixins.addConfiguration("mixins.emk.json");
			
			// MODLOADER.initalize
		} catch (MalformedURLException e) {
			LOGGER.error("Could not add Equilinox Java executable to classloader! Error: {}", e);
			LOGGER.warn("Exiting with code {}", -13);
			System.exit(-13);
		}
		
		LOGGER.debug("Successfully injected into classloader and initialized mixins.");
	}
	
	@Override
	public String getLaunchTarget() {
		return "main.MainApp";
	}
	
	@Override
	public String[] getLaunchArguments() {
		return new String[0];
	}
	
}
