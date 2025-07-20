package patterns.structural

/**
 * # Flyweight Pattern
 * 
 * ## Definition
 * Uses sharing to efficiently support large numbers of fine-grained objects.
 * Minimizes memory use by sharing efficiently all data that is common to similar objects.
 * 
 * ## Problem it solves
 * - Application needs to generate a huge number of objects
 * - Storage costs are high because of the sheer quantity of objects
 * - Most object state can be made extrinsic
 * - Groups of objects may be replaced by few shared objects
 * 
 * ## When to use
 * - Application uses a large number of objects
 * - Storage costs are high due to sheer quantity of objects
 * - Most object state can be made extrinsic
 * - Groups of objects can be replaced by relatively few shared objects
 * 
 * ## When NOT to use
 * - Objects don't share common state
 * - Memory usage is not a concern
 * - Objects are frequently created and destroyed
 * 
 * ## Advantages
 * - Reduces memory footprint
 * - Can reduce total number of instances
 * - Centralizes state for many similar objects
 * - Can improve performance through sharing
 * 
 * ## Disadvantages
 * - May introduce run-time costs for computing extrinsic state
 * - Can make code more complex
 * - May not be beneficial if objects don't share state
 */

// 1. Classic Flyweight Pattern - Text Editor Character Formatting

// Flyweight interface
interface TextCharacter {
    fun render(position: Position, fontSize: Int, color: String): String
}

// Position represents extrinsic state
data class Position(val x: Int, val y: Int)

// Concrete flyweight - intrinsic state only
class CharacterFlyweight(
    private val character: Char,
    private val fontFamily: String,
    private val fontStyle: String // bold, italic, normal
) : TextCharacter {
    
    override fun render(position: Position, fontSize: Int, color: String): String {
        return "Character '$character' at (${position.x}, ${position.y}) " +
                "with font: $fontFamily $fontStyle ${fontSize}px $color"
    }
    
    // Intrinsic state getters
    fun getCharacter(): Char = character
    fun getFontFamily(): String = fontFamily
    fun getFontStyle(): String = fontStyle
}

// Flyweight factory
object CharacterFlyweightFactory {
    private val flyweights = mutableMapOf<String, CharacterFlyweight>()
    
    fun getFlyweight(character: Char, fontFamily: String, fontStyle: String): CharacterFlyweight {
        val key = "$character-$fontFamily-$fontStyle"
        return flyweights.getOrPut(key) {
            println("Creating new flyweight for: $key")
            CharacterFlyweight(character, fontFamily, fontStyle)
        }
    }
    
    fun getFlyweightCount(): Int = flyweights.size
    
    fun listFlyweights(): List<String> = flyweights.keys.toList()
}

// Context class that uses flyweights
class TextDocument {
    private val characters = mutableListOf<DocumentCharacter>()
    
    // Stores both flyweight reference and extrinsic state
    data class DocumentCharacter(
        val flyweight: CharacterFlyweight,
        val position: Position,
        val fontSize: Int,
        val color: String
    )
    
    fun addCharacter(
        character: Char,
        fontFamily: String,
        fontStyle: String,
        position: Position,
        fontSize: Int,
        color: String
    ) {
        val flyweight = CharacterFlyweightFactory.getFlyweight(character, fontFamily, fontStyle)
        characters.add(DocumentCharacter(flyweight, position, fontSize, color))
    }
    
    fun render(): List<String> {
        return characters.map { docChar ->
            docChar.flyweight.render(docChar.position, docChar.fontSize, docChar.color)
        }
    }
    
    fun getCharacterCount(): Int = characters.size
    fun getFlyweightCount(): Int = CharacterFlyweightFactory.getFlyweightCount()
    
    fun getMemoryFootprint(): String {
        return "Total characters: ${getCharacterCount()}, " +
                "Unique flyweights: ${getFlyweightCount()}, " +
                "Memory saved: ${getCharacterCount() - getFlyweightCount()} objects"
    }
}

// 2. Forest of Trees Example

// Flyweight interface
interface TreeType {
    fun render(canvas: String, x: Int, y: Int, size: Int): String
}

