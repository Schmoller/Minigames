package au.com.mineauz.minigames.minigame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;

import au.com.mineauz.minigames.CTFFlag;
import au.com.mineauz.minigames.FloorDegenerator;
import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.MinigameSave;
import au.com.mineauz.minigames.MinigameTimer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.MultiplayerBets;
import au.com.mineauz.minigames.MultiplayerTimer;
import au.com.mineauz.minigames.blockRecorder.RecorderData;
import au.com.mineauz.minigames.config.BooleanFlag;
import au.com.mineauz.minigames.config.EnumFlag;
import au.com.mineauz.minigames.config.Flag;
import au.com.mineauz.minigames.config.IntegerFlag;
import au.com.mineauz.minigames.config.ListFlag;
import au.com.mineauz.minigames.config.LocationFlag;
import au.com.mineauz.minigames.config.LocationListFlag;
import au.com.mineauz.minigames.config.RewardsFlag;
import au.com.mineauz.minigames.config.SimpleLocationFlag;
import au.com.mineauz.minigames.config.StringFlag;
import au.com.mineauz.minigames.gametypes.MinigameType;
import au.com.mineauz.minigames.mechanics.GameMechanicBase;
import au.com.mineauz.minigames.mechanics.GameMechanics;
import au.com.mineauz.minigames.menu.Callback;
import au.com.mineauz.minigames.menu.InteractionInterface;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigames.menu.MenuItemAddFlag;
import au.com.mineauz.minigames.menu.MenuItemBoolean;
import au.com.mineauz.minigames.menu.MenuItemCustom;
import au.com.mineauz.minigames.menu.MenuItemDisplayLoadout;
import au.com.mineauz.minigames.menu.MenuItemDisplayRewards;
import au.com.mineauz.minigames.menu.MenuItemDisplayWhitelist;
import au.com.mineauz.minigames.menu.MenuItemFlag;
import au.com.mineauz.minigames.menu.MenuItemList;
import au.com.mineauz.minigames.menu.MenuItemLoadoutAdd;
import au.com.mineauz.minigames.menu.MenuItemNewLine;
import au.com.mineauz.minigames.menu.MenuItemPage;
import au.com.mineauz.minigames.menu.MenuItemSaveMinigame;
import au.com.mineauz.minigames.menu.MenuItemString;
import au.com.mineauz.minigames.menu.MenuItemTime;
import au.com.mineauz.minigames.minigame.modules.LoadoutModule;
import au.com.mineauz.minigames.minigame.modules.LobbySettingsModule;
import au.com.mineauz.minigames.minigame.modules.MinigameModule;
import au.com.mineauz.minigames.minigame.modules.TeamsModule;
import au.com.mineauz.minigames.minigame.reward.RewardItem;
import au.com.mineauz.minigames.minigame.reward.Rewards;

public class Minigame {
	private Map<String, Flag<?>> configFlags = new HashMap<String, Flag<?>>();
	
	private final String name;
	private StringFlag displayName = new StringFlag(null, "displayName");
	private StringFlag objective = new StringFlag(null, "objective");
	private StringFlag gametypeName = new StringFlag(null, "gametypeName");
	private EnumFlag<MinigameType> type = new EnumFlag<MinigameType>(MinigameType.SINGLEPLAYER, "type");
	private BooleanFlag enabled = new BooleanFlag(false, "enabled");
	private IntegerFlag minPlayers = new IntegerFlag(2, "minplayers");
	private IntegerFlag maxPlayers = new IntegerFlag(4, "maxplayers");
	private BooleanFlag spMaxPlayers = new BooleanFlag(false, "spMaxPlayers");
	private ListFlag flags = new ListFlag(new ArrayList<String>(), "flags");
	private MinigameState state = MinigameState.IDLE;
	
	private SimpleLocationFlag floorDegen1 = new SimpleLocationFlag(null, "sfloorpos.1");
	private SimpleLocationFlag floorDegen2 = new SimpleLocationFlag(null, "sfloorpos.2");
	private StringFlag degenType = new StringFlag("inward", "degentype");
	private IntegerFlag degenRandomChance = new IntegerFlag(15, "degenrandom");
	private FloorDegenerator sfloordegen;
	private IntegerFlag floorDegenTime = new IntegerFlag(Minigames.plugin.getConfig().getInt("multiplayer.floordegenerator.time"), "floordegentime");
	
	private LocationListFlag startLocations = new LocationListFlag(null, "startpos");
	private LocationFlag endPosition = new LocationFlag(null, "endpos");
	private LocationFlag quitPosition = new LocationFlag(null, "quitpos");
	private LocationFlag lobbyPosisiton = new LocationFlag(null, "lobbypos");
	private LocationFlag spectatorPosition = new LocationFlag(null, "spectatorpos");
	
	private Rewards rewardItem = new Rewards();
	private RewardsFlag rewardItemFlag = new RewardsFlag(null, "reward");
	private Rewards secondaryRewardItem = new Rewards();
	private RewardsFlag secondaryRewardItemFlag = new RewardsFlag(null, "reward2");
	private BooleanFlag usePermissions = new BooleanFlag(false, "usepermissions");
	private IntegerFlag timer = new IntegerFlag(0, "timer");
	private BooleanFlag useXPBarTimer = new BooleanFlag(true, "useXPBarTimer");
	private IntegerFlag startWaitTime = new IntegerFlag(0, "startWaitTime");
	
	private BooleanFlag itemDrops = new BooleanFlag(false, "itemdrops");
	private BooleanFlag deathDrops = new BooleanFlag(false, "deathdrops");
	private BooleanFlag itemPickup = new BooleanFlag(true, "itempickup");
	private BooleanFlag blockBreak = new BooleanFlag(false, "blockbreak");
	private BooleanFlag blockPlace = new BooleanFlag(false, "blockplace");
	private EnumFlag<GameMode> defaultGamemode = new EnumFlag<GameMode>(GameMode.ADVENTURE, "gamemode");
	private BooleanFlag blocksdrop = new BooleanFlag(true, "blocksdrop");
	private BooleanFlag allowEnderpearls = new BooleanFlag(false, "allowEnderpearls");
	private BooleanFlag allowMPCheckpoints = new BooleanFlag(false, "allowMPCheckpoints");
	private BooleanFlag allowFlight = new BooleanFlag(false, "allowFlight");
	private BooleanFlag enableFlight = new BooleanFlag(false, "enableFlight");
	
