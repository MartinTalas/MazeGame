import javafx.application.Application

/*
    -------------------------------
    Made by GKPLJP - Tálas Martin
    Programtervező Informatikus BSc
               [ 2021 ]
    -------------------------------
*/

fun main()
{
    //--------[ GAME ]--------
    val game = Maze()
    val run = game::class.java
    Application.launch(run)
    //------------------------
}
