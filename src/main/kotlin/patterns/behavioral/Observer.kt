package patterns.behavioral

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

/**
 * # Observer Pattern
 * 
 * ## Definition
 * Defines a one-to-many dependency between objects so that when one object changes state,
 * all its dependents are notified and updated automatically.
 * 
 * ## Problem it solves
 * - Need to maintain consistency between related objects
 * - Want to notify multiple objects about state changes
 * - Don't want tight coupling between subject and observers
 * - Need to support broadcast communication
 * 
 * ## When to use
 * - Changes to one object require changing multiple objects
 * - Object should notify others without knowing who they are
 * - Set of objects to notify can vary at runtime
 * - Need to implement event handling systems
 * 
 * ## When NOT to use
 * - Simple one-to-one relationships
 * - Performance is critical and notifications are expensive
 * - Complex update logic between observers
 * 
 * ## Advantages
 * - Loose coupling between subject and observers
 * - Support for broadcast communication
 * - Dynamic relationships between objects
 * - Open/closed principle compliance
 * 
 * ## Disadvantages
 * - Can cause memory leaks if observers aren't removed
 * - Can lead to unexpected update chains
 * - No guarantee of notification order
 * - Debugging can be difficult
 */

// 1. Classic Observer Pattern - Stock Market

// Observer interface
interface StockObserver {
    fun update(symbol: String, price: Double, change: Double)
}

// Subject interface
interface StockSubject {
    fun addObserver(observer: StockObserver)
    fun removeObserver(observer: StockObserver)
    fun notifyObservers()
}

// Concrete subject
class Stock(
    private val symbol: String,
    private var price: Double
) : StockSubject {
    
    private val observers = mutableSetOf<StockObserver>()
    private var previousPrice = price
    
    override fun addObserver(observer: StockObserver) {
        observers.add(observer)
    }
    
    override fun removeObserver(observer: StockObserver) {
        observers.remove(observer)
    }
    
    override fun notifyObservers() {
        val change = price - previousPrice
        observers.forEach { observer ->
            observer.update(symbol, price, change)
        }
    }
    
    fun setPrice(newPrice: Double) {
        previousPrice = price
        price = newPrice
        notifyObservers()
    }
    
    fun getPrice(): Double = price
    fun getSymbol(): String = symbol
    fun getObserverCount(): Int = observers.size
}

// Concrete observers
class StockDisplay(private val name: String) : StockObserver {
    private val portfolio = mutableMapOf<String, Pair<Double, Double>>() // symbol to (price, change)
    
    override fun update(symbol: String, price: Double, change: Double) {
        portfolio[symbol] = Pair(price, change)
        println("$name: $symbol updated to $${"%.2f".format(price)} (${if (change >= 0) "+" else ""}${"%.2f".format(change)})")
    }
    
    fun displayPortfolio(): List<String> {
        return portfolio.map { (symbol, data) ->
            val (price, change) = data
            "$symbol: $${"%.2f".format(price)} (${if (change >= 0) "+" else ""}${"%.2f".format(change)})"
        }
    }
}

class StockAlert(private val alertThreshold: Double) : StockObserver {
    private val alerts = mutableListOf<String>()
    
    override fun update(symbol: String, price: Double, change: Double) {
        val changePercent = (change / (price - change)) * 100
        if (Math.abs(changePercent) >= alertThreshold) {
            val alert = "ALERT: $symbol changed by ${"%.2f".format(changePercent)}% to $${"%.2f".format(price)}"
            alerts.add(alert)
            println(alert)
        }
    }
    
    fun getAlerts(): List<String> = alerts.toList()
    fun clearAlerts() = alerts.clear()
}

class TradingBot(
    private val buyThreshold: Double,
    private val sellThreshold: Double
) : StockObserver {
    
    private val trades = mutableListOf<Trade>()
    
    data class Trade(
        val symbol: String,
        val action: String,
        val price: Double,
        val timestamp: Long = System.currentTimeMillis()
    )
    
    override fun update(symbol: String, price: Double, change: Double) {
        val changePercent = if (price - change != 0.0) (change / (price - change)) * 100 else 0.0
        
        when {
            changePercent <= -buyThreshold -> {
                val trade = Trade(symbol, "BUY", price)
                trades.add(trade)
                println("TradingBot: BUY $symbol at $${"%.2f".format(price)} (down ${"%.2f".format(Math.abs(changePercent))}%)")
            }
            changePercent >= sellThreshold -> {
                val trade = Trade(symbol, "SELL", price)
                trades.add(trade)
                println("TradingBot: SELL $symbol at $${"%.2f".format(price)} (up ${"%.2f".format(changePercent)}%)")
            }
        }
    }
    
    fun getTrades(): List<Trade> = trades.toList()
    fun getTradeCount(): Int = trades.size
}

