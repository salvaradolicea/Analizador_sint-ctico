# Analizador_sint-ctico
🧾 Analizador Sintáctico - Lenguaje PF2025
📌 Descripción
Este proyecto implementa un analizador léxico y sintáctico en Java para el lenguaje PF2025, desarrollado como parte de la asignatura Lenguajes y Autómatas.
El sistema es capaz de leer un archivo fuente (progfte.txt), analizarlo y verificar su estructura gramatical.

⚙️ Funcionamiento
El sistema se divide en dos etapas:
🔹 1. Analizador Léxico

Identifica los tokens del programa
Clasifica palabras reservadas, operadores, identificadores y números
Genera:

progfte.tok
progfte.tab
progfte.dep




🔹 2. Analizador Sintáctico

Toma la lista de tokens como entrada
Valida la estructura del lenguaje
Genera un árbol sintáctico
Detecta errores


🧠 Gramática del lenguaje
PROGRAMA → pf2025 DECL BLOQUE

DECL → decl LISTA_DECL
LISTA_DECL → TIPO ID (, ID)* ;

BLOQUE → inicio SENTENCIAS end

SENTENCIAS → SENTENCIA*

SENTENCIA → ASIGNACION | IMPRESION | LECTURA

ASIGNACION → ID := EXPRESION ;

EXPRESION → T (+|-) T*
T → F (*|/) F*
F → (EXPRESION) | ID | NUM


🌳 Árbol sintáctico
El analizador construye un árbol que representa la estructura del programa.
Ejemplo:
x := 5 + 3 * 2

Árbol:
      :=
     /  \
    x    +
        / \
       5   *
          / \
         3   2


🚨 Manejo de errores
El sistema detecta:

Paréntesis desbalanceados
Operadores consecutivos
Falta de operandos
Tokens inesperados


🧩 Estructura del proyecto
/src
 ├── Lexer.java
 ├── Token.java
 ├── TokenType.java
 ├── Parser.java
 ├── Nodo.java
 ├── Main.java
 ├── SymbolTable.java
 ├── EntradaSimbolo.java
 ├── FileManager.java


▶️ Ejecución

Colocar archivo progfte.txt
Ejecutar Main.java
Se generan:

progfte.tok
progfte.tab
progfte.dep




✅ Ejemplo de entrada
pf2025
decl int x, y;
inicio
x := 5 + 3;
impdig(x);
end


✅ Resultado esperado
✔ Tokens generados
✔ Tabla de símbolos
✔ Validación sintáctica
✔ Árbol sintáctico
