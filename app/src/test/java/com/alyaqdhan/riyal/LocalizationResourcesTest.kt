package com.alyaqdhan.riyal

import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import org.junit.Assert.*
import org.junit.Test
import org.w3c.dom.Element

/** Source-resource contracts complement Android rendering checks; they run on the JVM. */
class LocalizationResourcesTest {
    private val res = File("src/main/res")
    private fun entries(folder: String): Map<String, Element> =
        File(res, folder).listFiles().orEmpty().filter { it.extension == "xml" }.flatMap { file ->
            val children = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file).documentElement.childNodes
            (0 until children.length).mapNotNull { children.item(it) as? Element }
                .filter { it.tagName in setOf("string", "plurals") && it.getAttribute("translatable") != "false" }
        }.associateBy { it.getAttribute("name") }

    private fun placeholders(text: String): List<String> =
        Regex("%(?:[0-9]+\\$)?[sdfox]").findAll(text).map { it.value }.sorted().toList()

    @Test fun `English and Arabic have complete matching app resources`() {
        val english = entries("values")
        val arabic = entries("values-ar")
        assertTrue("Arabic resources must exist", arabic.isNotEmpty())
        assertEquals("Every app resource needs both languages", english.keys, arabic.keys)
        english.forEach { (name, source) ->
            val target = arabic.getValue(name)
            assertEquals("Resource type $name", source.tagName, target.tagName)
            if (source.tagName == "string") {
                assertEquals("Format parameters $name", placeholders(source.textContent), placeholders(target.textContent))
                val sourceWords = source.textContent.replace(Regex("%(?:[0-9]+\\$)?[sdfox]"), "")
                assertTrue("Arabic text missing in $name", target.textContent.any { it in '\u0600'..'\u06ff' } || source.textContent == target.textContent || sourceWords.none(Char::isLetter))
            }
        }
    }

    @Test fun `Arabic counts cover all six plural quantities and preserve parameters`() {
        val english = entries("values")
        val pluralEntries = entries("values-ar").filterValues { it.tagName == "plurals" }
        assertTrue("Localized counts must use plural resources", pluralEntries.isNotEmpty())
        pluralEntries.forEach { (name, element) ->
            val items = element.getElementsByTagName("item")
            val quantities = (0 until items.length).map { (items.item(it) as Element).getAttribute("quantity") }.toSet()
            assertEquals(name, setOf("zero", "one", "two", "few", "many", "other"), quantities)
            val source = english.getValue(name).getElementsByTagName("item")
            val parameters = (0 until source.length).flatMap { placeholders(source.item(it).textContent) }.toSet()
            (0 until items.length).forEach {
                val translation = items.item(it).textContent
                assertTrue("Empty plural form $name", translation.isNotBlank())
                assertTrue("Unknown parameters in $name", placeholders(translation).all(parameters::contains))
            }
        }
    }

    @Test fun `app languages include English Arabic and persistent AndroidX storage`() {
        val config = File(res, "xml/locales_config.xml")
        assertTrue("Android app language declaration missing", config.exists())
        val doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(config)
        val locales = doc.getElementsByTagName("locale")
        assertEquals(setOf("en", "ar"), (0 until locales.length).map { (locales.item(it) as Element).getAttribute("android:name") }.toSet())
        val manifest = File("src/main/AndroidManifest.xml").readText()
        assertTrue(manifest.contains("android:localeConfig=\"@xml/locales_config\""))
        assertTrue(manifest.contains("autoStoreLocales"))
        assertTrue(manifest.contains("android:supportsRtl=\"true\""))
        assertTrue("Fragment ComposeView needs stable saved-state ID", File("src/main/java/com/alyaqdhan/riyal/ui/nav/Fragments.kt").readText().contains("id = R.id.screen_compose_view"))
    }
}
