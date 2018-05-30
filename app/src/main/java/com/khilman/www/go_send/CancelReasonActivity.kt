package com.khilman.www.go_send

import android.app.PendingIntent.getActivity
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_cancel_reason.*
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick

class CancelReasonActivity : AppCompatActivity() {

    var currentSelectedItem : TextView ? = null
    var currentSelectedIndex : Int ? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cancel_reason)

        val cancleReasons : Array<String> = resources.getStringArray(R.array.cancel_reasons)

        val cancleAdapter = ArrayAdapter<String>(this, R.layout.simple_list_item_selectable, cancleReasons)
        // Set adapter into List view
        listCancleReasons.adapter = cancleAdapter
        listCancleReasons.setOnItemClickListener { adapterView, view, i, l ->

            val text = view.findViewById<TextView>(R.id.text_item)
            var checkImg : Drawable ? = null

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                checkImg = resources.getDrawable(R.drawable.ic_check_black_24dp, applicationContext.theme)
            } else {
                checkImg = resources.getDrawable(R.drawable.ic_check_black_24dp)
            }

            checkImg.setBounds(0,0,40,40)


            if (currentSelectedItem != null){
                currentSelectedItem!!.setCompoundDrawables(null, null, null, null)
            }
            text.setCompoundDrawables(null, null, checkImg, null)
            currentSelectedItem = text

            // Change button color
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                btnCancelOrder.backgroundColor = resources.getColor(R.color.danger_active, applicationContext.theme)
            } else {
                btnCancelOrder.backgroundColor = resources.getColor(R.color.danger_active)
            }

            // If user choose other reasen
            currentSelectedIndex = i
            if (i == 0){
                startActivity(intentFor<OtherCancelReasonActivity>())
            }
        }

        btnCancelOrder.onClick {
            if (currentSelectedItem != null){

                // if user select others reaseon they should be input the reason manually
                if (currentSelectedIndex == 0){
                    startActivity(intentFor<OtherCancelReasonActivity>())
                } else {
                    alert {
                        message = "Thank you, your order was successfully canceled"
                        yesButton {
                            title = "Ok"
                            startActivity(intentFor<MainActivity>().clearTop())
                        }
                        onCancelled {
                            startActivity(intentFor<MainActivity>().clearTop())
                        }
                    }.show()
                }

            }
        }

    }
}
