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
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.io.Files;



public class Reputation extends JavaPlugin implements Listener {

	public static void main(String[] args) {
		
	}
	
	//NOTES
	
	// logger("");  //write something in the server log
	
	//CONSTRUCTORS
	
	HashMap<String, Integer> repmap  = new HashMap<String, Integer>();  //hashmap that stores rep values
	File dataloc = getDataFolder();  //location of plugin data (including repfile)
	File repfile = new File(dataloc, "rep.txt");  //file that stores rep values
	
	
	//FUNCTIONS
	
	//	defaults
	
    public void onEnable(){ 
    	getServer().getPluginManager().registerEvents(this, this);
    	logger("Thanks for creating me. I'll keep an eye on your stuff. Love, Gary.");
    	if (!dataloc.exists()) {  //create the plugin's data folder if it doesn't exist (in /plugins, named like plugin)
    		dataloc.mkdir();
    		logger("Plugin data folder doesn't exist; creating... (at "+dataloc.getAbsolutePath()+")");
    		}  
    	else {
    		logger("Plugin data folder found at "+dataloc.getAbsolutePath());
    	}
    	if (!repfile.exists()) {  //create the rep backup file if it doesn't exist (in /plugins/pluginname)
    		try {
    			repfile.createNewFile();
    			logger("Player reputation backup file doesn't exist; creating '"+repfile.getName()+"' at "+dataloc.getAbsolutePath());
    			} 
    		catch (IOException e) {
    			e.printStackTrace();
    			logger("Failed to create reputation backup file '"+repfile.getName()+"' at "+dataloc.getAbsolutePath());
    			}
    		}
    	else {  //if rep backup file does exist, load values to repmap
    		loadrepmap();
    		logger("Player reputation backup file '"+repfile.getName()+"' found at "+dataloc.getAbsolutePath()+"; loading");
    	}
    	if (Bukkit.getServer().getOnlinePlayers().length != 0)  //check if any players are online on plugin load (reloading plugins without server reboot does that) 
    	{
    		logger("At least one player already online on plugin load; assigning metadata");
    		Player[] players = Bukkit.getServer().getOnlinePlayers();
    		for (int i=0; i<players.length; i++) {  //iterate through array of all players online on startup
    			if (ifmaprep(players[i].getName()) == true) {  //check if player has a reputation set in the repmap to be loaded into metadata
        			setmetarep(players[i], getmaprep(players[i].getName()));  //load rep values from previously loaded repmap to player metadata
        			players[i].sendMessage("§aWelcome back, §e"+players[i].getName()+"§a. Your server reputation is §e"+getmetarep(players[i])+"§a. ");
    			}
    			else {  
    	    		setmaprep(players[i].getName(), 0);  //new repmap entry with value 0 for player
    	    		setmetarep(players[i], 0);  //set metadata rep value to 0
    	    		players[i].sendMessage("§aIt seems you're new here. Your server reputation is §e0§a.");
    			}

    		}
    	}
    	else {
    		logger("Zero players online on plugin load");
    	}
    }
     
    public void onDisable(){ 
    	logger("on disable");
    	Player[] players = Bukkit.getOnlinePlayers();
    	for (Player player : players) {  //iterate through all players currently online
    		setmaprep(player.getName(), getmetarep(player));
    	}
    	saverepmap();
    	logger("Disabling Reputation plugin; Updating repmap from player metadata; saving repmap to "+repfile.getAbsolutePath());
    }
    
