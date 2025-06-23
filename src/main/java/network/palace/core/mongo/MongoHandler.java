package network.palace.core.mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.*;
import lombok.Getter;
import network.palace.core.Core;
import network.palace.core.economy.currency.CurrencyType;
import network.palace.core.economy.honor.HonorMapping;
import network.palace.core.economy.honor.TopHonorReport;
import network.palace.core.events.EconomyUpdateEvent;
import network.palace.core.npc.mob.MobPlayerTexture;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.core.player.RankTag;
import network.palace.core.resource.ResourcePack;
import network.palace.core.tracking.GameType;
import network.palace.core.tracking.StatisticType;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * The MongoHandler class is responsible for managing and interacting with a MongoDB database
 * relevant to player information, achievements, statistics, and other functionality in the system.
 * It performs operations such as retrieving and modifying player data, managing cosmetics,
 * handling resource packs, recording statistics, and more.
 */
@SuppressWarnings("rawtypes")
public class MongoHandler {

    /**
     * Represents the MongoDB client instance used to interact with the database.
     * This variable is initialized as null by default and should be assigned
     * a proper MongoClient instance before usage.
     * <p>
     * The client provides a connection to the MongoDB server and is responsible
     * for executing operations on the database, such as reading and writing data.
     */
    private MongoClient client = null;

    /**
     * Represents the MongoDatabase instance used for interacting with the MongoDB.
     * This variable holds the connection to the database and allows performing
     * various database operations such as querying and updating listOfCollections.txt.
     * It is initialized as null and should be properly set before use.
     */
    @Getter private MongoDatabase database = null;

    /**
     * Represents a MongoDB collection for storing and retrieving activity-related documents.
     * This variable holds a reference to the MongoCollection instance, which provides
     * the primary access point for performing database operations such as inserts,
     * queries, updates, and deletions on the activity data.
     * <p>
     * It is initialized to null and must be assigned a value before being used.
     */
    private MongoCollection<Document> activityCollection = null;

    /**
     * Represents a MongoDB collection used to store and manage player-related data.
     * The collection is expected to hold documents corresponding to player information.
     * This variable is initialized as null and must be assigned a valid MongoCollection
     * instance before use.
     */
    private MongoCollection<Document> playerCollection = null;

    /**
     * Represents the MongoDB collection used to store and manage friends-related data.
     * This variable is intended to provide an interface to perform operations on the
     * "friends" collection within a MongoDB database, such as inserting, querying,
     * updating, or deleting documents.
     * <p>
     * Note: This field must be initialized with a valid MongoDB collection instance
     * before performing any operations.
     */
    private MongoCollection<Document> friendsCollection = null;

    /**
     * A MongoDB collection object representing the storage for permissions.
     * This collection will store documents pertaining to permission data.
     * It is initialized to null and expected to be assigned during runtime
     * with a connection to the appropriate MongoDB database.
     */
    private MongoCollection<Document> permissionCollection = null;

    /**
     * Represents the MongoDB collection used to store and retrieve documents
     * related to resource packs.
     * <p>
     * The collection is used to persist and query data about resource packs
     * in a MongoDB database. This variable is initialized to null by default
     * and should be assigned an appropriate MongoCollection<Document> instance
     * before use.
     */
    private MongoCollection<Document> resourcePackCollection = null;

    /**
     * Represents a MongoDB collection used to store and manage ride counters.
     * This variable is initialized to null and should be assigned a valid
     * MongoCollection<Document> instance representing the desired collection.
     * <p>
     * The rideCounterCollection typically contains documents that are used
     * to track counters or accumulated statistics related to rides,
     * providing a centralized data store for applications that manage ride-related
     * operations or analytics.
     */
    private MongoCollection<Document> rideCounterCollection = null;

    /**
     * Represents a MongoDB collection for storing and accessing honor mapping documents.
     * This collection is used for performing database operations related to honor mappings.
     * <p>
     * The collection is defined to work with MongoDB documents and enables CRUD operations
     * along with other database interactions required for managing honor mapping data.
     * <p>
     * It is initialized to null and should be properly assigned an instance of
     * MongoCollection<Document> before use.
     */
    private MongoCollection<Document> honorMappingCollection = null;

    /**
     * Represents a MongoDB collection used to store and manage information
     * related to outfits. This variable is intended to interact with the
     * "outfits" collection within a MongoDB database.
     * <p>
     * The collection stores data as documents, typically in BSON format, and
     * provides methods for querying, inserting, updating, and deleting outfits data.
     * <p>
     * This variable is initialized and set to null initially and should be
     * properly assigned a valid MongoCollection instance before use.
     */
    private MongoCollection<Document> outfitsCollection = null;

    /**
     * Represents a collection in a MongoDB database that stores show schedule documents.
     * This collection contains data related to show schedules, and it allows CRUD operations
     * on the stored schedule documents. The collection is initialized to null and should be
     * assigned a valid MongoCollection instance before use.
     */
    private MongoCollection<Document> showScheduleCollection = null;

    /**
     * A MongoDB collection object representing the storage for hotel-related documents.
     * This collection is used to perform CRUD (Create, Read, Update, Delete) operations
     * on hotel data within the database.
     */
    private MongoCollection<Document> hotelCollection = null;

    /**
     * Represents the MongoDB collection used to store and manage warp data.
     * <p>
     * This variable is initialized to null and needs to be assigned an appropriate
     * MongoCollection instance during runtime to interact with the database.
     * <p>
     * The collection is expected to handle documents corresponding to warp-related
     * information, providing functionality for persistence, querying, and data manipulation.
     */
    private MongoCollection<Document> warpsCollection = null;

    /**
     * A MongoCollection instance representing the "servers" collection in the database.
     * This variable is used to interact with the "servers" collection to perform database operations
     * such as insert, update, delete, and retrieve documents.
     * It is initialized to null and should be properly instantiated before use.
     */
    private MongoCollection<Document> serversCollection = null;

    /**
     * Represents the MongoDB collection used for data storage and management.
     * This variable holds a reference to a specific collection within the database
     * and is used to perform various database operations such as insert, update,
     * delete, and query.
     * <p>
     * It is initialized to null and should be properly instantiated before use.
     */
    private MongoCollection<Document> storageCollection = null;

    /**
     * Constructs a new instance of the MongoHandler class.
     * This constructor initializes a connection to the MongoDB database
     * by invoking the {@code connect()} method.
     */
    public MongoHandler() {
        connect();
    }

    /**
     * Establishes a connection to a MongoDB database using the connection parameters
     * defined in the application's configuration. If required parameters (username,
     * password, or hostname) are missing, the application will log an error message
     * and shut down the Bukkit server.
     * <p>
     * This method initializes MongoDB client and retrieves references to predefined
     * listOfCollections.txt used within the application, such as activity, players, friends,
     * permissions, resource packs, ride counters, and more.
     * <p>
     * Configuration keys used:
     * - db.user: MongoDB username.
     * - db.password: MongoDB password.
     * - db.hostname: MongoDB hostname.
     * - db.database (optional): MongoDB database name. Defaults to "palace" if not defined.
     * <p>
     * Collections initialized:
     * - activityCollection: Represents activity-related data.
     * - playerCollection: Represents player-related data.
     * - friendsCollection: Represents friend relationships.
     * - permissionCollection: Represents permissions data.
     * - resourcePackCollection: Represents resource pack data.
     * - rideCounterCollection: Represents ride counter data.
     * - honorMappingCollection: Represents honor mapping data.
     * - outfitsCollection: Represents outfits data.
     * - showScheduleCollection: Represents show schedule data.
     * - hotelCollection: Represents hotel data.
     * - warpsCollection: Represents warp data.
     * - serversCollection: Represents server data.
     * - storageCollection: Represents general storage data.
     */
    public void connect() {
        String username = Core.getCoreConfig().getString("db.user");
        String password = Core.getCoreConfig().getString("db.password");
        String hostname = Core.getCoreConfig().getString("db.hostname");
        String dbName = Core.getCoreConfig().contains("db.database") ? Core.getCoreConfig().getString("db.database") : "palace";
        if (username == null || password == null || hostname == null) {
            Core.logMessage("Mongo Handler", ChatColor.RED + "" + ChatColor.BOLD + "Error with mongo config!");
            Bukkit.shutdown();
        }
        MongoClientURI connectionString = new MongoClientURI("mongodb://" + username + ":" + password + "@" + hostname);
        client = new MongoClient(connectionString);
        database = client.getDatabase(dbName);
        activityCollection = database.getCollection("activity");
        playerCollection = database.getCollection("players");
        friendsCollection = database.getCollection("friends");
        permissionCollection = database.getCollection("permissions");
        resourcePackCollection = database.getCollection("resourcepacks");
        rideCounterCollection = database.getCollection("ridecounters");
        honorMappingCollection = database.getCollection("honormapping");
        outfitsCollection = database.getCollection("outfits");
        showScheduleCollection = database.getCollection("showschedule");
        hotelCollection = database.getCollection("hotels");
        warpsCollection = database.getCollection("warps");
        serversCollection = database.getCollection("servers");
        storageCollection = database.getCollection("storage");
    }

