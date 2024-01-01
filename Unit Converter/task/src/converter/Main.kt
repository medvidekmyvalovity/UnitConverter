package converter
open class Unit(val unit:String, val units:String, val labels:List<String>, val conversion:Double) {
    fun isUnit(unit:String):Boolean{
        return(null !=labels.find {name-> name==unit})
    }
    open fun convertToBase(value:Double):Double=value*conversion
    open fun convertFromBase(value:Double):Double=value/conversion
    fun getUnits(value:Double)=if (value==1.0) unit else units
}
class TempUnit(unit:String,units:String,labels:List<String>,conversion:Double=0.0):Unit(unit,units,labels,conversion){
    override fun convertToBase(value:Double):Double{
        return(
                when(super.unit){
            "degree Celsius"-> value
            "degree Fahrenheit"->(value-32)*5/9
            "kelvin"->(value-273.15)
            else ->0.0
        })

    }
    override fun convertFromBase(value:Double):Double{
        return(when(super.unit){
            "degree Celsius"-> value
            "degree Fahrenheit"->value*9/5+32
            "kelvin"->value+273.15
            else ->0.0
        })

    }
}


class Convertor(val measure:String,val units: List<Unit>){
//    constructor(measure: String, units: List<TempUnit>, unit: Unit) : this()

    fun findUnit(unit:String):Unit?{
        return (units.find {u->u.isUnit(unit)})
    }

    fun printConversion(value:Double, unitName:String, toUnitName:String){
        val u1= findUnit(unitName)
        val u2= findUnit(toUnitName)
        if (value<0 &&(measure=="Weight" || measure=="Length")){
            println("$measure shouldn't be negative")
            return
        }
        val res= u2!!.convertFromBase(u1!!.convertToBase(value))
        println("$value ${u1.getUnits(value)} is $res ${u2.getUnits(res)}")
    }
}

fun findConvertor(unit:String,convertors: List<Convertor>):Convertor?{
    for (c in convertors){
        if (null!=c.findUnit(unit)) return(c)
    }
    return(null)
}

fun getUnitsConvertor(unit:String,unit2:String,convertors:List<Convertor>):Convertor?{
    val c1=findConvertor(unit,convertors)
    val c2=findConvertor(unit2,convertors)

    if (c1!=null && c2!=null && c1===c2) return(c1)

    val units1Name=c1?.findUnit(unit)?.units ?: "???"
    val units2Name=c2?.findUnit(unit2)?.units ?: "???"

    println("Conversion from $units1Name to $units2Name is impossible")
    return null
}


fun main() {
    val distanceConvertor=Convertor("Length",listOf(
        Unit("meter","meters",listOf("m", "meter", "meters"),1.0),
        Unit("kilometer","kilometers",listOf("km", "kilometer", "kilometers"),1000.0),
        Unit("centimeter","centimeters",listOf( "cm", "centimeter", "centimeters"),0.01),
        Unit("millimeter","millimeters",listOf("mm", "millimeter", "millimeters"),0.001),
        Unit("mile","miles",listOf("mi", "mile", "miles"),1609.35),
        Unit("yard","yards",listOf("yd", "yard", "yards"),0.9144),
        Unit("foot","feet",listOf("ft", "foot", "feet"),0.3048),
        Unit("inch","inches",listOf("in", "inch", "inches"),0.0254)
    ))
    val weightyConvertor=Convertor("Weight",listOf(
        Unit("gram","grams",listOf( "g", "gram", "grams"),1.0),
        Unit("kilogram","kilograms",listOf("kg", "kilogram", "kilograms"),1000.0),
        Unit("milligram","milligrams",listOf( "mg", "milligram", "milligrams"),0.001),
        Unit("pound","pounds",listOf("lb", "pound", "pounds"),453.592),
        Unit("ounce","ounces",listOf("oz", "ounce", "ounces"), 28.3495)
    ))
    val tempConvertor=Convertor("Temp",listOf(
        TempUnit("degree Celsius","degrees Celsius",listOf( "degree celsius", "degrees celsius", "celsius","dc","c")),
        TempUnit("kelvin","kelvins",listOf("kelvin", "kelvins", "k")),
        TempUnit("degree Fahrenheit","degrees Fahrenheit",listOf( "degree fahrenheit", "degrees fahrenheit", "fahrenheit","df","f"))
    ))

    while(true) {
        println("Enter what you want to convert (or exit):")
        try {
            val l=readln()
            if (l=="exit") return
            val ls: MutableList<String> =l.lowercase().replace("degree celsius", "celsius").replace("degrees celsius", "celsius").replace("degree fahrenheit", "fahrenheit").replace("degrees fahrenheit", "fahrenheit").split(" ").toMutableList()
            if(ls[1]=="degree" || ls[1]=="degrees") {
                ls[1]+=" "+ls[2]
                ls.removeAt(2)
            }
            if(ls[3]=="degree" || ls[3]=="degrees") {
                ls[3]+=" "+ls[4]
                ls.removeAt(4)
            }
            if (ls.size!=4) throw Exception("Wrong Input")
            val (value, unitName, word, toUnitName) = ls
            value.toDouble()
            val c = getUnitsConvertor(unitName, toUnitName, listOf(distanceConvertor, weightyConvertor, tempConvertor))
            c?.printConversion(value.toDouble(), unitName, toUnitName)

        }catch(e:Exception){
            println("Parse error");
        }
    }
}
