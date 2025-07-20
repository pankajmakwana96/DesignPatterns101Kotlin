package patterns.creational

/**
 * # Abstract Factory Pattern
 * 
 * ## Definition
 * Provides an interface for creating families of related or dependent objects
 * without specifying their concrete classes.
 * 
 * ## Problem it solves
 * - Need to create families of related objects
 * - Want to ensure objects in a family are used together
 * - Need to provide a library of products revealing only interfaces
 * - Want to configure a system with one of multiple families of products
 * 
 * ## When to use
 * - System should be independent of how its products are created
 * - System should be configured with one of multiple families of products
 * - Family of related products is designed to be used together
 * - You want to provide a library revealing only interfaces
 * 
 * ## When NOT to use
 * - When product families don't change
 * - When products are not related
 * - When flexibility of factory method is sufficient
 * 
 * ## Advantages
 * - Isolates concrete classes
 * - Makes exchanging product families easy
 * - Promotes consistency among products
 * - Code is more flexible and maintainable
 * 
 * ## Disadvantages
 * - Difficult to extend with new kinds of products
 * - Can be over-engineering for simple cases
 * - More complex than Factory Method
 */

// 1. GUI Component Example - Creating families of UI components
interface Button {
    fun render(): String
    fun onClick(): String
}

interface Checkbox {
    fun render(): String
    fun check(): String
    fun uncheck(): String
}

interface TextField {
    fun render(): String
    fun getText(): String
    fun setText(text: String): String
}

// Windows implementations
class WindowsButton : Button {
    override fun render(): String = "Rendering Windows-style button"
    override fun onClick(): String = "Windows button clicked with system sound"
}

class WindowsCheckbox : Checkbox {
    override fun render(): String = "Rendering Windows-style checkbox"
    override fun check(): String = "Windows checkbox checked"
    override fun uncheck(): String = "Windows checkbox unchecked"
}

class WindowsTextField : TextField {
    private var text: String = ""
    override fun render(): String = "Rendering Windows-style text field"
    override fun getText(): String = text
    override fun setText(text: String): String {
        this.text = text
        return "Windows text field updated: $text"
    }
}

// macOS implementations
class MacButton : Button {
    override fun render(): String = "Rendering macOS-style button with smooth animation"
    override fun onClick(): String = "macOS button clicked with haptic feedback"
}

class MacCheckbox : Checkbox {
    override fun render(): String = "Rendering macOS-style checkbox"
    override fun check(): String = "macOS checkbox checked with animation"
    override fun uncheck(): String = "macOS checkbox unchecked with animation"
}

class MacTextField : TextField {
    private var text: String = ""
    override fun render(): String = "Rendering macOS-style text field with rounded corners"
    override fun getText(): String = text
    override fun setText(text: String): String {
        this.text = text
        return "macOS text field updated: $text"
    }
}

// Linux implementations
class LinuxButton : Button {
    override fun render(): String = "Rendering Linux-style button"
    override fun onClick(): String = "Linux button clicked"
}

class LinuxCheckbox : Checkbox {
    override fun render(): String = "Rendering Linux-style checkbox"
    override fun check(): String = "Linux checkbox checked"
    override fun uncheck(): String = "Linux checkbox unchecked"
}

class LinuxTextField : TextField {
    private var text: String = ""
    override fun render(): String = "Rendering Linux-style text field"
    override fun getText(): String = text
    override fun setText(text: String): String {
        this.text = text
        return "Linux text field updated: $text"
    }
}

// Abstract Factory
interface GUIFactory {
    fun createButton(): Button
    fun createCheckbox(): Checkbox
    fun createTextField(): TextField
}

// Concrete Factories
class WindowsFactory : GUIFactory {
    override fun createButton(): Button = WindowsButton()
    override fun createCheckbox(): Checkbox = WindowsCheckbox()
    override fun createTextField(): TextField = WindowsTextField()
}

class MacFactory : GUIFactory {
    override fun createButton(): Button = MacButton()
    override fun createCheckbox(): Checkbox = MacCheckbox()
    override fun createTextField(): TextField = MacTextField()
}

class LinuxFactory : GUIFactory {
    override fun createButton(): Button = LinuxButton()
    override fun createCheckbox(): Checkbox = LinuxCheckbox()
    override fun createTextField(): TextField = LinuxTextField()
}

// Client application
class Application(private val factory: GUIFactory) {
    private val button = factory.createButton()
    private val checkbox = factory.createCheckbox()
    private val textField = factory.createTextField()
    
