package equilinoxmodkit.mod;


public abstract class EquilinoxMod {
	
	
	public abstract void preInit( PreInitializer pInit );
	
	public abstract void init( Initializer init );
	
	
	public ModInfo getModInfo() {
		return this.getClass().getDeclaredAnnotation( ModInfo.class );
	}
	
	public Dependency getDependency() {
		return this.getClass().getDeclaredAnnotation( Dependency.class );
	}
	
	
}
