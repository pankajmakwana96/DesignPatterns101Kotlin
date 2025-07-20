package patterns.structural

/**
 * # Facade Pattern
 * 
 * ## Definition
 * Provides a unified interface to a set of interfaces in a subsystem.
 * Defines a higher-level interface that makes the subsystem easier to use.
 * 
 * ## Problem it solves
 * - Complex subsystem with many interfaces
 * - Want to provide a simple interface to a complex subsystem
 * - Need to decouple clients from subsystem components
 * - Want to layer your subsystems
 * 
 * ## When to use
 * - Want to provide a simple interface to a complex subsystem
 * - Many dependencies between clients and implementation classes
 * - Want to layer your subsystems
 * - Need to wrap a poorly designed collection of APIs
 * 
 * ## When NOT to use
 * - Subsystem is already simple
 * - Need full access to subsystem functionality
 * - Facade would add unnecessary complexity
 * 
 * ## Advantages
 * - Shields clients from subsystem components
 * - Promotes weak coupling between subsystem and clients
 * - Reduces compilation dependencies
 * - Provides a simplified interface
 * 
 * ## Disadvantages
 * - Can become a god object
 * - May limit access to advanced features
 * - Can hide important complexity
 * - May not reduce the interface enough
 */

// 1. Home Theater System Facade

// Subsystem classes
class Amplifier {
    fun on(): String = "Amplifier: Turning on"
    fun off(): String = "Amplifier: Turning off"
    fun setVolume(volume: Int): String = "Amplifier: Setting volume to $volume"
    fun setSurroundSound(): String = "Amplifier: Setting surround sound"
    fun setStereoSound(): String = "Amplifier: Setting stereo sound"
}

class DvdPlayer {
    fun on(): String = "DVD Player: Turning on"
    fun off(): String = "DVD Player: Turning off"
    fun play(movie: String): String = "DVD Player: Playing '$movie'"
    fun stop(): String = "DVD Player: Stopping"
    fun eject(): String = "DVD Player: Ejecting disc"
}

class Projector {
    fun on(): String = "Projector: Turning on"
    fun off(): String = "Projector: Turning off"
    fun setInput(input: String): String = "Projector: Setting input to $input"
    fun wideScreenMode(): String = "Projector: Setting wide screen mode"
    fun tvMode(): String = "Projector: Setting TV mode"
}

class TheaterLights {
    fun on(): String = "Theater Lights: Turning on"
    fun off(): String = "Theater Lights: Turning off"
    fun dim(level: Int): String = "Theater Lights: Dimming to $level%"
}

class Screen {
    fun up(): String = "Screen: Going up"
    fun down(): String = "Screen: Going down"
}

class PopcornPopper {
    fun on(): String = "Popcorn Popper: Turning on"
    fun off(): String = "Popcorn Popper: Turning off"
    fun pop(): String = "Popcorn Popper: Popping popcorn"
}

// Facade
class HomeTheaterFacade(
    private val amplifier: Amplifier,
    private val dvdPlayer: DvdPlayer,
    private val projector: Projector,
    private val lights: TheaterLights,
    private val screen: Screen,
    private val popper: PopcornPopper
) {
    
    fun watchMovie(movie: String): List<String> {
        val steps = mutableListOf<String>()
        steps.add("Get ready to watch a movie...")
        steps.add(popper.on())
        steps.add(popper.pop())
        steps.add(lights.dim(10))
        steps.add(screen.down())
        steps.add(projector.on())
        steps.add(projector.wideScreenMode())
        steps.add(amplifier.on())
        steps.add(amplifier.setVolume(5))
        steps.add(amplifier.setSurroundSound())
        steps.add(dvdPlayer.on())
        steps.add(dvdPlayer.play(movie))
        return steps
    }
    
    fun endMovie(): List<String> {
        val steps = mutableListOf<String>()
        steps.add("Shutting movie theater down...")
        steps.add(popper.off())
        steps.add(lights.on())
        steps.add(screen.up())
        steps.add(projector.off())
        steps.add(amplifier.off())
        steps.add(dvdPlayer.stop())
        steps.add(dvdPlayer.eject())
        steps.add(dvdPlayer.off())
        return steps
    }
    
    fun pauseMovie(): List<String> {
        return listOf(
            "Pausing movie...",
            dvdPlayer.stop(),
            lights.dim(50)
        )
    }
    
    fun resumeMovie(): List<String> {
        return listOf(
            "Resuming movie...",
            lights.dim(10),
            dvdPlayer.play("current movie")
        )
    }
}

