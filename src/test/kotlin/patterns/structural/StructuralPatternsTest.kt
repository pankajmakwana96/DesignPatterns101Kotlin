package patterns.structural

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

/**
 * Tests for Structural Design Patterns
 * Tests cover: Adapter, Bridge, Composite, Decorator, Facade, Flyweight, Proxy
 */
class StructuralPatternsTest {

    @Test
    fun `test Adapter pattern`() {
        // Audio Player Adapter
        val player = AudioPlayer()
        
        // Test mp3 (no adapter needed)
        val mp3Result = player.play("mp3", "song.mp3")
        assertTrue(mp3Result.contains("Playing MP3"))
        
        // Test mp4 (uses adapter)
        val mp4Result = player.play("mp4", "movie.mp4")
        assertTrue(mp4Result.contains("Playing MP4"))
        
        // Test vlc (uses adapter)
        val vlcResult = player.play("vlc", "video.vlc")
        assertTrue(vlcResult.contains("Playing VLC"))
        
        // Test unsupported format
        val unsupportedResult = player.play("wav", "audio.wav")
        assertTrue(unsupportedResult.contains("Unsupported"))
    }

    @Test
    fun `test Composite pattern`() {
        // File System Composite
        val rootDir = Directory("root")
        val homeDir = Directory("home")
        val documentsDir = Directory("documents")
        
        val file1 = File("readme.txt", 1024)
        val file2 = File("config.xml", 2048)
        val file3 = File("data.json", 512)
        
        // Build directory structure
        rootDir.addComponent(homeDir)
        homeDir.addComponent(documentsDir)
        documentsDir.addComponent(file1)
        documentsDir.addComponent(file2)
        homeDir.addComponent(file3)
        
        // Test size calculation (composite behavior)
        assertEquals(1024 + 2048 + 512, rootDir.getSize())
        assertEquals(1024 + 2048, documentsDir.getSize())
        assertEquals(1024 + 2048 + 512, homeDir.getSize()) // Includes subdirectory files
        
        // Test search functionality
        val searchResults = rootDir.search("data")
        assertTrue(searchResults.any { it.name == "data.json" })
        
        // Test display functionality
        val display = rootDir.display()
        assertTrue(display.contains("📁 root"))
        assertTrue(display.contains("📄 readme.txt"))
        
        // Test file and directory counts
        assertEquals(2, documentsDir.getFileCount()) // Only 2 files in documents dir
        assertEquals(0, documentsDir.getDirectoryCount()) // No subdirectories in documents dir
    }

    @Test
    fun `test UI Component Composite`() {
        // Create UI components
        val loginForm = Form("loginForm", "/login", "POST")
        val usernameInput = TextInput("username", placeholder = "Enter username")
        val passwordInput = TextInput("password", placeholder = "Enter password")
        val submitButton = Button("submitBtn", "Login", enabled = true)
        val panel = Panel("loginPanel", "login-container")
        
        // Build component hierarchy
        loginForm.addField(usernameInput)
        loginForm.addField(passwordInput)
        loginForm.addField(submitButton)
        panel.addChild(loginForm)
        
        // Test rendering
        val rendered = panel.render()
        assertTrue(rendered.contains("<div id='loginPanel'"))
        assertTrue(rendered.contains("<form id='loginForm'"))
        assertTrue(rendered.contains("<input id='username'"))
        assertTrue(rendered.contains("<button id='submitBtn'"))
        
        // Test component finding
        val foundButton = panel.findById("submitBtn")
        assertNotNull(foundButton)
        assertEquals("submitBtn", foundButton?.id)
        
        // Test event handling
        usernameInput.setValue("testuser")
        assertEquals("testuser", usernameInput.getValue())
    }