// Concrete flyweight
class TreeTypeFlyweight(
    private val name: String,
    private val color: String,
    private val sprite: String // Image data (intrinsic)
) : TreeType {
    
    override fun render(canvas: String, x: Int, y: Int, size: Int): String {
        return "Drawing $name tree ($color, $sprite) at ($x, $y) with size $size on $canvas"
    }
    
    fun getName(): String = name
    fun getColor(): String = color
    fun getSprite(): String = sprite
}

// Flyweight factory
object TreeTypeFactory {
    private val treeTypes = mutableMapOf<String, TreeTypeFlyweight>()
    
    fun getTreeType(name: String, color: String, sprite: String): TreeTypeFlyweight {
        val key = "$name-$color-$sprite"
        return treeTypes.getOrPut(key) {
            println("Creating new tree type: $key")
            TreeTypeFlyweight(name, color, sprite)
        }
    }
    
    fun getCreatedTreeTypes(): Int = treeTypes.size
}

// Context class
class Tree(
    private val x: Int,
    private val y: Int,
    private val size: Int,
    private val treeType: TreeTypeFlyweight
) {
    fun render(canvas: String): String {
        return treeType.render(canvas, x, y, size)
    }
    
    fun getPosition(): Pair<Int, Int> = Pair(x, y)
    fun getSize(): Int = size
    fun getType(): TreeTypeFlyweight = treeType
}

// Forest containing many trees
class Forest {
    private val trees = mutableListOf<Tree>()
    
    fun plantTree(x: Int, y: Int, size: Int, name: String, color: String, sprite: String) {
        val treeType = TreeTypeFactory.getTreeType(name, color, sprite)
        trees.add(Tree(x, y, size, treeType))
    }
    
    fun render(canvas: String): List<String> {
        return trees.map { it.render(canvas) }
    }
    
    fun getTreeCount(): Int = trees.size
    fun getTreeTypeCount(): Int = TreeTypeFactory.getCreatedTreeTypes()
    
    fun getStatistics(): String {
        return "Forest contains ${getTreeCount()} trees using ${getTreeTypeCount()} different types. " +
                "Memory savings: ${getTreeCount() - getTreeTypeCount()} objects"
    }
    
    fun getTreesByType(typeName: String): List<Tree> {
        return trees.filter { it.getType().getName() == typeName }
    }
}

// 3. Particle System Flyweight

// Flyweight interface
interface ParticleType {
    fun render(x: Float, y: Float, velocity: Velocity, color: String, size: Float): String
    fun update(particle: Particle, deltaTime: Float): Particle
}

data class Velocity(val vx: Float, val vy: Float)

// Concrete flyweight
class ParticleTypeFlyweight(
    private val shape: String,
    private val texture: String,
    private val physics: ParticlePhysics
) : ParticleType {
    
    data class ParticlePhysics(
        val gravity: Float,
        val airResistance: Float,
        val bounceCoefficient: Float
    )
    
    override fun render(x: Float, y: Float, velocity: Velocity, color: String, size: Float): String {
        return "Rendering $shape particle ($texture) at ($x, $y) " +
                "with velocity (${velocity.vx}, ${velocity.vy}), color: $color, size: $size"
    }
    
    override fun update(particle: Particle, deltaTime: Float): Particle {
        val newVx = particle.velocity.vx * (1 - physics.airResistance * deltaTime)
        val newVy = particle.velocity.vy + physics.gravity * deltaTime
        val newX = particle.x + newVx * deltaTime
        val newY = particle.y + newVy * deltaTime
        
        return particle.copy(
            x = newX,
            y = newY,
            velocity = Velocity(newVx, newVy)
        )
    }
    
    fun getShape(): String = shape
    fun getTexture(): String = texture
}

// Flyweight factory
object ParticleTypeFactory {
    private val particleTypes = mutableMapOf<String, ParticleTypeFlyweight>()
    
    fun getParticleType(
        shape: String,
        texture: String,
        physics: ParticleTypeFlyweight.ParticlePhysics
    ): ParticleTypeFlyweight {
        val key = "$shape-$texture-${physics.hashCode()}"
        return particleTypes.getOrPut(key) {
            println("Creating new particle type: $key")
            ParticleTypeFlyweight(shape, texture, physics)
        }
    }
    