// 2. Event System using Observer Pattern

// Event types
sealed class ApplicationEvent {
    data class UserLoggedIn(val userId: String, val timestamp: Long) : ApplicationEvent()
    data class UserLoggedOut(val userId: String, val timestamp: Long) : ApplicationEvent()
    data class OrderPlaced(val orderId: String, val userId: String, val amount: Double) : ApplicationEvent()
    data class PaymentProcessed(val paymentId: String, val orderId: String, val amount: Double) : ApplicationEvent()
    data class SystemError(val error: String, val component: String, val severity: String) : ApplicationEvent()
}

// Event listener interface
interface EventListener {
    fun onEvent(event: ApplicationEvent)
    fun getSupportedEvents(): Set<Class<out ApplicationEvent>>
}

// Event publisher
class EventPublisher {
    private val listeners = mutableMapOf<Class<out ApplicationEvent>, MutableSet<EventListener>>()
    
    fun subscribe(eventType: Class<out ApplicationEvent>, listener: EventListener) {
        listeners.getOrPut(eventType) { mutableSetOf() }.add(listener)
    }
    
    fun unsubscribe(eventType: Class<out ApplicationEvent>, listener: EventListener) {
        listeners[eventType]?.remove(listener)
    }
    
    fun publish(event: ApplicationEvent) {
        val eventClass = event::class.java
        listeners[eventClass]?.forEach { listener ->
            try {
                listener.onEvent(event)
            } catch (e: Exception) {
                println("Error notifying listener: ${e.message}")
            }
        }
    }
    
    fun getListenerCount(eventType: Class<out ApplicationEvent>): Int {
        return listeners[eventType]?.size ?: 0
    }
}

// Concrete event listeners
class AuditLogger : EventListener {
    private val auditLog = mutableListOf<String>()
    
    override fun onEvent(event: ApplicationEvent) {
        val logEntry = when (event) {
            is ApplicationEvent.UserLoggedIn -> 
                "AUDIT: User ${event.userId} logged in at ${event.timestamp}"
            is ApplicationEvent.UserLoggedOut -> 
                "AUDIT: User ${event.userId} logged out at ${event.timestamp}"
            is ApplicationEvent.OrderPlaced -> 
                "AUDIT: Order ${event.orderId} placed by user ${event.userId} for $${event.amount}"
            is ApplicationEvent.PaymentProcessed -> 
                "AUDIT: Payment ${event.paymentId} processed for order ${event.orderId} - $${event.amount}"
            is ApplicationEvent.SystemError -> 
                "AUDIT: System error in ${event.component}: ${event.error} (${event.severity})"
        }
        auditLog.add(logEntry)
        println(logEntry)
    }
    
    override fun getSupportedEvents(): Set<Class<out ApplicationEvent>> {
        return setOf(
            ApplicationEvent.UserLoggedIn::class.java,
            ApplicationEvent.UserLoggedOut::class.java,
            ApplicationEvent.OrderPlaced::class.java,
            ApplicationEvent.PaymentProcessed::class.java,
            ApplicationEvent.SystemError::class.java
        )
    }
    
    fun getAuditLog(): List<String> = auditLog.toList()
}

class NotificationService : EventListener {
    private val notifications = mutableListOf<String>()
    
    override fun onEvent(event: ApplicationEvent) {
        val notification = when (event) {
            is ApplicationEvent.OrderPlaced -> 
                "Order confirmation: Your order ${event.orderId} for $${event.amount} has been placed."
            is ApplicationEvent.PaymentProcessed -> 
                "Payment confirmation: Your payment of $${event.amount} has been processed."
            is ApplicationEvent.SystemError -> 
                if (event.severity == "HIGH") "System alert: Critical error in ${event.component}" else null
            else -> null
        }
        
        notification?.let {
            notifications.add(it)
            println("NOTIFICATION: $it")
        }
    }
    
