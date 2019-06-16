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
	
	private static final String[] REGISTERS2 = REGISTERS;
    
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
        return Arrays.asList (REGISTERS2);
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

    public static final String PUNTERO_MARCO = ".IY"; // Indica punto de entrada al RA
    public static final String VINCULO_CONTROL = ".IX"; // Recuperar RA
    public static final String STACK_POINTER = ".SP";
    public static final String ACUMULADOR = ".A";
    public static final String CONTADOR_PROGRAMA = ".PC";
    
    /* Registro RA: 
         1 - VALOR DE RETORNO - para funciones
         2 - DIRECCION DE RETORNO
         3 - VINCULO DE CONTROL - apunta al RA anterior en la pila
         4 - VINCULO DE ACCESO - apunta al RA de la funcion padre
         5 - DIRECCION DE RETORNO REFERENCIA - anpunta a los par�metro pasados por REF
    */
    public static final int numDireccionesRA = 5;
    public static final String RA_VINCULO_CONTROL   = "#-3[" + PUNTERO_MARCO + "]";
    public static final String RA_VINCULO_ACCESO    = "#-2[" + PUNTERO_MARCO + "]";
    public static final String RA_DIRECCION_RETORNO = "#-1[" + PUNTERO_MARCO + "]";
    public static final String RA_VALOR_RETORNO     = "#0["  + PUNTERO_MARCO + "]";

    /**
     * Translate a quadruple into a set of final instruction according to 
     * execution environment. 
     * @param cuadruple The quadruple to be translated.
     * @return A String containing the set (lines) of specific environment instructions. 
     */
    public String translate (QuadrupleIF quadruple) {      
        switch(quadruple.getOperation()) {
            case "BEGIN": return traducir_INICIO_PROGRAMA(quadruple); 
            case "HALT": return traducir_FIN_PROGRAMA(quadruple); 
            case "STRING": return traducir_CADENA(quadruple); 
            case "DIV": return traducir_DIV(quadruple); 
            case "ADD": return traducir_ADD(quadruple); 
            case "CMP": return traducir_CMP(quadruple); 
            case "ACCESO_REGISTRO": return traducir_ACCESO_REGISTRO(quadruple); 
            case "ASIGNACION": return traducir_ASIG(quadruple); 
            case "ASIG_REGISTRO": return traducir_ASIG_REGISTRO(quadruple); 
            case "LABEL": return traducir_ETIQUETA(quadruple); 
            case "BNZ": return traducir_BNZ(quadruple); 
            case "BN": return traducir_BN(quadruple); 
            case "BR": return traducir_BR(quadruple); 
            case "INC": return traducir_INC(quadruple); 
            case "CALL": return traducir_CALL(quadruple); 
            case "RETORNO": return traducir_RETORNO(quadruple); 
            case "INICIO_SUBPROG": return traducir_INICIO_SUBPROG(quadruple); 
            case "FIN_SUBPROG": return traducir_FIN_SUBPROG(quadruple); 
            case "WRSTR": return traducir_WRSTR(quadruple); 
            case "WRTLN": return traducir_WRTLN(quadruple); 
            case "WRINT": return traducir_WRINT(quadruple); 
            case "NOP": return "NOP"; 
            case "NOT": return traducir_NOT(quadruple); 
        }

        return "";
    }

    private String traducir_NOT(QuadrupleIF quadruple) {
        OperandIF oper1= quadruple.getFirstOperand();
        String operador1="";

         if (oper1 instanceof Value) {
            Value cte = (Value) oper1;
            operador1 = "#" + cte.getValue();
        } else {
            if (oper1 instanceof Variable) {
                Variable var = (Variable) oper1;
                SymbolVariable SimVar = (SymbolVariable) var.getAmbito().getSymbolTable().getSymbol(var.getName());
                if ( SimVar.getScope().getName().equals(var.getScope().getName()) ){ 
                   operador1 = "#-" + SimVar.getDesplazamiento() + "[.IY]";
                }
            } else {
                Temporal temp = (Temporal) oper1;                
                int desp = temp.getDesplazamiento();
                operador1 = "#-" + desp + "[.IY]";
            }
        }

        return "NOT " + operador1;
    }

    private String traducir_INICIO_PROGRAMA (QuadrupleIF quadruple) {
        String trad =   "; INICIO\n" + 
                        "MOVE .SP, .IX\n" +
                        "PUSH .IX\n" + 
                        "PUSH .SR\n" + 
                        ";SUB .IX\n" +
                        "MOVE .A, .SP\n";

        return trad;
    }

    private String traducir_FIN_PROGRAMA(QuadrupleIF quadruple){ //BIEN
        String trad="HALT \n";
        trad = trad + "\n; Inicio Cadenas de Texto\n";
        return trad;
    }
    
    private String traducir_CADENA(QuadrupleIF quadruple){
        String trad = "";
        String etiqueta = quadruple.getResult().toString(); 
        String operador = quadruple.getFirstOperand().toString();
        trad = etiqueta + ":  DATA "  + operador;
        return trad;
    }

    private String traducir_DIV(QuadrupleIF quadruple){
        String trad=""; 
        OperandIF oper1= quadruple.getFirstOperand();
        OperandIF oper2= quadruple.getSecondOperand();
        OperandIF rdo= quadruple.getResult();
        String operador1="";
        String operador2="";
        String resultado="";
        
        // Primer Operador
        if (oper1 instanceof Value) {
            Value cte = (Value) oper1;
            operador1 = "#" + cte.getValue();
        } else {
            if (oper1 instanceof Variable) {
                Variable var = (Variable) oper1;
                SymbolVariable SimVar = (SymbolVariable) var.getAmbito().getSymbolTable().getSymbol(var.getName());
                if ( SimVar.getScope().getName().equals(var.getScope().getName()) ){ 
                   operador1 = "#-" + SimVar.getDesplazamiento() + "[.IY]";
                }
            } else {
                Temporal temp = (Temporal) oper1;
                int desp = temp.getDesplazamiento();
                operador1 = "#-" + desp + "[.IY]";
            }
        }
        // Segundo Operador
        if (oper2 instanceof Value) {
            Value cte = (Value) oper2;
            operador2 = "#" + cte.getValue();
        } else {
            if (oper2 instanceof Variable) {
                Variable var = (Variable) oper2;
                SymbolVariable SimVar = (SymbolVariable) var.getAmbito().getSymbolTable().getSymbol(var.getName());
                if ( SimVar.getScope().getName().equals(var.getScope().getName()) ){ 
                   operador2 = "#-" + SimVar.getDesplazamiento() + "[.IY]";
                }
            } else {
                Temporal temp = (Temporal) oper2;
                int desp = temp.getDesplazamiento();
                operador2 = "#-" + desp + "[.IY]";
            }
        }
        // Resultado division
        if (rdo instanceof Value) {
            Value cte = (Value) rdo;
            resultado = "#" + cte.getValue();
        } else {
            if (rdo instanceof Variable) {
                Variable var = (Variable) rdo;
                SymbolVariable SimVar = (SymbolVariable) var.getScope().getSymbolTable().getSymbol(var.getName());
                resultado = "#-" + SimVar.getDesplazamiento() + "[.IY]";
            } else {
                Temporal temp=(Temporal) rdo; 
                int desp=temp.getDesplazamiento();
                resultado="#-" + desp + "[.IY]";
            }
        }

        trad=trad+"DIV " + operador1 + ", " + operador2 + "\n";
        trad=trad+"MOVE " + ".A, " + resultado;
        return trad;
    }

    private String traducir_ADD(QuadrupleIF quadruple){
        String trad=""; 
        OperandIF oper1= quadruple.getFirstOperand();
        OperandIF oper2= quadruple.getSecondOperand();
        OperandIF rdo= quadruple.getResult();
        
        String operador1="";
        String operador2="";
        String resultado="";
        
        // Primer Operador
        if (oper1 instanceof Value) {
            Value cte = (Value) oper1;
            operador1 = "#" + cte.getValue();
        } else {
            if (oper1 instanceof Variable) {
                Variable var = (Variable) oper1;
                SymbolVariable SimVar = (SymbolVariable) var.getAmbito().getSymbolTable().getSymbol(var.getName());
                if ( SimVar.getScope().getName().equals(var.getScope().getName()) ){ 
                   operador1 = "#-" + SimVar.getDesplazamiento() + "[.IY]";
                }
            } else {
                Temporal temp = (Temporal) oper1;
                int desp = temp.getDesplazamiento();
                operador1 = "#-" + desp + "[.IY]";
            }
        }
        // Segundo Operador
        if (oper2 instanceof Value) {
            Value cte = (Value) oper2;
            operador2 = "#" + cte.getValue();
        } else {
            if (oper2 instanceof Variable) {
                Variable var = (Variable) oper2;
                
                SymbolVariable SimVar = (SymbolVariable) var.getAmbito().getSymbolTable().getSymbol(var.getName());
                if ( SimVar.getScope().getName().equals(var.getScope().getName()) ){ 
                   operador2 = "#-" + SimVar.getDesplazamiento() + "[.IY]";
                }
            } else {
                Temporal temp = (Temporal) oper2;
                int desp = temp.getDesplazamiento();
                operador2 = "#-" + desp + "[.IY]";
            }
        }
        
        // Resultado suma
        if (rdo instanceof Value) {
            Value cte = (Value) rdo;
            resultado = "#" + cte.getValue();
        } else {
            if (rdo instanceof Variable) {
                Variable var = (Variable) rdo;
                SymbolVariable SimVar = (SymbolVariable) var.getScope().getSymbolTable().getSymbol(var.getName());
                resultado = "#-" + SimVar.getDesplazamiento() + "[.IY]";
            } else{
                Temporal temp=(Temporal) rdo; 
                int desp=temp.getDesplazamiento();
                resultado="#-" + desp + "[.IY]";
            }
        }

        trad=trad+"ADD " + operador1 + ", " + operador2 + "\n";
        trad= trad + "MOVE " + ".A, " + resultado;
        return trad;
    }
    
    private String traducir_CMP(QuadrupleIF quadruple){
        String trad= "; Traducir "+quadruple.toString() +"\n";
        
        OperandIF oper1 = quadruple.getResult();
        OperandIF oper2 = quadruple.getFirstOperand();
        String operador1="";
        String operador2="";
        
        // Primer OPERANDO
        if (oper1 instanceof Value){
            Value cte=(Value) oper1;
            operador1="#" + cte.getValue();
         }else{
            if (oper1 instanceof Variable){
                Variable var=(Variable) oper1;
                SymbolVariable SimVar=(SymbolVariable) var.getAmbito().getSymbolTable().getSymbol(var.getName());
                if ( SimVar.getScope().getName().equals(var.getScope().getName()) ){ 
                   operador1 = "#-" + SimVar.getDesplazamiento() + "[.IY]";
                }
            }else{
                Temporal temp = (Temporal) oper1; 
                operador1="#-" + temp.getDesplazamiento() + "[.IY]";
            }
        }
        // Segundo OPERANDO
        if (oper2 instanceof Value){
            Value cte=(Value) oper2;
            operador2="#" + cte.getValue();
         }else{
            if (oper2 instanceof Variable){
                Variable var=(Variable) oper2;
                SymbolVariable SimVar=(SymbolVariable) var.getAmbito().getSymbolTable().getSymbol(var.getName());
                if ( SimVar.getScope().getName().equals(var.getScope().getName()) ){ 
                   operador2 = "#-" + SimVar.getDesplazamiento() + "[.IY]";
                }
            }else{
                Temporal temp = (Temporal) oper2; 
                operador2="#-" + temp.getDesplazamiento() + "[.IY]";
            }
        }
        trad = trad + "CMP " + operador1 + ", " + operador2 ;
        return trad;
    }
    
    private String traducir_ACCESO_REGISTRO(QuadrupleIF quadruple){
        String trad= "; Traducir "+quadruple.toString() +"\n";
        
        OperandIF rdo = quadruple.getResult();
        OperandIF oper1 = quadruple.getFirstOperand();
        OperandIF oper2 = quadruple.getSecondOperand();
        String resultado="";
        
        Variable var2=(Variable) oper2;
        Variable var=(Variable) oper1;
        SymbolVariable SimVar=(SymbolVariable) var.getAmbito().getSymbolTable().getSymbol(var.getName());
        Temporal temp = (Temporal) rdo; 
        resultado="#-" + temp.getDesplazamiento() + "[.IY]";

        if (SimVar.getScope().getName().equals(var.getScope().getName())) {
            trad= trad+"SUB "+PUNTERO_MARCO+" , #"+(SimVar.getDesplazamiento()+var2.getDesplCampo())+"\n";  
            trad= trad+"MOVE [.A] , "+resultado;
        }

        return trad;
    }
    
    private String traducir_ASIG(QuadrupleIF quadruple){
        String trad= "; Traducir "+quadruple.toString() +"\n";
        OperandIF oper1= quadruple.getFirstOperand();
        OperandIF rdo= quadruple.getResult();
        
        String operador1="";
        String resultado="";
        
        // Primer Operador
        if (oper1 instanceof Value) {
            Value cte = (Value) oper1;
            operador1 = "#" + cte.getValue();
        } else {
            if (oper1 instanceof Variable) {    
                Variable var = (Variable) oper1;
                SymbolVariable SimVar = (SymbolVariable) var.getAmbito().getSymbolTable().getSymbol(var.getName());
                if ( SimVar.getScope().getName().equals(var.getScope().getName()) ){ 
                   operador1 = "#-" + SimVar.getDesplazamiento() + "[.IY]";
                }
            } else {
                Temporal temp = (Temporal) oper1;
                int desp = temp.getDesplazamiento();
                operador1 = "#-" + desp + "[.IY]";
            }
        }
        
        // Segundo operando
        if (rdo instanceof Value) {
            Value cte = (Value) rdo;
            resultado = "#" + cte.getValue();
        } else {
            if (rdo instanceof Variable) {
                Variable var = (Variable) rdo;
                
                SymbolVariable SimVar = (SymbolVariable) var.getAmbito().getSymbolTable().getSymbol(var.getName());
                if ( SimVar.getScope().getName().equals(var.getScope().getName()) ){ 
                   resultado = "#-" + SimVar.getDesplazamiento() + "[.IY]";
                }
            } else {
                Temporal temp=(Temporal) rdo; 
                int desp=temp.getDesplazamiento();
                resultado="#-" + desp + "[.IY]";
            }
        }

        trad= trad + "MOVE " + operador1 + ", " + resultado;
        
        return trad;
    }
  
    private String traducir_ASIG_REGISTRO(QuadrupleIF quadruple){
        String trad="; Traducir "+quadruple.toString() +"\n";
        
        OperandIF rdo= quadruple.getResult();
        OperandIF oper1= quadruple.getFirstOperand();
        OperandIF oper2= quadruple.getSecondOperand();
        
        String operador2="";
        
        Variable var2=(Variable) oper1;
        
        Variable var1=(Variable) rdo;
        SymbolVariable SimVar1=(SymbolVariable) var1.getAmbito().getSymbolTable().getSymbol(var1.getName());
                
        // Segundo Operador o expresion
        if (oper2 instanceof Value) {
            Value cte = (Value) oper2;
            operador2 = "#" + cte.getValue();
        } else {
            if (oper2 instanceof Variable) {
                Variable var = (Variable) oper2;
                SymbolVariable SimVar = (SymbolVariable) var.getAmbito().getSymbolTable().getSymbol(var.getName());
                // El ambito donde encuentro la expresi�n recuperado del getResult()
                if ( SimVar.getScope().getName().equals(SimVar1.getScope().getName()) ){ 
                   operador2 = "#-" + SimVar.getDesplazamiento() + "[.IY]";
                }
            } else {
                Temporal temp = (Temporal) oper2;
                int desp = temp.getDesplazamiento();
                operador2 = "#-" + desp + "[.IY]";
            }
        }
        
        // Se trata despu�s la asignaci�n por temas del acumulador
        if (SimVar1.getScope().getName().equals(var1.getScope().getName())) {
            trad = trad+"SUB "+PUNTERO_MARCO+" , #"+(SimVar1.getDesplazamiento()+var2.getDesplCampo())+"\n";  
        } /* else {
            // Variable en otro ambito 
            trad = trad + "MOVE /" + SimVar1.getScope().getLevel() + " , .R1 \n";
            trad= trad+"SUB .R1 , #"+(SimVar1.getDesplazamiento()+var2.getDesplCampo())+"\n";  
        } */
        trad= trad+"MOVE "+operador2+" , [.A]";
        return trad;
    }
    
    private String traducir_ETIQUETA(QuadrupleIF quadruple){
        OperandIF rdo = quadruple.getResult();
        String trad = cambiarEtiqueta(rdo.toString()) + " :";
        return trad;
    }

    private String traducir_BNZ(QuadrupleIF quadruple){
        OperandIF rdo= quadruple.getResult();

        return "BNZ /" + cambiarEtiqueta(rdo.toString());
    }

    private String traducir_BN(QuadrupleIF quadruple){
        OperandIF rdo= quadruple.getResult();

        return "BN /" + cambiarEtiqueta(rdo.toString());
    }

    private String traducir_BR(QuadrupleIF quadruple){
        OperandIF rdo= quadruple.getResult();

        return "BR /" + cambiarEtiqueta(rdo.toString());
    }

    private String traducir_INC(QuadrupleIF quadruple){
        String trad=""; 
        Variable var=(Variable) quadruple.getResult();
        SymbolVariable SimVar = (SymbolVariable) var.getAmbito().getSymbolTable().getSymbol(var.getName());
        
        if (SimVar.getScope().getName().equals(var.getScope().getName())) {
            trad="INC #-" +SimVar.getDesplazamiento()+"[.IY]";
        }  
           
        return trad;
    }

    private String traducir_CALL(QuadrupleIF quadruple){
        return "";    
    }

     private String traducir_RETORNO(QuadrupleIF quadruple){
        String trad= "; Traducir "+quadruple.toString() +"\n";
        String operador1= "";
        OperandIF rdo = quadruple.getResult();
        
        OperandIF oper1 = quadruple.getFirstOperand();
        Variable resultado = (Variable) rdo;
        String etiqRetorno = cambiarEtiqueta(resultado.getEtiqRetorno().getName());
            
        if (oper1 instanceof Value){
            Value cte = (Value) oper1;
            operador1 = "#" + cte.getValue();
        }else{
            if (oper1 instanceof Variable) {
                Variable var = (Variable) oper1;
                SymbolVariable SimVar=(SymbolVariable) var.getAmbito().getSymbolTable().getSymbol(var.getName());
                if ( SimVar.getScope().getName().equals(var.getScope().getName()) ){ 
                   operador1 = "#-"+SimVar.getDesplazamiento()+"[.IY]";
                }
           }else{
                Temporal temp = (Temporal) oper1;
                operador1= "#-"+temp.getDesplazamiento()+"[.IY]";
           }
        }
        trad = "MOVE "+operador1 +" , "+RA_VALOR_RETORNO +"\n";
        trad = trad +"MOVE #"+ etiqRetorno +" , "+CONTADOR_PROGRAMA+"\n";      
        return trad;      
     }
 
    private String traducir_INICIO_SUBPROG(QuadrupleIF quadruple){
        String trad=";-- Definicion FUNCION/PROCEDIMIENTO \n";
        OperandIF rdo = (OperandIF) quadruple.getResult();        
        
        String etiq = cambiarEtiqueta(rdo.toString());
        trad=trad+"BR /FIN_"+etiq+"\n";
        trad=trad +etiq + " :\n";
        return trad;      
    }

    private String traducir_FIN_SUBPROG(QuadrupleIF quadruple){
        return "";
    }

    private String traducir_WRINT (QuadrupleIF quadruple) {
        String resultado = "";
        String trad= "";
        OperandIF rdo=quadruple.getResult();
        
        if(rdo  instanceof Value){
            Value cte=(Value) rdo;
            resultado = "#" + cte.getValue();
        }else{
            if(rdo instanceof Variable){
                Variable var=(Variable) rdo;
                SymbolVariable SimVar=(SymbolVariable) var.getAmbito().getSymbolTable().getSymbol(var.getName());
                resultado = "#-" + SimVar.getDesplazamiento() + "[.IY]";
                if ( SimVar.getScope().getName().equals(var.getScope().getName()) ){ 
                 resultado = "#-" + SimVar.getDesplazamiento() + "[.IY]";
               }
            }else{
                Temporal temp =(Temporal) rdo; 
                int desp=temp.getDesplazamiento();
                 resultado = "#-" + desp + "[.IY]";
            }
       }
       trad= trad+"WRINT "+resultado;
       return trad;
    }
    
     private String traducir_WRSTR(QuadrupleIF quadruple) {
        String trad = "";
        String operador = quadruple.getResult().toString();
        trad= "WRSTR /" + operador + "; escribir cadena";
        return trad;
    }

    private String traducir_WRTLN(QuadrupleIF quadruple) {
        String trad = "";
        trad = "; Escribimos un salto de linea\n";
        trad = trad + "WRSTR /cadena0";
        return trad;
    }
  
    private String cambiarEtiqueta(String etiq){
       return etiq.replace("_", "");
   }
}