    fun renderUI(): List<String> = listOf(
        button.render(),
        checkbox.render(),
        textField.render()
    )
    
    fun interactWithUI(): List<String> = listOf(
        button.onClick(),
        checkbox.check(),
        textField.setText("Hello World!")
    )
}

// 2. Database Connection Factory - Supporting multiple database types
interface Connection {
    fun connect(): String
    fun executeQuery(query: String): String
    fun close(): String
}

interface ResultSet {
    fun next(): Boolean
    fun getString(column: String): String
    fun getInt(column: String): Int
}

interface PreparedStatement {
    fun setString(index: Int, value: String)
    fun setInt(index: Int, value: Int)
    fun execute(): String
}

// MySQL implementations
class MySQLConnectionAF : Connection {
    override fun connect(): String = "Connected to MySQL database"
    override fun executeQuery(query: String): String = "MySQL: Executing $query"
    override fun close(): String = "MySQL connection closed"
}

class MySQLResultSet(private val data: Map<String, Any>) : ResultSet {
    private var position = 0
    override fun next(): Boolean = position++ < 1
    override fun getString(column: String): String = data[column]?.toString() ?: ""
    override fun getInt(column: String): Int = data[column] as? Int ?: 0
}

class MySQLPreparedStatement : PreparedStatement {
    private val parameters = mutableMapOf<Int, Any>()
    override fun setString(index: Int, value: String) { parameters[index] = value }
    override fun setInt(index: Int, value: Int) { parameters[index] = value }
    override fun execute(): String = "MySQL: Executing prepared statement with $parameters"
}

// PostgreSQL implementations
class PostgreSQLConnectionAF : Connection {
    override fun connect(): String = "Connected to PostgreSQL database"
    override fun executeQuery(query: String): String = "PostgreSQL: Executing $query"
    override fun close(): String = "PostgreSQL connection closed"
}

class PostgreSQLResultSet(private val data: Map<String, Any>) : ResultSet {
    private var position = 0
    override fun next(): Boolean = position++ < 1
    override fun getString(column: String): String = data[column]?.toString() ?: ""
    override fun getInt(column: String): Int = data[column] as? Int ?: 0
}

class PostgreSQLPreparedStatement : PreparedStatement {
    private val parameters = mutableMapOf<Int, Any>()
    override fun setString(index: Int, value: String) { parameters[index] = value }
    override fun setInt(index: Int, value: Int) { parameters[index] = value }
    override fun execute(): String = "PostgreSQL: Executing prepared statement with $parameters"
}

// Abstract Database Factory
interface DatabaseFactory {
    fun createConnection(): Connection
    fun createResultSet(data: Map<String, Any>): ResultSet
    fun createPreparedStatement(): PreparedStatement
}

class MySQLFactory : DatabaseFactory {
    override fun createConnection(): Connection = MySQLConnectionAF()
    override fun createResultSet(data: Map<String, Any>): ResultSet = MySQLResultSet(data)
    override fun createPreparedStatement(): PreparedStatement = MySQLPreparedStatement()
}

class PostgreSQLFactory : DatabaseFactory {
    override fun createConnection(): Connection = PostgreSQLConnectionAF()
    override fun createResultSet(data: Map<String, Any>): ResultSet = PostgreSQLResultSet(data)
    override fun createPreparedStatement(): PreparedStatement = PostgreSQLPreparedStatement()
}

// 3. Theme Factory - Creating cohesive UI themes
interface Theme {
    val primaryColor: String
    val secondaryColor: String
    val backgroundColor: String
    val textColor: String
    val fontFamily: String
}

interface ComponentTheme {
    fun getButtonStyle(): String
    fun getInputStyle(): String
    fun getCardStyle(): String
}

interface IconTheme {
    fun getMenuIcon(): String
    fun getCloseIcon(): String
    fun getSearchIcon(): String
}

// Dark theme implementations
class DarkTheme : Theme {
    override val primaryColor = "#BB86FC"
    override val secondaryColor = "#03DAC6"
    override val backgroundColor = "#121212"
    override val textColor = "#FFFFFF"
    override val fontFamily = "Roboto"
}

class DarkComponentTheme : ComponentTheme {
    override fun getButtonStyle(): String = "dark-button: purple background, white text"
    override fun getInputStyle(): String = "dark-input: black background, white text, purple border"
    override fun getCardStyle(): String = "dark-card: dark gray background, rounded corners"
}

