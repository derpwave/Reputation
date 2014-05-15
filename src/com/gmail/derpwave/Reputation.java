package com.gmail.derpwave;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.io.Files;



public class Reputation extends JavaPlugin implements Listener {

	public static void main(String[] args) {
		
	}
	
	//NOTES
	
	// Bukkit.getServer().getLogger().log(Level.INFO, "");  //write something in the server log
	
	//CONSTRUCTORS
	
	HashMap<String, Integer> repmap  = new HashMap<String, Integer>();  //hashmap that stores rep values
	File dataloc = getDataFolder();  //location of plugin data (including repfile)
	File repfile = new File(dataloc, "rep.txt");  //file that stores rep values
	
	
	//FUNCTIONS
	
	//	defaults
	
    public void onEnable(){ 
    	getServer().getPluginManager().registerEvents(this, this);
    	Bukkit.getServer().getLogger().log(Level.INFO, "[Reputation] Thanks for creating me. I'll keep an eye on your stuff. Love, Gary.");
    	if (!dataloc.exists()) {  //create the plugin's data folder if it doesn't exist (in /plugins, named like plugin)
    		dataloc.mkdir();
    		Bukkit.getServer().getLogger().log(Level.INFO, "[Reputation] Plugin data folder doesn't exist; creating... (at "+dataloc.getAbsolutePath()+")");
    		}  
    	else {
    		Bukkit.getServer().getLogger().log(Level.INFO, "[Reputation] Plugin data folder found at "+dataloc.getAbsolutePath());
    	}
    	if (!repfile.exists()) {  //create the rep backup file if it doesn't exist (in /plugins/pluginname)
    		try {
    			repfile.createNewFile();
    			Bukkit.getServer().getLogger().log(Level.INFO, "[Reputation] Player reputation backup file doesn't exist; creating '"+repfile.getName()+"' at "+dataloc.getAbsolutePath());
    			} 
    		catch (IOException e) {
    			e.printStackTrace();
    			Bukkit.getServer().getLogger().log(Level.INFO, "[Reputation] Failed to create reputation backup file '"+repfile.getName()+"' at "+dataloc.getAbsolutePath());
    			}
    		}
    	else {  //if rep backup file does exist, load values to repmap
    		loadrepmap();
    		Bukkit.getServer().getLogger().log(Level.INFO, "[Reputation] Player reputation backup file '"+repfile.getName()+"' found at "+dataloc.getAbsolutePath()+"; loading");
    	}
    }
     
    public void onDisable(){ 
    	//saverepmap();
    }
    
    //handling serialization of repmap (i.e. streaming repmap contents into a file to avoid data loss on server shutdown/crash)
    
