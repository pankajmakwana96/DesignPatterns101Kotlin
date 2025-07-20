package patterns.creational

/**
 * # Factory Method Pattern
 * 
 * ## Definition
 * Creates objects without specifying the exact class to create. Defines an interface
 * for creating objects, but lets subclasses decide which classes to instantiate.
 * 
 * ## Problem it solves
 * - Need to create objects without knowing their exact types
 * - Want to delegate object creation to subclasses
 * - Need to provide hooks for subclasses to extend object creation
 * 
 * ## When to use
 * - Class can't anticipate the class of objects it must create
 * - Class wants its subclasses to specify the objects it creates
 * - Classes delegate responsibility to helper subclasses
 * 
 * ## When NOT to use
 * - When object creation is simple and doesn't vary
 * - When you don't need the flexibility of subclass creation
 * - When it adds unnecessary complexity
 * 
 * ## Advantages
 * - Eliminates need to bind application-specific classes
 * - Provides hooks for subclasses
 * - Connects parallel class hierarchies
 * - Code is more flexible and reusable
 * 
 * ## Disadvantages
 * - Can make code more complex
 * - Requires creating subclasses just for object creation
 * - May not be needed for simple cases
 */

// Product interface
interface Vehicle {
    fun start(): String
    fun stop(): String
    fun getType(): String
    fun getMaxSpeed(): Int
}

// Concrete Products
class Car(private val model: String) : Vehicle {
    override fun start(): String = "Car $model engine started"
    override fun stop(): String = "Car $model engine stopped"
    override fun getType(): String = "Car"
    override fun getMaxSpeed(): Int = 200
}

class Motorcycle(private val model: String) : Vehicle {
    override fun start(): String = "Motorcycle $model engine started"
    override fun stop(): String = "Motorcycle $model engine stopped"
    override fun getType(): String = "Motorcycle"
    override fun getMaxSpeed(): Int = 300
}

class Truck(private val model: String) : Vehicle {
    override fun start(): String = "Truck $model engine started"
    override fun stop(): String = "Truck $model engine stopped"
    override fun getType(): String = "Truck"
    override fun getMaxSpeed(): Int = 120
}

// Creator abstract class
abstract class VehicleFactory {
    // Factory method - subclasses must implement
    abstract fun createVehicle(model: String): Vehicle
    
    // Template method that uses the factory method
    fun manufactureVehicle(model: String): Vehicle {
        val vehicle = createVehicle(model)
        performQualityCheck(vehicle)
        return vehicle
    }
    
    private fun performQualityCheck(vehicle: Vehicle) {
        println("Quality check passed for ${vehicle.getType()}")
    }
}

// Concrete Creators
class CarFactory : VehicleFactory() {
    override fun createVehicle(model: String): Vehicle = Car(model)
}

class MotorcycleFactory : VehicleFactory() {
    override fun createVehicle(model: String): Vehicle = Motorcycle(model)
}

class TruckFactory : VehicleFactory() {
    override fun createVehicle(model: String): Vehicle = Truck(model)
}

// 2. Kotlin-specific approach using sealed classes and when expression
sealed class DocumentType {
    object PDF : DocumentType()
    object Word : DocumentType()
    object Excel : DocumentType()
    data class Custom(val extension: String) : DocumentType()
}

interface Document {
    fun open(): String
    fun save(): String
    fun close(): String
    fun getFormat(): String
}

class PDFDocument : Document {
    override fun open(): String = "Opening PDF document"
    override fun save(): String = "Saving PDF document"
    override fun close(): String = "Closing PDF document"
    override fun getFormat(): String = "PDF"
}

class WordDocument : Document {
    override fun open(): String = "Opening Word document"
    override fun save(): String = "Saving Word document"
    override fun close(): String = "Closing Word document"
    override fun getFormat(): String = "DOCX"
}

class ExcelDocument : Document {
    override fun open(): String = "Opening Excel document"
    override fun save(): String = "Saving Excel document"
    override fun close(): String = "Closing Excel document"
    override fun getFormat(): String = "XLSX"
}

class CustomDocument(private val extension: String) : Document {
    override fun open(): String = "Opening $extension document"
    override fun save(): String = "Saving $extension document"
    override fun close(): String = "Closing $extension document"
    override fun getFormat(): String = extension
}

// Factory using sealed classes and when expression
object DocumentFactory {
    fun createDocument(type: DocumentType): Document = when (type) {
        is DocumentType.PDF -> PDFDocument()
        is DocumentType.Word -> WordDocument()
        is DocumentType.Excel -> ExcelDocument()
        is DocumentType.Custom -> CustomDocument(type.extension)
    }
}

// 3. Generic factory method using reified type parameters
interface LoggerFM {
    fun log(message: String): String
}

