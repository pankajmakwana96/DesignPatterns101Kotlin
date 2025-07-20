package patterns.creational

/**
 * # Builder Pattern
 * 
 * ## Definition
 * Constructs complex objects step by step. Allows you to produce different types
 * and representations of an object using the same construction code.
 * 
 * ## Problem it solves
 * - Complex object creation with many optional parameters
 * - Need different representations of the same object
 * - Want to construct objects step-by-step
 * - Avoid telescoping constructor anti-pattern
 * 
 * ## When to use
 * - Object has many optional parameters
 * - Want to create different representations of the same object
 * - Need to construct objects step by step
 * - Want to make object creation more readable
 * 
 * ## When NOT to use
 * - Object is simple with few parameters
 * - Object doesn't need different representations
 * - Construction is straightforward
 * 
 * ## Advantages
 * - Provides fine control over construction process
 * - Allows different representations of object
 * - Isolates code for construction and representation
 * - More readable than telescoping constructors
 * 
 * ## Disadvantages
 * - More complex than simple constructors
 * - May be overkill for simple objects
 * - Requires additional builder classes
 */

// 1. Classic Builder Pattern - Computer Configuration
data class Computer(
    val cpu: String,
    val ram: String,
    val storage: String,
    val gpu: String? = null,
    val motherboard: String? = null,
    val powerSupply: String? = null,
    val coolingSystem: String? = null,
    val caseType: String? = null,
    val isGamingPC: Boolean = false,
    val isWorkstation: Boolean = false
)

class ComputerBuilder {
    private var cpu: String = ""
    private var ram: String = ""
    private var storage: String = ""
    private var gpu: String? = null
    private var motherboard: String? = null
    private var powerSupply: String? = null
    private var coolingSystem: String? = null
    private var caseType: String? = null
    private var isGamingPC: Boolean = false
    private var isWorkstation: Boolean = false
    
    fun cpu(cpu: String) = apply { this.cpu = cpu }
    fun ram(ram: String) = apply { this.ram = ram }
    fun storage(storage: String) = apply { this.storage = storage }
    fun gpu(gpu: String) = apply { this.gpu = gpu }
    fun motherboard(motherboard: String) = apply { this.motherboard = motherboard }
    fun powerSupply(powerSupply: String) = apply { this.powerSupply = powerSupply }
    fun coolingSystem(coolingSystem: String) = apply { this.coolingSystem = coolingSystem }
    fun caseType(caseType: String) = apply { this.caseType = caseType }
    fun gamingPC() = apply { this.isGamingPC = true }
    fun workstation() = apply { this.isWorkstation = true }
    
    fun build(): Computer {
        require(cpu.isNotEmpty()) { "CPU is required" }
        require(ram.isNotEmpty()) { "RAM is required" }
        require(storage.isNotEmpty()) { "Storage is required" }
        
        return Computer(
            cpu, ram, storage, gpu, motherboard, powerSupply,
            coolingSystem, caseType, isGamingPC, isWorkstation
        )
    }
}

// Director class for common configurations
class ComputerDirector(private val builder: ComputerBuilder) {
    fun buildGamingPC(): Computer {
        return builder
            .cpu("Intel Core i9-13900K")
            .ram("32GB DDR4-3200")
            .storage("1TB NVMe SSD")
            .gpu("NVIDIA RTX 4080")
            .motherboard("ASUS ROG Strix Z790-E")
            .powerSupply("850W 80+ Gold")
            .coolingSystem("AIO Liquid Cooler")
            .caseType("Mid Tower RGB")
            .gamingPC()
            .build()
    }
    
    fun buildWorkstation(): Computer {
        return builder
            .cpu("AMD Ryzen 9 7950X")
            .ram("64GB DDR5-5600")
            .storage("2TB NVMe SSD")
            .gpu("NVIDIA RTX A4000")
            .motherboard("ASUS Pro WS X670E-ACE")
            .powerSupply("1000W 80+ Platinum")
            .coolingSystem("Tower Air Cooler")
            .caseType("Full Tower")
            .workstation()
            .build()
    }
    
    fun buildBudgetPC(): Computer {
        return builder
            .cpu("AMD Ryzen 5 5600G")
            .ram("16GB DDR4-3200")
            .storage("500GB SATA SSD")
            .motherboard("MSI B450M Pro-B")
            .powerSupply("500W 80+ Bronze")
            .caseType("Micro ATX")
            .build()
    }
}

