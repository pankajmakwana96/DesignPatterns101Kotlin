package org.example

import patterns.creational.DatabaseConnection
import patterns.creational.ConfigurationManager
import patterns.creational.ComputerBuilder

/**
 * Simplified main demonstrating core design patterns
 * This serves as a working example while we fix the comprehensive implementations
 */
fun main() {
    println("🎯 Design Patterns 101 - Working Examples")
    println("=" * 50)
    
    // Singleton Pattern Example
    println("\n📦 1. Singleton Pattern:")
    val db1 = DatabaseConnection
    val db2 = DatabaseConnection
    println("Database instances are same: ${db1 === db2}")
    println(db1.connect())
    
    // Singleton with Lazy Initialization
    val config1 = ConfigurationManager.instance
    val config2 = ConfigurationManager.instance
    println("Config instances are same: ${config1 === config2}")
    config1.setProperty("env", "production")
    println("Environment: ${config2.getProperty("env")}")
    
    // Builder Pattern Example
    println("\n🏗️ 2. Builder Pattern:")
    val gamingPC = ComputerBuilder()
        .cpu("Intel Core i9-13900K")
        .ram("32GB DDR4-3200")
        .storage("1TB NVMe SSD")
        .gpu("NVIDIA RTX 4080")
        .gamingPC()
        .build()
    
    println("Gaming PC: ${gamingPC.cpu}, ${gamingPC.ram}, ${gamingPC.gpu}")
    
    println("\n✅ Basic patterns working successfully!")
    println("📁 Explore individual pattern files for detailed implementations")
}

private operator fun String.times(n: Int): String = this.repeat(n)