// 2. Computer System Facade

// Subsystem classes
class CPU {
    fun freeze(): String = "CPU: Freezing"
    fun jump(position: Long): String = "CPU: Jumping to position $position"
    fun execute(): String = "CPU: Executing instructions"
}

class Memory {
    fun load(position: Long, data: ByteArray): String = 
        "Memory: Loading ${data.size} bytes at position $position"
}

class HardDrive {
    fun read(lba: Long, size: Int): ByteArray = 
        ByteArray(size) { (it % 256).toByte() }
    
    fun getBootSector(): String = "Hard Drive: Reading boot sector"
}

class Graphics {
    fun initialize(): String = "Graphics: Initializing graphics system"
    fun setResolution(width: Int, height: Int): String = 
        "Graphics: Setting resolution to ${width}x$height"
    fun enableAcceleration(): String = "Graphics: Enabling hardware acceleration"
}

class Network {
    fun connect(): String = "Network: Connecting to network"
    fun disconnect(): String = "Network: Disconnecting from network"
    fun getIP(): String = "Network: IP address 192.168.1.100"
}

class Audio {
    fun initialize(): String = "Audio: Initializing audio system"
    fun setVolume(level: Int): String = "Audio: Setting volume to $level"
    fun enableSurround(): String = "Audio: Enabling surround sound"
}

// Facade
class ComputerFacade(
    private val cpu: CPU,
    private val memory: Memory,
    private val hardDrive: HardDrive,
    private val graphics: Graphics,
    private val network: Network,
    private val audio: Audio
) {
    
    companion object {
        private const val BOOT_ADDRESS = 0L
        private const val BOOT_SECTOR_SIZE = 512
        private const val SECTOR_SIZE = 512
    }
    
    fun startComputer(): List<String> {
        val steps = mutableListOf<String>()
        steps.add("Starting computer...")
        
        // Power on sequence
        steps.add(cpu.freeze())
        steps.add(hardDrive.getBootSector())
        
        val bootData = hardDrive.read(BOOT_ADDRESS, BOOT_SECTOR_SIZE)
        steps.add(memory.load(BOOT_ADDRESS, bootData))
        
        steps.add(cpu.jump(BOOT_ADDRESS))
        steps.add(cpu.execute())
        
        // Initialize peripherals
        steps.add(graphics.initialize())
        steps.add(graphics.setResolution(1920, 1080))
        steps.add(graphics.enableAcceleration())
        
        steps.add(audio.initialize())
        steps.add(audio.setVolume(50))
        steps.add(audio.enableSurround())
        
        steps.add(network.connect())
        steps.add(network.getIP())
        
        steps.add("Computer startup complete!")
        return steps
    }
    
    fun shutdownComputer(): List<String> {
        val steps = mutableListOf<String>()
        steps.add("Shutting down computer...")
        steps.add(network.disconnect())
        steps.add("Saving system state...")
        steps.add("Computer shutdown complete!")
        return steps
    }
    
    fun enterSleepMode(): List<String> {
        return listOf(
            "Entering sleep mode...",
            audio.setVolume(0),
            network.disconnect(),
            "Computer is now sleeping"
        )
    }
    
    fun wakeFromSleep(): List<String> {
        return listOf(
            "Waking from sleep...",
            network.connect(),
            audio.setVolume(50),
            "Computer is now awake"
        )
    }
}

// 3. E-commerce Order Processing Facade

// Subsystem classes
class PaymentProcessor {
    fun validatePayment(cardNumber: String, amount: Double): Boolean = true
    fun processPayment(cardNumber: String, amount: Double): String = 
        "Payment of $$amount processed successfully"
    fun refundPayment(transactionId: String, amount: Double): String = 
        "Refund of $$amount processed for transaction $transactionId"
}

