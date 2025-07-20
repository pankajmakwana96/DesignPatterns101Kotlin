package patterns.structural

/**
 * # Adapter Pattern
 * 
 * ## Definition
 * Allows incompatible interfaces to work together. Acts as a bridge between two
 * incompatible interfaces by wrapping an existing class with a new interface.
 * 
 * ## Problem it solves
 * - Need to use an existing class with an incompatible interface
 * - Want to create a reusable class that cooperates with unrelated classes
 * - Need to use several existing subclasses, but it's impractical to adapt their interface
 * 
 * ## When to use
 * - Want to use an existing class with an incompatible interface
 * - Need to create a reusable class that cooperates with unrelated/unforeseen classes
 * - Want to integrate a third-party library
 * - Legacy system integration
 * 
 * ## When NOT to use
 * - When interfaces are already compatible
 * - When you can modify the original classes
 * - When the adaptation is too complex
 * 
 * ## Advantages
 * - Allows incompatible classes to work together
 * - Increases reusability of existing code
 * - Separates interface conversion from business logic
 * - Follows Single Responsibility Principle
 * 
 * ## Disadvantages
 * - Increases code complexity
 * - May introduce performance overhead
 * - Can mask the underlying interface complexity
 */

// 1. Classic Adapter Pattern - Media Player Example

// Target interface that client expects
interface MediaPlayer {
    fun play(audioType: String, fileName: String): String
}

// Adaptee interface - incompatible interface that needs adapting
interface AdvancedMediaPlayer {
    fun playVlc(fileName: String): String
    fun playMp4(fileName: String): String
}

// Concrete implementations of Adaptee
class VlcPlayer : AdvancedMediaPlayer {
    override fun playVlc(fileName: String): String = "Playing VLC file: $fileName"
    override fun playMp4(fileName: String): String = "VLC player cannot play MP4 files"
}

class Mp4Player : AdvancedMediaPlayer {
    override fun playVlc(fileName: String): String = "MP4 player cannot play VLC files"
    override fun playMp4(fileName: String): String = "Playing MP4 file: $fileName"
}

// Adapter class - adapts AdvancedMediaPlayer to MediaPlayer interface
class MediaAdapter(private val audioType: String) : MediaPlayer {
    private val advancedPlayer: AdvancedMediaPlayer = when (audioType.lowercase()) {
        "vlc" -> VlcPlayer()
        "mp4" -> Mp4Player()
        else -> throw IllegalArgumentException("Unsupported audio type: $audioType")
    }
    
    override fun play(audioType: String, fileName: String): String {
        return when (audioType.lowercase()) {
            "vlc" -> advancedPlayer.playVlc(fileName)
            "mp4" -> advancedPlayer.playMp4(fileName)
            else -> "Unsupported format: $audioType"
        }
    }
}

// Context class that uses the target interface
class AudioPlayer : MediaPlayer {
    override fun play(audioType: String, fileName: String): String {
        return when (audioType.lowercase()) {
            "mp3" -> "Playing MP3 file: $fileName"
            "vlc", "mp4" -> {
                val adapter = MediaAdapter(audioType)
                adapter.play(audioType, fileName)
            }
            else -> "Unsupported audio format: $audioType"
        }
    }
}

// 2. Object Adapter Pattern - Database Connection Example

// Target interface
interface DatabaseConnectionAdapter {
    fun connect(): String
    fun executeQuery(query: String): String
    fun disconnect(): String
}

// Adaptee - Third-party MySQL library with different interface
class MySQLLibrary {
    fun establishConnection(): String = "MySQL connection established"
    fun runQuery(sqlQuery: String): String = "MySQL query executed: $sqlQuery"
    fun closeConnection(): String = "MySQL connection closed"
}

// Adaptee - Third-party PostgreSQL library with different interface
class PostgreSQLLibrary {
    fun createConnection(): String = "PostgreSQL connection created"
    fun performQuery(sql: String): String = "PostgreSQL query performed: $sql"
    fun terminateConnection(): String = "PostgreSQL connection terminated"
}

// Object Adapter for MySQL
class MySQLAdapter(private val mysqlLibrary: MySQLLibrary) : DatabaseConnectionAdapter {
    override fun connect(): String = mysqlLibrary.establishConnection()
    override fun executeQuery(query: String): String = mysqlLibrary.runQuery(query)
    override fun disconnect(): String = mysqlLibrary.closeConnection()
}