// 2. Kotlin DSL Builder - HTTP Request Builder
data class HttpRequest(
    val url: String,
    val method: String = "GET",
    val headers: Map<String, String> = emptyMap(),
    val queryParams: Map<String, String> = emptyMap(),
    val body: String? = null,
    val timeout: Long = 30000,
    val followRedirects: Boolean = true
)

class HttpRequestBuilder {
    private var url: String = ""
    private var method: String = "GET"
    private val headers = mutableMapOf<String, String>()
    private val queryParams = mutableMapOf<String, String>()
    private var body: String? = null
    private var timeout: Long = 30000
    private var followRedirects: Boolean = true
    
    fun url(url: String) = apply { this.url = url }
    fun method(method: String) = apply { this.method = method }
    fun header(name: String, value: String) = apply { headers[name] = value }
    fun headers(headers: Map<String, String>) = apply { this.headers.putAll(headers) }
    fun queryParam(name: String, value: String) = apply { queryParams[name] = value }
    fun queryParams(params: Map<String, String>) = apply { this.queryParams.putAll(params) }
    fun body(body: String) = apply { this.body = body }
    fun timeout(timeout: Long) = apply { this.timeout = timeout }
    fun followRedirects(follow: Boolean) = apply { this.followRedirects = follow }
    
    // DSL methods
    fun headers(block: MutableMap<String, String>.() -> Unit) = apply { headers.block() }
    fun queryParams(block: MutableMap<String, String>.() -> Unit) = apply { queryParams.block() }
    
    fun build(): HttpRequest {
        require(url.isNotEmpty()) { "URL is required" }
        return HttpRequest(url, method, headers.toMap(), queryParams.toMap(), body, timeout, followRedirects)
    }
}

// DSL functions for common HTTP methods
fun httpRequest(block: HttpRequestBuilder.() -> Unit): HttpRequest {
    return HttpRequestBuilder().apply(block).build()
}

fun get(url: String, block: HttpRequestBuilder.() -> Unit = {}): HttpRequest {
    return HttpRequestBuilder().apply {
        url(url)
        method("GET")
        block()
    }.build()
}

fun post(url: String, block: HttpRequestBuilder.() -> Unit = {}): HttpRequest {
    return HttpRequestBuilder().apply {
        url(url)
        method("POST")
        block()
    }.build()
}

// 3. Fluent Builder with Validation - Database Query Builder
data class Query(
    val select: List<String>,
    val from: String,
    val joins: List<String>,
    val where: List<String>,
    val groupBy: List<String>,
    val having: List<String>,
    val orderBy: List<String>,
    val limit: Int?
) {
    fun toSQL(): String {
        val sql = StringBuilder()
        sql.append("SELECT ${select.joinToString(", ")}")
        sql.append(" FROM $from")
        
        if (joins.isNotEmpty()) {
            sql.append(" ${joins.joinToString(" ")}")
        }
        
        if (where.isNotEmpty()) {
            sql.append(" WHERE ${where.joinToString(" AND ")}")
        }
        
        if (groupBy.isNotEmpty()) {
            sql.append(" GROUP BY ${groupBy.joinToString(", ")}")
        }
        
        if (having.isNotEmpty()) {
            sql.append(" HAVING ${having.joinToString(" AND ")}")
        }
        
        if (orderBy.isNotEmpty()) {
            sql.append(" ORDER BY ${orderBy.joinToString(", ")}")
        }
        
        limit?.let { sql.append(" LIMIT $it") }
        
        return sql.toString()
    }
}

class QueryBuilder {
    private val select = mutableListOf<String>()
    private var from: String = ""
    private val joins = mutableListOf<String>()
    private val where = mutableListOf<String>()
    private val groupBy = mutableListOf<String>()
    private val having = mutableListOf<String>()
    private val orderBy = mutableListOf<String>()
    private var limit: Int? = null
    
    fun select(vararg columns: String) = apply { select.addAll(columns) }
    fun select(columns: List<String>) = apply { select.addAll(columns) }
    fun from(table: String) = apply { this.from = table }
    
    fun innerJoin(table: String, on: String) = apply { 
        joins.add("INNER JOIN $table ON $on") 
    }
    
    fun leftJoin(table: String, on: String) = apply { 
        joins.add("LEFT JOIN $table ON $on") 
    }
    
    fun rightJoin(table: String, on: String) = apply { 
        joins.add("RIGHT JOIN $table ON $on") 
    }
    
