import javafx.application.Application
import javafx.application.Platform
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.image.ImageView
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.stage.Stage
import java.io.File
import java.nio.file.Paths
import java.util.*
import kotlin.system.exitProcess

/*
    -------------------------------
    Made by GKPLJP - Tálas Martin
    Programtervező Informatikus BSc
               [ 2021 ]
    -------------------------------
*/

class Maze : Application()
{
    //------[ VARIABLES ]-----------------------------------------------------------------------------------------------

    private val player: Player = Player() //játékos osztály, a játékos adatait tárolja (pozíció, stb)
    private val images: MyImageLoader = MyImageLoader() //a képeket innen töltjük be

    private var field: MutableList<MutableList<Int>> = mutableListOf() //ez alapján épül fel a pálya, illetve a logikája
    private var coinLocations: MutableList<Array<Int>> = mutableListOf() // az érmék koordinátái
    private var healLocations: MutableList<Array<Int>> = mutableListOf() // a heal potion-ök koordinátái
    private var trapLocations: MutableList<Array<Int>> = mutableListOf() // a hidden trap-ek koordinátái

    private var playable: Boolean = false //változó, hogy épp játszható-e a játék, vagy már a végére értünk
    private var escapable: Boolean = false //változó, hogy megynitható-e az escape menu -> a lateinitek miatt kell!
    private var stopped: Boolean = false //a timer thread megállítása miatt
    private var titleStr: String = "Maze Game by GKPLJP"
    private var version: MazeEnum = MazeEnum.MAZE1 // ha pályát akarunk választani, ebbe a változóba mentjük, hogy melyiket akarjuk játszani
    private var time: Int = 0 // timer
    private var minute: Int = 0
    private var second: Int = 0
    private var savedTime: String = "00:00"


    private var path = Paths.get(System.getProperty("user.dir")) // path a file-hoz

    //------------------------------------------------------------------------------------------------------------------

    //a háttérkép beállítása
    private var bg: BackgroundImage = BackgroundImage(images.getBackgroundImg(), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
        BackgroundPosition.DEFAULT, BackgroundSize(1280.0, 720.0, false, false, true, true)
    )
    private var esc: BackgroundImage = BackgroundImage(images.getEscapeBackgroundImg(), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
        BackgroundPosition.DEFAULT, BackgroundSize(980.0, 520.0, false, false, true, true)
    )

    //------------------------------------------------------------------------------------------------------------------

    //------[ FXML VARIABLES ]------------------------------------------------------------------------------------------
    @FXML
    lateinit var mainAnchor: AnchorPane
    @FXML
    lateinit var gameField: GridPane
    @FXML
    lateinit var playerField: GridPane
    @FXML
    lateinit var hpGrid: GridPane
    @FXML
    lateinit var coinGrid: GridPane
    @FXML
    lateinit var mainPanel: Pane
    @FXML
    lateinit var infoPanel: Pane
    @FXML
    lateinit var escapePanel: Pane
    @FXML
    lateinit var escInfoPanel: Pane
    @FXML
    lateinit var controlPanel: Pane
    @FXML
    lateinit var creditPanel: Pane
    @FXML
    lateinit var infSelectorPanel: Pane
    @FXML
    lateinit var conSelectorPanel: Pane
    @FXML
    lateinit var creSelectorPanel: Pane
    @FXML
    lateinit var giveUpBtn: Button
    @FXML
    lateinit var maze1Btn: Button
    @FXML
    lateinit var maze2Btn: Button
    @FXML
    lateinit var maze3Btn: Button
    @FXML
    lateinit var gameOverLabel: Label
    @FXML
    lateinit var coinCountLabel: Label
    @FXML
    lateinit var colonLabel: Label
    @FXML
    lateinit var felbtn: Label
    @FXML
    lateinit var lebtn: Label
    @FXML
    lateinit var jobbbtn: Label
    @FXML
    lateinit var balbtn: Label
    @FXML
    lateinit var wasdLabel: Label
    @FXML
    lateinit var arrowLabel: Label
    @FXML
    lateinit var buttonLabel: Label
    @FXML
    lateinit var infLabel: Label
    @FXML
    lateinit var conLabel: Label
    @FXML
    lateinit var creLabel: Label
    @FXML
    lateinit var timerLabel: Label

