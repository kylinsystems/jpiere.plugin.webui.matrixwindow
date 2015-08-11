/******************************************************************************
 * Product: JPiere(Japan + iDempiere)                                         *
 * Copyright (C) Hideaki Hagiwara (h.hagiwara@oss-erp.co.jp)                  *
 *                                                                            *
 * This program is free software, you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY.                          *
 * See the GNU General Public License for more details.                       *
 *                                                                            *
 * JPiere supported by OSS ERP Solutions Co., Ltd.                            *
 * (http://www.oss-erp.co.jp)                                                 *
 *****************************************************************************/
package jpiere.plugin.matrixwindow.form;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TreeMap;

import org.adempiere.util.GridRowCtx;
import org.adempiere.webui.LayoutUtils;
import org.adempiere.webui.adwindow.AbstractADWindowContent;
import org.adempiere.webui.adwindow.GridView;
import org.adempiere.webui.adwindow.IADTabpanel;
import org.adempiere.webui.component.Button;
import org.adempiere.webui.component.Checkbox;
import org.adempiere.webui.component.NumberBox;
import org.adempiere.webui.editor.WButtonEditor;
import org.adempiere.webui.editor.WEditor;
import org.adempiere.webui.editor.WEditorPopupMenu;
import org.adempiere.webui.editor.WPaymentEditor;
import org.adempiere.webui.editor.WSearchEditor;
import org.adempiere.webui.editor.WTableDirEditor;
import org.adempiere.webui.editor.WebEditorFactory;
import org.adempiere.webui.event.ActionEvent;
import org.adempiere.webui.event.ActionListener;
import org.adempiere.webui.event.ContextMenuListener;
import org.adempiere.webui.panel.CustomForm;
import org.compiere.model.GridField;
import org.compiere.model.GridTab;
import org.compiere.model.PO;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.zkoss.zk.au.out.AuScript;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Cell;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelMap;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.RendererCtrl;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;
import org.zkoss.zul.RowRendererExt;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.impl.XulElement;


/**
 * JPMatrixGridRowRenderer
 *
 * JPIERE-0098
 *
 * @author Hideaki Hagiwara(h.hagiwara@oss-erp.co.jp)
 *
 */
public class JPMatrixGridRowRenderer implements RowRenderer<Map.Entry<Integer,Object>> ,RowRendererExt, RendererCtrl,EventListener<Event>{

	private static final String GRID_ROW_INDEX_ATTR = "grid.row.index";
	private static final String CELL_DIV_STYLE = "height: 100%; cursor: pointer; ";
	private static final String CELL_DIV_STYLE_ALIGN_CENTER = CELL_DIV_STYLE + "text-align:center; ";
	private static final String CELL_DIV_STYLE_ALIGN_RIGHT = CELL_DIV_STYLE + "text-align:right; ";

	private static final int MAX_TEXT_LENGTH = 60;
	private GridTab gridTab ;
	private int windowNo;
	private JPMatrixDataBinder dataBinder;

	private HashMap<Integer,GridField> columnGridFieldMap;
	private HashMap<Integer,WEditor>   columnEditorMap = new HashMap<Integer,WEditor> ();

	private Map<GridField, WEditor> fieldEditorMap = new LinkedHashMap<GridField, WEditor>();
	private Map<GridField, WEditor> readOnlyEditors = new LinkedHashMap<GridField, WEditor>();

	private RowListener rowListener;

	private Grid grid = null;
	private GridView gridView = null;

	private Row currentRow;

	private boolean editing = false;

	private AbstractADWindowContent m_windowPanel;
	private IADTabpanel adTabpanel;
	private ActionListener buttonListener;

	private ListModelMap<Object, Object>  viewModel;

	private ListModelMap<Object, Object> convertionTable ;

	private int columnsSize=0;

	private CustomForm form ;

	private JPiereMatrixWindow matrixWindow;

	public JPMatrixGridRowRenderer(ListModelMap<Object, Object> viewModel ,ListModelMap<Object, Object>  convetionTable
			,HashMap<Integer,PO> tableModel, HashMap<Integer,PO> dirtyModel,CustomForm form, JPiereMatrixWindow matrixWindow)
	{
		this.viewModel = viewModel;
		this.convertionTable = convetionTable;
		this.windowNo = form.getWindowNo();//Need to create process dialog.
		this.form = form;
		this.matrixWindow = matrixWindow;
		this.dataBinder = new JPMatrixDataBinder(viewModel,convetionTable,tableModel,dirtyModel,form);
	}