class InventoryManager {
    private val inventory = mutableMapOf<String, Int>(
        "laptop" to 50,
        "mouse" to 100,
        "keyboard" to 75
    )
    
    fun checkAvailability(productId: String, quantity: Int): Boolean = 
        (inventory[productId] ?: 0) >= quantity
    
    fun reserveItems(productId: String, quantity: Int): String {
        val available = inventory[productId] ?: 0
        if (available >= quantity) {
            inventory[productId] = available - quantity
            return "Reserved $quantity units of $productId"
        }
        return "Insufficient inventory for $productId"
    }
    
    fun releaseReservation(productId: String, quantity: Int): String {
        val current = inventory[productId] ?: 0
        inventory[productId] = current + quantity
        return "Released reservation for $quantity units of $productId"
    }
}

class ShippingService {
    fun calculateShippingCost(address: String, weight: Double): Double = 
        when {
            weight < 1.0 -> 5.99
            weight < 5.0 -> 9.99
            else -> 15.99
        }
    
    fun scheduleDelivery(address: String, items: List<String>): String = 
        "Delivery scheduled to $address for items: ${items.joinToString(", ")}"
    
    fun trackShipment(trackingNumber: String): String = 
        "Shipment $trackingNumber is in transit"
}

class NotificationServiceFacade {
    fun sendOrderConfirmation(email: String, orderId: String): String = 
        "Order confirmation sent to $email for order $orderId"
    
    fun sendShippingNotification(email: String, trackingNumber: String): String = 
        "Shipping notification sent to $email with tracking number $trackingNumber"
    
    fun sendDeliveryNotification(email: String): String = 
        "Delivery notification sent to $email"
}

class OrderDatabase {
    private val orders = mutableMapOf<String, Order>()
    
    data class Order(
        val id: String,
        val customerId: String,
        val items: List<String>,
        val totalAmount: Double,
        val status: String
    )
    
    fun saveOrder(order: Order): String {
        orders[order.id] = order
        return "Order ${order.id} saved to database"
    }
    
    fun updateOrderStatus(orderId: String, status: String): String {
        orders[orderId]?.let { order ->
            orders[orderId] = order.copy(status = status)
            return "Order $orderId status updated to $status"
        }
        return "Order $orderId not found"
    }
    
    fun getOrder(orderId: String): Order? = orders[orderId]
}