    //------------------------------------------------------------------------------------------------------------------

    //az Application felülírt start metódusa
    override fun start(primaryStage: Stage)
    {
        primaryStage.scene = Scene(FXMLLoader.load(javaClass.getResource("maze.fxml")))
        primaryStage.isResizable = false
        primaryStage.title = titleStr
        primaryStage.show()
        primaryStage.setOnCloseRequest { exitProcess(0) }
    }

    //file beolvasás -> itt olvassuk be a field-et, amelyet később eltárolunk
    private fun readFromFile(fileName: String): List<String> = File(fileName).bufferedReader().readLines()

    //a beolvasott értékeket szétbontja, és intet csinálunk belőle
    private fun toIntList(list: List<String>): MutableList<MutableList<Int>>
    {
        val tmp: MutableList<MutableList<Int>> = mutableListOf()
        val rows: MutableList<Int> = mutableListOf()

        for (element1 in list)
        {
            val strarr = element1.split("_")

            for(element2 in strarr) rows.add(element2.toInt())

            val flist = rows.toMutableList()
            tmp.add(flist)
            rows.clear()
        }

        return tmp
    }

    //betölti a pályának a "hátterét" (2 grid egymáson, az alsó a háttér, ez nem változik, a felette lévő, az az, amin a player is mozog)
    private fun loadBackground()
    {
        timerLabel.isVisible = true
        gameField.children.clear()
        mainPanel.background = Background(bg)//háttérkép hozzáadása
        loadEsc()

        val mazeNum: Int = when(version)
        {
            //----[ SELECT ]----
            MazeEnum.MAZE1 -> 1
            MazeEnum.MAZE2 -> 2
            MazeEnum.MAZE3 -> 3
            //------------------
        }

        field = toIntList(readFromFile("${path}/field${mazeNum}.dat"))//itt olvassa be az adatokat

        var i = 0
        var j = 0

        while (i < 20)
        {
            while (j < 20)
            {
                //a megfelelő helyre a megfelelő képet töltjük be
                //----------------------------------[ VISUALIZATION ]----------------------------------
                when
                {
                    field[i][j] == 9 -> gameField.add(ImageView(images.getWallImg()), j, i)
                    field[i][j] == 1 -> gameField.add(ImageView(images.getStartImg()),j,i)
                    field[i][j] == 5 -> gameField.add(ImageView(images.getFinishImg()),j,i)
                    field[i][j] == 2 -> gameField.add(ImageView(images.getWaterImg()),j,i)
                    field[i][j] == 3 -> gameField.add(ImageView(images.getLavaImg()),j,i)
                    else ->
                    {
                        gameField.add(ImageView(images.getWayImg()),j,i)

                        //az érmék és a heal potion-ök koordinátáit kimentjük
                        when
                        {
                            field[i][j] == 6 ->  coinLocations.add(arrayOf(i, j, 0))
                            field[i][j] == 4 -> healLocations.add(arrayOf(i, j, 0))
                            field[i][j] == 7 -> trapLocations.add(arrayOf(i, j, 0))
                        }
                    }
                }
                //--------------------------------------------------------------------------------------

                j += 1
            }
            j = 0
            i += 1
        }

        //---------------------------------------[ TERTIARY (NON) MAP ELEMENTS ]----------------------------------------
        //a hp megjelenítése (3 szív)
        hpGrid.children.clear()
        for (num in 0..2) hpGrid.add(ImageView(images.getHPImg()), num, 0)

        //megjelenítjük az érme képét pluszban (illetve mellette, hogy mennyit szedtünk fel, de azt máshol állítjuk be)
        coinGrid.children.clear()
        coinGrid.add(ImageView(images.getCoinImg()), 0, 0)
        //--------------------------------------------------------------------------------------------------------------
    }

