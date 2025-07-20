package patterns.structural

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class AdapterTest {

    @Test
    fun `test AudioPlayer with MP3 format`() {
        val player = AudioPlayer()
        val result = player.play("mp3", "song.mp3")
        
        assertEquals("Playing MP3 file: song.mp3", result)
    }

    @Test
    fun `test AudioPlayer with VLC format using adapter`() {
        val player = AudioPlayer()
        val result = player.play("vlc", "movie.vlc")
        
        assertEquals("Playing VLC file: movie.vlc", result)
    }

    @Test
    fun `test AudioPlayer with MP4 format using adapter`() {
        val player = AudioPlayer()
        val result = player.play("mp4", "video.mp4")
        
        assertEquals("Playing MP4 file: video.mp4", result)
    }

    @Test
    fun `test AudioPlayer with unsupported format`() {
        val player = AudioPlayer()
        val result = player.play("wav", "sound.wav")
        
        assertTrue(result.contains("Unsupported audio format"))
    }

    @Test
    fun `test MediaAdapter with VLC player`() {
        val adapter = MediaAdapter("vlc")
        val result = adapter.play("vlc", "test.vlc")
        
        assertEquals("Playing VLC file: test.vlc", result)
    }

    @Test
    fun `test MediaAdapter with MP4 player`() {
        val adapter = MediaAdapter("mp4")
        val result = adapter.play("mp4", "test.mp4")
        
        assertEquals("Playing MP4 file: test.mp4", result)
    }

    @Test
    fun `test MediaAdapter with wrong format`() {
        val adapter = MediaAdapter("vlc")
        val result = adapter.play("mp4", "test.mp4")
        
        assertEquals("VLC player cannot play MP4 files", result)
    }

    @Test
    fun `test database adapters`() {
        val mysqlLib = MySQLLibrary()
        val postgresLib = PostgreSQLLibrary()
        
        val mysqlAdapter = MySQLAdapter(mysqlLib)
        val postgresAdapter = PostgreSQLAdapter(postgresLib)
        
        // Test MySQL adapter
        val mysqlConnect = mysqlAdapter.connect()
        assertEquals("MySQL connection established", mysqlConnect)
        
        val mysqlQuery = mysqlAdapter.executeQuery("SELECT * FROM users")
        assertEquals("MySQL query executed: SELECT * FROM users", mysqlQuery)
        
        // Test PostgreSQL adapter
        val postgresConnect = postgresAdapter.connect()
        assertEquals("PostgreSQL connection created", postgresConnect)
        
        val postgresQuery = postgresAdapter.executeQuery("SELECT * FROM users")
        assertEquals("PostgreSQL query performed: SELECT * FROM users", postgresQuery)
    }

    @Test
    fun `test extension function adapter`() {
        val legacyProcessor = LegacyPaymentProcessor()
        val modernProcessor = legacyProcessor.toModernPayment()
        
        val paymentDetails = PaymentDetails(
            cardNumber = "1234567890123456",
            cvv = "123",
            expiryMonth = 12,
            expiryYear = 2025,
            holderName = "John Doe"
        )
        
        val result = modernProcessor.pay(100.0, paymentDetails)
        assertTrue(result.contains("Processing"))
        assertTrue(result.contains("100.0"))
    }

    @Test
    fun `test data source adapters`() {
        val jsonProvider = JsonDataProvider()
        val xmlProvider = XmlDataProvider()
        val csvProvider = CsvDataProvider()
        
        val jsonAdapter = DataSourceFactory.createJsonAdapter(jsonProvider)
        val xmlAdapter = DataSourceFactory.createXmlAdapter(xmlProvider)
        val csvAdapter = DataSourceFactory.createCsvAdapter(csvProvider)
        
        // Test JSON adapter
        val jsonUsers = jsonAdapter.getData()
        assertEquals(2, jsonUsers.size)
        
        val jsonSaveResult = jsonAdapter.saveData(jsonUsers)
        assertTrue(jsonSaveResult.contains("JSON data saved"))
        
        // Test XML adapter
        val xmlUsers = xmlAdapter.getData()
        assertEquals(2, xmlUsers.size)
        
        // Test CSV adapter
        val csvUsers = csvAdapter.getData()
        assertEquals(2, csvUsers.size)
    }

    @Test
    fun `test two-way calculator adapter`() {
        val adapter = CalculatorAdapter()
        
        // Test old interface methods
        assertEquals(5, adapter.add(2, 3))
        assertEquals(1, adapter.subtract(3, 2))
        
        // Test new interface methods
        assertEquals(5.0, adapter.calculate("add", listOf(2.0, 3.0)))
        assertEquals(1.0, adapter.calculate("subtract", listOf(3.0, 2.0)))
        assertEquals(6.0, adapter.calculate("multiply", listOf(2.0, 3.0)))
        assertEquals(2.0, adapter.calculate("divide", listOf(6.0, 3.0)))
    }

    @Test
    fun `test configuration adapters`() {
        val envVars = EnvironmentVariables()
        val propertyFile = PropertyFile(mapOf("app.name" to "TestApp", "app.version" to "1.0"))
        val dbConfig = DatabaseConfig(mapOf("db.host" to "localhost", "db.port" to "5432"))
        
        val envAdapter = EnvironmentAdapter(envVars)
        val propAdapter = PropertyFileAdapter(propertyFile)
        val dbAdapter = DatabaseConfigAdapter(dbConfig)
        
        // Test property file adapter
        assertEquals("TestApp", propAdapter.getProperty("app.name"))
        assertEquals("1.0", propAdapter.getProperty("app.version"))
        
        // Test database config adapter
        assertEquals("localhost", dbAdapter.getProperty("db.host"))
        assertEquals("5432", dbAdapter.getProperty("db.port"))
        
        // Test composite adapter with priority
        val composite = CompositeConfigurationAdapter(
            listOf(envAdapter, propAdapter, dbAdapter)
        )
        
        // Should get from property file (higher priority)
        assertEquals("TestApp", composite.getProperty("app.name"))
        
        // Should get from database config
        assertEquals("localhost", composite.getProperty("db.host"))
        
        // Should return null for non-existent property
        assertNull(composite.getProperty("non.existent"))
    }

    @Test
    fun `test adapter pattern error handling`() {
        // Test with invalid audio type in MediaAdapter
        assertThrows(IllegalArgumentException::class.java) {
            MediaAdapter("invalid")
        }
        
        // Test calculator adapter with invalid operation
        val adapter = CalculatorAdapter()
        assertThrows(IllegalArgumentException::class.java) {
            adapter.calculate("invalid", listOf(1.0, 2.0))
        }
        
        // Test calculator adapter with wrong number of operands
        assertThrows(IllegalArgumentException::class.java) {
            adapter.calculate("add", listOf(1.0)) // Need 2 operands
        }
    }

    @Test
    fun `test adapter performance characteristics`() {
        val player = AudioPlayer()
        
        // Measure direct MP3 playback
        val startTime = System.currentTimeMillis()
        repeat(1000) {
            player.play("mp3", "test.mp3")
        }
        val directTime = System.currentTimeMillis() - startTime
        
        // Measure adapted VLC playback
        val startTime2 = System.currentTimeMillis()
        repeat(1000) {
            player.play("vlc", "test.vlc")
        }
        val adaptedTime = System.currentTimeMillis() - startTime2
        
        // Adapter should have minimal overhead
        assertTrue(adaptedTime < directTime * 10, "Adapter overhead should be reasonable")
        
        println("Direct playback: ${directTime}ms, Adapted playback: ${adaptedTime}ms")
    }
}