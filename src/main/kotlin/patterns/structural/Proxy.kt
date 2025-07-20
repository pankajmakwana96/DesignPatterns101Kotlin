package patterns.structural

import kotlinx.coroutines.delay

/**
 * # Proxy Pattern
 * 
 * ## Definition
 * Provides a placeholder or surrogate for another object to control access to it.
 * Acts as an interface to something else, adding a level of indirection.
 * 
 * ## Problem it solves
 * - Need to control access to an object
 * - Want to add functionality when accessing an object
 * - Object creation is expensive and should be deferred
 * - Need to perform actions before/after accessing the real object
 * 
 * ## When to use
 * - Remote proxy: represent object in different address space
 * - Virtual proxy: create expensive objects on demand
 * - Protection proxy: control access to object
 * - Smart reference: additional actions when object is accessed
 * 
 * ## When NOT to use
 * - Simple object access is sufficient
 * - Performance overhead is not acceptable
 * - Direct access is required
 * 
 * ## Advantages
 * - Controls access to the object
 * - Can perform additional actions transparently
 * - Lazy loading and caching capabilities
 * - Can add security, logging, or caching
 * 
 * ## Disadvantages
 * - Adds complexity and indirection
 * - May slow down response times
 * - Can introduce bugs if not implemented carefully
 */

// 1. Virtual Proxy - Lazy Loading of Expensive Objects

// Subject interface
interface Image {
    fun display(): String
    fun getInfo(): String
}

// Real subject - expensive to create
class RealImage(private val filename: String) : Image {
    init {
        loadFromDisk()
    }
    
    private fun loadFromDisk() {
        println("Loading image from disk: $filename")
        // Simulate expensive loading operation
        Thread.sleep(100)
    }
    
    override fun display(): String = "Displaying image: $filename"
    override fun getInfo(): String = "RealImage: $filename (loaded from disk)"
}

// Virtual proxy - controls access and provides lazy loading
class ImageProxy(private val filename: String) : Image {
    private var realImage: RealImage? = null
    
    override fun display(): String {
        if (realImage == null) {
            realImage = RealImage(filename)
        }
        return realImage!!.display()
    }
    
    override fun getInfo(): String {
        return if (realImage == null) {
            "ImageProxy: $filename (not loaded yet)"
        } else {
            "ImageProxy: $filename (loaded)"
        }
    }
    
    fun isLoaded(): Boolean = realImage != null
}

// 2. Protection Proxy - Access Control

// Subject interface
interface DocumentService {
    fun readDocument(documentId: String): String
    fun writeDocument(documentId: String, content: String): String
    fun deleteDocument(documentId: String): String
}

// Real subject
class RealDocumentService : DocumentService {
    private val documents = mutableMapOf<String, String>()
    
    override fun readDocument(documentId: String): String {
        return documents[documentId] ?: "Document not found: $documentId"
    }
    
    override fun writeDocument(documentId: String, content: String): String {
        documents[documentId] = content
        return "Document $documentId written successfully"
    }
    
    override fun deleteDocument(documentId: String): String {
        return if (documents.remove(documentId) != null) {
            "Document $documentId deleted successfully"
        } else {
            "Document not found: $documentId"
        }
    }
}

// User roles
enum class UserRole {
    ADMIN, EDITOR, VIEWER, GUEST
}

data class UserProxy(val id: String, val name: String, val role: UserRole)

// Protection proxy
class ProtectedDocumentService(
    private val realService: DocumentService,
    private val currentUser: UserProxy
) : DocumentService {
    
    override fun readDocument(documentId: String): String {
        return when (currentUser.role) {
            UserRole.ADMIN, UserRole.EDITOR, UserRole.VIEWER -> {
                logAccess("READ", documentId)
                realService.readDocument(documentId)
            }
            UserRole.GUEST -> "Access denied: Guests cannot read documents"
        }
    }
    
    override fun writeDocument(documentId: String, content: String): String {
        return when (currentUser.role) {
            UserRole.ADMIN, UserRole.EDITOR -> {
                logAccess("WRITE", documentId)
                realService.writeDocument(documentId, content)
            }
            UserRole.VIEWER -> "Access denied: Viewers cannot write documents"
            UserRole.GUEST -> "Access denied: Guests cannot write documents"
        }
    }
    
    override fun deleteDocument(documentId: String): String {
        return when (currentUser.role) {
            UserRole.ADMIN -> {
                logAccess("DELETE", documentId)
                realService.deleteDocument(documentId)
            }
            UserRole.EDITOR -> "Access denied: Editors cannot delete documents"
            UserRole.VIEWER -> "Access denied: Viewers cannot delete documents"
            UserRole.GUEST -> "Access denied: Guests cannot delete documents"
        }
    }
    
    private fun logAccess(operation: String, documentId: String) {
        println("User ${currentUser.name} (${currentUser.role}) performed $operation on $documentId")
    }
}

