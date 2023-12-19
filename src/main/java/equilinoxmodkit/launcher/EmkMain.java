package equilinoxmodkit.launcher;

import com.google.common.collect.Lists;
import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configurator;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

import static equilinoxmodkit.launcher.OperatingSystem.*;
import static equilinoxmodkit.launcher.OperatingSystem.LINUX;
import static equilinoxmodkit.launcher.OperatingSystem.UNKNOWN;

public class EmkMain {
	
	private static final Logger LOGGER = LogManager.getLogger(EmkMain.class);
	
	/**
	 * <ul>
	 *     <li>{@code -debug} &#09;Enables debug mode for troubleshooting.</li>
	 * </ul>
	 */
	private static final String[] ACCEPTED_OPTIONS_NO_ARG = {
			"debug"
	};
	/**
	 * If any of the paths specified by one of the following options does not exist, they will be created. By default, EMK will create them in the Equilinox directory alongside the Equilinox executable.
	 * <ul>
	 *      <li>{@code -os <value>} &#09;Manually sets the operating system; one of: {@code WINDOWS}, {@code LINUX}, {@code MAC}.</li>
	 *      <li>{@code -equ-dir <value>} &#09;Manually sets the directory of Equilinox; e.g. {@code -equ-dir "C:\Program Files\Steam\steamapps\common\Equilinox"}.</li>
	 *      <li>{@code -equ-exe <value>} &#09;Manually sets the path of the Equilinox executable; e.g. {@code -equ-exe "C:\Program Files\Steam\steamapps\common\Equilinox\EquilinoxWindows.jar"}</li>
	 *      <li>{@code -nat-dir <value>} &#09;Manually sets the directory in which to extract the contents of the Equilinox natives; e.g. {@code -nat-dir "C:\Program Files\Steam\steamapps\common\Equilinox\natives"}.</li>
	 *      <li>{@code -jav-exe <value>} &#09;Manually sets the path of the Java executable used to start Equilinox; e.g. {@code -jav-exe "C:\Program Files\Steam\steamapps\common\Equilinox\jreWindows32\bin\java.exe"}</li>
	 *      <li>{@code -mod-dir <value>} &#09;Manually sets the directory from which to load mods; e.g. {@code -mod-dir "C:\Program Files\Steam\steamapps\common\Equilinox\mods"}</li>
	 *      <li>{@code -cfg-dir <value>} &#09;Manually sets the directory to and from which to write and read mod configuration files; e.g. {@code -cfg-dir "C:\Program Files\Steam\steamapps\common\Equilinox\configs"}</li>
	 *      <li>{@code -log-dir <value>} &#09;Manually sets the directory to which to write log files (both of mods and EMK); e.g. {@code -log-dir "C:\Program Files\Steam\steamapps\common\Equilinox\logs"}</li>
	 *  </ul>
	 */
	private static final String[] ACCEPTED_OPTIONS_WITH_REQ_ARG = {
			"os",
			"equ-dir",
			"equ-exe",
			"nat-dir",
			"jav-exe",
			"mod-dir",
			"cfg-dir",
			"log-dir",
	};
	/**
	 * Names of the native libraries used by Equilinox on Windows that will be extracted to the 'natives' directory.
	 */
	private static final String[] NATIVE_NAMES_WINDOWS = new String[]{
			"jinput-dx8.dll",
			"jinput-dx8_64.dll",
			"jinput-raw.dll",
			"jinput-raw_64.dll",
			"lwjgl.dll",
			"lwjgl64.dll",
			"OpenAL32.dll",
			"OpenAL64.dll"
	};
	/**
	 * Names of the native libraries used by Equilinox on Linux that will be extracted to the 'natives' directory.
	 */
	private static final String[] NATIVE_NAMES_LINUX = new String[]{
			"libjinput-linux.so",
			"libjinput-linux64.so",
			"liblwjgl.so",
			"liblwjgl64.so",
			"libopenal.so",
			"libopenal64.so"
	};
	/**
	 * Names of the native libraries used by Equilinox on macOS that will be extracted to the 'natives' directory.
	 */
	private static final String[] NATIVE_NAMES_MACOS = new String[]{
			"libjinput-osx.dylib",
			"liblwjgl.dylib",
			"openal.dylib"
	};
	