    override fun getSupportedEvents(): Set<Class<out ApplicationEvent>> {
        return setOf(
            ApplicationEvent.OrderPlaced::class.java,
            ApplicationEvent.PaymentProcessed::class.java,
            ApplicationEvent.SystemError::class.java
        )
    }
    
    fun getNotifications(): List<String> = notifications.toList()
}

class AnalyticsCollector : EventListener {
    private val userSessions = mutableMapOf<String, Long>()
    private val orderMetrics = mutableListOf<Double>()
    private val errorCounts = mutableMapOf<String, Int>()
    
    override fun onEvent(event: ApplicationEvent) {
        when (event) {
            is ApplicationEvent.UserLoggedIn -> {
                userSessions[event.userId] = event.timestamp
            }
            is ApplicationEvent.UserLoggedOut -> {
                val loginTime = userSessions[event.userId]
                if (loginTime != null) {
                    val sessionDuration = event.timestamp - loginTime
                    println("ANALYTICS: User ${event.userId} session duration: ${sessionDuration}ms")
                }
            }
            is ApplicationEvent.OrderPlaced -> {
                orderMetrics.add(event.amount)
                println("ANALYTICS: Order value recorded: $${event.amount}")
            }
            is ApplicationEvent.SystemError -> {
                errorCounts[event.component] = (errorCounts[event.component] ?: 0) + 1
                println("ANALYTICS: Error count for ${event.component}: ${errorCounts[event.component]}")
            }
            else -> { /* Not interested in other events */ }
        }
    }
    
    override fun getSupportedEvents(): Set<Class<out ApplicationEvent>> {
        return setOf(
            ApplicationEvent.UserLoggedIn::class.java,
            ApplicationEvent.UserLoggedOut::class.java,
            ApplicationEvent.OrderPlaced::class.java,
            ApplicationEvent.SystemError::class.java
        )
    }
    
    fun getAverageOrderValue(): Double {
        return if (orderMetrics.isNotEmpty()) orderMetrics.average() else 0.0
    }
    
    fun getErrorCount(component: String): Int = errorCounts[component] ?: 0
}

// 3. Kotlin Flow-based Observer Pattern (Reactive)

// Flow-based subject
class ReactiveDataSource<T> {
    private val _dataFlow = MutableSharedFlow<T>()
    val dataFlow: SharedFlow<T> = _dataFlow.asSharedFlow()
    
    suspend fun emit(value: T) {
        _dataFlow.emit(value)
    }
    
    fun getSubscriberCount(): Int = _dataFlow.subscriptionCount.value
}

// Temperature monitoring system using Flow
class TemperatureSensor {
    private val temperatureSource = ReactiveDataSource<Double>()
    val temperatureFlow = temperatureSource.dataFlow
    
    suspend fun recordTemperature(temperature: Double) {
        temperatureSource.emit(temperature)
    }
    
    // Simulate temperature readings
    suspend fun startSimulation() = coroutineScope {
        launch {
            var currentTemp = 20.0
            while (true) {
                currentTemp += (Math.random() - 0.5) * 2 // Random change ±1 degree
                recordTemperature(currentTemp)
                delay(1000) // Every second
            }
        }
    }
}

// Flow-based observers
class TemperatureDisplay {
    suspend fun startMonitoring(sensor: TemperatureSensor) {
        sensor.temperatureFlow.collect { temperature ->
            println("Display: Current temperature: ${"%.1f".format(temperature)}°C")
        }
    }
}

class TemperatureAlert(private val thresholds: Pair<Double, Double>) {
    suspend fun startMonitoring(sensor: TemperatureSensor) {
        sensor.temperatureFlow
            .filter { it < thresholds.first || it > thresholds.second }
            .collect { temperature ->
                val alertType = if (temperature < thresholds.first) "LOW" else "HIGH"
                println("ALERT: $alertType temperature detected: ${"%.1f".format(temperature)}°C")
            }
    }
}

class TemperatureLogger {
    private val readings = mutableListOf<Pair<Double, Long>>()
    