    /* Player Methods */

    /**
     * Creates a new player entry in the database if the player does not already exist.
     *
     * @param player the player object containing necessary data to create a new player entry
     */
    public void createPlayer(CPlayer player) {
        if (getPlayer(player.getUniqueId()) != null) return;

        Document newDocument = new Document("uuid", player.getUniqueId())
                .append("username", player.getName())
                .append("ip", "localhost")
                .append("tokens", 1)
                .append("adventure", 1)
                .append("currentServer", "Hub1")
                .append("isp", "localhost")
                .append("rank", player.getRank().getDBName())
                .append("lastOnline", System.currentTimeMillis())
                .append("isVisible", true)
                .append("currentMute", null)
                .append("bans", new HashMap<String, Object>() {{
                    put("isBanned", false);
                    put("currentBan", null);
                }})
                .append("kicks", new ArrayList<>());
        playerCollection.insertOne(newDocument);
    }

    /**
     * Retrieves a player's document from the database by their UUID.
     *
     * @param uuid the unique identifier of the player whose document is to be retrieved
     * @return the player's document if found, or null if no document matches the given UUID
     */
    public Document getPlayer(UUID uuid) {
        return playerCollection.find(Filters.eq("uuid", uuid.toString())).first();
    }

    /**
     * Retrieves a player's document from the player collection based on their UUID.
     *
     * @param uuid the unique identifier of the player to fetch
     * @param limit the projection document to limit the fields returned in the result
     * @return the first document matching the given UUID, or null if no match is found
     */
    public Document getPlayer(UUID uuid, Document limit) {
        FindIterable<Document> doc = playerCollection.find(Filters.eq("uuid", uuid.toString())).projection(limit);
        if (doc == null) return null;
        return doc.first();
    }

    /**
     * Checks if a player with the specified UUID is currently online.
     *
     * @param uuid the UUID of the player to check
     * @return true if the player is online, false otherwise
     */
    public boolean isPlayerOnline(UUID uuid) {
        return playerCollection.find(Filters.and(Filters.eq("uuid", uuid.toString()), Filters.eq("online", true))).first() != null;
    }

    /**
     * Checks if a player with the given username exists in the collection.
     *
     * @param username the username of the player to check
     * @return true if the player exists, false otherwise
     */
    public boolean playerExists(String username) {
        return playerCollection.find(Filters.eq("username", username)) != null;
    }

    /**
     * Retrieves the rank of a player based on their unique identifier (UUID).
     *
     * @param uuid the unique identifier of the player; if null, the default rank is returned
     * @return the rank of the player as a Rank object; defaults to Rank.GUEST if the UUID is not found or null
     */
    public Rank getRank(UUID uuid) {
        if (uuid == null) return Rank.GUEST;
        Document result = playerCollection.find(Filters.eq("uuid", uuid.toString())).first();
        if (result == null) return Rank.GUEST;
        return Rank.fromString(result.getString("rank"));
    }

    /**
     * Retrieves the rank of a player based on their username.
     *
     * @param username the username of the player whose rank is to be fetched
     * @return the Rank object corresponding to the player's rank
     */
    public Rank getRank(String username) {
        return Rank.fromString(playerCollection.find(Filters.eq("username", username)).first().getString("rank"));
    }

    /**
     * Retrieves a list of rank tags associated with the specified user's UUID.
     *
     * @param uuid the UUID of the user for whom to retrieve rank tags
     * @return a list of RankTag objects associated with the user's UUID;
     *         returns an empty list if the UUID is null, no data is found,
     *         or no tags are associated with the UUID
     */
    @SuppressWarnings("rawtypes")
    public List<RankTag> getRankTags(UUID uuid) {
        if (uuid == null) return new ArrayList<>();
        Document result = playerCollection.find(Filters.eq("uuid", uuid.toString())).projection(new Document("tags", 1)).first();
        if (result == null || !result.containsKey("tags")) return new ArrayList<>();
        ArrayList list = result.get("tags", ArrayList.class);
        List<RankTag> tags = new ArrayList<>();
        for (Object o : list) {
            tags.add(RankTag.fromString((String) o));
        }
        return tags;
    }

    /**
     * Adds a rank tag to the specified player's record in the database.
     *
     * @param uuid the unique identifier of the player to whom the rank tag will be added
     * @param tag the rank tag to be added to the player's record
     */
    public void addRankTag(UUID uuid, RankTag tag) {
        if (uuid == null || tag == null) return;
        playerCollection.updateOne(Filters.eq("uuid", uuid.toString()), Updates.addToSet("tags", tag.getDBName()), new UpdateOptions().upsert(true));
    }

    /**
     * Removes the specified rank tag from the player's collection in the database.
     *
     * @param tag the rank tag to be removed.
     */
    public void removeRankTag(UUID uuid, RankTag tag) {
        if (uuid == null || tag == null) return;
        playerCollection.updateOne(Filters.eq("uuid", uuid.toString()), Updates.pull("tags", tag.getDBName()));
    }

    /**
     * Converts a UUID to the corresponding username by searching a player collection.
     *
     * @param uuid the UUID of the player whose username is to be retrieved
     * @return the username associated with the provided UUID, or null if not found
     */
    public String uuidToUsername(UUID uuid) {
        FindIterable<Document> list = playerCollection.find(Filters.eq("uuid", uuid.toString()));
        if (list.first() == null) return null;
        return list.first().getString("username");
    }

