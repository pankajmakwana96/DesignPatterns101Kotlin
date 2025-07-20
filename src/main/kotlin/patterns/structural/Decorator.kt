package patterns.structural

/**
 * # Decorator Pattern
 * 
 * ## Definition
 * Attaches additional responsibilities to an object dynamically without altering its structure.
 * Provides a flexible alternative to subclassing for extending functionality.
 * 
 * ## Problem it solves
 * - Need to add behavior to objects without changing their interface
 * - Want to add responsibilities to objects dynamically
 * - Extension by subclassing is impractical
 * - Want to combine behaviors in different ways
 * 
 * ## When to use
 * - Add responsibilities to individual objects dynamically and transparently
 * - Withdraw responsibilities from objects
 * - When extension by subclassing is impractical
 * - Need to combine behaviors in different ways
 * 
 * ## When NOT to use
 * - Simple inheritance is sufficient
 * - Object interfaces are different
 * - Too many small objects in the system
 * 
 * ## Advantages
 * - More flexible than static inheritance
 * - Avoids feature-laden classes high up in the hierarchy
 * - Can add/remove responsibilities at runtime
 * - Can combine behaviors in different ways
 * 
 * ## Disadvantages
 * - Can result in many small objects
 * - Decorators and components aren't identical
 * - Can complicate debugging
 * - May affect performance
 */

// 1. Classic Decorator Pattern - Coffee Shop Example

// Component interface
interface Coffee {
    fun getDescription(): String
    fun getCost(): Double
}

// Concrete component
class SimpleCoffee : Coffee {
    override fun getDescription(): String = "Simple Coffee"
    override fun getCost(): Double = 2.0
}

class Espresso : Coffee {
    override fun getDescription(): String = "Espresso"
    override fun getCost(): Double = 1.5
}

class DarkRoast : Coffee {
    override fun getDescription(): String = "Dark Roast Coffee"
    override fun getCost(): Double = 2.5
}

// Decorator base class
abstract class CoffeeDecorator(protected val coffee: Coffee) : Coffee {
    override fun getDescription(): String = coffee.getDescription()
    override fun getCost(): Double = coffee.getCost()
}

// Concrete decorators
class MilkDecorator(coffee: Coffee) : CoffeeDecorator(coffee) {
    override fun getDescription(): String = "${super.getDescription()}, Milk"
    override fun getCost(): Double = super.getCost() + 0.5
}

class SugarDecorator(coffee: Coffee) : CoffeeDecorator(coffee) {
    override fun getDescription(): String = "${super.getDescription()}, Sugar"
    override fun getCost(): Double = super.getCost() + 0.2
}

class VanillaDecorator(coffee: Coffee) : CoffeeDecorator(coffee) {
    override fun getDescription(): String = "${super.getDescription()}, Vanilla"
    override fun getCost(): Double = super.getCost() + 0.7
}

class WhipCreamDecorator(coffee: Coffee) : CoffeeDecorator(coffee) {
    override fun getDescription(): String = "${super.getDescription()}, Whipped Cream"
    override fun getCost(): Double = super.getCost() + 0.8
}

class CaramelDecorator(coffee: Coffee) : CoffeeDecorator(coffee) {
    override fun getDescription(): String = "${super.getDescription()}, Caramel"
    override fun getCost(): Double = super.getCost() + 0.6
}

class ExtraShotDecorator(coffee: Coffee) : CoffeeDecorator(coffee) {
    override fun getDescription(): String = "${super.getDescription()}, Extra Shot"
    override fun getCost(): Double = super.getCost() + 1.0
}

// 2. Text Processing Decorator Pattern

// Component interface
interface TextProcessor {
    fun process(text: String): String
}

// Concrete component
class PlainTextProcessor : TextProcessor {
    override fun process(text: String): String = text
}

// Decorator base class
abstract class TextDecorator(protected val processor: TextProcessor) : TextProcessor {
    override fun process(text: String): String = processor.process(text)
}

// Concrete decorators
class UpperCaseDecorator(processor: TextProcessor) : TextDecorator(processor) {
    override fun process(text: String): String = super.process(text).uppercase()
}

class LowerCaseDecorator(processor: TextProcessor) : TextDecorator(processor) {
    override fun process(text: String): String = super.process(text).lowercase()
}

class TrimWhitespaceDecorator(processor: TextProcessor) : TextDecorator(processor) {
    override fun process(text: String): String = super.process(text).trim()
}

class RemoveSpecialCharsDecorator(processor: TextProcessor) : TextDecorator(processor) {
    override fun process(text: String): String {
        return super.process(text).replace(Regex("[^a-zA-Z0-9\\s]"), "")
    }
}

class CapitalizeWordsDecorator(processor: TextProcessor) : TextDecorator(processor) {
    override fun process(text: String): String {
        return super.process(text).split(" ").joinToString(" ") { word ->
            word.lowercase().replaceFirstChar { it.uppercase() }
        }
    }
}

class ReverseTextDecorator(processor: TextProcessor) : TextDecorator(processor) {
    override fun process(text: String): String = super.process(text).reversed()
}

