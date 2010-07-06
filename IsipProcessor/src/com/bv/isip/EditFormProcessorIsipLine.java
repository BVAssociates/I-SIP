package com.bv.isip;



import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import com.bv.isis.console.core.abs.gui.MainWindowInterface;
import com.bv.isis.console.core.abs.processor.ProcessorInterface;
import com.bv.isis.console.core.common.InnerException;
import com.bv.isis.console.node.GenericTreeObjectNode;
import com.bv.isis.console.node.TreeNodeFactory;
import com.bv.isis.corbacom.IsisForeignKey;
import com.bv.isis.corbacom.IsisForeignKeyLink;
import com.bv.isis.corbacom.IsisParameter;
import com.bv.isis.corbacom.IsisTableColumn;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;

public class EditFormProcessorIsipLine extends EditFormProcessor {

    /**
     * Isip contructor
     *
     * @param  closeable
     */
	public EditFormProcessorIsipLine() {
		super();
		
	}


    @Override
	public ProcessorInterface duplicate() {
		
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"IsipTest", "duplicate");
		
		trace_methods.beginningOfMethod();

		trace_methods.endOfMethod();
		return new EditFormProcessorIsipLine();
	}

    /**
     * Cette m�thode encapsule la m�thode surcharg�e pour qu'elle s'applique au
     * noeud Item p�re et aux noeud fr�res
     * 
     * @param node
     * @param refresh
     * @throws com.bv.isis.console.core.common.InnerException
     */
    @Override
	protected void refreshLabel(GenericTreeObjectNode node, boolean refresh)
            throws InnerException
    {
        IsisParameter[] data=node.getObjectParameters();
        try {
            super.refreshLabel((GenericTreeObjectNode)node.getParent().getParent(),refresh);

            // rafraichi les noeuds "fr�res" si on met � jour une clef
            if (TreeNodeFactory.getValueOfParameter(data, "TABLE_KEY").equals(
                    TreeNodeFactory.getValueOfParameter(data, "FIELD_VALUE")))
            {
                MainWindowInterface window=getMainWindowInterface();
                int count=((GenericTreeObjectNode) node.getParent()).getChildCount();
                Enumeration enum_node = ((GenericTreeObjectNode) node.getParent()).children();
                getMainWindowInterface().setProgressMaximum(count);

                count=0;
                while (enum_node.hasMoreElements()) {
                    //window.setStatus("Mise � jour de tous les noeuds ", null, count);
                    count++;
                    super.refreshLabel((GenericTreeObjectNode) enum_node.nextElement(), refresh);
                }
            }
            else {
                super.refreshLabel((GenericTreeObjectNode) node, refresh);
            }

        } catch (InnerException exception) {
            Trace trace_errors = TraceAPI.declareTraceErrors("Console");

			trace_errors.writeTrace(
				"Erreur lors de la modification du Label: " +
				exception.getMessage());
            throw new InnerException("Erreur lors de la modification du Label", exception.getMessage(), exception);
        }
    }
    
    /**
     * Initialise le membre _tableDefinition avec la definition extraite
     * du node en cours d'exploration
     */
    @Override
    protected void initTable( String[] parameters_array )
            throws InnerException
    {
        super.initTable(parameters_array);
        
        
        if (parameters_array.length >= 2) {
            
            //modifie la d�finition � la vol�e
            
            ArrayList<IsisTableColumn> columns = new ArrayList<IsisTableColumn>(Arrays.asList(_tableDefinition.columns));
            ArrayList<IsisForeignKey> fkeys = new ArrayList<IsisForeignKey>(Arrays.asList(_tableDefinition.foreignKeys));
                        
            String[] query_fields = parameters_array[1].split(",");
            
            for (int i=0; i < query_fields.length ; i++ ) {
                
                String field = query_fields[i];
                
                boolean field_found=false;
                
                Iterator<IsisTableColumn> iter = columns.iterator();
                while (iter.hasNext()) {
                    IsisTableColumn col = iter.next();
                    
                    if ( field.equals(col.name) ) {
                        field_found=true;
                    }
                }
                
                if ( ! field_found ) {
                    
                    // SIZE="10s" par defaut
                    //TODO : parametrer au lieu de coder en "dur"
                    columns.add(new IsisTableColumn(field, 10, 's'));
                    
                    // cas sp�cial du STATUS
                    //TODO : parametrer au lieu de coder en "dur"
                    if ( field.equals("STATUS") ) {
                        IsisForeignKeyLink link = new IsisForeignKeyLink("STATUS", "Name");
                        fkeys.add(new IsisForeignKey("ETAT", new IsisForeignKeyLink[] {link} ));
                    }
                    
                }
            }
            
            // Mise � jour de la d�finition avec les colonnes modifi�es
            _tableDefinition.columns = columns.toArray(_tableDefinition.columns);
            _tableDefinition.foreignKeys = fkeys.toArray(_tableDefinition.foreignKeys);
            
        }
        
        
        
    }
    
    
}