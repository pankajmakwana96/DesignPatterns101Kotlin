package patterns.structural

/**
 * # Composite Pattern
 * 
 * ## Definition
 * Composes objects into tree structures to represent part-whole hierarchies.
 * Lets clients treat individual objects and compositions of objects uniformly.
 * 
 * ## Problem it solves
 * - Need to represent hierarchical data structures
 * - Want to treat individual objects and groups of objects the same way
 * - Need to perform operations on complex tree structures
 * - Want to add new types of components without changing existing code
 * 
 * ## When to use
 * - Want to represent part-whole hierarchies of objects
 * - Want clients to treat individual and composite objects uniformly
 * - Tree structures with recursive composition
 * - When the core model can be represented as a tree
 * 
 * ## When NOT to use
 * - Structure is not hierarchical
 * - Operations on individual and composite objects are very different
 * - Performance is critical and traversal overhead is too high
 * 
 * ## Advantages
 * - Makes client code simple
 * - Easy to add new types of components
 * - Provides structure flexibility
 * - Allows recursive composition
 * 
 * ## Disadvantages
 * - Can make design overly general
 * - Difficult to restrict component types
 * - May have performance overhead
 * - Type checking happens at runtime
 */

// 1. Classic Composite Pattern - File System

// Component interface
interface FileSystemComponent {
    val name: String
    fun getSize(): Long
    fun getPath(): String
    fun display(indent: String = ""): String
    fun search(query: String): List<FileSystemComponent>
    fun copy(): FileSystemComponent
}

// Leaf
class File(
    override val name: String,
    private val size: Long,
    private val content: String = ""
) : FileSystemComponent {
    
    override fun getSize(): Long = size
    
    override fun getPath(): String = name
    
    override fun display(indent: String): String {
        return "$indent📄 $name (${size} bytes)"
    }
    
    override fun search(query: String): List<FileSystemComponent> {
        return if (name.contains(query, ignoreCase = true) || content.contains(query, ignoreCase = true)) {
            listOf(this)
        } else {
            emptyList()
        }
    }
    
    override fun copy(): FileSystemComponent = File(name, size, content)
    
    fun getContent(): String = content
}

// Composite
class Directory(
    override val name: String,
    private val parent: Directory? = null
) : FileSystemComponent {
    private val children = mutableListOf<FileSystemComponent>()
    
    fun addComponent(component: FileSystemComponent) {
        children.add(component)
    }
    
    fun removeComponent(component: FileSystemComponent) {
        children.remove(component)
    }
    
    fun getChildren(): List<FileSystemComponent> = children.toList()
    
    override fun getSize(): Long = children.sumOf { it.getSize() }
    
    override fun getPath(): String {
        return if (parent != null) {
            "${parent.getPath()}/$name"
        } else {
            name
        }
    }
    
    override fun display(indent: String): String {
        val result = StringBuilder("$indent📁 $name/ (${getSize()} bytes)\n")
        children.forEach { child ->
            result.append(child.display("$indent  "))
            result.append("\n")
        }
        return result.toString().trimEnd()
    }
    
    override fun search(query: String): List<FileSystemComponent> {
        val results = mutableListOf<FileSystemComponent>()
        
        // Check if this directory matches
        if (name.contains(query, ignoreCase = true)) {
            results.add(this)
        }
        
        // Search children
        children.forEach { child ->
            results.addAll(child.search(query))
        }
        
        return results
    }
    
    override fun copy(): FileSystemComponent {
        val newDirectory = Directory(name, parent)
        children.forEach { child ->
            newDirectory.addComponent(child.copy())
        }
        return newDirectory
    }
    
    fun findByName(name: String): FileSystemComponent? {
        return search(name).firstOrNull { it.name == name }
    }
    
    fun getFileCount(): Int {
        return children.sumOf { child ->
            when (child) {
                is File -> 1
                is Directory -> child.getFileCount()
                else -> 0
            }
        }
    }
    
    fun getDirectoryCount(): Int {
        return children.sumOf { child ->
            when (child) {
                is File -> 0
                is Directory -> 1 + child.getDirectoryCount()
                else -> 0
            }
        }
    }
}