	private StringFlag mechanic = new StringFlag("custom", "scoretype");
	private BooleanFlag paintBallMode = new BooleanFlag(false, "paintball");
	private IntegerFlag paintBallDamage = new IntegerFlag(2, "paintballdmg");
	private BooleanFlag unlimitedAmmo = new BooleanFlag(false, "unlimitedammo");
	private BooleanFlag saveCheckpoints = new BooleanFlag(false, "saveCheckpoints");
	private BooleanFlag lateJoin = new BooleanFlag(false, "latejoin");
	private IntegerFlag lives = new IntegerFlag(0, "lives");
	
	private LocationFlag regenArea1 = new LocationFlag(null, "regenarea.1");
	private LocationFlag regenArea2 = new LocationFlag(null, "regenarea.2");
	private IntegerFlag regenDelay = new IntegerFlag(0, "regenDelay");
	
	private Map<String, MinigameModule> modules = new HashMap<String, MinigameModule>();
	
	private Scoreboard sbManager = Minigames.plugin.getServer().getScoreboardManager().getNewScoreboard();
	
	private IntegerFlag minScore = new IntegerFlag(5, "minscore");
	private IntegerFlag maxScore = new IntegerFlag(10, "maxscore");
	
	private BooleanFlag canSpectateFly = new BooleanFlag(false, "canspectatefly");
	
	private BooleanFlag randomizeChests = new BooleanFlag(false, "randomizechests");
	private IntegerFlag minChestRandom = new IntegerFlag(5, "minchestrandom");
	private IntegerFlag maxChestRandom = new IntegerFlag(10, "maxchestrandom");
	
	private ScoreboardData sbData = new ScoreboardData();
	
	//Unsaved data
	private List<MinigamePlayer> players = new ArrayList<MinigamePlayer>();
	private List<MinigamePlayer> spectators = new ArrayList<MinigamePlayer>();
	private RecorderData blockRecorder = new RecorderData(this);
	private boolean regenerating = false;
	//Multiplayer
	private MultiplayerTimer mpTimer = null;
	private MinigameTimer miniTimer = null;
	private MultiplayerBets mpBets = null;
	//CTF
	private Map<MinigamePlayer, CTFFlag> flagCarriers = new HashMap<MinigamePlayer, CTFFlag>();
	private Map<String, CTFFlag> droppedFlag = new HashMap<String, CTFFlag>();

	public Minigame(String name, MinigameType type, Location start){
		this.name = name;
		setup(type, start);
	}
	
	public Minigame(String name){
		this.name = name;
		setup(MinigameType.SINGLEPLAYER, null);
	}
	