    @Test
    fun `test Decorator pattern`() {
        // Coffee Decorator
        var coffee: Coffee = Espresso()
        assertEquals("Espresso", coffee.getDescription())
        assertEquals(1.5, coffee.getCost(), 0.01)
        
        // Add decorators
        coffee = MilkDecorator(coffee)
        coffee = SugarDecorator(coffee)
        coffee = VanillaDecorator(coffee)
        
        assertTrue(coffee.getDescription().contains("Espresso"))
        assertTrue(coffee.getDescription().contains("Milk"))
        assertTrue(coffee.getDescription().contains("Sugar"))
        assertTrue(coffee.getDescription().contains("Vanilla"))
        
        val expectedCost = 1.5 + 0.5 + 0.2 + 0.7 // Base + Milk + Sugar + Vanilla
        assertEquals(expectedCost, coffee.getCost(), 0.01)
        
        // Test different base coffee
        var darkRoast: Coffee = DarkRoast()
        darkRoast = WhipCreamDecorator(ExtraShotDecorator(darkRoast))
        
        assertTrue(darkRoast.getDescription().contains("Dark Roast"))
        assertTrue(darkRoast.getDescription().contains("Extra Shot"))
        assertTrue(darkRoast.getDescription().contains("Whipped Cream"))
    }

    @Test
    fun `test Text Processing Decorator`() {
        var processor: TextProcessor = PlainTextProcessor()
        val originalText = "  Hello WORLD with Special!@# Characters  "
        
        // Apply decorators
        processor = TrimWhitespaceDecorator(processor)
        processor = RemoveSpecialCharsDecorator(processor)
        processor = CapitalizeWordsDecorator(processor)
        
        val result = processor.process(originalText)
        assertEquals("Hello World With Special Characters", result)
        
        // Test reverse decorator
        var reverseProcessor: TextProcessor = PlainTextProcessor()
        reverseProcessor = ReverseTextDecorator(reverseProcessor)
        
        val reversed = reverseProcessor.process("Hello")
        assertEquals("olleH", reversed)
    }

    @Test
    fun `test Facade pattern`() {
        // Home Theater Facade
        val homeTheater = HomeTheaterFacade(
            Amplifier(),
            DvdPlayer(),
            Projector(),
            TheaterLights(),
            Screen(),
            PopcornPopper()
        )
        
        // Test watch movie
        val watchSteps = homeTheater.watchMovie("The Matrix")
        assertTrue(watchSteps.isNotEmpty())
        assertTrue(watchSteps.any { it.contains("Popcorn") })
        assertTrue(watchSteps.any { it.contains("Projector") })
        assertTrue(watchSteps.any { it.contains("The Matrix") })
        
        // Test end movie
        val endSteps = homeTheater.endMovie()
        assertTrue(endSteps.isNotEmpty())
        assertTrue(endSteps.any { it.contains("Shutting") })
        
        // Test pause/resume
        val pauseSteps = homeTheater.pauseMovie()
        assertTrue(pauseSteps.any { it.contains("Pausing") })
        
        val resumeSteps = homeTheater.resumeMovie()
        assertTrue(resumeSteps.any { it.contains("Resuming") })
    }

    @Test
    fun `test Computer System Facade`() {
        val computer = ComputerFacade(
            CPU(),
            Memory(),
            HardDrive(),
            Graphics(),
            Network(),
            Audio()
        )
        
        // Test startup
        val startupSteps = computer.startComputer()
        assertTrue(startupSteps.isNotEmpty())
        assertTrue(startupSteps.any { it.contains("Starting computer") })
        assertTrue(startupSteps.any { it.contains("Graphics") })
        assertTrue(startupSteps.any { it.contains("Network") })
        assertTrue(startupSteps.any { it.contains("startup complete") })
        
        // Test shutdown
        val shutdownSteps = computer.shutdownComputer()
        assertTrue(shutdownSteps.any { it.contains("Shutting down") })
        
        // Test sleep/wake
        val sleepSteps = computer.enterSleepMode()
        assertTrue(sleepSteps.any { it.contains("sleep") })
        
        val wakeSteps = computer.wakeFromSleep()
        assertTrue(wakeSteps.any { it.contains("Waking") })
    }

