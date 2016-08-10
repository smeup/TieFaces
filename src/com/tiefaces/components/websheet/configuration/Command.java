package com.tiefaces.components.websheet.configuration;

import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Sheet;

import com.tiefaces.components.websheet.service.CellHelper;

import org.apache.poi.xssf.usermodel.XSSFEvaluationWorkbook;

/**
 * Interface for all the command.
 * 
 * @author Jason Jiang
 *
 */
public interface Command {

	/**
	 * Return command type.
	 * @return command type name.
	 */
	String getCommandTypeName();

	/**
	 * Set command type.
	 * @param pCommandTypeName command type name.
	 */
	void setCommandTypeName(String pCommandTypeName);

	/**
	 * Constructor configRange if it's null. Always return an object for
	 * configRange.
	 * 
	 * @return ConfigRange object.
	 */
	ConfigRange getConfigRange();

	/**
	 * Set config range.
	 * @param pConfigRange config range.
	 */
	void setConfigRange(ConfigRange pConfigRange);

	/**
	 * Return parent command.
	 * @return parent command.
	 */
	Boolean isParentFound();
	
	void setParentFound(Boolean parentFound);


	/**
	 * command area length.
	 * @return length.
	 */
	String getLength();

	/**
	 * Set command area length.
	 * @param pLength length.
	 */
	void setLength(String pLength);

	/**
	 * Get top row of command range.
	 * 
	 * @return int top row index.
	 */
	int getTopRow();

	/**
	 * Get last row of command range.
	 * 
	 * @return int last row index.
	 */

	int getLastRow();

	/**
	 * get left column index of the command range.
	 * 
	 * @return int left column index.
	 */
	int getLeftCol();

	/**
	 * get right column index of the command range.
	 * 
	 * @return int right column index.
	 */
	int getRightCol();

	/**
	 * the final length of command area.
	 * @return final length.
	 */
	int getFinalLength();

	/**
	 * Set the final length.
	 * @param populatedLength final length.
	 */
	void setFinalLength(int populatedLength);

	/**
	 * build the command area.
	 * @param wbWrapper workbook wrapper.
	 * @param sheet sheet.
	 * @param atRow populate at the row.
	 * @param context context map.
	 * @param watchList watch list for formula.
	 * @param currentRowsMappingList rowsMapping for formula.
	 * @param allRowsMappingList all rowsMapping for formula.
	 * @param engine evaluation engine.
	 * @param cellHelper cell helper.
	 * @return
	 */
	int buildAt(XSSFEvaluationWorkbook wbWrapper,
			Sheet sheet, int atRow, Map<String, Object> context,
			List<Integer> watchList,
			List<RowsMapping> currentRowsMappingList,
			List<RowsMapping> allRowsMappingList,
			ExpressionEngine engine, final CellHelper cellHelper);

}