	private Component createReadonlyCheckbox(Object value) {
		Checkbox checkBox = new Checkbox();
		if (value != null && "true".equalsIgnoreCase(value.toString()))
			checkBox.setChecked(true);
		else
			checkBox.setChecked(false);
		checkBox.setDisabled(true);
		return checkBox;
	}


	/**
	 * Get display text of a field. when field have isDisplay = false always return empty string, except isForceGetValue = true
	 * @param value
	 * @param gridField
	 * @param rowIndex
	 * @param isForceGetValue
	 * @return
	 */
	private String getDisplayText(Object value, GridField gridField, int rowIndex, boolean isForceGetValue)
	{
		if (value == null)
			return "";

		if (rowIndex >= 0) {
			GridRowCtx gridRowCtx = new GridRowCtx(Env.getCtx(), gridField.getGridTab(), rowIndex);
			if (!isForceGetValue && !gridField.isDisplayed(gridRowCtx, true)) {
				return "";
			}
		}

		if (gridField.isEncryptedField())
		{
			return "********";
		}
		else if (readOnlyEditors.get(gridField) != null)
		{
			WEditor editor = readOnlyEditors.get(gridField);
			return editor.getDisplayTextForGridView(value);
		}
    	else
    		return value.toString();
	}


	/**
	 * get component to display value of a field.
	 * when display is boolean or button, return correspond component
	 * other return a label with text get from {@link #getDisplayText(Object, GridField, int, boolean)}
	 * @param rowIndex
	 * @param value
	 * @param gridField
	 * @param isForceGetValue
	 * @return
	 */
	private Component getDisplayComponent(int rowIndex, Object value, GridField gridField, boolean isForceGetValue) {
		Component component;
		if (gridField.getDisplayType() == DisplayType.YesNo) {
			component = createReadonlyCheckbox(value);
		} else if (gridField.getDisplayType() == DisplayType.Button) {
			GridRowCtx gridRowCtx = new GridRowCtx(Env.getCtx(), gridTab, rowIndex);
			WButtonEditor editor = new WButtonEditor(gridField, rowIndex);
			editor.setValue(gridTab.getValue(rowIndex, gridField.getColumnName()));
			editor.setReadWrite(gridField.isEditable(gridRowCtx, true,true));
			editor.getComponent().setAttribute(GRID_ROW_INDEX_ATTR, rowIndex);
			editor.addActionListener(buttonListener);
			component = editor.getComponent();
		} else {
			String text = getDisplayText(value, gridField, rowIndex, isForceGetValue);

			Label label = new Label();
			setLabelText(text, label);

			component = label;
		}
		return component;
	}



	private void setLabelText(String text, Label label) {
		String display = text;
		if (text != null && text.length() > MAX_TEXT_LENGTH)
			display = text.substring(0, MAX_TEXT_LENGTH - 3) + "...";
		// since 5.0.8, the org.zkoss.zhtml.Text is encoded by default
//		if (display != null)
//			display = XMLs.encodeText(display);
		label.setValue(display);
		if (text != null && text.length() > MAX_TEXT_LENGTH)
			label.setTooltiptext(text);
	}


	/**
	 * RowListener
	 *
	 * In case you want to set a row event
	 *
	 */
	static class RowListener implements EventListener<Event> {

		private Grid _grid;

		private String[] yx ;
		private int x;
		private int y;

		public int getY()
		{
			return y;
		}

		public int getX()
		{
			return x;
		}


		public RowListener(Grid grid) {
			_grid = grid;
		}

		public void onEvent(Event event) throws Exception {

			if(event.getTarget() instanceof Cell)//Get Row(Y) and Column(X) info, When User Clicked.
			{
				yx = ((Cell)event.getTarget()).getId().split("_");
	        	y =Integer.valueOf(yx[0]).intValue();
	            x =Integer.valueOf(yx[1]).intValue();
			}


			if (Events.ON_CLICK.equals(event.getName())) {
				if (Executions.getCurrent().getAttribute("gridView.onSelectRow") != null)
					return;
				Event evt = new Event(Events.ON_CLICK, _grid, event.getTarget());
				Events.sendEvent(_grid, evt);
				evt.stopPropagation();

			}
			else if (Events.ON_DOUBLE_CLICK.equals(event.getName())) {
				Event evt = new Event(Events.ON_DOUBLE_CLICK, _grid, _grid);
				Events.sendEvent(_grid, evt);
			}
			else if (Events.ON_OK.equals(event.getName())) {
				Event evt = new Event(Events.ON_OK, _grid, _grid);
				Events.sendEvent(_grid, evt);
			}
		}
	}