	public static EmkLauncher LAUNCHER;
	
	public static void main(String[] args) {
		OptionSet options = parseArgs(ACCEPTED_OPTIONS_NO_ARG, ACCEPTED_OPTIONS_WITH_REQ_ARG, args);
		
		final boolean debugMode = options.has("debug");
		if (debugMode) {
			LoggerContext loggerContext = (LoggerContext) LogManager.getContext(false);
			Configurator.setLevel(loggerContext.getRootLogger().getName(), Level.DEBUG);
		}
		
		printStartupInfo();
		
		final OperatingSystem os = determineOperatingSystem(options);
		final File equilinoxDir = findEquilinoxDirectory(options, os); // if returned, this file will never be null
		final File equilinoxExe = findEquilinoxExecutable(options, equilinoxDir, os);
		final File javaExe = findJavaExecutable(options, equilinoxDir, os);
		final File nativesDir = findNativesDirectory(options, equilinoxDir, equilinoxExe, os);
		final File modsDir = findModsDirectory(options, equilinoxDir);
		final File configsDir = findConfigsDirectory(options, equilinoxDir);
		final File logsDir = findLogsDirectory(options, equilinoxDir);
		
		LAUNCHER = new EmkLauncher(
				os,
				equilinoxExe,
				javaExe,
				nativesDir,
				modsDir,
				configsDir,
				logsDir,
				debugMode
		);
		LAUNCHER.configure()
				.loadMods()
				.launchEquilinox();
	}
	
	/**
	 * @param acceptedOptionsNoArg      Array of accepted options that do not require an argument.
	 * @param acceptedOptionsWithReqArg Array of accepted options that require an additional argument.
	 * @return An {@link OptionParser} with the given accepted options. The options should be given without the '-' or '-' prefix (e.g. {@code ["arg1","arg"]}). {@code acceptedOptionsWithReqArg} are such options that require an additional argument such as a file path; the required type and thus return type of these options is {@link String}. If there is an error during parsing, a default {@link OptionSet} is returned that only contains {@code -debug}.
	 * <br><br>
	 * <i>Usage examples in the console: {@code -debug}, {@code -log-dir "C:/Equilinox/logs"}</i>
	 */
	private static OptionSet parseArgs(String[] acceptedOptionsNoArg, String[] acceptedOptionsWithReqArg, String[] args) {
		OptionParser optionParser = new OptionParser();
		Stream.of(acceptedOptionsNoArg).forEach(option -> optionParser.accepts(option));
		Stream.of(acceptedOptionsWithReqArg).forEach(option -> optionParser.accepts(option).withRequiredArg().ofType(String.class));
		
		OptionSet options = optionParser.parse("-debug"); // enable debug by default; gets overwritten if actual options are parsed correctly
		try {
			options = optionParser.parse(args);
		} catch (OptionException e) {
			LOGGER.error("Error while parsing startup options: {}; continuing in debug mode.", e.getMessage());
		}
		
		LOGGER.debug("Startup options: {}", options.asMap());
		return options;
	}
	
	/**
	 * Prints startup information. More data will be printed if debug mode is enabled.
	 */
	private static void printStartupInfo() {
		LOGGER.info("Starting Equilinox Mod Kit v{}", EmkMain.class.getPackage().getImplementationVersion());
		LOGGER.debug("Running in debug mode.");
		LOGGER.debug("Java v{}", System.getProperty("java.version"));
		LOGGER.debug("{} v{}", System.getProperty("os.name"), System.getProperty("os.version"));
	}
	
	/**
	 * @param options Application options provided by the user.
	 * @return The determined {@link OperatingSystem}. If an option was set on startup, that value will be used. Otherwise, the operating system will be determined automatically. If automatic detection fails, {@link OperatingSystem#UNKNOWN} will be returned.
	 */
	private static OperatingSystem determineOperatingSystem(OptionSet options) {
		OperatingSystem os = UNKNOWN;
		
		// via option
		if (options.has("os")) {
			try {
				os = OperatingSystem.valueOf(
						options.valueOf("os").toString().toUpperCase()
				);
			} catch (NullPointerException e) {
				LOGGER.warn("No value was provided for -os; continuing with automatic operating system detection.");
			} catch (OptionException e) {
				LOGGER.warn("More than one option was provided for -os; continuing with automatic operating system detection.");
			} catch (IllegalArgumentException e) {
				LOGGER.warn("The value provided for -os is not valid; continuing with automatic operating system detection.");
			}
		}
		
		// automatic detection
		if (os == UNKNOWN) {
			String osName = System.getProperty("os.name").toLowerCase();
			if (osName.contains("win")) os = WINDOWS;
			else if (osName.contains("nux")) os = LINUX;
			else if (osName.contains("mac")) os = MAC;
			else LOGGER.error("Automatic operating system detection failed; continuing with UNKNOWN.");
		}
		
		LOGGER.debug("Operating system: {}", os);
		return os;
	}
	
