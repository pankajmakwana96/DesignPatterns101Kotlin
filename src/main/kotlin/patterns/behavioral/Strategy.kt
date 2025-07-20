package patterns.behavioral

/**
 * # Strategy Pattern
 * 
 * ## Definition
 * Defines a family of algorithms, encapsulates each one, and makes them interchangeable.
 * Lets the algorithm vary independently from clients that use it.
 * 
 * ## Problem it solves
 * - Multiple ways to perform a task
 * - Want to switch algorithms at runtime
 * - Avoid conditional statements for algorithm selection
 * - Make algorithms reusable across different contexts
 * 
 * ## When to use
 * - Multiple ways to perform a task
 * - Need to switch algorithms at runtime
 * - Want to eliminate conditional statements
 * - Need to isolate algorithm implementation
 * 
 * ## When NOT to use
 * - Only one way to perform the task
 * - Algorithms are unlikely to change
 * - Simple conditional logic is sufficient
 * 
 * ## Advantages
 * - Algorithms are interchangeable
 * - Easy to add new strategies
 * - Eliminates conditional statements
 * - Runtime algorithm switching
 * 
 * ## Disadvantages
 * - Increased number of classes
 * - Client must know about strategies
 * - Communication overhead between strategy and context
 */

// 1. Payment Processing Strategy

interface PaymentStrategy {
    fun pay(amount: Double): PaymentResult
    fun validatePayment(amount: Double): Boolean
    fun getPaymentMethod(): String
}

data class PaymentResult(
    val success: Boolean,
    val transactionId: String?,
    val message: String,
    val fees: Double = 0.0
)

class CreditCardStrategy(
    private val cardNumber: String,
    private val expiryDate: String,
    private val cvv: String
) : PaymentStrategy {
    
    override fun pay(amount: Double): PaymentResult {
        return if (validatePayment(amount)) {
            val fees = amount * 0.029 // 2.9% processing fee
            PaymentResult(
                success = true,
                transactionId = "CC_${System.currentTimeMillis()}",
                message = "Credit card payment successful",
                fees = fees
            )
        } else {
            PaymentResult(
                success = false,
                transactionId = null,
                message = "Credit card payment failed"
            )
        }
    }
    
    override fun validatePayment(amount: Double): Boolean {
        return amount > 0 && cardNumber.length == 16 && cvv.length == 3
    }
    
    override fun getPaymentMethod(): String = "Credit Card ending in ${cardNumber.takeLast(4)}"
}

class PayPalStrategy(private val email: String) : PaymentStrategy {
    
    override fun pay(amount: Double): PaymentResult {
        return if (validatePayment(amount)) {
            val fees = amount * 0.034 + 0.30 // 3.4% + $0.30
            PaymentResult(
                success = true,
                transactionId = "PP_${System.currentTimeMillis()}",
                message = "PayPal payment successful",
                fees = fees
            )
        } else {
            PaymentResult(
                success = false,
                transactionId = null,
                message = "PayPal payment failed"
            )
        }
    }
    
    override fun validatePayment(amount: Double): Boolean {
        return amount > 0 && email.contains("@")
    }
    
    override fun getPaymentMethod(): String = "PayPal ($email)"
}

class CryptocurrencyStrategy(
    private val walletAddress: String,
    private val currency: String
) : PaymentStrategy {
    
    override fun pay(amount: Double): PaymentResult {
        return if (validatePayment(amount)) {
            val fees = amount * 0.01 // 1% network fee
            PaymentResult(
                success = true,
                transactionId = "CRYPTO_${System.currentTimeMillis()}",
                message = "$currency payment successful",
                fees = fees
            )
        } else {
            PaymentResult(
                success = false,
                transactionId = null,
                message = "$currency payment failed"
            )
        }
    }
    
    override fun validatePayment(amount: Double): Boolean {
        return amount > 0 && walletAddress.length >= 26
    }
    
    override fun getPaymentMethod(): String = "$currency wallet"
}

// Context class
class PaymentProcessor {
    private var strategy: PaymentStrategy? = null
    
    fun setPaymentStrategy(strategy: PaymentStrategy) {
        this.strategy = strategy
    }
    
    fun processPayment(amount: Double): PaymentResult {
        return strategy?.pay(amount) ?: PaymentResult(
            success = false,
            transactionId = null,
            message = "No payment method selected"
        )
    }
    
    fun getCurrentPaymentMethod(): String {
        return strategy?.getPaymentMethod() ?: "No payment method selected"
    }
}

// 2. Sorting Strategy

interface SortingStrategy<T : Comparable<T>> {
    fun sort(data: MutableList<T>): List<T>
    fun getAlgorithmName(): String
    fun getTimeComplexity(): String
    fun getSpaceComplexity(): String
}

