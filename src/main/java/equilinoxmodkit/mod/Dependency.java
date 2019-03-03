package equilinoxmodkit.mod;


import java.lang.annotation.*;


@Target( ElementType.TYPE )
@Retention( RetentionPolicy.RUNTIME )
public @interface Dependency {
	
	
	String[] dependencyIDs();
	
	String[] dependencyMinVersions();
	
	
}
