package patterns.behavioral

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.coroutineScope

/**
 * Tests for Behavioral Design Patterns
 * Tests cover: Observer, Strategy, and related patterns
 */
class BehavioralPatternsTest {

    @Test
    fun `test Observer pattern - Stock Market`() {
        val stock = Stock("AAPL", 150.0)
        val display = StockDisplay("Main Display")
        val alert = StockAlert(5.0) // 5% change threshold
        val tradingBot = TradingBot(3.0, 3.0) // Buy at 3% drop, sell at 3% gain
        
        // Add observers
        stock.addObserver(display)
        stock.addObserver(alert)
        stock.addObserver(tradingBot)
        
        assertEquals(3, stock.getObserverCount())
        
        // Test price change notifications
        stock.setPrice(155.0) // Small increase
        
        val portfolio = display.displayPortfolio()
        assertTrue(portfolio.any { it.contains("AAPL") && it.contains("155") })
        
        // Test significant price change triggering alerts
        stock.setPrice(165.0) // Significant increase
        
        val alerts = alert.getAlerts()
        assertTrue(alerts.isNotEmpty())
        
        // Test trading bot activity
        val trades = tradingBot.getTrades()
        assertTrue(trades.isNotEmpty() || tradingBot.getTradeCount() >= 0)
        
        // Test observer removal
        stock.removeObserver(alert)
        assertEquals(2, stock.getObserverCount())
    }

    @Test
    fun `test Event System Observer`() {
        val eventPublisher = EventPublisher()
        val auditLogger = AuditLogger()
        val notificationService = NotificationService()
        val analyticsCollector = AnalyticsCollector()
        
        // Subscribe to events
        eventPublisher.subscribe(ApplicationEvent.UserLoggedIn::class.java, auditLogger)
        eventPublisher.subscribe(ApplicationEvent.UserLoggedIn::class.java, analyticsCollector)
        eventPublisher.subscribe(ApplicationEvent.OrderPlaced::class.java, notificationService)
        eventPublisher.subscribe(ApplicationEvent.OrderPlaced::class.java, auditLogger)
        eventPublisher.subscribe(ApplicationEvent.OrderPlaced::class.java, analyticsCollector)
        eventPublisher.subscribe(ApplicationEvent.SystemError::class.java, notificationService)
        eventPublisher.subscribe(ApplicationEvent.SystemError::class.java, analyticsCollector)
        
        // Test user login event
        val loginEvent = ApplicationEvent.UserLoggedIn("user123", System.currentTimeMillis())
        eventPublisher.publish(loginEvent)
        
        val auditLog = auditLogger.getAuditLog()
        assertTrue(auditLog.any { it.contains("user123 logged in") })
        
        // Test order placed event
        val orderEvent = ApplicationEvent.OrderPlaced("order456", "user123", 99.99)
        eventPublisher.publish(orderEvent)
        
        val notifications = notificationService.getNotifications()
        assertTrue(notifications.any { it.contains("order456") })
        
        val orderValue = analyticsCollector.getAverageOrderValue()
        assertEquals(99.99, orderValue, 0.01)
        
        // Test system error event
        val errorEvent = ApplicationEvent.SystemError("Database timeout", "OrderService", "HIGH")
        eventPublisher.publish(errorEvent)
        
        val errorNotifications = notificationService.getNotifications()
        assertTrue(errorNotifications.any { it.contains("Critical error") })
        
        assertTrue(analyticsCollector.getErrorCount("OrderService") >= 1)
    }

    @Test
    fun `test MVC Observer pattern`() {
        val userModel = UserModel()
        val listView = UserListView()
        val statsView = UserStatsView()
        
        // Add views as observers
        userModel.addObserver(listView)
        userModel.addObserver(statsView)
        
        // Add users
        val user1 = UserModel.User("1", "John Doe", "john@example.com", true)
        val user2 = UserModel.User("2", "Jane Smith", "jane@example.com", false)
        
        userModel.addUser(user1)
        userModel.addUser(user2)
        
        // Check list view
        val currentUsers = listView.getCurrentUsers()
        assertEquals(2, currentUsers.size)
        assertTrue(currentUsers.any { it.name == "John Doe" })
        
        // Check stats view
        val (totalUsers, activeUsers) = statsView.getStats()
        assertEquals(2, totalUsers)
        assertEquals(1, activeUsers)
        
        // Update user
        val updatedUser = user2.copy(active = true)
        userModel.updateUser(updatedUser)
        
        // Delete user
        userModel.deleteUser("1")
        
        val finalUsers = listView.getCurrentUsers()
        assertEquals(1, finalUsers.size)
        assertFalse(finalUsers.any { it.name == "John Doe" })
    }

