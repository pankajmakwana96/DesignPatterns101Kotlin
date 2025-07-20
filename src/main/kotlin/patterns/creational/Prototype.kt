package patterns.creational

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * # Prototype Pattern
 *
 * ## Definition
 * Creates objects by cloning an existing instance rather than creating new instances
 * from scratch. Specifies the kinds of objects to create using a prototypical instance.
 *
 * ## Problem it solves
 * - Creating objects is expensive (database calls, network requests, complex calculations)
 * - Want to avoid subclasses of an object creator in the client application
 * - Need to create objects at runtime based on dynamic configuration
 * - Want to reduce the number of classes
 *
 * ## When to use
 * - Object creation is expensive
 * - Classes to instantiate are specified at runtime
 * - You want to avoid building a class hierarchy of factories
 * - Instances of a class can have one of only a few different combinations of state
 *
 * ## When NOT to use
 * - Object creation is simple and cheap
 * - Objects don't have many variations
 * - Deep cloning is complex and error-prone
 * - Immutable objects are preferred
 *
 * ## Advantages
 * - Reduces need for subclassing
 * - Can add and remove objects at runtime
 * - Can specify new objects by varying values
 * - Can specify new objects by varying structure
 * - Reduced initialization code
 *
 * ## Disadvantages
 * - Can be difficult to implement deep cloning
 * - Each subclass must implement cloning
 * - Circular references can cause issues
 * - May not be needed if object creation is simple
 */

// 1. Basic Prototype using Kotlin's data class copy() method
@Serializable
data class BasicDocument(
    val title: String,
    val content: String,
    val author: String,
    val createdAt: Long,
    val tags: List<String> = emptyList(),
    val metadata: Map<String, String> = emptyMap()
) {
    // Kotlin data classes automatically provide copy() method
    fun createCopy(
        title: String = this.title,
        content: String = this.content,
        author: String = this.author,
        createdAt: Long = this.createdAt,
        tags: List<String> = this.tags,
        metadata: Map<String, String> = this.metadata
    ): BasicDocument {
        return copy(title, content, author, createdAt, tags, metadata)
    }
}

// 2. Custom Prototype interface for more complex cloning
interface Prototype<T> {
    fun clone(): T
}

// Shape hierarchy with prototype pattern
abstract class Shape : Prototype<Shape> {
    abstract var x: Int
    abstract var y: Int
    abstract var color: String

    abstract fun draw(): String
    abstract fun calculateArea(): Double
}

class Circle(
    override var x: Int,
    override var y: Int,
    override var color: String,
    var radius: Double
) : Shape() {

    override fun clone(): Circle {
        return Circle(x, y, color, radius)
    }

    override fun draw(): String = "Drawing circle at ($x, $y) with radius $radius and color $color"

    override fun calculateArea(): Double = Math.PI * radius * radius
}

class Rectangle(
    override var x: Int,
    override var y: Int,
    override var color: String,
    var width: Double,
    var height: Double
) : Shape() {

    override fun clone(): Rectangle {
        return Rectangle(x, y, color, width, height)
    }

    override fun draw(): String = "Drawing rectangle at ($x, $y) with size ${width}x$height and color $color"

    override fun calculateArea(): Double = width * height
}

class Triangle(
    override var x: Int,
    override var y: Int,
    override var color: String,
    var base: Double,
    var height: Double
) : Shape() {

    override fun clone(): Triangle {
        return Triangle(x, y, color, base, height)
    }

    override fun draw(): String = "Drawing triangle at ($x, $y) with base $base, height $height and color $color"

    override fun calculateArea(): Double = 0.5 * base * height
}

// 3. Prototype Manager/Registry
class ShapePrototypeManager {
    private val prototypes = mutableMapOf<String, Shape>()

    fun addPrototype(key: String, shape: Shape) {
        prototypes[key] = shape
    }

    fun getPrototype(key: String): Shape? {
        return prototypes[key]?.clone()
    }

    fun removePrototype(key: String) {
        prototypes.remove(key)
    }

    fun listPrototypes(): List<String> = prototypes.keys.toList()

