package me.prisonranksx.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import me.prisonranksx.PrisonRanksX;
import me.prisonranksx.utils.XUUID;

public class PlayerDataStorage {
	
	private PrisonRanksX main;
	private Map<String, PlayerDataHandler> playerData;
	private Set<String> loadedUUIDs;
	private String defaultPath = null;
	private String defaultRank = null;
	
	public PlayerDataStorage(PrisonRanksX main) {this.main = main;
	  this.playerData = new HashMap<String, PlayerDataHandler>();
	  this.loadedUUIDs = new HashSet<>();
	  defaultPath = this.main.globalStorage.getStringData("defaultpath");
	  defaultRank = this.main.globalStorage.getStringData("defaultrank");
	}
    
	public Set<String> getLoadedUUIDs() {
		return this.loadedUUIDs;
	}
	
	public Map<String, PlayerDataHandler> getPlayerData() {
		return this.playerData;
	}
	
	
	@Deprecated
	public void loadPlayersData() {
		for(String strg : main.configManager.rankDataConfig.getConfigurationSection("players").getKeys(false)) {
			XUser xu = XUser.getXUser(strg);
			String str = xu.getUUID().toString();
			PlayerDataHandler pdh = new PlayerDataHandler(xu);
			RankPath rankPath = new RankPath(main.configManager.rankDataConfig.getString("players." + str + ".rank"), main.configManager.rankDataConfig.getString("players." + str + ".path"));
			if(main.configManager.prestigeDataConfig.getString("players." + str) != null) {
				pdh.setPrestige(main.configManager.prestigeDataConfig.getString("players."+ str));
			}
			if(main.configManager.rebirthDataConfig.getString("players." + str) != null) {
				pdh.setRebirth(main.configManager.rebirthDataConfig.getString("players." + str));
			}
			pdh.setRankPath(rankPath);
			pdh.setUUID(xu.getUUID());
			getPlayerData().put(str, pdh);
		}

	}
	
	@Deprecated
	public void loadPlayerData(OfflinePlayer player) {
		    XUser xu = XUser.getXUser(player);
			PlayerDataHandler pdh = new PlayerDataHandler(xu);
			RankPath rankPath = new RankPath(main.configManager.rankDataConfig.getString("players." + xu.getUUID() + ".rank"), main.configManager.rankDataConfig.getString("players." + xu.getUUID() + ".path"));
			if(main.configManager.prestigeDataConfig.getString("players." + xu.getUUID()) != null) {
				pdh.setPrestige(main.configManager.prestigeDataConfig.getString("players." + xu.getUUID()));
			}
			if(main.configManager.rebirthDataConfig.getString("players." + xu.getUUID()) != null) {
				pdh.setRebirth(main.configManager.rebirthDataConfig.getString("players." + xu.getUUID()));
			}
			pdh.setRankPath(rankPath);
			pdh.setUUID(xu.getUUID());
			getPlayerData().put(xu.getUUID().toString(), pdh);
	}
	