class DarkIconTheme : IconTheme {
    override fun getMenuIcon(): String = "dark-menu-icon: white hamburger lines"
    override fun getCloseIcon(): String = "dark-close-icon: white X"
    override fun getSearchIcon(): String = "dark-search-icon: white magnifying glass"
}

// Light theme implementations
class LightTheme : Theme {
    override val primaryColor = "#6200EE"
    override val secondaryColor = "#018786"
    override val backgroundColor = "#FFFFFF"
    override val textColor = "#000000"
    override val fontFamily = "Roboto"
}

class LightComponentTheme : ComponentTheme {
    override fun getButtonStyle(): String = "light-button: blue background, white text"
    override fun getInputStyle(): String = "light-input: white background, black text, gray border"
    override fun getCardStyle(): String = "light-card: white background, subtle shadow"
}

class LightIconTheme : IconTheme {
    override fun getMenuIcon(): String = "light-menu-icon: black hamburger lines"
    override fun getCloseIcon(): String = "light-close-icon: black X"
    override fun getSearchIcon(): String = "light-search-icon: black magnifying glass"
}

// Theme Abstract Factory
interface ThemeFactory {
    fun createTheme(): Theme
    fun createComponentTheme(): ComponentTheme
    fun createIconTheme(): IconTheme
}

class DarkThemeFactory : ThemeFactory {
    override fun createTheme(): Theme = DarkTheme()
    override fun createComponentTheme(): ComponentTheme = DarkComponentTheme()
    override fun createIconTheme(): IconTheme = DarkIconTheme()
}

class LightThemeFactory : ThemeFactory {
    override fun createTheme(): Theme = LightTheme()
    override fun createComponentTheme(): ComponentTheme = LightComponentTheme()
    override fun createIconTheme(): IconTheme = LightIconTheme()
}

// 4. Kotlin-specific approach using sealed classes and object expressions
sealed class Platform {
    object Android : Platform()
    object iOS : Platform()
    object Web : Platform()
}

// Using object expressions for factory pattern
object PlatformFactory {
    fun createGUIFactory(platform: Platform): GUIFactory = when (platform) {
        Platform.Android -> object : GUIFactory {
            override fun createButton(): Button = object : Button {
                override fun render(): String = "Rendering Android Material button"
                override fun onClick(): String = "Android button clicked with ripple effect"
            }
            override fun createCheckbox(): Checkbox = object : Checkbox {
                override fun render(): String = "Rendering Android Material checkbox"
                override fun check(): String = "Android checkbox checked"
                override fun uncheck(): String = "Android checkbox unchecked"
            }
            override fun createTextField(): TextField = object : TextField {
                private var text: String = ""
                override fun render(): String = "Rendering Android Material text field"
                override fun getText(): String = text
                override fun setText(text: String): String {
                    this.text = text
                    return "Android text field updated: $text"
                }
            }
        }
        
        Platform.iOS -> object : GUIFactory {
            override fun createButton(): Button = object : Button {
                override fun render(): String = "Rendering iOS button with rounded corners"
                override fun onClick(): String = "iOS button clicked with haptic feedback"
            }
            override fun createCheckbox(): Checkbox = object : Checkbox {
                override fun render(): String = "Rendering iOS switch (iOS doesn't use checkboxes)"
                override fun check(): String = "iOS switch turned on"
                override fun uncheck(): String = "iOS switch turned off"
            }
            override fun createTextField(): TextField = object : TextField {
                private var text: String = ""
                override fun render(): String = "Rendering iOS text field with clear button"
                override fun getText(): String = text
                override fun setText(text: String): String {
                    this.text = text
                    return "iOS text field updated: $text"
                }
            }
        }
        
        Platform.Web -> object : GUIFactory {
            override fun createButton(): Button = object : Button {
                override fun render(): String = "Rendering HTML button with CSS styling"
                override fun onClick(): String = "Web button clicked - JavaScript event fired"
            }
            override fun createCheckbox(): Checkbox = object : Checkbox {
                override fun render(): String = "Rendering HTML checkbox input"
                override fun check(): String = "Web checkbox checked - DOM updated"
                override fun uncheck(): String = "Web checkbox unchecked - DOM updated"
            }
            override fun createTextField(): TextField = object : TextField {
                private var text: String = ""
                override fun render(): String = "Rendering HTML input field"
                override fun getText(): String = text
                override fun setText(text: String): String {
                    this.text = text
                    return "Web text field updated: $text"
                }
            }
        }
    }
}