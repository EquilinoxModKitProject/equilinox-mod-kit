package equilinoxmodkit.mod;


import java.lang.annotation.*;


@Target( ElementType.TYPE )
@Retention( RetentionPolicy.RUNTIME )
public @interface ModInfo {
	
	
	String id();
	
	String name();
	
	String version();
	
	String author();
	
	String description();
	
	String thumbnail();
	
	
}
