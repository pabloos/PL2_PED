package compiler.code;

import java.util.*;

import compiler.semantic.symbol.*;
import compiler.intermediate.*;
import compiler.semantic.type.TypeSimple;

import es.uned.lsi.compiler.code.ExecutionEnvironmentIF;
import es.uned.lsi.compiler.code.MemoryDescriptorIF;
import es.uned.lsi.compiler.code.RegisterDescriptorIF;

import es.uned.lsi.compiler.intermediate.*;

public class ExecutionEnvironmentEns2001 
    implements ExecutionEnvironmentIF
{    
    private final static int      MAX_ADDRESS = 65535; 
    private final static String[] REGISTERS   = {
       ".PC", ".SP", ".SR", ".IX", ".IY", ".A", 
       ".R0", ".R1", ".R2", ".R3", ".R4", 
       ".R5", ".R6", ".R7", ".R8", ".R9"
    };
        
    private String DIR_IND_MEM = "[" + REGISTERS[4] + "]";
    private String DIR_IND_AC  = "[" + REGISTERS[5] + "]";

    private String VAL_DIR = "#";
    private String VAL_RES_REL = VAL_DIR + "-"; 

    private String NEW_LINE_LABEL = "/cadena0";
    private String SEPARATOR = " ,";

    private RegisterDescriptorIF registerDescriptor;
    private MemoryDescriptorIF   memoryDescriptor;

    /**
     * Constructor for ENS2001Environment.
     */
    public ExecutionEnvironmentEns2001() {       
        super ();
    }
    
    /**
     * Returns the size of the type within the architecture.
     * @return the size of the type within the architecture.
     */
    @Override
    public final int getTypeSize (TypeSimple type) {      
        return 1;  
    }
    
    /**
     * Returns the registers.
     * @return the registers.
     */
    @Override
    public final List<String> getRegisters () {
        return Arrays.asList (REGISTERS);
    }
    
    /**
     * Returns the memory size.
     * @return the memory size.
     */
    @Override
    public final int getMemorySize () {
        return MAX_ADDRESS;
    }
           
    /**
     * Returns the registerDescriptor.
     * @return Returns the registerDescriptor.
     */
    @Override
    public final RegisterDescriptorIF getRegisterDescriptor () {
        return registerDescriptor;
    }

    /**
     * Returns the memoryDescriptor.
     * @return Returns the memoryDescriptor.
     */
    @Override
    public final MemoryDescriptorIF getMemoryDescriptor () {
        return memoryDescriptor;
    }

    /**
     * Translate a quadruple into a set of final instruction according to 
     * execution environment. 
     * @param cuadruple The quadruple to be translated.
     * @return A String containing the set (lines) of specific environment instructions. 
     */
    public String translate (QuadrupleIF quadruple) {      
        switch(quadruple.getOperation()) {
            case "DIV":             return "DIV " + getTrans(quadruple.getFirstOperand()) + SEPARATOR + getTrans(quadruple.getSecondOperand()) + "\n" + "MOVE " + REGISTERS[5] + SEPARATOR + getTrans(quadruple.getResult()); 
            case "ADD":             return "ADD " + getTrans(quadruple.getFirstOperand()) + SEPARATOR + getTrans(quadruple.getSecondOperand()) + "\n" + "MOVE " + REGISTERS[5] + SEPARATOR + getTrans(quadruple.getResult());
            case "CMP":             return "CMP " + getTrans(quadruple.getResult()) + SEPARATOR + getTrans(quadruple.getFirstOperand()); 
            case "ASIGNACION":      return "MOVE "+ getTrans(quadruple.getFirstOperand()) + SEPARATOR + getTrans(quadruple.getResult()); 
            case "ACCESO_REGISTRO": return ACC_REG(quadruple); 
            case "ASIG_REGISTRO":   return ASIG_REG(quadruple); 
            case "STRING":          return quadruple.getResult().toString() + ":  DATA " + quadruple.getFirstOperand().toString();
            case "LABEL":           return quadruple.getResult().toString() + " :";
            case "BNZ":             return "BNZ /" + quadruple.getResult().toString(); 
            case "BN":              return "BN /" + quadruple.getResult().toString(); 
            case "BR":              return "BR /" + quadruple.getResult().toString();
            case "WRSTR":           return "WRSTR /" + quadruple.getResult().toString();
            case "WRINT":           return "WRINT " + getTrans(quadruple.getResult());
            case "WRTLN":           return "WRSTR " + NEW_LINE_LABEL; 
            case "NOP":             return "NOP"; 
            case "HALT":            return "HALT \n"; 
        }
        return "";
    }
    
    private String getValueTran(OperandIF operand) {
        Value constante = (Value) operand;
        return VAL_DIR + constante.getValue();
    }

    private String getVariableTran(OperandIF operand) {
        Variable var = (Variable) operand;
        SymbolVariable symbolVariable = (SymbolVariable) var.getAmbito().getSymbolTable().getSymbol(var.getName());
        return VAL_RES_REL + symbolVariable.getDesplazamiento() + DIR_IND_MEM;
    }

    private String getTemporalTran(OperandIF operand) {
        Temporal temporal = (Temporal) operand;
        return VAL_RES_REL + temporal.getDesplazamiento() + DIR_IND_MEM;
    }

    private String getTrans(OperandIF operand) {
        if (operand instanceof Value) {
            return getValueTran(operand);
        } else if (operand instanceof Variable) {
            return getVariableTran(operand);
        } else {
            return getTemporalTran(operand);
        }
    }
    
    private String ACC_REG(QuadrupleIF quadruple){
        Variable campo = (Variable) quadruple.getSecondOperand();
        Variable registro = (Variable) quadruple.getFirstOperand();

        SymbolVariable symbolVariable = (SymbolVariable) registro.getAmbito().getSymbolTable().getSymbol(registro.getName());
        Temporal temporal = (Temporal) quadruple.getResult();

        return  "SUB " + REGISTERS[4] + SEPARATOR + VAL_DIR + (symbolVariable.getDesplazamiento() + campo.getDesplCampo()) + "\n" + //accedemos a la posicion correspondiente degun el campo
                "MOVE " + DIR_IND_AC + SEPARATOR + VAL_RES_REL + temporal.getDesplazamiento() + DIR_IND_MEM;
    }
  
    private String ASIG_REG(QuadrupleIF quadruple){        
        String operador2 = getTrans(quadruple.getSecondOperand());
        
        Variable var2 = (Variable) quadruple.getFirstOperand();
        Variable var1 = (Variable) quadruple.getResult();

        SymbolVariable SimVar1 = (SymbolVariable) var1.getAmbito().getSymbolTable().getSymbol(var1.getName());

        return  "SUB " + REGISTERS[4] + SEPARATOR + VAL_DIR + (SimVar1.getDesplazamiento()+var2.getDesplCampo()) + "\n" + 
                "MOVE " + operador2 + SEPARATOR + DIR_IND_AC;
    }
}