// Object Adapter for PostgreSQL
class PostgreSQLAdapter(private val postgresLibrary: PostgreSQLLibrary) : DatabaseConnectionAdapter {
    override fun connect(): String = postgresLibrary.createConnection()
    override fun executeQuery(query: String): String = postgresLibrary.performQuery(query)
    override fun disconnect(): String = postgresLibrary.terminateConnection()
}

// 3. Kotlin Extension Function Adapter
// Sometimes we can use extension functions as a lightweight adapter

// Legacy payment processor with old interface
class LegacyPaymentProcessor {
    fun processPayment(amount: Double, cardNumber: String, cvv: String): String {
        return "Legacy: Processing $${amount} payment with card ending in ${cardNumber.takeLast(4)}"
    }
}

// Modern payment interface
interface ModernPaymentInterface {
    fun pay(amount: Double, paymentDetails: PaymentDetails): String
}

data class PaymentDetails(
    val cardNumber: String,
    val cvv: String,
    val expiryMonth: Int,
    val expiryYear: Int,
    val holderName: String
)

// Extension function adapter
fun LegacyPaymentProcessor.toModernPayment(): ModernPaymentInterface {
    return object : ModernPaymentInterface {
        override fun pay(amount: Double, paymentDetails: PaymentDetails): String {
            return this@toModernPayment.processPayment(amount, paymentDetails.cardNumber, paymentDetails.cvv)
        }
    }
}

// 4. Generic Adapter Pattern using delegation
interface DataSourceAdapter<T> {
    fun getData(): List<T>
    fun saveData(data: List<T>): String
}

// Different data sources with incompatible interfaces
class JsonDataProvider {
    fun loadJsonData(): String = """[{"id":1,"name":"John"},{"id":2,"name":"Jane"}]"""
    fun saveJsonData(json: String): String = "JSON data saved: $json"
}

class XmlDataProvider {
    fun loadXmlData(): String = "<users><user id='1'>John</user><user id='2'>Jane</user></users>"
    fun saveXmlData(xml: String): String = "XML data saved: $xml"
}

class CsvDataProvider {
    fun loadCsvData(): String = "id,name\n1,John\n2,Jane"
    fun saveCsvData(csv: String): String = "CSV data saved: $csv"
}

// Data model
data class User(val id: Int, val name: String)

// Generic adapter using higher-order functions
class GenericDataSourceAdapter<T>(
    private val loadFunction: () -> String,
    private val saveFunction: (String) -> String,
    private val parseFunction: (String) -> List<T>,
    private val serializeFunction: (List<T>) -> String
) : DataSourceAdapter<T> {
    
    override fun getData(): List<T> {
        val rawData = loadFunction()
        return parseFunction(rawData)
    }
    
    override fun saveData(data: List<T>): String {
        val serializedData = serializeFunction(data)
        return saveFunction(serializedData)
    }
}

// Factory for creating adapted data sources
object DataSourceFactory {
    fun createJsonAdapter(jsonProvider: JsonDataProvider): DataSourceAdapter<User> {
        return GenericDataSourceAdapter(
            loadFunction = jsonProvider::loadJsonData,
            saveFunction = jsonProvider::saveJsonData,
            parseFunction = { json -> 
                // Simplified JSON parsing - in real app use a JSON library
                listOf(User(1, "John"), User(2, "Jane"))
            },
            serializeFunction = { users ->
                // Simplified JSON serialization
                users.joinToString(prefix = "[", postfix = "]") { 
                    """{"id":${it.id},"name":"${it.name}"}""" 
                }
            }
        )
    }
    
    fun createXmlAdapter(xmlProvider: XmlDataProvider): DataSourceAdapter<User> {
        return GenericDataSourceAdapter(
            loadFunction = xmlProvider::loadXmlData,
            saveFunction = xmlProvider::saveXmlData,
            parseFunction = { xml ->
                // Simplified XML parsing
                listOf(User(1, "John"), User(2, "Jane"))
            },
            serializeFunction = { users ->
                val userXml = users.joinToString("") { "<user id='${it.id}'>${it.name}</user>" }
                "<users>$userXml</users>"
            }
        )
    }
    
    fun createCsvAdapter(csvProvider: CsvDataProvider): DataSourceAdapter<User> {
        return GenericDataSourceAdapter(
            loadFunction = csvProvider::loadCsvData,
            saveFunction = csvProvider::saveCsvData,
            parseFunction = { csv ->
                // Simplified CSV parsing
                csv.lines().drop(1).mapNotNull { line ->
                    val parts = line.split(",")
                    if (parts.size == 2) User(parts[0].toInt(), parts[1]) else null
                }
            },
            serializeFunction = { users ->
                val header = "id,name"
                val rows = users.joinToString("\n") { "${it.id},${it.name}" }
                "$header\n$rows"
            }
        )
    }
}