// 3. Caching Proxy - Performance Enhancement

// Subject interface
interface WeatherService {
    fun getWeather(city: String): WeatherData
    fun getForecast(city: String, days: Int): List<WeatherData>
}

data class WeatherData(
    val city: String,
    val temperature: Double,
    val humidity: Int,
    val description: String,
    val timestamp: Long = System.currentTimeMillis()
)

// Real subject - expensive network calls
class RealWeatherService : WeatherService {
    override fun getWeather(city: String): WeatherData {
        // Simulate network delay
        Thread.sleep(500)
        return WeatherData(
            city = city,
            temperature = 20.0 + (Math.random() * 20),
            humidity = 30 + (Math.random() * 50).toInt(),
            description = listOf("Sunny", "Cloudy", "Rainy", "Snowy").random()
        )
    }
    
    override fun getForecast(city: String, days: Int): List<WeatherData> {
        // Simulate network delay
        Thread.sleep(1000)
        return (1..days).map { day ->
            WeatherData(
                city = city,
                temperature = 15.0 + (Math.random() * 25),
                humidity = 25 + (Math.random() * 60).toInt(),
                description = listOf("Sunny", "Cloudy", "Rainy", "Snowy").random(),
                timestamp = System.currentTimeMillis() + (day * 24 * 60 * 60 * 1000)
            )
        }
    }
}

// Caching proxy
class CachingWeatherProxy(
    private val realService: WeatherService,
    private val cacheExpirationMs: Long = 5 * 60 * 1000 // 5 minutes
) : WeatherService {
    
    private val weatherCache = mutableMapOf<String, CachedWeatherData>()
    private val forecastCache = mutableMapOf<String, CachedForecast>()
    
    data class CachedWeatherData(val data: WeatherData, val cachedAt: Long)
    data class CachedForecast(val data: List<WeatherData>, val cachedAt: Long, val days: Int)
    
    override fun getWeather(city: String): WeatherData {
        val cached = weatherCache[city]
        val now = System.currentTimeMillis()
        
        return if (cached != null && (now - cached.cachedAt) < cacheExpirationMs) {
            println("Cache hit for weather in $city")
            cached.data
        } else {
            println("Cache miss for weather in $city - fetching from service")
            val freshData = realService.getWeather(city)
            weatherCache[city] = CachedWeatherData(freshData, now)
            freshData
        }
    }
    
    override fun getForecast(city: String, days: Int): List<WeatherData> {
        val cacheKey = "$city-$days"
        val cached = forecastCache[cacheKey]
        val now = System.currentTimeMillis()
        
        return if (cached != null && (now - cached.cachedAt) < cacheExpirationMs) {
            println("Cache hit for $days-day forecast in $city")
            cached.data
        } else {
            println("Cache miss for $days-day forecast in $city - fetching from service")
            val freshData = realService.getForecast(city, days)
            forecastCache[cacheKey] = CachedForecast(freshData, now, days)
            freshData
        }
    }
    
    fun clearCache() {
        weatherCache.clear()
        forecastCache.clear()
        println("Weather cache cleared")
    }
    
    fun getCacheStats(): String {
        return "Cache contains ${weatherCache.size} weather entries and ${forecastCache.size} forecast entries"
    }
}

// 4. Remote Proxy - Network Communication

// Subject interface
interface UserRepository {
    suspend fun getUserById(id: String): UserProfile?
    suspend fun createUser(profile: UserProfile): String
    suspend fun updateUser(profile: UserProfile): String
    suspend fun deleteUser(id: String): String
}

data class UserProfile(
    val id: String,
    val name: String,
    val email: String,
    val age: Int,
    val preferences: Map<String, String> = emptyMap()
)

// Real subject - would be on remote server
class LocalUserRepository : UserRepository {
    private val users = mutableMapOf<String, UserProfile>()
    
