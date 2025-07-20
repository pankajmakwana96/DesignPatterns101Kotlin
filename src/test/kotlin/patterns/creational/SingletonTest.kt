package patterns.creational

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Assertions.*
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.launch
import kotlinx.coroutines.async

class SingletonTest {

    @BeforeEach
    fun setUp() {
        // Clear logger state before each test
        Logger.getInstance().clearLogs()
    }

    @Test
    fun `test Logger singleton returns same instance`() {
        val logger1 = Logger.getInstance()
        val logger2 = Logger.getInstance()
        
        assertSame(logger1, logger2, "Logger should return the same instance")
    }

    @Test
    fun `test Logger maintains state across instances`() {
        val logger1 = Logger.getInstance()
        logger1.log("Test message 1")
        
        val logger2 = Logger.getInstance()
        logger2.log("Test message 2")
        
        assertEquals(2, logger1.getLogs().size)
        assertEquals(2, logger2.getLogs().size)
        assertEquals(logger1.getLogs(), logger2.getLogs())
    }

    @Test
    fun `test DatabaseConnection object declaration singleton`() {
        val connection1 = DatabaseConnection
        val connection2 = DatabaseConnection
        
        assertSame(connection1, connection2, "Object declaration should be singleton")
        
        val result1 = connection1.connect()
        val result2 = connection2.connect()
        
        assertTrue(result1.contains("connection #"))
        assertTrue(result2.contains("connection #"))
        
        // Should share connection count
        assertEquals(2, connection1.getConnectionCount())
        assertEquals(2, connection2.getConnectionCount())
    }

    @Test
    fun `test ConfigurationManager lazy delegate singleton`() {
        val config1 = ConfigurationManager.instance
        val config2 = ConfigurationManager.instance
        
        assertSame(config1, config2, "Lazy delegate should return same instance")
        
        config1.setProperty("test.key", "test.value")
        assertEquals("test.value", config2.getProperty("test.key"))
    }

    @Test
    fun `test ApplicationState enum singleton`() {
        val state1 = ApplicationState.INSTANCE
        val state2 = ApplicationState.INSTANCE
        
        assertSame(state1, state2, "Enum singleton should return same instance")
        
        state1.setState("RUNNING")
        assertEquals("RUNNING", state2.getState())
    }

    @Test
    fun `test AsyncCacheManager coroutine-safe singleton`() = runBlocking {
        val cache1 = AsyncCacheManager.getInstance()
        val cache2 = AsyncCacheManager.getInstance()
        
        assertSame(cache1, cache2, "Async singleton should return same instance")
        
        cache1.put("key1", "value1")
        assertEquals("value1", cache2.get("key1"))
    }

    @Test
    fun `test ServiceRegistry pattern`() {
        // Clear registry before test
        ServiceRegistry.clear()
        
        val emailService = EmailService()
        ServiceRegistry.register(emailService)
        
        val retrievedService = ServiceRegistry.get<EmailService>()
        assertSame(emailService, retrievedService, "Registry should return same instance")
        
        val result = retrievedService?.sendEmail("test@example.com", "Test", "Body")
        assertTrue(result?.contains("Email sent") == true)
    }

    @Test
    fun `test DatabasePool singleton with parameters`() {
        val pool1 = DatabasePool.getInstance(5)
        val pool2 = DatabasePool.getInstance(10) // Should ignore new parameter
        
        assertSame(pool1, pool2, "Should return same instance regardless of parameters")
        
        val connection1 = pool1.getConnection()
        val connection2 = pool2.getConnection()
        
        assertNotNull(connection1)
        assertNotNull(connection2)
        assertEquals(2, pool1.getActiveConnections())
        assertEquals(2, pool2.getActiveConnections())
    }

    @Test
    fun `test thread safety of Logger singleton`() = runBlocking {
        val jobs = (1..100).map { i ->
            async {
                val logger = Logger.getInstance()
                logger.log("Message from coroutine $i")
                logger
            }
        }
        
        val loggers = jobs.map { it.await() }
        
        // All should be the same instance
        val firstLogger = loggers.first()
        assertTrue(loggers.all { it === firstLogger }, "All instances should be the same")
        
        // Should have 100 log messages
        assertEquals(100, firstLogger.getLogs().size)
    }

    @Test
    fun `test concurrent access to AsyncCacheManager`() = runBlocking {
        val cache = AsyncCacheManager.getInstance()
        
        val jobs = (1..50).map { i ->
            launch {
                cache.put("key$i", "value$i")
            }
        }
        
        jobs.forEach { it.join() }
        
        assertEquals(50, cache.size())
        
        // Verify all values are present
        repeat(50) { i ->
            assertEquals("value${i+1}", cache.get("key${i+1}"))
        }
    }

    @Test
    fun `test memory efficiency of singleton pattern`() {
        // Create multiple references to singleton
        val instances = (1..1000).map { Logger.getInstance() }
        
        // All should be the same instance (memory efficient)
        val firstInstance = instances.first()
        assertTrue(instances.all { it === firstInstance })
        
        // Should use minimal memory (only one instance)
        val uniqueInstances = instances.toSet()
        assertEquals(1, uniqueInstances.size, "Should only have one unique instance")
    }
}