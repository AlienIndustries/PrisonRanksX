package me.prisonranksx.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import io.samdev.actionutil.ActionUtil;
import me.prisonranksx.PrisonRanksX;
import me.prisonranksx.data.RankPath;
import me.prisonranksx.data.RankRandomCommands;
import me.prisonranksx.events.XRankupMaxEvent;

public class RankupMax {

	
	public Map<OfflinePlayer, String> rankupMaxMap = new HashMap<>();
	public Map<OfflinePlayer, List<String>> rankupMaxCommandsMap = new HashMap<>();
	public Map<OfflinePlayer, String> rankupMaxRecentRankupMap = new HashMap<>();
    public List<OfflinePlayer> rankupMaxProcess = new ArrayList<>();
    public Map<OfflinePlayer, Integer> rankupMaxStreak = new HashMap<>();
    public Map<OfflinePlayer, String> rankupFromMap = new HashMap<>();
    public Map<OfflinePlayer, List<String>> rankupMaxPassedRanks = new HashMap<>();
	public Map<OfflinePlayer, Integer> len = new HashMap<>();
	private PrisonRanksX main = (PrisonRanksX)Bukkit.getPluginManager().getPlugin("PrisonRanksX");
	private PRXAPI prxAPI;
	public RankupMax() {
		this.prxAPI = main.prxAPI;
	}
	