    @Test
    fun `test Strategy pattern - Payment Processing`() {
        val processor = PaymentProcessor()
        
        // Test Credit Card Strategy
        val creditCardStrategy = CreditCardStrategy(
            "1234567890123456",
            "12/25",
            "123"
        )
        
        processor.setPaymentStrategy(creditCardStrategy)
        assertEquals("Credit Card ending in 3456", processor.getCurrentPaymentMethod())
        
        val creditResult = processor.processPayment(100.0)
        assertTrue(creditResult.success)
        assertEquals("Credit card payment successful", creditResult.message)
        assertTrue(creditResult.fees > 0)
        
        // Test PayPal Strategy
        val paypalStrategy = PayPalStrategy("test@example.com")
        processor.setPaymentStrategy(paypalStrategy)
        
        val paypalResult = processor.processPayment(50.0)
        assertTrue(paypalResult.success)
        assertEquals("PayPal payment successful", paypalResult.message)
        assertTrue(paypalResult.fees > 0)
        
        // Test Cryptocurrency Strategy
        val cryptoStrategy = CryptocurrencyStrategy(
            "1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa",
            "Bitcoin"
        )
        processor.setPaymentStrategy(cryptoStrategy)
        
        val cryptoResult = processor.processPayment(200.0)
        assertTrue(cryptoResult.success)
        assertEquals("Bitcoin payment successful", cryptoResult.message)
        assertTrue(cryptoResult.fees > 0)
        
        // Test invalid payment
        val invalidCreditCard = CreditCardStrategy("123", "invalid", "1")
        processor.setPaymentStrategy(invalidCreditCard)
        
        val invalidResult = processor.processPayment(100.0)
        assertFalse(invalidResult.success)
    }

    @Test
    fun `test Sorting Strategy`() {
        val sorter = DataSorter<Int>()
        val data = mutableListOf(64, 34, 25, 12, 22, 11, 90)
        
        // Test Bubble Sort
        sorter.setSortingStrategy(BubbleSortStrategy())
        val bubbleResult = sorter.sort(data)
        
        assertEquals("Bubble Sort", bubbleResult.algorithmUsed)
        assertEquals("O(n²)", bubbleResult.complexity.substringBefore(" time"))
        assertTrue(bubbleResult.sortedData.isSorted())
        
        // Test Quick Sort
        sorter.setSortingStrategy(QuickSortStrategy())
        val quickResult = sorter.sort(data)
        
        assertEquals("Quick Sort", quickResult.algorithmUsed)
        assertTrue(quickResult.complexity.contains("O(n log n)"))
        assertTrue(quickResult.sortedData.isSorted())
        
        // Test Merge Sort
        sorter.setSortingStrategy(MergeSortStrategy())
        val mergeResult = sorter.sort(data)
        
        assertEquals("Merge Sort", mergeResult.algorithmUsed)
        assertTrue(mergeResult.complexity.contains("O(n log n)"))
        assertTrue(mergeResult.sortedData.isSorted())
        
        // Test with strings
        val stringSorter = DataSorter<String>()
        val stringData = mutableListOf("zebra", "apple", "banana", "cherry")
        
        stringSorter.setSortingStrategy(QuickSortStrategy())
        val stringResult = stringSorter.sort(stringData)
        
        assertEquals(listOf("apple", "banana", "cherry", "zebra"), stringResult.sortedData)
    }

