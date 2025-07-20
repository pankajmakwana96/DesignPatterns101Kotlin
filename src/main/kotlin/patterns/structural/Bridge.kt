package patterns.structural

/**
 * # Bridge Pattern
 * 
 * ## Definition
 * Separates an abstraction from its implementation so that both can vary independently.
 * Divides business logic or huge class into separate class hierarchies that can be developed independently.
 * 
 * ## Problem it solves
 * - Want to avoid permanent binding between abstraction and implementation
 * - Both abstractions and implementations should be extensible through subclassing
 * - Changes in implementation should have no impact on clients
 * - Want to share implementation among multiple objects
 * 
 * ## When to use
 * - Want to avoid compile-time binding between abstraction and implementation
 * - Both abstraction and implementation should be extensible independently
 * - Want to hide implementation details from clients completely
 * - Implementation changes shouldn't require recompiling client code
 * 
 * ## When NOT to use
 * - Only one implementation exists and won't change
 * - Abstraction and implementation are tightly coupled by nature
 * - The complexity doesn't justify the pattern
 * 
 * ## Advantages
 * - Decouples interface and implementation
 * - Improves extensibility
 * - Allows implementation details to be hidden from clients
 * - Supports platform independence
 * 
 * ## Disadvantages
 * - Increases complexity due to abstraction
 * - May impact performance due to indirection
 * - Can be overkill for simple scenarios
 */

// 1. Classic Bridge Pattern - Remote Control and Device

// Implementation interface
interface Device {
    fun isEnabled(): Boolean
    fun enable()
    fun disable()
    fun getVolume(): Int
    fun setVolume(volume: Int)
    fun getChannel(): Int
    fun setChannel(channel: Int)
    fun getInfo(): String
}

// Concrete implementations
class Television : Device {
    private var enabled = false
    private var volume = 30
    private var channel = 1
    
    override fun isEnabled(): Boolean = enabled
    override fun enable() { enabled = true }
    override fun disable() { enabled = false }
    override fun getVolume(): Int = volume
    override fun setVolume(volume: Int) { this.volume = volume.coerceIn(0, 100) }
    override fun getChannel(): Int = channel
    override fun setChannel(channel: Int) { this.channel = channel.coerceIn(1, 999) }
    override fun getInfo(): String = "TV: Enabled=$enabled, Volume=$volume, Channel=$channel"
}

class Radio : Device {
    private var enabled = false
    private var volume = 50
    private var channel = 101 // FM frequency
    
    override fun isEnabled(): Boolean = enabled
    override fun enable() { enabled = true }
    override fun disable() { enabled = false }
    override fun getVolume(): Int = volume
    override fun setVolume(volume: Int) { this.volume = volume.coerceIn(0, 100) }
    override fun getChannel(): Int = channel
    override fun setChannel(channel: Int) { this.channel = channel.coerceIn(88, 108) }
    override fun getInfo(): String = "Radio: Enabled=$enabled, Volume=$volume, Frequency=$channel FM"
}

class SmartSpeaker : Device {
    private var enabled = false
    private var volume = 60
    private var channel = 1 // Playlist number
    private var connectedService = "Spotify"
    
    override fun isEnabled(): Boolean = enabled
    override fun enable() { enabled = true }
    override fun disable() { enabled = false }
    override fun getVolume(): Int = volume
    override fun setVolume(volume: Int) { this.volume = volume.coerceIn(0, 100) }
    override fun getChannel(): Int = channel
    override fun setChannel(channel: Int) { this.channel = channel.coerceIn(1, 50) }
    override fun getInfo(): String = "Smart Speaker: Enabled=$enabled, Volume=$volume, Playlist=$channel on $connectedService"
    
    fun switchService(service: String) { connectedService = service }
}

// Abstraction
open class RemoteControl(protected val device: Device) {
    open fun togglePower(): String {
        return if (device.isEnabled()) {
            device.disable()
            "Device turned OFF"
        } else {
            device.enable()
            "Device turned ON"
        }
    }
    