	@SuppressWarnings("unused")
	public void rankupMax(Player player) {
        Player p = player;
        String rankupFrom = null;
        if(rankupMaxProcess.contains(p)) {
        	p.sendMessage(prxAPI.g("rankupmax-is-on"));
        	return;
        }
        rankupMaxMap.remove(p);
        rankupMaxStreak.put(p, 0);
        rankupMaxProcess.add(p);
        //clear old data
        //player checking values
        RankPath rp1 = prxAPI.getPlayerRankPath(p);
        String currentRank = prxAPI.getPlayerRank(p);
        rankupFrom = currentRank;
        rankupFromMap.put(p, rankupFrom);
        String nextRank = prxAPI.getPlayerNextRank(p);
        Double playerBalance = main.econ.getBalance(p);
        main.debug("Before loop: " + nextRank);
        // other values
        List<String> ranksConfigList = prxAPI.getRanksCollection(rp1.getPathName());
        List<String> lastRankMessage = main.messagesStorage.getStringListMessage("lastrank");
        List<String> notEnoughMoneyMessage = main.messagesStorage.getStringListMessage("notenoughmoney");
        // if the player is at the latest rank
        if(nextRank == null) {
        	main.sendListMessage(p, lastRankMessage);
        	rankupMaxProcess.remove(p);
        	return;
        }
        // if he had a nextrank
        // player values
        String nextRankDisplay = main.getString(prxAPI.getPlayerRankupDisplay(p), p.getName());
	    Double nextRankCost = prxAPI.getPlayerRankupCostWithIncreaseDirect(p);
	    String nextRankCostInString = String.valueOf(nextRankCost);
        String nextRankCostFormatted = prxAPI.formatBalance(nextRankCost);
        //other values [2]
    	List<String> allRanksCommands = new ArrayList<>();
    	List<String> rankups = new ArrayList<>();
    	String rankupMessage = main.getString(main.messagesStorage.getStringMessage("rankup"), p.getName());
    	boolean isBroadcastLastRankOnly = main.globalStorage.getBooleanData("Options.rankupmax-broadcastlastrankonly");
    	boolean isMsgLastRankOnly = main.globalStorage.getBooleanData("Options.rankupmax-msglastrankonly");
    	boolean isRankupMsgLastRankOnly = main.globalStorage.getBooleanData("Options.rankupmax-rankupmsglastrankonly");
    	boolean isPerRankPermission = main.globalStorage.getBooleanData("Options.per-rank-permission");
    	String rankupNoPermissionMessage = main.getString(main.messagesStorage.getStringMessage("rankup-nopermission"), p.getName()).replace("%nextrank%", nextRank).replace("%rankup%", nextRank).replace("%rankup_display%", nextRankDisplay);
    	
        //if the rank cost is higher than player's balance
        if(nextRankCost > playerBalance) {
            for (String msg : notEnoughMoneyMessage) {
                p.sendMessage(main.getString(msg, p.getName()).replace("%player%", p.getName()).replace("%rankup_cost%", nextRankCostInString).replace("%rankup%", nextRank).replace("%rankup_display%", nextRankDisplay).replace("%rankup_cost_formatted%", nextRankCostFormatted));
            }
            rankupMaxProcess.remove(p);
        	return;
        }
        //if per rank permission option is enabled and the player dosen't has the required permission
        if(isPerRankPermission && !p.hasPermission(main.rankupCommand.getPermission() + "." + nextRank)) {
        	p.sendMessage(rankupNoPermissionMessage);
        	rankupMaxProcess.remove(p);
        	return;
        }
        //add new player data
 	   rankupMaxMap.put(p, prxAPI.getPlayerRank(p));
        //@@@@@@@@@@@@@@@@@@@@
        //==============
        //~~~~~~
        // loop section
        //~~~~~~
        //==============
        //@@@@@@@@@@@@@@@@@@@@
 	  main.getServer().getScheduler().runTaskAsynchronously(main, () -> {
        for(String rankSection : ranksConfigList) {
        	//loopValues
        	String loopCurrentRank = null;
        	String loopCurrentRankDisplay = null;
        	String loopNextRank = null;
        	Double loopNextRankCost = null;
        	String loopNextRankCostInString = null;
        	String loopNextRankCostFormatted = null;
        	String loopNextRankDisplay = null;
        	String loopRankupMsg = null;
        	List<String> loopNextRankCommands = new ArrayList<>();
        	List<String> loopNextRankBroadcast = new ArrayList<>();
        	List<String> loopNextRankMsg = new ArrayList<>();
        	List<String> loopNextRankActions = new ArrayList<>();
        	Double loopPlayerBalance = main.econ.getBalance(p);
        	//temporarily save player data in a map
        	   RankPath rp = new RankPath(rankupMaxMap.get(p), "default");
        	   loopCurrentRank = rankupMaxMap.get(p);
        	   //RankPath rp = main.playerStorage.getPlayerRankPath(p);
        	   loopCurrentRankDisplay = main.rankStorage.getDisplayName(rp);
        	   loopNextRank = main.rankStorage.getRankupName(rp);
        	   main.debug("After loop: " + loopNextRank);
        	   //if there is no rank next then stop the loop
        	   if(loopNextRank.equalsIgnoreCase("lastrank")) {
        		   main.sendListMessage(p, lastRankMessage);
        		   main.debug("After loop: lastrankmessage");
        		   rankupMaxProcess.remove(p);
        		   break;
        	   }
        	   //if not then continue and check for the cost
        	   loopNextRankCost = main.rankStorage.getRankupCost(rp);
        	   //if the player is prestiged then will we check with the increase percentage
               if(prxAPI.hasPrestiged(p)) {
              	loopNextRankCost = prxAPI.getIncreasedRankupCost(main.playerStorage.getPlayerPrestige(p), rp);
                }
               //update values
               loopNextRankCostInString = String.valueOf(loopNextRankCost);
               loopNextRankCostFormatted = prxAPI.formatBalance(loopNextRankCost);
               loopNextRankDisplay = main.getString(main.rankStorage.getRankupDisplayName(rp), p.getName());
               String loopRankupNoPermissionMessage = main.getString(main.messagesStorage.getStringMessage("rankup-nopermission"), p.getName()).replace("%nextrank%", loopNextRank).replace("%rankup%", loopNextRank).replace("%rankup_display%", loopNextRankDisplay);
        	   //check if the next rank cost is higher than player's balance
               if(loopNextRankCost > loopPlayerBalance) {
                   for (String msg : notEnoughMoneyMessage) {
                       p.sendMessage(main.getString(msg, p.getName()).replace("%player%", p.getName()).replace("%rankup_cost%", loopNextRankCostInString).replace("%rankup%", loopNextRank).replace("%rankup_display%", loopNextRankDisplay).replace("%rankup_cost_formatted%", loopNextRankCostFormatted));
                   }
                   rankupMaxProcess.remove(p);
            	   break;
               }
               //
               if(isPerRankPermission && !p.hasPermission(main.rankupCommand.getPermission() + "." + nextRank)) {
               	p.sendMessage(loopRankupNoPermissionMessage);
               	rankupMaxProcess.remove(p);
               	break;
               }
               //after check actions
               loopRankupMsg = main.getString(main.messagesStorage.getStringMessage("rankup"), p.getName()).replace("%rankup%", loopNextRank).replace("%rankup_display%", loopNextRankDisplay).replace("%rankup_cost%", loopNextRankCostInString).replace("%rankup_cost_formatted%", loopNextRankCostFormatted);
               if(!isRankupMsgLastRankOnly) {
            	   p.sendMessage(loopRankupMsg);
               }
               loopNextRankCommands = main.rankStorage.getRankupCommands(rp);
               
               if(loopNextRankCommands != null && !loopNextRankCommands.isEmpty()) {
               for(String cmd : loopNextRankCommands) {
            	   allRanksCommands.add(cmd.replace("%rankup%", loopNextRank).replace("%rankup_display%", loopNextRankDisplay).replace("%rank%", loopCurrentRank).replace("%player%", p.getName()).replace("%rankup_cost%", loopNextRankCostInString).replace("%rankup_cost_formatted%", loopNextRankCostFormatted));
               }
               }
               
   	        if(main.rankStorage.getAddPermissionList(rp) != null && !main.rankStorage.getAddPermissionList(rp).isEmpty()) {
  	          for(String addpermission : main.rankStorage.getAddPermissionList(rp)) {
  	        	main.perm.addPermission(p, addpermission.replace("%player%", p.getName()).replace("%rankup%", loopNextRank).replace("%rankup_display%", loopNextRankDisplay).replace("%rank%", loopCurrentRank));
  	          }
  	        }
  	        if(main.rankStorage.getDelPermissionList(rp) != null && !main.rankStorage.getDelPermissionList(rp).isEmpty()) {
  	        	for(String delpermission : main.rankStorage.getDelPermissionList(rp)) {
  	        		main.perm.delPermission(p, delpermission.replace("%player%", p.getName()).replace("%rankup%", loopNextRank).replace("%rankup_display%", loopNextRankDisplay).replace("%rank%", loopCurrentRank));
  	        	}
  	        }
               loopNextRankBroadcast = main.rankStorage.getBroadcast(rp);
               if(loopNextRankBroadcast != null && !isBroadcastLastRankOnly) {
                 for(String broadcast :  loopNextRankBroadcast) {
            	     Bukkit.broadcastMessage(main.getString(broadcast, p.getName()).replace("%player%", p.getName()).replace("%rankup%", loopNextRank).replace("%rankup_display%", loopNextRankDisplay).replace("%rankupdisplay%", loopNextRankDisplay));   
                 }
               }
               loopNextRankMsg = main.rankStorage.getMsg(rp);
               if(loopNextRankMsg != null && !isMsgLastRankOnly) {
                 for(String msg : loopNextRankMsg) {
            	     p.sendMessage(main.getString(msg, p.getName()).replace("%player%", p.getName()).replace("%rankup%", loopNextRank).replace("%rankup_display%", loopNextRankDisplay));   
                 }
               }
               loopNextRankActions = main.rankStorage.getActions(rp);
               if(main.isActionUtil && !loopNextRankActions.isEmpty()) {
            	   ActionUtil.executeActions(p, loopNextRankActions);
               }
   			Map<String, Double> chances = new HashMap<String, Double>();
   			RankRandomCommands rrc = main.rankStorage.getRandomCommandsManager(rp);
   			if(rrc != null && rrc.getRandomCommandsMap() != null) {
   			for(String section : rrc.getRandomCommandsMap().keySet()) {
   				Double chance = rrc.getChance(section);
   				chances.put(section, chance);
   			}
   			String randomSection = prxAPI.numberAPI.getChanceFromWeightedMap(chances);
   			if(rrc.getCommands(randomSection) != null) {
   			List<String> commands = rrc.getCommands(randomSection);
   			List<String> replacedCommands = new ArrayList<>();
   			for(String cmd : commands) {
   				String pCMD = prxAPI.cp(cmd.replace("%player%", p.getName()).replace("%rankup%", prxAPI.getPlayerNextRank(p)), p);
   				allRanksCommands.add(pCMD);
   			}
   			}
   			}
               //rankup things
               main.econ.withdrawPlayer(p, loopNextRankCost);
               rankupMaxStreak.put(p, main.plus(rankupMaxStreak.get(p)));
               rankups.add(loopNextRank);
               rankupMaxMap.put(p, loopNextRank);
               //prxAPI.setPlayerRank(p, loopNextRank);
        }
        main.executeCommandsSafely(p, allRanksCommands);
        allRanksCommands.clear();
        //end of loop
        //save player data
        RankPath rp = new RankPath(rankupMaxMap.get(p), "default");
    	List<String> endNextRankActionbarMessage = new ArrayList<>();
    	Integer endNextRankActionbarInterval = null;
    	List<String> endNextRankBroadcast = new ArrayList<>();
    	List<String> endNextRankMsg = new ArrayList<>();
    	String endRankupMessage = main.getString(main.messagesStorage.getStringMessage("rankup"), p.getName());
    	boolean endIsBroadcastLastRankOnly = main.globalStorage.getBooleanData("Options.rankupmax-broadcastlastrankonly");
    	boolean endIsMsgLastRankOnly = main.globalStorage.getBooleanData("Options.rankupmax-msglastrankonly");
    	boolean endIsRankupMsgLastRankOnly = main.globalStorage.getBooleanData("Options.rankupmax-rankupmsglastrankonly");
        if(endIsBroadcastLastRankOnly) {
            for(String broadcast :  endNextRankBroadcast) {
       	     Bukkit.broadcastMessage(main.getString(broadcast, p.getName()).replace("%player%", p.getName()).replace("%rankup_display%", main.getString(main.rankStorage.getDisplayName(rp), p.getName())));
            }
        }
        if(endIsMsgLastRankOnly) {
            for(String msg :  endNextRankMsg) {
          	     p.sendMessage(main.getString(msg, p.getName()).replace("%player%", p.getName()).replace("%rankup_display%", main.getString(main.rankStorage.getDisplayName(rp), p.getName())));  
             }
        }
        if(endIsRankupMsgLastRankOnly) {
        	p.sendMessage(rankupMessage.replace("%rankup%", rankupMaxMap.get(p)).replace("%rankup_display%", main.getString(main.rankStorage.getDisplayName(rp), p.getName())));
        }
        
        endNextRankActionbarMessage = main.rankStorage.getActionbarMessages(rp);
        endNextRankActionbarInterval = main.rankStorage.getActionbarInterval(rp);
		prxAPI.setPlayerRank(p, rankupMaxMap.get(p));
        main.animateActionbar(p, endNextRankActionbarInterval, endNextRankActionbarMessage);
        rankupMaxPassedRanks.put(p, rankups);
		XRankupMaxEvent x = new XRankupMaxEvent(p, rankupFromMap.get(p), rankupMaxMap.get(p), rankupMaxStreak.get(p), rankupMaxPassedRanks.get(p));
		Bukkit.getScheduler().runTask(main, () -> {
		main.getServer().getPluginManager().callEvent(x);
        rankupMaxMap.remove(p);
        rankupMaxProcess.remove(p);
        rankupMaxPassedRanks.remove(p);
        rankupFromMap.remove(p);
		});
 	  });
	}
	
}
