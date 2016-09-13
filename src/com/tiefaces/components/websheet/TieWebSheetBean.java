/*
 * Copyright 2015 TieFaces.
 * Licensed under MIT
 */

package com.tiefaces.components.websheet;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFEvaluationWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.tiefaces.common.FacesUtility;
import com.tiefaces.components.websheet.chart.ChartData;
import com.tiefaces.components.websheet.chart.ChartHelper;
import com.tiefaces.components.websheet.configuration.ExpressionEngine;
import com.tiefaces.components.websheet.configuration.SheetConfiguration;
import com.tiefaces.components.websheet.dataobjects.CachedCells;
import com.tiefaces.components.websheet.dataobjects.CellMap;
import com.tiefaces.components.websheet.dataobjects.FacesRow;
import com.tiefaces.components.websheet.dataobjects.HeaderCell;
import com.tiefaces.components.websheet.service.CellHelper;
import com.tiefaces.components.websheet.service.DataHandler;
import com.tiefaces.components.websheet.service.PicturesHelper;
import com.tiefaces.components.websheet.service.WebSheetLoader;
import com.tiefaces.components.websheet.service.ValidationHandler;

import javax.script.ScriptEngine;

/**
 * Main class for web sheet.
 * 
 * @author Jason Jiang
 *
 */
