package com.gmail.derpwave;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.logging.Level;

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
	
	HashMap<String, Integer> repmap  = new HashMap<String, Integer>();
	
	//FUNCTIONS
	
	//	defaults
	
    public void onEnable(){ 
    	Bukkit.getServer().getLogger().log(Level.INFO, "Thanks for creating me. I'll keep an eye on your stuff. Love, Gary.");
    	repmap.clear(); //make sure the hashmap is empty
    	repmap.put("Nixodas", 3);
    	repmap.put("Itanshir", 12);
    	repmap.put("Penisfisch", 34245);
    }
     
    public void onDisable(){ 
    	
    }
    
    //handling serialization of repmap (i.e. streaming repmap contents into a file to avoid data loss on server shutdown/crash)
    
	public void saverepmap() {
		
		try {
			
			File dataFolder = getDataFolder();
			if(!dataFolder.exists()) {
				dataFolder.mkdir();
			}
			File saveTo = new File(getDataFolder(), "data.txt");
			if (!saveTo.exists()){
				saveTo.createNewFile();
			}
			FileWriter fw = new FileWriter(saveTo, true);
			PrintWriter pw = new PrintWriter(fw);
			for (String key : repmap.keySet()) {  //iterate through all repmap entries
				pw.println(key+";"+getmaprep(key));  //write all repmap entries into the 
			}
			pw.flush();
			pw.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public void asdasd() {
		
	}
	
	/*
	public void readrepmap() {
		try
			{
				FileInputStream fileIn = new FileInputStream("filedrepmap.ser");
				ObjectInputStream in = new ObjectInputStream(fileIn);
				try {
				repmap = (HashMap) in.readObject();
			} 
			catch (ClassNotFoundException e) {
				Bukkit.getServer().getLogger().log(Level.INFO, "repmap could not be read from file");
				e.printStackTrace();
			}
	         in.close();
	         fileIn.close();
	      }
		catch(IOException i) { 
			Bukkit.getServer().getLogger().log(Level.INFO, "repmap could not be read from file");
			i.printStackTrace();
			return;
			}
	    }
    */
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
         event.getPlayer().sendMessage("");
    }
    
    //	for commands
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
    	if(cmd.getName().equalsIgnoreCase("getmaprep")){  //command to get a player's reputation from the hashmap
    		if (args.length != 1) {
    			sender.sendMessage("use like '/getmaprep [player]'");
    			return true;
    		}
    		if (checkmaprep(args[0]) == false){
    			sender.sendMessage("Player '"+args[0]+"' doesn't exist.");
    			return true;
    		}
			sender.sendMessage("Player "+args[0]+" has "+getmaprep(args[0])+" reputation.");
			return true;
    	} 
    	
		if(cmd.getName().equalsIgnoreCase("setmaprep")){ //command to set a player's reputation in the hashmap
			if (args.length != 2) {  //checks if 2 arguments (player, value) have been given
				sender.sendMessage("use like '/setmaprep [player] [value]'");
				return true;
			}
			if (ifplayer(args[0]) == false) {  //checks if player exists on the server
				sender.sendMessage("Player '"+args[0]+"doesn't exist");
				return true;
			}
			if (ifnumber(args[1], false) == false){  //checks if 2nd argument (reputation) is a valid int
				sender.sendMessage("Reputation needs to be a numeric value");
				return true;
			}
			setmaprep(args[0], Integer.parseInt(args[1]));
			sender.sendMessage(args[0]+"'s reputation has been set to "+Integer.parseInt(args[1]));
			return true;
		}
    	
		if(cmd.getName().equalsIgnoreCase("altmaprep")){ //command to alter a player's reputation in the hashmap
			if (args.length != 2) {  //checks if 2 arguments (player, value) have been given
				sender.sendMessage("use like '/altmaprep [player] [value]'");
				return true;
			}
			if (ifplayer(args[0]) == false) {  //checks if player exists on the server
				sender.sendMessage("Player '"+args[0]+"doesn't exist");
				return true;
			}
			if (ifnumber(args[1], false) == false){  //checks if 2nd argument (reputation) is a valid int
				sender.sendMessage("Reputation needs to be a numeric value");
				return true;
			}
			altmaprep(args[0], Integer.parseInt(args[1]));
			sender.sendMessage(args[0]+"'s reputation has been changed by "+args[1]+" to "+Integer.parseInt(args[1]));
			return true;
		}
		
		if(cmd.getName().equalsIgnoreCase("savereps")){
			saverepmap();
			sender.sendMessage("herpaderp");
			return true;
		}
		
		if(cmd.getName().equalsIgnoreCase("readreps")){
			sender.sendMessage(getDataFolder().getAbsolutePath());
			return true;
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