// 2. UI Component Hierarchy using Composite Pattern

// Component interface
interface UIComponent {
    val id: String
    fun render(): String
    fun handleEvent(event: String): String
    fun getChildren(): List<UIComponent>
    fun findById(id: String): UIComponent?
    fun addEventHandler(event: String, handler: (String) -> String)
}

// Leaf components
class Button(
    override val id: String,
    private val text: String,
    private val enabled: Boolean = true
) : UIComponent {
    private val eventHandlers = mutableMapOf<String, (String) -> String>()
    
    override fun render(): String = 
        "<button id='$id' ${if (!enabled) "disabled" else ""}>$text</button>"
    
    override fun handleEvent(event: String): String {
        return if (enabled && eventHandlers.containsKey(event)) {
            eventHandlers[event]?.invoke(id) ?: "No handler for $event"
        } else {
            "Button $id cannot handle $event"
        }
    }
    
    override fun getChildren(): List<UIComponent> = emptyList()
    
    override fun findById(id: String): UIComponent? = if (this.id == id) this else null
    
    override fun addEventHandler(event: String, handler: (String) -> String) {
        eventHandlers[event] = handler
    }
    
    fun click(): String = handleEvent("click")
}

class Label(
    override val id: String,
    private val text: String
) : UIComponent {
    override fun render(): String = "<label id='$id'>$text</label>"
    
    override fun handleEvent(event: String): String = "Label $id does not handle events"
    
    override fun getChildren(): List<UIComponent> = emptyList()
    
    override fun findById(id: String): UIComponent? = if (this.id == id) this else null
    
    override fun addEventHandler(event: String, handler: (String) -> String) {
        // Labels typically don't handle events
    }
}

class TextInput(
    override val id: String,
    private var value: String = "",
    private val placeholder: String = ""
) : UIComponent {
    private val eventHandlers = mutableMapOf<String, (String) -> String>()
    
    override fun render(): String = 
        "<input id='$id' type='text' value='$value' placeholder='$placeholder'/>"
    
    override fun handleEvent(event: String): String {
        return eventHandlers[event]?.invoke(id) ?: "TextInput $id: $event event occurred"
    }
    
    override fun getChildren(): List<UIComponent> = emptyList()
    
    override fun findById(id: String): UIComponent? = if (this.id == id) this else null
    
    override fun addEventHandler(event: String, handler: (String) -> String) {
        eventHandlers[event] = handler
    }
    
    fun setValue(newValue: String) {
        value = newValue
        handleEvent("change")
    }
    
    fun getValue(): String = value
}

// Composite components
class Panel(
    override val id: String,
    private val cssClass: String = ""
) : UIComponent {
    private val children = mutableListOf<UIComponent>()
    private val eventHandlers = mutableMapOf<String, (String) -> String>()
    
    fun addChild(component: UIComponent) {
        children.add(component)
    }
    
    fun removeChild(component: UIComponent) {
        children.remove(component)
    }
    
    override fun render(): String {
        val childrenHtml = children.joinToString("\n  ") { it.render() }
        val classAttr = if (cssClass.isNotEmpty()) " class='$cssClass'" else ""
        return "<div id='$id'$classAttr>\n  $childrenHtml\n</div>"
    }
    
    override fun handleEvent(event: String): String {
        val results = mutableListOf<String>()
        
        // Handle own events
        eventHandlers[event]?.let { handler ->
            results.add(handler(id))
        }
        
        // Propagate to children
        children.forEach { child ->
            val result = child.handleEvent(event)
            if (result.isNotEmpty()) results.add(result)
        }
        
        return results.joinToString("; ")
    }
    
    override fun getChildren(): List<UIComponent> = children.toList()
    
    override fun findById(id: String): UIComponent? {
        if (this.id == id) return this
        
        children.forEach { child ->
            child.findById(id)?.let { return it }
        }
        
        return null
    }
    
    override fun addEventHandler(event: String, handler: (String) -> String) {
        eventHandlers[event] = handler
    }
}