	@Override
	public void render(Row row, Map.Entry<Integer,Object> dataEntry, int rowIndex) throws Exception
	{
		//render() method is called out per row. in other words, render() method is called only the number of rows.

		@SuppressWarnings("unchecked")
		TreeMap<Integer,Object> data = (TreeMap<Integer,Object>)dataEntry.getValue();

		List<Component> comps = row.getChildren();
		int size = comps.size();
		if(size > 0)
		{
			comps.remove(0);
		}

		if (grid == null)
			grid = (Grid) row.getParent().getParent();

		if (rowListener == null)
			rowListener = new RowListener((Grid)row.getParent().getParent());


		@SuppressWarnings("unchecked")
		TreeMap<Integer,Object>  treeMap = (TreeMap<Integer,Object>)convertionTable.get(data.get(0));
		Cell div = null;
		WEditor editor = null;
		String divStyle = CELL_DIV_STYLE;

		//Edit Mode start
		if(matrixWindow.getEditMode().equals(JPiereMatrixWindow.EDITMODE_EDIT))
		{
			for (int i = 0; i < columnsSize; i++)
			{
				if (fieldEditorMap.get(columnGridFieldMap.get(i)) == null)
				{
					editor = WebEditorFactory.getEditor(columnGridFieldMap.get(i), true);
					fieldEditorMap.put(columnGridFieldMap.get(i), editor);
					if (editor instanceof WButtonEditor) {
						((WButtonEditor)editor).addActionListener(buttonListener);
					}else{
						fieldEditorMap.put(columnGridFieldMap.get(i), editor);
					}

					//readonly for display text
					WEditor readOnlyEditor = WebEditorFactory.getEditor(columnGridFieldMap.get(i), true);
					readOnlyEditor.setReadWrite(false);
					readOnlyEditors.put(columnGridFieldMap.get(i), readOnlyEditor);

				}//if

				columnEditorMap.put(i, WebEditorFactory.getEditor(columnGridFieldMap.get(i), true));


				div = new Cell();
				divStyle = CELL_DIV_STYLE;

				if (treeMap.get(i) != null )
				{
					Component component = getDisplayComponent(rowIndex, data.get(i), columnGridFieldMap.get(i), false);
					if (component instanceof Button)
					{
						WButtonEditor button  = (WButtonEditor)WebEditorFactory.getEditor(columnGridFieldMap.get(i), true);
						button.addActionListener(buttonListener);
						button.setADTabpanel(adTabpanel);
						button.setValue(treeMap.get(i)); // Set Record ID in Button for process
						div.appendChild(button.getComponent());
					}else{
						div.appendChild(component);
					}

					div.setAttribute("display.component", component);
					div.setId(String.valueOf(row.getIndex())+"_"+String.valueOf(i));//Set Row(Y-axis) and Column(X-axis) in ID of Cell(div)
					div.addEventListener(Events.ON_OK, this);//OnEvent()

					if (DisplayType.YesNo ==  columnGridFieldMap.get(i).getDisplayType() || DisplayType.Image ==  columnGridFieldMap.get(i).getDisplayType()) {
						divStyle = CELL_DIV_STYLE_ALIGN_CENTER;
					}
					else if (DisplayType.isNumeric(columnGridFieldMap.get(i).getDisplayType())) {
						divStyle = CELL_DIV_STYLE_ALIGN_RIGHT;
					}

					div.setStyle(divStyle);
					div.setWidth("100%");
					div.setAttribute("columnName", columnGridFieldMap.get(i).getColumnName());
					div.addEventListener(Events.ON_CLICK, rowListener);
//					div.addEventListener(Events.ON_DOUBLE_CLICK, rowListener);
					row.appendChild(div);

				}else{//when there are not data

					Component component = new Label("");
					div.appendChild(component);
					div.setAttribute("display.component", component);
					div.setStyle(divStyle);
					div.setWidth("100%");
					div.setAttribute("columnName", columnGridFieldMap.get(i).getColumnName());
					div.addEventListener(Events.ON_OK, this);//OnEvent()
					row.appendChild(div);

				}//if
			}//for

			row.setStyle("cursor:pointer");
			row.setTooltiptext("Row " + (rowIndex+1));

		}else if(matrixWindow.getEditMode().equals(JPiereMatrixWindow.EDITMODE_READ)){


			for (int i = 0; i < columnsSize; i++)
			{
				if (fieldEditorMap.get(columnGridFieldMap.get(i)) == null)
				{
					//readonly for display text
					WEditor readOnlyEditor = WebEditorFactory.getEditor(columnGridFieldMap.get(i), true);
					readOnlyEditor.setReadWrite(false);
					readOnlyEditors.put(columnGridFieldMap.get(i), readOnlyEditor);
				}//if

				div = new Cell();
				divStyle = CELL_DIV_STYLE;

				if (treeMap.get(i) != null )
				{
					editor = WebEditorFactory.getEditor(columnGridFieldMap.get(i), true);

					Component component = getDisplayComponent(rowIndex, data.get(i), columnGridFieldMap.get(i), false);
					if (component instanceof Button)
					{
						WButtonEditor button  = (WButtonEditor)WebEditorFactory.getEditor(columnGridFieldMap.get(i), true);
						button.setVisible(false);
					}else{
						div.appendChild(component);
					}
					div.setAttribute("display.component", component);

					if (DisplayType.YesNo ==  columnGridFieldMap.get(i).getDisplayType() || DisplayType.Image ==  columnGridFieldMap.get(i).getDisplayType()) {
						divStyle = CELL_DIV_STYLE_ALIGN_CENTER;
					}
					else if (DisplayType.isNumeric(columnGridFieldMap.get(i).getDisplayType())) {
						divStyle = CELL_DIV_STYLE_ALIGN_RIGHT;
					}

					div.setStyle(divStyle);
					div.setWidth("100%");
					row.appendChild(div);

				}else{//when there are not data

					Component component = new Label("");
					div.appendChild(component);
					div.setAttribute("display.component", component);
					div.setStyle(divStyle);
					div.setWidth("100%");
					row.appendChild(div);

				}//if
			}//for

		}else if(matrixWindow.getEditMode().equals(JPiereMatrixWindow.EDITMODE_TEST)){

			for (int i = 0; i < columnsSize; i++)
			{
				div = new Cell();

				if (treeMap.get(i) != null )
				{
					editor = WebEditorFactory.getEditor(columnGridFieldMap.get(i), true);

					if (editor instanceof WButtonEditor)
					{
						((WButtonEditor)editor).addActionListener(buttonListener);
						((WButtonEditor)editor).setADTabpanel(adTabpanel);
						editor.setValue(treeMap.get(i)); // Set Record ID in Button for process
					}else{
						editor.setValue(data.get(i));
					}

					editor.getComponent().setId(String.valueOf(row.getIndex())+"_"+String.valueOf(i));//Set Row(Y-axis) and Column(X-axis) in ID
					if(columnGridFieldMap.get(i).isReadOnly() || i==0)
					{
						editor.setReadWrite(false);
						div.appendChild(new Label(editor.getDisplay()));

						if(editor.getComponent() instanceof NumberBox)
						{
							div.setStyle(CELL_DIV_STYLE_ALIGN_RIGHT);
						}

					}else{

						editor.addValueChangeListener(dataBinder);
						fieldEditorMap.put(columnGridFieldMap.get(i), editor);//Create WEditor Map that is edit field only.
						Component component = getCellComponent(rowIndex, data.get(i), columnGridFieldMap.get(i), false);
						div.appendChild(editor.getComponent());
						div.setAttribute("display.component", component);
						div.setId(String.valueOf(row.getIndex())+"_"+String.valueOf(i));//Set Row(Y-axis) and Column(X-axis) in ID of Cell(div)

						editor.getComponent().addEventListener(Events.ON_OK, this);//OnEvent()

						div.setStyle(divStyle);
						div.setWidth("100%");
						div.setAttribute("columnName", columnGridFieldMap.get(i).getColumnName());
						((HtmlBasedComponent)div.getChildren().get(0)).setWidth("100%");

					}

					row.appendChild(div);

				}else{//when there are not data

					Component component = new Label("");
					div.appendChild(component);
					div.setAttribute("display.component", component);
					div.setStyle(divStyle);
					div.setWidth("100%");
					div.setAttribute("columnName", columnGridFieldMap.get(i).getColumnName());
					row.appendChild(div);

				}//if

			}//for

		}//Edit Mode End

	}