class AddPrefixDecorator(
    processor: TextProcessor,
    private val prefix: String
) : TextDecorator(processor) {
    override fun process(text: String): String = "$prefix${super.process(text)}"
}

class AddSuffixDecorator(
    processor: TextProcessor,
    private val suffix: String
) : TextDecorator(processor) {
    override fun process(text: String): String = "${super.process(text)}$suffix"
}

// 3. Data Source Decorator Pattern

// Component interface
interface DataSource {
    fun readData(): String
    fun writeData(data: String): String
}

// Concrete component
class FileDataSource(private val filename: String) : DataSource {
    override fun readData(): String = "Reading data from file: $filename"
    override fun writeData(data: String): String = "Writing data to file $filename: $data"
}

class DatabaseDataSource(private val connectionString: String) : DataSource {
    override fun readData(): String = "Reading data from database: $connectionString"
    override fun writeData(data: String): String = "Writing data to database $connectionString: $data"
}

// Decorator base class
abstract class DataSourceDecorator(protected val dataSource: DataSource) : DataSource {
    override fun readData(): String = dataSource.readData()
    override fun writeData(data: String): String = dataSource.writeData(data)
}

// Concrete decorators
class EncryptionDecorator(dataSource: DataSource) : DataSourceDecorator(dataSource) {
    override fun readData(): String {
        val data = super.readData()
        return "Decrypted: $data"
    }
    
    override fun writeData(data: String): String {
        val encryptedData = "Encrypted: $data"
        return super.writeData(encryptedData)
    }
}

class CompressionDecorator(dataSource: DataSource) : DataSourceDecorator(dataSource) {
    override fun readData(): String {
        val data = super.readData()
        return "Decompressed: $data"
    }
    
    override fun writeData(data: String): String {
        val compressedData = "Compressed: $data"
        return super.writeData(compressedData)
    }
}

class CachingDecorator(dataSource: DataSource) : DataSourceDecorator(dataSource) {
    private var cachedData: String? = null
    private var cacheValid = false
    
    override fun readData(): String {
        return if (cacheValid && cachedData != null) {
            "Cache hit: $cachedData"
        } else {
            val data = super.readData()
            cachedData = data
            cacheValid = true
            "Cache miss: $data"
        }
    }
    
    override fun writeData(data: String): String {
        cacheValid = false
        cachedData = null
        return super.writeData(data)
    }
    
    fun invalidateCache() {
        cacheValid = false
        cachedData = null
    }
}

class LoggingDecorator(dataSource: DataSource) : DataSourceDecorator(dataSource) {
    override fun readData(): String {
        val timestamp = System.currentTimeMillis()
        val result = super.readData()
        return "[$timestamp] READ: $result"
    }
    
    override fun writeData(data: String): String {
        val timestamp = System.currentTimeMillis()
        val result = super.writeData(data)
        return "[$timestamp] WRITE: $result"
    }
}

class ValidationDecorator(dataSource: DataSource) : DataSourceDecorator(dataSource) {
    override fun readData(): String {
        val data = super.readData()
        return if (isValidData(data)) {
            "Validated: $data"
        } else {
            "Invalid data detected: $data"
        }
    }
    
    override fun writeData(data: String): String {
        return if (isValidData(data)) {
            super.writeData("Validated: $data")
        } else {
            "Write failed - invalid data: $data"
        }
    }
    
    private fun isValidData(data: String): Boolean {
        return data.isNotEmpty() && !data.contains("invalid")
    }
}

// 4. Kotlin Functional Decorator using Higher-Order Functions

typealias StringProcessor = (String) -> String

// Functional decorators
fun upperCaseDecorator(processor: StringProcessor): StringProcessor = { text ->
    processor(text).uppercase()
}

fun lowerCaseDecorator(processor: StringProcessor): StringProcessor = { text ->
    processor(text).lowercase()
}

fun trimDecorator(processor: StringProcessor): StringProcessor = { text ->
    processor(text).trim()
}

fun prefixDecorator(prefix: String): (StringProcessor) -> StringProcessor = { processor ->
    { text -> "$prefix${processor(text)}" }
}

fun suffixDecorator(suffix: String): (StringProcessor) -> StringProcessor = { processor ->
    { text -> "${processor(text)}$suffix" }
}

// Extension function for chaining decorators
fun StringProcessor.then(decorator: (StringProcessor) -> StringProcessor): StringProcessor {
    return decorator(this)
}

// 5. Notification Decorator Pattern

// Component interface
interface NotificationService {
    fun send(message: String, recipient: String): String
}

// Concrete components
class EmailNotification : NotificationService {
    override fun send(message: String, recipient: String): String =
        "Email sent to $recipient: $message"
}

class SmsNotification : NotificationService {
    override fun send(message: String, recipient: String): String =
        "SMS sent to $recipient: $message"
}

class SlackNotification : NotificationService {
    override fun send(message: String, recipient: String): String =
        "Slack message sent to $recipient: $message"
}