    override suspend fun getUserById(id: String): UserProfile? {
        delay(100) // Simulate database access
        return users[id]
    }
    
    override suspend fun createUser(profile: UserProfile): String {
        delay(200) // Simulate database write
        users[profile.id] = profile
        return "User ${profile.id} created successfully"
    }
    
    override suspend fun updateUser(profile: UserProfile): String {
        delay(150) // Simulate database update
        users[profile.id] = profile
        return "User ${profile.id} updated successfully"
    }
    
    override suspend fun deleteUser(id: String): String {
        delay(100) // Simulate database delete
        return if (users.remove(id) != null) {
            "User $id deleted successfully"
        } else {
            "User $id not found"
        }
    }
}

// Remote proxy with network simulation
class RemoteUserRepositoryProxy(
    private val serverUrl: String,
    private val timeout: Long = 5000
) : UserRepository {
    
    override suspend fun getUserById(id: String): UserProfile? {
        return try {
            simulateNetworkCall("GET", "$serverUrl/users/$id")
            // In real implementation, would make HTTP request
            // For demo, return mock data
            UserProfile(id, "User $id", "$id@example.com", 25)
        } catch (e: Exception) {
            println("Network error getting user $id: ${e.message}")
            null
        }
    }
    
    override suspend fun createUser(profile: UserProfile): String {
        return try {
            simulateNetworkCall("POST", "$serverUrl/users")
            "User ${profile.id} created successfully via remote call"
        } catch (e: Exception) {
            "Failed to create user: ${e.message}"
        }
    }
    
    override suspend fun updateUser(profile: UserProfile): String {
        return try {
            simulateNetworkCall("PUT", "$serverUrl/users/${profile.id}")
            "User ${profile.id} updated successfully via remote call"
        } catch (e: Exception) {
            "Failed to update user: ${e.message}"
        }
    }
    
    override suspend fun deleteUser(id: String): String {
        return try {
            simulateNetworkCall("DELETE", "$serverUrl/users/$id")
            "User $id deleted successfully via remote call"
        } catch (e: Exception) {
            "Failed to delete user: ${e.message}"
        }
    }
    
    private suspend fun simulateNetworkCall(method: String, url: String) {
        println("Making $method request to $url")
        delay(300) // Simulate network latency
        
        // Simulate occasional network failures
        if (Math.random() < 0.1) {
            throw Exception("Network timeout")
        }
    }
}

// 5. Smart Reference Proxy - Additional Functionality

// Subject interface
interface FileManager {
    fun readFile(filename: String): String
    fun writeFile(filename: String, content: String): String
    fun deleteFile(filename: String): String
}

// Real subject
class RealFileManager : FileManager {
    private val files = mutableMapOf<String, String>()
    
    override fun readFile(filename: String): String {
        return files[filename] ?: "File not found: $filename"
    }
    
    override fun writeFile(filename: String, content: String): String {
        files[filename] = content
        return "File $filename written successfully"
    }
    
    override fun deleteFile(filename: String): String {
        return if (files.remove(filename) != null) {
            "File $filename deleted successfully"
        } else {
            "File not found: $filename"
        }
    }
}