    fun getParticleTypeCount(): Int = particleTypes.size
    
    // Predefined particle types
    fun getExplosionParticle(): ParticleTypeFlyweight {
        return getParticleType(
            "circle",
            "fire",
            ParticleTypeFlyweight.ParticlePhysics(0.5f, 0.1f, 0.0f)
        )
    }
    
    fun getSmokeParticle(): ParticleTypeFlyweight {
        return getParticleType(
            "cloud",
            "smoke",
            ParticleTypeFlyweight.ParticlePhysics(-0.1f, 0.05f, 0.0f)
        )
    }
    
    fun getSparkParticle(): ParticleTypeFlyweight {
        return getParticleType(
            "star",
            "electric",
            ParticleTypeFlyweight.ParticlePhysics(0.8f, 0.02f, 0.7f)
        )
    }
}

// Context class
data class Particle(
    val x: Float,
    val y: Float,
    val velocity: Velocity,
    val color: String,
    val size: Float,
    val lifeTime: Float,
    val particleType: ParticleTypeFlyweight
) {
    fun render(): String {
        return particleType.render(x, y, velocity, color, size)
    }
    
    fun update(deltaTime: Float): Particle {
        val updated = particleType.update(this, deltaTime)
        return updated.copy(lifeTime = lifeTime - deltaTime)
    }
    
    fun isAlive(): Boolean = lifeTime > 0
}

// Particle system
class ParticleSystem {
    private val particles = mutableListOf<Particle>()
    
    fun addExplosion(x: Float, y: Float, count: Int) {
        val explosionType = ParticleTypeFactory.getExplosionParticle()
        repeat(count) {
            val angle = (it * 360.0f / count) * Math.PI / 180.0
            val speed = 2.0f + Math.random().toFloat() * 3.0f
            val velocity = Velocity(
                (Math.cos(angle) * speed).toFloat(),
                (Math.sin(angle) * speed).toFloat()
            )
            
            particles.add(
                Particle(
                    x, y, velocity,
                    if (Math.random() > 0.5) "red" else "orange",
                    1.0f + Math.random().toFloat() * 2.0f,
                    2.0f + Math.random().toFloat() * 3.0f,
                    explosionType
                )
            )
        }
    }
    
    fun addSmoke(x: Float, y: Float, count: Int) {
        val smokeType = ParticleTypeFactory.getSmokeParticle()
        repeat(count) {
            val velocity = Velocity(
                (Math.random().toFloat() - 0.5f) * 0.5f,
                -0.5f - Math.random().toFloat() * 1.0f
            )
            
            particles.add(
                Particle(
                    x + (Math.random().toFloat() - 0.5f) * 2.0f,
                    y,
                    velocity,
                    "gray",
                    2.0f + Math.random().toFloat() * 3.0f,
                    5.0f + Math.random().toFloat() * 5.0f,
                    smokeType
                )
            )
        }
    }
    
    fun update(deltaTime: Float) {
        particles.replaceAll { it.update(deltaTime) }
        particles.removeAll { !it.isAlive() }
    }
    
    fun render(): List<String> {
        return particles.map { it.render() }
    }
    
    fun getParticleCount(): Int = particles.size
    fun getParticleTypeCount(): Int = ParticleTypeFactory.getParticleTypeCount()
    
    fun getMemoryEfficiency(): String {
        return "Active particles: ${getParticleCount()}, " +
                "Particle types: ${getParticleTypeCount()}, " +
                "Memory efficiency: ${(getParticleCount().toFloat() / getParticleTypeCount()).toInt()}:1 ratio"
    }
}

// 4. Web Page Element Flyweight

// Flyweight interface
interface WebElementType {
    fun render(content: String, attributes: Map<String, String>): String
}

