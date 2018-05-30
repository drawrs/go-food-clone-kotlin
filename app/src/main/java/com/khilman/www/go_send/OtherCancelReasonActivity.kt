package com.khilman.www.go_send

import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import kotlinx.android.synthetic.main.activity_other_cancel_reason.*
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.sdk25.coroutines.textChangedListener

class OtherCancelReasonActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_other_cancel_reason)

        etInputReason.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
               // toast(count.toString())
                if (count > 0){
                    // Change button color
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        btnCancelOrder.backgroundColor = resources.getColor(R.color.danger_active, applicationContext.theme)
                    } else {
                        btnCancelOrder.backgroundColor = resources.getColor(R.color.danger_active)
                    }
                } else {
                    // Change button color
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        btnCancelOrder.backgroundColor = resources.getColor(R.color.disabled_active, applicationContext.theme)
                    } else {
                        btnCancelOrder.backgroundColor = resources.getColor(R.color.disabled_active)
                    }
                }
            }

        })

        btnCancelOrder.onClick {
            if (etInputReason.text.length > 0) {
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
