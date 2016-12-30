/*
 * Copyright 2015 TieFaces.
 * Licensed under MIT
 */

package org.tiefaces.components.websheet.dataobjects;

import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.tiefaces.components.websheet.CellAttributesMap;
import org.tiefaces.components.websheet.TieWebSheetConstants;

/**
 * Cell object used for JSF datatable. This object hold an reference to POI cell
 * object
 * 
 * @author Jason Jiang
 */
public class FacesCell {

	/** cell web css style. */
	private String style = "";
	/** column css style. */
	private String columnStyle = "";
	/** cell column span default set to 1. */
	private int colspan = 1; //
	/** row span default set to 1. */
	private int rowspan = 1; //
	/** column index in the datatable. */
	private int columnIndex; //
	/** indicate the cell hold invalid data when. */
	private boolean invalid = false; //
	/** hold error message when the cell is invalid. */
	private String errormsg;
	/** data type for input cell. could be text/text area/number etc. */
	private String inputType = "";
	/** indicate the cell hold picture when set to true. */
	private boolean containPic = false;
	/** picture Id for retrieve picture when containPic = true. */
	private String pictureId;
	/** cell web css style. */
	private String pictureStyle = "";
	/** decimalPlaces for number. default is 2. */
	private short decimalPlaces = 2;
	/** symbol. default is null. */
	private String symbol;
	/** symbolPosition. default is prefix */
	private String symbolPosition = "p";
	/** indicate the cell hold chart when set to true. */
	private boolean containChart = false;
	/** chart Id for retrieve picture when containPic = true. */
	private String chartId;
	/** cell web css style. */
	private String chartStyle = "";
	
	private String control = "";
	
	private List<CellFormAttributes> inputAttrs;
	
	private Map<String, String> selectItemAttrs;
	
	private String datePattern = "";
	
	private boolean hasSaveAttr= false;
	

	public final String getStyle() {
		return style;
	}

	public final void setStyle(final String style) {
		this.style = style;
	}

	public String getValidStyle() {
		if (invalid)
			return style + "border-color: red;";
		else
			return style;
	}

	public String getInputType() {
		return inputType;
	}

	public void setInputType(String inputType) {
		this.inputType = inputType;
	}

	public int getColumnIndex() {
		return columnIndex;
	}

	public void setColumnIndex(int columnIndex) {
		this.columnIndex = columnIndex;
	}

	public boolean isInvalid() {
		return invalid;
	}

	public void setInvalid(boolean invalid) {
		this.invalid = invalid;
	}

	public boolean isContainPic() {
		return containPic;
	}

	public void setContainPic(boolean containPic) {
		this.containPic = containPic;
	}

	public String getPictureStyle() {
		return pictureStyle;
	}

	public void setPictureStyle(String pictureStyle) {
		this.pictureStyle = pictureStyle;
	}

	public String getPictureId() {
		return pictureId;
	}

	public void setPictureId(String pictureId) {
		this.pictureId = pictureId;
	}

	public String getErrormsg() {
		return errormsg;
	}

	public void setErrormsg(String errormsg) {
		this.errormsg = errormsg;
	}

	public int getColspan() {
		return colspan;
	}

	public void setColspan(int colspan) {
		this.colspan = colspan;
	}

	public int getRowspan() {
		return rowspan;
	}

	public void setRowspan(int rowspan) {
		this.rowspan = rowspan;
	}

	public String getColumnStyle() {
		return columnStyle;
	}

	public void setColumnStyle(String columnStyle) {
		this.columnStyle = columnStyle;
	}

	public short getDecimalPlaces() {
		return decimalPlaces;
	}

	public void setDecimalPlaces(short decimalPlaces) {
		this.decimalPlaces = decimalPlaces;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public String getSymbolPosition() {
		return symbolPosition;
	}

	public void setSymbolPosition(String symbolPosition) {
		this.symbolPosition = symbolPosition;
	}

	
	public boolean isContainChart() {
		return containChart;
	}

	public void setContainChart(boolean containChart) {
		this.containChart = containChart;
	}

	public String getChartId() {
		return chartId;
	}

	public void setChartId(String chartId) {
		this.chartId = chartId;
	}
	
	

	public String getChartStyle() {
		return chartStyle;
	}

	public void setChartStyle(String chartStyle) {
		this.chartStyle = chartStyle;
	}
	
	
	

	public List<CellFormAttributes> getInputAttrs() {
		return inputAttrs;
	}

	public void setInputAttrs(List<CellFormAttributes> inputAttrs) {
		this.inputAttrs = inputAttrs;
	}

	public Map<String, String> getSelectItemAttrs() {
		return selectItemAttrs;
	}

	public void setSelectItemAttrs(Map<String, String> selectItemAttrs) {
		this.selectItemAttrs = selectItemAttrs;
	}

	public String getControl() {
		return control;
	}

	public void setControl(String control) {
		this.control = control;
	}
	
	

	public String getDatePattern() {
		return datePattern;
	}

	public void setDatePattern(String datePattern) {
		this.datePattern = datePattern;
		
	}
	
	


	public boolean isHasSaveAttr() {
		return hasSaveAttr;
	}

	public void setHasSaveAttr(boolean hasSaveAttr) {
		this.hasSaveAttr = hasSaveAttr;
	}

	/**
	 * Obtain a human readable representation.
	 * 
	 * @return String Human readable label
	 */
	public String toString() {

		StringBuffer sb = new StringBuffer();
		sb.append("{");
		sb.append("style = " + style);
		sb.append(",");
		sb.append("columnStyle = " + columnStyle);
		sb.append(",");
		sb.append("colspan = " + colspan);
		sb.append(",");
		sb.append("rowspan = " + rowspan);
		sb.append(",");
		sb.append("columnIndex = " + columnIndex);
		sb.append(",");
		sb.append("invalid = " + invalid);
		sb.append(",");
		sb.append("errormsg = " + errormsg);
		sb.append(",");
		sb.append("inputType = " + inputType);
		sb.append(",");
		sb.append("containPic = " + containPic);
		sb.append(",");
		sb.append("pictureId = " + pictureId);
		sb.append(",");
		sb.append("pictureStyle = " + pictureStyle);
		sb.append(",");
		sb.append("decimalPlaces = " + decimalPlaces);
		sb.append(",");
		sb.append("symbol = " + symbol);
		sb.append(",");
		sb.append("symbolPosition = " + symbolPosition);
		sb.append(",");
		sb.append("containChart = " + containChart);
		sb.append(",");
		sb.append("chartId = " + chartId);
		sb.append(",");
		sb.append("chartStyle = " + chartStyle);
		sb.append(",");
		sb.append("control = " + control);
		sb.append(",");
		sb.append("saveAttr = " + hasSaveAttr);
		sb.append("}");
		return sb.toString();
	}
	
}