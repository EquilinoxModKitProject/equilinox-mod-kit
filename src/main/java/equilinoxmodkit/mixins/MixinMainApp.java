package equilinoxmodkit.mixins;

import main.MainApp;
import mainGuis.EquilinoxGuis;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import textures.Texture;
import utils.MyFile;

@Mixin( value = MainApp.class, remap = false )
public class MixinMainApp {
	
	
	/* Show EML startup message. Call mod's 'init' method. */
	@Inject( method = "main", at = @At( value = "INVOKE", target = "LgameManaging/GameManager;init()V", shift = At.Shift.AFTER ) )
	private static void main(String[] args, CallbackInfo c ) {
		String description = String.format( "Version %s - %d mods loaded, %d mods rejected",0,
				0
		);
		
		Texture emkLogo = Texture.newTexture( new MyFile( "emk_logo.png" ) ).noFiltering().clampEdges().create();
		EquilinoxGuis.notify( "EML initialized",description,emkLogo,null );
		
//		ModLoader.initMods();
	}
	
	
}