class Form(
    override val id: String,
    private val action: String = "",
    private val method: String = "POST"
) : UIComponent {
    private val children = mutableListOf<UIComponent>()
    private val eventHandlers = mutableMapOf<String, (String) -> String>()
    
    fun addField(component: UIComponent) {
        children.add(component)
    }
    
    fun removeField(component: UIComponent) {
        children.remove(component)
    }
    
    override fun render(): String {
        val fieldsHtml = children.joinToString("\n  ") { it.render() }
        return "<form id='$id' action='$action' method='$method'>\n  $fieldsHtml\n</form>"
    }
    
    override fun handleEvent(event: String): String {
        return when (event) {
            "submit" -> {
                val formData = collectFormData()
                eventHandlers[event]?.invoke(formData) ?: "Form $id submitted with data: $formData"
            }
            else -> {
                eventHandlers[event]?.invoke(id) ?: children.joinToString("; ") { it.handleEvent(event) }
            }
        }
    }
    
    override fun getChildren(): List<UIComponent> = children.toList()
    
    override fun findById(id: String): UIComponent? {
        if (this.id == id) return this
        
        children.forEach { child ->
            child.findById(id)?.let { return it }
        }
        
        return null
    }
    
    override fun addEventHandler(event: String, handler: (String) -> String) {
        eventHandlers[event] = handler
    }
    
    private fun collectFormData(): String {
        val data = mutableListOf<String>()
        children.forEach { child ->
            when (child) {
                is TextInput -> data.add("${child.id}=${child.getValue()}")
                // Add other input types as needed
            }
        }
        return data.joinToString("&")
    }
}

// 3. Menu System using Composite Pattern

// Component interface
interface MenuComponent {
    val name: String
    fun execute(): String
    fun getDescription(): String
    fun getChildren(): List<MenuComponent>
    fun display(indent: String = ""): String
    fun findByName(name: String): MenuComponent?
}

// Leaf - Menu Item
class MenuItem(
    override val name: String,
    private val description: String,
    private val action: () -> String
) : MenuComponent {
    
    override fun execute(): String = action.invoke()
    
    override fun getDescription(): String = description
    
    override fun getChildren(): List<MenuComponent> = emptyList()
    
    override fun display(indent: String): String = "$indent• $name - $description"
    
    override fun findByName(name: String): MenuComponent? = 
        if (this.name.equals(name, ignoreCase = true)) this else null
}

// Composite - Menu/Submenu
class Menu(
    override val name: String,
    private val description: String = ""
) : MenuComponent {
    private val items = mutableListOf<MenuComponent>()
    
    fun addItem(item: MenuComponent) {
        items.add(item)
    }
    
    fun removeItem(item: MenuComponent) {
        items.remove(item)
    }
    
    override fun execute(): String {
        return "Opening menu: $name\n${display()}"
    }
    
    override fun getDescription(): String = description.ifEmpty() { "Menu: $name" }
    
    override fun getChildren(): List<MenuComponent> = items.toList()
    
    override fun display(indent: String): String {
        val result = StringBuilder("$indent▼ $name")
        if (description.isNotEmpty()) {
            result.append(" - $description")
        }
        result.append("\n")
        
        items.forEach { item ->
            result.append(item.display("$indent  "))
            result.append("\n")
        }
        
        return result.toString().trimEnd()
    }
    
    override fun findByName(name: String): MenuComponent? {
        if (this.name.equals(name, ignoreCase = true)) return this
        
        items.forEach { item ->
            item.findByName(name)?.let { return it }
        }
        
        return null
    }
    
    fun executeItem(itemName: String): String {
        return findByName(itemName)?.execute() ?: "Menu item '$itemName' not found"
    }
    
    fun getItemCount(): Int = items.size
    
    fun getTotalItemCount(): Int {
        return items.sumOf { item ->
            when (item) {
                is MenuItem -> 1
                is Menu -> item.getTotalItemCount()
                else -> 0
            }
        }
    }
}