    //beállítja az érméket és a heal potion-öket, figyelve, hogy felszedtük-e már, vagy sem
    private fun itemSetter()
    {
        //--------------------------------------[ SECONDARY MAP ELEMENTS ]----------------------------------------------
        for (index in 0 until coinLocations.size) if(coinLocations[index][2] == 0) playerField.add(ImageView(images.getCoinImg()), coinLocations[index][1], coinLocations[index][0])
        for (index in 0 until healLocations.size) if(healLocations[index][2] == 0) playerField.add(ImageView(images.getHealImg()), healLocations[index][1], healLocations[index][0])
        for (index in 0 until trapLocations.size) if(trapLocations[index][2] == 1) playerField.add(ImageView(images.getTrapImg()), trapLocations[index][1], trapLocations[index][0])
        //--------------------------------------------------------------------------------------------------------------
    }

    //felszedi az érméket, illetve a heal potion-öket, és amit felszedtünk, ott a listályában felszedettre állítjuk (abban az esetben, ha rajta állunk)
    private fun collect()
    {
        for (index in 0 until coinLocations.size)
        {
            if (player.getRow() == coinLocations[index][0] && player.getColumn() == coinLocations[index][1] && coinLocations[index][2] == 0)
            {
                player.addCoins()
                coinLocations[index][2] = 1
            }
        }

        for (index in 0 until healLocations.size)
        {
            if (player.getRow() == healLocations[index][0] && player.getColumn() == healLocations[index][1] && healLocations[index][2] == 0)
            {
                if(player.getHP() < 3)
                {
                    player.heal()
                    healLocations[index][2] = 1
                }
            }
        }
    }

    //a play gomb
    fun giveUpBtnClick()
    {
        //----------------[ GAME OVER ]----------------
        playable = false
        giveUpBtn.isVisible = false
        this.showMazeButtons()
        this.hideMoves()
        this.stopped = true
        //---------------------------------------------
    }

    //győzelemkor fut le, itt menthetünk
    private fun win()
    {
        //----------------[ GAME OVER ]----------------
        gameOverLabel.text = "Win"
        gameOverLabel.textFill = Color.GREEN
        playable = false
        giveUpBtn.isVisible = false
        this.showMazeButtons()
        this.hideMoves()
        this.stopped = true
        //---------------------------------------------
    }

    //ha veszítünk, akkor fut le, itt nem mentünk
    private fun lose()
    {
        //----------------[ GAME OVER ]----------------
        gameOverLabel.text = "Game Over"
        gameOverLabel.textFill = Color.RED
        playable = false
        giveUpBtn.isVisible = false
        this.showMazeButtons()
        this.hideMoves()
        this.stopped = true
        //---------------------------------------------
    }

    //a játékos mozgása (ha fal van arra, amerre menni akarunk, nem tudunk)
    fun move(param: MoveEnum)
    {
        this.hiddenTrapTracker() //mozgás előtt trackeljük az előttünk lévő rejtett csapdát

        //------------------------------[ MOVEMENT ]------------------------------
        if(param == MoveEnum.W)
        {
            if(player.getRow() > 0)
            {
                if(field[player.getRow() - 1][player.getColumn()] != 9)
                {
                    player.moveRow('-')
                }
            }
        }
        else if(param == MoveEnum.A)
        {
            if(player.getColumn() > 0)
            {
                if(field[player.getRow()][player.getColumn() - 1] != 9)
                {
                    player.moveColumn('-')
                }
            }
        }
        else if(param == MoveEnum.S)
        {
            if (player.getRow() < 19)
            {
                if(field[player.getRow() + 1][player.getColumn()] != 9)
                {
                    player.moveRow('+')
                }
            }
        }
        else if(param == MoveEnum.D)
        {
            if(player.getColumn() < 19)
            {
                if(field[player.getRow()][player.getColumn() + 1] != 9)
                {
                    player.moveColumn('+')
                }
            }
        }
        //------------------------------------------------------------------------

        if(field[player.getRow()][player.getColumn()] == 5) this.win() // ha elértük a célunkat, nyertünk
        if(field[player.getRow()][player.getColumn()] == 2 || field[player.getRow()][player.getColumn()] == 3 || field[player.getRow()][player.getColumn()] == 7) player.sufferDamage() //ha csaptába lépünk, sebződünk egyet

        this.collect() //lefut a fügvény, ami felszedi az érmét
        this.hpCheck() // ellenőrzi a hp-t

        playerField.children.clear()
        this.itemSetter()
        playerField.add(ImageView(images.getPlayerImg()), player.getColumn(), player.getRow())

        coinCountLabel.text = player.getCoins().toString()//kiírja mennyi érménk van eddig
    }

