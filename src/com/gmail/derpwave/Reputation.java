package com.gmail.derpwave;

import java.util.HashMap;

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
	
	//CONSTRUCTORS
	
	HashMap<String, Integer> repmap = new HashMap<String, Integer>();
	
	//FUNCTIONS
	
	//	defaults
	
    public void onEnable(){ 
        
    }
     
    public void onDisable(){ 
     
    }
    
    //	maprep changes
    
    public void setmaprep(String player, Integer value) {  //sets a rep value in the hashmap; creates entry if key (player name) doesn't exist yet, replaces otherwise
    	repmap.put(player, value);
    }
    
    public Integer getmaprep(String player) {  //returns the reputation of a player as int. Don't forget to check if entry exists first with checkmaprep()
    	return repmap.get(player);
    }
    
    public void altmaprep(String player, Integer value) {  //alters a player's reputation by a given value, which can be negative
    	setmaprep(player, getmaprep(player)+value);
    }
    
    public boolean checkmaprep(String player) {  //checks if an entry for a player exists in the hashmap, returns boolean
    	return repmap.containsKey(player);
    }
    
    
    //	metarep changes
    
    
    
    //	misc
    
    public boolean ifplayer(String player) {  //checks if player exists on the server
    	boolean ret = false;
    	for (Player ply : Bukkit.getServer().getOnlinePlayers()) {
    		if (ply.getName().equalsIgnoreCase(player)){
    			ret = true;
    		}
    	}
    	return ret;
    }
    
    public Player getplayerobj(String playername) {  //get object Player from player name; returns null if player not found (returned type is Player) 
    	for (Player ply : Bukkit.getServer().getOnlinePlayers()) {
    		if (ply.getName().equalsIgnoreCase(playername)) {
    			return ply;
    		}
    	}
    	return null;
    }
    
    public static boolean ifnumber(String input, Boolean hasdecs) {  //if hasdecs = true, number can be a decimal. if hasdecs = false, number can only be integer
    	if (hasdecs = true) {
    		return input.matches("[+-]?\\d*(\\.\\d+)?");
    	}
    	else {
    		return input.matches("[+-]?\\d+");
    	}
    }
    
    //LISTENER
    
    //	for events
    
    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
         event.getPlayer().sendMessage("Thanks for creating me. I'll keep an eye on your stuff. Love, Gary.");
    }
    
    //	for commands
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
    	if(cmd.getName().equalsIgnoreCase("getmaprep")){ 
    		if (args.length != 1) {
    			sender.sendMessage("use like '/getmaprep [player]'");
    			return true;
    		}
    		if (checkmaprep(args[0])){
    			sender.sendMessage("Player "+args[0]+" has "+getmaprep(args[0])+" reputation.");
    			return true;
    		}
    		else
    		{
    			sender.sendMessage("Player '"+args[0]+"' doesn't exist.");
    			return true;
    		}
    	} 
    	
    	if(cmd.getName().equalsIgnoreCase("setmaprep")){ //command to set a player's reputation in the hashmap
    		if (args.length == 2) {  //checks if 2 arguments (player, value) have been given
    			if (ifnumber(args[1], false) == true){  //checks if 2nd argument (reputation) is a valid int
    				if (ifplayer(args[0])) {  //checks if user exists on the server
    					setmaprep(args[0], Integer.parseInt(args[1]));
    					sender.sendMessage(args[0]+"'s reputation has been set to "+Integer.parseInt(args[1]));
    				}
    				else {
    					sender.sendMessage("Player '"+args[0]+"doesn't exist");
    					return true;
    				}
    			}
    			else {
    				sender.sendMessage("Reputation needs to be a numeric value");
    				return true;
    			}
    		}
    		else { 
    			sender.sendMessage("use like '/setmaprep [player] [value]'");
    			return true;
    		}
    	} 
    	
    	if(cmd.getName().equalsIgnoreCase("altmaprep")){ //command to alter a player's reputation in the hashmap
    		if (args.length == 2) {  //checks if 2 arguments (player, value) have been given
    			if (ifnumber(args[1], false) == true){  //checks if 2nd argument (reputation) is a valid int
    				if (ifplayer(args[0])) {  //checks if user exists on the server
    					setmaprep(args[0], Integer.parseInt(args[1]));
    					sender.sendMessage(args[0]+"'s reputation has been changed by "+args[1]+ " to "+(getmaprep(args[0])+Integer.parseInt(args[1])));
    				}
    				else {
    					sender.sendMessage("Player '"+args[0]+"doesn't exist");
    					return true;
    				}
    			}
    			else {
    				sender.sendMessage("Reputation needs to be a numeric value");
    				return true;
    			}
    		}
    		else { 
    			sender.sendMessage("use like '/setmaprep [player] [value]'");
    			return true;
    		}
    	} 
    	
    	//template for new commands
    	/* 
    	if(cmd.getName().equalsIgnoreCase("command")){ 
    		//dosomething
    	} 
    	*/
    	return false; 
    }


    
}