	public void setcColumnsSize(int size)
	{
		columnsSize = size;
	}

	private Component getCellComponent(int rowIndex ,Object value, GridField gridField, boolean isGridViewCustomized)
	{

		Component component ;
		if (gridField.getDisplayType() == DisplayType.YesNo) {
			component = createReadonlyCheckbox(value);
		} else if (gridField.getDisplayType() == DisplayType.Button) {
			GridRowCtx gridRowCtx = new GridRowCtx(Env.getCtx(), gridTab, rowIndex);
			WButtonEditor editor = new WButtonEditor(gridField, rowIndex);
			editor.setValue(gridTab.getValue(rowIndex, gridField.getColumnName()));
			editor.setReadWrite(gridField.isEditable(gridRowCtx, true,true));
			editor.getComponent().setAttribute(GRID_ROW_INDEX_ATTR, rowIndex);
			editor.addActionListener(buttonListener);
			component = editor.getComponent();
		} else {
			String text = getDisplayText(value, gridField, rowIndex, isGridViewCustomized);

			Label label = new Label();
			setLabelText(text, label);

			component = label;
		}
		return component;
	}


	//These variables is used by onEvent() method only except y,x.
	private String[] yx;
	private int y = 0;
	private int x = 0;
	private NumberBox numberbox;
	private int minY = 0;
	private int maxY = 0;