    open fun volumeDown(): String {
        val newVolume = device.getVolume() - 10
        device.setVolume(newVolume)
        return "Volume: ${device.getVolume()}"
    }
    
    open fun volumeUp(): String {
        val newVolume = device.getVolume() + 10
        device.setVolume(newVolume)
        return "Volume: ${device.getVolume()}"
    }
    
    open fun channelDown(): String {
        val newChannel = device.getChannel() - 1
        device.setChannel(newChannel)
        return "Channel: ${device.getChannel()}"
    }
    
    open fun channelUp(): String {
        val newChannel = device.getChannel() + 1
        device.setChannel(newChannel)
        return "Channel: ${device.getChannel()}"
    }
    
    open fun getStatus(): String = device.getInfo()
}

// Refined abstraction
class AdvancedRemoteControl(device: Device) : RemoteControl(device) {
    fun mute(): String {
        device.setVolume(0)
        return "Device muted"
    }
    
    fun setVolumePercent(percent: Int): String {
        device.setVolume(percent)
        return "Volume set to $percent%"
    }
    
    fun jumpToChannel(channel: Int): String {
        device.setChannel(channel)
        return "Jumped to channel: ${device.getChannel()}"
    }
    
    fun quickVolumeBoost(): String {
        val currentVolume = device.getVolume()
        device.setVolume(minOf(100, currentVolume + 20))
        return "Volume boosted to: ${device.getVolume()}"
    }
}

class VoiceRemoteControl(device: Device) : RemoteControl(device) {
    fun executeVoiceCommand(command: String): String {
        return when (command.lowercase()) {
            "turn on", "power on" -> togglePower()
            "turn off", "power off" -> togglePower()
            "volume up", "louder" -> volumeUp()
            "volume down", "quieter" -> volumeDown()
            "next channel" -> channelUp()
            "previous channel" -> channelDown()
            "status", "info" -> getStatus()
            else -> "Voice command not recognized: $command"
        }
    }
    
    fun processNaturalLanguage(command: String): String {
        return when {
            "set volume to" in command.lowercase() -> {
                val volume = command.filter { it.isDigit() }.toIntOrNull() ?: 50
                device.setVolume(volume)
                "Volume set to $volume via voice command"
            }
            "change to channel" in command.lowercase() -> {
                val channel = command.filter { it.isDigit() }.toIntOrNull() ?: 1
                device.setChannel(channel)
                "Changed to channel $channel via voice command"
            }
            else -> executeVoiceCommand(command)
        }
    }
}

// 2. Database Bridge Pattern - Different database implementations

// Implementation interface
interface DatabaseImplementation {
    fun connect(): String
    fun disconnect(): String
    fun executeQuery(query: String): String
    fun executeUpdate(query: String): String
    fun beginTransaction(): String
    fun commitTransaction(): String
    fun rollbackTransaction(): String
}

// Concrete implementations
class MySQLImplementation : DatabaseImplementation {
    private var connected = false
    private var transactionActive = false
    
    override fun connect(): String {
        connected = true
        return "Connected to MySQL database"
    }
    
    override fun disconnect(): String {
        connected = false
        return "Disconnected from MySQL database"
    }
    
    override fun executeQuery(query: String): String {
        return if (connected) "MySQL Query Result: $query" else "Not connected to MySQL"
    }
    
    override fun executeUpdate(query: String): String {
        return if (connected) "MySQL Update: $query affected 1 row" else "Not connected to MySQL"
    }
    
    override fun beginTransaction(): String {
        transactionActive = true
        return "MySQL transaction started"
    }
    
    override fun commitTransaction(): String {
        transactionActive = false
        return "MySQL transaction committed"
    }
    
    override fun rollbackTransaction(): String {
        transactionActive = false
        return "MySQL transaction rolled back"
    }
}

class PostgreSQLImplementation : DatabaseImplementation {
    private var connected = false
    private var transactionActive = false
    
    override fun connect(): String {
        connected = true
        return "Connected to PostgreSQL database"
    }
    
    override fun disconnect(): String {
        connected = false
        return "Disconnected from PostgreSQL database"
    }
    
