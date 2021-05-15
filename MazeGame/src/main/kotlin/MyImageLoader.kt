import javafx.scene.image.Image
import java.io.FileInputStream
import java.nio.file.Paths

/*
    -------------------------------
    Made by GKPLJP - Tálas Martin
    Programtervező Informatikus BSc
               [ 2021 ]
    -------------------------------
*/

class MyImageLoader()
{
    //------[ PATH VARIABLE ]-------------------------------------------------------------------------------------------

    private var path = Paths.get(System.getProperty("user.dir")) // path a képekhez

    //------------------------------------------------------------------------------------------------------------------

    init
    {
        path = Paths.get(System.getProperty("user.dir")) // path a képekhez (az initben is kell, enélkül nem működik)
        load()
    }

    //------[ VARIABLES ]-----------------------------------------------------------------------------------------------

    //képek, amelyek a játék megjelenítéséhez szükségesek (32x32 png) (+háttér: 1280x720 png)
    private lateinit var wallImg: Image
    private lateinit var wayImg: Image
    private lateinit var finishImg: Image
    private lateinit var startImg: Image
    private lateinit var lavaImg: Image
    private lateinit var waterImg: Image
    private lateinit var trapImg: Image
    private lateinit var playerImg: Image
    private lateinit var hpImg: Image
    private lateinit var dpImg: Image
    private lateinit var coinImg: Image
    private lateinit var healImg: Image
    private lateinit var upImg: Image
    private lateinit var downImg: Image
    private lateinit var rightImg: Image
    private lateinit var leftImg: Image
    private lateinit var arrowsImg: Image
    private lateinit var wasdImg: Image
    private lateinit var buttonImg: Image
    private lateinit var backgroundImg: Image
    private lateinit var escapeBackgroundImg: Image

    //------------------------------------------------------------------------------------------------------------------

    //------[ GETTERS ]-------------------------------------------------------------------------------------------------
    fun getWallImg(): Image = this.wallImg
    fun getWayImg(): Image = this.wayImg
    fun getCoinImg(): Image = this.coinImg
    fun getFinishImg(): Image = this.finishImg
    fun getStartImg(): Image = this.startImg
    fun getLavaImg(): Image = this.lavaImg
    fun getWaterImg(): Image = this.waterImg
    fun getPlayerImg(): Image = this.playerImg
    fun getHPImg(): Image = this.hpImg
    fun getHealImg(): Image = this.healImg
    fun getDPImg(): Image = this.dpImg
    fun getTrapImg(): Image = this.trapImg
    fun getUpImg(): Image = this.upImg
    fun getDownImg(): Image = this.downImg
    fun getRightImg(): Image = this.rightImg
    fun getLeftImg(): Image = this.leftImg
    fun getBackgroundImg(): Image = this.backgroundImg
    fun getEscapeBackgroundImg(): Image = this.escapeBackgroundImg
    fun getWasdImg(): Image = this.wasdImg
    fun getArrowsImg(): Image = this.arrowsImg
    fun getButtonImg(): Image = this.buttonImg

    //------------------------------------------------------------------------------------------------------------------

    //a képek betöltése
    //------[ IMAGE LOADER ]--------------------------------------------------------------------------------------------
    private fun load()
    {
        wallImg = Image(FileInputStream("${path}/img/map/defaults/wall.png"))
        wayImg = Image(FileInputStream("${path}/img/map/defaults/way.png"))
        finishImg = Image(FileInputStream("${path}/img/map/defaults/finish.png"))
        startImg = Image(FileInputStream("${path}/img/map/defaults/start.png"))
        lavaImg = Image(FileInputStream("${path}/img/map/traps/lava.png"))
        waterImg = Image(FileInputStream("${path}/img/map/traps/water.png"))
        trapImg = Image(FileInputStream("${path}/img/map/traps/trap.png"))
        playerImg = Image(FileInputStream("${path}/img/player/player.png"))
        hpImg = Image(FileInputStream("${path}/img/hp/hp.png"))
        dpImg = Image(FileInputStream("${path}/img/hp/dp.png"))
        coinImg = Image(FileInputStream("${path}/img/collectables/coin.png"))
        healImg = Image(FileInputStream("${path}/img/collectables/heal.png"))
        upImg = Image(FileInputStream("${path}/img/buttons/up.png"))
        downImg = Image(FileInputStream("${path}/img/buttons/down.png"))
        rightImg = Image(FileInputStream("${path}/img/buttons/right.png"))
        leftImg = Image(FileInputStream("${path}/img/buttons/left.png"))
        wasdImg = Image(FileInputStream("${path}/img/buttons/wasd.png"))
        arrowsImg = Image(FileInputStream("${path}/img/buttons/arrows.png"))
        buttonImg = Image(FileInputStream("${path}/img/buttons/buttons.png"))
        backgroundImg = Image(FileInputStream("${path}/img/background/background.png"))
        escapeBackgroundImg = Image(FileInputStream("${path}/img/background/escapebackground.png"))
    }
    //------------------------------------------------------------------------------------------------------------------
}