	private void setup(MinigameType type, Location start){
		this.type.setFlag(type);
		startLocations.setFlag(new ArrayList<Location>());
		rewardItemFlag.setFlag(rewardItem);
		secondaryRewardItemFlag.setFlag(secondaryRewardItem);
		
		if(start != null)
			startLocations.getFlag().add(start);
		
		sbManager.registerNewObjective(this.name, "dummy");
		sbManager.getObjective(this.name).setDisplaySlot(DisplaySlot.SIDEBAR);
		
		for(Class<? extends MinigameModule> mod : Minigames.plugin.mdata.getModules()){
			try {
				addModule(mod.getDeclaredConstructor(Minigame.class).newInstance(this));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		addConfigFlag(allowEnderpearls);
		addConfigFlag(allowFlight);
		addConfigFlag(allowMPCheckpoints);
		addConfigFlag(blockBreak);
		addConfigFlag(blockPlace);
		addConfigFlag(blocksdrop);
		addConfigFlag(canSpectateFly);
		addConfigFlag(deathDrops);
		addConfigFlag(defaultGamemode);
		addConfigFlag(degenRandomChance);
		addConfigFlag(degenType);
		addConfigFlag(displayName);
		addConfigFlag(enableFlight);
		addConfigFlag(enabled);
		addConfigFlag(endPosition);
		addConfigFlag(flags);
		addConfigFlag(floorDegen1);
		addConfigFlag(floorDegen2);
		addConfigFlag(floorDegenTime);
		addConfigFlag(gametypeName);
		addConfigFlag(itemDrops);
		addConfigFlag(itemPickup);
		addConfigFlag(lateJoin);
		addConfigFlag(lives);
		addConfigFlag(lobbyPosisiton);
		addConfigFlag(maxChestRandom);
		addConfigFlag(maxPlayers);
		addConfigFlag(maxScore);
		addConfigFlag(minChestRandom);
		addConfigFlag(minPlayers);
		addConfigFlag(minScore);
		addConfigFlag(objective);
		addConfigFlag(paintBallDamage);
		addConfigFlag(paintBallMode);
		addConfigFlag(quitPosition);
		addConfigFlag(randomizeChests);
		addConfigFlag(regenArea1);
		addConfigFlag(regenArea2);
		addConfigFlag(regenDelay);
		addConfigFlag(rewardItemFlag);
		addConfigFlag(saveCheckpoints);
		addConfigFlag(mechanic);
		addConfigFlag(secondaryRewardItemFlag);
		addConfigFlag(spMaxPlayers);
		addConfigFlag(startLocations);
		addConfigFlag(startWaitTime);
		addConfigFlag(timer);
		addConfigFlag(this.type);
		addConfigFlag(unlimitedAmmo);
		addConfigFlag(usePermissions);
		addConfigFlag(useXPBarTimer);
		addConfigFlag(spectatorPosition);
	}
	
	public MinigameState getState(){
		return state;
	}
	
	public void setState(MinigameState state){
		this.state = state;
	}
	
	private void addConfigFlag(Flag<?> flag){
		configFlags.put(flag.getName(), flag);
	}
	
	public Flag<?> getConfigFlag(String name){
		return configFlags.get(name);
	}
	
	public boolean addModule(MinigameModule module){
		if(!modules.containsKey(module.getName())){
			modules.put(module.getName(), module);
			return true;
		}
		return false;
	}
	
	public void removeModule(String moduleName){
		modules.remove(moduleName);
	}
	
	public List<MinigameModule> getModules(){
		return new ArrayList<MinigameModule>(modules.values());
	}
	
	public MinigameModule getModule(String name){
		return modules.get(name);
	}
	
	public boolean isTeamGame(){
		if(getType() == MinigameType.MULTIPLAYER && TeamsModule.getMinigameModule(this).getTeams().size() > 0)
			return true;
		return false;
	}

	public List<RewardItem> getSecondaryRewardItem(){
		return secondaryRewardItem.getReward();
	}
	
	public Rewards getSecondaryRewardItems(){
		return secondaryRewardItem;
	}

	public List<RewardItem> getRewardItem(){
		return rewardItem.getReward();
	}
	
	public Rewards getRewardItems(){
		return rewardItem;
	}
	
	public boolean hasFlags(){
		return !flags.getFlag().isEmpty();
	}
	
	public void addFlag(String flag){
		flags.getFlag().add(flag);
	}
	
	public void setFlags(List<String> flags){
		this.flags.setFlag(flags);
	}
	
	public List<String> getFlags(){
		return flags.getFlag();
	}
	
	public boolean removeFlag(String flag){
		if(flags.getFlag().contains(flag)){
			flags.getFlag().remove(flag);
			return true;
		}
		return false;
	}
	
	public void setStartLocation(Location loc){
		startLocations.getFlag().set(0, loc);
	}
	
	public void addStartLocation(Location loc){
		startLocations.getFlag().add(loc);
	}
	
	public void addStartLocation(Location loc, int number){
		if(startLocations.getFlag().size() >= number){
			startLocations.getFlag().set(number - 1, loc);
		}
		else{
			startLocations.getFlag().add(loc);
		}
	}
	
	public List<Location> getStartLocations(){
		return startLocations.getFlag();
	}
	
	public boolean removeStartLocation(int locNumber){
		if(startLocations.getFlag().size() < locNumber){
			startLocations.getFlag().remove(locNumber);
			return true;
		}
		return false;
	}
	
	public void setSpectatorLocation(Location loc){
		spectatorPosition.setFlag(loc);
	}
	
	public Location getSpectatorLocation(){
		return spectatorPosition.getFlag();
	}
	
	public boolean isEnabled(){
		return enabled.getFlag();
	}

	public void setEnabled(boolean enabled){
		this.enabled.setFlag(enabled);
	}

	public int getMinPlayers(){
		return minPlayers.getFlag();
	}

	public void setMinPlayers(int minPlayers){
		this.minPlayers.setFlag(minPlayers);
	}

	public int getMaxPlayers(){
		return maxPlayers.getFlag();
	}

	public void setMaxPlayers(int maxPlayers){
		this.maxPlayers.setFlag(maxPlayers);
	}

	public boolean isSpMaxPlayers() {
		return spMaxPlayers.getFlag();
	}

	public void setSpMaxPlayers(boolean spMaxPlayers) {
		this.spMaxPlayers.setFlag(spMaxPlayers);
	}

	public Location getFloorDegen1(){
		return floorDegen1.getFlag();
	}

	public void setFloorDegen1(Location loc){
		this.floorDegen1.setFlag(loc);
	}

	public Location getFloorDegen2(){
		return floorDegen2.getFlag();
	}

	public void setFloorDegen2(Location loc){
		this.floorDegen2.setFlag(loc);
	}

	public String getDegenType() {
		return degenType.getFlag();
	}

	public void setDegenType(String degenType) {
		this.degenType.setFlag(degenType);
	}

	public int getDegenRandomChance() {
		return degenRandomChance.getFlag();
	}

	public void setDegenRandomChance(int degenRandomChance) {
		this.degenRandomChance.setFlag(degenRandomChance);
	}

	public Location getEndPosition(){
		return endPosition.getFlag();
	}

	public void setEndPosition(Location endPosition){
		this.endPosition.setFlag(endPosition);
	}

	public Location getQuitPosition(){
		return quitPosition.getFlag();
	}

	public void setQuitPosition(Location quitPosition){
		this.quitPosition.setFlag(quitPosition);
	}

	public Location getLobbyPosition(){
		return lobbyPosisiton.getFlag();
	}

	public void setLobbyPosition(Location lobbyPosisiton){
		this.lobbyPosisiton.setFlag(lobbyPosisiton);
	}
	
	public String getName(boolean useDisplay){
		if(useDisplay && displayName.getFlag() != null)
			return displayName.getFlag();
		return name;
	}

	public void setDisplayName(String displayName) {
		this.displayName.setFlag(displayName);
	}

	public MinigameType getType(){
		return type.getFlag();
	}
	
	private Callback<String> getTypeCallback(){
		return new Callback<String>() {

			@Override
			public void setValue(String value) {
				type.setFlag(MinigameType.valueOf(value.toUpperCase().replace(" ", "_")));
			}

			@Override
			public String getValue() {
				return MinigameUtils.capitalize(type.getFlag().toString().replace("_", " "));
			}
		};
	}
	
	public void setType(MinigameType type){
		this.type.setFlag(type);
	}
	
	public MultiplayerTimer getMpTimer() {
		return mpTimer;
	}

	public void setMpTimer(MultiplayerTimer mpTimer) {
		this.mpTimer = mpTimer;
	}
	
	@Deprecated
	public boolean isNotWaitingForPlayers(){
		if(getState() != MinigameState.WAITING){
			return true;
		}
		return false;
	}
	
	public boolean isWaitingForPlayers(){
		if(getState() == MinigameState.WAITING)
			return true;
		return false;
	}
	
	public boolean hasStarted(){
		if(getState() == MinigameState.STARTED || getState() == MinigameState.OCCUPIED)
			return true;
		return false;
	}
	
	public MinigameTimer getMinigameTimer() {
		return miniTimer;
	}

	public void setMinigameTimer(MinigameTimer mgTimer) {
		this.miniTimer = mgTimer;
	}

	public MultiplayerBets getMpBets() {
		return mpBets;
	}

	public void setMpBets(MultiplayerBets mpBets) {
		this.mpBets = mpBets;
	}

	public void setUsePermissions(boolean usePermissions) {
		this.usePermissions.setFlag(usePermissions);
	}

	public boolean getUsePermissions() {
		return usePermissions.getFlag();
	}
	
	public List<MinigamePlayer> getPlayers() {
		return players;
	}
	
	public void addPlayer(MinigamePlayer player){
		players.add(player);
	}
	
	public void removePlayer(MinigamePlayer player){
		if(players.contains(player)){
			players.remove(player);
		}
	}
	
	public boolean hasPlayers(){
		return !players.isEmpty();
	}
	
	public boolean hasSpectators(){
		return !spectators.isEmpty();
	}
	
	public List<MinigamePlayer> getSpectators() {
		return spectators;
	}
	
	public void addSpectator(MinigamePlayer player){
		spectators.add(player);
	}
	
	public void removeSpectator(MinigamePlayer player){
		if(spectators.contains(player)){
			spectators.remove(player);
		}
	}
	
	public boolean isSpectator(MinigamePlayer player){
		return spectators.contains(player);
	}
	
	public void setScore(MinigamePlayer ply, int amount){
		sbManager.getObjective(getName(false)).getScore(ply.getName()).setScore(amount);
	}

	public int getMinScore() {
		return minScore.getFlag();
	}

	public void setMinScore(int minScore) {
		this.minScore.setFlag(minScore);
	}

	public int getMaxScore() {
		return maxScore.getFlag();
	}

	public void setMaxScore(int maxScore) {
		this.maxScore.setFlag(maxScore);
	}
	
	public int getMaxScorePerPlayer(){
		float scorePerPlayer = getMaxScore() / getMaxPlayers();
		int score = (int) Math.round(scorePerPlayer * getPlayers().size());
		if(score < minScore.getFlag()){
			score = minScore.getFlag();
		}
		return score;
	}

	public FloorDegenerator getFloorDegenerator() {
		return sfloordegen;
	}

	public void addFloorDegenerator() {
		sfloordegen = new FloorDegenerator(getFloorDegen1(), getFloorDegen2(), this);
	}
	
	public void setTimer(int time){
		timer.setFlag(time);
	}
	
	public int getTimer(){
		return timer.getFlag();
	}

	public boolean isUsingXPBarTimer() {
		return useXPBarTimer.getFlag();
	}

	public void setUseXPBarTimer(boolean useXPBarTimer) {
		this.useXPBarTimer.setFlag(useXPBarTimer);
	}

	public int getStartWaitTime() {
		return startWaitTime.getFlag();
	}

	public void setStartWaitTime(int startWaitTime) {
		this.startWaitTime.setFlag(startWaitTime);
	}

	public boolean hasItemDrops() {
		return itemDrops.getFlag();
	}

	public void setItemDrops(boolean itemDrops) {
		this.itemDrops.setFlag(itemDrops);
	}

	public boolean hasDeathDrops() {
		return deathDrops.getFlag();
	}

	public void setDeathDrops(boolean deathDrops) {
		this.deathDrops.setFlag(deathDrops);
	}

	public boolean hasItemPickup() {
		return itemPickup.getFlag();
	}

	public void setItemPickup(boolean itemPickup) {
		this.itemPickup.setFlag(itemPickup);
	}

	public RecorderData getBlockRecorder() {
		return blockRecorder;
	}

	public boolean isRegenerating() {
		return regenerating;
	}

	public void setRegenerating(boolean regenerating) {
		this.regenerating = regenerating;
	}

	public boolean canBlockBreak() {
		return blockBreak.getFlag();
	}

	public void setCanBlockBreak(boolean blockBreak) {
		this.blockBreak.setFlag(blockBreak);
	}

	public boolean canBlockPlace() {
		return blockPlace.getFlag();
	}

	public void setCanBlockPlace(boolean blockPlace) {
		this.blockPlace.setFlag(blockPlace);
	}
	
	public GameMode getDefaultGamemode() {
		return defaultGamemode.getFlag();
	}
	
	public Callback<String> getDefaultGamemodeCallback(){
		return new Callback<String>() {

			@Override
			public void setValue(String value) {
				defaultGamemode.setFlag(GameMode.valueOf(value.toUpperCase()));
			}

			@Override
			public String getValue() {
				return MinigameUtils.capitalize(defaultGamemode.getFlag().toString());
			}
		};
	}

	public void setDefaultGamemode(GameMode defaultGamemode) {
		this.defaultGamemode.setFlag(defaultGamemode);
	}

	public boolean canBlocksdrop() {
		return blocksdrop.getFlag();
	}

	public void setBlocksdrop(boolean blocksdrop) {
		this.blocksdrop.setFlag(blocksdrop);
	}

	public String getMechanicName() {
		return mechanic.getFlag();
	}
	
	public GameMechanicBase getMechanic(){
		return GameMechanics.getGameMechanic(mechanic.getFlag());
	}

	public void setMechanic(String scoreType) {
		this.mechanic.setFlag(scoreType);
	}

	public boolean isFlagCarrier(MinigamePlayer ply){
		return flagCarriers.containsKey(ply);
	}
	
	public void addFlagCarrier(MinigamePlayer ply, CTFFlag flag){
		flagCarriers.put(ply, flag);
	}
	
	public void removeFlagCarrier(MinigamePlayer ply){
		flagCarriers.remove(ply);
	}
	
	public CTFFlag getFlagCarrier(MinigamePlayer ply){
		return flagCarriers.get(ply);
	}
	
	public void resetFlags(){
		for(MinigamePlayer ply : flagCarriers.keySet()){
			getFlagCarrier(ply).respawnFlag();
			getFlagCarrier(ply).stopCarrierParticleEffect();
		}
		flagCarriers.clear();
		for(String id : droppedFlag.keySet()){
			if(!getDroppedFlag(id).isAtHome()){
				getDroppedFlag(id).stopTimer();
				getDroppedFlag(id).respawnFlag();
			}
		}
		droppedFlag.clear();
	}
	
	public boolean hasDroppedFlag(String id){
		return droppedFlag.containsKey(id);
	}
	
	public void addDroppedFlag(String id, CTFFlag flag){
		droppedFlag.put(id, flag);
	}
	
	public void removeDroppedFlag(String id){
		droppedFlag.remove(id);
	}
	
	public CTFFlag getDroppedFlag(String id){
		return droppedFlag.get(id);
	}

	public boolean hasPaintBallMode() {
		return paintBallMode.getFlag();
	}

	public void setPaintBallMode(boolean paintBallMode) {
		this.paintBallMode.setFlag(paintBallMode);
	}

	public int getPaintBallDamage() {
		return paintBallDamage.getFlag();
	}

	public void setPaintBallDamage(int paintBallDamage) {
		this.paintBallDamage .setFlag(paintBallDamage);
	}

	public boolean hasUnlimitedAmmo() {
		return unlimitedAmmo.getFlag();
	}

	public void setUnlimitedAmmo(boolean unlimitedAmmo) {
		this.unlimitedAmmo.setFlag(unlimitedAmmo);
	}

	public boolean canSaveCheckpoint() {
		return saveCheckpoints.getFlag();
	}

	public void setSaveCheckpoint(boolean saveCheckpoint) {
		this.saveCheckpoints.setFlag(saveCheckpoint);
	}

	public boolean canLateJoin() {
		return lateJoin.getFlag();
	}

	public void setLateJoin(boolean lateJoin) {
		this.lateJoin.setFlag(lateJoin);
	}

	public boolean canSpectateFly() {
		return canSpectateFly.getFlag();
	}

	public void setCanSpectateFly(boolean canSpectateFly) {
		this.canSpectateFly.setFlag(canSpectateFly);
	}

	public boolean isRandomizeChests() {
		return randomizeChests.getFlag();
	}

	public void setRandomizeChests(boolean randomizeChests) {
		this.randomizeChests.setFlag(randomizeChests);
	}

	public int getMinChestRandom() {
		return minChestRandom.getFlag();
	}

	public void setMinChestRandom(int minChestRandom) {
		this.minChestRandom.setFlag(minChestRandom);
	}

	public int getMaxChestRandom() {
		return maxChestRandom.getFlag();
	}

	public void setMaxChestRandom(int maxChestRandom) {
		this.maxChestRandom.setFlag(maxChestRandom);
	}

	public Location getRegenArea1() {
		return regenArea1.getFlag();
	}

	public void setRegenArea1(Location regenArea1) {
		this.regenArea1.setFlag(regenArea1);
	}

	public Location getRegenArea2() {
		return regenArea2.getFlag();
	}

	public void setRegenArea2(Location regenArea2) {
		this.regenArea2.setFlag(regenArea2);
	}

	public int getRegenDelay() {
		return regenDelay.getFlag();
	}

	public void setRegenDelay(int regenDelay) {
		if(regenDelay < 0)
			regenDelay = 0;
		this.regenDelay.setFlag(regenDelay);
	}

	public int getLives() {
		return lives.getFlag();
	}

	public void setLives(int lives) {
		this.lives.setFlag(lives);
	}

	public int getFloorDegenTime() {
		return floorDegenTime.getFlag();
	}

	public void setFloorDegenTime(int floorDegenTime) {
		this.floorDegenTime.setFlag(floorDegenTime);
	}
	
	public boolean isAllowedEnderpearls() {
		return allowEnderpearls.getFlag();
	}

	public void setAllowEnderpearls(boolean allowEnderpearls) {
		this.allowEnderpearls.setFlag(allowEnderpearls);
	}

	public boolean isAllowedMPCheckpoints() {
		return allowMPCheckpoints.getFlag();
	}

	public void setAllowMPCheckpoints(boolean allowMPCheckpoints) {
		this.allowMPCheckpoints.setFlag(allowMPCheckpoints);
	}
	
	public boolean isAllowedFlight() {
		return allowFlight.getFlag();
	}

	public void setAllowedFlight(boolean allowFlight) {
		this.allowFlight.setFlag(allowFlight);
	}

	public boolean isFlightEnabled() {
		return enableFlight.getFlag();
	}

	public void setFlightEnabled(boolean enableFlight) {
		this.enableFlight.setFlag(enableFlight);
	}

	public Scoreboard getScoreboardManager(){
		return sbManager;
	}
	
	public String getObjective() {
		return objective.getFlag();
	}

	public void setObjective(String objective) {
		this.objective.setFlag(objective);
	}

	public String getGametypeName() {
		return gametypeName.getFlag();
	}

	public void setGametypeName(String gametypeName) {
		this.gametypeName.setFlag(gametypeName);
	}

	public void displayMenu(MinigamePlayer player){
		Menu main = new Menu(6, getName(false), player);
		Menu playerMenu = new Menu(6, getName(false), player);
		Menu loadouts = new Menu(6, getName(false), player);
		Menu flags = new Menu(6, getName(false), player);
		Menu lobby = new Menu(6, getName(false), player);
		
		List<MenuItem> itemsMain = new ArrayList<MenuItem>();
		itemsMain.add(enabled.getMenuItem("Enabled", Material.PAPER));
		itemsMain.add(usePermissions.getMenuItem("Use Permissions", Material.PAPER));
		List<String> mgTypes = new ArrayList<String>();
		for(MinigameType val : MinigameType.values()){
			mgTypes.add(MinigameUtils.capitalize(val.toString().replace("_", " ")));
		}
		itemsMain.add(new MenuItemList("Game Type", Material.PAPER, getTypeCallback(), mgTypes));
		List<String> scoreTypes = new ArrayList<String>();
		for(GameMechanicBase val : GameMechanics.getGameMechanics()){
			scoreTypes.add(MinigameUtils.capitalize(val.getMechanic()));
		}
		itemsMain.add(new MenuItemList("Game Mechanic", MinigameUtils.stringToList("Multiplayer Only"), Material.ROTTEN_FLESH, new Callback<String>() {

			@Override
			public void setValue(String value) {
				mechanic.setFlag(value.toLowerCase());
			}

			@Override
			public String getValue() {
				return MinigameUtils.capitalize(mechanic.getFlag());
			}
		}, scoreTypes));
		final MenuItemCustom mechSettings = new MenuItemCustom("Game Mechanic Settings", Material.PAPER);
		final Minigame mgm = this;
		final Menu fmain = main;
		mechSettings.setClick(new InteractionInterface() {
			
			@Override
			public Object interact(Object object) {
				if(getMechanic().displaySettings(mgm) != null && 
						getMechanic().displaySettings(mgm).displayMechanicSettings(fmain))
					return null;
				return mechSettings.getItem();
			}
		});
		itemsMain.add(mechSettings);
		MenuItemString obj = (MenuItemString) objective.getMenuItem("Objective Description", Material.DIAMOND);
		obj.setAllowNull(true);
		itemsMain.add(obj);
		obj = (MenuItemString) gametypeName.getMenuItem("Gametype Description", Material.SIGN);
		obj.setAllowNull(true);
		itemsMain.add(obj);
		obj = (MenuItemString) displayName.getMenuItem("Display Name", Material.SIGN);
		obj.setAllowNull(true);
		itemsMain.add(obj);
		itemsMain.add(new MenuItemNewLine());
		itemsMain.add(minScore.getMenuItem("Min. Score", Material.STEP, MinigameUtils.stringToList("Multiplayer Only")));
		itemsMain.add(maxScore.getMenuItem("Max. Score", Material.DOUBLE_STEP, MinigameUtils.stringToList("Multiplayer Only")));
		itemsMain.add(minPlayers.getMenuItem("Min. Players", Material.STEP, MinigameUtils.stringToList("Multiplayer Only")));
		itemsMain.add(maxPlayers.getMenuItem("Max. Players", Material.DOUBLE_STEP, MinigameUtils.stringToList("Multiplayer Only")));
		itemsMain.add(spMaxPlayers.getMenuItem("Enable Singleplayer Max Players", Material.IRON_FENCE));
		itemsMain.add(new MenuItemPage("Lobby Settings", MinigameUtils.stringToList("Multiplayer Only"), Material.WOOD_DOOR, lobby));
		itemsMain.add(new MenuItemNewLine());
		itemsMain.add(new MenuItemTime("Time Length", MinigameUtils.stringToList("Multiplayer Only"), Material.WATCH, new Callback<Integer>() {

			@Override
			public void setValue(Integer value) {
				timer.setFlag(value);
			}

			@Override
			public Integer getValue() {
				return timer.getFlag();
			}
		}, 0, null));
		itemsMain.add(useXPBarTimer.getMenuItem("Use XP bar as Timer", Material.ENDER_PEARL));
		itemsMain.add(new MenuItemTime("Start Wait Time", MinigameUtils.stringToList("Multiplayer Only"), Material.WATCH, new Callback<Integer>() {

			@Override
			public void setValue(Integer value) {
				startWaitTime.setFlag(value);
			}

			@Override
			public Integer getValue() {
				return startWaitTime.getFlag();
			}
		}, 3, null));
		itemsMain.add(lateJoin.getMenuItem("Allow Late Join", Material.DEAD_BUSH, MinigameUtils.stringToList("Multiplayer Only")));
		itemsMain.add(new MenuItemDisplayRewards("Primary Rewards", Material.CHEST, rewardItem));
		itemsMain.add(new MenuItemDisplayRewards("Secondary Rewards", Material.CHEST, secondaryRewardItem));
		itemsMain.add(new MenuItemDisplayWhitelist("Block Whitelist/Blacklist", MinigameUtils.stringToList("Blocks that can/can't;be broken"), 
				Material.CHEST, getBlockRecorder().getWBBlocks(), getBlockRecorder().getWhitelistModeCallback()));
		itemsMain.add(new MenuItemNewLine());
		List<String> floorDegenDes = new ArrayList<String>();
		floorDegenDes.add("Mainly used to prevent");
		floorDegenDes.add("islanding in spleef Minigames.");
		List<String> floorDegenOpt = new ArrayList<String>();
		floorDegenOpt.add("Inward");
		floorDegenOpt.add("Circle");
		floorDegenOpt.add("Random");
		itemsMain.add(new MenuItemList("Floor Degenerator Type", floorDegenDes, Material.SNOW_BLOCK, new Callback<String>() {

			@Override
			public void setValue(String value) {
				degenType.setFlag(value.toLowerCase());
			}

			@Override
			public String getValue() {
				return MinigameUtils.capitalize(degenType.getFlag());
			}
		}, floorDegenOpt));
		List<String> degenRandDes = new ArrayList<String>();
		degenRandDes.add("Chance of block being");
		degenRandDes.add("removed on random");
		degenRandDes.add("degeneration.");
		itemsMain.add(degenRandomChance.getMenuItem("Random Floor Degen Chance", Material.SNOW, degenRandDes, 1, 100));
		itemsMain.add(floorDegenTime.getMenuItem("Floor Degenerator Delay", Material.WATCH, 1, null));
		itemsMain.add(new MenuItemTime("Regeneration Delay", MinigameUtils.stringToList("Time in seconds before;Minigame regeneration starts"), Material.WATCH, new Callback<Integer>() {

			@Override
			public void setValue(Integer value) {
				regenDelay.setFlag(value);
			}

			@Override
			public Integer getValue() {
				return regenDelay.getFlag();
			}
		}, 0, null));
		itemsMain.add(new MenuItemNewLine());
		itemsMain.add(new MenuItemPage("Player Settings", Material.SKULL_ITEM, playerMenu));
		List<String> thDes = new ArrayList<String>();
		thDes.add("Treasure hunt related");
		thDes.add("settings.");
//		itemsMain.add(new MenuItemPage("Treasure Hunt Settings", thDes, Material.CHEST, treasureHunt));
//		MenuItemDisplayLoadout defLoad = new MenuItemDisplayLoadout("Default Loadout", Material.DIAMOND_SWORD, LoadoutModule.getMinigameModule(this).getDefaultPlayerLoadout(), this);
//		defLoad.setAllowDelete(false);
//		itemsMain.add(defLoad);
		itemsMain.add(new MenuItemPage("Loadouts", Material.CHEST, loadouts));
		itemsMain.add(canSpectateFly.getMenuItem("Allow Spectator Fly", Material.FEATHER));
		List<String> rndChstDes = new ArrayList<String>();
		rndChstDes.add("Randomize items in");
		rndChstDes.add("chest upon first opening");
		itemsMain.add(randomizeChests.getMenuItem("Randomize Chests", Material.CHEST, rndChstDes));
		rndChstDes.clear();
		rndChstDes.add("Min. item randomization");
		itemsMain.add(minChestRandom.getMenuItem("Min. Chest Random", Material.STEP, rndChstDes, 0, null));
		rndChstDes.clear();
		rndChstDes.add("Max. item randomization");
		itemsMain.add(maxChestRandom.getMenuItem("Max. Chest Random", Material.DOUBLE_STEP, rndChstDes, 0, null));
		itemsMain.add(new MenuItemNewLine());

		//--------------//
		//Loadout Settings
		//--------------//
		List<MenuItem> mi = new ArrayList<MenuItem>();
		List<String> des = new ArrayList<String>();
		des.add("Shift + Right Click to Delete");
		for(String ld : LoadoutModule.getMinigameModule(this).getLoadouts()){
			Material item = Material.THIN_GLASS;
			if(LoadoutModule.getMinigameModule(this).getLoadout(ld).getItems().size() != 0){
				item = LoadoutModule.getMinigameModule(this).getLoadout(ld).getItem((Integer)LoadoutModule.getMinigameModule(this).getLoadout(ld).getItems().toArray()[0]).getType();
			}
			if(LoadoutModule.getMinigameModule(this).getLoadout(ld).isDeleteable())
				mi.add(new MenuItemDisplayLoadout(ld, des, item, LoadoutModule.getMinigameModule(this).getLoadout(ld), this));
			else
				mi.add(new MenuItemDisplayLoadout(ld, item, LoadoutModule.getMinigameModule(this).getLoadout(ld), this));
		}
		loadouts.addItem(new MenuItemLoadoutAdd("Add Loadout", Material.ITEM_FRAME, LoadoutModule.getMinigameModule(this).getLoadoutMap(), this), 53);
		loadouts.addItem(new MenuItemPage("Back", Material.REDSTONE_TORCH_ON, main), loadouts.getSize() - 9);
		loadouts.addItems(mi);
		
		main.addItems(itemsMain);
		main.addItem(new MenuItemSaveMinigame("Save " + getName(false), Material.REDSTONE_TORCH_ON, this), main.getSize() - 1);

		//----------------------//
		//Minigame Player Settings
		//----------------------//
		List<MenuItem> itemsPlayer = new ArrayList<MenuItem>(14);
		List<String> gmopts = new ArrayList<String>();
		for(GameMode gm : GameMode.values()){
			gmopts.add(MinigameUtils.capitalize(gm.toString()));
		}
		itemsPlayer.add(new MenuItemList("Players Gamemode", Material.WORKBENCH, getDefaultGamemodeCallback(), gmopts));
		itemsPlayer.add(allowEnderpearls.getMenuItem("Allow Enderpearls", Material.ENDER_PEARL));
		itemsPlayer.add(itemDrops.getMenuItem("Allow Item Drops", Material.DIAMOND_SWORD));
		itemsPlayer.add(deathDrops.getMenuItem("Allow Death Drops", Material.SKULL_ITEM));
		itemsPlayer.add(itemPickup.getMenuItem("Allow Item Pickup", Material.DIAMOND));
		itemsPlayer.add(blockBreak.getMenuItem("Allow Block Break", Material.DIAMOND_PICKAXE));
		itemsPlayer.add(blockPlace.getMenuItem("Allow Block Place", Material.STONE));
		itemsPlayer.add(blocksdrop.getMenuItem("Allow Block Drops", Material.COBBLESTONE));
		itemsPlayer.add(lives.getMenuItem("Lives", Material.APPLE, 0, null));
		itemsPlayer.add(paintBallMode.getMenuItem("Paintball Mode", Material.SNOW_BALL));
		itemsPlayer.add(paintBallDamage.getMenuItem("Paintball Damage", Material.ARROW, 1, null));
		itemsPlayer.add(unlimitedAmmo.getMenuItem("Unlimited Ammo", Material.SNOW_BLOCK));
		itemsPlayer.add(allowMPCheckpoints.getMenuItem("Enable Multiplayer Checkpoints", Material.SIGN));
		itemsPlayer.add(saveCheckpoints.getMenuItem("Save Checkpoints", Material.SIGN, MinigameUtils.stringToList("Singleplayer Only")));
		itemsPlayer.add(new MenuItemPage("Flags", MinigameUtils.stringToList("Singleplayer flags"), Material.SIGN, flags));
		itemsPlayer.add(allowFlight.getMenuItem("Allow Flight", Material.FEATHER, MinigameUtils.stringToList("Allow flight to;be toggled")));
		itemsPlayer.add(enableFlight.getMenuItem("Enable Flight", Material.FEATHER, MinigameUtils.stringToList("Start players;in flight;(Must have Allow;Flight)")));
		playerMenu.addItems(itemsPlayer);
		playerMenu.addItem(new MenuItemPage("Back", Material.REDSTONE_TORCH_ON, main), main.getSize() - 9);
		
		//--------------//
		//Minigame Flags//
		//--------------//
		List<MenuItem> itemsFlags = new ArrayList<MenuItem>(getFlags().size());
		for(String flag : getFlags()){
			itemsFlags.add(new MenuItemFlag(Material.SIGN, flag, getFlags()));
		}
		flags.addItem(new MenuItemPage("Back", Material.REDSTONE_TORCH_ON, playerMenu), flags.getSize() - 9);
		flags.addItem(new MenuItemAddFlag("Add Flag", Material.ITEM_FRAME, this), flags.getSize() - 1);
		flags.addItems(itemsFlags);
		
		//--------------//
		//Lobby Settings//
		//--------------//
		List<MenuItem> itemsLobby = new ArrayList<MenuItem>(4);
		itemsLobby.add(new MenuItemBoolean("Can Interact on Player Wait", Material.STONE_BUTTON, LobbySettingsModule.getMinigameModule(this).getCanInteractPlayerWaitCallback()));
		itemsLobby.add(new MenuItemBoolean("Can Interact on Start Wait", Material.STONE_BUTTON, LobbySettingsModule.getMinigameModule(this).getCanInteractStartWaitCallback()));
		itemsLobby.add(new MenuItemBoolean("Can Move on Player Wait", Material.ICE, LobbySettingsModule.getMinigameModule(this).getCanMovePlayerWaitCallback()));
		itemsLobby.add(new MenuItemBoolean("Can Move on Start Wait", Material.ICE, LobbySettingsModule.getMinigameModule(this).getCanMoveStartWaitCallback()));
		itemsLobby.add(new MenuItemBoolean("Teleport After Player Wait", MinigameUtils.stringToList("Should players be teleported;after player wait time?"), 
				Material.ENDER_PEARL, LobbySettingsModule.getMinigameModule(this).getTeleportOnPlayerWaitCallback()));
		itemsLobby.add(new MenuItemBoolean("Teleport on Start", MinigameUtils.stringToList("Should players teleport;to the start position;after lobby?"),
				Material.ENDER_PEARL, LobbySettingsModule.getMinigameModule(this).getTeleportOnStartCallback()));
		lobby.addItems(itemsLobby);
		lobby.addItem(new MenuItemPage("Back", Material.REDSTONE_TORCH_ON, main), lobby.getSize() - 9);

		for(MinigameModule mod : getModules()){
			mod.addEditMenuOptions(main);
		}
		main.displayMenu(player);
		
	}

	public ScoreboardData getScoreboardData() {
		return sbData;
	}

	public void saveMinigame(){
		MinigameSave minigame = new MinigameSave(name, "config");
		FileConfiguration cfg = minigame.getConfig();
		cfg.set(name, null);
		
		for(MinigameModule module : getModules()){
			if(!module.useSeparateConfig()){
				module.save(cfg);
				
				if(module.getFlags() != null){
					for(Flag<?> flag : module.getFlags().values()){
						if(flag.getFlag() != null && (flag.getDefaultFlag() == null || !flag.getDefaultFlag().equals(flag.getFlag())))
							flag.saveValue(name, cfg);
					}
				}
			}else{
				MinigameSave modsave = new MinigameSave("minigames/" + name + "/" + module.getName().toLowerCase());
				modsave.getConfig().set(name, null);
				module.save(modsave.getConfig());
				
				if(module.getFlags() != null){
					for(Flag<?> flag : module.getFlags().values()){
						if(flag.getFlag() != null && (flag.getDefaultFlag() == null || !flag.getDefaultFlag().equals(flag.getFlag())))
							flag.saveValue(name, modsave.getConfig());
					}
				}
				
				modsave.saveConfig();
			}
		}
		
		for(String configOpt : configFlags.keySet()){
			if(configFlags.get(configOpt).getFlag() != null && 
					(configFlags.get(configOpt).getDefaultFlag() == null ||
						!configFlags.get(configOpt).getDefaultFlag().equals(configFlags.get(configOpt).getFlag())))
				configFlags.get(configOpt).saveValue(name, cfg);
		}
		
		if(!getBlockRecorder().getWBBlocks().isEmpty()){
			List<String> blocklist = new ArrayList<String>();
			for(Material mat : getBlockRecorder().getWBBlocks()){
				blocklist.add(mat.toString());
			}
			minigame.getConfig().set(name + ".whitelistblocks", blocklist);
		}
		
		if(getBlockRecorder().getWhitelistMode()){
			minigame.getConfig().set(name + ".whitelistmode", getBlockRecorder().getWhitelistMode());
		}
		
		getScoreboardData().saveDisplays(minigame, name);
		
		minigame.saveConfig();
	}
	
	public void loadMinigame(){
		MinigameSave minigame = new MinigameSave(name, "config");
		FileConfiguration cfg = minigame.getConfig();
		
		//-----------------------------------------------
		//TODO: Remove me after 1.7
		if(cfg.contains(name + ".type")){
			if(cfg.getString(name + ".type").equals("TEAMS")) {
				cfg.set(name + ".type", "MULTIPLAYER");
				TeamsModule.getMinigameModule(this).addTeam(TeamColor.RED);
				TeamsModule.getMinigameModule(this).addTeam(TeamColor.BLUE);
			}
			else if(cfg.getString(name + ".type").equals("FREE_FOR_ALL")){
				cfg.set(name + ".type", "MULTIPLAYER");
			}
			else if(cfg.getString(name + ".type").equals("TREASURE_HUNT")){
				cfg.set(name + ".type", "GLOBAL");
				cfg.set(name + ".scoretype", "treasure_hunt");
				cfg.set(name + ".timer", Minigames.plugin.getConfig().getInt("treasurehunt.findtime") * 60);
			}
		}
		//-----------------------------------------------
		
		for(MinigameModule module : getModules()){
			if(!module.useSeparateConfig()){
				module.load(cfg);
				
				if(module.getFlags() != null){
					for(String flag : module.getFlags().keySet()){
						if(cfg.contains(name + "." + flag))
							module.getFlags().get(flag).loadValue(name, cfg);
					}
				}
			}else{
				MinigameSave modsave = new MinigameSave("minigames/" + name + "/" + module.getName().toLowerCase());
				module.load(modsave.getConfig());
				
				if(module.getFlags() != null){
					for(String flag : module.getFlags().keySet()){
						if(modsave.getConfig().contains(name + "." + flag))
							module.getFlags().get(flag).loadValue(name, modsave.getConfig());
					}
				}
			}
		}
		
		for(String flag : configFlags.keySet()){
			if(cfg.contains(name + "." + flag))
				configFlags.get(flag).loadValue(name, cfg);
		}
		
		if(minigame.getConfig().contains(name + ".whitelistmode")){
			getBlockRecorder().setWhitelistMode(minigame.getConfig().getBoolean(name + ".whitelistmode"));
		}
		
		if(minigame.getConfig().contains(name + ".whitelistblocks")){
			List<String> blocklist = minigame.getConfig().getStringList(name + ".whitelistblocks");
			for(String block : blocklist){
				getBlockRecorder().addWBBlock(Material.matchMaterial(block));
			}
		}

//		Bukkit.getLogger().info("------- Minigame Load -------");
//		Bukkit.getLogger().info("Name: " + getName());
//		Bukkit.getLogger().info("Type: " + getType());
//		Bukkit.getLogger().info("Enabled: " + isEnabled());
//		Bukkit.getLogger().info("-----------------------------");
		
		final Minigame mgm = this;
		
		if(getType() == MinigameType.GLOBAL && isEnabled()){
			Bukkit.getScheduler().scheduleSyncDelayedTask(Minigames.plugin, new Runnable() {
				
				@Override
				public void run() {
					Minigames.plugin.mdata.startGlobalMinigame(mgm, null);
				}
			});
		}
		
		getScoreboardData().loadDisplays(minigame, this);
		
		saveMinigame();
	}
	
	@Override
	public String toString(){
		return getName(false);
	}
}