// 5. Two-Way Adapter Pattern
interface OldCalculator {
    fun add(a: Int, b: Int): Int
    fun subtract(a: Int, b: Int): Int
}

interface NewCalculator {
    fun calculate(operation: String, operands: List<Double>): Double
}

class ScientificCalculator : NewCalculator {
    override fun calculate(operation: String, operands: List<Double>): Double {
        return when (operation.lowercase()) {
            "add" -> operands.sum()
            "subtract" -> operands.reduceOrNull { acc, value -> acc - value } ?: 0.0
            "multiply" -> operands.reduceOrNull { acc, value -> acc * value } ?: 0.0
            "divide" -> operands.reduceOrNull { acc, value -> if (value != 0.0) acc / value else acc } ?: 0.0
            else -> throw IllegalArgumentException("Unknown operation: $operation")
        }
    }
}

class BasicCalculator : OldCalculator {
    override fun add(a: Int, b: Int): Int = a + b
    override fun subtract(a: Int, b: Int): Int = a - b
}

// Two-way adapter
class CalculatorAdapter : OldCalculator, NewCalculator {
    private val newCalculator = ScientificCalculator()
    private val oldCalculator = BasicCalculator()
    
    // Adapting new interface to old interface
    override fun add(a: Int, b: Int): Int {
        return newCalculator.calculate("add", listOf(a.toDouble(), b.toDouble())).toInt()
    }
    
    override fun subtract(a: Int, b: Int): Int {
        return newCalculator.calculate("subtract", listOf(a.toDouble(), b.toDouble())).toInt()
    }
    
    // Adapting old interface to new interface
    override fun calculate(operation: String, operands: List<Double>): Double {
        return when (operation.lowercase()) {
            "add" -> {
                require(operands.size == 2) { "Add operation requires exactly 2 operands" }
                oldCalculator.add(operands[0].toInt(), operands[1].toInt()).toDouble()
            }
            "subtract" -> {
                require(operands.size == 2) { "Subtract operation requires exactly 2 operands" }
                oldCalculator.subtract(operands[0].toInt(), operands[1].toInt()).toDouble()
            }
            else -> newCalculator.calculate(operation, operands)
        }
    }
}

// 6. Configuration Adapter for different environments
interface ConfigurationProvider {
    fun getProperty(key: String): String?
    fun getAllProperties(): Map<String, String>
}

// Different configuration sources
class EnvironmentVariables {
    fun getEnvVar(name: String): String? = System.getenv(name)
    fun getAllEnvVars(): Map<String, String> = System.getenv()
}

class PropertyFile(private val properties: Map<String, String>) {
    fun getProperty(key: String): String? = properties[key]
    fun getProperties(): Map<String, String> = properties
}

class DatabaseConfig(private val config: Map<String, String>) {
    fun fetchConfigValue(key: String): String? = config[key]
    fun fetchAllConfig(): Map<String, String> = config
}

// Adapters for different configuration sources
class EnvironmentAdapter(private val envVars: EnvironmentVariables) : ConfigurationProvider {
    override fun getProperty(key: String): String? = envVars.getEnvVar(key)
    override fun getAllProperties(): Map<String, String> = envVars.getAllEnvVars()
}

class PropertyFileAdapter(private val propertyFile: PropertyFile) : ConfigurationProvider {
    override fun getProperty(key: String): String? = propertyFile.getProperty(key)
    override fun getAllProperties(): Map<String, String> = propertyFile.getProperties()
}

class DatabaseConfigAdapter(private val dbConfig: DatabaseConfig) : ConfigurationProvider {
    override fun getProperty(key: String): String? = dbConfig.fetchConfigValue(key)
    override fun getAllProperties(): Map<String, String> = dbConfig.fetchAllConfig()
}

// Composite adapter that tries multiple sources
class CompositeConfigurationAdapter(
    private val providers: List<ConfigurationProvider>
) : ConfigurationProvider {
    
    override fun getProperty(key: String): String? {
        for (provider in providers) {
            provider.getProperty(key)?.let { return it }
        }
        return null
    }
    
    override fun getAllProperties(): Map<String, String> {
        val allProperties = mutableMapOf<String, String>()
        providers.reversed().forEach { provider ->
            allProperties.putAll(provider.getAllProperties())
        }
        return allProperties
    }
}