    // ellenőrzi a hp-t
    private fun hpCheck()
    {
        //a hp vizuális megjelenítése
        //----------------------------------[ VISUALIZATION ]----------------------------------
        when
        {
            player.getHP() == 3 ->
            {
                for(hpslot in 0..2)
                {
                    hpGrid.add(ImageView(images.getHPImg()), hpslot, 0)
                }
            }
            player.getHP() == 2 ->
            {
                for(hpslot in 0..1)
                {
                    hpGrid.add(ImageView(images.getHPImg()), hpslot, 0)
                }
                hpGrid.add(ImageView(images.getDPImg()), 2, 0)
            }
            player.getHP() == 1 ->
            {
                for(hpslot in 1..2)
                {
                    hpGrid.add(ImageView(images.getDPImg()), hpslot, 0)
                }
                hpGrid.add(ImageView(images.getHPImg()), 0, 0)
            }
            player.getHP() == 0 ->
            {
                for(hpslot in 0..2)
                {
                    hpGrid.add(ImageView(images.getDPImg()), hpslot, 0)
                }
                this.lose() // ha elfogyott, meghaltunk, vesztettünk
            }
        }
        //-------------------------------------------------------------------------------------
    }

    //a játékos körüli mezőkben trackeli a rejtett csapdákat
    private fun hiddenTrapTracker()
    {
        //(trapLocations[index][2] == 1
        if(field[player.getRow()][player.getColumn()] != 1)
        {
            if (field[player.getRow()][player.getColumn()] != 5)
            {
                for (index in 0 until trapLocations.size)
                {
                    //-------------------------------------------[ TRACKING ]-------------------------------------------
                    //OLDALT
                    if (player.getRow() == trapLocations[index][0] && (player.getColumn() + 2) == trapLocations[index][1])
                    {
                        if(field[player.getRow()][player.getColumn() + 1] != 9) trapLocations[index][2] = 1
                    }

                    if (player.getRow() == trapLocations[index][0] && (player.getColumn() - 2) == trapLocations[index][1])
                    {
                        if(field[player.getRow()][player.getColumn() - 1] != 9) trapLocations[index][2] = 1
                    }

                    if ((player.getRow() + 2) == trapLocations[index][0] && player.getColumn() == trapLocations[index][1])
                    {
                        if(field[player.getRow() + 1][player.getColumn()] != 9) trapLocations[index][2] = 1
                    }

                    if ((player.getRow() - 2) == trapLocations[index][0] && player.getColumn() == trapLocations[index][1])
                    {
                        if(field[player.getRow() - 1][player.getColumn()] != 9) trapLocations[index][2] = 1
                    }

                    //ÁTLÓ
                    if ((player.getRow() + 1) == trapLocations[index][0] && (player.getColumn() + 1) == trapLocations[index][1])
                    {
                       trapLocations[index][2] = 1
                    }

                    if ((player.getRow() + 1) == trapLocations[index][0] && (player.getColumn() - 1) == trapLocations[index][1])
                    {
                        trapLocations[index][2] = 1
                    }

                    if ((player.getRow() - 1) == trapLocations[index][0] && (player.getColumn() - 1) == trapLocations[index][1])
                    {
                        trapLocations[index][2] = 1
                    }

                    if ((player.getRow() - 1) == trapLocations[index][0] && (player.getColumn() + 1) == trapLocations[index][1])
                    {
                        trapLocations[index][2] = 1
                    }
                    //--------------------------------------------------------------------------------------------------
                }
            }
        }
    }


    //mindent visszállít
    private fun resetall()
    {
        //---------------------------------------[ RESET ]---------------------------------------
        player.reset()
        coinCountLabel.text = player.getCoins().toString()
        gameOverLabel.text = ""
        playerField.children.clear()
        playerField.add(ImageView(images.getPlayerImg()), player.getColumn(), player.getRow())
        itemSetter()
        playable = true
        //---------------------------------------------------------------------------------------
    }

