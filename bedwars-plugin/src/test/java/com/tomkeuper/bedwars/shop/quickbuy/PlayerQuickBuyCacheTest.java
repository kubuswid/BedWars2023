package com.tomkeuper.bedwars.shop.quickbuy;

import com.tomkeuper.bedwars.api.arena.shop.ICategoryContent;
import com.tomkeuper.bedwars.api.shop.IQuickBuyElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PlayerQuickBuyCacheTest {

    private PlayerQuickBuyCache cache;

    @BeforeEach
    void setUp() {
        cache = new PlayerQuickBuyCache();
    }

    @SuppressWarnings("unchecked")
    private Map<Integer, String> getUpdateSlots() throws Exception {
        Field field = PlayerQuickBuyCache.class.getDeclaredField("updateSlots");
        field.setAccessible(true);
        return (Map<Integer, String>) field.get(cache);
    }

    @Test
    void setElement_withNonNullCategoryContent() throws Exception {
        int slot = 19;
        ICategoryContent mockContent = mock(ICategoryContent.class);
        when(mockContent.getIdentifier()).thenReturn("wool");

        cache.setElement(slot, mockContent);

        List<IQuickBuyElement> elements = cache.getElements();
        assertEquals(1, elements.size());
        assertEquals(slot, elements.get(0).getSlot());

        Map<Integer, String> updateSlots = getUpdateSlots();
        assertTrue(updateSlots.containsKey(slot));
        assertEquals("wool", updateSlots.get(slot));
    }

    @Test
    void setElement_withNullCategoryContent() throws Exception {
        int slot = 19;
        cache.setElement(slot, (ICategoryContent) null);

        List<IQuickBuyElement> elements = cache.getElements();
        assertTrue(elements.isEmpty());

        Map<Integer, String> updateSlots = getUpdateSlots();
        assertTrue(updateSlots.containsKey(slot));
        assertEquals(" ", updateSlots.get(slot));
    }

    @Test
    void setElement_replacesExistingElement() throws Exception {
        int slot = 19;

        // Add first element
        ICategoryContent mockContent1 = mock(ICategoryContent.class);
        when(mockContent1.getIdentifier()).thenReturn("wool");
        cache.setElement(slot, mockContent1);

        // Replace with second element
        ICategoryContent mockContent2 = mock(ICategoryContent.class);
        when(mockContent2.getIdentifier()).thenReturn("stone_sword");
        cache.setElement(slot, mockContent2);

        List<IQuickBuyElement> elements = cache.getElements();
        assertEquals(1, elements.size());
        assertEquals(slot, elements.get(0).getSlot());

        Map<Integer, String> updateSlots = getUpdateSlots();
        assertTrue(updateSlots.containsKey(slot));
        assertEquals("stone_sword", updateSlots.get(slot));
    }

    @Test
    void setElement_withStringCategory() throws Exception {
        int slot = 19;
        String category = "wool";

        cache.setElement(slot, category);

        List<IQuickBuyElement> elements = cache.getElements();
        assertEquals(1, elements.size());
        assertEquals(slot, elements.get(0).getSlot());

        Map<Integer, String> updateSlots = getUpdateSlots();
        assertTrue(updateSlots.containsKey(slot));
        assertEquals("wool", updateSlots.get(slot));
    }

    @Test
    void setElement_withNullStringCategory() throws Exception {
        int slot = 19;

        cache.setElement(slot, (String) null);

        List<IQuickBuyElement> elements = cache.getElements();
        assertTrue(elements.isEmpty());

        Map<Integer, String> updateSlots = getUpdateSlots();
        assertTrue(updateSlots.containsKey(slot));
        assertEquals(" ", updateSlots.get(slot));
    }
}
