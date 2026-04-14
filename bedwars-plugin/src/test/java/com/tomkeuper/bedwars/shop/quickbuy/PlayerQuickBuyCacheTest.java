package com.tomkeuper.bedwars.shop.quickbuy;

import com.tomkeuper.bedwars.api.arena.shop.ICategoryContent;
import com.tomkeuper.bedwars.api.shop.IQuickBuyElement;
import com.tomkeuper.bedwars.api.shop.IShopCategory;
import com.tomkeuper.bedwars.shop.ShopManager;
import com.tomkeuper.bedwars.shop.main.ShopIndex;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PlayerQuickBuyCacheTest {

    private PlayerQuickBuyCache cache;

    @Mock
    private ICategoryContent categoryContentMock;

    @Mock
    private ShopIndex shopIndexMock;

    @Mock
    private IShopCategory shopCategoryMock;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock the static ShopManager.shop
        ShopManager.shop = shopIndexMock;
        when(shopIndexMock.getCategoryList()).thenReturn(Collections.singletonList(shopCategoryMock));
        when(shopCategoryMock.getCategoryContentList()).thenReturn(Collections.singletonList(categoryContentMock));

        cache = new PlayerQuickBuyCache();

        // Clear state
        cache.getElements().clear();
        try {
            Field updateSlotsField = PlayerQuickBuyCache.class.getDeclaredField("updateSlots");
            updateSlotsField.setAccessible(true);
            ((HashMap<?, ?>) updateSlotsField.get(cache)).clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSetElementWithValidCategoryContent() throws Exception {
        int slot = 19;
        String identifier = "test-item";
        when(categoryContentMock.getIdentifier()).thenReturn(identifier);

        // Action
        cache.setElement(slot, categoryContentMock);

        // Assert elements
        List<IQuickBuyElement> elements = cache.getElements();
        assertEquals(1, elements.size());
        assertEquals(slot, elements.get(0).getSlot());
        assertEquals(identifier, elements.get(0).getCategoryContent().getIdentifier());

        // Assert updateSlots map
        Field updateSlotsField = PlayerQuickBuyCache.class.getDeclaredField("updateSlots");
        updateSlotsField.setAccessible(true);
        HashMap<?, ?> updateSlots = (HashMap<?, ?>) updateSlotsField.get(cache);

        assertEquals(1, updateSlots.size());
        assertTrue(updateSlots.containsKey(slot));
        assertEquals(identifier, updateSlots.get(slot));
    }

    @Test
    public void testSetElementWithNullCategoryContent() throws Exception {
        int slot = 20;

        // Action
        cache.setElement(slot, (ICategoryContent) null);

        // Assert elements (should not add anything, but old element is removed)
        List<IQuickBuyElement> elements = cache.getElements();
        assertEquals(0, elements.size());

        // Assert updateSlots map
        Field updateSlotsField = PlayerQuickBuyCache.class.getDeclaredField("updateSlots");
        updateSlotsField.setAccessible(true);
        HashMap<?, ?> updateSlots = (HashMap<?, ?>) updateSlotsField.get(cache);

        assertEquals(1, updateSlots.size());
        assertTrue(updateSlots.containsKey(slot));
        assertEquals(" ", updateSlots.get(slot));
    }

    @Test
    public void testSetElementReplacesExistingSlot() throws Exception {
        int slot = 21;
        String identifier1 = "test-item-1";
        String identifier2 = "test-item-2";

        ICategoryContent cc1 = mock(ICategoryContent.class);
        when(cc1.getIdentifier()).thenReturn(identifier1);

        ICategoryContent cc2 = mock(ICategoryContent.class);
        when(cc2.getIdentifier()).thenReturn(identifier2);

        // Provide both elements via category list
        when(shopCategoryMock.getCategoryContentList()).thenReturn(List.of(cc1, cc2));

        // Action 1
        cache.setElement(slot, cc1);

        // Action 2
        cache.setElement(slot, cc2);

        // Assert elements
        List<IQuickBuyElement> elements = cache.getElements();
        assertEquals(1, elements.size());
        assertEquals(slot, elements.get(0).getSlot());
        assertEquals(identifier2, elements.get(0).getCategoryContent().getIdentifier());

        // Assert updateSlots map
        Field updateSlotsField = PlayerQuickBuyCache.class.getDeclaredField("updateSlots");
        updateSlotsField.setAccessible(true);
        HashMap<?, ?> updateSlots = (HashMap<?, ?>) updateSlotsField.get(cache);

        assertEquals(1, updateSlots.size());
        assertTrue(updateSlots.containsKey(slot));
        assertEquals(identifier2, updateSlots.get(slot));
    }

    @Test
    public void testSetElementWithStringCategory() throws Exception {
        int slot = 22;
        String category = "test-category";

        when(categoryContentMock.getIdentifier()).thenReturn(category);

        // Action
        cache.setElement(slot, category);

        // Assert elements
        List<IQuickBuyElement> elements = cache.getElements();
        assertEquals(1, elements.size());
        assertEquals(slot, elements.get(0).getSlot());
        assertEquals(category, elements.get(0).getCategoryContent().getIdentifier());

        // Assert updateSlots map
        Field updateSlotsField = PlayerQuickBuyCache.class.getDeclaredField("updateSlots");
        updateSlotsField.setAccessible(true);
        HashMap<?, ?> updateSlots = (HashMap<?, ?>) updateSlotsField.get(cache);

        assertEquals(1, updateSlots.size());
        assertTrue(updateSlots.containsKey(slot));
        assertEquals(category, updateSlots.get(slot));
    }

    @Test
    public void testSetElementWithNullStringCategory() throws Exception {
        int slot = 23;

        // Action
        cache.setElement(slot, (String) null);

        // Assert elements
        List<IQuickBuyElement> elements = cache.getElements();
        assertEquals(0, elements.size());

        // Assert updateSlots map
        Field updateSlotsField = PlayerQuickBuyCache.class.getDeclaredField("updateSlots");
        updateSlotsField.setAccessible(true);
        HashMap<?, ?> updateSlots = (HashMap<?, ?>) updateSlotsField.get(cache);

        assertEquals(1, updateSlots.size());
        assertTrue(updateSlots.containsKey(slot));
        assertEquals(" ", updateSlots.get(slot));
    }

    @Test
    public void testSetElementReplacesExistingStringSlot() throws Exception {
        int slot = 24;
        String category1 = "test-category-1";
        String category2 = "test-category-2";

        ICategoryContent cc1 = mock(ICategoryContent.class);
        when(cc1.getIdentifier()).thenReturn(category1);

        ICategoryContent cc2 = mock(ICategoryContent.class);
        when(cc2.getIdentifier()).thenReturn(category2);

        // Provide both elements via category list
        when(shopCategoryMock.getCategoryContentList()).thenReturn(List.of(cc1, cc2));

        // Action 1
        cache.setElement(slot, category1);

        // Action 2
        cache.setElement(slot, category2);

        // Assert elements
        List<IQuickBuyElement> elements = cache.getElements();
        assertEquals(1, elements.size());
        assertEquals(slot, elements.get(0).getSlot());
        assertEquals(category2, elements.get(0).getCategoryContent().getIdentifier());

        // Assert updateSlots map
        Field updateSlotsField = PlayerQuickBuyCache.class.getDeclaredField("updateSlots");
        updateSlotsField.setAccessible(true);
        HashMap<?, ?> updateSlots = (HashMap<?, ?>) updateSlotsField.get(cache);

        assertEquals(1, updateSlots.size());
        assertTrue(updateSlots.containsKey(slot));
        assertEquals(category2, updateSlots.get(slot));
    }
}
