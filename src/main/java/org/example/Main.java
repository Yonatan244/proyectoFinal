package org.example;

//TIP Para <b>ejecutar</b> el código, pulsa <shortcut actionId="Run"/> o
// haz clic en el icono <icon src="AllIcons.Actions.Execute"/> del margen.
public class Main {
    static void main() {
        //TIP Pulsa <shortcut actionId="ShowIntentionActions"/> con el cursor sobre el texto resaltado
        // para ver cómo sugiere IntelliJ IDEA corregirlo.
        IO.println(String.format("Hello and welcome!"));

        for (int i = 1; i <= 5; i++) {
            //TIP Pulsa <shortcut actionId="Debug"/> para empezar a depurar el código. Hemos establecido un punto de interrupción <icon src="AllIcons.Debugger.Db_set_breakpoint"/>
            // por ti, pero siempre puedes añadir más pulsando <shortcut actionId="ToggleLineBreakpoint"/>.
            IO.println("i = " + i);
        }
    }
}