	/**
	 * Enter Key Event(onOK)
	 */
	@Override
	public void onEvent(Event event) throws Exception {

		if(!event.getName().equals(Events.ON_OK))
			return;

		//Get Row(Y) and Column(X) info
		if(event.getTarget() instanceof Decimalbox)
		{
			yx = event.getTarget().getParent().getId().split("_");

		}else if(event.getTarget() instanceof Textbox){

			if(event.getTarget().getParent() instanceof Cell)
			{
				yx = event.getTarget().getId().split("_");//TextBox,List,

			}else{
				yx = event.getTarget().getParent().getId().split("_");//Search Editor
			}

		}

		y = Integer.valueOf(yx[0]);
		if(matrixWindow.getEditMode().equals(JPiereMatrixWindow.EDITMODE_EDIT))
		{
			x = rowListener.getX();
		}else{ //EDITMODE_TEST
			x = Integer.valueOf(yx[1]);
		}

        minY = grid.getActivePage() * grid.getPageSize();
        maxY = minY + grid.getPageSize();

        if(maxY > convertionTable.getSize())
        	maxY = convertionTable.getSize();

		for(int i = 0 ; i < grid.getPageSize(); i++)
     	{
         	if(y == maxY-1)
         		y = minY - 1 ;

      		if(getRawData(++y,x) == null){
      			continue;
     		}else{

     			if(matrixWindow.getEditMode().equals(JPiereMatrixWindow.EDITMODE_EDIT))
     			{
	 				editNextRow(y,x);
	 				event.stopPropagation();
	 				return;

     			}else{//EDITMODE_TEST

     				if(event.getTarget() instanceof Decimalbox)
     				{
     					Cell cell = (Cell)grid.getCell(y, x);
     					if (cell.getChildren().get(0) instanceof NumberBox)
     					{
     						numberbox = (NumberBox)cell.getChildren().get(0);
     	    	        	numberbox.focus();
     	    	        	numberbox.getDecimalbox().select();
     	    				return;
     	        		}
     					return;
     				}else if(event.getTarget() instanceof Textbox){
     					((Cell)grid.getCell(y, x)).focus();
       					return;
     				}else{
     					((Cell)grid.getCell(y, x)).focus();
     					return;
     				}
     			}

     		}//if
     	}//for
	}//onEvent


	/* RendererCtrl */
	@Override
	public void doTry() {

	}

	@Override
	public void doCatch(Throwable ex) throws Throwable {

	}

	@Override
	public void doFinally() {

	}


	/* RowRendererExt */
	@Override
	public Row newRow(Grid grid) {
		return null;
	}

	@Override
	public Component newCell(Row row) {
		return null;
	}

	@Override
	public int getControls() {
		return 0;
	}


