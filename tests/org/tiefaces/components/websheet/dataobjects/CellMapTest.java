/**
 * 
 */
package org.tiefaces.components.websheet.dataobjects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Test;
import org.tiefaces.common.Item;
import org.tiefaces.components.websheet.TieWebSheetBean;

/**
 * @author Jason Jiang
 *
 */
public class CellMapTest {

	CellMap cellMap = new CellMap(new TieWebSheetBean());

	/**
	 * Test method for
	 * {@link org.tiefaces.components.websheet.dataobjects.CellMap#size()}.
	 */
	@Test
	public final void testSize() throws Exception {
		assertEquals(cellMap.size(), 0);
	}

	/**
	 * Test method for
	 * {@link org.tiefaces.components.websheet.dataobjects.CellMap#isEmpty()}.
	 */
	@Test
	public final void testIsEmpty() throws Exception {
		assertFalse(cellMap.isEmpty());
	}

	/**
	 * Test method for
	 * {@link org.tiefaces.components.websheet.dataobjects.CellMap#containsKey(java.lang.Object)}
	 * .
	 */
	@Test
	public final void testContainsKey() throws Exception {
		assertFalse(cellMap.containsKey("any"));
	}

	/**
	 * Test method for
	 * {@link org.tiefaces.components.websheet.dataobjects.CellMap#containsValue(java.lang.Object)}
	 * .
	 */
	@Test
	public final void testContainsValue() throws Exception {
		assertFalse(cellMap.containsValue("any"));
	}

	/**
	 * Test method for
	 * {@link org.tiefaces.components.websheet.dataobjects.CellMap#remove(java.lang.Object)}
	 * .
	 */
	@Test
	public final void testRemove() throws Exception {
		assertNull(cellMap.remove("any"));
	}

	/**
	 * Test method for
	 * {@link org.tiefaces.components.websheet.dataobjects.CellMap#keySet()}.
	 */
	@Test
	public final void testKeySet() throws Exception {
		assertTrue(cellMap.keySet().isEmpty());
	}

	/**
	 * Test method for
	 * {@link org.tiefaces.components.websheet.dataobjects.CellMap#values()}.
	 */
	@Test
	public final void testValues() throws Exception {
		assertTrue(cellMap.values().isEmpty());
	}

	/**
	 * Test method for
	 * {@link org.tiefaces.components.websheet.dataobjects.CellMap#entrySet()}.
	 */
	@Test
	public final void testEntrySet() throws Exception {
		assertTrue(cellMap.entrySet().isEmpty());
	}

	/**
	 * Test method for
	 * {@link org.tiefaces.components.websheet.dataobjects.CellMap#get(java.lang.Object)}
	 * .
	 */
	@Test
	public final void testGetChart() throws Exception {

		final TieWebSheetBean bean = new TieWebSheetBean();
		bean.init();
		final InputStream stream =
				this.getClass().getClassLoader().getResourceAsStream(
						"resources/sheet/linecharts1.xlsx");
		assertEquals(bean.loadWebSheet(stream), 1);
		final String chartViewId = (String) bean.getCellsMap().get("7:0:chart");
		assertFalse(chartViewId.isEmpty());

	}

	@Test
	public final void testGet() throws Exception {

		final TieWebSheetBean bean = new TieWebSheetBean();
		bean.init();
		final InputStream stream =
				this.getClass().getClassLoader().getResourceAsStream(
						"resources/sheet/PRICELISTINPUTVALIDATION.xlsx");
		final List<Item> itemList = new ArrayList<Item>();
		itemList.add(new Item());
		final HashMap<String, Object> context = new HashMap<String, Object>();
		context.put("items", itemList);
		assertEquals(bean.loadWebSheet(stream, context), 1);
		assertEquals(bean.getCellsMap().get("0:0"), "Sale Price Report");
		assertEquals(bean.getCellsMap().get("2:1"), "Item Number");

	}

	/**
	 * Test method for
	 * {@link org.tiefaces.components.websheet.dataobjects.CellMap#put(java.lang.Object, java.lang.Object)}
	 * .
	 */
	@SuppressWarnings("unchecked")
	@Test
	public final void testPut() throws Exception {
		final TieWebSheetBean bean = new TieWebSheetBean();
		bean.init();
		final InputStream stream =
				this.getClass().getClassLoader().getResourceAsStream(
						"resources/sheet/PRICELISTINPUTVALIDATION.xlsx");
		final List<Item> itemList = new ArrayList<Item>();
		itemList.add(new Item());
		final HashMap<String, Object> context = new HashMap<String, Object>();
		context.put("items", itemList);
		assertEquals(bean.loadWebSheet(stream, context), 1);
		assertEquals(bean.getCellsMap().put("4:1", "test item"),
				"test item");
		assertEquals(bean.getCellsMap().get("4:1"), "test item");
	}

}
