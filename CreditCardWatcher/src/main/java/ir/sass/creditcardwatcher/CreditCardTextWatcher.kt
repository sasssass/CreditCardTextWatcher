package ir.sass.creditcardwatcher

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import com.google.android.material.textfield.TextInputEditText
import java.lang.Exception
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.ParseException
import java.util.*

fun checkCreditCardValid(number_: String) : Boolean{
    var value = 0
    val number = castPersianDigitToEn(number_)
    for ((i,numChar) in number.withIndex()){
        if((i+1)%2 == 1){
            if(2*(numChar.toInt()-48) >= 10)
                value += (2*(numChar.toInt()-48) - 9)
            else
                value += 2*(numChar.toInt()-48)
        }
        else
            value += (numChar.toInt()-48)
    }
    if(value % 10 == 0 && value != 0)return true
    return false
}

fun castPersianDigitToEn(str : String) : String{
    return str
        .replace("۰","0")
        .replace("۱","1")
        .replace("۲","2")
        .replace("۳","3")
        .replace("۴","4")
        .replace("۵","5")
        .replace("۶","6")
        .replace("۷","7")
        .replace("۸","8")
        .replace("۹","9")
}

open class CreditCardTextWatcher(et: EditText) : TextWatcher {

    var lastSize = et.text.toString().length
    var lastStr = et.text.toString()

    private val df: DecimalFormat
    private val dfnd: DecimalFormat
    private var hasFractionalPart: Boolean
    private val et: EditText
    private var oldStr = ""
    override fun afterTextChanged(s_: Editable) {
        et.removeTextChangedListener(this)
        val cp = et.selectionStart
        var flag = false
        try {
            if(lastSize > s_.toString().length){
                if(lastStr[cp] == ' '){
                    flag = true
                }
            }
        }catch (e : Exception){}


        if(!s_.toString().isEmpty()){
            val inilen = et.text.length

            var s = s_.toString().replace(" ","")

            try {
                if(flag) s = (oldStr.substring(0,cp-1)+ oldStr.substring(cp+1)).replace(" ","")
            }catch (e : Exception){}

//            if(s.length <= 4){
//                et.setText(s)
//            }else if(s.length <= 8){
//                et.setText(s.substring(0,4)+" "+s.substring(4))
//            }else if(s.length <= 12){
//                et.setText(s.substring(0,4)+" "+s.substring(4,8)+" "+s.substring(8))
//            }else if(s.length <= 16){
//                et.setText(s.substring(0,4)+" "+s.substring(4,8)+" "+s.substring(8,12)+" "+s.substring(12))
//            }

            var str = ""
            s.forEachIndexed { index, c ->
                str += c.toString()
                if(index % 4 == 3)  str += " "
            }

            if(str[str.length-1].equals(' '))
                str = str.substring(0,str.length-1)

            et.setText(str)


            val outlen = et.text.length

            var sel = cp + outlen - inilen
            if(flag)
                sel--
            if(sel < 0) sel = 0
            et.setSelection(sel/*et.text.toString().length*/)
            lastStr = et.text.toString()
            lastSize = lastStr.length
//            df.decimalFormatSymbols.groupingSeparator.toString()
        }
        oldStr = et.text.toString()
        et.addTextChangedListener(this)
    }

    override fun beforeTextChanged(
        s: CharSequence,
        start: Int,
        count: Int,
        after: Int
    ) {
    }

    override fun onTextChanged(
        s: CharSequence,
        start: Int,
        before: Int,
        count: Int
    ) {
        hasFractionalPart =
            s.toString().contains(df.decimalFormatSymbols.decimalSeparator.toString())
    }

    companion object {
        private const val TAG = "NumberTextWatcher"
    }

    init {
        val otherSymbols = DecimalFormatSymbols(Locale.getDefault())
        otherSymbols.setDecimalSeparator(',')
        otherSymbols.setGroupingSeparator(' ')
        df = DecimalFormat("#,####.##",otherSymbols)
        df.isDecimalSeparatorAlwaysShown = true
        dfnd = DecimalFormat("#,####",otherSymbols)
        this.et = et
        hasFractionalPart = false
    }
}

