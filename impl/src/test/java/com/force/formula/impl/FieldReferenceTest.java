/*
 * Copyright, 1999-2018, salesforce.com
 * All Rights Reserved
 * Company Confidential
 */
package com.force.formula.impl;

import java.util.*;

import com.force.formula.*;
import com.google.common.collect.ImmutableSet;

/**
 * Perform a test of Field References
 * @author stamm
 * @since 0.2
 */
public class FieldReferenceTest extends BaseFieldReferenceTest {
    public FieldReferenceTest(String name) {
        super(name);
    }
    
    /**
     * This works like "Contact", where you can access Contact fields 
     * @throws Exception
     */
    public void testDisplayFields() throws Exception {            
        BeanFormulaContext context = setupMockContext(null);
        DisplayField[] fields = context.getDisplayFields(context.getEntity());
        Map<String,FormulaFieldInfo> ffis = new HashMap<>();
        for (DisplayField field : fields) {
            assertEquals("BaseFieldReferenceTest$TestContact", field.getCategoryLabel());
            ffis.put(field.getFormulaFieldInfo().getName(), field.getFormulaFieldInfo());
        }
        assertEquals(ImmutableSet.of("account", "accountId", "createdDate", "optIn", "nullNumber", "nullAccount", "nullDate", "nullText",
                "dateFormula", "numberFormula"), ffis.keySet());
        assertTrue(ffis.get("createdDate").getDataType().isDateOnly());            
        assertTrue(ffis.get("optIn").getDataType().isBoolean());            
    }
 
    public void testEncoding() throws Exception {
        assertEquals("ENCODED:{!ID:$System.OriginDateTime}", encode("$System.OriginDateTime", MockFormulaDataType.DATEONLY));
        assertEquals("ENCODED:NOT({!ID:optIn})", encode("NOT(OptIn)", MockFormulaDataType.BOOLEAN));
        assertEquals("ENCODED:{!ID:Account.createdDate}", encode("Account.CreatedDate", MockFormulaDataType.DATEONLY));
        
        validateDecode("$System.OriginDateTime", MockFormulaDataType.DATEONLY);
        validateDecode("NOT(OptIn)", MockFormulaDataType.BOOLEAN);
        // TODO doesn't work yet...
        //validateDecode("Account.CreatedDate", MockFormulaDataType.DATEONLY);
    }
    
    public String encode(String formulaSource, FormulaDataType columnType) throws FormulaException {
        FormulaRuntimeContext context = setupMockContext(columnType);
        RuntimeFormulaInfo formulaInfo = FormulaEngine.getFactory().create(getFormulaType(), context, formulaSource);
        return formulaInfo.encode();
    }

    public void validateDecode(String formulaSource, FormulaDataType columnType) throws FormulaException {
        FormulaRuntimeContext context = setupMockContext(columnType);
        RuntimeFormulaInfo formulaInfo = FormulaEngine.getFactory().create(getFormulaType(), context, formulaSource);
        String decoded = FormulaUtils.decode(context, formulaInfo.encode(), getFormulaType().getDefaultProperties());
        assertTrue(decoded + "!=" + formulaSource, decoded.equalsIgnoreCase(formulaSource));
    }
    

}