    suspend fun startLogging(sensor: TemperatureSensor) {
        sensor.temperatureFlow
            .sample(5000) // Log every 5 seconds
            .collect { temperature ->
                val timestamp = System.currentTimeMillis()
                readings.add(Pair(temperature, timestamp))
                println("LOG: Temperature ${"%.1f".format(temperature)}°C at $timestamp")
            }
    }
    
    fun getReadings(): List<Pair<Double, Long>> = readings.toList()
    fun getAverageTemperature(): Double = readings.map { it.first }.average()
}

// 4. Model-View-Controller with Observer Pattern

// Model
class UserModel {
    private val observers = mutableSetOf<UserObserver>()
    private var users = mutableListOf<User>()
    
    data class User(val id: String, val name: String, val email: String, val active: Boolean = true)
    
    interface UserObserver {
        fun onUserAdded(user: User)
        fun onUserUpdated(user: User)
        fun onUserDeleted(userId: String)
    }
    
    fun addObserver(observer: UserObserver) {
        observers.add(observer)
    }
    
    fun removeObserver(observer: UserObserver) {
        observers.remove(observer)
    }
    
    fun addUser(user: User) {
        users.add(user)
        observers.forEach { it.onUserAdded(user) }
    }
    
    fun updateUser(updatedUser: User) {
        val index = users.indexOfFirst { it.id == updatedUser.id }
        if (index != -1) {
            users[index] = updatedUser
            observers.forEach { it.onUserUpdated(updatedUser) }
        }
    }
    
    fun deleteUser(userId: String) {
        users.removeIf { it.id == userId }
        observers.forEach { it.onUserDeleted(userId) }
    }
    
    fun getUsers(): List<User> = users.toList()
    fun getUserById(id: String): User? = users.find { it.id == id }
}

// Views (Observers)
class UserListView : UserModel.UserObserver {
    private val displayedUsers = mutableListOf<UserModel.User>()
    
    override fun onUserAdded(user: UserModel.User) {
        displayedUsers.add(user)
        refreshDisplay()
    }
    
    override fun onUserUpdated(user: UserModel.User) {
        val index = displayedUsers.indexOfFirst { it.id == user.id }
        if (index != -1) {
            displayedUsers[index] = user
            refreshDisplay()
        }
    }
    
    override fun onUserDeleted(userId: String) {
        displayedUsers.removeIf { it.id == userId }
        refreshDisplay()
    }
    
    private fun refreshDisplay() {
        println("UserListView: Displaying ${displayedUsers.size} users")
        displayedUsers.forEach { user ->
            println("  - ${user.name} (${user.email}) ${if (user.active) "[Active]" else "[Inactive]"}")
        }
    }
    
    fun getCurrentUsers(): List<UserModel.User> = displayedUsers.toList()
}

class UserStatsView : UserModel.UserObserver {
    private var totalUsers = 0
    private var activeUsers = 0
    
    override fun onUserAdded(user: UserModel.User) {
        totalUsers++
        if (user.active) activeUsers++
        updateStats()
    }
    
    override fun onUserUpdated(user: UserModel.User) {
        // Would need to track previous state for accurate stats
        updateStats()
    }
    
    override fun onUserDeleted(userId: String) {
        totalUsers--
        updateStats()
    }
    
    private fun updateStats() {
        println("UserStatsView: Total Users: $totalUsers, Active Users: $activeUsers")
    }
    
    fun getStats(): Pair<Int, Int> = Pair(totalUsers, activeUsers)
}

// Controller
class UserController(private val model: UserModel) {
    fun createUser(name: String, email: String): String {
        val user = UserModel.User(
            id = "user_${System.currentTimeMillis()}",
            name = name,
            email = email
        )
        model.addUser(user)
        return "User ${user.id} created successfully"
    }
    
    fun updateUserStatus(userId: String, active: Boolean): String {
        val user = model.getUserById(userId)
        return if (user != null) {
            model.updateUser(user.copy(active = active))
            "User $userId status updated to ${if (active) "active" else "inactive"}"
        } else {
            "User $userId not found"
        }
    }
    
    fun deleteUser(userId: String): String {
        val user = model.getUserById(userId)
        return if (user != null) {
            model.deleteUser(userId)
            "User $userId deleted successfully"
        } else {
            "User $userId not found"
        }
    }
}