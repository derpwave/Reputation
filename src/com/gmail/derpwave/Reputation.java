package com.gmail.derpwave;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;



public class Reputation extends JavaPlugin {

	public static void main(String[] args) {

	}
	
	//FUNCTIONS
	
	//	defaults
	
    public void onEnable(){ 
        
    }
     
    public void onDisable(){ 
     
    }
    
    //	metarep changes
    
    
    
    //	misc
    
    public Player getplayerobj(String playername) {
    	for (Player ply : Bukkit.getServer().getOnlinePlayers()) {
    		if (ply.getName().equalsIgnoreCase(playername)) {

    			return ply;

    		}
    	}
    }
    
    public static boolean ifnumber(String input) {
        return input.matches("[+-]?\\d*(\\.\\d+)?");
    }
    
    //LISTENER
    
    //	for events
    
    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
         event.getPlayer().sendMessage("hi!");
    }
    
    //	for commands
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
    	if(cmd.getName().equalsIgnoreCase("test")){ 
    		//doSomething
    		return true;
    	} 
    	return false; 
    }


    
}