class BubbleSortStrategy<T : Comparable<T>> : SortingStrategy<T> {
    override fun sort(data: MutableList<T>): List<T> {
        val result = data.toMutableList()
        val n = result.size
        
        for (i in 0 until n - 1) {
            for (j in 0 until n - i - 1) {
                if (result[j] > result[j + 1]) {
                    val temp = result[j]
                    result[j] = result[j + 1]
                    result[j + 1] = temp
                }
            }
        }
        return result
    }
    
    override fun getAlgorithmName(): String = "Bubble Sort"
    override fun getTimeComplexity(): String = "O(n²)"
    override fun getSpaceComplexity(): String = "O(1)"
}

class QuickSortStrategy<T : Comparable<T>> : SortingStrategy<T> {
    override fun sort(data: MutableList<T>): List<T> {
        val result = data.toMutableList()
        quickSort(result, 0, result.size - 1)
        return result
    }
    
    private fun quickSort(arr: MutableList<T>, low: Int, high: Int) {
        if (low < high) {
            val pi = partition(arr, low, high)
            quickSort(arr, low, pi - 1)
            quickSort(arr, pi + 1, high)
        }
    }
    
    private fun partition(arr: MutableList<T>, low: Int, high: Int): Int {
        val pivot = arr[high]
        var i = low - 1
        
        for (j in low until high) {
            if (arr[j] <= pivot) {
                i++
                val temp = arr[i]
                arr[i] = arr[j]
                arr[j] = temp
            }
        }
        
        val temp = arr[i + 1]
        arr[i + 1] = arr[high]
        arr[high] = temp
        
        return i + 1
    }
    
    override fun getAlgorithmName(): String = "Quick Sort"
    override fun getTimeComplexity(): String = "O(n log n) average, O(n²) worst"
    override fun getSpaceComplexity(): String = "O(log n)"
}

class MergeSortStrategy<T : Comparable<T>> : SortingStrategy<T> {
    override fun sort(data: MutableList<T>): List<T> {
        if (data.size <= 1) return data.toList()
        
        val middle = data.size / 2
        val left = data.subList(0, middle).toMutableList()
        val right = data.subList(middle, data.size).toMutableList()
        
        return merge(sort(left), sort(right))
    }
    
    private fun merge(left: List<T>, right: List<T>): List<T> {
        var leftIndex = 0
        var rightIndex = 0
        val result = mutableListOf<T>()
        
        while (leftIndex < left.size && rightIndex < right.size) {
            if (left[leftIndex] <= right[rightIndex]) {
                result.add(left[leftIndex])
                leftIndex++
            } else {
                result.add(right[rightIndex])
                rightIndex++
            }
        }
        
        result.addAll(left.subList(leftIndex, left.size))
        result.addAll(right.subList(rightIndex, right.size))
        
        return result
    }
    
    override fun getAlgorithmName(): String = "Merge Sort"
    override fun getTimeComplexity(): String = "O(n log n)"
    override fun getSpaceComplexity(): String = "O(n)"
}

// Context for sorting
class DataSorter<T : Comparable<T>> {
    private var strategy: SortingStrategy<T>? = null
    
    fun setSortingStrategy(strategy: SortingStrategy<T>) {
        this.strategy = strategy
    }
    
    fun sort(data: MutableList<T>): SortResult<T> {
        val currentStrategy = strategy ?: return SortResult(
            data.toList(), "No sorting strategy set", 0, ""
        )
        
        val startTime = System.currentTimeMillis()
        val sortedData = currentStrategy.sort(data)
        val duration = System.currentTimeMillis() - startTime
        
        return SortResult(
            sortedData,
            currentStrategy.getAlgorithmName(),
            duration,
            "${currentStrategy.getTimeComplexity()} time, ${currentStrategy.getSpaceComplexity()} space"
        )
    }
    
    data class SortResult<T>(
        val sortedData: List<T>,
        val algorithmUsed: String,
        val executionTimeMs: Long,
        val complexity: String
    )
}

// 3. Kotlin Functional Strategy using Higher-Order Functions

typealias ValidationStrategy<T> = (T) -> ValidationResult

data class ValidationResult(
    val isValid: Boolean,
    val errorMessage: String? = null
)

// Functional strategies
object ValidationStrategies {
    val emailValidation: ValidationStrategy<String> = { email ->
        when {
            email.isBlank() -> ValidationResult(false, "Email cannot be blank")
            !email.contains("@") -> ValidationResult(false, "Email must contain @")
            !email.contains(".") -> ValidationResult(false, "Email must contain domain")
            else -> ValidationResult(true)
        }
    }
    
