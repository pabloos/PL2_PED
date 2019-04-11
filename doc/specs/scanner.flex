package compiler.lexical;

import compiler.syntax.sym;
import compiler.lexical.Token;
import es.uned.lsi.compiler.lexical.ScannerIF;
import es.uned.lsi.compiler.lexical.LexicalError;
import es.uned.lsi.compiler.lexical.LexicalErrorManager;

// incluir aqui, si es necesario otras importaciones


%%
%public
%class Scanner
%char
%line
%column
%cup
%ignorecase

%implements ScannerIF
%scanerror LexicalError

%full 
%unicode

%state YYCOMENTARIO



%{  
    
    LexicalErrorManager lexicalErrorManager = new LexicalErrorManager ();

    /**Contador de la profundidad de anidamiento del comentario.*/
    int nComentaryCount=0;

    
    

    /**Construye un token con los datos proporcionados*/
    private Token buildToken(int nTokId, int nLine, int nColumn, String szLexema){
       Token token = new Token (nTokId);
       token.setLine (nLine + 1);
       token.setColumn (nColumn + 1);
       token.setLexema (szLexema);

       
       return token;
    }

%}






/*
 *Definici�n de macros:
 */
LETRA=[a-zA-Z]
DIGITO=[0-9]
LIT_ENT=0|[1-9][0-9]*
//Cualquier cosa entre comillas
LIT_STR=\"([^\"])*\"
IDENTIFICADOR={LETRA}({LETRA}|{DIGITO})*
BLANCO=[\n\r\ \t\b]*
//Cualquier cosa que no sea "(*" o "*)", o bien, unos par�ntesis solos o un * solo.
CUERPO_COMENT=([^"(*""*)"]* | ["("")""*"])

%%

<YYINITIAL> 
{
    "+"				    { return buildToken(sym.TSUMA, yyline, yycolumn, yytext()); }

    // incluir aqui el resto de las reglas patron - accion

    
    <<EOF>>             { 
                          return buildToken(sym.EOF, yyline, yycolumn, "End Of File");
                        }
    "(*"                { 
                          nComentaryCount=1;                          
                          yybegin(YYCOMENTARIO);
                        }
	"*)"                {   
                            LexicalError error = new LexicalError ("Final de comentario SIN balancear: ");
                            error.setLine (yyline + 1);
                            error.setColumn (yycolumn + 1);
                            error.setLexema (yytext ());
                            lexicalErrorManager.lexicalFatalError ( error);
                        }




    "AND"               { return buildToken(sym.TAND, yyline, yycolumn, yytext()); }
    "NOT"               { return buildToken(sym.TNOT, yyline, yycolumn, yytext()); }
    "BEGIN"			    { return buildToken(sym.TBEGIN, yyline, yycolumn, yytext()); }
    "END"			    { return buildToken(sym.TEND, yyline, yycolumn, yytext()); }
    "IF"			    { return buildToken(sym.TIF, yyline, yycolumn, yytext()); }
    "THEN"			    { return buildToken(sym.TTHEN, yyline, yycolumn, yytext()); }
    "ELSE"			    { return buildToken(sym.TELSE, yyline, yycolumn, yytext()); }
    "MODULE"		    { return buildToken(sym.TMODULE, yyline, yycolumn, yytext()); }
    "PROCEDURE"		    { return buildToken(sym.TPROCEDURE, yyline, yycolumn, yytext()); }
    "RETURN"		    { return buildToken(sym.TRETURN, yyline, yycolumn, yytext()); }
    "DO"			    { return buildToken(sym.TDO, yyline, yycolumn, yytext()); }
    "WHILE" 		    { return buildToken(sym.TWHILE, yyline, yycolumn, yytext()); }
    "WRITESTRING"	    { return buildToken(sym.TWRITESTRING, yyline, yycolumn, yytext()); }
    "WRITEINT"		    { return buildToken(sym.TWRITEINT, yyline, yycolumn, yytext()); }
    "WRITELN"		    { return buildToken(sym.TWRITELN, yyline, yycolumn, yytext()); }
    "BOOLEAN"		    { return buildToken(sym.TBOOLEAN, yyline, yycolumn, yytext()); }
    "CONST"		        { return buildToken(sym.TCONST, yyline, yycolumn, yytext()); }
    "INTEGER"		    { return buildToken(sym.TINTEGER, yyline, yycolumn, yytext()); }
    "RECORD"	    	{ return buildToken(sym.TRECORD, yyline, yycolumn, yytext()); }
    "TYPE"	    	    { return buildToken(sym.TTYPE, yyline, yycolumn, yytext()); }
    "VAR"   		    { return buildToken(sym.TVAR, yyline, yycolumn, yytext()); }

    "TRUE"              { return buildToken(sym.TLTRUE, yyline, yycolumn, yytext()); }
    "FALSE"             { return buildToken(sym.TLFALSE, yyline, yycolumn, yytext()); }
    {LIT_ENT}           { return buildToken(sym.TLINT, yyline, yycolumn, yytext()); }
    {LIT_STR}		    { return buildToken(sym.TLSTRING, yyline, yycolumn, yytext()); }

    ";"     		    { return buildToken(sym.TPTOCOMA, yyline, yycolumn, yytext()); }
    "("	        	    { return buildToken(sym.TPARI, yyline, yycolumn, yytext()); }
    ")"		            { return buildToken(sym.TPARD, yyline, yycolumn, yytext()); }
    ","     		    { return buildToken(sym.TCOMA, yyline, yycolumn, yytext()); }
    ":"     		    { return buildToken(sym.TDOSPTS, yyline, yycolumn, yytext()); }
    "="     		    { return buildToken(sym.TIGUAL, yyline, yycolumn, yytext()); }

    "/"     		    { return buildToken(sym.TDIVIDE, yyline, yycolumn, yytext()); }
    ":="	    	    { return buildToken(sym.TASSIGN, yyline, yycolumn, yytext()); }
    "<"	    	        { return buildToken(sym.TMENOR, yyline, yycolumn, yytext()); }
    "<>"	    	    { return buildToken(sym.TDISTINTO, yyline, yycolumn, yytext()); }
    "."	    	        { return buildToken(sym.TPUNTO, yyline, yycolumn, yytext()); }


    {IDENTIFICADOR}     { return buildToken(sym.TIDENTIFICADOR, yyline, yycolumn, yytext()); }
    {BLANCO}            {  }



    // error en caso de no coincidir con ning�n patr�n
	[^]                 {
                            LexicalError error = new LexicalError ("Caracter incorrecto: ");
                            error.setLine (yyline + 1);
                            error.setColumn (yycolumn + 1);
                            error.setLexema (yytext ());
                            lexicalErrorManager.lexicalFatalError ( error);
                        }
    
}
<YYCOMENTARIO>
{
    "(*"                {
                            nComentaryCount++;
                            
                        }
    "*)"                {
                            if(nComentaryCount==1){
                                nComentaryCount--;
                            
                                yybegin(YYINITIAL);
                            } else {
                            
                                nComentaryCount--;
                            }
                        }
    {CUERPO_COMENT}     {
                            
                        }
    <<EOF>>             {
                            LexicalError error = new LexicalError ("Comentario sin balancear");
                            error.setLine (yyline + 1);
                            error.setColumn (yycolumn + 1);
                            error.setLexema ("EOF en");

                            lexicalErrorManager.lexicalFatalError (error);
                            
                            System.exit(1);
                         }
}