    // Predefined common shapes
    init {
        addPrototype("small-red-circle", Circle(0, 0, "red", 5.0))
        addPrototype("medium-blue-circle", Circle(0, 0, "blue", 10.0))
        addPrototype("large-green-circle", Circle(0, 0, "green", 20.0))
        addPrototype("small-red-rectangle", Rectangle(0, 0, "red", 10.0, 5.0))
        addPrototype("square", Rectangle(0, 0, "black", 10.0, 10.0))
        addPrototype("right-triangle", Triangle(0, 0, "yellow", 6.0, 8.0))
    }
}

// 4. Deep cloning with complex nested objects
@Serializable
data class Address(
    val street: String,
    val city: String,
    val zipCode: String,
    val country: String
)

@Serializable
data class Contact(
    val email: String,
    val phone: String,
    val address: Address
)

@Serializable
data class Person(
    val firstName: String,
    val lastName: String,
    val age: Int,
    val contact: Contact,
    val hobbies: MutableList<String>,
    val properties: MutableMap<String, String>
) : Prototype<Person> {

    // Deep cloning using serialization (Kotlin way)
    override fun clone(): Person {
        val json = Json.encodeToString(this)
        return Json.decodeFromString(json)
    }

    // Manual deep cloning (more control)
    fun deepClone(): Person {
        return Person(
            firstName = this.firstName,
            lastName = this.lastName,
            age = this.age,
            contact = Contact(
                email = this.contact.email,
                phone = this.contact.phone,
                address = Address(
                    street = this.contact.address.street,
                    city = this.contact.address.city,
                    zipCode = this.contact.address.zipCode,
                    country = this.contact.address.country
                )
            ),
            hobbies = this.hobbies.toMutableList(), // Create new list
            properties = this.properties.toMutableMap() // Create new map
        )
    }
}

// 5. Game entity prototype system
interface GameEntity : Prototype<GameEntity> {
    val id: String
    var x: Float
    var y: Float
    var health: Int
    var maxHealth: Int

    fun takeDamage(damage: Int): Boolean
    fun heal(amount: Int)
    fun move(deltaX: Float, deltaY: Float)
    fun getInfo(): String
}

abstract class BaseGameEntity(
    override val id: String,
    override var x: Float,
    override var y: Float,
    override var health: Int,
    override var maxHealth: Int
) : GameEntity {

    override fun takeDamage(damage: Int): Boolean {
        health = maxOf(0, health - damage)
        return health <= 0
    }

    override fun heal(amount: Int) {
        health = minOf(maxHealth, health + amount)
    }

    override fun move(deltaX: Float, deltaY: Float) {
        x += deltaX
        y += deltaY
    }
}

class Warrior(
    id: String,
    x: Float,
    y: Float,
    health: Int,
    maxHealth: Int,
    var weapon: String,
    var armor: String,
    var attackPower: Int
) : BaseGameEntity(id, x, y, health, maxHealth) {

    override fun clone(): Warrior {
        return Warrior(
            id = "warrior_${System.currentTimeMillis()}",
            x = 0f, y = 0f, // Reset position for new instance
            health = maxHealth, // Reset to full health
            maxHealth = maxHealth,
            weapon = weapon,
            armor = armor,
            attackPower = attackPower
        )
    }

    override fun getInfo(): String =
        "Warrior [$id] at ($x, $y) - Health: $health/$maxHealth, Weapon: $weapon, Armor: $armor, Attack: $attackPower"

    fun attack(): String = "Warrior attacks with $weapon for $attackPower damage!"
}

class Mage(
    id: String,
    x: Float,
    y: Float,
    health: Int,
    maxHealth: Int,
    var mana: Int,
    var maxMana: Int,
    var spells: MutableList<String>,
    var magicPower: Int
) : BaseGameEntity(id, x, y, health, maxHealth) {

    override fun clone(): Mage {
        return Mage(
            id = "mage_${System.currentTimeMillis()}",
            x = 0f, y = 0f,
            health = maxHealth,
            maxHealth = maxHealth,
            mana = maxMana,
            maxMana = maxMana,
            spells = spells.toMutableList(), // Deep copy spells list
            magicPower = magicPower
        )
    }

    override fun getInfo(): String =
        "Mage [$id] at ($x, $y) - Health: $health/$maxHealth, Mana: $mana/$maxMana, Magic Power: $magicPower, Spells: ${spells.joinToString()}"

    fun castSpell(spellName: String): String {
        return if (spells.contains(spellName) && mana >= 10) {
            mana -= 10
            "Mage casts $spellName for $magicPower magic damage!"
        } else {
            "Cannot cast $spellName - insufficient mana or spell not known"
        }
    }
}