    val passwordValidation: ValidationStrategy<String> = { password ->
        when {
            password.length < 8 -> ValidationResult(false, "Password must be at least 8 characters")
            !password.any { it.isUpperCase() } -> ValidationResult(false, "Password must contain uppercase letter")
            !password.any { it.isDigit() } -> ValidationResult(false, "Password must contain digit")
            else -> ValidationResult(true)
        }
    }
    
    fun ageValidation(minAge: Int, maxAge: Int): ValidationStrategy<Int> = { age ->
        when {
            age < minAge -> ValidationResult(false, "Age must be at least $minAge")
            age > maxAge -> ValidationResult(false, "Age cannot exceed $maxAge")
            else -> ValidationResult(true)
        }
    }
}

// Validator using functional strategies
class FieldValidator<T> {
    fun validate(value: T, strategy: ValidationStrategy<T>): ValidationResult {
        return strategy(value)
    }
    
    fun validateMultiple(value: T, strategies: List<ValidationStrategy<T>>): List<ValidationResult> {
        return strategies.map { it(value) }
    }
}

// 4. Compression Strategy

interface CompressionStrategy {
    fun compress(data: String): CompressedData
    fun decompress(compressedData: CompressedData): String
    fun getCompressionRatio(original: String, compressed: CompressedData): Double
    fun getAlgorithmName(): String
}

data class CompressedData(
    val data: ByteArray,
    val originalSize: Int,
    val algorithm: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as CompressedData
        if (!data.contentEquals(other.data)) return false
        if (originalSize != other.originalSize) return false
        if (algorithm != other.algorithm) return false
        return true
    }
    
    override fun hashCode(): Int {
        var result = data.contentHashCode()
        result = 31 * result + originalSize
        result = 31 * result + algorithm.hashCode()
        return result
    }
}

class RunLengthEncodingStrategy : CompressionStrategy {
    override fun compress(data: String): CompressedData {
        if (data.isEmpty()) return CompressedData(byteArrayOf(), 0, getAlgorithmName())
        
        val compressed = StringBuilder()
        var count = 1
        var currentChar = data[0]
        
        for (i in 1 until data.length) {
            if (data[i] == currentChar) {
                count++
            } else {
                compressed.append("$count$currentChar")
                currentChar = data[i]
                count = 1
            }
        }
        compressed.append("$count$currentChar")
        
        return CompressedData(
            compressed.toString().toByteArray(),
            data.length,
            getAlgorithmName()
        )
    }
    
    override fun decompress(compressedData: CompressedData): String {
        val compressed = String(compressedData.data)
        val decompressed = StringBuilder()
        
        var i = 0
        while (i < compressed.length) {
            val count = compressed[i].toString().toInt()
            val char = compressed[i + 1]
            repeat(count) { decompressed.append(char) }
            i += 2
        }
        
        return decompressed.toString()
    }
    
    override fun getCompressionRatio(original: String, compressed: CompressedData): Double {
        return compressed.data.size.toDouble() / original.length
    }
    
    override fun getAlgorithmName(): String = "Run Length Encoding"
}

class SimpleCompressionStrategy : CompressionStrategy {
    override fun compress(data: String): CompressedData {
        // Simple compression by removing duplicate spaces and common words
        val compressed = data
            .replace(Regex("\\s+"), " ")
            .replace(" the ", " T ")
            .replace(" and ", " A ")
            .replace(" or ", " O ")
            .replace(" in ", " I ")
        
        return CompressedData(
            compressed.toByteArray(),
            data.length,
            getAlgorithmName()
        )
    }
    
    override fun decompress(compressedData: CompressedData): String {
        return String(compressedData.data)
            .replace(" T ", " the ")
            .replace(" A ", " and ")
            .replace(" O ", " or ")
            .replace(" I ", " in ")
    }
    
    override fun getCompressionRatio(original: String, compressed: CompressedData): Double {
        return compressed.data.size.toDouble() / original.length
    }
    
    override fun getAlgorithmName(): String = "Simple Text Compression"
}

// Compression context
class FileCompressor {
    private var strategy: CompressionStrategy = SimpleCompressionStrategy()
    
    fun setCompressionStrategy(strategy: CompressionStrategy) {
        this.strategy = strategy
    }
    
    fun compressFile(content: String): CompressionResult {
        val startTime = System.currentTimeMillis()
        val compressed = strategy.compress(content)
        val compressionTime = System.currentTimeMillis() - startTime
        
        return CompressionResult(
            compressed,
            strategy.getCompressionRatio(content, compressed),
            compressionTime,
            strategy.getAlgorithmName()
        )
    }
    
    fun decompressFile(compressedData: CompressedData): String {
        return strategy.decompress(compressedData)
    }
    
    data class CompressionResult(
        val compressedData: CompressedData,
        val compressionRatio: Double,
        val compressionTimeMs: Long,
        val algorithm: String
    ) {
        fun getSizeReduction(): Double = (1 - compressionRatio) * 100
    }
}