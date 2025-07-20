package patterns.creational

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * # Singleton Pattern
 * 
 * ## Definition
 * Ensures a class has only one instance and provides a global point of access to it.
 * 
 * ## Problem it solves
 * - Need exactly one instance of a class (e.g., database connection, logger, cache)
 * - Need global access to that instance
 * - Want to control instantiation
 * 
 * ## When to use
 * - Database connections
 * - Logging systems
 * - Configuration managers
 * - Cache implementations
 * - Thread pools
 * 
 * ## When NOT to use
 * - When you need multiple instances
 * - In unit testing (hard to mock)
 * - When it creates hidden dependencies
 * - When it violates single responsibility principle
 * 
 * ## Advantages
 * - Controlled access to sole instance
 * - Reduced namespace pollution
 * - Permits refinement of operations and representation
 * - Can control the number of instances
 * 
 * ## Disadvantages
 * - Difficult to unit test
 * - Violates single responsibility principle
 * - Can mask bad design
 * - Problems in multithreaded environments
 * - Can become a bottleneck
 */

// 1. Object Declaration - Kotlin's idiomatic singleton
object DatabaseConnection {
    private var connectionCount = 0
    
    fun connect(): String {
        connectionCount++
        return "Database connected (connection #$connectionCount)"
    }
    
    fun getConnectionCount(): Int = connectionCount
}

// 2. Class-based singleton with lazy initialization (thread-safe)
class Logger private constructor() {
    companion object {
        @Volatile
        private var INSTANCE: Logger? = null
        
        fun getInstance(): Logger {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Logger().also { INSTANCE = it }
            }
        }
    }
    
    private val logs = mutableListOf<String>()
    
    fun log(message: String) {
        logs.add("[${System.currentTimeMillis()}] $message")
    }
    
    fun getLogs(): List<String> = logs.toList()
    
    fun clearLogs() = logs.clear()
}

// 3. Lazy delegate singleton - Kotlin's lazy delegate ensures thread safety
class ConfigurationManager private constructor() {
    companion object {
        val instance: ConfigurationManager by lazy { ConfigurationManager() }
    }
    
    private val properties = mutableMapOf<String, String>()
    
    fun setProperty(key: String, value: String) {
        properties[key] = value
    }
    
    fun getProperty(key: String): String? = properties[key]
    
    fun getAllProperties(): Map<String, String> = properties.toMap()
}

// 4. Enum singleton - Java-style, but works in Kotlin
enum class ApplicationState {
    INSTANCE;
    
    private var currentState: String = "IDLE"
    
    fun setState(state: String) {
        currentState = state
    }
    
    fun getState(): String = currentState
}

// 5. Thread-safe singleton with coroutines
class AsyncCacheManager private constructor() {
    companion object {
        @Volatile
        private var INSTANCE: AsyncCacheManager? = null
        private val mutex = Mutex()
        
        suspend fun getInstance(): AsyncCacheManager {
            return INSTANCE ?: mutex.withLock {
                INSTANCE ?: AsyncCacheManager().also { INSTANCE = it }
            }
        }
    }
    
    private val cache = mutableMapOf<String, Any>()
    private val cacheMutex = Mutex()
    
    suspend fun put(key: String, value: Any) {
        cacheMutex.withLock {
            cache[key] = value
        }
    }
    
    suspend fun get(key: String): Any? {
        return cacheMutex.withLock {
            cache[key]
        }
    }
    
    suspend fun size(): Int {
        return cacheMutex.withLock {
            cache.size
        }
    }
}

// 6. Registry pattern (alternative to singleton)
object ServiceRegistry {
    val services = mutableMapOf<String, Any>()
    
    inline fun <reified T> register(service: T) {
        services[T::class.java.name] = service as Any
    }
    
    inline fun <reified T> get(): T? {
        return services[T::class.java.name] as? T
    }
    
    fun clear() = services.clear()
}

// Example service for registry
class EmailService {
    fun sendEmail(to: String, subject: String, body: String): String {
        return "Email sent to $to with subject '$subject'"
    }
}

// 7. Singleton with parameters (using factory method)
class DatabasePool private constructor(private val maxConnections: Int) {
    companion object {
        @Volatile
        private var INSTANCE: DatabasePool? = null
        
        fun getInstance(maxConnections: Int = 10): DatabasePool {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: DatabasePool(maxConnections).also { INSTANCE = it }
            }
        }
    }
    
    private var activeConnections = 0
    
    fun getConnection(): String? {
        return if (activeConnections < maxConnections) {
            activeConnections++
            "Connection #$activeConnections (max: $maxConnections)"
        } else {
            null
        }
    }
    
    fun releaseConnection() {
        if (activeConnections > 0) {
            activeConnections--
        }
    }
    
    fun getActiveConnections(): Int = activeConnections
}