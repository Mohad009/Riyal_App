package com.alyaqdhan.riyal

import com.alyaqdhan.riyal.core.Money
import com.alyaqdhan.riyal.data.Categories
import com.alyaqdhan.riyal.ui.compose.categoryNameResource
import java.util.Locale
import org.junit.Assert.*
import org.junit.Test

class LocalizationBehaviorTest {
    @Test fun `every built in category has display copy but stable id and custom name wins`() {
        Categories.BUILTIN.forEach { category ->
            assertNotNull("Arabic label missing for ${category.id}", categoryNameResource(category))
            assertNull(categoryNameResource(category.copy(name = "My category")))
            assertNull(categoryNameResource(category.copy(custom = true)))
            assertEquals(category.id, Categories.byId(category.id).id)
        }
    }

    @Test fun `language changes do not alter OMR precision or minor units`() {
        val original = Locale.getDefault()
        try {
            Locale.setDefault(Locale.forLanguageTag("ar"))
            assertEquals("OMR 4.500", Money.format(4500, "OMR"))
            assertEquals("− OMR 4.500", Money.formatSigned(4500, "OMR", true))
            assertEquals(4500L, Money.toMinor(Money.toMajor(4500, "OMR"), "OMR"))
        } finally {
            Locale.setDefault(original)
        }
    }
}