// Smart reference proxy with logging, monitoring, and validation
class SmartFileManagerProxy(
    private val realManager: FileManager
) : FileManager {
    
    private val accessLog = mutableListOf<AccessLogEntry>()
    private val referenceCount = mutableMapOf<String, Int>()
    
    data class AccessLogEntry(
        val operation: String,
        val filename: String,
        val timestamp: Long,
        val success: Boolean,
        val errorMessage: String? = null
    )
    
    override fun readFile(filename: String): String {
        val startTime = System.currentTimeMillis()
        return try {
            validateFilename(filename)
            incrementReferenceCount(filename)
            
            val result = realManager.readFile(filename)
            logAccess("READ", filename, true)
            logPerformance("READ", filename, System.currentTimeMillis() - startTime)
            
            result
        } catch (e: Exception) {
            logAccess("READ", filename, false, e.message)
            "Error reading file: ${e.message}"
        }
    }
    
    override fun writeFile(filename: String, content: String): String {
        val startTime = System.currentTimeMillis()
        return try {
            validateFilename(filename)
            validateContent(content)
            
            val result = realManager.writeFile(filename, content)
            logAccess("WRITE", filename, true)
            logPerformance("WRITE", filename, System.currentTimeMillis() - startTime)
            
            result
        } catch (e: Exception) {
            logAccess("WRITE", filename, false, e.message)
            "Error writing file: ${e.message}"
        }
    }
    
    override fun deleteFile(filename: String): String {
        val startTime = System.currentTimeMillis()
        return try {
            validateFilename(filename)
            decrementReferenceCount(filename)
            
            val result = realManager.deleteFile(filename)
            logAccess("DELETE", filename, true)
            logPerformance("DELETE", filename, System.currentTimeMillis() - startTime)
            
            result
        } catch (e: Exception) {
            logAccess("DELETE", filename, false, e.message)
            "Error deleting file: ${e.message}"
        }
    }
    
    private fun validateFilename(filename: String) {
        if (filename.isBlank()) {
            throw IllegalArgumentException("Filename cannot be blank")
        }
        if (filename.contains("..")) {
            throw IllegalArgumentException("Filename cannot contain '..'")
        }
        if (filename.length > 255) {
            throw IllegalArgumentException("Filename too long")
        }
    }
    
    private fun validateContent(content: String) {
        if (content.length > 1_000_000) {
            throw IllegalArgumentException("Content too large (max 1MB)")
        }
    }
    
    private fun incrementReferenceCount(filename: String) {
        referenceCount[filename] = (referenceCount[filename] ?: 0) + 1
    }
    
    private fun decrementReferenceCount(filename: String) {
        val count = referenceCount[filename] ?: 0
        if (count > 1) {
            referenceCount[filename] = count - 1
        } else {
            referenceCount.remove(filename)
        }
    }
    
    private fun logAccess(operation: String, filename: String, success: Boolean, error: String? = null) {
        accessLog.add(AccessLogEntry(operation, filename, System.currentTimeMillis(), success, error))
    }
    
    private fun logPerformance(operation: String, filename: String, durationMs: Long) {
        if (durationMs > 100) {
            println("SLOW OPERATION: $operation on $filename took ${durationMs}ms")
        }
    }
    
    fun getAccessLog(): List<AccessLogEntry> = accessLog.toList()
    
    fun getReferenceCount(filename: String): Int = referenceCount[filename] ?: 0
    
    fun getStatistics(): String {
        val totalOperations = accessLog.size
        val successfulOperations = accessLog.count { it.success }
        val failedOperations = totalOperations - successfulOperations
        val uniqueFiles = accessLog.map { it.filename }.toSet().size
        
        return """
            File Manager Statistics:
            - Total operations: $totalOperations
            - Successful: $successfulOperations
            - Failed: $failedOperations
            - Unique files accessed: $uniqueFiles
            - Files with active references: ${referenceCount.size}
        """.trimIndent()
    }
}

// 6. Kotlin Delegation-based Proxy

// Using Kotlin's delegation feature for proxy pattern
class LoggingProxy<T>(
    private val target: T,
    private val logger: (String) -> Unit = ::println
) {
    
    internal inline fun <R> execute(operationName: String, operation: () -> R): R {
        logger("Starting operation: $operationName")
        val startTime = System.currentTimeMillis()
        
        return try {
            val result = operation()
            val duration = System.currentTimeMillis() - startTime
            logger("Completed operation: $operationName in ${duration}ms")
            result
        } catch (e: Exception) {
            val duration = System.currentTimeMillis() - startTime
            logger("Failed operation: $operationName after ${duration}ms - ${e.message}")
            throw e
        }
    }
}

// Example usage with delegation
class Calculator {
    fun add(a: Int, b: Int): Int = a + b
    fun multiply(a: Int, b: Int): Int = a * b
    fun divide(a: Int, b: Int): Double {
        if (b == 0) throw IllegalArgumentException("Division by zero")
        return a.toDouble() / b
    }
}

class LoggingCalculatorProxy(private val calculator: Calculator) {
    private val proxy = LoggingProxy(calculator)
    
    fun add(a: Int, b: Int): Int = proxy.execute("add($a, $b)") {
        calculator.add(a, b)
    }
    
    fun multiply(a: Int, b: Int): Int = proxy.execute("multiply($a, $b)") {
        calculator.multiply(a, b)
    }
    
    fun divide(a: Int, b: Int): Double = proxy.execute("divide($a, $b)") {
        calculator.divide(a, b)
    }
}