    //------------------------------------------------------------------------------------------------------------------
    //MOVES BY BUTTONS
    fun le() { if(playable) move(MoveEnum.S) }
    fun fel() { if(playable) move(MoveEnum.W) }
    fun jobb() { if(playable) move(MoveEnum.D) }
    fun bal() { if(playable) move(MoveEnum.A) }

    //MOVES BY KEYBOARD
    fun keyboardControl(keyEvent: KeyEvent)
    {
        if(playable)
        {
            when (keyEvent.code)
            {
                //-----------------[ KEYS ]------------------
                KeyCode.W -> move(MoveEnum.W)
                KeyCode.A -> move(MoveEnum.A)
                KeyCode.S -> move(MoveEnum.S)
                KeyCode.D -> move(MoveEnum.D)

                KeyCode.UP -> move(MoveEnum.W)
                KeyCode.LEFT -> move(MoveEnum.A)
                KeyCode.DOWN -> move(MoveEnum.S)
                KeyCode.RIGHT -> move(MoveEnum.D)
                //-------------------------------------------
            }
        }

        when (keyEvent.code)
        {
            //-----------------[ KEYS ]------------------
            KeyCode.ESCAPE ->
            {
                if (escapable) escapePanel.isVisible = !escapePanel.isVisible
            }
            //-------------------------------------------
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    //MOVE BUTTONS / MAZE BUTTONS | HIDE / SHOW
    private fun hideMoves()
    {
        //-----------------------[ VISIBILITY ]-----------------------
        felbtn.isVisible = false
        lebtn.isVisible = false
        jobbbtn.isVisible = false
        balbtn.isVisible = false
        //------------------------------------------------------------
    }

    private fun showMoves()
    {
        //-----------------------[ VISIBILITY ]-----------------------
        felbtn.isVisible = true
        lebtn.isVisible = true
        jobbbtn.isVisible = true
        balbtn.isVisible = true
        //------------------------------------------------------------
    }

    private fun showMazeButtons()
    {
        //-----------------------[ VISIBILITY ]-----------------------
        maze1Btn.isVisible = true
        maze2Btn.isVisible = true
        maze3Btn.isVisible = true
        //------------------------------------------------------------
    }

    private fun hideMazeButtons()
    {
        //-----------------------[ VISIBILITY ]-----------------------
        maze1Btn.isVisible = false
        maze2Btn.isVisible = false
        maze3Btn.isVisible = false
        //------------------------------------------------------------
    }
    //------------------------------------------------------------------------------------------------------------------

    //MAZE CHOOSING
    fun maze1ButtonClick() { this.version = MazeEnum.MAZE1; this.loadSelector(); this.escapable = true; this.stopped = false; this.timeCounter() }
    fun maze2ButtonClick() { this.version = MazeEnum.MAZE2; this.loadSelector(); this.escapable = true; this.stopped = false; this.timeCounter() }
    fun maze3ButtonClick() { this.version = MazeEnum.MAZE3; this.loadSelector(); this.escapable = true; this.stopped = false; this.timeCounter() }

    //Betölti az escape menüt
    private fun loadEsc()
    {
        escapePanel.background = Background(esc)
        wasdLabel.graphic = ImageView(images.getWasdImg())
        arrowLabel.graphic = ImageView(images.getArrowsImg())
        buttonLabel.graphic = ImageView(images.getButtonImg())

        //-----------------------[ VISIBILITY ]-----------------------
        this.escInfoPanel.isVisible = true
        this.controlPanel.isVisible = false
        this.creditPanel.isVisible = false
        //------------------------------------------------------------

        //--------------------------[ COLOR ]-------------------------
        this.infSelectorPanel.style = "-fx-background-color: #FFFFFF;"
        this.creSelectorPanel.style = "-fx-background-color: #DDDDDD;"
        this.conSelectorPanel.style = "-fx-background-color: #DDDDDD;"
        //------------------------------------------------------------

    }

    //betölti a pályát, amelyet kiválasztottunk [a MazeEnum-ok segítségével a loadBackground() azt a pályát állítja be, amit mi szeretnénk]
    private fun loadSelector()
    {
        this.locationClear() //betöltés előtt biztosra megyünk, hogy a lista üres legyen (egy bug kijavítása, miszerint a tesztek alatt 10 esetből 2-szer nem törlődtek)
        this.loadBackground() //a betöltés csak a pályát tölti be, a hátteret illetve az érméket és a heal-t
        this.resetall() //főként a playerhez kapcsolódó dolgokat reseteljük, nem tesz kárt az előtte lévő pályabetöltésben
        giveUpBtn.isVisible = true //feladhatjuk a játékot menetközben, a betöltés után elindul a játék
        this.hideMazeButtons() //ha játszunk, ne lehessen új pályát betölteni, max ha feladtuk
        this.showMoves() //ha gombokkal akarjuk irányítani, azzal is lehet

        this.buttonImageLoader()

        //Betöltésnél szükséges, defaultban false
        //-----------------------[ VISIBILITY ]-----------------------
        colonLabel.isVisible = true
        hpGrid.isVisible = true
        coinGrid.isVisible = true
        infoPanel.isVisible = false
        //------------------------------------------------------------
    }

    //törli a coin/heal/trap locations-öket, hogy az új pályán az újak adódjanak hozzá
    private fun locationClear()
    {
        coinLocations.clear()
        healLocations.clear()
        trapLocations.clear()
    }

    //image hozzáadása a gombokhoz
    private fun buttonImageLoader()
    {
        felbtn.graphic = ImageView(images.getUpImg())
        lebtn.graphic = ImageView(images.getDownImg())
        jobbbtn.graphic = ImageView(images.getRightImg())
        balbtn.graphic = ImageView(images.getLeftImg())
    }

    //------------------------------------------------------------------------------------------------------------------
    //ESCAPE MENU CONTROLS
    fun infoClick()
    {
        //-----------------------[ VISIBILITY ]-----------------------
        this.escInfoPanel.isVisible = true
        this.controlPanel.isVisible = false
        this.creditPanel.isVisible = false
        //------------------------------------------------------------

        //--------------------------[ COLOR ]-------------------------
        this.infSelectorPanel.style = "-fx-background-color: #FFFFFF;"
        this.creSelectorPanel.style = "-fx-background-color: #DDDDDD;"
        this.conSelectorPanel.style = "-fx-background-color: #DDDDDD;"
        //------------------------------------------------------------
    }

    fun controlsClick()
    {
        //-----------------------[ VISIBILITY ]-----------------------
        this.controlPanel.isVisible = true
        this.escInfoPanel.isVisible = false
        this.creditPanel.isVisible = false
        //------------------------------------------------------------

        //--------------------------[ COLOR ]-------------------------
        this.conSelectorPanel.style = "-fx-background-color: #FFFFFF;"
        this.creSelectorPanel.style = "-fx-background-color: #DDDDDD;"
        this.infSelectorPanel.style = "-fx-background-color: #DDDDDD;"
        //------------------------------------------------------------
    }

    fun creditClick()
    {
        //-----------------------[ VISIBILITY ]-----------------------
        this.creditPanel.isVisible = true
        this.escInfoPanel.isVisible = false
        this.controlPanel.isVisible = false
        //------------------------------------------------------------

        //--------------------------[ COLOR ]-------------------------
        this.creSelectorPanel.style = "-fx-background-color: #FFFFFF;"
        this.infSelectorPanel.style = "-fx-background-color: #DDDDDD;"
        this.conSelectorPanel.style = "-fx-background-color: #DDDDDD;"
        //------------------------------------------------------------
    }
    //------------------------------------------------------------------------------------------------------------------

    private fun timeCounter()
    {
        time = 0
        minute = 0
        second = 0

        Timer().scheduleAtFixedRate(object : TimerTask()
        {
            override fun run()
            {
                Platform.runLater {

                    if(stopped)
                    {
                        this.cancel()
                    }
                    else
                    {

                        println(time)

                        minute = time / 60
                        second = time % 60

                        savedTime = if (minute < 10)
                        {
                            if (second < 10) "0${minute}:0${second}" else "0${minute}:${second}"
                        }
                        else
                        {
                            if (second < 10) "${minute}:0${second}" else "${minute}:${second}"
                        }

                        timerLabel.text = savedTime
                        time++
                    }
                }
            }
        }, 0, 1000) // 1 sec
    }
    //------------------------------------------------------------------------------------------------------------------
}