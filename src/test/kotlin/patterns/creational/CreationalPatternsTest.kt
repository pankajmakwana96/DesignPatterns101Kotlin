package patterns.creational

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

/**
 * Tests for Creational Design Patterns
 * Tests cover: Singleton, Builder, Factory Method, Abstract Factory, Prototype
 */
class CreationalPatternsTest {

    @Test
    fun `test Singleton pattern`() {
        // Database Connection Singleton
        val db1 = DatabaseConnection
        val db2 = DatabaseConnection
        assertSame(db1, db2, "Database connections should be the same instance")
        
        // Configuration Manager Singleton
        val config1 = ConfigurationManager.instance
        val config2 = ConfigurationManager.instance
        assertSame(config1, config2, "Config managers should be the same instance")
        
        // Test configuration functionality
        config1.setProperty("test", "value")
        assertEquals("value", config2.getProperty("test"), "Properties should be shared")
    }

    @Test
    fun `test Builder pattern`() {
        // Computer Builder
        val computer = ComputerBuilder()
            .cpu("Intel i7")
            .ram("16GB")
            .storage("512GB SSD")
            .gpu("RTX 4070")
            .gamingPC()
            .build()
        
        assertEquals("Intel i7", computer.cpu)
        assertEquals("16GB", computer.ram)
        assertEquals("512GB SSD", computer.storage)
        assertEquals("RTX 4070", computer.gpu)
        assertTrue(computer.isGamingPC)
        
        // HTTP Request Builder
        val request = HttpRequestBuilder()
            .url("https://api.example.com")
            .method("POST")
            .header("Content-Type", "application/json")
            .body("""{"key": "value"}""")
            .build()
        
        assertEquals("https://api.example.com", request.url)
        assertEquals("POST", request.method)
        assertTrue(request.headers.containsKey("Content-Type"))
        assertEquals("""{"key": "value"}""", request.body)
    }

    @Test
    fun `test Factory Method pattern`() {
        // Vehicle Factory
        val carFactory = CarFactory()
        val car = carFactory.createVehicle("BMW")
        assertEquals("Car", car.getType())
        assertTrue(car.start().contains("BMW"))
        assertEquals(200, car.getMaxSpeed())
        
        val motorcycleFactory = MotorcycleFactory()
        val motorcycle = motorcycleFactory.createVehicle("Harley")
        assertEquals("Motorcycle", motorcycle.getType())
        assertTrue(motorcycle.start().contains("Harley"))
        assertEquals(300, motorcycle.getMaxSpeed())
        
        // Document Factory
        val pdfDoc = DocumentFactory.createDocument(DocumentType.PDF)
        assertEquals("PDF", pdfDoc.getFormat())
        assertTrue(pdfDoc.open().contains("PDF"))
        
        val wordDoc = DocumentFactory.createDocument(DocumentType.Word)
        assertEquals("DOCX", wordDoc.getFormat())
        assertTrue(wordDoc.open().contains("Word"))
    }

    @Test
    fun `test Abstract Factory pattern`() {
        // Windows GUI Factory
        val windowsFactory = WindowsFactory()
        val windowsButton = windowsFactory.createButton()
        val windowsCheckbox = windowsFactory.createCheckbox()
        
        assertTrue(windowsButton.render().contains("Windows"))
        assertTrue(windowsCheckbox.render().contains("Windows"))
        
        // Mac GUI Factory
        val macFactory = MacFactory()
        val macButton = macFactory.createButton()
        val macCheckbox = macFactory.createCheckbox()
        
        assertTrue(macButton.render().contains("macOS"))
        assertTrue(macCheckbox.render().contains("macOS"))
        
        // Linux GUI Factory
        val linuxFactory = LinuxFactory()
        val linuxButton = linuxFactory.createButton()
        val linuxCheckbox = linuxFactory.createCheckbox()
        
        assertTrue(linuxButton.render().contains("Linux"))
        assertTrue(linuxCheckbox.render().contains("Linux"))
    }