	/**
	 * @param options Application options provided by the user.
	 * @param os      Operating system the app is running on.
	 * @return A {@link File} pointing to the Equilinox directory. If no valid directory could be found, the application exits with code {@code -2}.
	 * * <br>
	 * * The function first checks if a valid file was provided via the option {@code -equ-dir}; if not, it searches current execution directory and any directory and some common installation locations.
	 */
	private static File findEquilinoxDirectory(OptionSet options, OperatingSystem os) {
		AtomicReference<File> equilinoxDir = new AtomicReference<>(null);
		
		// via option 'equ-dir'
		if (options.has("equ-dir")) {
			try {
				File file = new File(options.valueOf("equ-dir").toString());
				if (file.exists() &&
						file.isDirectory() &&
						file.getPath().toLowerCase().contains("equilinox")
				) {
					equilinoxDir.set(file);
				} else {
					LOGGER.warn("The path provided for -equ-dir is not a valid Equilinox directory; continuing with automatic Equilinox executable detection.");
				}
			} catch (NullPointerException e) {
				LOGGER.warn("No value was provided for -equ-dir; continuing with automatic Equilinox executable detection.");
			} catch (OptionException e) {
				LOGGER.warn("More than one option was provided for -equ-dir; continuing with automatic Equilinox executable detection.");
			}
		}
		
		// automatic detection
		List<String> osDependentEquilinoxDirPaths = new ArrayList<>(Arrays.asList(
				System.getProperty("user.dir"), // start with the current execution directory
				System.getProperty("user.dir")+File.separator+"/Equilinox" // sub-dir when developing mods with a cloned EMK repo
		));
		switch (os) {
			case WINDOWS:
				osDependentEquilinoxDirPaths.addAll(Arrays.asList(
						"C:/Program Files (x86)/Steam/steamapps/common/Equilinox/",
						"C:/Program Files/Steam/steamapps/common/Equilinox/"
				));
				break;
			case LINUX:
				osDependentEquilinoxDirPaths.addAll(Arrays.asList(
						"/home/" + System.getProperty("user.name") + "/.var/app/com.valvesoftware.Steam/.local/share/Steam/steamapps/common/Equilinox/",
						"home/" + System.getProperty("user.name") + "/.steam/debian-installation/steamapps/common/Equilinox/"
				));
				break;
			case MAC:
				osDependentEquilinoxDirPaths.addAll(Arrays.asList(
						"/Users/" + System.getProperty("user.name") + "/Library/Application Support/Steam/steamapps/common/Equilinox/"
				));
				break;
			default:
			case UNKNOWN:
				// only searches the current execution directory
		}
		
		osDependentEquilinoxDirPaths.forEach(path -> {
			if (equilinoxDir.get() == null) {
				File dir = new File(path);
				if (dir.exists() &&
						dir.isDirectory() &&
						dir.getPath().toLowerCase().contains("equilinox") &&
						!dir.getPath().toLowerCase().endsWith("equilinox-mod-kit") // ignore equilinox-mod-kit directory to make the automatic detection find the Equilinox sub-dir when developing mods with cloned EMK repo
				) {
					equilinoxDir.set(dir);
				}
			}
		});
		
		if (equilinoxDir.get() == null) {
			LOGGER.error("Equilinox directory not found! Manually specify it with -equ-dir, or execute EMK from within the Equilinox directory.");
			LOGGER.warn("Exiting with code {}", -2);
			System.exit(-2);
		}
		
		LOGGER.debug("Equilinox directory: {}", equilinoxDir.get());
		return equilinoxDir.get();
	}
	
