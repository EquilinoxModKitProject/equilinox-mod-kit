package equilinoxmodkit.launcher;

import net.minecraftforge.fml.relauncher.FMLInjectionData;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import net.minecraftforge.fml.relauncher.IFMLCallHook;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.MCVersion;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.Name;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.SortingIndex;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;

import java.io.File;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public class EmkNewLaunchTweaker  implements IFMLLoadingPlugin, IFMLCallHook{
	
	public EmkNewLaunchTweaker() {
		FMLLaunchHandler.configureForRuntimeDeobfuscation(FMLInjectionData.data()[1]);
		MixinBootstrap.init();
		Mixins.addConfiguration("mixins.equinox.json");
	}
	
	@Override
	public Void call() {
		return null;
	}
	
	@Override
	public String[] getASMTransformerClass() {
		return new String[0];
	}
	
	@Override
	public String getModContainerClass() {
		return null;
	}
	
	@Override
	public void injectData(Map<String, Object> data) {
	}
	
	@Override
	public String getAccessTransformerClass() {
		return null;
	}
	
	@Override
	public String getSetupClass() {
		return this.getClass().getName();
	}
	
	@Override
	public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile) {
	}
	
	@Override
	public String getLaunchTarget() {
		return null;
	}
	
	@Override
	public String[] getLaunchArguments() {
		return new String[0];
	}
	
	public static void main(String[] args) {
		try {
			Class<?> launchClassLoaderClass = Class.forName("net.minecraft.launchwrapper.LaunchClassLoader");
			Method addURLMethod = launchClassLoaderClass.getDeclaredMethod("addURL", File.class);
			addURLMethod.setAccessible(true);
			
			File equilinoxExecutable = new File("path/to/equilinox/executable.jar"); // Replace with the actual path to your Equilinox executable
			addURLMethod.invoke(FMLInjectionData.data()[1], equilinoxExecutable.toURI().toURL());
			
			FMLLaunchHandler.appendCoreMod(new MixinLoader());
			FMLInjectionData.data()[1].addModder("equilinoxmodkit.mixin.MixinLoader");
			
			FMLInjectionData.data()[0] = new String[0];
			FMLInjectionData.data()[2] = new File("path/to/game/dir"); // Replace with the actual path to your game directory
			FMLInjectionData.data()[3] = new File("path/to/assets/dir"); // Replace with the actual path to your assets directory
			FMLInjectionData.data()[4] = "profile"; // Replace with the desired profile name
			
			FMLLaunchHandler.callSystemExit(FMLInjectionData.data()[1].getMainClass(), args);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
