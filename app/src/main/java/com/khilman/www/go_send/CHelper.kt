package com.khilman.www.go_send

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

/**
 * Created by root on 22/01/18.
 */
public class CHelper {

    public fun toRupiahFormat(price : Double ?) : String {
        val harga = price

        val kursIndonesia = DecimalFormat.getCurrencyInstance() as DecimalFormat
        val formatRp = DecimalFormatSymbols()

        formatRp.setCurrencySymbol("Rp. ")
        formatRp.setMonetaryDecimalSeparator(',')
        formatRp.setGroupingSeparator('.')

        kursIndonesia.setDecimalFormatSymbols(formatRp)

        val result : String = "${kursIndonesia.format(harga)}"
        return  result
    }

}