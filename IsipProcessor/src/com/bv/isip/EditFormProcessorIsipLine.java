package com.bv.isip;



import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import com.bv.isis.console.core.abs.processor.ProcessorInterface;
import com.bv.isis.console.core.common.InnerException;
import com.bv.isis.console.node.GenericTreeObjectNode;
import com.bv.isis.console.node.TreeNodeFactory;
import com.bv.isis.corbacom.IsisParameter;
import java.util.Enumeration;

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
            if (TreeNodeFactory.getValueOfParameter(data, "TABLE_KEY").equals(
                    TreeNodeFactory.getValueOfParameter(data, "FIELD_VALUE"))) {

                Enumeration enum_node = ((GenericTreeObjectNode) node.getParent()).children();
                while (enum_node.hasMoreElements()) {
                    super.refreshLabel((GenericTreeObjectNode) enum_node.nextElement(), refresh);
                }
            }

        } catch (InnerException exception) {
            Trace trace_errors = TraceAPI.declareTraceErrors("Console");

			trace_errors.writeTrace(
				"Erreur lors de la modification du Label: " +
				exception.getMessage());
            throw new InnerException("Erreur lors de la modification du Label", exception.getMessage(), exception);
        }
    }
}