    override fun executeQuery(query: String): String {
        return if (connected) "PostgreSQL Query Result: $query" else "Not connected to PostgreSQL"
    }
    
    override fun executeUpdate(query: String): String {
        return if (connected) "PostgreSQL Update: $query modified 1 record" else "Not connected to PostgreSQL"
    }
    
    override fun beginTransaction(): String {
        transactionActive = true
        return "PostgreSQL transaction initiated"
    }
    
    override fun commitTransaction(): String {
        transactionActive = false
        return "PostgreSQL transaction committed"
    }
    
    override fun rollbackTransaction(): String {
        transactionActive = false
        return "PostgreSQL transaction aborted"
    }
}

class MongoDBImplementation : DatabaseImplementation {
    private var connected = false
    private var sessionActive = false
    
    override fun connect(): String {
        connected = true
        return "Connected to MongoDB database"
    }
    
    override fun disconnect(): String {
        connected = false
        return "Disconnected from MongoDB database"
    }
    
    override fun executeQuery(query: String): String {
        return if (connected) "MongoDB Find Result: $query" else "Not connected to MongoDB"
    }
    
    override fun executeUpdate(query: String): String {
        return if (connected) "MongoDB Update: $query updated 1 document" else "Not connected to MongoDB"
    }
    
    override fun beginTransaction(): String {
        sessionActive = true
        return "MongoDB session started"
    }
    
    override fun commitTransaction(): String {
        sessionActive = false
        return "MongoDB session committed"
    }
    
    override fun rollbackTransaction(): String {
        sessionActive = false
        return "MongoDB session aborted"
    }
}

// Abstraction
abstract class DatabaseConnectionBridge(protected val implementation: DatabaseImplementation) {
    abstract fun connect(): String
    abstract fun disconnect(): String
    abstract fun executeQuery(query: String): String
    abstract fun findById(table: String, id: String): String
    abstract fun findAll(table: String): String
    abstract fun insert(table: String, data: Map<String, Any>): String
    abstract fun update(table: String, id: String, data: Map<String, Any>): String
    abstract fun delete(table: String, id: String): String
}

// Refined abstractions
class SQLDatabaseConnection(implementation: DatabaseImplementation) : DatabaseConnectionBridge(implementation) {
    override fun connect(): String = implementation.connect()
    override fun disconnect(): String = implementation.disconnect()
    override fun executeQuery(query: String): String = implementation.executeQuery(query)
    
    override fun findById(table: String, id: String): String {
        val query = "SELECT * FROM $table WHERE id = '$id'"
        return implementation.executeQuery(query)
    }
    
    override fun findAll(table: String): String {
        val query = "SELECT * FROM $table"
        return implementation.executeQuery(query)
    }
    
    override fun insert(table: String, data: Map<String, Any>): String {
        val columns = data.keys.joinToString(", ")
        val values = data.values.joinToString(", ") { "'$it'" }
        val query = "INSERT INTO $table ($columns) VALUES ($values)"
        return implementation.executeUpdate(query)
    }
    
    override fun update(table: String, id: String, data: Map<String, Any>): String {
        val setClause = data.entries.joinToString(", ") { "${it.key} = '${it.value}'" }
        val query = "UPDATE $table SET $setClause WHERE id = '$id'"
        return implementation.executeUpdate(query)
    }
    
    override fun delete(table: String, id: String): String {
        val query = "DELETE FROM $table WHERE id = '$id'"
        return implementation.executeUpdate(query)
    }
    
    fun executeTransaction(queries: List<String>): List<String> {
        val results = mutableListOf<String>()
        results.add(implementation.beginTransaction())
        
        try {
            queries.forEach { query ->
                results.add(implementation.executeUpdate(query))
            }
            results.add(implementation.commitTransaction())
        } catch (e: Exception) {
            results.add(implementation.rollbackTransaction())
            results.add("Transaction failed: ${e.message}")
        }
        
        return results
    }
}

