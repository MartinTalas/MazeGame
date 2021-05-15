/*
    -------------------------------
    Made by GKPLJP - Tálas Martin
    Programtervező Informatikus BSc
               [ 2021 ]
    -------------------------------
*/

class Player()
{
    //------[ VARIABLES ]-----------------------------------------------------------------------------------------------
    private var positionRow: Int = 1 // a player pozíciója [row]
    private var positionColumn: Int = 0 // a player pozíciója [column]
    private var hp: Int = 3 // a player hp-ja
    private var coins: Int = 0 // a player által felszedett érmék
    //------------------------------------------------------------------------------------------------------------------

    //visszaállítja a player adatait
    fun reset()
    {
        this.setRow(1)
        this.setColumn(0)
        this.hp = 3
        this.coins = 0
    }

    //------[ GETTERS ]-------------------------------------------------------------------------------------------------
    fun getRow(): Int = this.positionRow
    fun getColumn(): Int = this.positionColumn
    fun getHP(): Int = this.hp
    fun getCoins(): Int = this.coins
    //------------------------------------------------------------------------------------------------------------------

    //------[ SETTERS ]-------------------------------------------------------------------------------------------------
    fun setRow(param: Int) { this.positionRow = param }
    fun setColumn(param: Int) { this.positionColumn = param }
    //------------------------------------------------------------------------------------------------------------------

    //------[ MODIFIERS ]-----------------------------------------------------------------------------------------------
    fun sufferDamage() { this.hp -= 1 } // levon a hp-ból egyet, ha sebződünk
    fun heal() { this.hp += 1 } //hozzáad egyet a hp-hoz, ha heal-elődünk (ha felvesszük a heal potion-t)
    fun addCoins() { this.coins += 1 } //ha felveszünk egy érmét, azt hozzáadja
    //------------------------------------------------------------------------------------------------------------------


    //------------------------------------------------[ MOVEMENT ]------------------------------------------------------

    //a mozgáshoz így módosítjuk a player pozícióját [row]
    fun moveRow(sign: Char)
    {
        when(sign)
        {
            '-' -> this.positionRow -= 1
            '+' -> this.positionRow += 1
        }
    }

    //a mozgáshoz így módosítjuk a player pozícióját [column]
    fun moveColumn(sign: Char)
    {
        when(sign)
        {
            '-' -> this.positionColumn -= 1
            '+' -> this.positionColumn += 1
        }
    }
    //------------------------------------------------------------------------------------------------------------------
}