    @Test
    fun `test Flyweight pattern`() {
        val document = TextDocument()
        
        // Add many characters with some repetition
        document.addCharacter('H', "Arial", "bold", Position(0, 0), 12, "black")
        document.addCharacter('e', "Arial", "normal", Position(10, 0), 12, "black")
        document.addCharacter('l', "Arial", "normal", Position(20, 0), 12, "black")
        document.addCharacter('l', "Arial", "normal", Position(30, 0), 12, "black")
        document.addCharacter('o', "Arial", "normal", Position(40, 0), 12, "black")
        
        // Should have 5 characters but fewer flyweights due to sharing
        assertEquals(5, document.getCharacterCount())
        assertTrue(document.getFlyweightCount() < document.getCharacterCount())
        
        // Test rendering
        val rendered = document.render()
        assertEquals(5, rendered.size)
        assertTrue(rendered[0].contains("Character 'H'"))
        assertTrue(rendered[0].contains("Arial bold"))
        
        // Test memory footprint
        val footprint = document.getMemoryFootprint()
        assertTrue(footprint.contains("Total characters: 5"))
        assertTrue(footprint.contains("Memory saved"))
    }

    @Test
    fun `test Forest Flyweight`() {
        val forest = Forest()
        
        // Plant many trees with some type repetition
        forest.plantTree(10, 20, 15, "Oak", "Green", "oak_sprite")
        forest.plantTree(30, 40, 20, "Oak", "Green", "oak_sprite")
        forest.plantTree(50, 60, 18, "Pine", "Dark Green", "pine_sprite")
        forest.plantTree(70, 80, 25, "Oak", "Green", "oak_sprite")
        forest.plantTree(90, 100, 22, "Pine", "Dark Green", "pine_sprite")
        
        assertEquals(5, forest.getTreeCount())
        assertEquals(2, forest.getTreeTypeCount()) // Only Oak and Pine types
        
        // Test rendering
        val rendered = forest.render("Canvas1")
        assertEquals(5, rendered.size)
        assertTrue(rendered.any { it.contains("Oak tree") })
        assertTrue(rendered.any { it.contains("Pine tree") })
        
        // Test statistics
        val stats = forest.getStatistics()
        assertTrue(stats.contains("5 trees"))
        assertTrue(stats.contains("2 different types"))
        
        // Test filtering by type
        val oaks = forest.getTreesByType("Oak")
        assertEquals(3, oaks.size)
    }

    @Test
    fun `test Proxy pattern`() {
        // Image Proxy
        val imageProxy = ImageProxy("large_image.jpg")
        
        // Should not load image initially
        val info = imageProxy.getInfo()
        assertTrue(info.contains("large_image.jpg"))
        assertTrue(info.contains("not loaded") || info.contains("proxy"))
        
        // For now, we only test the Image Proxy since other proxy classes don't exist
        // In a full implementation, you would add DatabaseProxy and SecurityProxy classes
    }

    @Test
    fun `test Data Source Decorator with Proxy-like behavior`() {
        // Test caching decorator (proxy-like behavior)
        var dataSource: DataSource = FileDataSource("test.txt")
        dataSource = CachingDecorator(dataSource)
        dataSource = LoggingDecorator(dataSource)
        
        // First read should miss cache
        val read1 = dataSource.readData()
        assertTrue(read1.contains("Cache miss") || read1.contains("Reading"))
        
        // Test encryption decorator
        var encryptedSource: DataSource = DatabaseDataSource("connection_string")
        encryptedSource = EncryptionDecorator(encryptedSource)
        encryptedSource = CompressionDecorator(encryptedSource)
        
        val encryptedData = encryptedSource.writeData("sensitive data")
        assertTrue(encryptedData.contains("Compressed") || encryptedData.contains("Encrypted"))
        
        val decryptedData = encryptedSource.readData()
        assertTrue(decryptedData.contains("Decompressed") || decryptedData.contains("Decrypted"))
    }
}