    fun where(condition: String) = apply { where.add(condition) }
    fun groupBy(vararg columns: String) = apply { groupBy.addAll(columns) }
    fun having(condition: String) = apply { having.add(condition) }
    fun orderBy(column: String, direction: String = "ASC") = apply { 
        orderBy.add("$column $direction") 
    }
    fun limit(count: Int) = apply { this.limit = count }
    
    fun build(): Query {
        require(select.isNotEmpty()) { "SELECT clause is required" }
        require(from.isNotEmpty()) { "FROM clause is required" }
        
        return Query(
            select.toList(), from, joins.toList(), where.toList(),
            groupBy.toList(), having.toList(), orderBy.toList(), limit
        )
    }
}

// DSL function for query building
fun query(block: QueryBuilder.() -> Unit): Query {
    return QueryBuilder().apply(block).build()
}

// 4. Generic Builder Pattern
abstract class GenericBuilder<T> {
    abstract fun build(): T
    
    protected inline fun <R> R.applyIf(condition: Boolean, block: R.() -> R): R {
        return if (condition) block() else this
    }
}

// Example: Email builder
data class Email(
    val to: List<String>,
    val cc: List<String>,
    val bcc: List<String>,
    val subject: String,
    val body: String,
    val isHtml: Boolean,
    val attachments: List<String>,
    val priority: Priority
) {
    enum class Priority { LOW, NORMAL, HIGH }
}

class EmailBuilder : GenericBuilder<Email>() {
    private val to = mutableListOf<String>()
    private val cc = mutableListOf<String>()
    private val bcc = mutableListOf<String>()
    private var subject: String = ""
    private var body: String = ""
    private var isHtml: Boolean = false
    private val attachments = mutableListOf<String>()
    private var priority: Email.Priority = Email.Priority.NORMAL
    
    fun to(email: String) = apply { to.add(email) }
    fun to(emails: List<String>) = apply { to.addAll(emails) }
    fun cc(email: String) = apply { cc.add(email) }
    fun bcc(email: String) = apply { bcc.add(email) }
    fun subject(subject: String) = apply { this.subject = subject }
    fun body(body: String) = apply { this.body = body }
    fun htmlBody(body: String) = apply { 
        this.body = body
        this.isHtml = true
    }
    fun attachment(path: String) = apply { attachments.add(path) }
    fun priority(priority: Email.Priority) = apply { this.priority = priority }
    fun highPriority() = apply { this.priority = Email.Priority.HIGH }
    fun lowPriority() = apply { this.priority = Email.Priority.LOW }
    
    override fun build(): Email {
        require(to.isNotEmpty()) { "At least one recipient is required" }
        require(subject.isNotEmpty()) { "Subject is required" }
        require(body.isNotEmpty()) { "Body is required" }
        
        return Email(
            to.toList(), cc.toList(), bcc.toList(), subject, body,
            isHtml, attachments.toList(), priority
        )
    }
}

// DSL function for email building
fun email(block: EmailBuilder.() -> Unit): Email {
    return EmailBuilder().apply(block).build()
}

// 5. Step Builder Pattern - Ensures required fields are set in order
interface CPUStep {
    fun cpu(cpu: String): RAMStep
}

interface RAMStep {
    fun ram(ram: String): StorageStep
}

interface StorageStep {
    fun storage(storage: String): OptionalStep
}

interface OptionalStep {
    fun gpu(gpu: String): OptionalStep
    fun motherboard(motherboard: String): OptionalStep
    fun powerSupply(powerSupply: String): OptionalStep
    fun build(): Computer
}

class StepComputerBuilder private constructor() : CPUStep, RAMStep, StorageStep, OptionalStep {
    companion object {
        fun newBuilder(): CPUStep = StepComputerBuilder()
    }
    
    private var cpu: String = ""
    private var ram: String = ""
    private var storage: String = ""
    private var gpu: String? = null
    private var motherboard: String? = null
    private var powerSupply: String? = null
    
    override fun cpu(cpu: String): RAMStep {
        this.cpu = cpu
        return this
    }
    
    override fun ram(ram: String): StorageStep {
        this.ram = ram
        return this
    }
    
    override fun storage(storage: String): OptionalStep {
        this.storage = storage
        return this
    }
    
    override fun gpu(gpu: String): OptionalStep {
        this.gpu = gpu
        return this
    }
    
    override fun motherboard(motherboard: String): OptionalStep {
        this.motherboard = motherboard
        return this
    }
    
    override fun powerSupply(powerSupply: String): OptionalStep {
        this.powerSupply = powerSupply
        return this
    }
    
    override fun build(): Computer {
        return Computer(cpu, ram, storage, gpu, motherboard, powerSupply)
    }
}