	/**
	 * Detach all editor and optionally set the current value of the editor as cell label.
	 * @param updateCellLabel
	 */
	public void stopEditing() {
		if (!editing) {
			return;
		} else {
			editing = false;
		}

		String string = null;
		WEditor editor= null;
		for (Entry<Integer, WEditor> entry : columnEditorMap.entrySet())
		{
			editor = entry.getValue();
			string = null;
			if(editor instanceof WButtonEditor)
			{
            	continue;

			}else if(editor instanceof WSearchEditor || editor instanceof WTableDirEditor || editor instanceof WPaymentEditor){

            	string =getDisplayText(editor.getValue(), editor.getGridField(), -1, false);

			}else if(editor.getValue() != null){

            	string = editor.getValue().toString();

            }else{

            	continue;
            }

			if (entry.getValue().getComponent().getParent() != null)
			{
				Component child = entry.getValue().getComponent();
				Cell div = null;
				while (div == null && child != null)
				{
					Component parent = child.getParent();
					if (parent instanceof Cell && parent.getParent() instanceof Row)
						div = (Cell)parent;
					else
						child = parent;
				}//While

				Label component = new Label(string);
				entry.getValue().getComponent().detach();
				entry.getValue().removeValuechangeListener(dataBinder);
				if (component.getParent() == null || component.getParent() != div)
				{
					div.appendChild(component);
				}else if (!component.isVisible()) {
					component.setVisible(true);
				}
			}

		}//for
	}


	/**
	 * @param row
	 */
	public void setCurrentRow(Row row) {

		currentRow = row;

		if (editing) {
			stopEditing();
			editCurrentRow();
		}

		String script = "jq('#"+row.getUuid()+"').addClass('highlight').siblings().removeClass('highlight')";

		Clients.response(new AuScript(script));
	}

	/**
	 * Enter edit mode
	 */
	public void editCurrentRow() {
		if (currentRow != null && currentRow.getParent() != null && currentRow.isVisible()
			&& grid != null && grid.isVisible() && grid.getParent() != null && grid.getParent().isVisible()) {

			y = rowListener.getY();
			x = rowListener.getX();

			editRow();

		}
	}

	private void editNextRow(int y2, int x2) {

		Cell cell = (Cell)grid.getCell(y2, rowListener.getX());
		setCurrentRow((Row)cell.getParent());

		y = y2;
		x = x2;

		editRow();
	}