	/**
	 * @param options      Application options provided by the user.
	 * @param equilinoxDir Directory containing the Equilinox executable.
	 * @param os           Operating system the app is running on.
	 * @return A {@link File} pointing to the Equilinox executable JAR. If no valid executable could be found, the application exits with code {@code -3}.
	 * <br>
	 * The function first checks if a valid file was provided via the option {@code -equ-exe}; if not, it searches the Equilinox directory and its subfolders.
	 */
	private static File findEquilinoxExecutable(OptionSet options, File equilinoxDir, OperatingSystem os) {
		AtomicReference<File> equilinoxExecutable = new AtomicReference<>(null);
		
		// via option 'equ-exe'
		if (options.has("equ-exe")) {
			try {
				File file = new File(options.valueOf("equ-exe").toString());
				if (file.exists() &&
						file.isFile() &&
						file.getName().toLowerCase().endsWith(".jar") &&
						(file.getName().toLowerCase().contains("equilinox") || file.getName().toLowerCase().contains("input"))
				) {
					equilinoxExecutable.set(file);
				} else {
					LOGGER.warn("The path provided for -equ-exe is not a valid Equilinox executable JAR file; continuing with automatic Equilinox executable detection.");
				}
			} catch (NullPointerException e) {
				LOGGER.warn("No value was provided for -equ-exe; continuing with automatic Equilinox executable detection.");
			}
		}
		
		// in option Equilinox directory
		if (equilinoxExecutable.get() == null && equilinoxDir != null) {
			try {
				equilinoxExecutable.set(findEquilinoxExecutableInSubDirs(equilinoxDir.getPath()));
			} catch (IOException e) {
				// no need to catch here; 'equilinoxExecutable' will be null and the application will exit in the next step
			}
		}
		
		if (equilinoxExecutable.get() == null) {
			LOGGER.error("Equilinox executable not found! Manually specify it with -equ-exe or -equ-dir, or execute EMK from within the Equilinox directory.");
			LOGGER.warn("Exiting with code {}", -3);
			System.exit(-3);
		}
		
		LOGGER.debug("Equilinox executable: {}", equilinoxExecutable.get());
		return equilinoxExecutable.get();
	}
	
