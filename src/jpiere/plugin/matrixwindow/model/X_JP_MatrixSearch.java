/******************************************************************************
 * Product: iDempiere ERP & CRM Smart Business Solution                       *
 * Copyright (C) 1999-2012 ComPiere, Inc. All Rights Reserved.                *
 * This program is free software, you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY, without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program, if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 * For the text or an alternative of this public license, you may reach us    *
 * ComPiere, Inc., 2620 Augustine Dr. #245, Santa Clara, CA 95054, USA        *
 * or via info@compiere.org or http://www.compiere.org/license.html           *
 *****************************************************************************/
/** Generated Model - DO NOT CHANGE */
package jpiere.plugin.matrixwindow.model;

import java.sql.ResultSet;
import java.util.Properties;
import org.compiere.model.*;

/** Generated Model for JP_MatrixSearch
 *  @author iDempiere (generated) 
 *  @version Release 2.1 - $Id$ */
public class X_JP_MatrixSearch extends PO implements I_JP_MatrixSearch, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20150921L;

    /** Standard Constructor */
    public X_JP_MatrixSearch (Properties ctx, int JP_MatrixSearch_ID, String trxName)
    {
      super (ctx, JP_MatrixSearch_ID, trxName);
      /** if (JP_MatrixSearch_ID == 0)
        {
			setAD_Field_ID (0);
			setAD_Tab_ID (0);
// @0|AD_Tab_ID@
			setColumnSpan (0);
// 2
			setIsMandatory (true);
// Y
			setJP_MatrixSearch_ID (0);
			setJP_MatrixWindow_ID (0);
			setSeqNo (0);
// @SQL=SELECT COALESCE(MAX(SeqNo),0)+10 AS DefaultValue FROM JP_MatrixSearch WHERE JP_MatrixWindow_ID=@JP_MatrixWindow_ID@
			setXPosition (0);
// 1
        } */
    }

    /** Load Constructor */
    public X_JP_MatrixSearch (Properties ctx, ResultSet rs, String trxName)
    {
      super (ctx, rs, trxName);
    }

    /** AccessLevel
      * @return 4 - System 
      */
    protected int get_AccessLevel()
    {
      return accessLevel.intValue();
    }

    /** Load Meta Data */
    protected POInfo initPO (Properties ctx)
    {
      POInfo poi = POInfo.getPOInfo (ctx, Table_ID, get_TrxName());
      return poi;
    }

    public String toString()
    {
      StringBuffer sb = new StringBuffer ("X_JP_MatrixSearch[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	public org.compiere.model.I_AD_Field getAD_Field() throws RuntimeException
    {
		return (org.compiere.model.I_AD_Field)MTable.get(getCtx(), org.compiere.model.I_AD_Field.Table_Name)
			.getPO(getAD_Field_ID(), get_TrxName());	}

	/** Set Field.
		@param AD_Field_ID 
		Field on a database table
	  */
	public void setAD_Field_ID (int AD_Field_ID)
	{
		if (AD_Field_ID < 1) 
			set_Value (COLUMNNAME_AD_Field_ID, null);
		else 
			set_Value (COLUMNNAME_AD_Field_ID, Integer.valueOf(AD_Field_ID));
	}

	/** Get Field.
		@return Field on a database table
	  */
	public int getAD_Field_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_AD_Field_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_AD_Tab getAD_Tab() throws RuntimeException
    {
		return (org.compiere.model.I_AD_Tab)MTable.get(getCtx(), org.compiere.model.I_AD_Tab.Table_Name)
			.getPO(getAD_Tab_ID(), get_TrxName());	}

	/** Set Tab.
		@param AD_Tab_ID 
		Tab within a Window
	  */
	public void setAD_Tab_ID (int AD_Tab_ID)
	{
		if (AD_Tab_ID < 1) 
			set_Value (COLUMNNAME_AD_Tab_ID, null);
		else 
			set_Value (COLUMNNAME_AD_Tab_ID, Integer.valueOf(AD_Tab_ID));
	}

	/** Get Tab.
		@return Tab within a Window
	  */
	public int getAD_Tab_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_AD_Tab_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Column Span.
		@param ColumnSpan 
		Number of column for a box of field
	  */
	public void setColumnSpan (int ColumnSpan)
	{
		set_Value (COLUMNNAME_ColumnSpan, Integer.valueOf(ColumnSpan));
	}

	/** Get Column Span.
		@return Number of column for a box of field
	  */
	public int getColumnSpan () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_ColumnSpan);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Default Logic.
		@param DefaultValue 
		Default value hierarchy, separated by ;
	  */
	public void setDefaultValue (String DefaultValue)
	{
		set_Value (COLUMNNAME_DefaultValue, DefaultValue);
	}

	/** Get Default Logic.
		@return Default value hierarchy, separated by ;
	  */
	public String getDefaultValue () 
	{
		return (String)get_Value(COLUMNNAME_DefaultValue);
	}

	/** Set Description.
		@param Description 
		Optional short description of the record
	  */
	public void setDescription (String Description)
	{
		set_Value (COLUMNNAME_Description, Description);
	}

	/** Get Description.
		@return Optional short description of the record
	  */
	public String getDescription () 
	{
		return (String)get_Value(COLUMNNAME_Description);
	}

	/** Set Mandatory.
		@param IsMandatory 
		Data entry is required in this column
	  */
	public void setIsMandatory (boolean IsMandatory)
	{
		set_Value (COLUMNNAME_IsMandatory, Boolean.valueOf(IsMandatory));
	}

	/** Get Mandatory.
		@return Data entry is required in this column
	  */
	public boolean isMandatory () 
	{
		Object oo = get_Value(COLUMNNAME_IsMandatory);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set JPiere Matrix Search.
		@param JP_MatrixSearch_ID JPiere Matrix Search	  */
	public void setJP_MatrixSearch_ID (int JP_MatrixSearch_ID)
	{
		if (JP_MatrixSearch_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_JP_MatrixSearch_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_JP_MatrixSearch_ID, Integer.valueOf(JP_MatrixSearch_ID));
	}

	/** Get JPiere Matrix Search.
		@return JPiere Matrix Search	  */
	public int getJP_MatrixSearch_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_MatrixSearch_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set JP_MatrixSearch_UU.
		@param JP_MatrixSearch_UU JP_MatrixSearch_UU	  */
	public void setJP_MatrixSearch_UU (String JP_MatrixSearch_UU)
	{
		set_ValueNoCheck (COLUMNNAME_JP_MatrixSearch_UU, JP_MatrixSearch_UU);
	}

	/** Get JP_MatrixSearch_UU.
		@return JP_MatrixSearch_UU	  */
	public String getJP_MatrixSearch_UU () 
	{
		return (String)get_Value(COLUMNNAME_JP_MatrixSearch_UU);
	}

	public I_JP_MatrixWindow getJP_MatrixWindow() throws RuntimeException
    {
		return (I_JP_MatrixWindow)MTable.get(getCtx(), I_JP_MatrixWindow.Table_Name)
			.getPO(getJP_MatrixWindow_ID(), get_TrxName());	}

	/** Set Matrix Window.
		@param JP_MatrixWindow_ID Matrix Window	  */
	public void setJP_MatrixWindow_ID (int JP_MatrixWindow_ID)
	{
		if (JP_MatrixWindow_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_JP_MatrixWindow_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_JP_MatrixWindow_ID, Integer.valueOf(JP_MatrixWindow_ID));
	}

	/** Get Matrix Window.
		@return Matrix Window	  */
	public int getJP_MatrixWindow_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_MatrixWindow_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Sequence.
		@param SeqNo 
		Method of ordering records; lowest number comes first
	  */
	public void setSeqNo (int SeqNo)
	{
		set_Value (COLUMNNAME_SeqNo, Integer.valueOf(SeqNo));
	}

	/** Get Sequence.
		@return Method of ordering records; lowest number comes first
	  */
	public int getSeqNo () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_SeqNo);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set X Position.
		@param XPosition 
		Absolute X (horizontal) position in 1/72 of an inch
	  */
	public void setXPosition (int XPosition)
	{
		set_Value (COLUMNNAME_XPosition, Integer.valueOf(XPosition));
	}

	/** Get X Position.
		@return Absolute X (horizontal) position in 1/72 of an inch
	  */
	public int getXPosition () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_XPosition);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}
}