	private void editRow()
	{
		@SuppressWarnings("unchecked")
		TreeMap<Integer,Object> rowValueMap = (TreeMap<Integer,Object>)viewModel.getElementAt(y).getValue();
		org.zkoss.zul.Columns columns = grid.getColumns();

		//skip selection and indicator column
		for (int i = 0; i < columnEditorMap.size(); i++) {

			GridField gridField = columnGridFieldMap.get(i);

			if ((!gridField.isDisplayedGrid()) || gridField.isToolbarOnlyButton()) {
				continue;
			}

			if (fieldEditorMap.get(gridField) == null)
				fieldEditorMap.put(gridField, WebEditorFactory.getEditor(gridField, true));

			org.zkoss.zul.Column column = (org.zkoss.zul.Column) columns.getChildren().get(i);
			if (column.isVisible()) {
				Cell div = (Cell) currentRow.getChildren().get(i);


				WEditor editor = columnEditorMap.get(i);
				editor.setValue(rowValueMap.get(i));
				editor.getComponent().setId(String.valueOf(y +"_"+ i+"_"));
				editor.getComponent().addEventListener(Events.ON_OK, this);//OnEvent()
				editor.addValueChangeListener(dataBinder);

				gridField.setValue(rowValueMap.get(i),false);

				//TODO:ここのリフレッシュは不要ではないか？→リフレッシュさせる事で、依存するフィールドの内容もリフレッシュさせている様子…要調査。
				if(editor instanceof WTableDirEditor)
				{
        			((WTableDirEditor)editor).getLookup().refresh();
				}else if(editor instanceof WSearchEditor){
//					((WSearchEditor)editor)
					;
				}


				if (div.getChildren().isEmpty() || !(div.getChildren().get(0) instanceof Button))
				{
					div.getChildren().clear();
				}else if (!div.getChildren().isEmpty()) {
					div.getChildren().get(0).setVisible(true);//Button
				}

				Properties ctx = gridField.getVO().ctx;

				if(getRawData(y,i) == null)
	        	{
	        		if (!gridField.isDisplayed(ctx, true)){
						div.removeChild(editor.getComponent());
					}
	        		continue;
	        	}

				if(i == 0)	//Fix Item
				{
					div.appendChild(new Label(editor.getDisplay()));
				}else{
					div.appendChild(editor.getComponent());
					((HtmlBasedComponent)div.getChildren().get(0)).setWidth("100%");
				}



	            //check context
				if (!gridField.isDisplayed(ctx, true)){
					div.removeChild(editor.getComponent());
				}

				//Pop up menu
				WEditorPopupMenu popupMenu = editor.getPopupMenu();
	            if (popupMenu != null)
	            {
	            	popupMenu.addMenuListener((ContextMenuListener)editor);
	            	div.appendChild(popupMenu);
	            	popupMenu.addContextElement((XulElement) editor.getComponent());


					List<Component> listcomp = popupMenu.getChildren();
					Menuitem menuItem = null;
					String image = null;
					for(Component comp : listcomp)
					{
						if(comp instanceof Menuitem)
						{
							menuItem = (Menuitem)comp;
							image = menuItem.getImage();
							if(image.endsWith("Zoom16.png")||image.endsWith("Refresh16.png")
									|| image.endsWith("New16.png") || image.endsWith("InfoBPartner16.png"))
							{
								menuItem.setVisible(true);
							}else{
								menuItem.setVisible(false);
							}
						}
					}//for
	            }


//				editor.setReadWrite(gridFeld.isEditableGrid(true));
				editor.setReadWrite(true);

				if(i == x)
				{

					if(div.getChildren().get(0) instanceof NumberBox)
					{
						numberbox = (NumberBox)div.getChildren().get(0);
	    	        	numberbox.focus();
	    	        	numberbox.getDecimalbox().select();

//					}else if(div.getChildren().get(0) instanceof Textbox){
//						Textbox textbox = (Textbox)div.getChildren().get(0);
//						int cols =textbox.getCols();
//						List<Component> aa = textbox.getChildren();
//						textbox.select();
//						div.focus();

					}else{
						div.focus();
					}
				}
			}
		}
		editing = true;
	}

	private Object getRawData(int y, int x)
	{
		ListModelMap.Entry<Object, Object> convertionTableRow = convertionTable.getElementAt(y);
    	@SuppressWarnings("unchecked")
		TreeMap<Integer,Object> convertionTableRowData = (TreeMap<Integer,Object>)convertionTableRow.getValue();

		return convertionTableRowData.get(x);
	}

	/**
	 * @return Row
	 */
	public Row getCurrentRow() {
		Cell cell = (Cell)grid.getCell(rowListener.getY(), rowListener.getX());
		return (Row)cell.getParent();
	}

	/**
	 * @return boolean
	 */
	public boolean isEditing() {
		return editing;
	}



	/**
	 *
	 * setADWindowPanel Method
	 *
	 * Need to Create Process Dialog
	 *
	 */
	public void setADWindowPanel(AbstractADWindowContent windowPanel,IADTabpanel adTabpanel) {
		if (this.m_windowPanel == windowPanel)
			return;

		this.m_windowPanel = windowPanel;
		this.adTabpanel = adTabpanel;

		buttonListener = new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				WButtonEditor editor = (WButtonEditor) event.getSource();
				String stringRecord_ID = editor.getDisplay();//valueの取得

				JPiereMatrixWindowProcessModelDialog dialog = new JPiereMatrixWindowProcessModelDialog(windowNo, editor.getProcess_ID(), 0, Integer.parseInt(stringRecord_ID), false, matrixWindow);


				if (dialog.isValid())
				{
					//dialog.setWidth("500px");
					dialog.setBorder("normal");
					form.getParent().appendChild(dialog);
//					showBusyMask(dialog);
					LayoutUtils.openOverlappedWindow(form.getParent(), dialog, "middle_center");
					dialog.focus();
				}
				else
				{
//					onRefresh(true, false);
				}

			}
		};
	}

	public void setGridView(GridView gridView)
	{
		this.gridView = gridView;
	}

	public void setGridTab(GridTab gridTab)
	{
		this.gridTab = gridTab;
	}

	public void setColumnGridFieldMap(HashMap<Integer,GridField> columnGridFieldMap)
	{
		this.columnGridFieldMap = columnGridFieldMap;
	}

}
