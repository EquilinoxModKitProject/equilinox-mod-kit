package equilinoxmodkit.mod;


import equilinoxmodkit.util.OperatingSystem;

import java.io.File;
import java.util.ArrayList;


public class PreInitializer {
	
	
	private boolean emlDebugModeEnabled;
	private boolean equilinoxDebugModeEnabled;
	
	private OperatingSystem operatingSystem;
	private File equilinoxFolder;
	private File equilinoxJarFile;
	private File logFile;
	private File nativesFolder;
	private File modsFolder;
	
	private ArrayList<String> presentMods;
	private int numberOfLoadedMods;
	private int numberOfRejectedMods;
	
	private ArrayList<Class<?>> eventClasses;
	private ArrayList<Class<?>> blueprintClasses;
	
	
	public PreInitializer( boolean emlDebugModeEnabled,boolean equilinoxDebugModeEnabled,OperatingSystem operatingSystem,File equilinoxFolder,File equilinoxJarFile,File logFile,File nativesFolder,File modsFolder,ArrayList<EquilinoxMod> presentMods,int numberOfLoadedMods,int numberOfRejectedMods ) {
		this.emlDebugModeEnabled = emlDebugModeEnabled;
		this.equilinoxDebugModeEnabled = equilinoxDebugModeEnabled;
		this.operatingSystem = operatingSystem;
		this.equilinoxFolder = equilinoxFolder;
		this.equilinoxJarFile = equilinoxJarFile;
		this.logFile = logFile;
		this.nativesFolder = nativesFolder;
		this.modsFolder = modsFolder;
		this.presentMods = new ArrayList<>();
		for( int i = 0;i < presentMods.size();i++ ) this.presentMods.add( presentMods.get( i ).getModInfo().id() );
		this.numberOfLoadedMods = numberOfLoadedMods;
		this.numberOfRejectedMods = numberOfRejectedMods;
		
		eventClasses = new ArrayList<>();
		blueprintClasses = new ArrayList<>();
	}
	
	
	public void addEventClass( Class<?> clazz ) {
		eventClasses.add( clazz );
	}
	
	public void addBlueprintClass( Class<?> clazz ) {
		blueprintClasses.add( clazz );
	}
	
	public boolean isEmlDebugModeEnabled() {
		return emlDebugModeEnabled;
	}
	
	public boolean isEquilinoxDebugModeEnabled() {
		return equilinoxDebugModeEnabled;
	}
	
	public OperatingSystem getOperatingSystem() {
		return operatingSystem;
	}
	
	public File getEquilinoxFolder() {
		return equilinoxFolder;
	}
	
	public File getEquilinoxJarFile() {
		return equilinoxJarFile;
	}
	
	public File getLogFile() {
		return logFile;
	}
	
	public File getNativesFolder() {
		return nativesFolder;
	}
	
	public File getModsFolder() {
		return modsFolder;
	}
	
	public boolean isModPresent( String id ) {
		return presentMods.contains( id );
	}
	
	public int getNumberOfLoadedMods() {
		return numberOfLoadedMods;
	}
	
	public int getNumberOfRejectedMods() {
		return numberOfRejectedMods;
	}
	
	public ArrayList<Class<?>> getEventClasses() {
		return eventClasses;
	}
	
	public ArrayList<Class<?>> getBlueprintClasses() {
		return blueprintClasses;
	}
	
	
}