public class TieWebSheetBean extends TieWebSheetView implements
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3495468356246589276L;

	/** hold instance for columns in current display sheet. */
	private List<String> columns;
	/** hold instance for each body rows in current display sheet. */
	private List<FacesRow> bodyRows;
	/** hold instance for each header rows in current display sheet. */
	private List<List<HeaderCell>> headerRows;
	/** current workbook. */
	private Workbook wb;
	/** current workbook wrapper for formula parser. */
	private XSSFEvaluationWorkbook wbWrapper;
	/** current formula evaluator. */
	private FormulaEvaluator formulaEvaluator;
	/** current dataFormatter. */
	private DataFormatter dataFormatter;
	/** hold data object context. */
	private Map<String, Object> dataContext;
	/** hold pictures for current display sheet. */
	private Map<String, Picture> picturesMap;
	private ChartsData charsData = new ChartsData();

	/** hold cached cells in current display sheet. */
	private CachedCells cachedCells;
	/**
	 * The max column counts across sheets of this workbook. e.g. sheet1 has 3
	 * columns, sheet2 has 5 columns. maxColCounts = 5;
	 */
	private int maxColCounts = 0;
	/** hold configuration for each sheet. */
	private Map<String, SheetConfiguration> sheetConfigMap;
	/** hold scriptEngine instance. */
	private ScriptEngine engine;
	/** hold expressionEngine instance */
	private ExpressionEngine expEngine;
	/** current tab name of display sheet. */
	private String currentTabName;
	/** current top row of display sheet. */
	private int currentTopRow;
	/** current left column of display sheet. */
	private int currentLeftColumn;
	/** weather process full a validation. */
	private Boolean fullValidation = false;
	/** hold instance for loader class. */
	private WebSheetLoader webSheetLoader = null;
	/** hold instance for cell helper class. */
	private CellHelper cellHelper = null;
	/** hold instance for picture helper class. */
	private PicturesHelper picHelper = null;
	/** hold instance for data helper class. */
	private DataHandler dataHandler = null;
	/** hold instance for validation handler class. */
	private ValidationHandler validationHandler = null;
	/** hold instance for chart helper class. */
	private ChartHelper chartHelper = null;
	/**
	 * Client id for whole websheet component. This is the top level client id.
	 * There're tabs and web forms under this top level.
	 */
	private String clientId = null;
	/** Client id for web forms. */
	private String webFormClientId = null;
	/**
	 * Current Excel file type. Only 2 types supported: xls or xlsx.
	 */
	private String excelType = null;
	/** configuration tab name. */
	private String configurationTab = 
			TieWebSheetConstants.TIE_WEBSHEET_CONFIGURATION_SHEET;
	/** logger. */
	private static final Logger log = Logger.getLogger(Thread
			.currentThread().getStackTrace()[0].getClassName());

	/** constructor. Allow for extension. */
	public TieWebSheetBean() {
		log.fine("TieWebSheetBean Constructor");
	}

	/** initialize. */
	@PostConstruct
	public final void init() {
		columns = new ArrayList<String>();
		engine = (ScriptEngine) FacesUtility
				.evaluateExpressionGet("#{tieWebSheetApp.engine}");
		webSheetLoader = new WebSheetLoader(this);
		cellHelper = new CellHelper(this);
		dataHandler = new DataHandler(this);
		validationHandler = new ValidationHandler(this);
		picHelper = new PicturesHelper(this);
		chartHelper = new ChartHelper(this);
		initialLoad();
	}

	/**
	 * assign web form client id.
	 * 
	 * @param pWebFormClientId
	 *            String client id name.
	 */
	public final void setWebFormClientId(final String pWebFormClientId) {
		this.webFormClientId = pWebFormClientId;
	}

	/**
	 * @return web form client Id.
	 */
	public final String getWebFormClientId() {
		return webFormClientId;
	}

	/**
	 * @return client id.
	 */
	public final String getClientId() {
		return clientId;
	}

	/**
	 * 
	 * @param pClientId
	 *            client Id.
	 */
	public final void setClientId(final String pClientId) {
		this.clientId = pClientId;
	}

	public final List<FacesRow> getBodyRows() {
		return bodyRows;
	}

	public final void setBodyRows(final List<FacesRow> pBodyRows) {
		this.bodyRows = pBodyRows;
	}

	/**
	 * 
	 * @return return header row list.
	 */
	public final List<List<HeaderCell>> getHeaderRows() {
		if (headerRows == null) {
			headerRows = new ArrayList<List<HeaderCell>>();
		}
		return headerRows;
	}

	public final void setHeaderRows(
			final List<List<HeaderCell>> pHeaderRows) {
		this.headerRows = pHeaderRows;
	}

	public final void setColumns(final List<String> pColumns) {
		this.columns = pColumns;
	}

	public final Workbook getWb() {
		return wb;
	}
	
	/**
	 * Set up workbook. 
	 * Also create evaluation wrapper.
	 * @param pWb workbook.
	 */

	public final void setWb(final Workbook pWb) {
		this.wb = pWb;
		this.wbWrapper = XSSFEvaluationWorkbook.create((XSSFWorkbook) wb);
	}
	/**
	 * Return evaluation wrapper if needed.
	 * @return wbwrapper.
	 */
	public final XSSFEvaluationWorkbook getWbWrapper() {
		if ((this.wbWrapper == null) && (this.wb != null)) {
			this.wbWrapper = XSSFEvaluationWorkbook
					.create((XSSFWorkbook) wb);
		}
		return wbWrapper;
	}

	public final FormulaEvaluator getFormulaEvaluator() {
		return formulaEvaluator;
	}

	public final void setFormulaEvaluator(
			final FormulaEvaluator pFormulaEvaluator) {
		this.formulaEvaluator = pFormulaEvaluator;
	}

	public final DataFormatter getDataFormatter() {
		return dataFormatter;
	}

	public final void setDataFormatter(final DataFormatter pDataFormatter) {
		this.dataFormatter = pDataFormatter;
	}

	public final List<String> getColumns() {
		return columns;
	}

	public final String getCurrentTabName() {
		return currentTabName;
	}

	public final void setCurrentTabName(final String pCurrentTabName) {
		this.currentTabName = pCurrentTabName;
	}

	public final Boolean getFullValidation() {
		return fullValidation;
	}

	public final void setFullValidation(final Boolean pFullValidation) {
		this.fullValidation = pFullValidation;
	}

	public final CellHelper getCellHelper() {
		return cellHelper;
	}

	public final ScriptEngine getEngine() {
		return engine;
	}

	public ExpressionEngine getExpEngine() {
		return expEngine;
	}

	public final WebSheetLoader getWebSheetLoader() {
		return webSheetLoader;
	}

	public final DataHandler getDataHandler() {
		return dataHandler;
	}

	public final ValidationHandler getValidationHandler() {
		return validationHandler;
	}

	public final PicturesHelper getPicHelper() {
		return picHelper;
	}

	public final ChartHelper getChartHelper() {
		return chartHelper;
	}

	public final String getExcelType() {
		return excelType;
	}

	public final void setExcelType(final String pExcelType) {
		this.excelType = pExcelType;
	}

	public final int getCurrentTopRow() {
		return currentTopRow;
	}

	public final void setCurrentTopRow(final int pCurrentTopRow) {
		this.currentTopRow = pCurrentTopRow;
	}

	public final int getCurrentLeftColumn() {
		return currentLeftColumn;
	}

	public final void setCurrentLeftColumn(final int pCurrentLeftColumn) {
		this.currentLeftColumn = pCurrentLeftColumn;
	}

	public final Map<String, Picture> getPicturesMap() {
		return picturesMap;
	}

	public final void setPicturesMap(
			final Map<String, Picture> pPicturesMap) {
		this.picturesMap = pPicturesMap;
	}

	/**
	 * 
	 * @return charts map.
	 */
	public final Map<String, BufferedImage> getChartsMap() {
		if (charsData.chartsMap == null) {
			charsData.chartsMap = new HashMap<String, BufferedImage>();
		}
		return charsData.chartsMap;
	}

	public final void setChartsMap(
			final Map<String, BufferedImage> pChartsMap) {
		this.charsData.chartsMap = pChartsMap;
	}

	/**
	 * 
	 * @return chart data map.
	 */
	public final Map<String, ChartData> getChartDataMap() {
		if (charsData.chartDataMap == null) {
			charsData.chartDataMap = new HashMap<String, ChartData>();
		}
		return charsData.chartDataMap;
	}

	public final void setChartDataMap(
			final Map<String, ChartData> pChartDataMap) {
		this.charsData.chartDataMap = pChartDataMap;
	}

	/**
	 * 
	 * @return chart anchors map.
	 */
	public final Map<String, ClientAnchor> getChartAnchorsMap() {
		if (charsData.chartAnchorsMap == null) {
			charsData.chartAnchorsMap = new HashMap<String, ClientAnchor>();
		}
		return charsData.chartAnchorsMap;
	}

	public final void setChartAnchorsMap(
			final Map<String, ClientAnchor> pChartAnchorsMap) {
		this.charsData.chartAnchorsMap = pChartAnchorsMap;
	}

	/**
	 * chart position map.
	 * 
	 * @return chartPositionMap.
	 */
	public final Map<String, String> getChartPositionMap() {
		if (charsData.chartPositionMap == null) {
			charsData.chartPositionMap = new HashMap<String, String>();
		}
		return charsData.chartPositionMap;
	}

	public final void setChartPositionMap(
			final Map<String, String> pChartPositionMap) {
		this.charsData.chartPositionMap = pChartPositionMap;
	}

	/**
	 * 
	 * @return cached cells.
	 */
	public final CachedCells getCachedCells() {
		if (cachedCells == null) {
			cachedCells = new CachedCells(this);
		}
		return cachedCells;
	}

	public final void setCachedCells(final CachedCells pCachedCells) {
		this.cachedCells = pCachedCells;
	}

	/**
	 * create datacontext map if needed.
	 * @return map.
	 */
	public final Map<String, Object> getDataContext() {
		return dataContext;
	}

	public final void setDataContext(final Map<String, Object> pDataContext) {
		this.dataContext = pDataContext;
		this.expEngine = new ExpressionEngine(
			this.dataContext);
	}

	/**
	 * 
	 * @return max column counts.
	 */
	public final int getMaxColCounts() {
		if (this.maxColCounts == 0) {
			reCalcMaxColCounts();
		}
		return maxColCounts;
	}

	/**
	 * recalculate max coulumn count across sheets in the workbook.
	 */
	public final void reCalcMaxColCounts() {
		if ((this.sheetConfigMap == null)
				|| (this.sheetConfigMap.size() == 0)) {
			this.maxColCounts = 0;
			return;
		}
		int maxColumns = 0;
		for (SheetConfiguration sheetConfig : this.sheetConfigMap
				.values()) {
			int counts = sheetConfig.getHeaderCellRange().getRightCol()
					- sheetConfig.getHeaderCellRange().getLeftCol() + 1;
			if (maxColumns < counts) {
				maxColumns = counts;
			}
		}
		this.maxColCounts = maxColumns;
	}

	public final String getConfigurationTab() {
		return configurationTab;
	}

	public final void setConfigurationTab(final String pConfigurationTab) {
		this.configurationTab = pConfigurationTab;
	}

	/**
	 * load web sheet from inputStream file.
	 * 
	 * @param inputStream
	 *            input stream file.
	 * @return 1 (success) -1 (failed)
	 */
	public final int loadWebSheet(final InputStream inputStream) {
		return loadWebSheet(inputStream, null);
	}

	/**
	 * load web sheet from inputStream file with data object.
	 * 
	 * @param inputStream
	 *            input stream file.
	 * @param pDataContext
	 *            data object.
	 * @return 1 (success) -1 (failed)
	 */
	public final int loadWebSheet(final InputStream inputStream,
			final Map<String, Object> pDataContext) {
		return webSheetLoader.loadWorkbook(inputStream, pDataContext);
	}

	/**
	 * load web sheet from giving workbook.
	 * 
	 * @param pWb
	 *            workbook.
	 * @return 1 (success) -1 (failed)
	 */

	public final int loadWebSheet(final Workbook pWb) {
		return loadWebSheet(pWb, null);
	}

	/**
	 * load web sheet from giving workbook with data object.
	 * 
	 * @param pWb
	 *            workbook.
	 * @param pDataContext
	 *            data object.
	 * @return 1 (success) -1 (failed)
	 */

	public final int loadWebSheet(final Workbook pWb,
			final Map<String, Object> pDataContext) {
		return webSheetLoader.loadWorkbook(pWb, pDataContext);
	}

	/**
	 * Triggered when user switch the tab. This will load different tab(sheet)
	 * as the current sheet.
	 * 
	 * @param event
	 *            tabchange event.
	 */
	public final void onTabChange(final TabChangeEvent event) {
		String tabName = event.getTab().getTitle();

		int sheetId = webSheetLoader.findTabIndexWithName(tabName);

		if ((getSheetConfigMap() != null)
				&& (sheetId < getSheetConfigMap().size())) {
			webSheetLoader.loadWorkSheet(tabName);
		}
	}

	/** for download file. */
	private StreamedContent exportFile;

	public final StreamedContent getExportFile() {
		return exportFile;
	}

	/** download current workbook. */
	public final void doExport() {
		try {

			webSheetLoader.loadAllFields();
			String fileName = "WebSheetTemplate" + "."
					+ this.getExcelType();
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			wb.write(out);
			InputStream stream = new BufferedInputStream(
					new ByteArrayInputStream(out.toByteArray()));
			exportFile = new DefaultStreamedContent(stream,
					"application/force-download", fileName);

		} catch (Exception e) {
			log.severe("Error in export file : "
					+ e.getLocalizedMessage());
		}
		return;
	}

	/**
	 * triggered before validation process.
	 * 
	 * @param passEmptyCheck
	 *            true(allow pass empty fields) false ( not allow pass empty
	 *            fields).
	 * @return true (pass) false (failed)
	 */
	private boolean preValidation(final boolean passEmptyCheck) {

		String tabName = validationHandler
				.findFirstInvalidSheet(passEmptyCheck);
		if (tabName != null) {
			webSheetLoader.loadWorkSheet(tabName);
			return false;
		}
		return true;
	}

	/**
	 * set full validation flag with javascript for holding in client side.
	 * 
	 * @param fullflag
	 *            true or false
	 */
	private void setFullValidationInView(final Boolean fullflag) {

		Map<String, Object> viewMap = FacesContext.getCurrentInstance()
				.getViewRoot().getViewMap();
		Boolean flag = (Boolean) viewMap.get("fullValidation");
		if ((flag == null) || (!flag.equals(fullflag))) {
			viewMap.put("fullValidation", fullflag);
		}
	}

	/**
	 * Save the current workbooks.
	 */
	public final void doSave() {

		fullValidation = false;
		setFullValidationInView(fullValidation);
		if (!preValidation(true)) {
			log.info("Validation failded before saving");
			return;
		}
		processSave();

	}

	/**
	 * save process. unfinished.
	 * 
	 * @return 1 (success) -1 (failed).
	 */
	private int processSave() {

		return 1;
	}


	/**
	 * Needed a change handler on the note field which doesn't need all the
	 * other code below.
	 * 
	 * @param event
	 *            ajax event.
	 */
	public final void noteChangeEvent(final AjaxBehaviorEvent event) {
		dataHandler.setUnsavedStatus(RequestContext.getCurrentInstance(),
				true);
	}

	/**
	 * Triggered when value in cells changed. e.g. user edit cell.
	 * 
	 * @param event
	 *            ajax event.
	 */
	public final void valueChangeEvent(final AjaxBehaviorEvent event) {

		FacesContext facesContext = FacesContext.getCurrentInstance();
		String tblName = getWebFormClientId();
		UIComponent target = event.getComponent();

		boolean pass = validationHandler.validateCell(target);
		if (pass) {
			// to improve performance, re-validate current row only
			// page validation take times. will happen when change tab(page) or
			// reload page.
			int[] rowcol = cellHelper
					.getRowColFromComponentAttributes(target);
			validationHandler.validateRowInCurrentPage(rowcol[0], true);
			// refresh current page calculation fields
			UIComponent s = facesContext.getViewRoot().findComponent(
					tblName);
			if (s != null) {
				DataTable webDataTable = (DataTable) s;
				int first = webDataTable.getFirst();
				int rowsToRender = webDataTable.getRowsToRender();
				int rowCounts = webDataTable.getRowCount();
				int top = this.getCurrentTopRow();
				int left = this.getCurrentLeftColumn();

				String sheetName = getSheetConfigMap().get(
						this.getCurrentTabName()).getSheetName();
				Sheet sheet1 = getWb().getSheet(sheetName);
				for (int i = first; i <= (first + rowsToRender); i++) {
					if (i < rowCounts) {
						FacesRow dataRow = bodyRows.get(i);
						for (int index = 0; index < dataRow.getCells()
								.size(); index++) {
							Cell poiCell = this.getCellHelper()
									.getPoiCellWithRowColFromCurrentPage(
											i + top, index + left);
							if (poiCell != null) {
								getWebSheetLoader().refreshCachedCell(
										tblName, i, index, sheet1,
										poiCell);
							}
						}
					}

				}
			}
		}
		dataHandler.setUnsavedStatus(RequestContext.getCurrentInstance(),
				true);
	}

	/**
	 * check whether current workbook contain multiple pages.
	 * 
	 * @return true (multiple pages) false ( single page).
	 */
	public final boolean isMultiplePage() {
		if ((bodyRows != null)
				&& (bodyRows.size() > this.getMaxRowsPerPage())) {
			return true;
		}
		return false;
	}

	/**
	 * called before bean gone.
	 */
	@PreDestroy
	public final void finish() {
		log.fine("finishing view webformbean");
		if (FacesContext.getCurrentInstance() == null) {
			log.info("session has gone");
		}

	}

	public final Map<String, SheetConfiguration> getSheetConfigMap() {
		return sheetConfigMap;
	}

	public final void setSheetConfigMap(
			final Map<String, SheetConfiguration> pSheetConfigMap) {
		this.sheetConfigMap = pSheetConfigMap;
	}

	/**
	 * cells map for current display sheet.
	 */
	@SuppressWarnings("rawtypes")
	private Map cellsMap = new CellMap(this);

	@SuppressWarnings("rawtypes")
	public final Map getCellsMap() {
		return cellsMap;
	}

	/**
	 * initial load process. designed for extension.
	 */
	public void initialLoad() {
	}

	/**
	 * Triggered when user click add row button.
	 * 
	 * @param rowIndex
	 *            row index.
	 */
	public final void addRepeatRow(final int rowIndex) {
		this.webSheetLoader.addRepeatRow(rowIndex);
	}

	/**
	 * Triggered when user click delete row button.
	 * 
	 * @param rowIndex
	 *            row index.
	 */
	public final void deleteRepeatRow(final int rowIndex) {
		this.webSheetLoader.deleteRepeatRow(rowIndex);
	}

}
