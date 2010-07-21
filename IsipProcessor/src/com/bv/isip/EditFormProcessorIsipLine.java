package com.bv.isip;



import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import com.bv.isis.console.core.abs.gui.MainWindowInterface;
import com.bv.isis.console.core.abs.processor.ProcessorInterface;
import com.bv.isis.console.core.common.IndexedList;
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
     * Cette méthode encapsule la méthode surchargée pour qu'elle s'applique au
     * noeud Item père et aux noeud frères
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

            // rafraichi les noeuds "frères" si on met à jour une clef
            String table_key_value = TreeNodeFactory.getValueOfParameter(data, "TABLE_KEY");
            String field_value     = TreeNodeFactory.getValueOfParameter(data, "FIELD_VALUE");
            
            if ( table_key_value != null && field_value != null
                    && table_key_value.equals(field_value))
            {
                int count=((GenericTreeObjectNode) node.getParent()).getChildCount();
                Enumeration enum_node = ((GenericTreeObjectNode) node.getParent()).children();
                getMainWindowInterface().setProgressMaximum(count);

                count=0;
                while (enum_node.hasMoreElements()) {
                    //window.setStatus("Mise à jour de tous les noeuds ", null, count);
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
     * du node en cours d'exploration.
     * 
     * Permet l'ajout de champs qui seront renvoyés dans le résultat.
     * 
     * @param parameters_array : tableau de paramètres du processeur
     */
    @Override
    protected void initTable( String[] parameters_array )
            throws InnerException
    {
        super.initTable(parameters_array);
        
        
        if (parameters_array.length >= 2) {
            
            //modifie la définition à la volée
            
            ArrayList<IsisTableColumn> columns = new ArrayList<IsisTableColumn>(Arrays.asList(_tableDefinition.columns));
            ArrayList<IsisForeignKey> fkeys = new ArrayList<IsisForeignKey>(Arrays.asList(_tableDefinition.foreignKeys));
            
            // Récupération du contexte
            GenericTreeObjectNode node = (GenericTreeObjectNode) getSelectedNode();
            
            String[] query_fields = parameters_array[1].split(",");
            
            //ajoute les champs supplémentaire à la definition
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
                    
                    // verification que le nouveau champ a une valeur dans le contexte
                    if ( ! node.getContext(true).containsKey(field) ) {
                        
                        
                        IsisParameter parameter = new IsisParameter(field, "", '"');
                        
                        
                        // ajout de la valeur "virtuelle" au preprocessing du noeud
                        IsisParameter[] preprocessing = node.getPreprocessingData();
                        if ( preprocessing == null ) {
                            IsisParameter[] preprocessing_new = { parameter };
                            node.setPreprocessingData( preprocessing_new );
                        }
                        else {
                            IsisParameter[] preprocessing_new= new IsisParameter[preprocessing.length+1];
                            for (i=0; i < preprocessing.length; i++) {
                                preprocessing_new[i]=preprocessing[i];
                                node.setPreprocessingData( preprocessing_new );
                            }
                            
                            // ajout de la nouvelle variable en dernier
                            preprocessing_new[preprocessing_new.length-1] = parameter;
                            node.setPreprocessingData( preprocessing_new );
                        }
                        
                    }
                    
                    // cas spécial du STATUS
                    //TODO : parametrer au lieu de coder en "dur"
                    if ( field.equals("STATUS") ) {
                        IsisForeignKeyLink link = new IsisForeignKeyLink("STATUS", "Name");
                        fkeys.add(new IsisForeignKey("ETAT", new IsisForeignKeyLink[] {link} ));
                    }
                    
                }
            }
            
            // Mise à jour de la définition avec les colonnes modifiées
            _tableDefinition.columns = columns.toArray(new IsisTableColumn[0]);
            _tableDefinition.foreignKeys = fkeys.toArray(new IsisForeignKey[0]);
            
        }
        
        
        
    }
    
    
}