// Decorator base class
abstract class NotificationDecorator(
    protected val notification: NotificationService
) : NotificationService {
    override fun send(message: String, recipient: String): String =
        notification.send(message, recipient)
}

// Concrete decorators
class UrgentNotificationDecorator(
    notification: NotificationService
) : NotificationDecorator(notification) {
    override fun send(message: String, recipient: String): String {
        val urgentMessage = "🚨 URGENT: $message"
        return super.send(urgentMessage, recipient)
    }
}

class EncryptedNotificationDecorator(
    notification: NotificationService
) : NotificationDecorator(notification) {
    override fun send(message: String, recipient: String): String {
        val encryptedMessage = encrypt(message)
        return "Encrypted: ${super.send(encryptedMessage, recipient)}"
    }
    
    private fun encrypt(message: String): String = "ENCRYPTED[$message]"
}

class RetryNotificationDecorator(
    notification: NotificationService,
    private val maxRetries: Int = 3
) : NotificationDecorator(notification) {
    override fun send(message: String, recipient: String): String {
        repeat(maxRetries) { attempt ->
            try {
                val result = super.send(message, recipient)
                return "Attempt ${attempt + 1}: $result"
            } catch (e: Exception) {
                if (attempt == maxRetries - 1) {
                    return "Failed after $maxRetries attempts: ${e.message}"
                }
            }
        }
        return "Failed to send notification"
    }
}

class TimestampNotificationDecorator(
    notification: NotificationService
) : NotificationDecorator(notification) {
    override fun send(message: String, recipient: String): String {
        val timestamp = System.currentTimeMillis()
        val timestampedMessage = "[$timestamp] $message"
        return super.send(timestampedMessage, recipient)
    }
}

class PriorityNotificationDecorator(
    notification: NotificationService,
    private val priority: Priority
) : NotificationDecorator(notification) {
    
    enum class Priority(val symbol: String) {
        LOW("🔵"), MEDIUM("🟡"), HIGH("🔴")
    }
    
    override fun send(message: String, recipient: String): String {
        val priorityMessage = "${priority.symbol} [${priority.name}] $message"
        return super.send(priorityMessage, recipient)
    }
}

// 6. Stream Processing Decorator Pattern

// Component interface
interface DataStream<T> {
    fun process(data: List<T>): List<T>
}

// Concrete component
class BasicDataStream<T> : DataStream<T> {
    override fun process(data: List<T>): List<T> = data
}

// Decorator base class
abstract class DataStreamDecorator<T>(
    protected val stream: DataStream<T>
) : DataStream<T> {
    override fun process(data: List<T>): List<T> = stream.process(data)
}

// Concrete decorators
class FilterDecorator<T>(
    stream: DataStream<T>,
    private val predicate: (T) -> Boolean
) : DataStreamDecorator<T>(stream) {
    override fun process(data: List<T>): List<T> {
        return super.process(data).filter(predicate)
    }
}

class MapDecorator<T, R>(
    private val stream: DataStream<T>,
    private val transform: (T) -> R
) : DataStream<R> {
    override fun process(data: List<R>): List<R> {
        // This would need proper type handling in real implementation
        return data
    }
    
    fun processTyped(data: List<T>): List<R> {
        return stream.process(data).map(transform)
    }
}

class SortDecorator<T : Comparable<T>>(
    stream: DataStream<T>,
    private val descending: Boolean = false
) : DataStreamDecorator<T>(stream) {
    override fun process(data: List<T>): List<T> {
        val processed = super.process(data)
        return if (descending) processed.sortedDescending() else processed.sorted()
    }
}

class LimitDecorator<T>(
    stream: DataStream<T>,
    private val limit: Int
) : DataStreamDecorator<T>(stream) {
    override fun process(data: List<T>): List<T> {
        return super.process(data).take(limit)
    }
}

class DistinctDecorator<T>(
    stream: DataStream<T>
) : DataStreamDecorator<T>(stream) {
    override fun process(data: List<T>): List<T> {
        return super.process(data).distinct()
    }
}

// Builder for creating decorated streams
class DataStreamBuilder<T> {
    private var stream: DataStream<T> = BasicDataStream()
    
    fun filter(predicate: (T) -> Boolean): DataStreamBuilder<T> {
        stream = FilterDecorator(stream, predicate)
        return this
    }
    
    fun sort(descending: Boolean = false): DataStreamBuilder<T> {
        @Suppress("UNCHECKED_CAST")
        stream = SortDecorator(stream as DataStream<Comparable<Any>>, descending) as DataStream<T>
        return this
    }
    
    fun limit(count: Int): DataStreamBuilder<T> {
        stream = LimitDecorator(stream, count)
        return this
    }
    
    fun distinct(): DataStreamBuilder<T> {
        stream = DistinctDecorator(stream)
        return this
    }
    
    fun build(): DataStream<T> = stream
}

// Utility function for creating decorated streams
fun <T> dataStream(block: DataStreamBuilder<T>.() -> Unit): DataStream<T> {
    return DataStreamBuilder<T>().apply(block).build()
}