	public void loadPlayerData(Player player) {
		XUser xu = XUser.getXUser(player);
		if(main.isMySql()) {
			ResultSet result = null;
			try {
				result = main.getMySqlStatement().executeQuery("SELECT * FROM " + main.database + "." + main.table + " WHERE UUID = " + xu.getUUID().toString() + ";");
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			String rank = null;
			String prestige = null;
			String rebirth = null;
			String path = null;
			try {
				while (result.next()) {
				    rank = result.getString("rank") == null ? defaultRank : result.getString("rank");
				    prestige = result.getString("prestige");
				    rebirth = result.getString("rebirth");
				    path = result.getString("path") == null ? defaultPath : result.getString("path");
				}
				PlayerDataHandler pdh = new PlayerDataHandler(xu);
				RankPath rankPath = new RankPath(rank, path);
				if(prestige != null) {
					pdh.setPrestige(prestige);
				}
				if(rebirth != null) {
					pdh.setRebirth(rebirth);
				}
				pdh.setRankPath(rankPath);
				pdh.setUUID(xu.getUUID());
				getPlayerData().put(xu.getUUID().toString(), pdh);
				loadedUUIDs.add(xu.getUUID().toString());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		PlayerDataHandler pdh = new PlayerDataHandler(xu);
		RankPath rankPath = new RankPath(main.configManager.rankDataConfig.getString("players." + xu.getUUID() + ".rank"), main.configManager.rankDataConfig.getString("players." + xu.getUUID() + ".path"));
		if(main.configManager.prestigeDataConfig.getString("players." + xu.getUUID()) != null) {
			pdh.setPrestige(main.configManager.prestigeDataConfig.getString("players." + xu.getUUID()));
		}
		if(main.configManager.rebirthDataConfig.getString("players." + xu.getUUID()) != null) {
			pdh.setRebirth(main.configManager.rebirthDataConfig.getString("players." + xu.getUUID()));
		}
		pdh.setRankPath(rankPath);
		pdh.setUUID(xu.getUUID());
		getPlayerData().put(xu.getUUID().toString(), pdh);
		loadedUUIDs.add(xu.getUUID().toString());
}

	/**
	 * loads player data from mysql database or yaml file
	 * @param uuid
	 */
	public void loadPlayerData(UUID uuid) {
		XUser xu = XUser.getXUser(uuid.toString());
		if(main.isMySql()) {
			main.debug(uuid.toString());
			ResultSet result = null;
			try {
				result = main.getMySqlStatement().executeQuery("SELECT * FROM " + main.database + "." + main.table + " WHERE uuid = '" + xu.getUUID().toString() + "';");
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			String rank = null;
			String prestige = null;
			String rebirth = null;
			String path = null;
			try {
				while (result.next()) {
				    rank = result.getString("rank") == null ? defaultRank : result.getString("rank");
				    prestige = result.getString("prestige");
				    rebirth = result.getString("rebirth");
				    path = result.getString("path") == null ? defaultPath : result.getString("path");
					PlayerDataHandler pdh = new PlayerDataHandler(xu);
					RankPath rankPath = new RankPath(rank, path);
					if(prestige != null) {
						pdh.setPrestige(prestige);
					}
					if(rebirth != null) {
						pdh.setRebirth(rebirth);
					}
					pdh.setRankPath(rankPath);
					pdh.setUUID(xu.getUUID());
					getPlayerData().put(xu.getUUID().toString(), pdh);
					loadedUUIDs.add(xu.getUUID().toString());
				}

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}
		PlayerDataHandler pdh = new PlayerDataHandler(xu);
		if(!main.configManager.rankDataConfig.isConfigurationSection("players." + xu.getUUID().toString())) {
			return;
		}
		RankPath rankPath = new RankPath(main.configManager.rankDataConfig.getString("players." + xu.getUUID() + ".rank"), main.configManager.rankDataConfig.getString("players." + xu.getUUID() + ".path"));
		if(main.configManager.prestigeDataConfig.getString("players." + xu.getUUID()) != null) {
			pdh.setPrestige(main.configManager.prestigeDataConfig.getString("players." + xu.getUUID()));
		}
		if(main.configManager.rebirthDataConfig.getString("players." + xu.getUUID()) != null) {
			pdh.setRebirth(main.configManager.rebirthDataConfig.getString("players." + xu.getUUID()));
		}
		pdh.setRankPath(rankPath);
		pdh.setUUID(xu.getUUID());
		getPlayerData().put(xu.getUUID().toString(), pdh);
		loadedUUIDs.add(xu.getUUID().toString());
}
	
	public boolean isRegistered(OfflinePlayer player) {
		return getPlayerData().get(XUUID.getXUUID(player).toString()) != null;
	}
	
	public boolean isRegistered(String uuid) {
		return getPlayerData().get(uuid) != null;
	}
	
	public boolean isRegistered(UUID uuid) {
		return getPlayerData().get(uuid.toString()) != null;
	}
	
	public boolean isLoaded(UUID uuid) {
		return loadedUUIDs.contains(uuid.toString());
	}
	
	public boolean isLoaded(String uuid) {
		return loadedUUIDs.contains(uuid);
	}
	
	@Deprecated
	public boolean isLoaded(Player player) {
		return loadedUUIDs.contains(player.getUniqueId().toString());
	}
	
	@Deprecated
	public boolean isLoaded(OfflinePlayer player) {
		return loadedUUIDs.contains(player.getUniqueId().toString());
	}
	
	public void register(OfflinePlayer player) {
		getPlayerData().put(XUser.getXUser(player).getUUID().toString(), new PlayerDataHandler(XUser.getXUser(player)));
	}
	
	public void register(UUID uuid) {
		getPlayerData().put(uuid.toString(), new PlayerDataHandler(new XUser(uuid)));
	}
	
	public void register(String uuid) {
		getPlayerData().put(uuid, new PlayerDataHandler(XUser.getXUser(uuid)));
	}
	
	public boolean hasRankPath(OfflinePlayer player) {
		return getPlayerData().get(XUser.getXUser(player).getUUID().toString()).getRankPath() != null;
	}
	
	public boolean hasPrestige(OfflinePlayer player) {
		return getPlayerData().get(XUser.getXUser(player).getUUID().toString()).getPrestige() != null;
	}
	
	public boolean hasRebirth(OfflinePlayer player) {
		return getPlayerData().get(XUser.getXUser(player).getUUID().toString()).getRebirth() != null;
	}
	
	/**
	 * 
	 * @return list of player uuids
	 */
	public Set<String> getPlayers() {
		return getPlayerData().keySet();
	}
	
	public String getPlayerRank(OfflinePlayer player) {
		return getPlayerData().get(XUser.getXUser(player).getUUID().toString()).getRankPath().getRankName();
	}
	
	public String getPlayerRank(UUID uuid) {
		return getPlayerData().get(uuid.toString()).getRankPath().getRankName();
	}
	
	public void setPlayerRank(OfflinePlayer player, String rankName, String pathName) {
		RankPath rankPath = new RankPath(rankName, pathName);
		getPlayerData().get(XUser.getXUser(player).getUUID().toString()).setRankPath(rankPath);
	}
	
	public void setPlayerRank(OfflinePlayer player, String rankName) {
		RankPath rankPath = new RankPath(rankName, getPlayerData().get(XUser.getXUser(player).getUUID().toString()).getRankPath().getPathName());
		getPlayerData().get(XUser.getXUser(player).getUUID().toString()).setRankPath(rankPath);
	}
	
	public void setPlayerRank(OfflinePlayer player, RankPath rankPath) {
		getPlayerData().get(XUser.getXUser(player).getUUID().toString()).setRankPath(rankPath);
	}
	
	public void setPlayerRank(UUID uuid, RankPath rankPath) {
		getPlayerData().get(uuid.toString()).setRankPath(rankPath);
	}
	
	public void setPlayerRank(UUID uuid, String rankName) {
		RankPath rankPath = new RankPath(rankName, getPlayerData().get(XUser.getXUser(uuid.toString()).getUUID().toString()).getRankPath().getPathName());
		getPlayerData().get(XUser.getXUser(uuid.toString()).getUUID().toString()).setRankPath(rankPath);
	}
	
	public String getPlayerPrestige(OfflinePlayer player) {
		return getPlayerData().get(XUser.getXUser(player).getUUID().toString()).getPrestige();
	}
	
	public String getPlayerPrestige(UUID uuid) {
		return getPlayerData().get(uuid.toString()).getPrestige();
	}
	
	public void setPlayerPrestige(OfflinePlayer player, String prestigeName) {
		getPlayerData().get(XUser.getXUser(player).getUUID().toString()).setPrestige(prestigeName);
	}
	
	public void setPlayerPrestige(UUID uuid, String prestigeName) {
		getPlayerData().get(new XUser(uuid).getUUID().toString()).setPrestige(prestigeName);
	}
	
	public String getPlayerRebirth(OfflinePlayer player) {
		return getPlayerData().get(XUser.getXUser(player).getUUID().toString()).getRebirth();
	}
	
	public String getPlayerRebirth(UUID uuid) {
		return getPlayerData().get(uuid.toString()).getRebirth();
	}
	
	public void setPlayerRebirth(OfflinePlayer player, String rebirthName) {
		getPlayerData().get(XUser.getXUser(player).getUUID().toString()).setRebirth(rebirthName);
	}
	
	public void setPlayerRebirth(UUID uuid, String rebirthName) {
		getPlayerData().get(new XUser(uuid).getUUID().toString()).setRebirth(rebirthName);
	}
	
	public String getPlayerPath(OfflinePlayer player) {
		return getPlayerData().get(XUser.getXUser(player).getUUID().toString()).getRankPath().getPathName();
	}
	
	public String getPlayerPath(UUID uuid) {
		return getPlayerData().get(uuid.toString()).getRankPath().getPathName();
	}
	
	public void setPlayerPath(OfflinePlayer player, String pathName) {
		RankPath rankPath = new RankPath(getPlayerData().get(XUser.getXUser(player).getUUID().toString()).getRankPath().getRankName(), pathName);
		getPlayerData().get(XUser.getXUser(player).getUUID().toString()).setRankPath(rankPath);
	}
	
	public void setPlayerPath(UUID uuid, String pathName) {
		RankPath rankPath = new RankPath(getPlayerData().get(new XUser(uuid).getUUID().toString()).getRankPath().getRankName(), pathName);
		getPlayerData().get(new XUser(uuid).getUUID().toString()).setRankPath(rankPath);
	}
	
	public void setPlayerRankPath(OfflinePlayer player, RankPath rankPath) {
		getPlayerData().get(XUser.getXUser(player).getUUID().toString()).setRankPath(rankPath);
	}
	
	public void setPlayerRankPath(UUID uuid, RankPath rankPath) {
		getPlayerData().get(uuid.toString()).setRankPath(rankPath);
	}
	
	public RankPath getPlayerRankPath(OfflinePlayer player) {
		return getPlayerData().get(XUser.getXUser(player).getUUID().toString()).getRankPath();
	}
	
	public RankPath getPlayerRankPath(UUID uuid) {
		return getPlayerData().get(uuid.toString()).getRankPath();
	}
	
	public void savePlayersData() {
		if(main.isMySql()) {
			for(Entry<String, PlayerDataHandler> player : getPlayerData().entrySet()) {
				if(player.getKey() != null) {
					PlayerDataHandler value = player.getValue();
			    		try {
			    			String u = player.getKey();
			    			String rankName = value.getRankPath().getRankName() == null ? "A" : value.getRankPath().getRankName();
			    			String pathName = value.getRankPath().getPathName() == null ? "default" : value.getRankPath().getPathName();
			    			String prestigeName = value.getPrestige() == null ? null : value.getPrestige();
			    			String rebirthName = value.getRebirth() == null ? null : value.getRebirth();
			    			String name = main.isBefore1_7 ? "unknown" : Bukkit.getOfflinePlayer(UUID.fromString(u)).getName();
			    			ResultSet result = main.getMySqlStatement().executeQuery("SELECT * FROM " + main.database + "." + main.table + " WHERE uuid = '" + u + "'");
			    			if(result.next()) {
			    				main.getMySqlStatement().executeUpdate("DELETE FROM " + main.database + "." + main.table + " WHERE uuid = '" + u + "';");
			    				main.getMySqlStatement().executeUpdate("INSERT INTO " + main.database + "." + main.table + " (uuid, name, rank, prestige, rebirth, path) VALUES ('" + u + "', '" + name + "', '" + rankName + "', '" + prestigeName + "', '" + rebirthName + "', '" + pathName + "');");
			    			} else {
			    				main.getMySqlStatement().executeUpdate("INSERT INTO " + main.database + "." + main.table +" (uuid, name, rank, prestige, rebirth, path) VALUES ('" + u + "', '" + name + "', '" + rankName + "', '" + prestigeName + "', '" + rebirthName + "', '" + pathName + "');");
			    			}
			    		} catch (SQLException e1) {
			    			// TODO Auto-generated catch block
			    			Bukkit.getConsoleSender().sendMessage("�e[�9PrisonRanksX�e] �cSQL data update failed.");
			    			e1.printStackTrace();
			    			main.getLogger().info("ERROR Updating Player SQL Data");
			    		}
				}
			}
			return;
		}
			for(Entry<String, PlayerDataHandler> player : getPlayerData().entrySet()) {
				if(player.getKey() != null) {
		main.configManager.rankDataConfig.set("players." + player.getKey() + ".rank", player.getValue().getRankPath().getRank());
		main.configManager.rankDataConfig.set("players." + player.getKey() + ".path", player.getValue().getRankPath().getPath());
		main.configManager.prestigeDataConfig.set("players." + player.getKey(), player.getValue().getPrestige());
		main.configManager.rebirthDataConfig.set("players." + player.getKey(), player.getValue().getRebirth());
				}
			}
	}
	
	public void savePlayerData(OfflinePlayer player) {
		main.configManager.rankDataConfig.set("players." + player.getUniqueId().toString() + ".rank", getPlayerData().get(XUser.getXUser(player).getUUID().toString()).getRankPath().getRank());
		main.configManager.rankDataConfig.set("players." + player.getUniqueId().toString() + ".path", getPlayerData().get(XUser.getXUser(player).getUUID().toString()).getRankPath().getPath());
		main.configManager.prestigeDataConfig.set("players." + player.getUniqueId().toString(), getPlayerData().get(XUser.getXUser(player).getUUID().toString()).getPrestige());
		main.configManager.rebirthDataConfig.set("players." + player.getUniqueId().toString(), getPlayerData().get(XUser.getXUser(player).getUUID().toString()).getRebirth());
	}
	
}