	public void saverepmap() {  //save all reputation from the repmap to the backup file
		try {
			FileWriter fw = new FileWriter(repfile, false);  //overwrites all contents of the file
			PrintWriter pw = new PrintWriter(fw);
			for (String key : repmap.keySet()) {  //iterate through all repmap entries
				pw.println(key+";"+getmaprep(key));  //write all repmap entries into the file
			}
			pw.flush();
			pw.close();
		}
		catch (IOException e) {
			e.printStackTrace();
			Bukkit.getServer().getLogger().log(Level.INFO, "[Reputation] Failed to save data to '"+repfile.getName()+"' at "+dataloc.getAbsolutePath()+" ("+e.getMessage()+")");
		}
	}
	
	
	public void loadrepmap() {  //load all reputation from the backup file to the repmap
		repmap.clear();  //empty the repmap
		try {     
			List<String> stringList = Files.readLines(repfile, Charset.defaultCharset());  //fetches the contents of the file as string list
			for (Integer i=0; i < stringList.size(); i++) {  //loop for each entry in stringList
				String[] parts = stringList.get(i).split(";");  //split line at semicolon, e.g. "test;123" becomes part[0]="test" and part[1]="123" 
				setmaprep(parts[0], Integer.parseInt(parts[1]));  //parses the rep value in parts[1] as int, creates entry in repmap
			}
			//String[] stringArray = stringList.toArray(new String[]{});
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
			Bukkit.getServer().getLogger().log(Level.INFO, "[Reputation] Failed to fetch data from '"+repfile.getName()+"' at "+dataloc.getAbsolutePath()+" ("+e.getMessage()+")");
		}
		catch (IOException e) {
			e.printStackTrace();
			Bukkit.getServer().getLogger().log(Level.INFO, "[Reputation] Failed to fetch data from '"+repfile.getName()+"' at "+dataloc.getAbsolutePath()+" ("+e.getMessage()+")");
		}
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
    
	public void setmetarep(Player player, Integer value) {
		player.setMetadata("rep", new FixedMetadataValue(this,value));
	}
    
    
    public Integer getmetarep(Player player) {
    	  List<MetadataValue> values = player.getMetadata("rep");  
    	  for (MetadataValue value : values) {
    		     if (value.getOwningPlugin() == this) {
    		         return value.asInt();
    		      }
    	  }
    	  return null;
    	}
    
    //	misc
    
    public boolean ifplayeronline(String player) {  //checks if player exists on the server
    	boolean ret = false;
    	for (Player ply : Bukkit.getServer().getOnlinePlayers()) {
    		if (ply.getName().equalsIgnoreCase(player)){
    			ret = true;
    		}
    	}
    	return ret;
    }
    public boolean ifplayermapentry(String player) {  //checks if entry for player exists in the repmap
    	return repmap.containsKey(player);
    }
    
    public Player getplayerobj(String playername) {  //get object Player from player name; returns null if player not found (returned type is Player) 
    	for (Player ply : Bukkit.getServer().getOnlinePlayers()) {
    		if (ply.getName().equalsIgnoreCase(playername)) {
    			return ply;
    		}
    	}
    	return null;
    }
    
    public static boolean ifnumber(String input, Boolean hasdecs) {  //check if input string is a valid number (hasdecs determines if decimals are allowed)
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
	public void onLogin(PlayerJoinEvent event) {
    	if (!checkmaprep(event.getPlayer().getName())) {
    		Bukkit.getServer().getLogger().log(Level.INFO, "[Reputation] No entry found for player in hashmap; creating entry");
    		setmaprep(event.getPlayer().getName(), 0);
    		setmetarep(event.getPlayer(), 0);
    		event.getPlayer().sendMessage("$aIt seems you're new here. Your server reputation is §e0$a.");
    	}
    	else {
    		Bukkit.getServer().getLogger().log(Level.INFO, "[Reputation] Player found in hashmap; loading to metadata");
    		setmetarep(event.getPlayer(), getmaprep(event.getPlayer().getName()));  //fetch 
    		event.getPlayer().sendMessage("$aWelcome back. Your server reputation is §e"+getmaprep(event.getPlayer().getName())+"§f. ");
    	}
    }
    //	for commands
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
    	if(cmd.getName().equalsIgnoreCase("getmaprep")){  //command to get a player's reputation from the hashmap
    		if (args.length != 1) {
    			sender.sendMessage("$ause like '/getmaprep [player]'");
    			return true;
    		}
    		if (checkmaprep(args[0]) == false){
    			sender.sendMessage("$aPlayer '$e"+args[0]+"$a' doesn't exist.");
    			return true;
    		}
			sender.sendMessage("$aPlayer$e "+args[0]+"$a has $e"+getmaprep(args[0])+" $areputation.");
			return true;
    	} 
    	
		if(cmd.getName().equalsIgnoreCase("setmaprep")){ //command to set a player's reputation in the hashmap
			if (args.length != 2) {  //checks if 2 arguments (player, value) have been given
				sender.sendMessage("$ause like '$e/setmaprep [player] [value]$a'");
				return true;
			}
			if (ifplayermapentry(args[0]) == false) {  //checks if player exists on the server
				sender.sendMessage("$aPlayer '$e"+args[0]+"$a' doesn't exist");
				return true;
			}
			if (ifnumber(args[1], false) == false){  //checks if 2nd argument (reputation) is a valid int
				sender.sendMessage("$aReputation needs to be a numeric value");
				return true;
			}
			setmaprep(args[0], Integer.parseInt(args[1]));
			sender.sendMessage("$e"+args[0]+"'s $areputation has been set to $e"+Integer.parseInt(args[1])+"$a.");
			return true;
		}
    	
		if(cmd.getName().equalsIgnoreCase("altmaprep")){ //command to alter a player's reputation in the hashmap
			if (args.length != 2) {  //checks if 2 arguments (player, value) have been given
				sender.sendMessage("use like '/altmaprep [player] [value]'");
				return true;
			}
			if (ifplayermapentry(args[0]) == false) {  //checks if player exists on the server
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
		
		if(cmd.getName().equalsIgnoreCase("saverepmap")){
			saverepmap();
			sender.sendMessage("repmap has been saved to "+dataloc.getAbsolutePath());
			return true;
		}
		
		if(cmd.getName().equalsIgnoreCase("loadrepmap")){
			loadrepmap();
			sender.sendMessage("repmap has been cached from "+dataloc.getAbsolutePath());
			return true;
		}
    	
    	if(cmd.getName().equalsIgnoreCase("setmetarep")){ 
    		setmetarep(getplayerobj(args[0]), Integer.parseInt(args[1]));
    		return true;
    	} 
    	
    	if(cmd.getName().equalsIgnoreCase("getmetarep")){ 
    		sender.sendMessage("is "+getmetarep(getplayerobj(args[0])));
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