// Facade
class OrderProcessingFacade(
    private val paymentProcessor: PaymentProcessor,
    private val inventoryManager: InventoryManager,
    private val shippingService: ShippingService,
    private val notificationService: NotificationServiceFacade,
    private val orderDatabase: OrderDatabase
) {
    
    data class OrderRequest(
        val customerId: String,
        val customerEmail: String,
        val items: Map<String, Int>, // productId to quantity
        val shippingAddress: String,
        val paymentCard: String
    )
    
    fun processOrder(request: OrderRequest): OrderResult {
        val orderId = generateOrderId()
        val steps = mutableListOf<String>()
        
        try {
            // 1. Validate inventory
            steps.add("Checking inventory availability...")
            request.items.forEach { (productId, quantity) ->
                if (!inventoryManager.checkAvailability(productId, quantity)) {
                    return OrderResult.failure(orderId, steps, "Insufficient inventory for $productId")
                }
            }
            
            // 2. Calculate total cost
            val itemsCost = request.items.entries.sumOf { (_, quantity) -> quantity * 100.0 } // Simplified pricing
            val shippingCost = shippingService.calculateShippingCost(request.shippingAddress, 2.0)
            val totalCost = itemsCost + shippingCost
            
            // 3. Process payment
            steps.add("Processing payment...")
            if (!paymentProcessor.validatePayment(request.paymentCard, totalCost)) {
                return OrderResult.failure(orderId, steps, "Payment validation failed")
            }
            
            val paymentResult = paymentProcessor.processPayment(request.paymentCard, totalCost)
            steps.add(paymentResult)
            
            // 4. Reserve inventory
            steps.add("Reserving inventory...")
            request.items.forEach { (productId, quantity) ->
                steps.add(inventoryManager.reserveItems(productId, quantity))
            }
            
            // 5. Save order
            val order = OrderDatabase.Order(
                id = orderId,
                customerId = request.customerId,
                items = request.items.keys.toList(),
                totalAmount = totalCost,
                status = "confirmed"
            )
            steps.add(orderDatabase.saveOrder(order))
            
            // 6. Schedule shipping
            steps.add("Scheduling shipping...")
            val shippingResult = shippingService.scheduleDelivery(
                request.shippingAddress, 
                request.items.keys.toList()
            )
            steps.add(shippingResult)
            
            // 7. Send notifications
            steps.add("Sending notifications...")
            steps.add(notificationService.sendOrderConfirmation(request.customerEmail, orderId))
            
            steps.add("Order processed successfully!")
            return OrderResult.success(orderId, steps, totalCost)
            
        } catch (e: Exception) {
            // Rollback on failure
            steps.add("Error occurred: ${e.message}")
            steps.add("Rolling back...")
            request.items.forEach { (productId, quantity) ->
                inventoryManager.releaseReservation(productId, quantity)
            }
            return OrderResult.failure(orderId, steps, e.message ?: "Unknown error")
        }
    }
    
    fun cancelOrder(orderId: String): List<String> {
        val steps = mutableListOf<String>()
        val order = orderDatabase.getOrder(orderId)
        
        if (order == null) {
            steps.add("Order $orderId not found")
            return steps
        }
        
        steps.add("Canceling order $orderId...")
        steps.add(orderDatabase.updateOrderStatus(orderId, "cancelled"))
        
        // Release inventory
        order.items.forEach { productId ->
            steps.add(inventoryManager.releaseReservation(productId, 1)) // Simplified
        }
        
        // Process refund
        steps.add(paymentProcessor.refundPayment("txn_$orderId", order.totalAmount))
        
        steps.add("Order $orderId cancelled successfully")
        return steps
    }
    
    fun trackOrder(orderId: String): String {
        val order = orderDatabase.getOrder(orderId)
        return if (order != null) {
            "Order $orderId status: ${order.status}"
        } else {
            "Order $orderId not found"
        }
    }
    
    private fun generateOrderId(): String = "ORD${System.currentTimeMillis()}"
    
    data class OrderResult(
        val orderId: String,
        val success: Boolean,
        val steps: List<String>,
        val totalCost: Double?,
        val error: String?
    ) {
        companion object {
            fun success(orderId: String, steps: List<String>, totalCost: Double) = 
                OrderResult(orderId, true, steps, totalCost, null)
            
            fun failure(orderId: String, steps: List<String>, error: String) = 
                OrderResult(orderId, false, steps, null, error)
        }
    }
}

// 4. API Gateway Facade (Microservices)

// External service interfaces
interface UserService {
    fun getUserById(userId: String): User?
    fun validateUser(userId: String, token: String): Boolean
    
    data class User(val id: String, val name: String, val email: String)
}

interface ProductService {
    fun getProductById(productId: String): Product?
    fun searchProducts(query: String): List<Product>
    
    data class Product(val id: String, val name: String, val price: Double)
}

interface OrderService {
    fun createOrder(userId: String, productIds: List<String>): String
    fun getOrderById(orderId: String): Order?
    
    data class Order(val id: String, val userId: String, val products: List<String>, val status: String)
}

interface PaymentService {
    fun processPayment(orderId: String, amount: Double, cardToken: String): PaymentResult
    
    data class PaymentResult(val success: Boolean, val transactionId: String?, val error: String?)
}

// Implementations (simplified)
class UserServiceImpl : UserService {
    private val users = mapOf(
        "1" to UserService.User("1", "John Doe", "john@example.com"),
        "2" to UserService.User("2", "Jane Smith", "jane@example.com")
    )
    
    override fun getUserById(userId: String): UserService.User? = users[userId]
    override fun validateUser(userId: String, token: String): Boolean = users.containsKey(userId)
}