	/**
	 * @param dirPath The directory to search in.
	 * @return A {@link File} pointing to the Equilinox executable JAR file if found. Otherwise {@code null}. The function searches in subdirectories of the provided path name until a valid Equilinox executable is found, but will stop after 3 levels to avoid lengthy searches.
	 * @throws IOException From {@link Files#walkFileTree}: <i>if an I/O error is thrown by a visitor method</i>.
	 */
	private static File findEquilinoxExecutableInSubDirs(String dirPath) throws IOException {
		final File[] equilinoxExecutable = {null};
		Files.walkFileTree(Paths.get(dirPath), new SimpleFileVisitor<Path>() {
			private final int maxDepth = 3; // if placed alongside the Equilinox.app on macOS, the executable should have been found 3 directories down
			private int depth = 0;
			
			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				if (depth >= maxDepth) return FileVisitResult.SKIP_SUBTREE;
				depth++;
				return super.preVisitDirectory(dir, attrs);
			}
			
			@Override
			public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) {
				String fileName = path.getFileName().toString().toLowerCase();
				if ((fileName.contains("equilinox") || fileName.contains("input")) && fileName.endsWith(".jar")) {
					equilinoxExecutable[0] = path.toFile();
					return FileVisitResult.TERMINATE;
				}
				return FileVisitResult.CONTINUE;
			}
		});
		return equilinoxExecutable[0];
	}
	
	/**
	 * @param options      Application options provided by the user.
	 * @param equilinoxDir Directory containing the Equilinox executable.
	 * @param os           Operating system the app is running on.
	 * @return A {@link File} pointing to the Java executable shipped with Equilinox. If no valid executable could be found, the application exits with code {@code -4}.
	 * <br>
	 * The function first checks if a valid file was provided via the option {@code -jav-exe}; if not, it searches the Equilinox directory and its subfolders.
	 */
	private static File findJavaExecutable(OptionSet options, File equilinoxDir, OperatingSystem os) {
		File javaExecutable = null;
		
		// via option 'jav-exe'
		if (options.has("jav-exe")) {
			try {
				File file = new File(options.valueOf("jav-exe").toString());
				if (file.exists() &&
						file.isFile() &&
						file.getName().toLowerCase().contains("java")) {
					javaExecutable = file;
				} else {
					LOGGER.warn("The path provided for -jav-exe is not a valid Java executable; continuing with automatic Java executable detection.");
				}
			} catch (NullPointerException e) {
				LOGGER.warn("No value was provided for -jav-exe; continuing with automatic Java executable detection.");
			}
		}
		
		// in Equilinox directory
		if (javaExecutable == null && equilinoxDir != null) {
			try {
				javaExecutable = findJavaExecutableInSubDirs(equilinoxDir.getPath());
			} catch (IOException e) {
				// no need to catch here; 'javaExecutable' will be null and the application will exit in the next step
			}
		}
		
		if (javaExecutable == null) {
			LOGGER.error("Java executable not found! Manually specify it with -jav-exe or -equ-dir, or execute EMK from within the Equilinox directory.");
			LOGGER.warn("Exiting with code {}", -4);
			System.exit(-4);
		}
		
		LOGGER.debug("Java executable: {}", javaExecutable);
		return javaExecutable;
	}
	
	/**
	 * @param dirPath The directory to search in.
	 * @return A {@link File} pointing to the Java executable shipped with Equilinox file if found. Otherwise {@code null}. The function searches in subdirectories of the provided path name until a valid Java executable is found, but will stop after 7 levels to avoid lengthy searches.
	 * @throws IOException From {@link Files#walkFileTree}: <i>if an I/O error is thrown by a visitor method</i>.
	 */
	private static File findJavaExecutableInSubDirs(String dirPath) throws IOException {
		final File[] javaExecutable = {null};
		Files.walkFileTree(Paths.get(dirPath), new SimpleFileVisitor<Path>() {
			private final int maxDepth = 7; // if placed alongside the Equilinox.app on macOS, the executable should have been found 7 directories down
			private int depth = 0;
			
			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				if (depth >= maxDepth) return FileVisitResult.SKIP_SUBTREE;
				depth++;
				return super.preVisitDirectory(dir, attrs);
			}
			
			@Override
			public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) {
				String pathName = path.toString().toLowerCase().replace("\\", "/");
				if (pathName.endsWith("bin/java.exe") || pathName.endsWith("bin/java")) {
					javaExecutable[0] = path.toFile();
					return FileVisitResult.TERMINATE;
				}
				return FileVisitResult.CONTINUE;
			}
		});
		return javaExecutable[0];
	}
	
	/**
	 * @param options      Application options provided by the user.
	 * @param equilinoxDir Directory containing the Equilinox executable.
	 * @param equilinoxExe The Equilinox executable JAR.
	 * @return A {@link File} pointing to the natives folder into which Equilinox' natives will be extracted. If no valid executable could be found, the application exits with code {@code -6}. It may also exit with code {@code -5} if the natives directory could not be created due to missing write-permissions, or with code {@code -7} if the extraction of natives failed. The called function {@link #verifyNativesIntegrityOrExtractNatives} might exit with code {@code -8} if an error during extraction of a native file occurs.
	 * <br>
	 * The function first checks if a valid file was provided via the option {@code -nat-dir}; if not, it places the natives folder in the Equilinox directory.
	 */
	private static File findNativesDirectory(OptionSet options, File equilinoxDir, File equilinoxExe, OperatingSystem os) {
		File nativesDir = null;
		
		// via option 'nat-dir'
		if (options.has("nat-dir")) {
			try {
				File file = new File(options.valueOf("nat-dir").toString());
				if (!file.exists()) {
					file.mkdir();
					LOGGER.debug("Created natives directory at: {}", file);
				}
				if (!file.canWrite()) {
					LOGGER.error("Cannot write to natives directory at '{}'! Check that it is not read-only.", file);
					LOGGER.warn("Exiting with code {}", -5);
					System.exit(-5);
				}
				nativesDir = file;
				// continue to end of method where natives are verified and / or extracted
			} catch (NullPointerException e) {
				LOGGER.warn("No value was provided for -nat-dir; continuing with automatic natives directory detection.");
			}
		}
		
		// in Equilinox directory
		if (nativesDir == null && equilinoxDir != null) {
			File file = new File(equilinoxDir.getPath() + File.separator + "natives");
			if (!file.exists()) {
				file.mkdir();
				LOGGER.debug("Created natives directory: {}", file);
			}
			if (!file.canWrite()) {
				LOGGER.error("Cannot write to natives directory at '{}'! Check that it is not read-only.", file);
				LOGGER.warn("Exiting with code {}", -5);
				System.exit(-5);
			}
			nativesDir = file;
		}
		
		if (nativesDir == null) {
			LOGGER.error("Natives directory not found! Manually specify it with -nat-dir or -equ-dir, or execute EMK from within the Equilinox directory.");
			LOGGER.warn("Exiting with code {}", -6);
			System.exit(-6);
		}
		
		try {
			if (!verifyNativesIntegrityOrExtractNatives(nativesDir, equilinoxExe, os)) {
				LOGGER.error("Could not verify or extract natives! Verify that the Equilinox executable is valid.");
				LOGGER.warn("Exiting with code {}", -7);
				System.exit(-7);
			}
		} catch (IOException e) {
			LOGGER.error("Could not verify or extract natives! Verify that the Equilinox executable is valid.");
			LOGGER.warn("Exiting with code {}", -7);
			System.exit(-7);
		}
		
		LOGGER.debug("Natives directory: {}", nativesDir);
		return nativesDir;
	}
	
	/**
	 * @param nativesDir   Directory into which Equilinox' natives will be extracted.
	 * @param equilinoxExe The Equilinox executable JAR.
	 * @param os           Operating system the app is running on.
	 * @return {@code True} if all natives were verified and / or extracted successfully, {@code false} otherwise. The function might exit with code {@code -8} if an error during extraction of a native file occurs in {@link #extractNativeFile}.
	 * @throws IOException From {@link JarFile#JarFile}: <i>if an I/O error has occurred</i>.
	 */
	private static boolean verifyNativesIntegrityOrExtractNatives(File nativesDir, File equilinoxExe, OperatingSystem os) throws IOException {
		JarFile equilinoxJar = new JarFile(equilinoxExe);
		
		// verify integrity
		List<String> osDependentNativeNames;
		switch (os) {
			case WINDOWS:
				osDependentNativeNames = Arrays.asList(NATIVE_NAMES_WINDOWS);
				break;
			case LINUX:
				osDependentNativeNames = Arrays.asList(NATIVE_NAMES_LINUX);
				break;
			case MAC:
				osDependentNativeNames = Arrays.asList(NATIVE_NAMES_MACOS);
				break;
			default:
				return false;
		}
		
		AtomicInteger foundNativeFilesCount = new AtomicInteger(0);
		osDependentNativeNames.forEach(nativeName -> {
			File file = new File(nativesDir + File.separator + nativeName);
			if (file.exists() && getFilesizeMB(file) > 0.05) {
				foundNativeFilesCount.getAndIncrement();
			} else {
				try {
					if (extractNativeFile(equilinoxJar, file)) foundNativeFilesCount.getAndIncrement();
				} catch (IOException e) {
					LOGGER.error("A native file ({}) could not be extracted from the Equilinox executable JAR file! Attempt to extract it manually.", nativeName);
					LOGGER.warn("Exiting with code {}", -8);
					System.exit(-8);
				}
			}
		});
		
		if (foundNativeFilesCount.get() == osDependentNativeNames.size()) return true;
		
		return false;
	}
	
	private static double getFilesizeMB(File file) {
		try {
			return Files.size(file.toPath()) / 1000.0 / 1000.0;
		} catch (IOException e) {
			LOGGER.error("Could not get file size of {}! Attempt to manually delete the file.", file);
			LOGGER.warn("Exiting with code {}", -12);
			System.exit(-12);
		}
		return 0.0;
	}
	
	/**
	 * @param equilinoxJar The Equilinox executable JAR.
	 * @param targetFile   The file name to copy the file to.
	 * @return {@code True} if the file was extracted from the JAR file successfully, {@code false} otherwise.
	 * @throws IOException Originates from one fo these: {@link JarFile#getInputStream}, {@link FileOutputStream#FileOutputStream}, {@link FileOutputStream#write(int)}.
	 */
	private static boolean extractNativeFile(JarFile equilinoxJar, File targetFile) throws IOException {
		Enumeration<JarEntry> jarEntries = equilinoxJar.entries();
		JarEntry entry;
		while ((entry = jarEntries.nextElement()) != null) {
			;
			if (entry.getName().equals(targetFile.getName())) {
				InputStream jarFileStream = equilinoxJar.getInputStream(entry);
				FileOutputStream fileStream = new FileOutputStream(targetFile);
				while (jarFileStream.available() > 0) {
					fileStream.write(jarFileStream.read());
				}
				return true;
			}
		}
		
		return false;
	}
	
	private static File findModsDirectory(OptionSet options, File equilinoxDir) {
		File modsDir = null;
		
		// via option 'mod-dir'
		if (options.has("mod-dir")) {
			try {
				File file = new File(options.valueOf("mod-dir").toString());
				if (!file.exists()) {
					file.mkdir();
					LOGGER.debug("Created mods directory at: {}", file);
				}
				modsDir = file;
			} catch (NullPointerException e) {
				LOGGER.warn("No value was provided for -mod-dir; continuing with automatic mods directory detection.");
			}
		}
		
		// in Equilinox directory
		if (modsDir == null && equilinoxDir != null) {
			File file = new File(equilinoxDir + File.separator + "mods");
			if (!file.exists()) {
				file.mkdir();
				LOGGER.debug("Created mods directory at: {}", file);
			}
			modsDir = file;
		}
		
		if (modsDir == null) {
			LOGGER.error("Mods directory not found! Manually specify it with -mod-dir or -equ-dir, or execute EMK from within the Equilinox directory.");
			System.exit(-9);
			LOGGER.warn("Exiting with code {}", -9);
		}
		
		LOGGER.debug("Mods directory: {}", modsDir);
		return modsDir;
	}
	
	private static File findConfigsDirectory(OptionSet options, File equilinoxDir) {
		File configsDir = null;
		
		// via option 'cfg-dir'
		if (options.has("cfg-dir")) {
			try {
				File file = new File(options.valueOf("cfg-dir").toString());
				if (!file.exists()) {
					file.mkdir();
					LOGGER.debug("Created configs directory at: ", file);
				}
				configsDir = file;
			} catch (NullPointerException e) {
				LOGGER.warn("No value was provided for -cfg-dir; continuing with automatic mods directory detection.");
			}
		}
		
		// in Equilinox directory
		if (configsDir == null && equilinoxDir != null) {
			File file = new File(equilinoxDir + File.separator + "configs");
			if (!file.exists()) {
				file.mkdir();
				LOGGER.debug("Created configs directory at: ", file);
			}
			configsDir = file;
		}
		
		if (configsDir == null) {
			LOGGER.error("Configs directory not found! Manually specify it with -cfg-dir or -equ-dir, or execute EMK from within the Equilinox directory.");
			LOGGER.warn("Exiting with code {}", -10);
			System.exit(-10);
		}
		
		LOGGER.debug("Configs directory: {}", configsDir);
		return configsDir;
	}
	
	private static File findLogsDirectory(OptionSet options, File equilinoxDir) {
		File logsDir = null;
		
		// via option 'mods-dir'
		if (options.has("log-dir")) {
			try {
				File file = new File(options.valueOf("log-dir").toString());
				if (!file.exists()) {
					file.mkdir();
					LOGGER.debug("Created logs directory at: ", file);
				}
				logsDir = file;
			} catch (NullPointerException e) {
				LOGGER.warn("No value was provided for -log-dir; continuing with automatic mods directory detection.");
			}
		}
		
		// in Equilinox directory
		if (logsDir == null && equilinoxDir != null) {
			File file = new File(equilinoxDir + File.separator + "logs");
			if (!file.exists()) {
				file.mkdir();
				LOGGER.debug("Created mods directory at: ", file);
			}
			logsDir = file;
		}
		
		if (logsDir == null) {
			LOGGER.error("Mods directory not found! Manually specify it with -log-dir or -equ-dir, or execute EMK from within the Equilinox directory.");
			System.exit(-11);
			LOGGER.warn("Exiting with code {}", -11);
		}
		
		LOGGER.debug("Logs directory: {}", logsDir);
		return logsDir;
	}
	
}
