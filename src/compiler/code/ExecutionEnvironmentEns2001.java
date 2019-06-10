package compiler.code;

import java.util.Arrays;
import java.util.List;

import java_cup.runtime.Symbol;
import java.util.*;

import compiler.semantic.type.TypeSimple;

import es.uned.lsi.compiler.code.ExecutionEnvironmentIF;
import es.uned.lsi.compiler.code.MemoryDescriptorIF;
import es.uned.lsi.compiler.code.RegisterDescriptorIF;
import es.uned.lsi.compiler.lexical.*;
import es.uned.lsi.compiler.code.*;
import es.uned.lsi.compiler.intermediate.*;
import es.uned.lsi.compiler.semantic.*;
import es.uned.lsi.compiler.semantic.symbol.*;
import es.uned.lsi.compiler.semantic.type.*;
import es.uned.lsi.compiler.syntax.*;

import compiler.CompilerContext;
import compiler.lexical.*;
import compiler.syntax.nonTerminal.*;
import compiler.semantic.*;
import compiler.semantic.symbol.*;
import compiler.semantic.type.*;
import compiler.intermediate.*;
import compiler.code.*;

import compiler.semantic.utils.*;

/**
 * Class for the ENS2001 Execution environment.
 */

public class ExecutionEnvironmentEns2001 
    implements ExecutionEnvironmentIF
{    
    private final static int      MAX_ADDRESS = 65535; 
    private final static String[] REGISTERS   = {
       ".PC", ".SP", ".SR", ".IX", ".IY", ".A", 
       ".R0", ".R1", ".R2", ".R3", ".R4", 
       ".R5", ".R6", ".R7", ".R8", ".R9"
    };
    
    private RegisterDescriptorIF registerDescriptor;
    private MemoryDescriptorIF   memoryDescriptor;
    
    /**
     * Constructor for ENS2001Environment.
     */
    public ExecutionEnvironmentEns2001 ()
    {       
        super ();
    }
    
    /**
     * Returns the size of the type within the architecture.
     * @return the size of the type within the architecture.
     */
    @Override
    public final int getTypeSize (TypeSimple type)
    {      
        return 1;  
    }
    
    /**
     * Returns the registers.
     * @return the registers.
     */
    @Override
    public final List<String> getRegisters ()
    {
        return Arrays.asList (REGISTERS);
    }
    
    /**
     * Returns the memory size.
     * @return the memory size.
     */
    @Override
    public final int getMemorySize ()
    {
        return MAX_ADDRESS;
    }
           
    /**
     * Returns the registerDescriptor.
     * @return Returns the registerDescriptor.
     */
    @Override
    public final RegisterDescriptorIF getRegisterDescriptor ()
    {
        return registerDescriptor;
    }

    /**
     * Returns the memoryDescriptor.
     * @return Returns the memoryDescriptor.
     */
    @Override
    public final MemoryDescriptorIF getMemoryDescriptor ()
    {
        return memoryDescriptor;
    }

    /**
     * Translate a quadruple into a set of final code instructions. 
     * @param cuadruple The quadruple to be translated.
     * @return a quadruple into a set of final code instructions. 
     */
    // @Override
    // public final String translate (QuadrupleIF quadruple)
    // {      
    //     //TODO: Student work
    //     return quadruple.toString(); 
    // }

    /*************************
     * lista es un Array de arrays de argumentos de llamada. Se inicializa en el INICIO_ARGUMENTOS y 
     * contiene las intruscciones ensanblador para la carga de Argumentos.
     * Necesario si los par�metros son llamadas a funciones / procedimientos
     */
    ArrayList[] lista = new ArrayList[30];
    ArrayList listaPorReferencia = new ArrayList();
  

    /****************
     * Indice de la lista en laa que est�n cargada los argumentos del array lista. 
     * Toma el valor -1 se incrementa antes de ser usada, tomando valor 0.
     */
    int contadorLlamadas = -1;
    // HashMap parPorReferencia = new HashMap();
            
    List cadenas = new ArrayList();
    HashMap tablaScopes = new HashMap();
    
    List argumentos;
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
     * Constructor for ENS2001Environment.
     */
    // public ENS2001Environment ()
    // {
     
    // }
       
    /**
     * Translate a quadruple into a set of final instruction according to 
     * execution environment. 
     * @param cuadruple The quadruple to be translated.
     * @return A String containing the set (lines) of specific environment instructions. 
     */
    public String translate (QuadrupleIF quadruple)
    {      
        //TODO: Student work
        String oper = quadruple.getOperation();
        String rdo =null;
        if (oper.equals("BEGIN")) { rdo=traducir_INICIO_PROGRAMA(quadruple);
        }else if (oper.equals("HALT")){ rdo=traducir_FIN_PROGRAMA(quadruple);
        }else if (oper.equals("STRING")){ rdo=traducir_CADENA(quadruple);
        }else if (oper.equals("DIV")){ rdo=traducir_SUB(quadruple);
        }else if (oper.equals("ADD")){ rdo=traducir_ADD(quadruple);
        }else if (oper.equals("CMP")){ rdo=traducir_CMP(quadruple);
        }else if (oper.equals("ACCESO_REGISTRO")){ rdo=traducir_ACCESO_REGISTRO(quadruple);
        }else if (oper.equals("ASIGNACION")){ rdo=traducir_ASIG(quadruple);
        }else if (oper.equals("ASIG_REGISTRO")){ rdo=traducir_ASIG_REGISTRO(quadruple);
        }else if (oper.equals("LABEL")){ rdo=traducir_ETIQUETA(quadruple);
        }else if (oper.equals("BZ")){ rdo=traducir_BZ(quadruple);
        }else if (oper.equals("BNZ")){ rdo=traducir_BNZ(quadruple);
        }else if (oper.equals("BN")){ rdo=traducir_BN(quadruple);        
        }else if (oper.equals("BR")){ rdo=traducir_BR(quadruple);
        }else if (oper.equals("BP")){ rdo=traducir_BP(quadruple);
        }else if (oper.equals("INC")){ rdo=traducir_INC(quadruple);
        }else if (oper.equals("CALL")){ rdo=traducir_CALL(quadruple);
        }else if (oper.equals("ARGUMENTO")){ rdo=traducir_ARGUMENTO(quadruple);
        }else if (oper.equals("ARGUMENTO_REF")){ rdo=traducir_ARGUMENTO_REF(quadruple);
        }else if (oper.equals("INICIO_ARGUMENTOS")){ rdo=traducir_INICIO_ARGUMENTOS(quadruple);
        }else if (oper.equals("RETORNO")){ rdo=traducir_RETORNO(quadruple);
        }else if (oper.equals("INICIO_SUBPROG")){ rdo=traducir_INICIO_SUBPROG(quadruple);
        }else if (oper.equals("FIN_SUBPROG")){ rdo=traducir_FIN_SUBPROG(quadruple);        
        }else if (oper.equals("WRSTR")){ rdo=traducir_WRSTR(quadruple);
        }else if (oper.equals("WRTLN")){ rdo=traducir_WRTLN(quadruple);
        }else if (oper.equals("WRINT")){ rdo=traducir_WRINT(quadruple);
        }else if (oper.equals("NOP")){ rdo="NOP";
        } else if (oper.equals("NOT")) { rdo=traducir_NOT(quadruple); 
        }
        return rdo;
         
    }

    //NOS QUEDAMOS AQUI - HAY QUE TRADUCIR EL BRF DE LA VENTANA DE AL LADO, DA REFLEXTION ERROR
    // private String traducir_BRF(QuadrupleIF quadruple) {
    //    String trad=";Salto si es falso \r\n";

	// 	String op1 = ((LabelIF) quadruple.getFirstOperand()).toString();
	// 	// String res = quadruple.getSecondOperand().toString();
	// 	// trad = trad + "CMP "+res+", #0 \r\n";
    //     // trad = trad + "BZ /"+op1+"\r\n";

    //     return trad;

    // }

    private String traducir_NOT(QuadrupleIF quadruple) {
        //OperandIF operador1;// = quadruple.getResult();

        System.out.println("QUADRUPLA " + quadruple);

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
                } /* else {
                   // Variable en otro ambito 
                   trad=trad+"MOVE /"+SimVar.getScope().getLevel()+" , .R1 \n";
                   trad=trad+"DIV .R1 , #"+SimVar.getDesplazamiento()+"\n";
                   trad=trad+"MOVE [.A] , .R2 \n";
                   operador1 = ".R2";
                } */
            } else {
                System.out.println("oper "+ oper1);

                Temporal temp = (Temporal) oper1;
                System.out.println("TEMPORAL "+temp);
                
                int desp = temp.getDesplazamiento();
                operador1 = "#-" + desp + "[.IY]";
            }
        }

        return "NOT " + operador1;
    }

    private String traducir_INICIO_PROGRAMA (QuadrupleIF quadruple) {
        // Inicializar la lista de llamadas
        for (int k=0; k<30;k++){
            lista[k]=new ArrayList();
        }
        // Variable var = (Variable) quadruple.getResult();
        // Value valor = (Value) quadruple.getFirstOperand();
        // this.tablaScopes = var.getTablaDespl();
        // int tRA=0;
        
        // Integer i = (Integer)this.tablaScopes.get(var.getName());
        // tRA = (Integer) valor.getValue();
        // String trad="; INICIO PROGRAMA PRINCIPAL \n"+
        //                  "RES "+tablaScopes.size()+1+"\n"+   
        //                  "MOVE #65535, "+ STACK_POINTER+"\n"+
        //                  "MOVE "+ STACK_POINTER+" , "+ PUNTERO_MARCO+"\n"+
        //                  "SUB  "+ STACK_POINTER+" , #"+tRA+"\n"+
        //                  "MOVE "+ ACUMULADOR + " , "+ STACK_POINTER+"\n"+
        //                  "MOVE "+ PUNTERO_MARCO+" , /0";

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
        // la etiqueta
        String trad = "";
        String etiqueta = quadruple.getResult().toString(); 
        String operador = quadruple.getFirstOperand().toString();
        trad = etiqueta + ":  DATA "  + operador;
        return trad;
    }
    private String traducir_SUB(QuadrupleIF quadruple){
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
                }else {
                   // Variable en otro ambito 
                   trad=trad+"MOVE /"+SimVar.getScope().getLevel()+" , .R1 \n";
                   trad=trad+"DIV .R1 , #"+SimVar.getDesplazamiento()+"\n";
                   trad=trad+"MOVE [.A] , .R2 \n";
                   operador1 = ".R2";
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
                }else {
                    
                   // Variable en otro ambito 
                   trad=trad+"MOVE /"+SimVar.getScope().getLevel()+" , .R3 \n";
                   trad=trad+"DIV .R3 , #"+SimVar.getDesplazamiento()+"\n";
                   trad=trad+"MOVE [.A] , .R4 \n";
                   operador2 = ".R4";
                }
            } else {
                Temporal temp = (Temporal) oper2;
                int desp = temp.getDesplazamiento();
                operador2 = "#-" + desp + "[.IY]";
            }
        }
        // Resultado RESTA
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
        
        System.out.println(oper1);
        System.out.println(oper2);


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
                }else {
                   // Variable en otro ambito 
                   trad=trad+"MOVE /"+SimVar.getScope().getLevel()+" , .R1 \n";
                   trad=trad+"SUB .R1 , #"+SimVar.getDesplazamiento()+"\n";
                   trad=trad+"MOVE [.A] , .R2 \n";
                   operador1 = ".R2";
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
                }else {
                    
                   // Variable en otro ambito 
                   trad=trad+"MOVE /"+SimVar.getScope().getLevel()+" , .R3 \n";
                   trad=trad+"SUB .R3 , #"+SimVar.getDesplazamiento()+"\n";
                   trad=trad+"MOVE [.A] , .R4 \n";
                   operador2 = ".R4";
                }
            } else {
                Temporal temp = (Temporal) oper2;
                int desp = temp.getDesplazamiento();
                operador2 = "#-" + desp + "[.IY]";
            }
        }
        
        // Resultado SUMA
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
                }else {
                    
                   // Variable en otro ambito 
                   trad=trad+"MOVE /"+SimVar.getScope().getLevel()+" , .R1 \n";
                   trad=trad+"SUB .R1 , #"+SimVar.getDesplazamiento()+"\n";
                   trad=trad+"MOVE [.A] , .R2 \n";
                   operador1 = ".R2";
                }
                // operador1="#-" + SimVar.getDesplazamiento() + "[.IY]";
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
                }else {
                    
                   // Variable en otro ambito 
                   trad=trad+"MOVE /"+SimVar.getScope().getLevel()+" , .R3 \n";
                   trad=trad+"SUB .R3 , #"+SimVar.getDesplazamiento()+"\n";
                   trad=trad+"MOVE [.A] , .R4 \n";
                   operador2 = ".R4";
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
        } else {

            // Variable en otro ambito 
            trad = trad + "MOVE /" + SimVar.getScope().getLevel() + " , .R1 \n";
            trad= trad+"SUB .R1 , #"+(SimVar.getDesplazamiento()+var2.getDesplCampo())+"\n";  
            trad= trad+"MOVE [.A] , "+resultado;
        }

        return trad;
    }
    private String traducir_ACCESO_REGISTRO_PUNTERO(QuadrupleIF quadruple){
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
            trad= trad+"MOVE [.A] , .R1 \n";
            trad= trad+"SUB .R1 , #0 \n";
            trad= trad+"MOVE [.A] , "+resultado;
        } else {

            // Variable en otro ambito 
            trad = trad + "MOVE /" + SimVar.getScope().getLevel() + " , .R1 \n";
            trad= trad+"SUB .R1 , #"+(SimVar.getDesplazamiento()+var2.getDesplCampo())+"\n";  
            trad= trad+"MOVE [.A] , .R2 \n";
            trad= trad+"SUB .R2 , #0 \n";
            trad= trad+"MOVE [.A] , "+resultado;
            
        }

        return trad;
    }
    
    private String traducir_ACCESO_PUNTERO(QuadrupleIF quadruple){
        String trad=""; 
        OperandIF rdo= quadruple.getResult();
        OperandIF oper1= quadruple.getFirstOperand();
        
        Variable var = (Variable) oper1;
        SymbolVariable SimVar = (SymbolVariable) var.getAmbito().getSymbolTable().getSymbol(var.getName());
        Temporal temp = (Temporal) rdo;
        
        if (SimVar.getScope().getName().equals(var.getScope().getName())) {
            trad=trad+"SUB #-"+ SimVar.getDesplazamiento()+ "[.IY] , #0 \n";
            trad=trad+"MOVE [.A] , #-"+ temp.getDesplazamiento()+ "[.IY]";
        } else {
            // Variable en otro ambito 
            trad = trad + "MOVE /" + SimVar.getScope().getLevel() + " , .R1 \n";
            trad = trad + "SUB .R1 , #" + SimVar.getDesplazamiento() + "\n";
            trad=trad+"MOVE [.A] , #-"+ temp.getDesplazamiento()+ "[.IY]";
            trad=trad+"SUB #-"+ temp.getDesplazamiento()+ "[.IY] , #0 \n";
            trad=trad+"MOVE [.A] , #-"+ temp.getDesplazamiento()+ "[.IY]";
        }
        return trad;
    }
    private String traducir_DIR_MEM(QuadrupleIF quadruple){
        String trad= "; Traducir "+quadruple.toString() +"\n";
        OperandIF rdo= quadruple.getResult();
        OperandIF oper1= quadruple.getFirstOperand();
        
        Variable var = (Variable) oper1;
        SymbolVariable SimVar = (SymbolVariable) var.getAmbito().getSymbolTable().getSymbol(var.getName());

        Temporal temp = (Temporal) rdo;
        
        if (SimVar.getScope().getName().equals(var.getScope().getName())) {
            trad=trad+"SUB .IY , #"+ SimVar.getDesplazamiento()+"\n";
            trad=trad+"MOVE .A ,  #-"+temp.getDesplazamiento()+"[.IY]";
        
        } else {
            // Variable en otro ambito 
            trad = trad + "MOVE /" + SimVar.getScope().getLevel() + " , .R1 \n";
            trad = trad + "SUB .R1 , #" + SimVar.getDesplazamiento() + "\n";
            trad=trad+"MOVE .A ,  #-"+temp.getDesplazamiento()+"[.IY]";
        }
        
        return trad;
    }
    
        private String traducir_DIR_MEM_REGISTRO(QuadrupleIF quadruple){
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
            trad= trad+"MOVE .A , "+resultado;
        } else {

            // Variable en otro ambito 
            trad = trad + "MOVE /" + SimVar.getScope().getLevel() + " , .R1 \n";
            trad= trad+"SUB .R1 , #"+(SimVar.getDesplazamiento()+var2.getDesplCampo())+"\n";  
            trad= trad+"MOVE .A , "+resultado;
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
                }else {
                    
                   // Variable en otro ambito 
                   trad=trad+"MOVE /"+SimVar.getScope().getLevel()+" , .R1 \n";
                   trad=trad+"SUB .R1 , #"+SimVar.getDesplazamiento()+"\n";
                   trad=trad+"MOVE [.A] , .R2 \n";
                   operador1 = ".R2";
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
                }else {
                   // Variable en otro ambito 
                   trad=trad+"MOVE /"+SimVar.getScope().getLevel()+" , .R3 \n";
                   trad=trad+"SUB .R3 , #"+SimVar.getDesplazamiento()+"\n";
                   // trad=trad+"MOVE [.A] , .R4 \n";
                   // resultado= ".R4";
                   resultado="[.A]";
                }
            } else{
            
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
                }else {
                    
                 // Variable en otro ambito 
                 trad=trad+"MOVE /"+SimVar.getScope().getLevel()+" , .R3 \n";
                 trad=trad+"SUB .R3 , #"+SimVar.getDesplazamiento()+"\n";
                 trad=trad+"MOVE [.A] , .R4 \n";
                 operador2 = ".R4";
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
        } else {
            // Variable en otro ambito 
            trad = trad + "MOVE /" + SimVar1.getScope().getLevel() + " , .R1 \n";
            trad= trad+"SUB .R1 , #"+(SimVar1.getDesplazamiento()+var2.getDesplCampo())+"\n";  
        }
        trad= trad+"MOVE "+operador2+" , [.A]";
        return trad;
    }
    
    private String traducir_ASIG_REGISTRO_PUNTERO(QuadrupleIF quadruple){
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
                }else {
                    
                 // Variable en otro ambito 
                 trad=trad+"MOVE /"+SimVar.getScope().getLevel()+" , .R3 \n";
                 trad=trad+"SUB .R3 , #"+SimVar.getDesplazamiento()+"\n";
                 trad=trad+"MOVE [.A] , .R4 \n";
                 operador2 = ".R4";
                }
            } else {
                Temporal temp = (Temporal) oper2;
                int desp = temp.getDesplazamiento();
                operador2 = "#-" + desp + "[.IY]";
            }
        }
        
        // Primer operando
        // Se trata despu�s la asignaci�n por temas del acumulador
        if (SimVar1.getScope().getName().equals(var1.getScope().getName())) {
            trad = trad+"SUB "+PUNTERO_MARCO+" , #"+(SimVar1.getDesplazamiento()+var2.getDesplCampo())+"\n";  
            trad= trad+"MOVE [.A] , .R2 \n";  
            trad= trad+"SUB .R2 , #0\n";
        } else {

            // Variable en otro ambito 
            trad = trad + "MOVE /" + SimVar1.getScope().getLevel() + " , .R1 \n";
            trad= trad+"SUB .R1 , #"+(SimVar1.getDesplazamiento()+var2.getDesplCampo())+"\n";  
            trad= trad+"MOVE [.A] , .R2 \n";  
            trad= trad+"SUB .R2 , #0\n";
        }

        trad= trad+"MOVE "+operador2+" , [.A]";
        return trad;
    }
    
    private String traducir_ASIG_PUNTERO(QuadrupleIF quadruple){
        String trad= "; Traducir "+quadruple.toString() +"\n";
        OperandIF rdo= quadruple.getResult();
        OperandIF oper1= quadruple.getFirstOperand();
        
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
                operador1 = "#-" + SimVar.getDesplazamiento() + "[.IY]";
                if (SimVar.getScope().getName().equals(var.getScope().getName())) {
                    operador1 = "#-" + SimVar.getDesplazamiento() + "[.IY]";
                } else {
                    // Variable en otro ambito 
                    trad = trad + "MOVE /" + SimVar.getScope().getLevel() + " , .R3 \n";
                    trad = trad + "SUB .R3 , #" + SimVar.getDesplazamiento() + "\n";
                    trad = trad + "MOVE [.A] , .R4 \n";
                    operador1 = ".R4";
                }
            } else {
                Temporal temp = (Temporal) oper1;
                int desp = temp.getDesplazamiento();
                operador1 = "#-" + desp + "[.IY]";
            }
        }
        // Resultado al acumulador y movemos la expr a la direcci�n del acumulador
        Variable var = (Variable) rdo;
        SymbolVariable SimVar = (SymbolVariable) var.getAmbito().getSymbolTable().getSymbol(var.getName());
        resultado = "#-" + SimVar.getDesplazamiento() + "[.IY]";
        
        if (SimVar.getScope().getName().equals(var.getScope().getName())) {
            trad = trad + "SUB " + resultado + ", #0 \n";
            trad = trad + "MOVE " + operador1 + ", [.A]";
        } else {

            // Variable en otro ambito 
            trad = trad + "MOVE /" + SimVar.getScope().getLevel() + " , .R1 \n";
            trad = trad + "SUB .R1 , #" + SimVar.getDesplazamiento() + "\n";
            trad = trad + "MOVE [.A] , .R2 \n";
            trad = trad + "SUB .R2 , #0 \n";
            trad = trad + "MOVE " + operador1 + ", [.A]";
        }
        return trad;
    }
    private String traducir_ETIQUETA(QuadrupleIF quadruple){
        OperandIF rdo = quadruple.getResult();
        String trad = "; Etiqueta de salto " + rdo+"\n";
        trad = trad + cambiarEtiqueta(rdo.toString()) + " :";
        return trad;
    }
    private String traducir_BZ(QuadrupleIF quadruple){
        String trad=""; 
        OperandIF rdo= quadruple.getResult();
        trad = "; Salto si cero a etiqueta " + rdo+"\n";
        trad = trad + "BZ /" + cambiarEtiqueta(rdo.toString()); 
        return trad;
    }
    private String traducir_BNZ(QuadrupleIF quadruple){
        String trad=""; 
        OperandIF rdo= quadruple.getResult();
        trad = "; Salto si no cero a etiqueta " + rdo+"\n";
        trad = trad + "BNZ /" + cambiarEtiqueta(rdo.toString()); 
        return trad;
    }
    private String traducir_BN(QuadrupleIF quadruple){
        String trad=""; 
        OperandIF rdo= quadruple.getResult();
        trad = "; Salto si negativo " + rdo+"\n";
        trad = trad + "BN /" + cambiarEtiqueta(rdo.toString()); 
        return trad;
    }
    private String traducir_BR(QuadrupleIF quadruple){
        String trad=""; 
        OperandIF rdo= quadruple.getResult();
        trad = "; Salto incondicional " + rdo+"\n";
        trad="BR /" + cambiarEtiqueta(rdo.toString()); 
        return trad;
    }
    private String traducir_BP(QuadrupleIF quadruple){
        String trad=""; 
        OperandIF rdo= quadruple.getResult();
        trad = "; Salto si positiv0 " + rdo+"\n";
        trad = trad + "BP /" + cambiarEtiqueta(rdo.toString()); 
        return trad;
    }
    private String traducir_INC(QuadrupleIF quadruple){
        String trad=""; 
        Variable var=(Variable) quadruple.getResult();
        SymbolVariable SimVar = (SymbolVariable) var.getAmbito().getSymbolTable().getSymbol(var.getName());
        
        if (SimVar.getScope().getName().equals(var.getScope().getName())) {
            trad="INC #-" +SimVar.getDesplazamiento()+"[.IY]";
        } else {

            // Variable en otro ambito 
            trad = trad + "MOVE /" + SimVar.getScope().getLevel() + " , .R1 \n";
            trad = trad + "SUB .R1 , #" + SimVar.getDesplazamiento() + "\n";
            trad = trad + "MOVE [.A] , .R2 \n";
            trad = trad + "INC .R2 \n";
            trad = trad + "MOVE .R2 , [.A]";
        }        
        return trad;
    }

    private String traducir_INICIAR_SET(QuadrupleIF quadruple) {
        String trad= "; Traducir "+quadruple.toString() +"\n";
        OperandIF rdo= quadruple.getResult();
        OperandIF oper1= quadruple.getFirstOperand();
   
        String resultado="";
         // Operador1: Tamanyo del conjunto
        Value tamanyo = (Value) oper1;
        
        // Resultado Var
        Variable var=(Variable) rdo;
        SymbolVariable SimVar=(SymbolVariable) var.getAmbito().getSymbolTable().getSymbol(var.getName());
        for (int i = 0; i < (Integer) tamanyo.getValue(); i++) {
            int d = SimVar.getDesplazamiento()+i;
            if (SimVar.getScope().getName().equals(var.getScope().getName())) {
                resultado = "#-" + d + "[.IY]";
            } else {
                // Variable en otro ambito 
                trad = trad + "MOVE /" + SimVar.getScope().getLevel() + " , .R1 \n";
                trad = trad + "SUB .R1 , #" + d + "\n";
                trad = trad + "MOVE [.A] , .R2 \n";
                resultado = ".R2";
            }
            trad = trad + "MOVE #0 , "+resultado + "\n";
        }
        return trad;
        
    }
    private String traducir_CARGAR_SET(QuadrupleIF quadruple){
        String trad= "; Traducir "+quadruple.toString() +"\n";
        OperandIF oper1= quadruple.getFirstOperand();
        OperandIF oper2= quadruple.getSecondOperand();
        String resultado= "";
        
        // Resultado 
        Variable var = (Variable) quadruple.getResult();
        SymbolVariable SimVar = (SymbolVariable) var.getAmbito().getSymbolTable().getSymbol(var.getName());
        TypeSet tipoSet = (TypeSet) SimVar.getType();
        
        // Primer y Segundo Operador
        Value valIni  = (Value) oper1;
        Value valFin  = (Value) oper2;
        
        for (int i= (Integer) valIni.getValue(); i<= (Integer) valFin.getValue(); i++){
            int d = SimVar.getDesplazamiento()+i-1;
            if (SimVar.getScope().getName().equals(var.getScope().getName())) {
                resultado = "#-" + d + "[.IY]";
            } else {
                // Variable en otro ambito 
                trad = trad + "MOVE /" + SimVar.getScope().getLevel() + " , .R3 \n";
                trad = trad + "SUB .R3 , #" + d + "\n";
                resultado = "[.A]";
            }
            trad = trad + "MOVE #1 , "+resultado+"\n";    
        }
        return trad;
    }
    private String traducir_IN_SET(QuadrupleIF quadruple){
        String trad= "; Traducir "+quadruple.toString() +"\n";
        
        OperandIF rdo   = quadruple.getResult();
        OperandIF oper1 = quadruple.getFirstOperand();
        OperandIF oper2 = quadruple.getSecondOperand();
        String operador1="";
        String operador2="";
        String resultado="";
        
        
        // Segundo OPERANDO: Expresion
        if (oper2 instanceof Value){
            Value cte=(Value) oper2;
            operador2="#" + cte.getValue();
         }else{
            if (oper2 instanceof Variable){
                Variable var1=(Variable) oper2;
                SymbolVariable SimVar1=(SymbolVariable) var1.getAmbito().getSymbolTable().getSymbol(var1.getName());
                if ( SimVar1.getScope().getName().equals(var1.getScope().getName()) ){ 
                   operador2 = "#-" + SimVar1.getDesplazamiento() + "[.IY]";
                }else {
                    
                   // Variable en otro ambito 
                   trad=trad+"MOVE /"+SimVar1.getScope().getLevel()+" , .R3 \n";
                   trad=trad+"SUB .R3 , #"+SimVar1.getDesplazamiento()+"\n";
                   trad=trad+"MOVE [.A] , .R4 \n";
                   operador2 = ".R4";
                }
            }else{
                Temporal temp = (Temporal) oper2; 
                operador2="#-" + temp.getDesplazamiento() + "[.IY]";
            }
        }
        // Primer OPERANDO: variable cjto
        Variable var = (Variable) oper1;
        SymbolVariable SimVar = (SymbolVariable) var.getAmbito().getSymbolTable().getSymbol(var.getName());
        
        if (SimVar.getScope().getName().equals(var.getScope().getName())) {
            int d = SimVar.getDesplazamiento()-1;
            trad = trad + "ADD "+operador2+" ,#" + d +"\n";
            trad = trad + "SUB .IY , .A \n";
            operador1 = "[.A]";
        } else {
            // Variable en otro ambito 
            trad = trad + "MOVE /" + SimVar.getScope().getLevel() + " , .R1 \n";
            trad = trad + "SUB .R1 , #" + SimVar.getDesplazamiento() + "\n";
            trad = trad + "ADD "+operador2+" , [.A] \n";
            trad = trad + "MOVE [.A] , .R2 \n";
            operador1 = ".R2";
        }
        
        // Resultado
        Temporal temp = (Temporal) rdo; 
        resultado="#-" + temp.getDesplazamiento() + "[.IY]";
        
        trad = trad + "MOVE "+operador1+" , " + resultado ;
        
        return trad;
    }
    private String traducir_UNION_SET(QuadrupleIF quadruple){
        String trad= "; Traducir "+quadruple.toString() +"\n";
        OperandIF rdo   = quadruple.getResult();
        OperandIF oper1 = quadruple.getFirstOperand();
        OperandIF oper2 = quadruple.getSecondOperand();
        String operador1="";
        String operador2="";
        String resultado="";
        
        // Resultado
        Temporal temp = (Temporal) rdo; 
        resultado="#-" + temp.getDesplazamiento() + "[.IY]";
        
        int d = 0;
        
        for (int i=0; i<temp.getSize(); i++){
            // Oper1 es variable o temporal
            if (oper1 instanceof Variable) {
                Variable var = (Variable) oper1;
                SymbolVariable SimVar = (SymbolVariable) var.getAmbito().getSymbolTable().getSymbol(var.getName());
                
                d = SimVar.getDesplazamiento()+i;
                if (SimVar.getScope().getName().equals(var.getScope().getName())) {
                    operador1 = "#-" + d + "[.IY]";
                } else {
                    // Variable en otro ambito 
                    trad = trad + "MOVE /" + SimVar.getScope().getLevel() + " , .R1 \n";
                    trad = trad + "SUB .R1 , #" + d + "\n";
                    trad = trad + "MOVE [.A] , .R2 \n";
                    operador1 = ".R2";
                }
            } else {
                Temporal temp1 = (Temporal) oper1;
                d = temp1.getDesplazamiento()+i;
                operador1 = "#-" + d + "[.IY]";
            }
            
            // Oper1 es variable o temporal
            if (oper2 instanceof Variable) {
                Variable var2 = (Variable) oper2;
                SymbolVariable SimVar2 = (SymbolVariable) var2.getAmbito().getSymbolTable().getSymbol(var2.getName());
                d = SimVar2.getDesplazamiento()+i;
                if (SimVar2.getScope().getName().equals(var2.getScope().getName())) {
                    operador2 = "#-" + d + "[.IY]";
                } else {    
                    // Variable en otro ambito 
                    trad = trad + "MOVE /" + SimVar2.getScope().getLevel() + " , .R3 \n";
                    trad = trad + "SUB .R3 , #" + d + "\n";
                    trad = trad + "MOVE [.A] , .R4 \n";
                    operador2 = ".R4";
                }
            } else {
                Temporal temp2 = (Temporal) oper2;
                d = temp2.getDesplazamiento()+i;
                operador2 = "#-" + d + "[.IY]";
            }
            
            d = temp.getDesplazamiento() + i;
            resultado="#-" + d + "[.IY]";
            trad = trad + "OR "+operador1+" , "+operador2+"\n";
            trad = trad + "MOVE .A , "+resultado+"\n";
            
        }
        return trad;
    }
    private String traducir_ASIG_SET(QuadrupleIF quadruple){
        String trad= "; Traducir "+quadruple.toString() +"\n";
        OperandIF rdo   = quadruple.getResult();
        OperandIF oper1 = quadruple.getFirstOperand();
        String operador1="";
        String resultado="";
        
        // Operando 1: Variable cjto
        Variable var = (Variable) rdo;
        SymbolVariable SimVar = (SymbolVariable) var.getAmbito().getSymbolTable().getSymbol(var.getName());
        TypeSet tipoSet = (TypeSet) SimVar.getType();
        // Resultado
        Temporal temp = (Temporal) oper1; 
        resultado="#-" + temp.getDesplazamiento() + "[.IY]";
        
        int d = 0;
        
        for (int i=0; i<tipoSet.getTamanyo(); i++){
            d = SimVar.getDesplazamiento()+i;
            if (SimVar.getScope().getName().equals(var.getScope().getName())) {
                operador1 = "#-" + d + "[.IY]";
            } else {
                // Variable en otro ambito 
                trad = trad + "MOVE /" + SimVar.getScope().getLevel() + " , .R1 \n";
                trad = trad + "SUB .R1 , #" + d + "\n";
                trad = trad + "MOVE [.A] , .R2 \n";
                operador1 = ".R2";
            }
            d = temp.getDesplazamiento() + i;
            resultado="#-" + d + "[.IY]";
            trad = trad + "MOVE "+resultado+" , "+operador1+"\n";
            
        }
        return trad;
    }
    private String traducir_CALL(QuadrupleIF quadruple){
        
        String trad="; Llamada Funcion "+quadruple+"\n";
        Variable rdo = (Variable) quadruple.getResult();
        Value valor = (Value) quadruple.getFirstOperand();
        int nivel = (Integer) valor.getValue();
        OperandIF oper2 = quadruple.getSecondOperand();
        
        String etiqRet = cambiarEtiqueta(rdo.getEtiqRetorno().toString());
        String etiq = cambiarEtiqueta(rdo.getEtiqSub().toString());
        
        // Desplazamiento
        Integer d = (Integer)this.tablaScopes.get(rdo.getName());
        int despl = d.intValue();
        
        trad =trad+"MOVE "+STACK_POINTER+" , "+VINCULO_CONTROL+"\n";
        
        // Argumentos
        for (int i = 1; i < lista[contadorLlamadas].size(); i++) {
        	String s = (String) lista[contadorLlamadas].get(i);
        	trad=trad+s+"\n";
        }
        
        // una vez que se ha imprimido esa lita, hay que vaciarla
        lista[contadorLlamadas]=new ArrayList();
        
        // Areglo ambito
        trad=trad+"MOVE /"+nivel +" , "+RA_VINCULO_ACCESO+"\n";        
        trad=trad+"MOVE .IY , /"+nivel+"\n";        
        
        trad= trad+"MOVE "+PUNTERO_MARCO+" , "+VINCULO_CONTROL+"\n";
        trad= trad+"MOVE "+STACK_POINTER +" , "+PUNTERO_MARCO  +"\n";
        
        // Consideramos variables y temporales
        trad=trad+"SUB "+STACK_POINTER+" , #" + despl+"\n";  
        trad=trad+"MOVE "+ACUMULADOR+" , "+STACK_POINTER+"\n";
        trad=trad+"MOVE #RET_"+ etiqRet  +" , "+RA_DIRECCION_RETORNO+"\n";     
        trad=trad+"MOVE "+VINCULO_CONTROL+" , "+RA_VINCULO_CONTROL+"\n";        
        trad=trad+"MOVE #REF_"+ etiqRet  +" , #-4[.IY]\n";
        
        // Salto a la funcion 
        trad=trad+"BR /"+ etiq   +"\n";
        
        // Parametros referencia (copia-valor)
        trad=trad+"REF_"+ etiqRet  +":\n";
        for (int i=0; i<listaPorReferencia.size(); i++){
            String s = (String) listaPorReferencia.get(i);
            trad=trad+s+"\n";
        }
        listaPorReferencia.clear();
        
        trad=trad+"BR /REF_"+ etiq   +"\n";

        // Etiqueta Retorno llamada
        trad=trad+"RET_"+ etiqRet+": \n";
        
        // Cuando es un procedimiento esta sentencia no se implementa
        if (oper2 instanceof Temporal) {
            Temporal operando2 = (Temporal) oper2;
            trad=trad+"MOVE .R9 , #-"+ operando2.getDesplazamiento()+"[.IY]\n";
        } else {
            trad=trad+"NOP \n";    
        }    
        contadorLlamadas--;
        return trad;      
        
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
                }else {
                    
                   // Variable en otro ambito 
                   trad=trad+"MOVE /"+SimVar.getScope().getLevel()+" , .R1 \n";
                   trad=trad+"SUB .R1 , #"+SimVar.getDesplazamiento()+"\n";
                   trad=trad+"MOVE [.A] , .R2 \n";
                   operador1 = ".R2";
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
        String trad="";
        OperandIF rdo = quadruple.getResult();
        OperandIF oper1 = quadruple.getFirstOperand();
        Value valor = (Value) quadruple.getSecondOperand();
        int nivel =(Integer) valor.getValue();
        
        String etiqFin = cambiarEtiqueta(oper1.toString());
        trad=trad+etiqFin+" : \n";
        // Pasar valores por Referencia (Copia - valor)
        trad=trad+"; Retorno Argumentos REFERENCIA \n";
        trad=trad+"MOVE #-4[.IY] , .R5 \n"; 
        trad=trad+"MOVE .R5 , .PC \n";
        trad=trad+"REF_"+cambiarEtiqueta(rdo.toString())+": \n";
                
        trad=trad+"; Retorno Subprograma \n";
        trad=trad+"MOVE "+RA_VALOR_RETORNO+" , .R9"+"\n"; // se queda el valor de retorno en el R9
        trad=trad+"MOVE "+RA_DIRECCION_RETORNO+" , .R7\n";
        
        // Arreglo ambitos
        trad=trad+"MOVE "+RA_VINCULO_ACCESO+" , /"+nivel+"\n";
        
        trad=trad+"MOVE "+PUNTERO_MARCO+" , "+STACK_POINTER+"\n";
        trad=trad+"MOVE "+VINCULO_CONTROL+" , "+PUNTERO_MARCO+"\n";
        trad=trad+"MOVE "+RA_VINCULO_CONTROL +", "+VINCULO_CONTROL+"\n";        
        trad=trad+"MOVE .R7 , "+CONTADOR_PROGRAMA+"\n";
        
        trad=trad+"FIN_"+cambiarEtiqueta(rdo.toString())+" : \n";
        return trad;      
  }
        private String traducir_INICIO_ARGUMENTOS(QuadrupleIF quadruple){
        contadorLlamadas++;
        Integer i = Integer.valueOf(0);
        
        lista[contadorLlamadas].add(i); 
        return "; INICIO ARGUMENTOS FIN";
   }

    private String traducir_ARGUMENTO(QuadrupleIF quadruple){
        OperandIF rdo = quadruple.getResult();
        OperandIF oper1= quadruple.getFirstOperand();
        
        // Desplazamiento
        Value operando1 = (Value) oper1;
        int despl = (Integer) operando1.getValue();
        // Parametros
        Integer numArg = (Integer) lista[contadorLlamadas].get(0);
    	int offsetParametro = numArg.intValue();
        // Evaluamos argumentos
        offsetParametro++;
        Integer p = Integer.valueOf(offsetParametro);
        lista[contadorLlamadas].remove(0);
        lista[contadorLlamadas].add(0, p);
        
        if (rdo instanceof Value) {
            Value cte = (Value) rdo;
            String str = "MOVE #"+cte.getValue() +" , #-" + despl+ "[.IX]";
            lista[contadorLlamadas].add(str);
        }else{    
            if (rdo instanceof Variable){
                Variable var = (Variable) rdo;
                SymbolVariable SimVar=(SymbolVariable) var.getAmbito().getSymbolTable().getSymbol(var.getName());
                String str="";
                // Ambito variable
                if ( SimVar.getScope().getName().equals(var.getScope().getName()) ){ 
                   str = "MOVE #-"+SimVar.getDesplazamiento() + "[.IY] , #-" + despl+ "[.IX]";
                }else {
                   // Variable en otro ambito 
                   str="MOVE /"+SimVar.getScope().getLevel()+" , .R1 \n";
                   str=str+"SUB .R1 , #"+SimVar.getDesplazamiento()+"\n";
                   str=str+"MOVE [.A] , #-"+ despl+ "[.IX] \n";
                }
                lista[contadorLlamadas].add(str);
            }else {
                Temporal temp = (Temporal) rdo;
                String str = "MOVE #-"+temp.getDesplazamiento() + "[.IY] , #-" + despl+ "[.IX]";
                lista[contadorLlamadas].add(str);
            }
        }
        return "; Cargado argumento "+quadruple;
    }
    
    private String traducir_ARGUMENTO_REF(QuadrupleIF quadruple){
        OperandIF rdo = quadruple.getResult();
        OperandIF oper1= quadruple.getFirstOperand();
        
        // Desplazamiento
        Value operando1 = (Value) oper1;
        int despl = (Integer) operando1.getValue();
        // Parametros
        Integer numArg = (Integer) lista[contadorLlamadas].get(0);
    	int offsetParametro = numArg.intValue();
        // Evaluamos argumentos
        offsetParametro++;
        Integer p = Integer.valueOf(offsetParametro);
        lista[contadorLlamadas].remove(0);
        lista[contadorLlamadas].add(0, p);
        
        // Por referencia solo puede ser variable (ni temporal, ni valor)
        Variable var = (Variable) rdo;
        SymbolVariable SimVar=(SymbolVariable) var.getAmbito().getSymbolTable().getSymbol(var.getName());
                
        String str="";
        String str2="";
        // Ambito variable
        if ( SimVar.getScope().getName().equals(var.getScope().getName()) ){ 
            str = "MOVE #-"+SimVar.getDesplazamiento() + "[.IY] , #-" + despl+ "[.IX]";
            str2 = "MOVE #-"+ despl+ "[.IY] , #-"+ SimVar.getDesplazamiento()+ "[.IX]";
        }else {
            // Variable en otro ambito 
            str="MOVE /"+SimVar.getScope().getLevel()+" , .R1 \n";
            str=str+"SUB .R1 , #"+SimVar.getDesplazamiento()+"\n";
            str=str+"MOVE [.A] , #-"+ despl+ "[.IX] \n";
            
            // Parametro copia/valor
            str2="MOVE /"+SimVar.getScope().getLevel()+" , .R1 \n";
            str2=str2+"SUB .R1 , #"+SimVar.getDesplazamiento()+"\n";
            str2=str2+"MOVE #-"+despl +"[.IY] , [.A]";
            
        }
        // Guarda instruciones ensamblador de los par REFERENCIADOS        
        lista[contadorLlamadas].add(str);
        listaPorReferencia.add(str2);
        // parPorReferencia.put(var.getAmbito().getLevel(), listaPorReferencia);
            
        return "; Cargado argumento REFERENCIA "+quadruple;
        
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
               }else {
                   // Variable en otro ambito 
                   trad=trad+"MOVE /"+SimVar.getScope().getLevel()+" , .R1 \n";
                   trad=trad+"SUB .R1 , #"+SimVar.getDesplazamiento()+"\n";
                   trad=trad+"MOVE [.A] , .R2 \n";
                   resultado = ".R2";
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
    
    // Escribir una cadena
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