class EntityPrototypeRegistry {
    private val prototypes = mutableMapOf<String, GameEntity>()

    fun registerPrototype(name: String, entity: GameEntity) {
        prototypes[name] = entity
    }

    fun createEntity(prototypeName: String): GameEntity? {
        return prototypes[prototypeName]?.clone()
    }

    fun listPrototypes(): List<String> = prototypes.keys.toList()

    // Initialize with common entity templates
    init {
        registerPrototype(
            "basic-warrior", Warrior(
                "warrior_template", 0f, 0f, 100, 100,
                "Iron Sword", "Leather Armor", 25
            )
        )

        registerPrototype(
            "elite-warrior", Warrior(
                "elite_warrior_template", 0f, 0f, 200, 200,
                "Excalibur", "Plate Armor", 50
            )
        )

        registerPrototype(
            "basic-mage", Mage(
                "mage_template", 0f, 0f, 80, 80, 100, 100,
                mutableListOf("Fireball", "Heal"), 30
            )
        )

        registerPrototype(
            "archmage", Mage(
                "archmage_template", 0f, 0f, 150, 150, 200, 200,
                mutableListOf("Fireball", "Heal", "Lightning Bolt", "Teleport", "Meteor"), 60
            )
        )
    }
}

// 6. Configuration prototype for different environments
@Serializable
data class DatabaseConfigPT(
    val host: String,
    val port: Int,
    val database: String,
    val username: String,
    val password: String,
    val connectionPool: Int,
    val timeout: Long
)

@Serializable
data class ServerConfig(
    val port: Int,
    val host: String,
    val maxConnections: Int,
    val requestTimeout: Long,
    val enableSsl: Boolean
)

@Serializable
data class ApplicationConfig(
    val environment: String,
    val debugMode: Boolean,
    val logLevel: String,
    val databaseConfig: DatabaseConfigPT,
    val serverConfig: ServerConfig,
    val features: MutableMap<String, Boolean>
) : Prototype<ApplicationConfig> {

    override fun clone(): ApplicationConfig {
        val json = Json.encodeToString(this)
        return Json.decodeFromString(json)
    }

    fun createForEnvironment(env: String): ApplicationConfig {
        val cloned = clone()

        // Since data classes are immutable, we need to use copy() method
        return when (env) {
            "development" -> cloned.copy(
                environment = env,
                debugMode = true,
                logLevel = "DEBUG",
                databaseConfig = cloned.databaseConfig.copy(
                    host = "localhost",
                    database = "app_dev"
                )
            )

            "staging" -> cloned.copy(
                environment = env,
                debugMode = false,
                logLevel = "INFO",
                databaseConfig = cloned.databaseConfig.copy(
                    host = "staging-db.company.com",
                    database = "app_staging"
                )
            )

            "production" -> cloned.copy(
                environment = env,
                debugMode = false,
                logLevel = "WARN",
                databaseConfig = cloned.databaseConfig.copy(
                    host = "prod-db.company.com",
                    database = "app_prod",
                    connectionPool = 50
                ),
                serverConfig = cloned.serverConfig.copy(
                    enableSsl = true,
                    maxConnections = 1000
                )
            )

            else -> cloned.copy(environment = env)
        }
    }
}

// Configuration prototype manager
object ConfigPrototypeManager {
    private val baseConfig = ApplicationConfig(
        environment = "base",
        debugMode = false,
        logLevel = "INFO",
        databaseConfig = DatabaseConfigPT(
            host = "localhost",
            port = 5432,
            database = "app",
            username = "user",
            password = "password",
            connectionPool = 10,
            timeout = 30000
        ),
        serverConfig = ServerConfig(
            port = 8080,
            host = "0.0.0.0",
            maxConnections = 100,
            requestTimeout = 30000,
            enableSsl = false
        ),
        features = mutableMapOf(
            "userRegistration" to true,
            "emailNotifications" to true,
            "dataExport" to false,
            "analytics" to true
        )
    )

    fun getConfigForEnvironment(environment: String): ApplicationConfig {
        return baseConfig.createForEnvironment(environment)
    }
}