    @Test
    fun `test Prototype pattern`() {
        // Basic Document prototype
        val originalDoc = BasicDocument(
            title = "Original",
            content = "Test content",
            author = "Test Author",
            createdAt = System.currentTimeMillis()
        )
        
        val copiedDoc = originalDoc.createCopy(title = "Copied")
        
        assertNotSame(originalDoc, copiedDoc, "Documents should be different instances")
        assertEquals("Copied", copiedDoc.title)
        assertEquals(originalDoc.content, copiedDoc.content)
        assertEquals(originalDoc.author, copiedDoc.author)
        
        // Shape prototype
        val originalCircle = Circle(10, 20, "red", 5.0)
        val clonedCircle = originalCircle.clone()
        
        assertNotSame(originalCircle, clonedCircle, "Circles should be different instances")
        assertEquals(originalCircle.x, clonedCircle.x)
        assertEquals(originalCircle.y, clonedCircle.y)
        assertEquals(originalCircle.color, clonedCircle.color)
        assertEquals(originalCircle.radius, clonedCircle.radius)
        
        // Shape Prototype Manager - Test that it has predefined prototypes
        val manager = ShapePrototypeManager()
        val prototypes = manager.listPrototypes()
        assertTrue(prototypes.isNotEmpty())
        assertTrue(prototypes.contains("small-red-circle"))
        assertTrue(prototypes.contains("medium-blue-circle"))
        
        // Person deep cloning
        val address = Address("123 Main St", "City", "12345", "Country")
        val contact = Contact("test@example.com", "123-456-7890", address)
        val person = Person(
            "John", "Doe", 30, contact,
            mutableListOf("reading", "coding"),
            mutableMapOf("role" to "developer")
        )
        
        val clonedPerson = person.clone()
        assertNotSame(person, clonedPerson, "Persons should be different instances")
        assertEquals(person.firstName, clonedPerson.firstName)
        assertEquals(person.contact.email, clonedPerson.contact.email)
        
        // Verify deep cloning - modifying cloned person shouldn't affect original
        clonedPerson.hobbies.add("swimming")
        assertEquals(2, person.hobbies.size, "Original person hobbies should not be affected")
        assertEquals(3, clonedPerson.hobbies.size, "Cloned person should have new hobby")
    }

    @Test
    fun `test Game Entity prototypes`() {
        val registry = EntityPrototypeRegistry()
        
        // Create warrior from prototype
        val warrior = registry.createEntity("basic-warrior") as? Warrior
        assertNotNull(warrior)
        assertEquals(100, warrior?.health)
        assertEquals(100, warrior?.maxHealth)
        assertEquals("Iron Sword", warrior?.weapon)
        assertEquals(25, warrior?.attackPower)
        
        // Create mage from prototype
        val mage = registry.createEntity("basic-mage") as? Mage
        assertNotNull(mage)
        assertEquals(80, mage?.health)
        assertEquals(100, mage?.mana)
        assertTrue(mage?.spells?.contains("Fireball") == true)
        
        // Test that cloned entities have different IDs
        // Add a small delay to ensure different timestamps
        Thread.sleep(1)
        val warrior2 = registry.createEntity("basic-warrior") as? Warrior
        assertNotSame(warrior, warrior2, "Cloned warriors should be different instances")
        assertNotEquals(warrior?.id, warrior2?.id, "Cloned warriors should have different IDs")
    }

    @Test
    fun `test Configuration prototypes`() {
        // Test environment-specific configurations
        val devConfig = ConfigPrototypeManager.getConfigForEnvironment("development")
        assertEquals("development", devConfig.environment)
        assertTrue(devConfig.debugMode)
        assertEquals("DEBUG", devConfig.logLevel)
        assertEquals("localhost", devConfig.databaseConfig.host)
        
        val prodConfig = ConfigPrototypeManager.getConfigForEnvironment("production")
        assertEquals("production", prodConfig.environment)
        assertFalse(prodConfig.debugMode)
        assertEquals("WARN", prodConfig.logLevel)
        assertEquals("prod-db.company.com", prodConfig.databaseConfig.host)
        assertTrue(prodConfig.serverConfig.enableSsl)
        
        // Verify configurations are independent
        assertNotSame(devConfig, prodConfig, "Configurations should be different instances")
    }
}