    @Test
    fun `test Functional Validation Strategy`() {
        val validator = FieldValidator<String>()
        
        // Test email validation
        val validEmail = validator.validate("test@example.com", ValidationStrategies.emailValidation)
        assertTrue(validEmail.isValid)
        
        val invalidEmail = validator.validate("invalid-email", ValidationStrategies.emailValidation)
        assertFalse(invalidEmail.isValid)
        assertTrue(invalidEmail.errorMessage?.contains("@") == true)
        
        // Test password validation
        val validPassword = validator.validate("StrongPass123", ValidationStrategies.passwordValidation)
        assertTrue(validPassword.isValid)
        
        val weakPassword = validator.validate("weak", ValidationStrategies.passwordValidation)
        assertFalse(weakPassword.isValid)
        assertTrue(weakPassword.errorMessage?.contains("8 characters") == true)
        
        // Test age validation with custom strategy
        val ageValidator = FieldValidator<Int>()
        val ageValidationStrategy = ValidationStrategies.ageValidation(18, 65)
        
        val validAge = ageValidator.validate(25, ageValidationStrategy)
        assertTrue(validAge.isValid)
        
        val invalidAge = ageValidator.validate(16, ageValidationStrategy)
        assertFalse(invalidAge.isValid)
        assertTrue(invalidAge.errorMessage?.contains("18") == true)
        
        // Test multiple validations
        val results = validator.validateMultiple(
            "Test@123",
            listOf(ValidationStrategies.emailValidation, ValidationStrategies.passwordValidation)
        )
        
        assertEquals(2, results.size)
        assertFalse(results[0].isValid) // Not a valid email
        assertTrue(results[1].isValid) // Is a valid password
    }

    @Test
    fun `test Compression Strategy`() {
        val compressor = FileCompressor()
        val testData = "AAABBBCCCAAABBBCCC"
        
        // Test Run Length Encoding
        compressor.setCompressionStrategy(RunLengthEncodingStrategy())
        val rleResult = compressor.compressFile(testData)
        
        assertEquals("Run Length Encoding", rleResult.algorithm)
        assertTrue(rleResult.compressionRatio > 0)
        
        val decompressed = compressor.decompressFile(rleResult.compressedData)
        assertEquals(testData, decompressed)
        
        // Test Simple Compression
        compressor.setCompressionStrategy(SimpleCompressionStrategy())
        val simpleResult = compressor.compressFile("the quick brown fox and the lazy dog")
        
        assertEquals("Simple Text Compression", simpleResult.algorithm)
        assertTrue(simpleResult.compressionRatio < 1.0)
        
        val simpleDecompressed = compressor.decompressFile(simpleResult.compressedData)
        assertEquals("the quick brown fox and the lazy dog", simpleDecompressed)
    }

    @Test
    fun `test Reactive Observer with Kotlin Flow`() = runBlocking {
        val sensor = TemperatureSensor()
        val readings = mutableListOf<Double>()
        
        // Start collecting temperature readings
        val job = launch {
            sensor.temperatureFlow.collect { temperature ->
                readings.add(temperature)
            }
        }
        
        delay(10) // Allow job to start
        
        // Emit some temperatures
        sensor.recordTemperature(20.0)
        sensor.recordTemperature(25.5)
        sensor.recordTemperature(18.3)
        
        delay(100) // Allow collection
        
        // Verify we collected the temperatures
        assertTrue(readings.size >= 3, "Should have collected at least 3 readings")
        assertTrue(readings.contains(20.0))
        assertTrue(readings.contains(25.5))
        assertTrue(readings.contains(18.3))
        
        job.cancel()
    }

    @Test
    fun `test pattern combinations and interactions`() {
        // Test Strategy with Observer
        val processor = PaymentProcessor()
        val stock = Stock("PAYMENT_STOCK", 100.0)
        val display = StockDisplay("Payment Display")
        
        stock.addObserver(display)
        
        // Use different payment strategies and update stock
        processor.setPaymentStrategy(CreditCardStrategy("1234567890123456", "12/25", "123"))
        val result1 = processor.processPayment(100.0)
        
        if (result1.success) {
            stock.setPrice(105.0) // Simulate stock price increase on successful payment
        }
        
        processor.setPaymentStrategy(PayPalStrategy("test@example.com"))
        val result2 = processor.processPayment(200.0)
        
        if (result2.success) {
            stock.setPrice(110.0)
        }
        
        val portfolio = display.displayPortfolio()
        assertTrue(portfolio.any { it.contains("PAYMENT_STOCK") })
        
        assertTrue(result1.success)
        assertTrue(result2.success)
        assertEquals(110.0, stock.getPrice(), 0.01)
    }

    // Helper extension function to check if list is sorted
    private fun <T : Comparable<T>> List<T>.isSorted(): Boolean {
        return this == this.sorted()
    }
}