class ProductServiceImpl : ProductService {
    private val products = mapOf(
        "1" to ProductService.Product("1", "Laptop", 999.99),
        "2" to ProductService.Product("2", "Mouse", 29.99),
        "3" to ProductService.Product("3", "Keyboard", 79.99)
    )
    
    override fun getProductById(productId: String): ProductService.Product? = products[productId]
    
    override fun searchProducts(query: String): List<ProductService.Product> = 
        products.values.filter { it.name.contains(query, ignoreCase = true) }
}

class OrderServiceImpl : OrderService {
    private val orders = mutableMapOf<String, OrderService.Order>()
    
    override fun createOrder(userId: String, productIds: List<String>): String {
        val orderId = "order_${System.currentTimeMillis()}"
        val order = OrderService.Order(orderId, userId, productIds, "pending")
        orders[orderId] = order
        return orderId
    }
    
    override fun getOrderById(orderId: String): OrderService.Order? = orders[orderId]
}

class PaymentServiceImpl : PaymentService {
    override fun processPayment(orderId: String, amount: Double, cardToken: String): PaymentService.PaymentResult {
        return if (cardToken.isNotEmpty() && amount > 0) {
            PaymentService.PaymentResult(true, "txn_${System.currentTimeMillis()}", null)
        } else {
            PaymentService.PaymentResult(false, null, "Invalid payment details")
        }
    }
}

// API Gateway Facade
class ApiGatewayFacade(
    private val userService: UserService,
    private val productService: ProductService,
    private val orderService: OrderService,
    private val paymentService: PaymentService
) {
    
    data class CreateOrderRequest(
        val userId: String,
        val userToken: String,
        val productIds: List<String>,
        val cardToken: String
    )
    
    data class ApiResponse<T>(
        val success: Boolean,
        val data: T?,
        val error: String?
    ) {
        companion object {
            fun <T> success(data: T) = ApiResponse(true, data, null)
            fun <T> failure(error: String) = ApiResponse<T>(false, null, error)
        }
    }
    
    fun getUserProfile(userId: String, token: String): ApiResponse<UserService.User> {
        return if (userService.validateUser(userId, token)) {
            val user = userService.getUserById(userId)
            if (user != null) {
                ApiResponse.success(user)
            } else {
                ApiResponse.failure("User not found")
            }
        } else {
            ApiResponse.failure("Unauthorized")
        }
    }
    
    fun searchProducts(query: String, userId: String, token: String): ApiResponse<List<ProductService.Product>> {
        return if (userService.validateUser(userId, token)) {
            val products = productService.searchProducts(query)
            ApiResponse.success(products)
        } else {
            ApiResponse.failure("Unauthorized")
        }
    }
    
    fun createOrder(request: CreateOrderRequest): ApiResponse<String> {
        // Validate user
        if (!userService.validateUser(request.userId, request.userToken)) {
            return ApiResponse.failure("Unauthorized")
        }
        
        // Validate products exist
        val products = request.productIds.mapNotNull { productService.getProductById(it) }
        if (products.size != request.productIds.size) {
            return ApiResponse.failure("Some products not found")
        }
        
        // Calculate total
        val total = products.sumOf { it.price }
        
        // Create order
        val orderId = orderService.createOrder(request.userId, request.productIds)
        
        // Process payment
        val paymentResult = paymentService.processPayment(orderId, total, request.cardToken)
        
        return if (paymentResult.success) {
            ApiResponse.success(orderId)
        } else {
            ApiResponse.failure(paymentResult.error ?: "Payment failed")
        }
    }
    
    fun getOrderDetails(orderId: String, userId: String, token: String): ApiResponse<OrderService.Order> {
        if (!userService.validateUser(userId, token)) {
            return ApiResponse.failure("Unauthorized")
        }
        
        val order = orderService.getOrderById(orderId)
        return if (order != null && order.userId == userId) {
            ApiResponse.success(order)
        } else {
            ApiResponse.failure("Order not found or access denied")
        }
    }
}