    //File IO
    
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
			logger("Failed to save data to '"+repfile.getName()+"' at "+dataloc.getAbsolutePath()+" ("+e.getMessage()+")");
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
			logger("Failed to fetch data from '"+repfile.getName()+"' at "+dataloc.getAbsolutePath()+" ("+e.getMessage()+")");
		}
		catch (IOException e) {
			e.printStackTrace();
			logger("Failed to fetch data from '"+repfile.getName()+"' at "+dataloc.getAbsolutePath()+" ("+e.getMessage()+")");
		}
	}
    

    //	maprep functions
    
    public void setmaprep(String player, Integer value) {  //sets a rep value in the hashmap; creates entry if key (player name) doesn't exist yet, replaces otherwise
    	repmap.put(player, value);
    }
    
    public Integer getmaprep(String player) {  //returns the reputation of a player as int. Don't forget to check if entry exists first with ifmaprep()
    	return repmap.get(player);
    }
    
    public void altmaprep(String player, Integer value) {  //alters a player's reputation by a given value, which can be negative
    	setmaprep(player, getmaprep(player)+value);
    }

    public boolean ifmaprep(String player) {  //checks if entry for player exists in the repmap
    	return repmap.containsKey(player);
    }
    
    //	metarep functions
    
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
    
    public void altmetarep(Player player, Integer value) {
    	setmetarep(player, getmetarep(player)+value);
    }
    
    public boolean ifmetarep(Player player) {  //checks if entry for player exists in the repmap
    	return !player.getMetadata("rep").isEmpty();
    }
    
    //	misc functions
    
    public boolean ifplayeronline(String player) {  //checks if player exists on the server
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
    
    public static boolean ifnumber(String input, Boolean hasdecs) {  //check if input string is a valid number (hasdecs determines if decimals are allowed)
    	if (hasdecs = true) {
    		return input.matches("[+-]?\\d*(\\.\\d+)?");
    	}
    	else {
    		return input.matches("[+-]?\\d+");
    	}
    }
    
    public void logger(String text) {
    	Bukkit.getServer().getLogger().log(Level.INFO, "[Reputation] "+text);
    }
    
    public Plugin getplugin(String name) {
    	return Bukkit.getServer().getPluginManager().getPlugin(name);
    }
    
    //LISTENER
    
    //	for events
    
    @EventHandler
	public void onLogin(PlayerJoinEvent event) {
    	if (!ifmaprep(event.getPlayer().getName())) {
    		logger("No entry found for player in hashmap; creating entry");
    		setmaprep(event.getPlayer().getName(), 0);
    		setmetarep(event.getPlayer(), 0);
    		event.getPlayer().sendMessage("§aIt seems you're new here. Your server reputation is §e0§a.");
    	}
    	else {
    		logger("Player found in hashmap; loading to metadata");
    		setmetarep(event.getPlayer(), getmaprep(event.getPlayer().getName()));  //fetch 
    		event.getPlayer().sendMessage("§aWelcome back, §e"+event.getPlayer().getName()+"§a. Your server reputation is §e"+getmaprep(event.getPlayer().getName())+"§a. ");
    	}
    }
    
    @EventHandler
    public void onQuit(PlayerQuitEvent quit) {
    	logger("onQuit");
    	setmaprep(quit.getPlayer().getName(), getmetarep(quit.getPlayer()));  //updates the rep value in repmap to the value stored in the player's metadata
    	logger("Player "+quit.getPlayer().getName()+" quit; saving metadata to repmap");
    }
    
    //	for commands
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
    	
    	if(cmd.getName().equalsIgnoreCase("saverepmap")){ 
    		saverepmap();
    		return true;
    	} 
    	
		if(cmd.getName().equalsIgnoreCase("changerep")){ //command to alter a player's reputation in the hashmap
			if (args.length != 2) {  //checks if 2 arguments (player, value) have been given
				sender.sendMessage("use like '/changerep [player] [value]'");
				return true;
			}
			if (ifmetarep(getplayerobj(args[0])) == false) {  //checks if player exists on the server
				sender.sendMessage("Player '"+args[0]+" doesn't exist");
				return true;
			}
			if (ifnumber(args[1], false) == false){  //checks if 2nd argument (reputation) is a valid int
				sender.sendMessage("Reputation needs to be a numeric value");
				return true;
			}
			altmetarep(getplayerobj(args[0]), Integer.parseInt(args[1]));
			sender.sendMessage("§e"+args[0]+"§a's reputation has been changed by §e"+sender.getName()+"§a from §e"+args[1]+"§e to §a"+getmetarep(getplayerobj(args[0])));
			return true;
		}
		
		if(cmd.getName().equalsIgnoreCase("saverepmap")){
			saverepmap();
			sender.sendMessage("repmap has been saved to "+dataloc.getAbsolutePath());
			return true;
		}
    	
    	if(cmd.getName().equalsIgnoreCase("setrep")){ 
			if (args.length != 2) {  //checks if 2 arguments (player, value) have been given
				sender.sendMessage("§ause like '§e/setrep [player] [value]§a'");
				return true;
			}
			if (ifplayeronline(args[0]) == false) {  //checks if player exists on the server
				sender.sendMessage("§aPlayer '§e"+args[0]+"§a' doesn't exist");
				return true;
			}
			if (ifnumber(args[1], false) == false){  //checks if 2nd argument (reputation) is a valid int
				sender.sendMessage("§aReputation needs to be a numeric value");
				return true;
			}
			setmetarep(getplayerobj(args[0]), Integer.parseInt(args[1]));
			sender.sendMessage("§e"+args[0]+"'s §areputation has been set to §e"+Integer.parseInt(args[1])+"§a.");
    		return true;
    	} 
    	
    	if(cmd.getName().equalsIgnoreCase("getrep")){ 
    		if (args.length != 1) {
    			sender.sendMessage("§ause like '/getrep [player]'");
    			return true;
    		}
    		if (ifmaprep(args[0]) == false){
    			sender.sendMessage("§aPlayer '§e"+args[0]+"§a' doesn't exist.");
    			return true;
    		}
			sender.sendMessage("§aPlayer§e "+args[0]+"§a has §e"+getmetarep(getplayerobj(args[0]))+" §areputation.");
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