// Concrete flyweight
class WebElementTypeFlyweight(
    private val tagName: String,
    private val defaultStyles: Map<String, String>,
    private val semanticProperties: Set<String>
) : WebElementType {
    
    override fun render(content: String, attributes: Map<String, String>): String {
        val mergedAttributes = defaultStyles + attributes
        val attributeString = mergedAttributes.entries.joinToString(" ") { "${it.key}='${it.value}'" }
        return "<$tagName $attributeString>$content</$tagName>"
    }
    
    fun getTagName(): String = tagName
    fun getDefaultStyles(): Map<String, String> = defaultStyles
    fun getSemanticProperties(): Set<String> = semanticProperties
}

// Flyweight factory
object WebElementTypeFactory {
    private val elementTypes = mutableMapOf<String, WebElementTypeFlyweight>()
    
    fun getElementType(
        tagName: String,
        defaultStyles: Map<String, String> = emptyMap(),
        semanticProperties: Set<String> = emptySet()
    ): WebElementTypeFlyweight {
        val key = "$tagName-${defaultStyles.hashCode()}-${semanticProperties.hashCode()}"
        return elementTypes.getOrPut(key) {
            println("Creating new web element type: $tagName")
            WebElementTypeFlyweight(tagName, defaultStyles, semanticProperties)
        }
    }
    
    fun getElementTypeCount(): Int = elementTypes.size
    
    // Predefined common element types
    fun getParagraphType(): WebElementTypeFlyweight {
        return getElementType(
            "p",
            mapOf("margin" to "1em 0", "line-height" to "1.6"),
            setOf("text-content", "block-level")
        )
    }
    
    fun getHeadingType(level: Int): WebElementTypeFlyweight {
        return getElementType(
            "h$level",
            mapOf("font-weight" to "bold", "margin" to "${2.5 - level * 0.3}em 0"),
            setOf("heading", "block-level", "sectioning")
        )
    }
    
    fun getButtonType(): WebElementTypeFlyweight {
        return getElementType(
            "button",
            mapOf(
                "padding" to "0.5em 1em",
                "border" to "1px solid #ccc",
                "background" to "#f9f9f9",
                "cursor" to "pointer"
            ),
            setOf("interactive", "form-control")
        )
    }
}

// Context class
data class WebElement(
    val elementType: WebElementTypeFlyweight,
    val content: String,
    val customAttributes: Map<String, String> = emptyMap(),
    val id: String? = null,
    val classes: List<String> = emptyList()
) {
    fun render(): String {
        val attributes = customAttributes.toMutableMap()
        id?.let { attributes["id"] = it }
        if (classes.isNotEmpty()) {
            attributes["class"] = classes.joinToString(" ")
        }
        
        return elementType.render(content, attributes)
    }
}

// Web page containing many elements
class WebPage(private val title: String) {
    private val elements = mutableListOf<WebElement>()
    
    fun addParagraph(content: String, classes: List<String> = emptyList(), id: String? = null) {
        elements.add(
            WebElement(
                WebElementTypeFactory.getParagraphType(),
                content,
                emptyMap(),
                id,
                classes
            )
        )
    }
    
    fun addHeading(level: Int, content: String, classes: List<String> = emptyList(), id: String? = null) {
        elements.add(
            WebElement(
                WebElementTypeFactory.getHeadingType(level),
                content,
                emptyMap(),
                id,
                classes
            )
        )
    }
    
    fun addButton(text: String, onClick: String, classes: List<String> = emptyList(), id: String? = null) {
        elements.add(
            WebElement(
                WebElementTypeFactory.getButtonType(),
                text,
                mapOf("onclick" to onClick),
                id,
                classes
            )
        )
    }
    
    fun render(): String {
        val body = elements.joinToString("\n") { "  ${it.render()}" }
        return """
            <!DOCTYPE html>
            <html>
            <head><title>$title</title></head>
            <body>
            $body
            </body>
            </html>
        """.trimIndent()
    }
    
    fun getElementCount(): Int = elements.size
    fun getElementTypeCount(): Int = WebElementTypeFactory.getElementTypeCount()
    
    fun getOptimizationStats(): String {
        return "Page '$title': ${getElementCount()} elements using ${getElementTypeCount()} types. " +
                "Flyweight efficiency: ${getElementCount() - getElementTypeCount()} objects saved"
    }
}