class ConsoleLogger : LoggerFM {
    override fun log(message: String): String = "Console: $message"
}

class FileLogger : LoggerFM {
    override fun log(message: String): String = "File: $message"
}

class DatabaseLogger : LoggerFM {
    override fun log(message: String): String = "Database: $message"
}

object LoggerFactory {
    inline fun <reified T : LoggerFM> createLogger(): LoggerFM = when (T::class) {
        ConsoleLogger::class -> ConsoleLogger()
        FileLogger::class -> FileLogger()
        DatabaseLogger::class -> DatabaseLogger()
        else -> throw IllegalArgumentException("Unknown logger type: ${T::class}")
    }
    
    // Alternative with string-based creation
    fun createLogger(type: String): LoggerFM = when (type.lowercase()) {
        "console" -> ConsoleLogger()
        "file" -> FileLogger()
        "database" -> DatabaseLogger()
        else -> throw IllegalArgumentException("Unknown logger type: $type")
    }
}

// 4. Factory method with dependency injection
interface PaymentProcessor {
    fun processPayment(amount: Double): String
}

class CreditCardProcessor : PaymentProcessor {
    override fun processPayment(amount: Double): String = 
        "Processing credit card payment of $$amount"
}

class PayPalProcessor : PaymentProcessor {
    override fun processPayment(amount: Double): String = 
        "Processing PayPal payment of $$amount"
}

class CryptoProcessor : PaymentProcessor {
    override fun processPayment(amount: Double): String = 
        "Processing cryptocurrency payment of $$amount"
}

enum class PaymentMethod {
    CREDIT_CARD, PAYPAL, CRYPTO
}

class PaymentProcessorFactory(
    private val defaultProcessor: PaymentProcessor = CreditCardProcessor()
) {
    fun createProcessor(method: PaymentMethod): PaymentProcessor = when (method) {
        PaymentMethod.CREDIT_CARD -> CreditCardProcessor()
        PaymentMethod.PAYPAL -> PayPalProcessor()
        PaymentMethod.CRYPTO -> CryptoProcessor()
    }
    
    fun createDefaultProcessor(): PaymentProcessor = defaultProcessor
}

// 5. Factory method with configuration
data class DatabaseConfigFM(
    val host: String,
    val port: Int,
    val database: String,
    val username: String,
    val password: String
)

interface DatabaseConnectionFM {
    fun connect(): String
    fun executeQuery(query: String): String
    fun disconnect(): String
}

class MySQLConnectionFM(private val config: DatabaseConfigFM) : DatabaseConnectionFM {
    override fun connect(): String = 
        "Connected to MySQL at ${config.host}:${config.port}/${config.database}"
    override fun executeQuery(query: String): String = "MySQL: Executing $query"
    override fun disconnect(): String = "Disconnected from MySQL"
}

class PostgreSQLConnectionFM(private val config: DatabaseConfigFM) : DatabaseConnectionFM {
    override fun connect(): String = 
        "Connected to PostgreSQL at ${config.host}:${config.port}/${config.database}"
    override fun executeQuery(query: String): String = "PostgreSQL: Executing $query"
    override fun disconnect(): String = "Disconnected from PostgreSQL"
}

class MongoDBConnectionFM(private val config: DatabaseConfigFM) : DatabaseConnectionFM {
    override fun connect(): String = 
        "Connected to MongoDB at ${config.host}:${config.port}/${config.database}"
    override fun executeQuery(query: String): String = "MongoDB: Executing $query"
    override fun disconnect(): String = "Disconnected from MongoDB"
}

enum class DatabaseType {
    MYSQL, POSTGRESQL, MONGODB
}

class DatabaseConnectionFactory {
    fun createConnection(type: DatabaseType, config: DatabaseConfigFM): DatabaseConnectionFM {
        return when (type) {
            DatabaseType.MYSQL -> MySQLConnectionFM(config)
            DatabaseType.POSTGRESQL -> PostgreSQLConnectionFM(config)
            DatabaseType.MONGODB -> MongoDBConnectionFM(config)
        }
    }
}

// 6. Functional approach using higher-order functions
typealias VehicleCreator = (String) -> Vehicle

object FunctionalVehicleFactory {
    private val creators: Map<String, VehicleCreator> = mapOf(
        "car" to { model -> Car(model) },
        "motorcycle" to { model -> Motorcycle(model) },
        "truck" to { model -> Truck(model) }
    )
    
    fun createVehicle(type: String, model: String): Vehicle {
        val creator = creators[type.lowercase()] 
            ?: throw IllegalArgumentException("Unknown vehicle type: $type")
        return creator(model)
    }
    
    fun registerCreator(type: String, creator: VehicleCreator) {
        (creators as MutableMap)[type] = creator
    }
}