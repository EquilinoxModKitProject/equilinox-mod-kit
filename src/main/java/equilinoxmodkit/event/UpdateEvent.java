package equilinoxmodkit.event;


import gameManaging.GameSpeed;
import gameManaging.GameState;
import picking.EntitySelectionManager;
import session.SessionManager;
import shopping.ShopManager;
import tasks.TaskManager;
import toolbox.MousePicker;


public class UpdateEvent extends EquilinoxEvent {
	
	
	GameSpeed gameSpeed;
	float timeSpeed;
	MousePicker terrainPicker;
	EntitySelectionManager entityPicker;
	SessionManager sessionManager;
	TaskManager taskManager;
	ShopManager shops;
	GameState gameState;
	float time;
	int ticker;
	
	
	public UpdateEvent( GameSpeed gameSpeed,float timeSpeed,MousePicker terrainPicker,EntitySelectionManager entityPicker,SessionManager sessionManager,TaskManager taskManager,ShopManager shops,GameState gameState,float time,int ticker ) {
		this.gameSpeed = gameSpeed;
		this.timeSpeed = timeSpeed;
		this.terrainPicker = terrainPicker;
		this.entityPicker = entityPicker;
		this.sessionManager = sessionManager;
		this.taskManager = taskManager;
		this.shops = shops;
		this.gameState = gameState;
		this.time = time;
		this.ticker = ticker;
	}
	
	
	public GameSpeed getGameSpeed() {
		return gameSpeed;
	}
	
	public float getTimeSpeed() {
		return timeSpeed;
	}
	
	public MousePicker getTerrainPicker() {
		return terrainPicker;
	}
	
	public EntitySelectionManager getEntityPicker() {
		return entityPicker;
	}
	
	public SessionManager getSessionManager() {
		return sessionManager;
	}
	
	public TaskManager getTaskManager() {
		return taskManager;
	}
	
	public ShopManager getShops() {
		return shops;
	}
	
	public GameState getGameState() {
		return gameState;
	}
	
	public float getTime() {
		return time;
	}
	
	public int getTicker() {
		return ticker;
	}
	
	
}