// 4. Organization Hierarchy using Composite Pattern

// Component interface
interface OrganizationComponent {
    val name: String
    val position: String
    fun getSalary(): Double
    fun getEmployeeCount(): Int
    fun getInfo(): String
    fun getSubordinates(): List<OrganizationComponent>
    fun findEmployee(name: String): OrganizationComponent?
    fun calculateTotalSalary(): Double
}

// Leaf - Individual Employee
class Employee(
    override val name: String,
    override val position: String,
    private val salary: Double,
    private val department: String,
    private val skills: List<String> = emptyList()
) : OrganizationComponent {
    
    override fun getSalary(): Double = salary
    
    override fun getEmployeeCount(): Int = 1
    
    override fun getInfo(): String = 
        "$name - $position ($department) - $${String.format("%.2f", salary)} - Skills: ${skills.joinToString(", ")}"
    
    override fun getSubordinates(): List<OrganizationComponent> = emptyList()
    
    override fun findEmployee(name: String): OrganizationComponent? = 
        if (this.name.equals(name, ignoreCase = true)) this else null
    
    override fun calculateTotalSalary(): Double = salary
    
    fun getSkills(): List<String> = skills.toList()
    
    fun getDepartment(): String = department
}

// Composite - Manager with subordinates
class Manager(
    override val name: String,
    override val position: String,
    private val salary: Double,
    private val department: String
) : OrganizationComponent {
    private val subordinates = mutableListOf<OrganizationComponent>()
    
    fun addSubordinate(employee: OrganizationComponent) {
        subordinates.add(employee)
    }
    
    fun removeSubordinate(employee: OrganizationComponent) {
        subordinates.remove(employee)
    }
    
    override fun getSalary(): Double = salary
    
    override fun getEmployeeCount(): Int = 1 + subordinates.sumOf { it.getEmployeeCount() }
    
    override fun getInfo(): String {
        val result = StringBuilder("$name - $position ($department) - $${String.format("%.2f", salary)}")
        result.append("\n  Manages ${subordinates.size} direct reports:")
        subordinates.forEach { subordinate ->
            result.append("\n    ${subordinate.getInfo()}")
        }
        return result.toString()
    }
    
    override fun getSubordinates(): List<OrganizationComponent> = subordinates.toList()
    
    override fun findEmployee(name: String): OrganizationComponent? {
        if (this.name.equals(name, ignoreCase = true)) return this
        
        subordinates.forEach { subordinate ->
            subordinate.findEmployee(name)?.let { return it }
        }
        
        return null
    }
    
    override fun calculateTotalSalary(): Double = 
        salary + subordinates.sumOf { it.calculateTotalSalary() }
    
    fun getDirectReports(): List<OrganizationComponent> = subordinates.toList()
    
    fun getDepartmentEmployees(): List<OrganizationComponent> {
        val employees = mutableListOf<OrganizationComponent>()
        if (subordinates.isNotEmpty()) {
            employees.add(this)
            subordinates.forEach { subordinate ->
                when (subordinate) {
                    is Employee -> employees.add(subordinate)
                    is Manager -> employees.addAll(subordinate.getDepartmentEmployees())
                }
            }
        }
        return employees
    }
    
    fun getManagementLevels(): Int {
        return if (subordinates.isEmpty()) {
            1
        } else {
            1 + subordinates.maxOfOrNull { subordinate ->
                when (subordinate) {
                    is Manager -> subordinate.getManagementLevels()
                    else -> 0
                }
            }!!
        }
    }
}