    /**
     * Converts a given username to its corresponding UUID by fetching data from the player collection.
     * If the username is not found or the UUID is invalid, the method returns null.
     *
     * @param username the username to be converted to a UUID. It must not be null.
     * @return the UUID associated with the provided username, or null if the username does not exist
     *         in the database or if the UUID string is malformed.
     */
    public UUID usernameToUUID(String username) {
        try {
            FindIterable<Document> list = playerCollection.find(Filters.eq("username", username));
            if (list.first() == null) return null;
            return UUID.fromString(list.first().getString("uuid"));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Caches the skin information for a player in the database.
     *
     * @param uuid      The UUID of the player whose skin is being cached.
     * @param value     The hash value of the player's skin.
     * @param signature The signature for verifying the skin's authenticity.
     */
    public void cacheSkin(UUID uuid, String value, String signature) {
        playerCollection.updateOne(Filters.eq("uuid", uuid.toString()), Updates.set("skin", new Document("hash", value).append("signature", signature)));
    }

    /**
     * Retrieves the player's texture hash information based on their UUID.
     *
     * @param uuid the unique identifier of the player whose texture hash is to be retrieved
     * @return a MobPlayerTexture object containing the player's texture hash and signature
     */
    public MobPlayerTexture getPlayerTextureHash(UUID uuid) {
        BasicDBObject skin = (BasicDBObject) getPlayer(uuid, new Document("skin", 1)).get("skin");
        return new MobPlayerTexture(skin.getString("hash"), skin.getString("signature"));
    }

    /**
     * Retrieves the language associated with the provided UUID.
     *
     * @param uuid the unique identifier for which the language is being queried
     * @return the language code associated with the given UUID
     */
    public String getLanguage(UUID uuid) {
        return "en_us";
    }

    /* Warp Methods */

    /**
     * Retrieves all warp documents from the warps collection.
     *
     * @return a FindIterable<Document> containing all warp documents in the collection.
     */
    public FindIterable<Document> getWarps() {
        return warpsCollection.find();
    }

    /**
     * Deletes a warp with the specified name from the warps collection.
     *
     * @param name the name of the warp to delete
     */
    public void deleteWarp(String name) {
        warpsCollection.deleteOne(Filters.eq("name", name));
    }

    /**
     * Creates and saves a new warp point to the database with the specified details.
     *
     * @param name The name of the warp point.
     * @param server The server where the warp point is located.
     * @param x The X-coordinate of the warp point.
     * @param y The Y-coordinate of the warp point.
     * @param z The Z-coordinate of the warp point.
     * @param yaw The yaw (rotation) of the warp point.
     * @param pitch The pitch (tilt) of the warp point.
     * @param world The name of the world where the warp point is located.
     * @param rank The required rank for accessing this warp point. Can be null if no rank restriction is needed.
     */
    public void createWarp(String name, String server, double x, double y, double z, float yaw, float pitch, String world, Rank rank) {
        Document doc = new Document("name", name).append("server", server).append("x", x).append("y", y)
                .append("z", z).append("yaw", (int) yaw).append("pitch", (int) pitch).append("world", world);
        if (rank != null) {
            doc.append("rank", rank.getDBName());
        }
        warpsCollection.insertOne(doc);
    }

    /* Achievement Methods */

    /**
     * Adds an achievement for a specific player identified by their UUID.
     *
     * @param uuid The unique identifier of the player.
     * @param achievementID The ID of the achievement to be added.
     */
    public void addAchievement(UUID uuid, int achievementID) {
        playerCollection.updateOne(Filters.eq("uuid", uuid.toString()), Updates.push("achievements", new BasicDBObject("id", achievementID).append("time", System.currentTimeMillis() / 1000)));
    }

    /**
     * Retrieves a list of achievement IDs for a player identified by their UUID.
     *
     * @param uuid the unique identifier of the player whose achievements are to be retrieved
     * @return a list of achievement IDs; an empty list if the player has no achievements or if the player is not found
     */
    public List<Integer> getAchievements(UUID uuid) {
        List<Integer> list = new ArrayList<>();
        Document player = getPlayer(uuid, new Document("achievements", 1));
        if (player == null) return list;
        List array = (ArrayList) player.get("achievements");
        for (Object obj : array) {
            Document doc = (Document) obj;
            list.add(doc.getInteger("id"));
        }
        return list;
    }

    /* Cosmetics */

    /**
     * Awards a cosmetic item to the specified player using a unique player ID and cosmetic ID.
     *
     * @param player The player who will receive the cosmetic item.
     * @param id The unique identifier of the cosmetic item to be awarded.
     */
    public void earnCosmetic(CPlayer player, int id) {
        earnCosmetic(player.getUniqueId(), id);
    }

    /**
     * Grants a cosmetic item to a player by updating their cosmetic collection.
     *
     * @param uuid the unique identifier of the player who will receive the cosmetic
     * @param id the unique identifier of the cosmetic item to be added
     */
    public void earnCosmetic(UUID uuid, int id) {
        playerCollection.updateOne(Filters.eq("uuid", uuid.toString()), Updates.push("cosmetics", id));
    }

    /**
     * Checks if the given player has the specified cosmetic item.
     *
     * @param player The player whose cosmetics are being checked.
     * @param id The unique identifier of the cosmetic item to check.
     * @return true if the player has the specified cosmetic item, false otherwise.
     */
    public boolean hasCosmetic(CPlayer player, int id) {
        return hasCosmetic(player.getUniqueId(), id);
    }

    /**
     * Checks if a specific cosmetic item is associated with a player based on their UUID and item ID.
     *
     * @param uuid the unique identifier of the player
     * @param id the identifier of the cosmetic item to check
     * @return true if the player has the cosmetic item, false otherwise
     */
    public boolean hasCosmetic(UUID uuid, int id) {
        Document doc = getPlayer(uuid, new Document("cosmetics", 1));
        return doc != null && doc.get("cosmetics", ArrayList.class).contains(id);
    }

    /**
     * Retrieves a list of cosmetic item IDs associated with the specified player's UUID.
     *
     * @param uuid the unique identifier of the player whose cosmetics are being retrieved
     * @return a list of integers representing the IDs of the cosmetics; an empty list if no cosmetics are found
     */
    @SuppressWarnings("unchecked")
    public List<Integer> getCosmetics(UUID uuid) {
        Document doc = getPlayer(uuid, new Document("cosmetics", 1));
        List<Integer> list = new ArrayList<>();
        if (doc == null) return list;
        try {
            return doc.get("cosmetics", ArrayList.class);
        } catch (Exception e) {
            e.printStackTrace();
            return list;
        }
    }

    /**
     * Retrieves the currently active hat ID for the player associated with the given UUID.
     *
     * @param uuid the unique identifier of the player for whom the active hat ID is being retrieved
     * @return the identifier of the currently active hat, or 0 if no hat is active
     */
    public int getActiveHat(UUID uuid) {
        return 0;
    }

    /**
     * Retrieves the active particle associated with the specified UUID.
     *
     * @param uuid the UUID of the entity or object whose active particle is to be retrieved
     * @return the ID of the active particle as an integer
     */
    public int getActiveParticle(UUID uuid) {
        return 0;
    }

    /**
     * Sets the active hat for a specific user identified by their UUID.
     *
     * @param uuid the unique identifier of the user
     * @param id the identifier of the hat to be set as active
     */
    public void setActiveHat(UUID uuid, int id) {
    }

    /**
     * Sets the active particle based on the given UUID and particle ID.
     *
     * @param uuid The unique identifier of the entity or system to associate with the particle.
     * @param id The identifier of the particle to be set as active.
     */
    public void setActiveParticle(UUID uuid, int id) {
    }

    /**
     * Sets the active toy using its unique identifier and ID.
     *
     * @param uuid the unique identifier of the toy
     * @param id the ID of the toy
     */
    public void setActiveToy(UUID uuid, int id) {
    }

    /* Economy Methods */

    /**
     * Retrieves the amount of a specified currency type for a player identified by their UUID.
     *
     * @param uuid the unique identifier of the player
     * @param type the type of currency to retrieve
     * @return the amount of the specified currency type the player has; returns 0 if the player
     *         or the specified currency type does not exist
     */
    public int getCurrency(UUID uuid, CurrencyType type) {
        Document player = getPlayer(uuid, new Document(type.getName(), 1));
        if (player == null) return 0;
        return (int) player.getOrDefault(type.getName(), 0);
    }

    /**
     * Modifies the currency amount of a player and logs the transaction details.
     *
     * @param uuid   The unique identifier of the player whose balance is being updated.
     * @param amount The amount to set or adjust for the player's currency.
     * @param source The source or reason for the currency modification (e.g., event, purchase).
     * @param type   The type of the currency being modified.
     * @param set    If true, the currency amount is set to the specified value. If false, the specified amount is added or subtracted.
     */
    public void changeAmount(UUID uuid, int amount, String source, CurrencyType type, boolean set) {
        playerCollection.updateOne(Filters.eq("uuid", uuid.toString()), set ? Updates.set(type.getName(), amount) : Updates.inc(type.getName(), amount));
        Document doc = new Document("amount", amount).append("type", type.getName()).append("source", source)
                .append("server", Core.getInstanceName()).append("timestamp", System.currentTimeMillis() / 1000);
        playerCollection.updateOne(Filters.eq("uuid", uuid.toString()), Updates.push("transactions", doc));
        Core.runTask(() -> new EconomyUpdateEvent(uuid, getCurrency(uuid, type), type).call());
    }

    /* Game Methods */

    /**
     * Retrieves the game statistic for a specific game type and statistic type, associated with the given player.
     *
     * @param game   the type of game for which the statistic is being requested
     * @param type   the specific statistic type to retrieve
     * @param player the player for whom the statistic is being retrieved
     * @return an integer representing the requested statistic for the given game, statistic type, and player
     */
    public int getGameStat(GameType game, StatisticType type, CPlayer player) {
        return getGameStat(game, type, player.getUniqueId());
    }

    /**
     * Retrieves the game statistic for a specific player, game type, and statistic type.
     *
     * @param game The type of game for which the statistic is being retrieved.
     * @param type The specific statistic type to be retrieved.
     * @param uuid The unique identifier of the player.
     * @return The value of the requested statistic as an integer. Returns 0 if the statistic
     *         is not found or if its value is not of a valid type (numeric or boolean).
     */
    public int getGameStat(GameType game, StatisticType type, UUID uuid) {
        Document player = getPlayer(uuid, new Document("gameData", 1));
        BasicDBObject obj = (BasicDBObject) player.get(game.getDbName());
        if (!obj.containsField(type.getType()) || (!(obj.get(type.getType()) instanceof Number) && !(obj.get(type.getType()) instanceof Boolean))) {
            return 0;
        }
        return obj.getInt(type.getType());
    }

    /**
     * Adds a game statistic entry for the specified player and game type.
     *
     * @param game the type of game for which the statistic is being added
     * @param statistic the specific type of statistic to update
     * @param amount the amount to add to the statistic
     * @param player the player for whom the statistic is being updated
     */
    public void addGameStat(GameType game, StatisticType statistic, int amount, CPlayer player) {
        addGameStat(game, statistic, amount, player.getUuid());
    }

    /**
     * Updates the game statistics of a player in the database.
     *
     * @param game the type of game for which the statistic is being updated
     * @param statistic the type of statistic to be updated
     * @param amount the value to set for the statistic
     * @param uuid the unique identifier of the player whose statistics are being updated
     */
    public void addGameStat(GameType game, StatisticType statistic, int amount, UUID uuid) {
        playerCollection.updateOne(Filters.eq("uuid", uuid.toString()), Updates.set("gameData", new BasicDBObject(game.getDbName(), new BasicDBObject(statistic.getType(), amount))));
    }

    /* Honor Methods */

    /**
     * Retrieves a list of honor mappings from the database.
     * This method queries the honor mapping collection, creating a list of
     * HonorMapping objects based on the retrieved documents.
     *
     * @return a list of HonorMapping objects, each representing a mapping of
     *         level to honor retrieved from the database.
     */
    public List<HonorMapping> getHonorMappings() {
        List<HonorMapping> list = new ArrayList<>();
        FindIterable<Document> iter = honorMappingCollection.find();
        for (Document doc : iter) {
            HonorMapping map = new HonorMapping(doc.getInteger("level"), doc.getInteger("honor"));
            list.add(map);
        }
        return list;
    }

    /**
     * Adds a specified amount of honor to a player's record and logs the transaction.
     *
     * @param uuid The unique identifier of the player to update.
     * @param amount The amount of honor to add to the player's record.
     * @param source The source or reason for the honor addition.
     */
    public void addHonor(UUID uuid, int amount, String source) {
        playerCollection.updateOne(Filters.eq("uuid", uuid.toString()), Updates.inc("honor", amount));
        Document doc = new Document("amount", amount).append("type", "honor").append("source", source)
                .append("server", Core.getInstanceName()).append("timestamp", System.currentTimeMillis() / 1000);
        playerCollection.updateOne(Filters.eq("uuid", uuid.toString()), Updates.push("transactions", doc));
        CPlayer tp;
        if ((tp = Core.getPlayerManager().getPlayer(uuid)) != null) {
            tp.loadHonor(getHonor(uuid));
            Core.getHonorManager().displayHonor(tp);
        }
    }

    /**
     * Updates the honor value for a player and records the transaction in the database.
     *
     * @param uuid   The unique identifier of the player whose honor is being updated.
     * @param amount The new honor amount to be set for the player.
     * @param source The source or reason for the honor change.
     */
    public void setHonor(UUID uuid, int amount, String source) {
        playerCollection.updateOne(Filters.eq("uuid", uuid.toString()), Updates.set("honor", amount));
        Document doc = new Document("amount", amount).append("type", "honor").append("source", source)
                .append("server", Core.getInstanceName()).append("timestamp", System.currentTimeMillis() / 1000);
        playerCollection.updateOne(Filters.eq("uuid", uuid.toString()), Updates.push("transactions", doc));
        CPlayer tp;
        if ((tp = Core.getPlayerManager().getPlayer(uuid)) != null) {
            tp.loadHonor(getHonor(uuid));
            Core.getHonorManager().displayHonor(tp);
        }
    }

    /**
     * Retrieves the honor value associated with a specific player's UUID.
     *
     * @param uuid the unique identifier of the player whose honor value is to be retrieved
     * @return the honor value of the player; if the player does not exist, returns 0
     */
    public int getHonor(UUID uuid) {
        Document player = getPlayer(uuid, new Document("honor", 1));
        if (player == null) return 0;
        return (int) player.getOrDefault("honor", 1);
    }

    /**
     * Retrieves a list of top players based on their honor rankings.
     * The method queries the player collection, sorts players by their honor in descending order,
     * and returns the top results up to the specified limit. If the provided limit exceeds 10, it is capped at 10.
     *
     * @param limit the maximum number of top players to retrieve. If greater than 10, it will default to 10.
     * @return a list of TopHonorReport objects representing the top players by honor ranking.
     */
    public List<TopHonorReport> getTopHonor(int limit) {
        List<TopHonorReport> list = new ArrayList<>();
        if (limit > 10) {
            limit = 10;
        }
        FindIterable<Document> iterable = playerCollection.find().projection(new Document("uuid", 1).append("username", 1)
                .append("rank", 1).append("honor", 1)).sort(new Document("honor", -1)).limit(limit);
        int place = 1;
        for (Document doc : iterable) {
            list.add(new TopHonorReport(UUID.fromString(doc.getString("uuid")),
                    doc.getString("username"), place++, doc.getInteger("honor")));
        }
        return list;
    }

    /* Resource Pack Methods */

    /**
     * Retrieves a list of resource packs available in the system. Each resource pack is generated based on
     * the data retrieved from the resource pack collection and includes information such as the name, versions,
     * and associated metadata.
     *
     * @return a list of ResourcePack objects containing detailed information about each resource pack,
     *         including their versions and associated attributes.
     */
    public List<ResourcePack> getResourcePacks() {
        List<ResourcePack> list = new ArrayList<>();

        for (Document doc : resourcePackCollection.find()) {
            String name = doc.getString("name");
            ResourcePack pack = new ResourcePack(name);
            List<ResourcePack.Version> versions = new ArrayList<>();

            if (doc.containsKey("versions")) {
                for (Object o : doc.get("versions", ArrayList.class)) {
                    Document version = (Document) o;
                    int protocolId = version.getInteger("id");
                    versions.add(pack.generateVersion(protocolId, version.getString("url"), version.containsKey("hash") ? version.getString("hash") : ""));
                }
            } else {
                versions.add(pack.generateVersion(-1, doc.getString("url"), doc.containsKey("hash") ? doc.getString("hash") : ""));
            }
            pack.setVersions(versions);
            list.add(pack);
        }

        return list;
    }

    /* Permission Methods */

    /**
     * Retrieves a list of member usernames based on the specified rank.
     *
     * @param rank the rank used as a filter to fetch member usernames
     * @return a list of usernames corresponding to the given rank
     */
    public List<String> getMembers(Rank rank) {
        List<String> list = new ArrayList<>();
        playerCollection.find(Filters.eq("rank", rank.getDBName())).projection(new Document("username", 1))
                .forEach((Block<Document>) d -> list.add(d.getString("username")));
        return list;
    }

    /**
     * Retrieves a list of member usernames associated with the specified rank tag.
     *
     * @param tag the rank tag used to filter and retrieve associated member usernames
     * @return a list of usernames corresponding to members with the specified rank tag
     */
    public List<String> getMembers(RankTag tag) {
        List<String> list = new ArrayList<>();
        playerCollection.find(Filters.eq("tags", tag.getDBName())).projection(new Document("username", 1))
                .forEach((Block<Document>) d -> list.add(d.getString("username")));
        return list;
    }

    /**
     * Updates the rank of a player in the database identified by their UUID.
     *
     * @param uuid the unique identifier of the player whose rank is to be updated
     * @param rank the new rank to be assigned to the player
     */
    public void setRank(UUID uuid, Rank rank) {
        playerCollection.updateOne(Filters.eq("uuid", uuid.toString()), Updates.set("rank", rank.getDBName()));
    }

    /**
     * Retrieves a document from the player collection by the specified UUID with a projection
     * including only the specified park entries.
     *
     * @param uuid The unique identifier to filter the document in the collection.
     * @param parkEntries The fields to include in the projection of the retrieved document.
     * @return A Document containing the data matching the specified UUID with the specified fields included,
     *         or null if no matching document is found.
     */
    public Document getJoinData(UUID uuid, String... parkEntries) {
        Document projection = new Document();
        for (String s : parkEntries) {
            projection.append(s, 1);
        }
        return playerCollection.find(Filters.eq("uuid", uuid.toString())).projection(projection).first();
    }

    /**
     * Retrieves a map of permissions corresponding to the specified player's rank.
     *
     * @param player the player whose permissions are to be retrieved
     * @return a map where the key is the permission name and the value is a boolean indicating
     *         whether the permission is granted (true) or denied (false) for the player's rank
     */
    public Map<String, Boolean> getPermissions(CPlayer player) {
        return getPermissions(player.getRank());
    }

    /**
     * Retrieves a map of permissions for a specific rank.
     * The permissions are fetched from a database collection based on the rank
     * and are categorized as either allowed or denied.
     *
     * @param rank The rank for which permissions need to be retrieved.
     *             This parameter determines the scope of the permissions to be fetched.
     * @return A map where the keys are permission nodes (formatted as strings),
     *         and the values are booleans indicating whether the permission
     *         is allowed (true) or denied (false) for the specified rank.
     */
    public Map<String, Boolean> getPermissions(Rank rank) {
        Map<String, Boolean> map = new HashMap<>();
        for (Document main : permissionCollection.find(new Document("rank", rank.getDBName()))) {
            ArrayList allowed = (ArrayList) main.get("allowed");
            ArrayList denied = (ArrayList) main.get("denied");
            for (Object o : denied) {
                String node = getNode(o);
                if (node == null) continue;
                map.put(MongoUtil.commaToPeriod(node), false);
            }
            for (Object o : allowed) {
                String node = getNode(o);
                if (node == null) continue;
                map.put(MongoUtil.commaToPeriod(node), true);
            }
        }
        map.put("palace.core.rank." + rank.getDBName(), true);
        return map;
    }

    /**
     * Retrieves the node name from the given object. If the input contains a server prefix
     * separated by a colon, it checks if the prefix matches the current server type. If the
     * server type does not match, the method returns null. Otherwise, it extracts and
     * returns the substring after the colon. If no colon is present, the input is returned
     * as is, assuming it is directly the node name.
     *
     * @param o the input object, expected to be a String representing a node name with
     *          an optional server prefix separated by a colon
     * @return the processed node name if the server type matches or if no prefix is present;
     *         null if the server type does not match the prefix
     */
    private String getNode(Object o) {
        String node = (String) o;
        if (node.contains(":")) {
            String[] split = node.split(":");
            String server = split[0];
            if (!Core.getServerType().equalsIgnoreCase(server)) return null;
            node = split[1];
        }
        return node;
    }

    /**
     * Sets the permission for a specific node and rank, allowing or denying access based on the provided value.
     *
     * @param node the name of the permission node to be updated
     * @param rank the rank for which the permission is being set
     * @param value a boolean indicating whether the permission should be allowed (true) or denied (false)
     */
    public void setPermission(String node, Rank rank, boolean value) {
        node = MongoUtil.periodToComma(node);
        boolean removeFromOtherList = false;
        String other = value ? "denied" : "allowed";
        for (Document d : permissionCollection.find(Filters.eq("rank", rank.getDBName())).projection(new Document(other, 1))) {
            for (Object o : d.get(other, ArrayList.class)) {
                String s = (String) o;
                if (s != null && s.equals(node)) {
                    permissionCollection.updateOne(Filters.eq("rank", rank.getDBName()), Updates.pull(other, node));
                }
            }
        }
        permissionCollection.updateOne(Filters.eq("rank", rank.getDBName()), Updates.addToSet(value ? "allowed" : "denied", node));
    }

    /**
     * Removes a specific permission node from the allowed and denied permissions
     * of a specified rank in the database.
     *
     * @param node the permission node to be removed; periods in the node
     *             are converted to commas before processing
     * @param rank the rank from which the permission node will be removed
     */
    public void unsetPermission(String node, Rank rank) {
        node = MongoUtil.periodToComma(node);
        permissionCollection.updateOne(Filters.eq("rank", rank.getDBName()), Updates.pull("allowed", node));
        permissionCollection.updateOne(Filters.eq("rank", rank.getDBName()), Updates.pull("denied", node));
    }

    /**
     * Retrieves the monthly rewards for a specific player using their unique identifier.
     *
     * @param uuid the unique identifier of the player whose monthly rewards are being retrieved
     * @return a Document containing the player's monthly rewards or null if not found
     */
    public Document getMonthlyRewards(UUID uuid) {
        return (Document) getPlayer(uuid, new Document("monthlyRewards", 1)).get("monthlyRewards");
    }

    /**
     * Retrieves the vote data for a specified player identified by their UUID.
     *
     * @param uuid the unique identifier of the player whose vote data is being retrieved
     * @return a Document containing the vote data associated with the specified player
     */
    public Document getVoteData(UUID uuid) {
        return (Document) getPlayer(uuid, new Document("vote", 1)).get("vote");
    }

    /**
     * Retrieves a list of friends associated with the given user ID.
     *
     * @param uuid the unique identifier of the user whose friend list is to be retrieved
     * @return a list of UUIDs representing the friends of the specified user
     */
    public List<UUID> getFriendList(UUID uuid) {
        return getList(uuid, true);
    }

    /**
     * Retrieves a list of request identifiers for the specified UUID.
     *
     * @param uuid the unique identifier used to fetch the associated request list
     * @return a list of UUIDs representing the request identifiers
     */
    public List<UUID> getRequestList(UUID uuid) {
        return getList(uuid, false);
    }

    /**
     * Retrieves a list of UUIDs based on the input UUID and the relationship type.
     *
     * @param uuid the UUID of the user for whom the list is being retrieved
     * @param friends a boolean indicating the type of relationship to consider;
     *                true to include only friends, false to include non-friends
     * @return a list of UUIDs representing either friends or non-friends, based on the parameter
     */
    public List<UUID> getList(UUID uuid, boolean friends) {
        List<UUID> list = new ArrayList<>();
        for (Document doc : friendsCollection.find(Filters.or(Filters.eq("sender", uuid.toString()),
                Filters.eq("receiver", uuid.toString())))) {
            try {
                UUID sender = UUID.fromString(doc.getString("sender"));
                UUID receiver = UUID.fromString(doc.getString("receiver"));
                boolean friend = doc.getLong("started") > 0;
                if (!friends && !friend && receiver.equals(uuid)) {
                    list.add(sender);
                } else if (friends && friend) {
                    if (uuid.equals(sender)) {
                        list.add(receiver);
                    } else {
                        list.add(sender);
                    }
                }
            } catch (Exception e) {
                Core.logMessage("MongoHandler", "Error processing friendship '" + doc.toString() + "': " + e.getMessage());
                e.printStackTrace();
            }
        }
        return list;
    }

    /**
     * Updates the monthly reward data for a player identified by their UUID.
     *
     * @param uuid The unique identifier of the player whose reward data is being updated.
     * @param settler The reward value associated with the "settler" rank.
     * @param dweller The reward value associated with the "dweller" rank.
     * @param noble The reward value associated with the "noble" rank.
     * @param majestic The reward value associated with the "majestic" rank.
     * @param honorable The reward value associated with the "honorable" rank.
     */
    public void updateMonthlyRewardData(UUID uuid, long settler, long dweller, long noble, long majestic, long honorable) {
        playerCollection.updateOne(Filters.eq("uuid", uuid.toString()), Updates.set("monthlyRewards",
                new Document("settler", settler).append("dweller", dweller).append("noble", noble)
                        .append("majestic", majestic).append("honorable", honorable)));
    }

    /**
     * Updates the FastPass data for a specific player identified by their UUID.
     *
     * @param uuid The unique identifier of the player whose FastPass data is to be updated.
     * @param fastPass The timestamp or data representing the last claimed FastPass.
     */
    public void updateFastPassData(UUID uuid, long fastPass) {
        playerCollection.updateOne(Filters.eq("uuid", uuid.toString()),
                Updates.set("parks.fastpass.lastClaimed", fastPass));
    }

    /**
     * Updates the FastPass data for a player in the database based on their UUID.
     *
     * @param uuid The unique identifier of the player whose FastPass data is being updated.
     * @param slow The count of slow rides available for the player's FastPass.
     * @param moderate The count of moderate rides available for the player's FastPass.
     * @param thrill The count of thrill rides available for the player's FastPass.
     * @param slowday The count of slow rides the player can use for a specific day.
     * @param moderateday The count of moderate rides the player can use for a specific day.
     * @param thrillday The count of thrill rides the player can use for a specific day.
     */
    public void updateFPData(UUID uuid, int slow, int moderate, int thrill, int slowday, int moderateday, int thrillday) {
        playerCollection.updateOne(Filters.eq("uuid", uuid.toString()), Updates.set("parks.fastpass",
                new Document("slow", slow).append("moderate", moderate).append("thrill", thrill).append("sday", slowday)
                        .append("mday", moderateday).append("tday", thrillday)));
    }

    /**
     * Adds a specified number of FastPasses to the player identified by the given UUID.
     * If the player does not already exist in the collection, a new entry will be created.
     *
     * @param uuid the unique identifier of the player
     * @param count the number of FastPasses to be added
     */
    public void addFastPass(UUID uuid, int count) {
        playerCollection.updateOne(Filters.eq("uuid", uuid.toString()),
                Updates.inc("parks.fastpass.count", count), new UpdateOptions().upsert(true));
    }

    /**
     * Retrieves a document containing hotel information from the hotel collection
     * with a projection that includes only the "hotels" field.
     *
     * @return a Document containing the projected hotel data, or null if no document is found.
     */
    public Document getHotels() {
        return hotelCollection.find().projection(new Document("hotels", 1)).first();
    }

    /**
     * Retrieves the messages of hotels from the hotel collection.
     *
     * @return a Document containing the hotel messages, or null if no documents are found.
     */
    public Document getHotelMessages() {
        return hotelCollection.find().projection(new Document("messages", 1)).first();
    }

    /**
     * Retrieves the messages associated with a specified hotel identified by the provided UUID.
     *
     * @param uuid the unique identifier of the target hotel for which the messages are to be retrieved
     * @return a FindIterable containing the documents that match the specified UUID within the "messages.target" field
     */
    public FindIterable<Document> getHotelMessages(UUID uuid) {
        return hotelCollection.find(Filters.eq("messages.target", uuid.toString()));
    }

    /*
    Park Methods
     */

    /**
     * Retrieves park data joined for a specific player based on the given UUID and park entries.
     *
     * @param uuid the unique identifier of the player whose park data is to be retrieved
     * @param parkEntries variable-length list of park entry names to include in the query
     * @return a Document containing the joined park data for the specified player and park entries
     */
    public Document getParkJoinData(UUID uuid, String... parkEntries) {
        Document projection = null;
        for (String s : parkEntries) {
            if (projection == null) {
                projection = new Document("parks." + s, 1);
            } else {
                projection.append("parks." + s, 1);
            }
        }
        return (Document) playerCollection.find(Filters.eq("uuid", uuid.toString())).projection(projection).first().get("parks");
    }

    /**
     * Retrieves park data for a specific player based on the provided UUID and limit string.
     *
     * @param uuid the unique identifier of the player whose park data is being retrieved
     * @param limit a string that defines the scope or depth of the data to retrieve;
     *              if null or empty, all park data is returned
     * @return a Document containing the requested park data based on the limit parameter
     */
    public Document getParkData(UUID uuid, String limit) {
        if (limit == null || limit.isEmpty()) {
            return (Document) getPlayer(uuid, new Document("parks", 1)).get("parks");
        }
        Document current = (Document) getPlayer(uuid, new Document("parks." + limit, 1)).get("parks");
        String[] split;
        if (limit.contains(".")) {
            split = limit.split("\\.");
        } else {
            split = new String[]{limit};
        }
        for (String s : split) {
            current = (Document) current.get(s);
        }
        return current;
    }

    /**
     * Retrieves the value associated with a specified key from the park data
     * corresponding to the given UUID.
     *
     * @param uuid the unique identifier of the park
     * @param key the key for which the value is to be retrieved
     * @return the value associated with the specified key, or null if not found
     */
    public String getParkValue(UUID uuid, String key) {
        Document park = getParkData(uuid, null);
        return park.getString(key);
    }

    /**
     * Sets a specific value for a park attribute associated with a player identified by their UUID.
     *
     * @param uuid the unique identifier of the player whose park attribute is to be updated
     * @param key the key representing the park attribute to be updated
     * @param value the new value to set for the specified park attribute
     */
    public void setParkValue(UUID uuid, String key, Object value) {
        playerCollection.updateOne(Filters.eq("uuid", uuid.toString()), Updates.set("parks." + key, value));
    }

    /**
     * Updates the park storage for a specific player identified by their UUID.
     * The method sets the storage data associated with the given key using the provided document.
     *
     * @param uuid the unique identifier of the player
     * @param key the key representing the specific park storage to update
     * @param doc the document containing the data to set for the specified park storage
     */
    public void setParkStorage(UUID uuid, String key, Document doc) {
        playerCollection.updateOne(Filters.and(
                Filters.eq("uuid", uuid.toString()),
                Filters.eq("onlineData.parkStorageLock", Core.getInstanceName())
        ), Updates.set("parks." + key, doc));
    }

    /**
     * Retrieves the name color associated with a magic band using its UUID.
     *
     * @param uuid the universally unique identifier of the magic band
     * @return the name color associated with the provided magic band UUID
     */
    public String getMagicBandNameColor(UUID uuid) {
        Document data = getMagicBandData(uuid);
        return data.getString("namecolor");
    }

    /**
     * Retrieves the MagicBand type for a specific user based on their UUID.
     *
     * @param uuid the unique identifier of the user whose MagicBand type is being retrieved
     * @return the type of the MagicBand as a string, or null if the type is not found
     */
    public String getMagicBandType(UUID uuid) {
        Document data = getMagicBandData(uuid);
        return data.getString("bandtype");
    }

    /**
     * Retrieves the magic band data associated with a specific UUID.
     *
     * @param uuid the unique identifier for accessing the magic band data
     * @return a Document containing the magic band data associated with the provided UUID
     */
    public Document getMagicBandData(UUID uuid) {
//        Document park = getParkData(uuid);
//        return (Document) park.get("magicband");
        return getParkData(uuid, "magicband");
    }

    /**
     * Retrieves a specific park setting for the given UUID.
     *
     * @param uuid the unique identifier of the park
     * @param setting the name of the setting to retrieve
     * @return the value of the requested park setting, or null if not found
     */
    public Object getParkSetting(UUID uuid, String setting) {
        Document settings = getParkData(uuid, "settings");
        return settings.get(setting);
    }

    /**
     * Retrieves a list of ride counter data associated with a specific UUID.
     *
     * @param uuid the unique identifier for which to retrieve ride counter data
     * @return an ArrayList containing documents of ride counter data for the specified UUID
     */
    public ArrayList getRideCounterData(UUID uuid) {
        ArrayList<Document> list = new ArrayList<>();
        for (Document d : rideCounterCollection.find(Filters.eq("uuid", uuid.toString()))) {
            list.add(d);
        }
        return list;
//        Document park = getParkData(uuid, null);
//        return park.get("rides", ArrayList.class);
    }

    /**
     * Logs ride counter information by creating a document with user details and inserting it into the rideCounterCollection.
     *
     * @param uuid the unique identifier of the user
     * @param name the name of the user
     */
    public void logRideCounter(UUID uuid, String name) {
        if (uuid == null || name == null) return;
        Document doc = new Document("uuid", uuid.toString()).append("name", name.trim()).append("server", Core.getServerType())
                .append("time", System.currentTimeMillis() / 1000);
        rideCounterCollection.insertOne(doc);
    }

    /**
     * Retrieves the list of autographs associated with a specific player identified by their UUID.
     *
     * @param uuid the unique identifier of the player whose autographs are to be retrieved
     * @return an ArrayList containing the autographs of the specified player
     */
    public ArrayList getAutographs(UUID uuid) {
        return playerCollection.find(Filters.eq("uuid", uuid.toString()))
                .projection(new Document("autographs", 1)).first().get("autographs", ArrayList.class);
    }

    /**
     * Adds a signed message to the autograph list of the specified player.
     *
     * @param player The UUID of the player whose autograph list will be updated.
     * @param sender The name or identifier of the person signing the book.
     * @param message The message or note that will be included in the autograph entry.
     */
    public void signBook(UUID player, String sender, String message) {
        Document doc = new Document("author", sender).append("message", message).append("time", System.currentTimeMillis());
        playerCollection.updateOne(Filters.eq("uuid", player.toString()), Updates.push("autographs", doc));
    }

    /**
     * Deletes an autograph from the player's collection based on the specified UUID, sender, and timestamp.
     *
     * @param uuid   the unique identifier of the player whose autograph is to be deleted
     * @param sender the name of the sender who created the autograph
     * @param time   the timestamp of when the autograph was created
     */
    public void deleteAutograph(UUID uuid, String sender, long time) {
        playerCollection.updateOne(Filters.eq("uuid", uuid.toString()), Updates.pull("autographs",
                new Document("author", sender).append("time", time)));
    }

    /**
     * Charges a specified amount of FastPass credits from a player identified by their UUID.
     *
     * @param uuid   The unique identifier of the player whose FastPass credits will be charged.
     * @param amount The number of FastPass credits to deduct.
     */
    public void chargeFastPass(UUID uuid, int amount) {
        playerCollection.updateOne(Filters.eq("uuid", uuid.toString()), Updates.inc("parks.fastpass.count", -amount));
    }

    /**
     * Retrieves a collection of outfit documents.
     * This method returns all available outfits.
     *
     * @return a FindIterable containing document representations of outfits
     */
    public FindIterable<Document> getOutfits() {
        return getOutfits(-1);
    }

    /**
     * Retrieves a list of outfit documents from the database.
     * If a valid resort ID is provided, the method returns outfits associated with that resort.
     * If the resort ID is negative, all outfits are retrieved.
     *
     * @param resort the ID of the resort to filter outfits. If negative, retrieves all outfits.
     * @return a FindIterable containing the matched outfit documents.
     */
    public FindIterable<Document> getOutfits(int resort) {
        if (resort < 0) {
            return outfitsCollection.find();
        } else {
            return outfitsCollection.find(Filters.eq("resort", resort));
        }
    }

    /**
     * Retrieves a list of outfit purchases for a specific user identified by their UUID.
     *
     * @param uuid the unique identifier of the user for whom the outfit purchases are to be retrieved
     * @return an ArrayList containing the outfit purchases associated with the specified user
     */
    public ArrayList getOutfitPurchases(UUID uuid) {
        Document park = getParkData(uuid, null);
        return park.get("outfitPurchases", ArrayList.class);
//        return getParkData(uuid, new Document("parks.outfitPurchases", 1));
    }

    /**
     * Updates the outfit code in the database for the specified player.
     *
     * @param uuid The unique identifier of the player whose outfit code is being updated.
     * @param code The new outfit code to be set for the player.
     */
    public void setOutfitCode(UUID uuid, String code) {
        playerCollection.updateOne(Filters.eq("uuid", uuid.toString()), Updates.set("parks.outfit", code));
    }

    /**
     * Records the purchase of an outfit for a specific player.
     * The method updates the player's data by adding a record of the outfit purchase,
     * including the outfit ID and the timestamp of the transaction.
     *
     * @param uuid The unique identifier of the player making the purchase.
     * @param id The identifier of the outfit being purchased.
     */
    public void purchaseOutfit(UUID uuid, int id) {
        playerCollection.updateOne(Filters.eq("uuid", uuid.toString()),
                Updates.push("parks.outfitPurchases", new Document("id", id).append("time", System.currentTimeMillis() / 1000)));
    }

    /**
     * Creates an outfit document and inserts it into the outfits collection.
     *
     * @param name The name of the outfit.
     * @param hid The ID of the head item.
     * @param hdata The data value of the head item.
     * @param head The name or description of the head item.
     * @param cid The ID of the chestplate item.
     * @param cdata The data value of the chestplate item.
     * @param chestplate The name or description of the chestplate item.
     * @param lid The ID of the leggings item.
     * @param ldata The data value of the leggings item.
     * @param leggings The name or description of the leggings item.
     * @param bid The ID of the boots item.
     * @param bdata The data value of the boots item.
     * @param boots The name or description of the boots item.
     * @param resort The resort value associated with the outfit.
     */
    public void createOutfit(String name, int hid, byte hdata, String head, int cid, byte cdata, String chestplate,
                             int lid, byte ldata, String leggings, int bid, byte bdata, String boots, int resort) {
        Document doc = new Document("id", getNextOutfitId());
        doc.append("name", name);
        doc.append("headID", hid);
        doc.append("headData", hdata);
        doc.append("head", head);
        doc.append("chestID", cid);
        doc.append("chestData", cdata);
        doc.append("chest", chestplate);
        doc.append("leggingsID", lid);
        doc.append("leggingsData", ldata);
        doc.append("leggings", leggings);
        doc.append("bootsID", bid);
        doc.append("bootsData", bdata);
        doc.append("boots", boots);
        doc.append("resort", resort);
        outfitsCollection.insertOne(doc);
    }

    /**
     * Creates a new outfit and stores it in the outfits collection.
     *
     * @param name  the name of the outfit
     * @param head  the head accessory details in JSON string format
     * @param shirt the shirt details in JSON string format
     * @param pants the pants details in JSON string format
     * @param boots the boots details in JSON string format
     * @param resort the resort identifier associated with this outfit
     */
    public void createOutfitNew(String name, String head, String shirt, String pants, String boots, int resort) {
        Document doc = new Document("id", getNextOutfitId());
        doc.append("name", name);
        doc.append("headJSON", head);
        doc.append("shirtJSON", shirt);
        doc.append("pantsJSON", pants);
        doc.append("bootsJSON", boots);
        doc.append("resort", resort);
        outfitsCollection.insertOne(doc);
    }

    /**
     * Generates the next available outfit ID by incrementing a sequence value in the database.
     * If no sequence exists for the user, it initializes the sequence and returns the starting value.
     *
     * @return the next outfit ID as an integer
     */
    private int getNextOutfitId() {
        BasicDBObject find = new BasicDBObject();
        find.put("_id", "userid");
        BasicDBObject update = new BasicDBObject();
        update.put("$inc", new BasicDBObject("seq", 1));
        Document obj = outfitsCollection.findOneAndUpdate(find, update, new FindOneAndUpdateOptions().upsert(true));
        int result = 1;
        if (obj != null && obj.containsKey("seq")) {
            result = obj.getInteger("seq") + 1;
        }
        return result;
    }

    /**
     * Deletes an outfit from the collection based on the provided unique identifier.
     *
     * @param id the unique identifier of the outfit to be deleted
     */
    public void deleteOutfit(int id) {
        outfitsCollection.deleteOne(Filters.eq("id", id));
    }

    /**
     * Updates the park setting for a specific player identified by their UUID.
     *
     * @param uuid The unique identifier of the player whose park setting is being updated.
     * @param setting The name of the park setting to be updated.
     * @param value The new value to assign to the specified park setting.
     */
    public void setParkSetting(UUID uuid, String setting, Object value) {
        playerCollection.updateOne(Filters.eq("uuid", uuid.toString()), Updates.set("parks.settings." + setting, value));
    }

    /**
     * Updates the MagicBand data for the specified player in the database.
     *
     * @param uuid The unique identifier of the player whose MagicBand data is to be updated.
     * @param key The key of the MagicBand attribute to be updated.
     * @param value The new value for the specified MagicBand attribute.
     */
    public void setMagicBandData(UUID uuid, String key, String value) {
        playerCollection.updateOne(Filters.eq("uuid", uuid.toString()), Updates.set("parks.magicband." + key, value));
    }

    /**
     * Sets the build mode for a player identified by their UUID.
     *
     * @param uuid   the unique identifier of the player
     * @param value  the build mode value to be set
     */
    public void setBuildMode(UUID uuid, boolean value) {
        playerCollection.updateOne(Filters.eq("uuid", uuid.toString()), Updates.set("parks.buildmode", value));
    }

    /**
     * Retrieves the build mode status for the player associated with the given UUID.
     *
     * @param uuid the unique identifier of the player whose build mode status is to be retrieved
     * @return true if the player has build mode enabled, false otherwise
     */
    public boolean getBuildMode(UUID uuid) {
        Document doc = (Document) getPlayer(uuid, new Document("parks.buildmode", 1)).get("parks");
        if (!doc.containsKey("buildmode")) return false;
        return doc.getBoolean("buildmode");
    }

    /**
     * Sets the inventory size for a specific player's park and inventory type.
     *
     * @param uuid   the unique identifier of the player
     * @param type   the type of inventory to update
     * @param size   the new size for the inventory
     * @param resort the identifier of the resort in the park
     */
    public void setInventorySize(UUID uuid, String type, int size, int resort) {
        playerCollection.updateOne(new Document("uuid", uuid.toString()).append("parks.inventories.resort", resort),
                Updates.set("parks.inventories.$." + type, size));
    }

    /**
     * Generates a leaderboard for ride counters based on the provided name.
     * <p>
     * This method retrieves ride counter data from the collection, aggregates the results,
     * sorts them in descending order of total rides, and limits the number of results
     * based on the specified amount. If the amount exceeds 20, it defaults to 10.
     *
     * @param name the name of the ride counter to filter records.
     * @param amount the maximum number of leaderboard entries to return. If greater than 20, it is capped at 10.
     * @return a list of documents where each document contains a unique identifier ("uuid")
     *         and the corresponding total ride count ("total"), sorted in descending order.
     */
    public List<Document> getRideCounterLeaderboard(String name, int amount) {
        if (amount > 20) {
            amount = 10;
        }
        List<Document> list = new ArrayList<>();
        rideCounterCollection.aggregate(
                Arrays.asList(
                        Aggregates.match(Filters.eq("name", name)),
                        Aggregates.group("$uuid", Accumulators.sum("total", 1)),
                        Aggregates.sort(new Document("total", -1)),
                        Aggregates.limit(amount)
                )
        ).forEach((Block<Document>) document -> {
            String uuid = document.getString("_id");
            int total = document.getInteger("total");
            list.add(new Document("uuid", uuid).append("total", total));
        });
        list.sort((o1, o2) -> o2.getInteger("total") - o1.getInteger("total"));
        return list;
    }

    /**
     * Retrieves the scheduled shows from the showScheduleCollection.
     *
     * @return a FindIterable<Document> containing the documents representing the scheduled shows.
     */
    public FindIterable<Document> getScheduledShows() {
        return showScheduleCollection.find();
    }

    /**
     * Updates the scheduled shows in the database with the provided list of shows.
     * This method clears the existing scheduled shows data in the collection and replaces it with the new data.
     *
     * @param shows the list of documents representing the new scheduled shows to be updated in the database
     */
    public void updateScheduledShows(List<Document> shows) {
        showScheduleCollection.deleteMany(new Document());
        showScheduleCollection.insertMany(shows);
    }

    /*
     * Creative Methods
     */

    /**
     * Fetches the creative data for a player associated with the given UUID.
     *
     * @param uuid the unique identifier of the player whose creative data is to be retrieved
     * @return a Document object containing the player's creative data, or null if no data is found
     */
    public Document getCreativeData(UUID uuid) {
        return (Document) getPlayer(uuid, new Document("creative", 1)).get("creative");
    }

    /**
     * Retrieves the value associated with the specified key from a player's creative data.
     *
     * @param uuid The unique identifier of the player whose data is to be retrieved.
     * @param key The key within the creative data to retrieve the value for.
     * @return The value associated with the specified key within the player's creative data,
     *         or null if no value is found or the data is unavailable.
     */
    public Object getCreativeValue(UUID uuid, String key) {
        Document doc = getPlayer(uuid, new Document("creative." + key, 1));
        if (doc == null || doc.isEmpty()) return null;
        return ((Document) doc.get("creative")).get(key);
    }

    /**
     * Updates the creative value in the data store for a player identified by the provided UUID.
     *
     * @param uuid The unique identifier of the player.
     * @param key The key representing the specific creative attribute to update.
     * @param value The new value to set for the specified creative attribute.
     */
    public void setCreativeValue(UUID uuid, String key, Object value) {
        playerCollection.updateOne(Filters.eq("uuid", uuid.toString()), Updates.set("creative." + key, value));
    }

    /**
     * Retrieves a list of usernames who are creators from the player collection.
     * Filters the collection to include only documents where the "creative.creator" field is true
     * and projects the "username" field for retrieval.
     *
     * @return a list of usernames representing the members marked as creators.
     */
    public List<String> getCreatorMembers() {
        List<String> list = new ArrayList<>();
        for (Document doc : playerCollection.find(Filters.eq("creative.creator", true))
                .projection(new Document("username", 1))) {
            list.add(doc.getString("username"));
        }
        return list;
    }

    /**
     * Logs an activity by recording the specified UUID, action, and description
     * into the activity collection.
     *
     * @param uuid         the unique identifier of the entity performing the activity
     * @param action       the action performed, described as a string
     * @param description  additional details or context about the action
     */
    public void logActivity(UUID uuid, String action, String description) {
        activityCollection.insertOne(new Document("uuid", uuid.toString())
                .append("action", action)
                .append("description", description));
    }

    /**
     * Closes the underlying client and releases any resources associated with it.
     * This method should be called when the client is no longer needed to ensure
     * proper resource cleanup.
     */
    public void close() {
        client.close();
    }

    public void setServerOnline(String instanceName, String serverType, boolean playground, boolean online) {
        serversCollection.updateOne(Filters.and(Filters.eq("name", instanceName), Filters.eq("type", serverType),
                Filters.exists("playground", playground)), Updates.set("online", online));
    }

    /**
     * Updates the online data value for a player identified by their UUID. If the provided value
     * is null, the specified key is removed from the online data. Otherwise, the key is set to
     * the provided value.
     *
     * @param uuid the unique identifier of the player whose online data is being updated
     * @param key the key in the player's online data to update or remove
     * @param value the value to assign to the specified key, or null to remove the key
     */
    public void setOnlineDataValue(UUID uuid, String key, Object value) {
        if (value == null) {
            playerCollection.updateOne(Filters.eq("uuid", uuid.toString()), Updates.unset("onlineData." + key));
        } else {
            playerCollection.updateOne(Filters.eq("uuid", uuid.toString()), Updates.set("onlineData." + key, value));
        }
    }

    /**
     * Updates a value in the "onlineData" map of a player document in the database in a concurrent-safe manner.
     * If the provided newValue is null, the key is removed from the map. The update only occurs if the current value
     * in the database matches the provided currentValue, ensuring safe concurrent modifications.
     *
     * @param uuid         The unique identifier of the player whose data is being updated.
     * @param key          The key within the "onlineData" map to be updated or removed.
     * @param newValue     The new value to assign to the key. If null, the key will be removed.
     * @param currentValue The expected current value of the key, used for concurrent-safe updates.
     */
    public void setOnlineDataValueConcurrentSafe(UUID uuid, String key, Object newValue, Object currentValue) {
        if (newValue == null) {
            playerCollection.updateOne(Filters.and(
                    Filters.eq("uuid", uuid.toString()),
                    Filters.eq("onlineData." + key, currentValue)
            ), Updates.unset("onlineData." + key));
        } else {
            playerCollection.updateOne(Filters.and(
                    Filters.eq("uuid", uuid.toString()),
                    Filters.eq("onlineData." + key, currentValue)
            ), Updates.set("onlineData." + key, newValue));
        }
    }

    /**
     * Retrieves the value of a specific key from the online data of a player identified by the given UUID.
     *
     * @param uuid the unique identifier of the player whose online data is to be retrieved
     * @param key the key within the online data whose value is to be fetched
     * @return the value associated with the specified key in the player's online data, or null if no matching data is found
     */
    public Object getOnlineDataValue(UUID uuid, String key) {
        Document onlineData = playerCollection.find(Filters.eq("uuid", uuid.toString())).projection(new Document("onlineData." + key, 1)).first();
        if (onlineData == null) return null;
        onlineData = onlineData.get("onlineData", Document.class);
        if (!onlineData.containsKey(key)) return null;
        return onlineData.get(key);
    }

    /**
     * Retrieves documents from the storage collection that match the specified UUID
     * and have at least one of the specified fields ('wdw' or 'uso') existing.
     *
     * @param uuid the UUID used to filter the documents in the storage collection
     * @return a FindIterable containing the documents that match the specified criteria
     */
    public FindIterable<Document> getOldStorageDocuments(UUID uuid) {
        return storageCollection.find(Filters.and(
                Filters.eq("uuid", uuid.toString()),
                Filters.or(
                        Filters.exists("wdw"),
                        Filters.exists("uso")
                )
        ));
    }

    /**
     * Updates the player count for a specific server in the collection.
     *
     * @param serverName the name of the server whose player count is to be updated
     * @param playground a flag indicating if the server is a playground server
     * @param size the new player count to be set for the server
     */
    public void setPlayerCount(String serverName, boolean playground, int size) {
        serversCollection.updateOne(Filters.and(Filters.eq("name", serverName), Filters.exists("playground", playground)), Updates.set("count", size));
    }

    /**
     * Retrieves the count of players currently online.
     *
     * @return the number of online players as an integer
     */
    public int getPlayerCount() {
        return (int) playerCollection.count(Filters.eq("online", true));
    }

    /**
     * Bans a player by their UUID with a specified reason, expiration time, and source.
     * The ban can be set as permanent or have an expiry time.
     *
     * @param uuid The unique identifier of the player to be banned.
     * @param reason The reason for banning the player.
     * @param expires The timestamp indicating when the ban will expire.
     *                Pass a negative value if the ban is permanent.
     * @param permanent A flag indicating whether the ban is permanent.
     * @param source The source or entity responsible for initiating the ban.
     */
    public void banPlayer(UUID uuid, String reason, long expires, boolean permanent, String source) {
        Document banDocument = new Document("created", System.currentTimeMillis()).append("expires", expires)
                .append("permanent", permanent).append("reason", reason)
                .append("source", source).append("active", true);

        playerCollection.updateOne(Filters.eq("uuid", uuid.toString()), Updates.push("bans", banDocument));
    }

    /**
     * Retrieves the Discord ID associated with the given user's UUID from the database.
     *
     * @param uuid the UUID of the user whose Discord ID is to be retrieved
     * @return an Optional containing the Discord ID if found, or an empty Optional if no ID is associated
     */
    public Optional<String> getUserDiscordId(UUID uuid) {
        FindIterable<Document> result = playerCollection.find(Filters.eq("uuid", uuid.toString()));
        Document data = result.first();
        Document discord = (Document) data.get("discord");
        String discordId = discord.getString("discordID");
        if (!discordId.equals("")) {
            return Optional.of(discordId);
        } else {
            return Optional.empty();
        }
    }
}