class NoSQLDatabaseConnection(implementation: DatabaseImplementation) : DatabaseConnectionBridge(implementation) {
    override fun connect(): String = implementation.connect()
    override fun disconnect(): String = implementation.disconnect()
    override fun executeQuery(query: String): String = implementation.executeQuery(query)
    
    override fun findById(table: String, id: String): String {
        val query = "db.$table.findOne({_id: '$id'})"
        return implementation.executeQuery(query)
    }
    
    override fun findAll(table: String): String {
        val query = "db.$table.find({})"
        return implementation.executeQuery(query)
    }
    
    override fun insert(table: String, data: Map<String, Any>): String {
        val document = data.entries.joinToString(", ") { "${it.key}: '${it.value}'" }
        val query = "db.$table.insertOne({$document})"
        return implementation.executeUpdate(query)
    }
    
    override fun update(table: String, id: String, data: Map<String, Any>): String {
        val updateDoc = data.entries.joinToString(", ") { "${it.key}: '${it.value}'" }
        val query = "db.$table.updateOne({_id: '$id'}, {\$set: {$updateDoc}})"
        return implementation.executeUpdate(query)
    }
    
    override fun delete(table: String, id: String): String {
        val query = "db.$table.deleteOne({_id: '$id'})"
        return implementation.executeUpdate(query)
    }
    
    fun findByQuery(collection: String, queryDoc: Map<String, Any>): String {
        val query = queryDoc.entries.joinToString(", ") { "${it.key}: '${it.value}'" }
        val mongoQuery = "db.$collection.find({$query})"
        return implementation.executeQuery(mongoQuery)
    }
    
    fun aggregateData(collection: String, pipeline: List<String>): String {
        val pipelineStr = pipeline.joinToString(", ")
        val query = "db.$collection.aggregate([$pipelineStr])"
        return implementation.executeQuery(query)
    }
}

// 3. Notification Bridge Pattern - Different notification channels

// Implementation interface
interface NotificationSender {
    fun send(recipient: String, message: String): String
    fun sendBatch(recipients: List<String>, message: String): String
    fun isAvailable(): Boolean
    fun getDeliveryStatus(messageId: String): String
}

// Concrete implementations
class EmailSender : NotificationSender {
    override fun send(recipient: String, message: String): String {
        return "Email sent to $recipient: $message"
    }
    
    override fun sendBatch(recipients: List<String>, message: String): String {
        return "Batch email sent to ${recipients.size} recipients: $message"
    }
    
    override fun isAvailable(): Boolean = true
    
    override fun getDeliveryStatus(messageId: String): String {
        return "Email $messageId: Delivered"
    }
}

class SMSSender : NotificationSender {
    override fun send(recipient: String, message: String): String {
        val truncatedMessage = if (message.length > 160) message.take(157) + "..." else message
        return "SMS sent to $recipient: $truncatedMessage"
    }
    
    override fun sendBatch(recipients: List<String>, message: String): String {
        return "Batch SMS sent to ${recipients.size} phone numbers"
    }
    
    override fun isAvailable(): Boolean = true
    
    override fun getDeliveryStatus(messageId: String): String {
        return "SMS $messageId: Delivered"
    }
}

class PushNotificationSender : NotificationSender {
    override fun send(recipient: String, message: String): String {
        return "Push notification sent to device $recipient: $message"
    }
    
    override fun sendBatch(recipients: List<String>, message: String): String {
        return "Batch push notification sent to ${recipients.size} devices"
    }
    
    override fun isAvailable(): Boolean = true
    
    override fun getDeliveryStatus(messageId: String): String {
        return "Push notification $messageId: Delivered"
    }
}

class SlackSender : NotificationSender {
    override fun send(recipient: String, message: String): String {
        return "Slack message sent to @$recipient: $message"
    }
    
    override fun sendBatch(recipients: List<String>, message: String): String {
        return "Slack message sent to ${recipients.size} users"
    }
    
    override fun isAvailable(): Boolean = true
    
    override fun getDeliveryStatus(messageId: String): String {
        return "Slack message $messageId: Read"
    }
}

// Abstraction
abstract class NotificationServiceBridge(protected val sender: NotificationSender) {
    abstract fun sendNotification(recipient: String, message: String): String
    abstract fun sendBulkNotification(recipients: List<String>, message: String): String
    
    fun checkServiceAvailability(): Boolean = sender.isAvailable()
    fun trackMessage(messageId: String): String = sender.getDeliveryStatus(messageId)
}

// Refined abstractions
class UrgentNotificationService(sender: NotificationSender) : NotificationServiceBridge(sender) {
    override fun sendNotification(recipient: String, message: String): String {
        val urgentMessage = "URGENT: $message"
        return sender.send(recipient, urgentMessage)
    }
    
    override fun sendBulkNotification(recipients: List<String>, message: String): String {
        val urgentMessage = "URGENT: $message"
        return sender.sendBatch(recipients, urgentMessage)
    }
    
    fun sendWithRetry(recipient: String, message: String, maxRetries: Int = 3): List<String> {
        val results = mutableListOf<String>()
        repeat(maxRetries) { attempt ->
            val result = sendNotification(recipient, message)
            results.add("Attempt ${attempt + 1}: $result")
            if ("sent" in result.lowercase()) return results
        }
        results.add("Failed to send after $maxRetries attempts")
        return results
    }
}

class ScheduledNotificationService(sender: NotificationSender) : NotificationServiceBridge(sender) {
    private val scheduledMessages = mutableListOf<ScheduledMessage>()
    
    data class ScheduledMessage(
        val recipient: String,
        val message: String,
        val scheduledTime: Long,
        var sent: Boolean = false
    )
    
    override fun sendNotification(recipient: String, message: String): String {
        return sender.send(recipient, message)
    }
    
    override fun sendBulkNotification(recipients: List<String>, message: String): String {
        return sender.sendBatch(recipients, message)
    }
    
    fun scheduleNotification(recipient: String, message: String, delayMs: Long): String {
        val scheduledTime = System.currentTimeMillis() + delayMs
        scheduledMessages.add(ScheduledMessage(recipient, message, scheduledTime))
        return "Notification scheduled for $recipient in ${delayMs}ms"
    }
    
    fun processPendingNotifications(): List<String> {
        val results = mutableListOf<String>()
        val currentTime = System.currentTimeMillis()
        
        scheduledMessages.filter { !it.sent && it.scheduledTime <= currentTime }.forEach { msg ->
            val result = sendNotification(msg.recipient, msg.message)
            results.add(result)
            msg.sent = true
        }
        
        return results
    }
    
    fun cancelScheduledNotifications(recipient: String): Int {
        val toRemove = scheduledMessages.filter { !it.sent && it.recipient == recipient }
        scheduledMessages.removeAll(toRemove)
        return toRemove.size
    }
}

class MultiChannelNotificationService(
    private val channels: Map<String, NotificationSender>
) : NotificationServiceBridge(channels.values.first()) {
    
    override fun sendNotification(recipient: String, message: String): String {
        val results = mutableListOf<String>()
        channels.forEach { (channelName, sender) ->
            if (sender.isAvailable()) {
                results.add("$channelName: ${sender.send(recipient, message)}")
            } else {
                results.add("$channelName: Service unavailable")
            }
        }
        return results.joinToString("; ")
    }
    
    override fun sendBulkNotification(recipients: List<String>, message: String): String {
        val results = mutableListOf<String>()
        channels.forEach { (channelName, sender) ->
            if (sender.isAvailable()) {
                results.add("$channelName: ${sender.sendBatch(recipients, message)}")
            } else {
                results.add("$channelName: Service unavailable")
            }
        }
        return results.joinToString("; ")
    }
    
    fun sendToSpecificChannel(channel: String, recipient: String, message: String): String {
        val sender = channels[channel] ?: return "Channel $channel not found"
        return if (sender.isAvailable()) {
            sender.send(recipient, message)
        } else {
            "Channel $channel is unavailable"
        }
    }
    
    fun getChannelStatuses(): Map<String, Boolean> {
        return channels.mapValues { it.value.isAvailable() }
    }
}